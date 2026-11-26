set -e
ls /mnt/vendor/protect_s/orech/noenforce
[ "$(sha256sum /data/local/tmp/SetupWizard_new.apk | cut -d' ' -f1)" = aec0cc712cb401b3de2e9c8f4dae838ef8b6e43b9f5053211ea71c294b4b215e ]
mount -o remount,rw /
SW=/system/system_ext/priv-app/SetupWizard/SetupWizard.apk
dd if=/data/local/tmp/SetupWizard_new.apk of=$SW bs=1048576 conv=notrunc
truncate -s 2053360 $SW
sync
sha256sum $SW
touch /system/system_ext/priv-app/SetupWizard /system/system_ext/priv-app/SetupWizard/SetupWizard.apk 2>&1 || true
rm -rf /data/system/package_cache
sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve
sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo check=$?
