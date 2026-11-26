B=/mnt/vendor/protect_s/orech
rmdir /dev/orech_integrity.lock 2>/dev/null
cp $B/manifest.sha256 /data/local/tmp/man.bak
sed -i '5s/^./0/' $B/manifest.sha256
ORECH_DRYRUN=1 ORECH_GRACE=2 ORECH_INTERVAL=3 timeout 80 sh $B/orech_integrity.sh monitor
sh $B/orech_integrity.sh approve
rmdir /dev/orech_integrity.lock 2>/dev/null
tail -6 $B/integrity.log