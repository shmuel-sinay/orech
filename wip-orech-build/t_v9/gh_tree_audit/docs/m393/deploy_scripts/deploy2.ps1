$adb = "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$d = "C:\orech_build\deploy"
$log = "$d\deploy2.log"
function L($m) { ("{0} {1}" -f (Get-Date -Format "HH:mm:ss"), $m) | Add-Content $log }

function RunJob([string]$cmd, [int]$timeout) {
    $j = Start-Job -ScriptBlock ([scriptblock]::Create($cmd))
    if (Wait-Job $j -Timeout $timeout) { $r = Receive-Job $j; Remove-Job $j -Force; return $r }
    Stop-Job $j; Remove-Job $j -Force; return $null
}
function IsUp() { return [bool](& $adb devices 2>&1 | Select-String "device$") }

function WriteSys([string]$file, [string]$target, [string]$remount) {
    $local = "$d\$file"
    $h = (Get-FileHash -Algorithm SHA256 $local).Hash.ToLower()
    $n = (Get-Item $local).Length
    $tmp = "/data/local/tmp/$file"
    $null = RunJob "& '$adb' push '$local' '$tmp' 2>&1" 90
    $on = RunJob "& '$adb' shell `"su -c 'sha256sum $tmp'`" 2>&1" 30
    if ((("$on") -split "\s+")[0] -ne $h) { return $false }
    $cmd = "su -c 'mount -o remount,rw $remount && dd if=$tmp of=$target bs=1048576 conv=notrunc && truncate -s $n $target && sync && sha256sum $target'"
    $r = RunJob "& '$adb' shell `"$cmd`" 2>&1" 90
    $dh = (($r | Select-String "^[0-9a-f]{64}") -split "\s+")[0]
    return ($dh -eq $h)
}

# wait for the first deploy to finish (or for 45 minutes)
$deadline = (Get-Date).AddMinutes(45)
L "waiting for deploy.log"
while ((Get-Date) -lt $deadline) {
    $t = Get-Content "$d\deploy.log" -ErrorAction SilentlyContinue
    if ($t -match "finished: done=3") { break }
    Start-Sleep 5
}
if (-not (Get-Content "$d\deploy.log" -ErrorAction SilentlyContinue | Select-String "finished: done=3")) { L "first deploy did not finish - abort"; exit 1 }
L "first deploy finished; continuing"

$items = @(
    @{ f = "GooglePackageInstaller_vg.apk"; t = "/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk"; m = "/" },
    @{ f = "orech_vguard_v2.sh";            t = "/vendor/bin/orech_vguard.sh"; m = "/vendor" }
)
$ok = @{}
$deadline2 = (Get-Date).AddMinutes(20)
while ((Get-Date) -lt $deadline2 -and $ok.Count -lt $items.Count) {
    if (-not (IsUp)) { Start-Sleep 3; continue }
    foreach ($x in $items) {
        if ($ok.ContainsKey($x.f)) { continue }
        if (WriteSys $x.f $x.t $x.m) { L "OK $($x.t)"; $ok[$x.f] = $true } else { L "failed $($x.f)"; break }
    }
    Start-Sleep 2
}
L ("second deploy done=" + $ok.Count)
if ($ok.Count -lt $items.Count) { exit 2 }

# fix permissions/context of the vguard script
$null = RunJob "& '$adb' shell `"su -c 'chmod 755 /vendor/bin/orech_vguard.sh; chcon u:object_r:vendor_file:s0 /vendor/bin/orech_vguard.sh'`" 2>&1" 30

L "rebooting"
$null = RunJob "& '$adb' reboot 2>&1" 20
Start-Sleep 25
$boot = $false
$bd = (Get-Date).AddMinutes(6)
while ((Get-Date) -lt $bd) {
    if (IsUp) {
        $b = RunJob "& '$adb' shell getprop sys.boot_completed 2>&1" 15
        if ("$b".Trim() -eq "1") { $boot = $true; break }
    }
    Start-Sleep 5
}
L ("boot completed=" + $boot)
if ($boot) {
    Start-Sleep 25
    foreach ($q in @(
        "getprop init.svc.orech_vguard",
        "su -c 'ps -A | grep orech_vguard | head -2'",
        "getprop persist.sys.usb.config",
        "settings get global adb_enabled",
        "su -c 'sha256sum /system/framework/services.jar /system/system_ext/priv-app/MtkSettings/MtkSettings.apk /system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk /system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk'",
        "logcat -d | grep -c 'FATAL EXCEPTION'")) {
        $o = RunJob "& '$adb' shell `"$q`" 2>&1" 30
        L ("check [" + $q.Substring(0, [Math]::Min(40, $q.Length)) + "] => " + (($o | Out-String).Trim() -replace "\s+", " "))
    }
} else {
    L "device did not boot: attempting restore of originals"
    $res = @(
        @{ f = "services.jar";    t = "/system/framework/services.jar" },
        @{ f = "MtkSettings.apk"; t = "/system/system_ext/priv-app/MtkSettings/MtkSettings.apk" },
        @{ f = "MtkSystemUI.apk"; t = "/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk" }
    )
    $rd = (Get-Date).AddMinutes(10)
    foreach ($x in $res) {
        while ((Get-Date) -lt $rd) {
            if (IsUp -and (WriteSys $x.f $x.t "/")) { L "restored $($x.t)"; break }
            Start-Sleep 5
        }
    }
    $null = RunJob "& '$adb' reboot 2>&1" 20
}
L "finished2"
