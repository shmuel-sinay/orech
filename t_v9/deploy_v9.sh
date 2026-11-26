set -e
B=/mnt/vendor/protect_s/orech
touch $B/noenforce
[ "$(wc -c < /data/local/tmp/t9.sh)" = "5386" ] || { echo SIZE_MISMATCH; exit 1; }
echo "b71c428c9166fc80c7f584df19f8a6067e4831be0de888b5443cd9f3458cf87b  /data/local/tmp/t9.sh" | sha256sum -c -
sh -n /data/local/tmp/t9.sh
mount -o remount,rw /vendor
cp /vendor/bin/orech_vguard.sh /data/local/tmp/orech_vguard.v8.bak
dd if=/data/local/tmp/t9.sh of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1
truncate -s 5386 /vendor/bin/orech_vguard.sh
sync
echo "b71c428c9166fc80c7f584df19f8a6067e4831be0de888b5443cd9f3458cf87b  /vendor/bin/orech_vguard.sh" | sha256sum -c -
sh $B/orech_integrity.sh approve
echo check=[$(sh $B/orech_integrity.sh check)]
stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard
