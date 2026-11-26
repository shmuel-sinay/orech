B=/mnt/vendor/protect_s/orech
touch $B/noenforce
cat /data/local/tmp/orech_integrity.sh > $B/orech_integrity.sh
cat /data/local/tmp/paths.txt > $B/paths.txt
mount -o remount,rw /vendor
cat /data/local/tmp/orech_integrity.rc > /vendor/etc/init/orech_integrity.rc
chmod 644 /vendor/etc/init/orech_integrity.rc; chcon u:object_r:vendor_file:s0 /vendor/etc/init/orech_integrity.rc
sh $B/orech_integrity.sh approve
rm -f $B/noenforce
echo "check=[$(sh $B/orech_integrity.sh check)]"
wc -l $B/manifest.sha256
cut -c1-12,65- $B/manifest.sha256 | tail -4