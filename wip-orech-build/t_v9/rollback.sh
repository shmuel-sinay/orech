set -e
B=/mnt/vendor/protect_s/orech
touch $B/noenforce
S=/data/local/tmp/orech_vguard.v8.bak
[ "$(wc -c < $S)" = "4938" ] || { echo SIZE_MISMATCH; exit 1; }
echo "890863c7b1837363fd63e26fc963b90829e14584c7c7cf5b382deb2c3406f193  $S" | sha256sum -c -
mount -o remount,rw /vendor
dd if=$S of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1
truncate -s 4938 /vendor/bin/orech_vguard.sh
sync
echo "890863c7b1837363fd63e26fc963b90829e14584c7c7cf5b382deb2c3406f193  /vendor/bin/orech_vguard.sh" | sha256sum -c -
rm -f /data/system/orech_allow_install
sh $B/orech_integrity.sh approve
echo check=[$(sh $B/orech_integrity.sh check)]
stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard
ls /data/system/orech_allow_install 2>&1 || true
