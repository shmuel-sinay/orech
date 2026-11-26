F=/data/system/users/0/package-restrictions.xml
cp $F /data/local/tmp/package-restrictions.abx.bak
ls -l /data/system/users/0/ | grep -i restr
rm -f $F /data/system/users/0/package-restrictions-backup.xml
: > /data/system/orech_vguard/disable_list.txt
ls -l /data/system/users/0/ | grep -i restr
echo removed