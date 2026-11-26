getprop ro.boot.slot_suffix
for p in boot_a init_boot_a vendor_boot_a boot_b; do echo $p $(blockdev --getsize64 /dev/block/by-name/$p); done
s=$(date +%s); for f in /system/framework/services.jar /system/system_ext/priv-app/MtkSettings/MtkSettings.apk /system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk; do sha256sum $f >/dev/null; done; e=$(date +%s); echo apks_seconds=$((e-s))
s=$(date +%s); sha256sum /dev/block/by-name/boot_a /dev/block/by-name/init_boot_a /dev/block/by-name/vendor_boot_a; e=$(date +%s); echo boots_seconds=$((e-s))
cat /proc/cmdline | tr ' ' '\n' | grep -E 'slot|verified|flash'