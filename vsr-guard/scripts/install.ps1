# Installs the vsr_block Magisk module on the connected device and starts it.
# Requires: adb in PATH or -Adb, device with root (Magisk su), authorized over USB.
param([string]$Adb = "adb")

$repo = Split-Path -Parent $PSScriptRoot
& $Adb devices -l
& $Adb push "$repo\module" /data/local/tmp/vsr_mod

$script = @'
su -c 'M=/data/adb/modules/vsr_block; mkdir -p $M && cp /data/local/tmp/vsr_mod/* $M/ && chown -R 0:0 $M && chmod 755 $M $M/*.sh && chmod 644 $M/module.prop $M/allowlist.txt && chcon -R u:object_r:system_file:s0 $M; ls -l $M'
su -c 'sh -n /data/adb/modules/vsr_block/service.sh && echo syntax-ok'
su -c 'pkill -f "vsr_blo[c]k/service.sh"'
su -c 'rm -f /data/adb/modules/vsr_block/.stamp; nohup sh /data/adb/modules/vsr_block/service.sh >/dev/null 2>&1 &'
'@
$script | & $Adb shell
