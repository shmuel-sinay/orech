ls /mnt/vendor | head -20; ls /dev/block/by-name | tr '\n' ' '; echo; mount | grep -E 'persist|nvdata|protect|nvcfg' ; ls /data/adb/service.d 2>&1 | head -3
