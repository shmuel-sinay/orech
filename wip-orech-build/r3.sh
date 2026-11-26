cat /data/local/tmp/t7.sh > /vendor/bin/orech_vguard.sh
sha256sum /vendor/bin/orech_vguard.sh
rm -f /mnt/vendor/nvdata/orech_id
setprop persist.sys.orech_id ""
stop orech_vguard; start orech_vguard
sleep 3; getprop init.svc.orech_vguard
getprop persist.sys.orech_id