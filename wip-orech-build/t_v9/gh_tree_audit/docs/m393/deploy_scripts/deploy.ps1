$adb = "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$d = "C:\orech_build\deploy"
$log = "$d\deploy.log"
function L($m) { ("{0} {1}" -f (Get-Date -Format "HH:mm:ss"), $m) | Add-Content $log }

function RunJob([scriptblock]$sb, [int]$timeout) {
    $j = Start-Job -ScriptBlock $sb
    if (Wait-Job $j -Timeout $timeout) { $r = Receive-Job $j; Remove-Job $j -Force; return $r }
    Stop-Job $j; Remove-Job $j -Force; return $null
}

$targets = @(
    @{ f = "services_final.jar";    t = "/system/framework/services.jar" },
    @{ f = "MtkSystemUI_final.apk"; t = "/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk" },
    @{ f = "MtkSettings_final.apk"; t = "/system/system_ext/priv-app/MtkSettings/MtkSettings.apk" }
)
$done = @{}
$deadline = (Get-Date).AddMinutes(40)
L "start"
while ((Get-Date) -lt $deadline -and $done.Count -lt $targets.Count) {
    $dev = & $adb devices 2>&1 | Select-String "device$"
    if (-not $dev) { Start-Sleep 3; continue }
    foreach ($x in $targets) {
        if ($done.ContainsKey($x.f)) { continue }
        $local = "$d\$($x.f)"
        $h = (Get-FileHash -Algorithm SHA256 $local).Hash.ToLower()
        $n = (Get-Item $local).Length
        $tmp = "/data/local/tmp/$($x.f)"
        $r1 = RunJob ([scriptblock]::Create("& '$adb' push '$local' '$tmp' 2>&1")) 90
        $on = RunJob ([scriptblock]::Create("& '$adb' shell `"su -c 'sha256sum $tmp'`" 2>&1")) 30
        $onh = ("$on" -split "\s+")[0]
        if ($onh -ne $h) { L "push/verify failed for $($x.f): '$onh'"; break }
        $cmd = "su -c 'mount -o remount,rw / && dd if=$tmp of=$($x.t) bs=1048576 conv=notrunc && truncate -s $n $($x.t) && sync && sha256sum $($x.t)'"
        $r2 = RunJob ([scriptblock]::Create("& '$adb' shell `"$cmd`" 2>&1")) 90
        $dh = (($r2 | Select-String "^[0-9a-f]{64}") -split "\s+")[0]
        if ($dh -eq $h) { L "OK $($x.t)"; $done[$x.f] = $true } else { L "write failed for $($x.f): '$dh'"; break }
    }
    Start-Sleep 2
}
L ("finished: done=" + $done.Count + " of " + $targets.Count)

