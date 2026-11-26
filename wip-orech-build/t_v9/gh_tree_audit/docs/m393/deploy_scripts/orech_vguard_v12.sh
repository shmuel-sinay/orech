#!/system/bin/sh
# orech video guard v12 (event driven): offending packages are uninstalled instead of disabled (system apps and com.orech.* are skipped).
#  - locks native video-engine libs by NAME and stops the app at once
#  - every minute: full rescan of installed apps, manifest check (video/ or installer permission),
#    installer-capability appop denied, mock-location appop granted to listed GPS apps
# State lives in /data/system/orech_vguard:
#   allowlist.txt    packages never touched (audio players, dev tools)
#   deny_install.txt packages whose REQUEST_INSTALL_PACKAGES appop is denied (system apps too)
#   mock_allow.txt   packages that get the mock-location appop (external GPS feeders)
D=/data/system/orech_vguard
LOG=$D/vguard.log
ALLOW=$D/allowlist.txt
DENY=$D/deny_install.txt
MOCK=$D/mock_allow.txt
BL=$D/blacklist.txt
DIS=$D/disable_list.txt
STATE=$D/locked.txt
STAMP=$D/.stamp
NEWSTAMP=$D/.stamp.new

mkdir -p "$D"
chmod 700 "$D"

until [ "$(getprop sys.boot_completed)" = "1" ]; do sleep 3; done
touch "$ALLOW" "$DENY" "$MOCK" "$STATE" "$BL" "$DIS"
touch -d "@0" "$STAMP"

# device id (set by the seller app): kept in nvdata, exposed as a property for Settings
IDF=/mnt/vendor/nvdata/orech_id
# the id is set only from the seller app; here it is just republished after boot
[ -s "$IDF" ] && setprop persist.sys.orech_id "$(cat $IDF | tr -d '\n')"
# unique Bluetooth address per device: random one on first boot, kept in a persist property
cur=$(getprop persist.service.bdroid.bdaddr)
if [ -z "$cur" ] || [ "$cur" = "00:00:46:00:00:01" ]; then
  r=$(od -An -N5 -tx1 /dev/urandom | tr -d '\n' | sed 's/^ *//; s/ /:/g')
  setprop persist.service.bdroid.bdaddr "02:$r"
  echo "$(date '+%F %T') bluetooth address set 02:$r" >> "$LOG"
  if [ "$(settings get global bluetooth_on)" = "1" ]; then
    svc bluetooth disable; sleep 3; svc bluetooth enable
  fi
fi

is_video_lib() {
  case "${1##*/}" in
    libavcodec*|libffmpeg*|libvlc*|libdav1d*|libvpx*|libde265*|libopenh264*|libx264*|libmpv*|libijkffmpeg*|libijksdl*|libmedia3*|libexo*) return 0 ;;
    *) return 1 ;;
  esac
}

blacklist() {
  pm list packages -s | grep -qx "package:$1" && { echo "$(date '+%F %T') system app, not removed: $1 ($2)" >> "$LOG"; return; }
  case "$1" in com.orech.*) echo "$(date '+%F %T') own app, not removed: $1 ($2)" >> "$LOG"; return ;; esac
  # removed for good; no permanent ban is kept, so the package can be installed again later if it is allowed
  out=$(pm uninstall "$1" 2>&1)
  echo "$(date '+%F %T') uninstalled $1 ($2): $out" >> "$LOG"
}
pkg_of() { p=$(basename "$1"); echo "${p%%-*}"; }

scan_libs() {
  d=$1
  pkg=$(pkg_of "$d")
  grep -qx "$pkg" "$ALLOW" && return
  hit=0
  for f in $(find "$d" -type f -name '*.so' 2>/dev/null); do
    is_video_lib "$f" || continue
    [ "$(stat -c %a "$f")" = "0" ] && continue
    chmod 000 "$f"
    grep -qxF "$f" "$STATE" || echo "$f" >> "$STATE"
    echo "$(date '+%F %T') locked $f" >> "$LOG"
    hit=1
  done
  if [ "$hit" = "1" ]; then
    blacklist "$pkg" "decoder lib"
  fi
}

# manifest check of an installed app: video/ mime type -> disable; installer permission -> deny the appop
scan_manifest() {
  d=$1
  pkg=$(pkg_of "$d")
  grep -qx "$pkg" "$ALLOW" && return
  apk="$d/base.apk"
  [ -f "$apk" ] || return
  m=$(unzip -p "$apk" AndroidManifest.xml 2>/dev/null | tr -d '\000')
  if echo "$m" | grep -a -q 'video/'; then
    blacklist "$pkg" "manifest video/"
    return
  fi
  if echo "$m" | grep -a -q 'INSTALL_PACKAGES'; then
    cmd appops set "$pkg" REQUEST_INSTALL_PACKAGES deny >/dev/null 2>&1
  fi
}

apply_lists() {
  # packages disabled for a faster boot (reversible with pm enable)
  dd_=$(pm list packages -d)
  for p in $(cat "$DIS" 2>/dev/null); do
    echo "$dd_" | grep -qx "package:$p" || pm disable-user --user 0 "$p" >/dev/null 2>&1
  done
  # decoders dropped into app data after install (downloaded/extracted libs)
  for f in $(find /data/data -maxdepth 5 -type f -name '*.so' 2>/dev/null); do
    is_video_lib "$f" || continue
    pkg=$(echo "$f" | cut -d/ -f4)
    grep -qx "$pkg" "$ALLOW" && continue
    [ "$(stat -c %a "$f")" = "0" ] || chmod 000 "$f"
    blacklist "$pkg" "data lib ${f##*/}"
  done
  for p in $(cat "$DENY" 2>/dev/null); do
    cmd appops set "$p" REQUEST_INSTALL_PACKAGES deny >/dev/null 2>&1
  done
  for p in $(cat "$MOCK" 2>/dev/null); do
    cmd appops set "$p" android:mock_location allow >/dev/null 2>&1
  done
}

# event driven: no polling loop. A package change rewrites /data/system/packages.list (packages.list.tmp is created),
# which triggers a scan of what is new. A slow timer (every 5 minutes) triggers the full rescan and the list actions,
# because files dropped into app data cannot be watched without recursion.
TICK=$D/tick
scan_new() {
  touch "$NEWSTAMP"
  for d in $(find /data/app -mindepth 2 -maxdepth 2 -type d -newer "$STAMP" ! -name 'vmdl*' ! -name '*.tmp' 2>/dev/null); do
    scan_libs "$d"
    scan_manifest "$d"
  done
  mv -f "$NEWSTAMP" "$STAMP"
}
rm -f "$TICK"
( while true; do sleep 300; rm -f "$TICK"; : > "$TICK"; done ) &
scan_new   # full scan right after boot (STAMP starts at 0)
apply_lists
while true; do
  inotifyd - /data/system:yn "$D":yn | while read ev dir name; do
    case "$name" in
      packages.list.tmp) sleep 2; scan_new ;;
      tick) touch -d "@0" "$STAMP"; scan_new; apply_lists
            cmd appops write-settings >/dev/null 2>&1 ;;   # appops are written lazily; flush so tile changes survive a reboot
    esac
  done
  sleep 5
done
