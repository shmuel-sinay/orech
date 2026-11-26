set -e
T=/data/local/tmp/orech_vguard_v10.sh
mount -o remount,rw /vendor
dd if=$T of=/vendor/bin/orech_vguard.sh bs=1048576 conv=notrunc
truncate -s 5135 /vendor/bin/orech_vguard.sh
sync
sha256sum /vendor/bin/orech_vguard.sh
ls -l /vendor/bin/orech_vguard.sh
sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve
sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo check=$?
ls /mnt/vendor/protect_s/orech/noenforce
