touch /mnt/vendor/protect_s/orech/noenforce
mount -o remount,rw /product
cat /data/local/tmp/OrechMockLoc.apk > /product/app/OrechMockLoc/OrechMockLoc.apk
sha256sum /product/app/OrechMockLoc/OrechMockLoc.apk
sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve
rm -f /mnt/vendor/protect_s/orech/noenforce
sh /mnt/vendor/protect_s/orech/orech_integrity.sh check
