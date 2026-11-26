set -e
B=/mnt/vendor/protect_s/orech
touch $B/noenforce
[ "$(wc -c < /data/local/tmp/t11.sh)" = "4955" ] || { echo SIZE_MISMATCH; exit 1; }
echo "f6e4f97681e17df27661bbda305f1f20078569b3651d198be81dccef8e87cda8  /data/local/tmp/t11.sh" | sha256sum -c -
sh -n /data/local/tmp/t11.sh
mount -o remount,rw /vendor
cp /vendor/bin/orech_vguard.sh /data/local/tmp/orech_vguard.v10dev.bak
dd if=/data/local/tmp/t11.sh of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1
truncate -s 4955 /vendor/bin/orech_vguard.sh
sync
echo "f6e4f97681e17df27661bbda305f1f20078569b3651d198be81dccef8e87cda8  /vendor/bin/orech_vguard.sh" | sha256sum -c -
sh $B/orech_integrity.sh approve
echo check=[$(sh $B/orech_integrity.sh check)]
stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard
