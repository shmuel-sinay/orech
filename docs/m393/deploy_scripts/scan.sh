#!/system/bin/sh
dd if=/dev/block/dm-1 bs=4194304 2>/dev/null | grep -a -b -o 'ro.build.display.id=R04_62_3_16' > /data/local/tmp/scan.out
echo done > /data/local/tmp/scan.done
