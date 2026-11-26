BL=/data/system/orech_vguard/blacklist.txt
grep -vx com.orech.seller $BL > /data/local/tmp/bl.new
cat /data/local/tmp/bl.new > $BL
P=$(cat /data/local/tmp/v9test.path)
rm -f ${P}lib/arm64/libffmpeg_test.so
rmdir ${P}lib/arm64 ${P}lib 2>/dev/null
pm enable com.orech.seller
sed -i '/libffmpeg_test/d' /data/system/orech_vguard/locked.txt
rm -f /data/local/tmp/bl.new /data/local/tmp/v9test.path /data/local/tmp/ph1.sh /data/local/tmp/clean.sh
echo BL_LINES=$(wc -l < $BL)
pm list packages -d | grep -c seller
ls ${P}
