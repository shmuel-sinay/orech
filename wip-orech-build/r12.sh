B=/mnt/vendor/protect_s/orech
cat /data/local/tmp/paths.txt > $B/paths.txt
cat /data/local/tmp/orech_integrity.sh > $B/orech_integrity.sh
sh $B/orech_integrity.sh approve
echo check:; sh $B/orech_integrity.sh check
wc -l $B/manifest.sha256
cut -c1-12,65- $B/manifest.sha256
