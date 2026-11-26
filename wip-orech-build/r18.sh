B=/mnt/vendor/protect_s/orech
rm -f $B/testbreak
sh $B/orech_integrity.sh approve
echo check=[$(sh $B/orech_integrity.sh check)]
ls $B/
tail -2 $B/integrity.log
getprop init.svc.orech_integrity
