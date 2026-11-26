# Restores permissions of every locked lib and removes the module completely.
param([string]$Adb = "adb")

$script = @'
su -c 'M=/data/adb/modules/vsr_block; pkill -f "vsr_blo[c]k/service.sh"; sh $M/uninstall.sh; rm -rf $M /data/local/tmp/vsr_mod /data/local/tmp/vsr.log'
'@
$script | & $Adb shell
Write-Host "Module removed. Reboot the device to be sure nothing is left running."
