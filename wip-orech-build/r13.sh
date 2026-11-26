B=/mnt/vendor/protect_s/orech
echo check=[$(sh $B/orech_integrity.sh check)]
tail -2 $B/integrity.log
ls $B/noenforce 2>&1
getprop init.svc.orech_integrity
getprop init.svc.orech_vguard
logcat -d | grep -c 'FATAL EXCEPTION'
