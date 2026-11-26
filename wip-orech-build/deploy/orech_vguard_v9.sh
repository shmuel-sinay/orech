#!/system/bin/sh
# orech video guard v9.
#  - install-time guards are off (flag file below); enforcement happens after install:
#    decoder libs are locked (chmod 000) without disabling the app; the app is blacklisted only if a locked
#    decoder comes back to life, or if a decoder shows up in its data dir later
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
# install-time VideoGuard (installer + package manager) is switched off by this flag; see scan_libs/scan_manifest
touch /data/system/orech_allow_install
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
  pm list packages -s | grep -qx "package:$1" && { echo "$(date '+%F %T') system app, not disabled: $1 ($2)" >> "$LOG"; return; }
  grep -qx "$1" "$BL" || echo "$1" >> "$BL"
  pm disable-user --user 0 "$1" >/dev/null 2>&1
  am force-stop "$1" >/dev/null 2>&1
  echo "$(date '+%F %T') blacklisted+disabled $1 ($2)" >> "$LOG"
}
pkg_of() { p=$(basename "$1"); echo "${p%%-*}"; }

scan_libs() {
  d=$1
  pkg=$(pkg_of "$d")
  grep -qx "$pkg" "$ALLOW" && return
  back=0
  for f in $(find "$d" -type f -name '*.so' 2>/dev/null); do
    is_video_lib "$f" || continue
    [ "$(stat -c %a "$f")" = "0" ] && continue
    # a lib we already locked is readable again: the decoder came back to life
    grep -qxF "$f" "$STATE" && back=1
    chmod 000 "$f"
    grep -qxF "$f" "$STATE" || echo "$f" >> "$STATE"
    echo "$(date '+%F %T') locked $f" >> "$LOG"
  done
  if [ "$back" = "1" ]; then
    blacklist "$pkg" "decoder came back"
  fi
}

# manifest check of an installed app: installer permission -> deny the appop (video/ mime types are allowed)
scan_manifest() {
  d=$1
  pkg=$(pkg_of "$d")
  grep -qx "$pkg" "$ALLOW" && return
  apk="$d/base.apk"
  [ -f "$apk" ] || return
  m=$(unzip -p "$apk" AndroidManifest.xml 2>/dev/null | tr -d '\000')
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
  # keep blacklisted apps disabled
  for p in $(cat "$BL" 2>/dev/null); do
    pm list packages -d | grep -qx "package:$p" || { pm path "$p" >/dev/null 2>&1 && blacklist "$p" "re-enabled"; }
  done
  for p in $(cat "$DENY" 2>/dev/null); do
    cmd appops set "$p" REQUEST_INSTALL_PACKAGES deny >/dev/null 2>&1
  done
  for p in $(cat "$MOCK" 2>/dev/null); do
    cmd appops set "$p" android:mock_location allow >/dev/null 2>&1
  done
}

tick=0
while true; do
  touch "$NEWSTAMP"
  for d in $(find /data/app -mindepth 2 -maxdepth 2 -type d -newer "$STAMP" ! -name 'vmdl*' ! -name '*.tmp' 2>/dev/null); do
    scan_libs "$d"
    scan_manifest "$d"
  done
  mv -f "$NEWSTAMP" "$STAMP"
  tick=$((tick+1))
  if [ "$tick" -ge 30 ]; then
    tick=0
    touch -d "@0" "$STAMP"   # next round rescans every installed app
    apply_lists
    cmd appops write-settings >/dev/null 2>&1   # appops are written to disk lazily; flush so tile changes survive a reboot
  fi
  sleep 2
done
