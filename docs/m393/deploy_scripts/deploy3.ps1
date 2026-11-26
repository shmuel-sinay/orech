$adb = "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$d = "C:\orech_build\deploy"
$log = "$d\deploy3.log"
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
    $cmd = "su -c 'mount -o remount,rw / && dd if=$tmp of=$target bs=1048576 conv=notrunc && truncate -s $n $target && sync && sha256sum $target'"
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
if (-not (WriteSys "services_final2.jar" $svc)) { L "write services_final2 FAILED (nothing changed if hash check failed before dd)"; exit 2 }
L "written services_final2.jar; rebooting"
$null = RunJob "& '$adb' reboot 2>&1" 20
$ok = WaitBoot 4
L ("boot completed=" + $ok)
if (-not $ok) {
    L "no boot: restoring services_final.jar (previous good build)"
    $restored = $false
    $rd = (Get-Date).AddMinutes(10)
    while ((Get-Date) -lt $rd -and -not $restored) {
        if (IsUp) { $restored = WriteSys "services_final.jar" $svc }
        if (-not $restored) { Start-Sleep 5 }
    }
    L ("restored=" + $restored)
    $null = RunJob "& '$adb' reboot 2>&1" 20
    $ok2 = WaitBoot 4
    L ("boot after restore=" + $ok2)
    L "finished3-failed"
    exit 3
}
Start-Sleep 20
foreach ($q in @(
    "su -c 'sha256sum $svc'",
    "getprop init.svc.orech_vguard",
    "settings get global adb_enabled",
    "logcat -d | grep -c 'FATAL EXCEPTION'",
    "dumpsys package com.android.settings | grep -m1 versionName")) {
    $o = RunJob "& '$adb' shell `"$q`" 2>&1" 30
    L ("check [" + $q.Substring(0, [Math]::Min(40, $q.Length)) + "] => " + (($o | Out-String).Trim() -replace "\s+", " "))
}
L "finished3-ok"
