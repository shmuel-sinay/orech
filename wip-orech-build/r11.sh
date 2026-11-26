B=/mnt/vendor/protect_s/orech
cat /data/local/tmp/paths.txt > $B/paths.txt
sh $B/orech_integrity.sh approve
sh $B/orech_integrity.sh check
wc -l $B/manifest.sha256
getprop init.svc.orech_integrity
ps -A | grep -c orech_integrity
tail -3 $B/integrity.log
