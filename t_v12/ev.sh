rm -f /data/local/tmp/ev1.txt /data/local/tmp/ev2.txt
timeout 25 inotifyd - /data/system:ynd > /data/local/tmp/ev1.txt 2>&1 &
timeout 25 inotifyd - /data/app:ncy > /data/local/tmp/ev2.txt 2>&1 &
sleep 3
pm install /data/local/tmp/victim.apk
mkdir -p /data/data/com.zz.victim/files
sleep 3
pm uninstall com.zz.victim
sleep 22
echo ---system; cat /data/local/tmp/ev1.txt
echo ---app; cat /data/local/tmp/ev2.txt
