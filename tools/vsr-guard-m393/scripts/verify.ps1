# Read-only state check for the vsr_block module.
param([string]$Adb = "adb")

$script = @'
su -c 'M=/data/adb/modules/vsr_block; ls -la $M; echo --- allowlist; cat $M/allowlist.txt; echo --- locked; sort -u $M/locked.txt; echo --- log; cat /data/local/tmp/vsr.log; echo --- running; ps -A -o PID,ARGS | grep "[v]sr_block/service.sh"; echo --- codecs; grep -c "enabled=.false" /vendor/etc/media_codecs_c2.xml; echo --- video-lib permissions; find /data/app \( -name "libavcodec*.so" -o -name "libvlc*.so" -o -name "libffmpeg*.so" \) -exec ls -l {} + | cut -c1-11,40-'
'@
$script | & $Adb shell
