B=/mnt/vendor/protect_s/orech
touch $B/noenforce
mount -o remount,rw /product
cat /data/local/tmp/OrechMockLoc.apk > /product/app/OrechMockLoc/OrechMockLoc.apk
touch /product/app/OrechMockLoc /product/app/OrechMockLoc/OrechMockLoc.apk
rm -rf /data/system/package_cache
sh $B/orech_integrity.sh approve
rm -f $B/noenforce
echo check=[$(sh $B/orech_integrity.sh check)]