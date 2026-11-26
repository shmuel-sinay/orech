B=/mnt/vendor/protect_s/orech
touch $B/noenforce
mount -o remount,rw /vendor
cat /data/local/tmp/t8.sh > /vendor/bin/orech_vguard.sh
sha256sum /vendor/bin/orech_vguard.sh
sh $B/orech_integrity.sh approve
echo check=[$(sh $B/orech_integrity.sh check)]
stop orech_vguard; start orech_vguard; sleep 2; getprop init.svc.orech_vguard