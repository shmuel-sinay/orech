$adb = "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$d = "C:\orech_build\deploy"
$log = "$d\deploy8.log"
function L($m) { ("{0} {1}" -f (Get-Date -Format "HH:mm:ss"), $m) | Add-Content $log }
function RunJob([string]$cmd, [int]$timeout) {
    $j = Start-Job -ScriptBlock ([scriptblock]::Create($cmd))
    if (Wait-Job $j -Timeout $timeout) { $r = Receive-Job $j; Remove-Job $j -Force; return $r }
    Stop-Job $j; Remove-Job $j -Force; return $null
}
function IsUp() { return [bool](& $adb devices 2>&1 | Select-String "device$") }
function WriteSys([string]$file, [string]$target) {
    $local = "$d\$file"
    $h = (Get-FileHash -Algorithm SHA256 $local).Hash.ToLower()
    $n = (Get-Item $local).Length
    $tmp = "/data/local/tmp/$file"
    $null = RunJob "& '$adb' push '$local' '$tmp' 2>&1" 90
    $on = RunJob "& '$adb' shell `"su -c 'sha256sum $tmp'`" 2>&1" 30
    if ((("$on") -split "\s+")[0] -ne $h) { return $false }
    $cmd = "su -c 'mount -o remount,rw / ; mount -o remount,rw /vendor ;  dd if=$tmp of=$target bs=1048576 conv=notrunc && truncate -s $n $target && sync && sha256sum $target'"
    $r = RunJob "& '$adb' shell `"$cmd`" 2>&1" 90
    $dh = (($r | Select-String "^[0-9a-f]{64}") -split "\s+")[0]
    return ($dh -eq $h)
}
function WaitBoot([int]$minutes) {
    Start-Sleep 25
    $end = (Get-Date).AddMinutes($minutes)
    while ((Get-Date) -lt $end) {
        if (IsUp) {
            $b = RunJob "& '$adb' shell getprop sys.boot_completed 2>&1" 15
            if ("$b".Trim() -eq "1") { return $true }
        }
        Start-Sleep 5
    }
    return $false
}

$svc = "/system/framework/services.jar"
L "start"
if (-not (IsUp)) { L "device not up"; exit 1 }
$items = @(
    @{ f = "MtkSettings_v5.apk";   t = "/system/system_ext/priv-app/MtkSettings/MtkSettings.apk" },
    @{ f = "orech_vguard_v6.sh";   t = "/vendor/bin/orech_vguard.sh" }
)
foreach ($x in $items) {
    if (-not (WriteSys $x.f $x.t)) { L ("write FAILED " + $x.f); exit 2 }
    L ("OK " + $x.t)
}
$null = RunJob "& '$adb' push 'C:\orech_build\deploy\seed_vguard.sh' /data/local/tmp/seed_vguard.sh 2>&1" 30
$sr = RunJob "& '$adb' shell `"su -c 'sh /data/local/tmp/seed_vguard.sh'`" 2>&1" 30
L ("seed => " + (($sr | Out-String).Trim() -replace "\s+"," "))
L "rebooting"
$sw=[Diagnostics.Stopwatch]::StartNew(); $null = RunJob "& '$adb' reboot 2>&1" 20
$ok = WaitBoot 4; L ("boot seconds=" + [int]$sw.Elapsed.TotalSeconds)
L ("boot completed=" + $ok)
if (-not $ok) {
    L "no boot: restoring previous Settings"
    $restored = $false
    $rd = (Get-Date).AddMinutes(10)
    while ((Get-Date) -lt $rd -and -not $restored) {
        if (IsUp) { $restored = WriteSys "MtkSettings_v4.apk" "/system/system_ext/priv-app/MtkSettings/MtkSettings.apk" }
        if (-not $restored) { Start-Sleep 5 }
    }
    L ("restored=" + $restored)
    $null = RunJob "& '$adb' reboot 2>&1" 20
    L ("boot after restore=" + (WaitBoot 4))
    L "finished8-failed"
    exit 3
}
Start-Sleep 20
foreach ($q in @(
    "su -c 'sha256sum /system/framework/services.jar /system/system_ext/priv-app/MtkSettings/MtkSettings.apk /system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk /system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk'",
    "getprop init.svc.orech_vguard",
    "settings get global adb_enabled",
    "logcat -d | grep -c 'FATAL EXCEPTION'")) {
    $o = RunJob "& '$adb' shell `"$q`" 2>&1" 30
    L ("check [" + $q.Substring(0, [Math]::Min(40, $q.Length)) + "] => " + (($o | Out-String).Trim() -replace "\s+", " "))
}
L "finished8-ok"
