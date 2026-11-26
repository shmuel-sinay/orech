#!/system/bin/sh
# orech video guard v4.
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
STATE=$D/locked.txt
STAMP=$D/.stamp
NEWSTAMP=$D/.stamp.new

mkdir -p "$D"
chmod 700 "$D"

until [ "$(getprop sys.boot_completed)" = "1" ]; do sleep 3; done
touch "$ALLOW" "$DENY" "$MOCK" "$STATE"
touch -d "@0" "$STAMP"

is_video_lib() {
  case "${1##*/}" in
    libavcodec*|libffmpeg*|libvlc*|libdav1d*|libvpx*|libde265*|libopenh264*|libx264*|libmpv*|libijkffmpeg*|libijksdl*|libmedia3*|libexo*) return 0 ;;
    *) return 1 ;;
  esac
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
    am force-stop "$pkg" >/dev/null 2>&1
    echo "$(date '+%F %T') force-stop $pkg" >> "$LOG"
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
    pm disable-user --user 0 "$pkg" >/dev/null 2>&1
    am force-stop "$pkg" >/dev/null 2>&1
    echo "$(date '+%F %T') disabled (manifest video/) $pkg" >> "$LOG"
    return
  fi
  if echo "$m" | grep -a -q 'INSTALL_PACKAGES'; then
    cmd appops set "$pkg" REQUEST_INSTALL_PACKAGES deny >/dev/null 2>&1
  fi
}

apply_lists() {
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
  fi
  sleep 2
done
