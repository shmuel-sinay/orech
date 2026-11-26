#!/system/bin/sh
# vsr: locks native libs that carry video decoders; packages in allowlist.txt are skipped.
MODDIR=${0%/*}
LOG=/data/local/tmp/vsr.log
ALLOW=$MODDIR/allowlist.txt
STATE=$MODDIR/locked.txt
STAMP=$MODDIR/.stamp

until [ "$(getprop sys.boot_completed)" = "1" ]; do sleep 5; done
touch "$ALLOW" "$STATE"
[ -f "$STAMP" ] || touch -d "@0" "$STAMP"

is_video_lib() {
  # candidate by name, then confirm by decoder strings (at least 2 distinct)
  case "${1##*/}" in
    libavcodec*|libffmpeg*|libvlc*|libdav1d*|libvpx*|libde265*|libopenh264*|libx264*|libmpv*) ;;
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
  find "$d" -type f -name '*.so' 2>/dev/null | while read -r f; do
    grep -qxF "$f" "$STATE" && continue
    if is_video_lib "$f"; then
      chmod 000 "$f"
      echo "$f" >> "$STATE"
      echo "$(date '+%F %T') locked $f" >> "$LOG"
    fi
  done
}

while true; do
  for d in $(find /data/app -mindepth 2 -maxdepth 2 -type d -newer "$STAMP" ! -name 'vmdl*' ! -name '*.tmp' 2>/dev/null); do
    scan_dir "$d"
  done
  touch "$STAMP"
  sleep 10
done
