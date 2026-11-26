mount -o remount,rw /product 2>&1
mkdir -p /product/app/OrechMockLoc
cat /data/local/tmp/OrechMockLoc.apk > /product/app/OrechMockLoc/OrechMockLoc.apk
chmod 755 /product/app/OrechMockLoc; chmod 644 /product/app/OrechMockLoc/OrechMockLoc.apk
chcon -R u:object_r:system_file:s0 /product/app/OrechMockLoc
sha256sum /product/app/OrechMockLoc/OrechMockLoc.apk
ls -l /product/app/OrechMockLoc