set -e
mount -o remount,rw / 2>&1 || true
cp /system/build.prop /data/local/tmp/bp.backup.device
[ "$(wc -c < /data/local/tmp/bp.new)" = "8040" ] || { echo SIZE_MISMATCH_SRC; exit 1; }
echo "4ccf4a430069cdfa088290930cfd893a96c01cf1678c4a580481ee94578d97a2  /data/local/tmp/bp.new" | sha256sum -c -
dd if=/data/local/tmp/bp.new of=/system/build.prop conv=notrunc 2>&1
truncate -s 8040 /system/build.prop
sync
echo "4ccf4a430069cdfa088290930cfd893a96c01cf1678c4a580481ee94578d97a2  /system/build.prop" | sha256sum -c -
ls -l /system/build.prop
