set -e
[ "$(sha256sum /data/local/tmp/bp.new | cut -d' ' -f1)" = 7d249d6045b3e1894b516128e4348f885fb134cef6fd1212345a1f24c84ac269 ]
mount -o remount,rw /
dd if=/data/local/tmp/bp.new of=/system/build.prop bs=1048576 conv=notrunc
truncate -s 8035 /system/build.prop
sync
sha256sum /system/build.prop
sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve
sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo check=$?
