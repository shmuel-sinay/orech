#!/system/bin/sh
# second launcher, independent of /vendor: survives a reflash of super while boot (Magisk) and data are intact
[ -f /mnt/vendor/protect_s/orech/orech_integrity.sh ] && sh /mnt/vendor/protect_s/orech/orech_integrity.sh monitor &