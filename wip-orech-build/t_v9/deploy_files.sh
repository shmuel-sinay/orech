set -e
B=/mnt/vendor/protect_s/orech
touch $B/noenforce
[ "$(wc -c < /data/local/tmp/orechfiles.apk)" = "190036" ] || { echo SIZE_MISMATCH; exit 1; }
echo "2416a510aad42075aba7b2e6f0f16f1369649c5e71a36c95a3d93ff2149390f9  /data/local/tmp/orechfiles.apk" | sha256sum -c -
pm uninstall com.orech.files || true
mount -o remount,rw /product
mkdir -p /product/app/OrechFiles
cat /data/local/tmp/orechfiles.apk > /product/app/OrechFiles/OrechFiles.apk
chmod 755 /product/app/OrechFiles; chmod 644 /product/app/OrechFiles/OrechFiles.apk
chown root:root /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk
chcon --reference=/product/app/OrechMockLoc /product/app/OrechFiles
chcon --reference=/product/app/OrechMockLoc/OrechMockLoc.apk /product/app/OrechFiles/OrechFiles.apk
sync
echo "2416a510aad42075aba7b2e6f0f16f1369649c5e71a36c95a3d93ff2149390f9  /product/app/OrechFiles/OrechFiles.apk" | sha256sum -c -
touch /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk
rm -rf /data/system/package_cache
grep -qx /product/app/OrechFiles/OrechFiles.apk $B/paths.txt || echo /product/app/OrechFiles/OrechFiles.apk >> $B/paths.txt
sh $B/orech_integrity.sh approve
echo check=[$(sh $B/orech_integrity.sh check)]
ls -laZ /product/app/OrechFiles; tail -3 $B/paths.txt
