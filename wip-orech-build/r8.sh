B=/mnt/vendor/protect_s/orech
mkdir -p $B
cat /data/local/tmp/orech_integrity.sh > $B/orech_integrity.sh
cat /data/local/tmp/paths.txt > $B/paths.txt
chmod 700 $B/orech_integrity.sh; chmod 600 $B/paths.txt
mount -o remount,rw /vendor
cat /data/local/tmp/orech_integrity.rc > /vendor/etc/init/orech_integrity.rc
chmod 644 /vendor/etc/init/orech_integrity.rc; chcon u:object_r:vendor_file:s0 /vendor/etc/init/orech_integrity.rc
mkdir -p /data/adb/service.d
cat /data/local/tmp/orech_integrity_launcher.sh > /data/adb/service.d/orech_integrity.sh
chmod 755 /data/adb/service.d/orech_integrity.sh
sh $B/orech_integrity.sh approve
echo "--- manifest"; cat $B/manifest.sha256 | cut -c1-16,65-
echo "--- check (expect empty):"; sh $B/orech_integrity.sh check
echo "--- tamper simulation"
cp $B/manifest.sha256 $B/manifest.bak
sed -i '5s/^./0/' $B/manifest.sha256
echo "check ->"; sh $B/orech_integrity.sh check
sh $B/orech_integrity.sh approve
echo "after approve ->"; sh $B/orech_integrity.sh check
rm -f $B/manifest.bak