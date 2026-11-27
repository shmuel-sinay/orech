#!/system/bin/sh
# orech video guard v2: locks native libs that carry video decoders and stops the app at once.
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
[ -f "$STAMP" ] || touch -d "@0" "$STAMP"

is_video_lib() {
  # candidate by file name, then confirm by decoder strings (at least 2 distinct)
  case "${1##*/}" in
    libavcodec*|libffmpeg*|libvlc*|libdav1d*|libvpx*|libde265*|libopenh264*|libx264*|libmpv*|libijkffmpeg*) ;;
    *) return 1 ;;
  esac
  n=0
  for k in h264 hevc vp9 vp8 av1 mpeg4 h263; do
    head -c 4000000 "$1" | strings -a | grep -qi -F -e "$k" && n=$((n+1))
  done
  [ "$n" -ge 2 ]
}

scan_dir() {
  d=$1
  pkg=$(basename "$d"); pkg=${pkg%%-*}
  grep -qx "$pkg" "$ALLOW" && return
  hit=0
  for f in $(find "$d" -type f -name '*.so' 2>/dev/null); do
    grep -qxF "$f" "$STATE" && continue
    if is_video_lib "$f"; then
      chmod 000 "$f"
      echo "$f" >> "$STATE"
      echo "$(date '+%F %T') locked $f" >> "$LOG"
      hit=1
    fi
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
