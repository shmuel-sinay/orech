B=/mnt/vendor/protect_s/orech
ls -laZ /product/app/OrechFiles
chcon u:object_r:system_file:s0 /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk
sync
echo "2416a510aad42075aba7b2e6f0f16f1369649c5e71a36c95a3d93ff2149390f9  /product/app/OrechFiles/OrechFiles.apk" | sha256sum -c -
touch /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk
rm -rf /data/system/package_cache
grep -qx /product/app/OrechFiles/OrechFiles.apk $B/paths.txt || echo /product/app/OrechFiles/OrechFiles.apk >> $B/paths.txt
sh $B/orech_integrity.sh approve
echo check=[$(sh $B/orech_integrity.sh check)]
ls -laZ /product/app/OrechFiles; tail -2 $B/paths.txt; ls $B | grep noenforce
