set -e
ls /mnt/vendor/protect_s/orech/noenforce
echo cur=$(sha256sum /vendor/bin/orech_vguard.sh | cut -d' ' -f1)
[ "$(sha256sum /data/local/tmp/orech_vguard_v12.sh | cut -d' ' -f1)" = 020833de41a18d9aa60014e40961f4bb4cb59bd84509bc506d9362b3d136f9b5 ]
mount -o remount,rw /vendor
dd if=/data/local/tmp/orech_vguard_v12.sh of=/vendor/bin/orech_vguard.sh bs=1048576 conv=notrunc
truncate -s 5511 /vendor/bin/orech_vguard.sh
sync
sha256sum /vendor/bin/orech_vguard.sh
sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve
sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo check=$?
