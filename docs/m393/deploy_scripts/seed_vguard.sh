D=/data/system/orech_vguard
mkdir -p $D; chmod 700 $D
printf '%s\n' org.videolan.vlc >/dev/null
cat > $D/allowlist.txt <<EOF
com.topjohnwu.magisk
com.koshertech.su
bin.mt.plus.canary
bin.mt.plus
ru.maximoff.apktool
EOF
ls /data/app/*/ 2>/dev/null | grep -i musicolet | head -1 >/dev/null
for p in $(pm list packages | sed 's/package://' | grep -i musicolet); do echo $p >> $D/allowlist.txt; done
echo com.google.android.apps.nbu.files > $D/deny_install.txt
: > $D/mock_allow.txt
chmod 755 /vendor/bin/orech_vguard.sh; chcon u:object_r:vendor_file:s0 /vendor/bin/orech_vguard.sh
ls -l /vendor/bin/orech_vguard.sh; cat $D/allowlist.txtcat /data/local/tmp/disable_list.txt > /data/system/orech_vguard/disable_list.txt
