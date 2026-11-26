#!/system/bin/sh
# orech video guard v3: locks native video-engine libs by name and stops the app at once.
# Packages listed in allowlist.txt are skipped. State lives in /data/system/orech_vguard.
D=/data/system/orech_vguard
LOG=$D/vguard.log
ALLOW=$D/allowlist.txt
STATE=$D/locked.txt
STAMP=$D/.stamp
NEWSTAMP=$D/.stamp.new

mkdir -p "$D"
chmod 700 "$D"

until [ "$(getprop sys.boot_completed)" = "1" ]; do sleep 3; done
touch "$ALLOW" "$STATE"
# full rescan on every start so apps installed while the guard was down are caught too
touch -d "@0" "$STAMP"

is_video_lib() {
  case "${1##*/}" in
    libavcodec*|libffmpeg*|libvlc*|libdav1d*|libvpx*|libde265*|libopenh264*|libx264*|libmpv*|libijkffmpeg*|libijksdl*|libmedia3*|libexo*) return 0 ;;
    *) return 1 ;;
  esac
}

scan_dir() {
  d=$1
  pkg=$(basename "$d"); pkg=${pkg%%-*}
  grep -qx "$pkg" "$ALLOW" && return
  hit=0
  for f in $(find "$d" -type f -name '*.so' 2>/dev/null); do
    is_video_lib "$f" || continue
    # already locked (mode 000)? then nothing to do
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

while true; do
  touch "$NEWSTAMP"
  for d in $(find /data/app -mindepth 2 -maxdepth 2 -type d -newer "$STAMP" ! -name 'vmdl*' ! -name '*.tmp' 2>/dev/null); do
    scan_dir "$d"
  done
  mv -f "$NEWSTAMP" "$STAMP"
  sleep 2
done
