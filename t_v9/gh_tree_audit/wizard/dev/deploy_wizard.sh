set -e
T=/data/local/tmp/wz
PS=/mnt/vendor/protect_s/orech
ls $PS/noenforce
mount -o remount,rw /
mount -o remount,rw /vendor
# new vendor files
mkdir -p /vendor/etc/orech
cp $T/orech_wizard.sh /vendor/bin/orech_wizard.sh
cp $T/orech_wizard.rc /vendor/etc/init/orech_wizard.rc
cp $T/GooglePackageInstaller_locked.apk /vendor/etc/orech/GooglePackageInstaller_locked.apk
chmod 755 /vendor/bin/orech_wizard.sh; chmod 644 /vendor/etc/init/orech_wizard.rc /vendor/etc/orech/GooglePackageInstaller_locked.apk
chown root:root /vendor/bin/orech_wizard.sh /vendor/etc/init/orech_wizard.rc /vendor/etc/orech/GooglePackageInstaller_locked.apk
chown root:shell /vendor/etc/orech; chmod 755 /vendor/etc/orech
chcon u:object_r:vendor_file:s0 /vendor/bin/orech_wizard.sh /vendor/etc/init/orech_wizard.rc /vendor/etc/orech/GooglePackageInstaller_locked.apk
chcon u:object_r:vendor_configs_file:s0 /vendor/etc/orech
# the wizard: shrink-in-place write over the original (2 MB over 8.9 MB)
SW=/system/system_ext/priv-app/SetupWizard/SetupWizard.apk
N=$(stat -c %s $T/SetupWizard.apk)
[ $N -le $(stat -c %s $SW) ]
dd if=$T/SetupWizard.apk of=$SW bs=1048576 conv=notrunc
truncate -s $N $SW
sync
sha256sum $SW /vendor/bin/orech_wizard.sh /vendor/etc/init/orech_wizard.rc
# integrity list
for p in /vendor/bin/orech_wizard.sh /vendor/etc/init/orech_wizard.rc $SW; do grep -qx "$p" $PS/paths.txt || echo "$p" >> $PS/paths.txt; done
sh $PS/orech_integrity.sh approve
sh $PS/orech_integrity.sh check; echo check=$?
