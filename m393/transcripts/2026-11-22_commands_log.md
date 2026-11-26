# Ordered command log (extracted from the session transcript)

367 Bash/PowerShell invocations, in order.

## 1. Bash: Check connected adb devices
```
"C:/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe" devices 2>&1 | head
```

## 2. Bash: Show app gradle build file
```
cat /c/orech_build/app/build.gradle* | head -60
```

## 3. Bash: Show app gradle build file
```
cat /c/orech_build/app/build.gradle* | head -60
```

## 4. PowerShell: Locate gradle, JDK and build dir contents
```
Get-ChildItem "$env:USERPROFILE\.gradle\wrapper\dists" -Recurse -Filter gradle.bat | Select-Object -ExpandProperty FullName; Test-Path "C:\Program Files\Android\Android Studio\jbr\bin\java.exe"; Get-ChildItem C:\orech_build -Force | Select-Object -ExpandProperty Name
```

## 5. PowerShell: Run unit tests with gradle
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain --offline 2>&1 | Select-Object -Last 40
```

## 6. PowerShell: Run unit tests with gradle
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain --offline 2>&1 | Select-Object -Last 40
```

## 7. PowerShell: Run unit tests and show result summary
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain 2>&1 | Select-Object -Last 30
Get-ChildItem C:\orech_build\app\build\test-results\testDebugUnitTest\*.xml | ForEach-Object { Select-String -Path $_.FullName -Pattern '<testsuite ' | ForEach-Object { $_.Line } }
```

## 8. PowerShell: Run tests against deliberately broken alignment
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern 'FAILED|tests completed|BUILD'
```

## 9. PowerShell: Run tests against deliberately broken alignment
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern 'FAILED|tests completed|BUILD'
```

## 10. PowerShell: Re-run tests after restoring ZipUtil
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern 'FAILED|BUILD'
Select-String -Path C:\orech_build\app\build\test-results\testDebugUnitTest\*.xml -Pattern '<testsuite ' | ForEach-Object { $_.Line }
```

## 11. PowerShell: Re-run tests after restoring ZipUtil
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern 'FAILED|BUILD'
Select-String -Path C:\orech_build\app\build\test-results\testDebugUnitTest\*.xml -Pattern '<testsuite ' | ForEach-Object { $_.Line }
```

## 12. Bash: Append changelog line and show git status
```
printf '%s\n' '' '- בדיקות JVM ל-`ZipUtil` נוספו (`ZipUtilTest.kt`, 4 בדיקות, כולן עברו; שיבוש היישור הכשיל בדיקה אחת כמצופה). לא נגעו במכשיר. פירוט הרצה ב-HANDOFF סעיף 8.7.' >> "/c/פרוייקטים קלוד/zamir/CHANGELOG_M393.md"; tail -3 "/c/פרוייקטים קלוד/zamir/CHANGELOG_M393.md"; cd /c/orech_build && git status --short | head
```

## 13. Bash: Move appended line inside RTL div
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='CHANGELOG_M393.md'
s=open(p,encoding='utf-8').read()
i=s.rindex('</div>')
tail=s[i+6:]
s=s[:i].rstrip('\n')+'\n'+tail.strip('\n')+'\n\n</div>\n'
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
tail -4 CHANGELOG_M393.md
```

## 14. Bash: Read-only device state and integrity check
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices
"$ADB" shell getprop sys.boot_completed
"$ADB" shell getprop ro.serialno
"$ADB" shell getprop init.svc.orech_vguard
"$ADB" shell "su -c 'ls -la /mnt/vendor/protect_s/orech/; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; tail -5 /mnt/vendor/protect_s/orech/integrity.log'"
"$ADB" shell "dumpsys window | grep -E 'mDreamingLockscreen|isKeyguardShowing|KeyguardShowing' | head -3"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 15. Bash: Read-only device state and integrity check
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices
"$ADB" shell getprop sys.boot_completed
"$ADB" shell getprop ro.serialno
"$ADB" shell getprop init.svc.orech_vguard
"$ADB" shell "su -c 'ls -la /mnt/vendor/protect_s/orech/; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; tail -5 /mnt/vendor/protect_s/orech/integrity.log'"
"$ADB" shell "dumpsys window | grep -E 'mDreamingLockscreen|isKeyguardShowing|KeyguardShowing' | head -3"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 16. Bash: Restart adb server and list USB devices
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" kill-server; sleep 2; "$ADB" start-server 2>&1 | tail -2; sleep 5; "$ADB" devices -l
powershell -NoProfile -Command "Get-PnpDevice -PresentOnly | Where-Object { \$_.FriendlyName -match 'ADB|Android|MTP|Composite|MediaTek|PreLoader|VCOM' } | Select-Object Status,FriendlyName | Format-Table -AutoSize"
```

## 17. Bash: Check adb, git remote and GitHub access
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"; "$ADB" devices -l
cd /c/orech_build && git remote -v && git branch --show-current && git log --oneline -3
gh auth status 2>&1 | head -5
gh repo view shmuel-sinay/orech --json name,visibility,defaultBranchRef,pushedAt 2>&1 | head -5
```

## 18. Bash: Check adb, git remote and GitHub access
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"; "$ADB" devices -l
cd /c/orech_build && git remote -v && git branch --show-current && git log --oneline -3
gh auth status 2>&1 | head -5
gh repo view shmuel-sinay/orech --json name,visibility,defaultBranchRef,pushedAt 2>&1 | head -5
```

## 19. Bash: Read-only check of public GitHub repo
```
cd /c/orech_build && GIT_TERMINAL_PROMPT=0 git ls-remote https://github.com/shmuel-sinay/orech 2>&1 | head -5
```

## 20. PowerShell: Find seller module gradle task
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" tasks --all --console=plain 2>&1 | Select-String -Pattern 'seller.*assembleDebug|^:?seller:' | Select-Object -First 5
Get-Content settings.gradle.kts
```

## 21. PowerShell: Find seller module gradle task
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" tasks --all --console=plain 2>&1 | Select-String -Pattern 'seller.*assembleDebug|^:?seller:' | Select-Object -First 5
Get-Content settings.gradle.kts
```

## 22. PowerShell: Build seller APK and rerun unit tests
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :seller:assembleDebug :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern 'error|FAILED|BUILD|^e: '
Get-ChildItem C:\orech_build\seller\build\outputs\apk\debug\*.apk | Select-Object FullName,Length,LastWriteTime
Select-String -Path C:\orech_build\app\build\test-results\testDebugUnitTest\*.xml -Pattern '<testsuite ' | ForEach-Object { $_.Line }
```

## 23. Bash: Copy APK and run read-only device checks
```
cp /c/orech_build/seller/build/outputs/apk/debug/seller-debug.apk "/c/פרוייקטים קלוד/zamir/seller-debug.apk" && ls -l "/c/פרוייקטים קלוד/zamir/seller-debug.apk"
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell getprop sys.boot_completed
"$ADB" shell getprop init.svc.orech_vguard
"$ADB" shell "su -c 'ls /mnt/vendor/protect_s/orech/; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell "dumpsys window | grep -iE 'mDreamingLockscreen|showing=' | head -3"
```

## 24. Bash: Install seller APK with temporary allow file
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'touch /data/system/orech_allow_install'"
"$ADB" install -r "C:/פרוייקטים קלוד/zamir/seller-debug.apk" 2>&1 | tail -3
"$ADB" shell "su -c 'rm -f /data/system/orech_allow_install; ls /data/system/orech_allow_install 2>&1'"
"$ADB" shell pm list packages | grep -i orech
"$ADB" shell "cmd package resolve-activity --brief -c android.intent.category.LAUNCHER com.orech.seller 2>&1 | tail -2"
```

## 25. Bash: Launch seller app and take screenshot
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell am start -n com.orech.seller/.SellerActivity | tail -2; sleep 3
"$ADB" exec-out screencap -p > "C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad/seller1.png"; ls -l "C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad/seller1.png"
```

## 26. Bash: Tap grant on root dialog and screenshot
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
sleep 4; "$ADB" shell input tap 122 632; sleep 3
"$ADB" exec-out screencap -p > "C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad/seller2.png"
```

## 27. Bash: Inspect raw bytes of the id-writing lines
```
sed -n '108,111p' /c/orech_build/seller/src/main/java/com/orech/seller/SellerActivity.kt | od -c | head -20; cd /c/orech_build && git diff --stat -- seller | tail -2; git log -3 --format='%h %s' -- seller
```

## 28. Bash: Inspect raw bytes of the id-writing lines
```
sed -n '108,111p' /c/orech_build/seller/src/main/java/com/orech/seller/SellerActivity.kt | od -c | head -20; cd /c/orech_build && git diff --stat -- seller | tail -2; git log -3 --format='%h %s' -- seller
```

## 29. Bash: Open device-id dialog and type test id
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell input tap 210 258; sleep 2
"$ADB" shell input text T123; sleep 1
"$ADB" exec-out screencap -p > "$SP/seller3.png"
```

## 30. Bash: Save id in dialog and inspect results
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell input tap 88 543; sleep 3
"$ADB" exec-out screencap -p > "$SP/seller4.png"
"$ADB" shell "su -c 'cat /mnt/vendor/nvdata/orech_id | od -c | head -2; getprop persist.sys.orech_id'"
"$ADB" shell "su -c 'cat /sdcard/Android/data/com.orech.seller/files/sales.csv 2>&1'"
```

## 31. PowerShell: Rebuild seller APK and copy to zamir
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :seller:assembleDebug --console=plain 2>&1 | Select-String -Pattern '^e: |FAILED|BUILD'
Copy-Item C:\orech_build\seller\build\outputs\apk\debug\seller-debug.apk "C:\פרוייקטים קלוד\zamir\seller-debug.apk" -Force
(Get-Item "C:\פרוייקטים קלוד\zamir\seller-debug.apk").LastWriteTime
```

## 32. PowerShell: Rebuild seller APK and copy to zamir
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :seller:assembleDebug --console=plain 2>&1 | Select-String -Pattern '^e: |FAILED|BUILD'
Copy-Item C:\orech_build\seller\build\outputs\apk\debug\seller-debug.apk "C:\פרוייקטים קלוד\zamir\seller-debug.apk" -Force
(Get-Item "C:\פרוייקטים קלוד\zamir\seller-debug.apk").LastWriteTime
```

## 33. Bash: Reinstall, retest device id save, inspect results
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "su -c 'touch /data/system/orech_allow_install'"
"$ADB" install -r "C:/פרוייקטים קלוד/zamir/seller-debug.apk" 2>&1 | tail -1
"$ADB" shell "su -c 'rm -f /data/system/orech_allow_install /mnt/vendor/nvdata/orech_id'"
"$ADB" shell am start -n com.orech.seller/.SellerActivity >/dev/null; sleep 4
"$ADB" shell input tap 210 258; sleep 2
"$ADB" shell input text T123; sleep 1
"$ADB" shell input tap 88 543; sleep 3
"$ADB" exec-out screencap -p > "$SP/seller5.png"
echo "--- nvdata:"; "$ADB" shell "su -c 'cat /mnt/vendor/nvdata/orech_id'"
echo "--- prop:"; "$ADB" shell getprop persist.sys.orech_id
echo "--- csv:"; "$ADB" shell "su -c 'cat /sdcard/Android/data/com.orech.seller/files/sales.csv'"
```

## 34. Bash: Verify serial in Settings, integrity, crashes
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -a android.settings.DEVICE_INFO_SETTINGS >/dev/null; sleep 3
"$ADB" shell "uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; cat /sdcard/ui.xml" | grep -o 'text="[^"]*"' | grep -iE 'T123|סידורי|serial|Zamir' | head
"$ADB" exec-out screencap -p > "$SP/about.png"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech/ | grep noenforce'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 35. Bash: Update handoff and changelog with results
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
old='(א) אפליקציית המוכר על המכשיר:'
assert old in s
s=s.replace(old,'(א) **בוצע ונבדק** (מספר בדיקה `T123` נשאר על המכשיר ובקובץ `sales.csv`; אפשר לשנות מהאפליקציה; שעון המכשיר שגוי ולכן התאריך ב-CSV שגוי): נמצא ותוקן באג, `$id` חסר בפקודות הכתיבה, כך שהמספר לא נכתב מעולם (שוחזר על המכשיר לפני התיקון: `orech_id` ריק, "השמירה נכשלה"). אחרי התיקון המספר ב-`/mnt/vendor/nvdata/orech_id`, ב-`persist.sys.orech_id`, ב-`sales.csv` ובהגדרות. עוד תוקן ב-SellerActivity: הבקרה מופעלת ואפליקציית המוכר מסירה את עצמה רק אם `approve` הצליח ו-`check` החזיר ריק (קודם הופעלה גם בכשל, ואז כיבוי בעלייה הבאה). **תיקון זה לא נבדק על מכשיר** ושלב "החל" לא הורץ. מקור: `seller-debug.apk` בתיקייה. תיאור המשימה המקורי: אפליקציית המוכר על המכשיר:',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read()
i=c.rindex('</div>')
c=c[:i].rstrip('\n')+'\n- SellerActivity: תוקן `$id` חסר בכתיבת מספר המכשיר (נבדק על המכשיר: nvdata, prop, sales.csv, הגדרות). בנוסף: הפעלת הבקרה והסרה עצמית רק אחרי `approve` מוצלח ו-`check` ריק (לא נבדק על מכשיר). `seller-debug.apk` נבנה מחדש.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
EOF
grep -c 'T123' HANDOFF_M393.md CHANGELOG_M393.md
```

## 36. Bash: Inspect vguard state and scan logic
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'ls /data/system/orech_vguard/; echo ---; wc -l /data/system/orech_vguard/*; echo ---; tail -8 /data/system/orech_vguard/vguard.log; echo ---; grep -n \"data/data\|blacklist\|libffmpeg\|\\.so\" /vendor/bin/orech_vguard.sh 2>/dev/null | head -40'"
"$ADB" shell "su -c 'ls /vendor/bin/ | grep -i orech; ls /system/bin | grep -i orech'"
```

## 37. Bash: Grep vguard script for blacklist logic
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'F=\$(ls /vendor/bin/orech_vguard.sh /system/bin/orech_vguard.sh 2>/dev/null | head -1); echo \$F; wc -l \$F; grep -n -iE \"data/data|blacklist|libffmpeg|chmod|force-stop|log \" \$F | head -50'"
```

## 38. Bash: Read blacklist function and /data/data scan
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'sed -n 36,60p /vendor/bin/orech_vguard.sh; echo ....; sed -n 96,112p /vendor/bin/orech_vguard.sh; echo ....; cat /data/system/orech_vguard/allowlist.txt'"
```

## 39. Bash: Plant dummy decoder lib and wait for vguard detection
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'mkdir -p /data/data/com.orech.seller/files && echo dummy > /data/data/com.orech.seller/files/libffmpeg_test.so && ls -l /data/data/com.orech.seller/files/'"
for i in 1 2 3 4 5 6 7 8; do
  sleep 10
  R=$("$ADB" shell "su -c 'grep -c com.orech.seller /data/system/orech_vguard/blacklist.txt'" | tr -d '\r')
  [ "$R" != "0" ] && break
done
echo "waited ~$((i*10))s, blacklist hits=$R"
"$ADB" shell "su -c 'cat /data/system/orech_vguard/blacklist.txt; tail -3 /data/system/orech_vguard/vguard.log; ls -l /data/data/com.orech.seller/files/libffmpeg_test.so; pm list packages -d | grep seller'"
```

## 40. Bash: Check /data/data symlink and scan loop
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'ls -ld /data/data /data/user/0; readlink /data/data; echo ---; find /data/data -maxdepth 5 -type f -name libffmpeg_test.so 2>&1 | head -3; echo ---find-slash; find /data/data/ -maxdepth 5 -type f -name libffmpeg_test.so 2>&1 | head -3; echo ---; sed -n 60,96p /vendor/bin/orech_vguard.sh; echo ....; sed -n 112,136p /vendor/bin/orech_vguard.sh'"
```

## 41. Bash: Keep polling for vguard detection
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 14); do
  sleep 10
  R=$("$ADB" shell "su -c 'grep -c com.orech.seller /data/system/orech_vguard/blacklist.txt'" | tr -d '\r')
  [ "$R" != "0" ] && break
done
echo "extra wait ~$((i*10))s, blacklist hits=$R"
"$ADB" shell "su -c 'cat /data/system/orech_vguard/blacklist.txt; tail -3 /data/system/orech_vguard/vguard.log; ls -l /data/data/com.orech.seller/files/libffmpeg_test.so; pm list packages -d | grep seller'"
```

## 42. Bash: Clean up blacklist test and re-enable app
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'grep -vx com.orech.seller /data/system/orech_vguard/blacklist.txt > /data/local/tmp/bl.new; cat /data/local/tmp/bl.new > /data/system/orech_vguard/blacklist.txt; rm -f /data/local/tmp/bl.new /data/data/com.orech.seller/files/libffmpeg_test.so; pm enable com.orech.seller; echo BL_LINES=\$(wc -l < /data/system/orech_vguard/blacklist.txt); pm list packages -d | grep -c seller; ls /data/data/com.orech.seller/files/'"
sleep 3
"$ADB" shell am start -n com.orech.seller/.SellerActivity | tail -1
```

## 43. Bash: Find where Kosher Tech appears in device info
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell getprop | grep -iE 'kosher|ro.build.display.id|ro.build.id\]|ro.product.model|ro.build.version.incremental|ro.vendor.build.fingerprint|ro.build.description' 
"$ADB" shell am start -a android.settings.DEVICE_INFO_SETTINGS >/dev/null; sleep 2
"$ADB" shell input swipe 200 800 200 200 300; sleep 1
"$ADB" shell input swipe 200 800 200 200 300; sleep 1
"$ADB" exec-out screencap -p > "$SP/about2.png"
ls -l /c/orech_build/tools 2>/dev/null | head -20
```

## 44. Bash: Inspect build.prop size and lines
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"; mkdir -p "$D"
"$ADB" shell "su -c 'ls -l /system/build.prop; wc -c /system/build.prop; sha256sum /system/build.prop; df /system | tail -1'"
"$ADB" shell "su -c 'cat /system/build.prop'" > "$D/build.prop.orig"
wc -lc "$D/build.prop.orig"; grep -n 'display.id' "$D/build.prop.orig"; grep -c '^#' "$D/build.prop.orig"; grep -c '^$' "$D/build.prop.orig"
file "$D/build.prop.orig"; head -c 300 "$D/build.prop.orig"
```

## 45. Bash: Build and validate new build.prop locally
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"
"$ADB" exec-out "su -c 'cat /system/build.prop'" > "$D/build.prop.orig"
python - <<'EOF'
import hashlib
D='C:/orech_build/t_bp/'
b=open(D+'build.prop.orig','rb').read()
print(len(b), hashlib.sha256(b).hexdigest())
assert len(b)==8103 and hashlib.sha256(b).hexdigest()=='5ce02a1d8830e8eeeee2f1414cf3ba9b859d59542efbee6a9ddeedc9e7c28005'
assert b'\r' not in b and b.endswith(b'\n')
lines=b.decode('ascii').split('\n')[:-1]
val='כושר טק - כל הזכויות שמורות | <email hidden>'
newline='ro.build.display.id='+val
out=[];need=len(newline.encode())-len('ro.build.display.id=Kosher Tech')
print('growth needed',need)
saved=0
for l in lines:
    if l.startswith('ro.build.display.id='):
        out.append(newline); continue
    out.append(l)
# drop comment lines (keep first line only) until we've saved enough
res=[];dropped=0
for l in out:
    if l.startswith('#') and dropped<need+40 and l!=out[0]:
        dropped+=len(l.encode())+1; continue
    res.append(l)
data=('\n'.join(res)+'\n').encode('utf-8')
print('new size',len(data),'dropped',dropped)
assert len(data)<=8103
# validate: every non-comment line is key=value, property keys preserved
def props(ls): return {l.split('=',1)[0]:l.split('=',1)[1] for l in ls if l and not l.startswith('#')}
po,pn=props(lines),props(res)
assert set(po)==set(pn)
diff=[k for k in po if po[k]!=pn[k]]
print('changed props:',diff)
assert diff==['ro.build.display.id']
data.decode('utf-8')
open(D+'build.prop.new','wb').write(data)
print('OK')
EOF
```

## 46. Bash: Pull exact build.prop and build validated new one
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"
"$ADB" shell "su -c 'cp /system/build.prop /data/local/tmp/bp.orig; chmod 644 /data/local/tmp/bp.orig'"
"$ADB" pull /data/local/tmp/bp.orig "$D/build.prop.orig" | tail -1
python - <<'EOF'
import hashlib
D='C:/orech_build/t_bp/'
b=open(D+'build.prop.orig','rb').read()
print(len(b), hashlib.sha256(b).hexdigest())
assert len(b)==8103 and hashlib.sha256(b).hexdigest()=='5ce02a1d8830e8eeeee2f1414cf3ba9b859d59542efbee6a9ddeedc9e7c28005'
assert b'\r' not in b and b.endswith(b'\n')
lines=b.decode('ascii').split('\n')[:-1]
val='כושר טק - כל הזכויות שמורות | <email hidden>'
newline='ro.build.display.id='+val
need=len(newline.encode())-len('ro.build.display.id=Kosher Tech')
print('growth needed',need)
out=[newline if l.startswith('ro.build.display.id=') else l for l in lines]
res=[];dropped=0
for idx,l in enumerate(out):
    if l.startswith('#') and idx>0 and dropped<need+40:
        dropped+=len(l.encode())+1; continue
    res.append(l)
data=('\n'.join(res)+'\n').encode('utf-8')
print('new size',len(data),'dropped',dropped)
assert len(data)<=8103
def props(ls): return {l.split('=',1)[0]:l.split('=',1)[1] for l in ls if l and not l.startswith('#')}
po,pn=props(lines),props(res)
assert set(po)==set(pn)
diff=[k for k in po if po[k]!=pn[k]]
print('changed props:',diff)
assert diff==['ro.build.display.id']
open(D+'build.prop.new','wb').write(data)
print('OK')
EOF
```

## 47. Bash: Pull exact build.prop and build validated new one
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"
"$ADB" pull /data/local/tmp/bp.orig "$D/build.prop.orig" | tail -1
python - <<'EOF'
import hashlib
D='C:/orech_build/t_bp/'
b=open(D+'build.prop.orig','rb').read()
print(len(b), hashlib.sha256(b).hexdigest())
assert len(b)==8103 and hashlib.sha256(b).hexdigest()=='5ce02a1d8830e8eeeee2f1414cf3ba9b859d59542efbee6a9ddeedc9e7c28005'
assert b'\r' not in b and b.endswith(b'\n')
lines=b.decode('ascii').split('\n')[:-1]
val='כושר טק - כל הזכויות שמורות | <email hidden>'
newline='ro.build.display.id='+val
need=len(newline.encode())-len('ro.build.display.id=Kosher Tech')
print('growth needed',need)
out=[newline if l.startswith('ro.build.display.id=') else l for l in lines]
res=[];dropped=0
for idx,l in enumerate(out):
    if l.startswith('#') and idx>0 and dropped<need+40:
        dropped+=len(l.encode())+1; continue
    res.append(l)
data=('\n'.join(res)+'\n').encode('utf-8')
print('new size',len(data),'dropped',dropped)
assert len(data)<=8103
def props(ls): return {l.split('=',1)[0]:l.split('=',1)[1] for l in ls if l and not l.startswith('#')}
po,pn=props(lines),props(res)
assert set(po)==set(pn)
diff=[k for k in po if po[k]!=pn[k]]
print('changed props:',diff)
assert diff==['ro.build.display.id']
open(D+'build.prop.new','wb').write(data)
print('OK')
EOF
```

## 48. Bash: Write new build.prop with dd and verify sha256
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"
SHA=$(sha256sum "$D/build.prop.new" | cut -d' ' -f1); SZ=$(stat -c %s "$D/build.prop.new"); echo "$SHA $SZ"
printf '%s\n' 'set -e' \
 'mount -o remount,rw / 2>&1 || true' \
 'cp /system/build.prop /data/local/tmp/bp.backup.device' \
 "[ \"\$(wc -c < /data/local/tmp/bp.new)\" = \"$SZ\" ] || { echo SIZE_MISMATCH_SRC; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/bp.new\" | sha256sum -c -" \
 'dd if=/data/local/tmp/bp.new of=/system/build.prop conv=notrunc 2>&1' \
 "truncate -s $SZ /system/build.prop" \
 'sync' \
 "echo \"$SHA  /system/build.prop\" | sha256sum -c -" \
 'ls -l /system/build.prop' > "$D/write_bp.sh"
"$ADB" push "$D/build.prop.new" /data/local/tmp/bp.new | tail -1
"$ADB" push "$D/write_bp.sh" /data/local/tmp/write_bp.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/write_bp.sh'"
```

## 49. Bash: Approve integrity, reboot, verify boot and prop
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve; echo APPROVE_EXIT=\$?; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech/ | grep noenforce; rm -f /data/local/tmp/bp.new /data/local/tmp/bp.orig /data/local/tmp/write_bp.sh'"
"$ADB" reboot
sleep 45
for i in $(seq 1 30); do
  B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5
done
echo "boot_completed=$B after ~$((45+i*5))s"
"$ADB" shell getprop ro.build.display.id
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech/ | grep noenforce'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell getprop init.svc.orech_vguard
```

## 50. Bash: Open About phone and screenshot after reboot
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -a android.settings.DEVICE_INFO_SETTINGS | tail -1; sleep 3
"$ADB" shell input swipe 200 800 200 200 300; sleep 1; "$ADB" shell input swipe 200 800 200 200 300; sleep 1
"$ADB" exec-out screencap -p > "$SP/about3.png"
```

## 51. Bash: Update docs and save original build.prop
```
cd "/c/פרוייקטים קלוד/zamir" && cp /c/orech_build/t_bp/build.prop.orig build.prop.orig_before_branding && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='Build `Kosher Tech`'
assert a in s
s=s.replace(a,'Build (`ro.build.display.id`) = `כושר טק - כל הזכויות שמורות | <email hidden>` (היה `Kosher Tech`; הקובץ המקורי ב-`zamir\\build.prop.orig_before_branding`, sha256 `5ce02a1d...`, 8103 בייט; בגרסה החדשה 8040 בייט, הוסרו שורות הערה כדי לא לחרוג מהגודל המקורי)',1)
b='(ב) הרשימה השחורה:'
assert b in s
s=s.replace(b,'(ב) **נבדק בצורה סינתטית** (קובץ דמה `libffmpeg_test.so` ב-`/data/data` של אפליקציית המוכר, לא אפליקציה אמיתית): הזיהוי עבד, `chmod 000`, כניסה ל-`blacklist.txt`, שורה ב-`vguard.log`, השבתה. הזיהוי לקח כ-95 שניות ולא "תוך דקה" (סריקת `/data/data` רצה רק ב-`apply_lists`, אחת ל-30 מחזורים). אחרי הבדיקה: להוציא מהרשימה לפני `pm enable`, אחרת השירות משבית שוב. הנוסח המקורי של המשימה: הרשימה השחורה:',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read()
i=c.rindex('</div>')
c=c[:i].rstrip('\n')+'\n- מסך "מידע על הטלפון": ערך "מספר Build" שונה ל-`כושר טק - כל הזכויות שמורות | <email hidden>` דרך `/system/build.prop` (dd + truncate, אימות sha256, `approve`, אתחול; עלייה תקינה, 0 קריסות, `check` ריק). הרשימה השחורה נבדקה סינתטית ונוקתה.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
EOF
grep -c 'כושר טק - כל' HANDOFF_M393.md CHANGELOG_M393.md
```

## 52. Bash: Find APK with INSTALL_PACKAGES permission
```
export MSYS_NO_PATHCONV=1
AAPT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/*/aapt2.exe | tail -1); echo $AAPT
for a in "C:/פרוייקטים קלוד/zamir/seller-debug.apk" /c/orech_build/app/build/outputs/apk/debug/app-debug.apk; do
  echo "== $a"; "$AAPT" dump permissions "$a" 2>&1 | grep -iE 'INSTALL_PACKAGES|package:' | head -3
done
ls /c/orech_build/deploy | head -20
```

## 53. Bash: Build signed test APK with video mime type
```
export MSYS_NO_PATHCONV=1
BT=$(ls -d /c/Users/user1/AppData/Local/Android/Sdk/build-tools/*/ | tail -1); AAPT="${BT}aapt2.exe"; AS="${BT}apksigner.bat"
JAR=$(ls /c/Users/user1/AppData/Local/Android/Sdk/platforms/android-34/android.jar)
W=/c/orech_build/t_blocktest; mkdir -p $W && cd $W
cat > AndroidManifest.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.orech.blocktest">
  <application android:label="blocktest">
    <activity android:name=".A" android:exported="true">
      <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="video/*"/>
      </intent-filter>
    </activity>
  </application>
</manifest>
EOF
"$AAPT" link -o blocktest_unsigned.apk --manifest AndroidManifest.xml -I "$JAR" --min-sdk-version 26 --target-sdk-version 33 2>&1 | tail -2
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"; export PATH="$JAVA_HOME/bin:$PATH"
ls ~/.android/debug.keystore
"$AS" sign --ks ~/.android/debug.keystore --ks-pass pass:android --out blocktest.apk blocktest_unsigned.apk 2>&1 | tail -2
ls -l blocktest.apk
"$AAPT" dump xmltree --file AndroidManifest.xml blocktest.apk | grep -c 'video/'
```

## 54. Bash: List installed Android platforms
```
export MSYS_NO_PATHCONV=1
ls /c/Users/user1/AppData/Local/Android/Sdk/platforms/
```

## 55. Bash: Find android.jar location
```
ls /c/Users/user1/AppData/Local/Android/Sdk/platforms/android-34 /c/Users/user1/AppData/Local/Android/Sdk/platforms/android-36 | head -20
```

## 56. Bash: Build signed test APK using Windows paths
```
unset MSYS_NO_PATHCONV
SDK="C:/Users/user1/AppData/Local/Android/Sdk"
BTV=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="$SDK/build-tools/$BTV/aapt2.exe"; AS="$SDK/build-tools/$BTV/apksigner.bat"
JAR="$SDK/platforms/android-34/android.jar"
cd /c/orech_build/t_blocktest
"$AAPT" link -o blocktest_unsigned.apk --manifest AndroidManifest.xml -I "$JAR" --min-sdk-version 26 --target-sdk-version 33 2>&1 | tail -2
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$AS" sign --ks "C:/Users/user1/.android/debug.keystore" --ks-pass pass:android --out blocktest.apk blocktest_unsigned.apk 2>&1 | tail -2
ls -l blocktest.apk
"$AAPT" dump xmltree --file AndroidManifest.xml blocktest.apk | grep -c 'video/'
```

## 57. Bash: Open test APK in package installer UI
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "su -c 'ls /data/system/orech_allow_install 2>&1'"
"$ADB" push C:/orech_build/t_blocktest/blocktest.apk /sdcard/Download/blocktest.apk | tail -1
"$ADB" shell am start -a android.intent.action.VIEW -d file:///sdcard/Download/blocktest.apk -t application/vnd.android.package-archive 2>&1 | tail -2
sleep 4
"$ADB" exec-out screencap -p > "$SP/inst1.png"
"$ADB" shell pm list packages | grep -c blocktest
```

## 58. Bash: Launch installer via INSTALL_PACKAGE action
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell cmd package query-activities -a android.intent.action.INSTALL_PACKAGE 2>&1 | grep -E 'name=|packageName' | head -6
"$ADB" shell am start -a android.intent.action.INSTALL_PACKAGE -d file:///sdcard/Download/blocktest.apk 2>&1 | tail -2
sleep 4
"$ADB" exec-out screencap -p > "$SP/inst2.png"
"$ADB" shell pm list packages | grep -c blocktest
```

## 59. Bash: Inspect package installer packages and state
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell pm list packages | grep -iE 'installer'
"$ADB" shell "dumpsys package com.google.android.packageinstaller 2>/dev/null | grep -iE 'Activity Resolver|InstallStart|InstallInstalling|enabled=|android.intent.action.(VIEW|INSTALL)' | head -15"
"$ADB" shell "pm list packages -d | grep -iE 'installer'"
```

## 60. Bash: Start installer activity explicitly with APK
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -n com.google.android.packageinstaller/com.android.packageinstaller.InstallStart -a android.intent.action.VIEW -d file:///sdcard/Download/blocktest.apk 2>&1 | tail -3
sleep 4
"$ADB" exec-out screencap -p > "$SP/inst3.png"
"$ADB" shell pm list packages | grep -c blocktest
"$ADB" shell "logcat -d -t 60 | grep -iE 'orech|installer|InstallStart' | tail -8"
```

## 61. Bash: Check CE storage unlock state and focused app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell getprop sys.user.0.ce_available
"$ADB" shell "dumpsys user | grep -iE 'State:|RUNNING' | head -3"
"$ADB" shell "dumpsys window | grep -E 'mCurrentFocus|mFocusedApp' | head -2"
```

## 62. Bash: Inspect installer component state
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "pm dump com.google.android.packageinstaller | grep -iE 'disabledComponents|enabledComponents|versionName|codePath|flags=|InstallStart' | head -12"
"$ADB" shell "cmd package resolve-activity --brief -a android.intent.action.INSTALL_PACKAGE -d content://x/y.apk -t application/vnd.android.package-archive 2>&1 | tail -3"
"$ADB" shell "pm list packages -d | grep -c installer"
```

## 63. Bash: Find MediaStore id for the test APK
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "cmd media_session >/dev/null 2>&1; am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Download/blocktest.apk >/dev/null 2>&1"; sleep 3
"$ADB" shell "content query --uri content://media/external/file --projection _id:_data --where \"_data='/storage/emulated/0/Download/blocktest.apk'\"" 2>&1 | head -3
"$ADB" shell "content query --uri content://media/external/downloads --projection _id:_data --where \"_data='/storage/emulated/0/Download/blocktest.apk'\"" 2>&1 | head -3
```

## 64. Bash: Open test APK in installer via content URI
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/1000011095 -t application/vnd.android.package-archive --grant-read-uri-permission 2>&1 | tail -2
sleep 5
"$ADB" exec-out screencap -p > "$SP/inst4.png"
"$ADB" shell pm list packages | grep -c blocktest
```

## 65. Bash: Check logcat for installer launch result
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d -t 400 | grep -iE 'InstallStart|packageinstaller|orech|blocktest|ActivityTaskManager: START' | tail -12"
"$ADB" shell "dumpsys window | grep -E 'mCurrentFocus' | head -1"
```

## 66. Bash: Launch installer with wait and capture focus
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" logcat -c
"$ADB" shell am start -W -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/1000011095 -t application/vnd.android.package-archive --grant-read-uri-permission 2>&1 | tail -6
for i in 1 2 3; do "$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"; sleep 1; done
"$ADB" exec-out screencap -p > "$SP/inst5.png"
"$ADB" shell "logcat -d | grep -iE 'InstallStart|packageinstaller|orech' | tail -10"
```

## 67. Bash: Wait for device to return to adb
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 24); do
  S=$("$ADB" get-state 2>/dev/null | tr -d '\r'); [ "$S" = "device" ] && break; sleep 5
done
echo "state=$S after ~$((i*5))s"
"$ADB" devices
"$ADB" shell "getprop sys.boot_completed; uptime; getprop init.svc.orech_vguard" 2>&1 | head -4
```

## 68. Bash: Diagnose unexpected reboot cause
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 30); do B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5; done
echo boot_completed=$B
"$ADB" shell "getprop sys.boot.reason; getprop ro.boot.bootreason; getprop persist.sys.boot.reason; getprop persist.sys.boot.reason.history | head -3"
echo "--- integrity.log tail"; "$ADB" shell "su -c 'tail -6 /mnt/vendor/protect_s/orech/integrity.log; ls /mnt/vendor/protect_s/orech | grep noenforce; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
echo "--- crash buffer"; "$ADB" shell "logcat -b crash -d | tail -15"
echo "--- vguard"; "$ADB" shell "su -c 'tail -3 /data/system/orech_vguard/vguard.log'"
```

## 69. Bash: Check who rebooted: terminal and seller app state
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell pm list packages | grep -E 'seller|blocktest'
"$ADB" shell "su -c 'ls -l /mnt/vendor/protect_s/orech/; ls /data/system/orech_allow_install 2>&1'"
"$ADB" shell "dumpsys package com.orech.seller | grep -E 'lastUpdateTime|firstInstallTime'"
```

## 70. Bash: Clean up test APK and document findings
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /sdcard/Download/blocktest.apk; am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Download/blocktest.apk >/dev/null 2>&1; ls /sdcard/Download/blocktest.apk 2>&1; pm list packages | grep -c blocktest"
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='(ג) מסך חסימת המתקין בעיצוב החדש.'
assert a in s
s=s.replace(a,'(ג) מסך חסימת המתקין בעיצוב החדש: **לא נבדק**. נבנה APK בדיקה חתום עם `video/*` במניפסט (`C:\\orech_build\\t_blocktest\\blocktest.apk`, בנוי עם aapt2+apksigner ומפתח debug). פתיחה במתקין: `file://` לא נפתר; צריך `content://`: להעתיק ל-`/sdcard/Download`, לאנדקס (`am broadcast MEDIA_SCANNER_SCAN_FILE`), לשלוף `_id` ב-`content query --uri content://media/external/file`, ואז `am start -W -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/<id> -t application/vnd.android.package-archive --grant-read-uri-permission` (חזר `Status: ok`, `InstallStart`, אך המסך לא נצפה). **בזמן הבדיקה סשנים אחרים עבדו על אותו מכשיר ואתחלו אותו** (`reboot,shell`), לכן לבדוק רק כשאין סשן אחר פעיל על המכשיר. ',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
grep -c 't_blocktest' HANDOFF_M393.md
```

## 71. Bash: Check handoff path text for escape damage
```
cd "/c/פרוייקטים קלוד/zamir" && grep -c 'לא נבדק\*\*. נבנה APK' HANDOFF_M393.md; grep -o 'C:.\{0,3\}orech_build.\{0,25\}blocktest.apk' HANDOFF_M393.md | cat -A | head -2
```

## 72. Bash: Repair damaged path in handoff
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
import re
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
i=s.index('נבנה APK בדיקה חתום')
print(repr(s[i:i+130]))
# repair: replace whatever sits between the opening paren and 'blocktest.apk'
s2=re.sub(r'\(`[^`\n]*blocktest\.apk`','(`C:/orech_build/t_blocktest/blocktest.apk`',s,count=1)
print(s2!=s)
open(p,'w',encoding='utf-8',newline='\n').write(s2)
EOF
grep -c 'C:/orech_build/t_blocktest/blocktest.apk' HANDOFF_M393.md
```

## 73. Bash: Fix mangled path and check for control chars
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
bad='C:\\\\orech_build\t_blocktest\x08locktest.apk'
print(bad in s)
s=s.replace(bad,'C:/orech_build/t_blocktest/blocktest.apk')
assert '\x08' not in s
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
grep -c 'C:/orech_build/t_blocktest/blocktest.apk' HANDOFF_M393.md; python -c "
s=open('HANDOFF_M393.md',encoding='utf-8').read(); print('ctrl chars:', sum(1 for c in s if ord(c)<32 and c not in '\n\t'))"
```

## 74. Bash: Locate control characters in handoff
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
s=open('HANDOFF_M393.md',encoding='utf-8').read()
for i,c in enumerate(s):
    if ord(c)<32 and c not in '\n':
        print(i,repr(c),repr(s[max(0,i-35):i+25]))
EOF
```

## 75. Bash: Repair both mangled paths and verify
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
s=s.replace('zamir\x08uild.prop.orig_before_branding','zamir/build.prop.orig_before_branding')
s=s.replace('C:\\\\orech_build\t_blocktest\x08locktest.apk','C:/orech_build/t_blocktest/blocktest.apk')
bad=[(i,c) for i,c in enumerate(s) if ord(c)<32 and c not in '\n']
print('remaining ctrl chars:',bad)
assert not bad
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
grep -o 'zamir/build.prop.orig_before_branding' HANDOFF_M393.md | head -1; grep -o 'C:/orech_build/t_blocktest/blocktest.apk' HANDOFF_M393.md | head -1
python -c "
s=open('CHANGELOG_M393.md',encoding='utf-8').read(); print('changelog ctrl chars:', sum(1 for c in s if ord(c)<32 and c!='\n'))"
```

## 76. Bash: Read-only device and integrity status
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices -l
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime; getprop ro.boot.bootreason; getprop ro.build.display.id; logcat -d | grep -c 'FATAL EXCEPTION'" 2>&1 | head -6
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; tail -4 /mnt/vendor/protect_s/orech/integrity.log'" 2>&1
"$ADB" shell pm list packages | grep -iE 'orech|gps|mock'
```

## 77. Bash: Read-only device and integrity status
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices -l
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime; getprop ro.boot.bootreason; getprop ro.build.display.id; logcat -d | grep -c 'FATAL EXCEPTION'" 2>&1 | head -6
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; tail -4 /mnt/vendor/protect_s/orech/integrity.log'" 2>&1
"$ADB" shell pm list packages | grep -iE 'orech|gps|mock'
```

## 78. PowerShell: Check USB device presence and fastboot
```
Get-PnpDevice -PresentOnly -ErrorAction SilentlyContinue | Where-Object { $_.FriendlyName -match 'ADB|Android|MTK|MediaTek|Preloader|VCOM|Fastboot|Bootloader' -or $_.InstanceId -match 'VID_0E8D|VID_18D1' } | Select-Object Status,FriendlyName,InstanceId | Format-Table -AutoSize | Out-String -Width 200
& "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices
& "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\fastboot.exe" devices
```

## 79. PowerShell: Monitor USB enumeration and restart adb server
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
1..12 | ForEach-Object { $p = Get-PnpDevice -PresentOnly -ErrorAction SilentlyContinue | Where-Object { $_.InstanceId -match 'VID_0E8D&PID_201C' }; "{0:HH:mm:ss} present={1}" -f (Get-Date), [bool]$p; Start-Sleep 3 }
& $adb kill-server; Start-Sleep 2; & $adb start-server 2>&1 | Select-Object -Last 1; Start-Sleep 4; & $adb devices -l
Get-WinEvent -FilterHashtable @{LogName='System'; StartTime=(Get-Date).AddMinutes(-15)} -ErrorAction SilentlyContinue | Where-Object { $_.ProviderName -match 'Kernel-PnP|usbhub|USB' } | Select-Object -First 8 TimeCreated,Id,Message | Format-List | Out-String -Width 160
```

## 80. PowerShell: Re-check adb devices and background output
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
& $adb devices -l
Get-Content "C:\Users\user1\AppData\Local\Temp\claude\C----------------\b4a1b9f4-acb5-4092-933f-897217e378d3\tasks\bspaqkf5w.output" -Tail 12
```

## 81. PowerShell: List processes that may hold the USB ADB interface
```
Get-Process | Where-Object { $_.ProcessName -match 'adb|scrcpy|flash|SP_|MTK|mtk|qemu|HiSuite|android|studio64|python|node' } | Select-Object Id,ProcessName,StartTime,Path | Format-Table -AutoSize | Out-String -Width 220
Get-NetTCPConnection -LocalPort 5037 -ErrorAction SilentlyContinue | Select-Object LocalAddress,State,OwningProcess | Format-Table | Out-String
```

## 82. PowerShell: List recently modified project files
```
$cut=(Get-Date).AddHours(-4)
Get-ChildItem C:\orech_build -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.LastWriteTime -gt $cut -and $_.FullName -notmatch '\\(build|\.gradle|\.git|t_bp|t_blocktest)\\' -and $_.Extension -notin '.png','.class','.dex' } | Sort-Object LastWriteTime | Select-Object LastWriteTime,Length,@{n='Path';e={$_.FullName.Replace('C:\orech_build\','')}} | Format-Table -AutoSize | Out-String -Width 200
Get-ChildItem "C:\פרוייקטים קלוד\zamir" -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.LastWriteTime -gt $cut } | Select-Object LastWriteTime,Length,Name | Format-Table -AutoSize | Out-String -Width 200
```

## 83. Bash: Reconnect adb and scan mockloc code for risky calls
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" reconnect 2>&1 | tail -1; sleep 4; "$ADB" devices
cd /c/orech_build/mockloc/src/com/orech/mockloc && ls; echo ---; cat BootReceiver.java UsbAttachActivity.java
echo --- risky; grep -nE 'Runtime|exec\(|reboot|su |setprop|Settings\.(Secure|Global|System)|killProcess|System\.exit|PowerManager|shutdown|disable|setApplicationEnabled|Thread\.sleep|while *\(true\)' *.java | head -30
```

## 84. PowerShell: Poll adb devices only, no commands to device
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$r=""
for($i=0;$i -lt 12;$i++){ $r=(& $adb devices | Select-Object -Skip 1 | Where-Object {$_.Trim()}) -join "; "; if($r){break}; Start-Sleep 5 }
"after ~$($i*5)s devices=[$r]"
```

## 85. Bash: Read-only device health check after reconnect
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime; getprop ro.boot.bootreason; getprop sys.boot.reason; logcat -d | grep -c 'FATAL EXCEPTION'; getprop init.svc.orech_vguard; getprop ro.build.display.id"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; tail -3 /mnt/vendor/protect_s/orech/integrity.log; tail -3 /data/system/orech_vguard/vguard.log'"
"$ADB" shell pm list packages | grep -iE 'orech|gpsconn'
```

## 86. Bash: Compare deployed files with local, read-only
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
echo "device vguard:"; "$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh /product/app/OrechMockLoc/OrechMockLoc.apk; wc -l /vendor/bin/orech_vguard.sh'"
echo "local:"; sha256sum /c/orech_build/deploy/orech_vguard_v8.sh /c/orech_build/deploy/OrechMockLoc.apk; wc -l /c/orech_build/deploy/orech_vguard_v8.sh
"$ADB" shell "su -c 'tail -6 /data/system/orech_vguard/vguard.log; wc -l /data/system/orech_vguard/*.txt'"
"$ADB" shell "dumpsys package com.orech.mockloc | grep -E 'versionName|lastUpdateTime|codePath|granted=true' | head -12"
"$ADB" shell "settings get secure location_providers_allowed; dumpsys location 2>/dev/null | grep -iE 'mock|gps provider' | head -5"
```

## 87. Bash: Compare deployed files with local, read-only
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
echo "device vguard:"; "$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh /product/app/OrechMockLoc/OrechMockLoc.apk; wc -l /vendor/bin/orech_vguard.sh'"
echo "local:"; sha256sum /c/orech_build/deploy/orech_vguard_v8.sh /c/orech_build/deploy/OrechMockLoc.apk; wc -l /c/orech_build/deploy/orech_vguard_v8.sh
"$ADB" shell "su -c 'tail -6 /data/system/orech_vguard/vguard.log; wc -l /data/system/orech_vguard/*.txt'"
"$ADB" shell "dumpsys package com.orech.mockloc | grep -E 'versionName|lastUpdateTime|codePath|granted=true' | head -12"
"$ADB" shell "settings get secure location_providers_allowed; dumpsys location 2>/dev/null | grep -iE 'mock|gps provider' | head -5"
```

## 88. Bash: Launch installer on blocked test APK and capture screens
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "su -c 'ls /data/system/orech_allow_install 2>&1'"
"$ADB" push C:/orech_build/t_blocktest/blocktest.apk /sdcard/Download/blocktest.apk | tail -1
"$ADB" shell "am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Download/blocktest.apk >/dev/null 2>&1"; sleep 3
ID=$("$ADB" shell "content query --uri content://media/external/file --projection _id --where \"_data='/storage/emulated/0/Download/blocktest.apk'\"" | tr -d '\r' | sed -n 's/.*_id=\([0-9]*\).*/\1/p' | head -1); echo "id=$ID"
"$ADB" shell "input keyevent KEYCODE_WAKEUP"
"$ADB" logcat -c
"$ADB" shell am start -W -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/$ID -t application/vnd.android.package-archive --grant-read-uri-permission | tail -4
for n in 1 2 3; do sleep 2; "$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"; "$ADB" exec-out screencap -p > "$SP/blk$n.png"; done
"$ADB" shell pm list packages | grep -c blocktest
"$ADB" shell "logcat -d | grep -iE 'InstallStart|packageinstaller|orech|blocktest' | tail -10"
```

## 89. Bash: Inspect installer activity flow in logcat
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E 'ActivityTaskManager|ActivityManager' | grep -iE 'packageinstaller|InstallFailed|blocktest|Displayed' | tail -12"
echo ---; "$ADB" shell "logcat -d --pid=3185 | tail -25"
echo ---; "$ADB" shell "dumpsys activity activities | grep -iE 'packageinstaller' | head -8"
echo ---; "$ADB" shell "pm dump com.google.android.packageinstaller | grep -E 'Activity Resolver|^        [0-9a-f]+ com.google.android.packageinstaller/' | sed 's/ filter.*//' | sort -u | head -12"
```

## 90. Bash: Open Files app at Download folder
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -a android.provider.action.BROWSE -d "content://com.android.externalstorage.documents/document/primary%3ADownload" 2>&1 | tail -2
sleep 3
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/files1.png"
```

## 91. Bash: Try opening file manager via root URI
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "pm list packages | grep -iE 'documentsui|files|filemanager|mt.plus'"
"$ADB" shell am start -a android.intent.action.VIEW -d "content://com.android.externalstorage.documents/root/primary" -t vnd.android.document/root 2>&1 | tail -2
sleep 3
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/files2.png"
```

## 92. Bash: Choose Files by Google once and screenshot
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell input tap 265 732; sleep 1; "$ADB" shell input tap 178 886; sleep 4
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/files3.png"
```

## 93. Bash: Check device state after black screen
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" get-state 2>&1
sleep 3
"$ADB" shell "cat /proc/uptime; dumpsys window | grep -E 'mCurrentFocus|mScreenOn|mAwake' | head -3; dumpsys power | grep -E 'mWakefulness=' | head -1"
"$ADB" exec-out screencap -p > "$SP/files4.png"; ls -l "$SP/files4.png"
```

## 94. PowerShell: Wait for device to return, read-only
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$r=""
for($i=0;$i -lt 40;$i++){ $r=(& $adb devices | Select-Object -Skip 1 | Where-Object {$_.Trim()}) -join "; "; if($r -match 'device$'){break}; Start-Sleep 4 }
"after ~$($i*4)s devices=[$r]"
Get-PnpDevice -PresentOnly -ErrorAction SilentlyContinue | Where-Object { $_.InstanceId -match 'VID_0E8D' } | Select-Object Status,FriendlyName | Format-Table | Out-String
```

## 95. Bash: Diagnose reboot and Settings ANR, read-only
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime; getprop ro.boot.bootreason; getprop sys.boot.reason; getprop persist.sys.boot.reason.history | head -2"
echo "--- fatal/anr:"; "$ADB" shell "logcat -d | grep -E 'FATAL EXCEPTION|ANR in|am_anr|Process .* has died|system_server.*(crash|died)' | tail -8"
echo "--- crash buf:"; "$ADB" shell "logcat -b crash -d | tail -12"
echo "--- integrity:"; "$ADB" shell "su -c 'tail -3 /mnt/vendor/protect_s/orech/integrity.log; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce'"
echo "--- dropbox:"; "$ADB" shell "su -c 'ls -t /data/system/dropbox | head -5'"
```

## 96. Bash: Check screen and settings changed by taps
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1; settings get global adb_enabled; settings get global adb_wifi_enabled; settings get global development_settings_enabled; settings get global adb_allowed_connection_time; settings get global stay_on_while_plugged_in"
"$ADB" exec-out screencap -p > "$SP/now1.png"
"$ADB" shell "logcat -d | grep -iE 'ActivityTaskManager: START' | tail -6"
```

## 97. Bash: List installer patch files and dex edit tool
```
ls /c/orech_build/tools/dex_edit/patches_m393/ /c/orech_build/tools/dex_edit | head -40
```

## 98. Bash: List installer patch files and dex edit tool
```
ls /c/orech_build/tools/dex_edit/patches_m393/ /c/orech_build/tools/dex_edit | head -40
```

## 99. Bash: Screenshot current screen and look for dialog source
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/now2.png"
ls /c/orech_build/tools/dex_edit/installer_guard | head
grep -rn "showDialogInner\|DLG_PACKAGE_ERROR\|Parse_error\|dlg_text" /c/orech_build/tools/dex_edit/installer_guard /c/orech_build/tools/dex_edit/README.md 2>/dev/null | head -8
```

## 100. Bash: Pull installer APK and search Hebrew strings
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
W=/c/orech_build/t_inst; mkdir -p $W
"$ADB" shell "su -c 'ls -l /system/priv-app/GooglePackageInstaller/; cp /system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk /data/local/tmp/gpi.apk; chmod 644 /data/local/tmp/gpi.apk; sha256sum /data/local/tmp/gpi.apk'"
"$ADB" pull /data/local/tmp/gpi.apk C:/orech_build/t_inst/gpi.apk | tail -1
"$ADB" shell "rm -f /data/local/tmp/gpi.apk"
sha256sum $W/gpi.apk
BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe"
"$AAPT" dump strings C:/orech_build/t_inst/gpi.apk 2>&1 | grep -nE 'לא הותקנה|השתבש|נחסמה' | head
```

## 101. Bash: Map Hebrew install strings to resource names
```
BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe"
"$AAPT" dump resources C:/orech_build/t_inst/gpi.apk 2>&1 > /c/orech_build/t_inst/res.txt
python - <<'EOF'
import re
t=open('C:/orech_build/t_inst/res.txt',encoding='utf-8',errors='replace').read()
want=['האפליקציה לא הותקנה.','החבילה נחסמה להתקנה.','משהו השתבש','אירעה בעיה','לא ניתן להתקין']
blocks=re.split(r'\n(?=\s+resource 0x)',t)
for b in blocks:
    if 'string/' in b.split('\n')[0] and any(w in b for w in want):
        print(re.sub(r'\s+',' ',b)[:200])
EOF
```

## 102. Bash: Find resource names for relevant Hebrew strings
```
PYTHONIOENCODING=utf-8 python - <<'EOF'
import re
t=open('C:/orech_build/t_inst/res.txt',encoding='utf-8',errors='replace').read()
blocks=re.split(r'\n(?=\s+resource 0x)',t)
for b in blocks:
    head=b.split('\n')[0]
    if 'string/' not in head: continue
    m=re.search(r'\(iw\) "([^"]*)"|\(he\) "([^"]*)"',b)
    txt=(m.group(1) or m.group(2)) if m else ''
    en=re.search(r'\(\) "([^"]*)"',b)
    en=en.group(1) if en else ''
    if any(w in (txt or '') for w in ['לא הותקנה','נחסמה','השתבש','בעיה בניתוח']) or re.search(r'string/(Parse_error|install_failed|install_blocked|blocked)',head):
        print(head.strip().split()[1], '| EN:', en[:60], '| HE:', txt[:80])
EOF
```

## 103. Bash: Inspect existing editing tools and installer guard
```
cd /c/orech_build/tools && ls arsc_xml_edit overlays | head -20; sed -n 1,30p dex_edit/README.md; grep -n "installer_guard\|VideoGuard" dex_edit/README.md | head -5; grep -rn "showDialogInner\|const/4 v4\|DLG_" dex_edit/installer_guard/*.smali | head -5; cat dex_edit/installer_guard/VideoGuard.java | head -30
```

## 104. Bash: Read VideoGuard source and look for generic message
```
cd /c/orech_build/tools/dex_edit && sed -n 30,200p installer_guard/VideoGuard.java; echo ----; ls ../reference_smali | head; echo ----; cat classpath.txt | head -3; grep -n "משהו" /dev/null; BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1); "C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe" dump strings C:/orech_build/t_inst/gpi.apk 2>&1 | grep -n "משהו" | head
```

## 105. Bash: Read PM-level block patch and reference listener
```
cd /c/orech_build/tools/dex_edit && cat patches_m393/pm_preparePackageLI.txt; echo; echo ---- ; ls ../reference_smali; echo ----; head -40 ../reference_smali/installstart-blockedclicklistener.smali
```

## 106. Bash: Find which block path fired in user's attempt
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E 'Video-capable|INSTALL_FAILED|InstallFailed|InstallInstalling|InstallStaging|blocktest|STATUS_FAILURE|PackageInstaller' | tail -25"
echo ---; "$ADB" shell "logcat -d | grep -E 'ActivityTaskManager: START' | grep -iE 'packageinstaller|nbu.files|blocktest' | tail -8"
echo ---; "$ADB" shell "pm list packages | grep -c blocktest; ls -l /sdcard/Download/blocktest.apk"
```

## 107. Bash: Read the install session error from logcat
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E ' kjm ' | head -14"
"$ADB" shell "logcat -d -t 3000 | grep -iE 'Video-capable|PrepareFailure|verification|blocked on this' | head -8"
```

## 108. Bash: Check /data free space and biggest consumers
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "df -h /data /sdcard /storage/emulated 2>&1 | head -5"
"$ADB" shell "su -c 'du -sk /data/app /data/data /data/media/0 /data/system /data/dalvik-cache /data/local/tmp 2>/dev/null; ls -la /data/local/tmp | head -20'"
```

## 109. Bash: Verify block from logcat, read-only
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E 'ActivityTaskManager: START' | grep -iE 'packageinstaller' | tail -4"
"$ADB" shell "logcat -d | grep -iE 'Video-capable|VideoGuard|blocktest|orech' | tail -8"
"$ADB" shell "pm list packages | grep -c blocktest; su -c 'ls /data/system/orech_allow_install 2>&1; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 110. Bash: Remove test APK and record result in handoff
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /sdcard/Download/blocktest.apk; ls /sdcard/Download/blocktest.apk 2>&1"
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='(ג) מסך חסימת המתקין בעיצוב החדש: **לא נבדק**.'
assert a in s
s=s.replace(a,'(ג) מסך חסימת המתקין: **נבדק (המשתמש פתח את blocktest.apk מ-Files by Google וראה את נוסח החסימה שלנו; ביומן: InstallStart ואז PackageInstallerActivity, החבילה לא הותקנה, 0 קריסות)**. הנוסח הנוכחי: טוסט "האפליקציה נחסמה: היא כוללת תמיכה בוידיאו" ואחריו דיאלוג שגיאה של המתקין. נוסחים נפרדים לפי סיבה (וידיאו / יכולת התקנת אפליקציות) הוצעו ולא יושמו. הודעה כללית "האפליקציה לא הותקנה" יכולה להגיע גם מחוסר מקום (נצפה: `Failed to allocate ... only 152986 allocatable`), לא רק מהחסימה. פרטי הבדיקה הקודמת שלא הושלמה: ',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
bad=[c for c in s if ord(c)<32 and c!='\n']
print('ctrl chars:',len(bad))
EOF
```

## 111. Bash: Show git status excluding temp files
```
cd /c/orech_build && git status --short | grep -vE '\.(png)$|^\?\? r[0-9]+\.sh' | head -40; git branch --show-current; git diff --stat -- app/build.gradle.kts seller app/src/main | tail -5
```

## 112. Bash: Stage own files and commit without push
```
cd /c/orech_build && git add app/build.gradle.kts app/src/test seller/src/main/java/com/orech/seller/SellerActivity.kt && git status --short | grep -E '^[AM]' && git -c core.safecrlf=false commit -q -F - <<'EOF'
Seller: fix device id write, arm integrity only after verified approve; add ZipUtil tests

- SellerActivity: the device id was never written ($id missing from the nvdata and
  persist.sys commands); reproduced on a device before the fix, verified after.
- SellerActivity: enable the integrity control and self-remove only when approve
  succeeded and check returned empty; otherwise keep noenforce and stay installed.
- Add JVM unit tests for ZipUtil (compression method preserved, 4-byte alignment of
  stored entries, replacement with valid CRC, alignInPlace); JUnit 4.13.2.

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>
EOF
git log --oneline -2; git show --stat --format= HEAD | tail -5; git status --short | grep -E '^[AM]' | wc -l
```

## 113. Bash: Find the blocked Files Plus APK on the device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "ls -la /sdcard/Download | grep -iE 'apk|file|plus|סייר'" | head -20
"$ADB" shell "pm list packages | grep -iE 'filemanager|files|fileexplorer|explorer|plus'"
"$ADB" shell "logcat -d | grep -iE 'Video-capable|nstall' | grep -iE 'plus|explorer|file' | tail -5"
```

## 114. Bash: Find the blocked Files Plus APK on the device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "ls -la /sdcard/Download | grep -iE 'apk|file|plus|סייר'" | head -20
"$ADB" shell "pm list packages | grep -iE 'filemanager|files|fileexplorer|explorer|plus'"
"$ADB" shell "logcat -d | grep -iE 'Video-capable|nstall' | grep -iE 'plus|explorer|file' | tail -5"
```

## 115. PowerShell: Search PC downloads for file manager APKs
```
Get-ChildItem D:\Downloads,"$env:USERPROFILE\Downloads","C:\פרוייקטים קלוד" -Recurse -Include *.apk,*.apks,*.xapk,*.apkm -ErrorAction SilentlyContinue -Depth 3 | Where-Object { $_.Name -match 'file|plus|explor|manager|סייר|FX|solid' } | Sort-Object LastWriteTime -Descending | Select-Object -First 12 LastWriteTime,Length,FullName | Format-Table -AutoSize | Out-String -Width 220
```

## 116. Bash: List device storage root and APKs
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "ls -la /sdcard/ | head -40"
echo ---; "$ADB" shell "find /sdcard -maxdepth 2 -iname '*.apk*' 2>/dev/null | head -20"
```

## 117. Bash: Pull APK and read badging and permissions statically
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D=/c/orech_build/t_fp; mkdir -p $D
"$ADB" pull "/sdcard/פרימיום בעברית.apk" C:/orech_build/t_fp/fp.apk | tail -1
sha256sum $D/fp.apk; ls -l $D/fp.apk
BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe"
"$AAPT" dump badging C:/orech_build/t_fp/fp.apk 2>&1 | grep -E "^package:|application-label:|application-label-he|sdkVersion|targetSdk|native-code" | head -8
echo "--- permissions:"; "$AAPT" dump permissions C:/orech_build/t_fp/fp.apk 2>&1 | grep -iE 'INSTALL|uses-permission' | head -30
```

## 118. Bash: Statically check why the APK is blocked
```
cd /c/orech_build/t_fp && python -I - <<'EOF'
import zipfile,re
z=zipfile.ZipFile('fp.apk')
names=z.namelist()
libs=[n for n in names if n.startswith('lib/') and n.endswith('.so')]
LIBS=["libvlc","libavcodec","libffmpeg","libmpv","libdav1d","libvpx","libde265","libopenh264","libx264","libijkffmpeg","libijksdl"]
print('native libs:',len(libs))
hit=[n for n in libs if any(n.rsplit('/',1)[1].lower().startswith(p) for p in LIBS)]
print('decoder libs hit:',hit[:10])
print('abis:',sorted({n.split('/')[1] for n in libs}))
m=z.read('AndroidManifest.xml')
u8=b'video/'; u16='video/'.encode('utf-16le')
print('manifest video/ (utf8):',u8 in m,' (utf16):',u16 in m)
for tag,name in (('INSTALL_PACKAGES','INSTALL_PACKAGES'),):
    print('manifest',tag,':',name.encode() in m or name.encode('utf-16le') in m)
# which strings contain 'video/'
for enc in ('utf-8','utf-16le'):
    try:
        s=m.decode(enc,errors='ignore')
        for mm in re.finditer(r'[\w./*-]*video/[\w.*-]*',s): print(enc,mm.group(0))
    except Exception as e: pass
other=[n for n in names if re.search(r'(ffmpeg|vlc|exoplayer|media3|codec|decoder)',n,re.I)]
print('other decoder-like entries:',other[:10])
EOF
```

## 119. Bash: Build vguard v9 and syntax-check on device
```
cd /c/orech_build/deploy && python -I - <<'EOF'
s=open('orech_vguard_v8.sh',encoding='utf-8',newline='').read()
assert '\r' not in s
s=s.replace('# orech video guard v5.','# orech video guard v9.\n#  - install-time guards are off (flag file below); enforcement happens after install:\n#    decoder libs are locked (chmod 000) without disabling the app; the app is blacklisted only if a locked\n#    decoder comes back to life, or if a decoder shows up in its data dir later',1)
# flag file (persistent install-time bypass)
s=s.replace('touch "$ALLOW" "$DENY" "$MOCK" "$STATE" "$BL" "$DIS"','touch "$ALLOW" "$DENY" "$MOCK" "$STATE" "$BL" "$DIS"\n# install-time VideoGuard (installer + package manager) is switched off by this flag; see scan_libs/scan_manifest\ntouch /data/system/orech_allow_install',1)
old_libs=s[s.index('scan_libs() {'):s.index('# manifest check of an installed app')]
new_libs='''scan_libs() {
  d=$1
  pkg=$(pkg_of "$d")
  grep -qx "$pkg" "$ALLOW" && return
  back=0
  for f in $(find "$d" -type f -name '*.so' 2>/dev/null); do
    is_video_lib "$f" || continue
    [ "$(stat -c %a "$f")" = "0" ] && continue
    # a lib we already locked is readable again: the decoder came back to life
    grep -qxF "$f" "$STATE" && back=1
    chmod 000 "$f"
    grep -qxF "$f" "$STATE" || echo "$f" >> "$STATE"
    echo "$(date '+%F %T') locked $f" >> "$LOG"
  done
  if [ "$back" = "1" ]; then
    blacklist "$pkg" "decoder came back"
  fi
}

'''
s=s.replace(old_libs,new_libs,1)
s=s.replace('# manifest check of an installed app: video/ mime type -> disable; installer permission -> deny the appop','# manifest check of an installed app: installer permission -> deny the appop (video/ mime types are allowed)',1)
old_v='''  if echo "$m" | grep -a -q 'video/'; then
    blacklist "$pkg" "manifest video/"
    return
  fi
'''
assert old_v in s
s=s.replace(old_v,'',1)
assert 'decoder came back' in s and 'manifest video/' not in s
open('orech_vguard_v9.sh','w',encoding='utf-8',newline='\n').write(s)
print(len(s.encode()))
EOF
diff orech_vguard_v8.sh orech_vguard_v9.sh | head -60; export MSYS_NO_PATHCONV=1; ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" push C:/orech_build/deploy/orech_vguard_v9.sh /data/local/tmp/t9.sh | tail -1
"$ADB" shell "sh -n /data/local/tmp/t9.sh && echo SYNTAX_OK; df /vendor | tail -1"
```

## 120. Bash: Clean temp file and confirm device unchanged
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /data/local/tmp/t9.sh; su -c 'sha256sum /vendor/bin/orech_vguard.sh; ls /data/system/orech_allow_install 2>&1; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
sha256sum /c/orech_build/deploy/orech_vguard_v8.sh /c/orech_build/deploy/orech_vguard_v9.sh | cut -c1-16,65-
```

## 121. Bash: Deploy vguard v9 with backup and verification
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D=C:/orech_build/t_v9; mkdir -p /c/orech_build/t_v9
SHA=$(sha256sum /c/orech_build/deploy/orech_vguard_v9.sh | cut -d' ' -f1); SZ=$(stat -c %s /c/orech_build/deploy/orech_vguard_v9.sh); echo "$SHA $SZ"
printf '%s\n' 'set -e' \
 'B=/mnt/vendor/protect_s/orech' \
 'touch $B/noenforce' \
 "[ \"\$(wc -c < /data/local/tmp/t9.sh)\" = \"$SZ\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/t9.sh\" | sha256sum -c -" \
 'sh -n /data/local/tmp/t9.sh' \
 'mount -o remount,rw /vendor' \
 'cp /vendor/bin/orech_vguard.sh /data/local/tmp/orech_vguard.v8.bak' \
 'dd if=/data/local/tmp/t9.sh of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1' \
 "truncate -s $SZ /vendor/bin/orech_vguard.sh" \
 'sync' \
 "echo \"$SHA  /vendor/bin/orech_vguard.sh\" | sha256sum -c -" \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard' > /c/orech_build/t_v9/deploy_v9.sh
"$ADB" push C:/orech_build/deploy/orech_vguard_v9.sh /data/local/tmp/t9.sh | tail -1
"$ADB" push C:/orech_build/t_v9/deploy_v9.sh /data/local/tmp/deploy_v9.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_v9.sh'"
```

## 122. Bash: Deploy vguard v9 with backup and verification
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D=C:/orech_build/t_v9; mkdir -p /c/orech_build/t_v9
SHA=$(sha256sum /c/orech_build/deploy/orech_vguard_v9.sh | cut -d' ' -f1); SZ=$(stat -c %s /c/orech_build/deploy/orech_vguard_v9.sh); echo "$SHA $SZ"
printf '%s\n' 'set -e' \
 'B=/mnt/vendor/protect_s/orech' \
 'touch $B/noenforce' \
 "[ \"\$(wc -c < /data/local/tmp/t9.sh)\" = \"$SZ\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/t9.sh\" | sha256sum -c -" \
 'sh -n /data/local/tmp/t9.sh' \
 'mount -o remount,rw /vendor' \
 'cp /vendor/bin/orech_vguard.sh /data/local/tmp/orech_vguard.v8.bak' \
 'dd if=/data/local/tmp/t9.sh of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1' \
 "truncate -s $SZ /vendor/bin/orech_vguard.sh" \
 'sync' \
 "echo \"$SHA  /vendor/bin/orech_vguard.sh\" | sha256sum -c -" \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard' > /c/orech_build/t_v9/deploy_v9.sh
"$ADB" push C:/orech_build/deploy/orech_vguard_v9.sh /data/local/tmp/t9.sh | tail -1
"$ADB" push C:/orech_build/t_v9/deploy_v9.sh /data/local/tmp/deploy_v9.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_v9.sh'"
```

## 123. Bash: Phase 1: dummy decoder should be locked, not blacklisted
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /data/local/tmp/t9.sh /data/local/tmp/deploy_v9.sh; su -c 'ls -l /data/system/orech_allow_install; sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16'"
cat > /c/orech_build/t_v9/ph1.sh <<'EOF'
P=$(ls -d /data/app/*/com.orech.seller-*/ | head -1)
mkdir -p ${P}lib/arm64
echo dummy > ${P}lib/arm64/libffmpeg_test.so
chmod 644 ${P}lib/arm64/libffmpeg_test.so
echo "$P" > /data/local/tmp/v9test.path
ls -l ${P}lib/arm64/libffmpeg_test.so
EOF
"$ADB" push C:/orech_build/t_v9/ph1.sh /data/local/tmp/ph1.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/ph1.sh'"
for i in $(seq 1 16); do
  sleep 10
  L=$("$ADB" shell "su -c 'grep -c libffmpeg_test /data/system/orech_vguard/vguard.log'" | tr -d '\r'); [ "$L" != "0" ] && break
done
echo "waited ~$((i*10))s locked_log=$L"
"$ADB" shell "su -c 'tail -3 /data/system/orech_vguard/vguard.log; echo BL=[\$(cat /data/system/orech_vguard/blacklist.txt)]; pm list packages -d | grep -c seller; stat -c %a \$(cat /data/local/tmp/v9test.path)lib/arm64/libffmpeg_test.so'"
```

## 124. Bash: Phase 2: restored decoder should trigger blacklist
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'chmod 644 \$(cat /data/local/tmp/v9test.path)lib/arm64/libffmpeg_test.so'"
for i in $(seq 1 20); do
  sleep 10
  R=$("$ADB" shell "su -c 'grep -c \"decoder came back\" /data/system/orech_vguard/vguard.log'" | tr -d '\r'); [ "$R" != "0" ] && break
done
echo "waited ~$((i*10))s came_back_log=$R"
"$ADB" shell "su -c 'tail -3 /data/system/orech_vguard/vguard.log; echo BL=[\$(cat /data/system/orech_vguard/blacklist.txt)]; pm list packages -d | grep seller'"
```

## 125. Bash: Clean up test state and re-verify device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cat > /c/orech_build/t_v9/clean.sh <<'EOF'
BL=/data/system/orech_vguard/blacklist.txt
grep -vx com.orech.seller $BL > /data/local/tmp/bl.new
cat /data/local/tmp/bl.new > $BL
P=$(cat /data/local/tmp/v9test.path)
rm -f ${P}lib/arm64/libffmpeg_test.so
rmdir ${P}lib/arm64 ${P}lib 2>/dev/null
pm enable com.orech.seller
sed -i '/libffmpeg_test/d' /data/system/orech_vguard/locked.txt
rm -f /data/local/tmp/bl.new /data/local/tmp/v9test.path /data/local/tmp/ph1.sh /data/local/tmp/clean.sh
echo BL_LINES=$(wc -l < $BL)
pm list packages -d | grep -c seller
ls ${P}
EOF
"$ADB" push C:/orech_build/t_v9/clean.sh /data/local/tmp/clean.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/clean.sh'"
"$ADB" shell "pm list packages | grep -i vlc; pm list packages -d | grep -i vlc; su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'; logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 126. Bash: Verify install-time block is off with test APK
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" install C:/orech_build/t_blocktest/blocktest.apk 2>&1 | tail -2
"$ADB" shell "pm list packages | grep blocktest"
"$ADB" uninstall com.orech.blocktest 2>&1 | tail -1
"$ADB" shell "pm list packages | grep -c blocktest; su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce'; logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 127. Bash: Rebuild codeless test APK and verify clean install
```
unset MSYS_NO_PATHCONV
SDK="C:/Users/user1/AppData/Local/Android/Sdk"
BTV=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="$SDK/build-tools/$BTV/aapt2.exe"; AS="$SDK/build-tools/$BTV/apksigner.bat"; JAR="$SDK/platforms/android-34/android.jar"
cd /c/orech_build/t_blocktest && sed -i 's|<application android:label="blocktest">|<application android:label="blocktest" android:hasCode="false">|; s|<activity android:name=".A" android:exported="true">|<activity android:name="android.app.Activity" android:exported="true">|' AndroidManifest.xml
"$AAPT" link -o bt2_unsigned.apk --manifest AndroidManifest.xml -I "$JAR" --min-sdk-version 26 --target-sdk-version 33 2>&1 | tail -2
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$AS" sign --ks "C:/Users/user1/.android/debug.keystore" --ks-pass pass:android --out bt2.apk bt2_unsigned.apk 2>&1 | tail -2
ls -l bt2.apk
export MSYS_NO_PATHCONV=1; ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" install C:/orech_build/t_blocktest/bt2.apk 2>&1 | tail -2
"$ADB" shell "pm list packages | grep blocktest"
"$ADB" uninstall com.orech.blocktest 2>&1 | tail -1
"$ADB" shell "pm list packages | grep -c blocktest; su -c 'tail -2 /data/system/orech_vguard/vguard.log'"
```

## 128. Bash: Inspect install-permission appop state and Settings access
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'echo deny_install=[\$(cat /data/system/orech_vguard/deny_install.txt)]'"
for p in com.google.android.apps.nbu.files bin.mt.plus.canary com.topjohnwu.magisk; do echo "== $p"; "$ADB" shell "cmd appops get $p REQUEST_INSTALL_PACKAGES 2>&1 | head -2; pm list packages -s | grep -c $p"; done
echo "--- settings screen reachable?"
"$ADB" shell "cmd package query-activities -a android.settings.MANAGE_UNKNOWN_APP_SOURCES 2>&1 | grep -E 'name=|packageName' | head -3"
"$ADB" shell "cmd package query-activities -a android.settings.MANAGE_APP_EXTERNAL_SOURCES 2>&1 | grep -E 'name=' | head -3"
```

## 129. Bash: Document v9 policy and limits in handoff
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='גרסת השירות `orech_vguard` הפרוסה: v7.'
assert a in s
s=s.replace(a,'גרסת השירות `orech_vguard` הפרוסה: **v9** (מקור: `C:/orech_build/deploy/orech_vguard_v9.sh`; הגרסה הקודמת v8: `deploy/orech_vguard_v8.sh` ו-`/data/local/tmp/orech_vguard.v8.bak`).',1)
s=s.rstrip()
assert s.endswith('</div>')
s=s[:-6].rstrip('\n')+'''

## 9. מדיניות התקנה v9 (פרוסה בסוף הסשן הזה)
- **בדיקות ההתקנה כבויות:** השירות יוצר בכל עלייה את `/data/system/orech_allow_install`, ולכן `VideoGuard` במתקין ובמנהל החבילות לא חוסם. הדגל כבר לא "זמני לפני התקנה": אל תמחק אותו אלא אם רוצים לחזור לחסימה בזמן התקנה (במקרה כזה גם להחזיר את v8 ולמחוק את השורה `touch /data/system/orech_allow_install`).
- **אכיפה אחרי התקנה:** ספריית פענוח (לפי שם) באפליקציה מותקנת ננעלת ב-`chmod 000` בלי להשבית את האפליקציה. האפליקציה מושבתת ונכנסת לרשימה השחורה רק אם ספרייה נעולה חוזרת להיות קריאה (`decoder came back`) או אם מופיעה ספריית פענוח ב-`/data/data` שלה. הצהרת `video/` במניפסט כבר לא חוסמת. הרשאת `REQUEST_INSTALL_PACKAGES` נשללת באמצעות appops (ראו מגבלות למטה).
- **נבדק על המכשיר:** (1) קובץ דמה `libffmpeg_test.so` בספריית האפליקציה ננעל תוך שניות, האפליקציה לא הושבתה. (2) החזרת הרשאת קריאה גרמה ל-`blacklisted+disabled (decoder came back)` תוך כ-80 שניות. (3) APK בדיקה עם `video/*` במניפסט הותקן בהצלחה (בלי חסימה) והוסר. הכל נוקה אחרי הבדיקה. `check` ריק, 0 קריסות.
- **נצפה:** VLC הותקן (אחרי הפעלת הדגל) וספריות הפענוח שלו ננעלו בלי השבתה. File Manager + (גרסה מתוקנת מפורום, ללא ספריות native) לא הותקן על ידי הסשן; התקנה ידנית.
- **מגבלות (לא להבטיח יותר):** שלילת `REQUEST_INSTALL_PACKAGES` היא appop לכל חבילה (`cmd appops set <חבילה> REQUEST_INSTALL_PACKAGES deny`), מחודשת בערך פעם בדקה ולחבילות חדשות; מסך "התקנת אפליקציות לא ידועות" (`Settings$ManageExternalSourcesActivity`) נגיש למשתמש. נצפה ש-`com.google.android.apps.nbu.files` נמצא ב-`deny_install.txt` אך מצב ה-appop שלו היה `allow`, כלומר השלילה אינה אכיפה מלאה. לא נבדק למה.
</div>
'''
open(p,'w',encoding='utf-8',newline='\n').write(s)
bad=[c for c in s if ord(c)<32 and c!='\n']
print('ctrl chars',len(bad))
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- פרישת `orech_vguard` v9: בדיקות התקנה כבויות דרך דגל, נעילת מפענחים אחרי התקנה, רשימה שחורה רק כשמפענח חוזר. נבדק בבדיקות סינתטיות ונוקה. פירוט וגבולות ב-HANDOFF סעיף 9.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
EOF
grep -c 'סעיף 9\|## 9. מדיניות' HANDOFF_M393.md CHANGELOG_M393.md
```

## 130. Bash: Test deny command effect on appop for Files by Google
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
P=com.google.android.apps.nbu.files
echo "== before:"; "$ADB" shell "cmd appops get $P REQUEST_INSTALL_PACKAGES; cmd appops get $P --uid REQUEST_INSTALL_PACKAGES 2>&1 | head -2"
echo "== set deny (as root, like vguard):"; "$ADB" shell "su -c 'cmd appops set $P REQUEST_INSTALL_PACKAGES deny; echo EXIT=\$?'"
echo "== after:"; "$ADB" shell "cmd appops get $P REQUEST_INSTALL_PACKAGES"
echo "== uid:"; "$ADB" shell "dumpsys package $P | grep -E 'userId=|sharedUser|android.permission.REQUEST_INSTALL' | head -4"
echo "== vguard uses:"; "$ADB" shell "su -c 'grep -n REQUEST_INSTALL /vendor/bin/orech_vguard.sh'"
```

## 131. Bash: Roll back to vguard v8 and remove the bypass flag
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SHA8=$(sha256sum /c/orech_build/deploy/orech_vguard_v8.sh | cut -d' ' -f1); SZ8=$(stat -c %s /c/orech_build/deploy/orech_vguard_v8.sh); echo "$SHA8 $SZ8"
printf '%s\n' 'set -e' 'B=/mnt/vendor/protect_s/orech' 'touch $B/noenforce' \
 'S=/data/local/tmp/orech_vguard.v8.bak' \
 "[ \"\$(wc -c < \$S)\" = \"$SZ8\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA8  \$S\" | sha256sum -c -" \
 'mount -o remount,rw /vendor' \
 'dd if=$S of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1' \
 "truncate -s $SZ8 /vendor/bin/orech_vguard.sh" 'sync' \
 "echo \"$SHA8  /vendor/bin/orech_vguard.sh\" | sha256sum -c -" \
 'rm -f /data/system/orech_allow_install' \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard' \
 'ls /data/system/orech_allow_install 2>&1 || true' > /c/orech_build/t_v9/rollback.sh
"$ADB" push C:/orech_build/t_v9/rollback.sh /data/local/tmp/rollback.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/rollback.sh; rm -f /data/local/tmp/rollback.sh'"
echo "--- inotifyd available?"; "$ADB" shell "which inotifyd; ls /system/bin/inotifyd /system/bin/toybox 2>&1 | head -2"
```

## 132. Bash: Update handoff and changelog after rollback
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a=s.index('## 9. מדיניות התקנה v9')
s=s[:a]+'''## 9. ניסוי v9 שנדחה, והכיוון שהמשתמש בחר
- **v9 נפרס, נבדק ובוטל באותו סשן** לפי החלטת המשתמש. הפרוס בפועל: **v8** (sha256 `890863c7...`, 4938 בייט), הדגל `/data/system/orech_allow_install` **לא קיים** ובדיקות ההתקנה פעילות כמו קודם. המקור של v9 נשאר ב-`C:/orech_build/deploy/orech_vguard_v9.sh` לעיון בלבד (נעילת מפענחים בלי השבתה, השבתה רק כש"המפענח חוזר"; נבדק בבדיקות סינתטיות).
- **החלטות המשתמש:** (1) חסימת ההתקנה צריכה להישאר ולהיות חזקה יותר, לא להקל. (2) מוכן לוותר על File Manager + (גרסה מתוקנת מפורום; נחסמה בגלל `video/` במניפסט והרשאת `REQUEST_INSTALL_PACKAGES`, ואין בה ספריות native). (3) רוצה סייר קבצים **מובנה** ללא יכולת התקנה וללא תמיכה בוידיאו, וכל מה שיותקן יהיה רק דרכו. (4) **לא רוצה סריקות רקע תכופות**: שלילה חד-פעמית בהתקנה, בלי בזבוז סוללה והאטה.
- **מצב נוכחי של הסריקות (v8):** לולאה כל 2 שניות על `/data/app`, וסריקה מלאה של `/data/data` כל כ-60 שניות. לא נמדדה צריכת סוללה בפועל. `inotifyd` קיים ב-`/system/bin` ויכול להחליף חלק מהסריקות באירועים.
- **מגבלה ידועה של שלילת ההתקנה:** appop לכל חבילה (`cmd appops set <חבילה> REQUEST_INSTALL_PACKAGES deny`, בדיקה ידנית כ-root עבדה על `com.google.android.apps.nbu.files`, `EXIT=0`, מצב `deny`); מסך `Settings$ManageExternalSourcesActivity` נגיש למשתמש, ולכן זו לא אכיפה מלאה.
- **לא התחיל:** סייר קבצים מובנה, וסריקות מבוססות אירועים. אל תוריד אפליקציה לצורך זה בלי רשות מפורשת מהמשתמש.
</div>
'''
s=s.replace('הפרוסה: **v9** (מקור: `C:/orech_build/deploy/orech_vguard_v9.sh`; הגרסה הקודמת v8: `deploy/orech_vguard_v8.sh` ו-`/data/local/tmp/orech_vguard.v8.bak`).','הפרוסה: **v8** (`deploy/orech_vguard_v8.sh`; v9 נוסה ובוטל, ראו סעיף 9).',1)
assert 'v8** (`deploy' in s
bad=[c for c in s if ord(c)<32 and c!='\n']; print('ctrl',len(bad))
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c.replace('- פרישת `orech_vguard` v9: בדיקות התקנה כבויות דרך דגל, נעילת מפענחים אחרי התקנה, רשימה שחורה רק כשמפענח חוזר. נבדק בבדיקות סינתטיות ונוקה. פירוט וגבולות ב-HANDOFF סעיף 9.','- `orech_vguard` v9 נפרס ונבדק ואז **בוטל** לפי החלטת המשתמש; הוחזר v8 והדגל `orech_allow_install` הוסר (סעיף 9 ב-HANDOFF).')
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c+'\n')
EOF
grep -c 'בוטל' HANDOFF_M393.md CHANGELOG_M393.md; tail -c 200 CHANGELOG_M393.md
```

## 133. Bash: Clone the user-provided repo into an isolated folder
```
mkdir -p /c/orech_build/ext && cd /c/orech_build/ext && GIT_TERMINAL_PROMPT=0 git clone --depth 1 https://github.com/Xnet2/file-manager-x fmx 2>&1 | tail -3
cd fmx && ls -la | head -30; du -sh .; git log -1 --format='%h %an %ad %s' | head -1; ls | grep -iE 'licen|copying'
```

## 134. Bash: Clone the user-provided repo into an isolated folder
```
mkdir -p /c/orech_build/ext && cd /c/orech_build/ext && GIT_TERMINAL_PROMPT=0 git clone --depth 1 https://github.com/Xnet2/file-manager-x fmx 2>&1 | tail -3
cd fmx && ls -la | head -30; du -sh .; git log -1 --format='%h %an %ad %s' | head -1; ls | grep -iE 'licen|copying'
```

## 135. Bash: Check build kit and flavor flags usage
```
ls "$LOCALAPPDATA/VODOS-build" 2>&1 | head -5; cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && grep -n "NO_VIDEO\|NO_INSTALL" *.java | head -30; ls /c/orech_build/ext/fmx/צילומי*
```

## 136. Bash: Inspect app name strings and available toolchain
```
cd /c/orech_build/ext/fmx/app/src/main/res/values && grep -n "מנהל הקבצים\|app_name" values.xml | head; grep -rn "מנהל הקבצים\|Flavor.NAME\|com.custom" /c/orech_build/ext/fmx/app/src/main/java --include=*.java | grep -v "^.*:package com.custom" | head -12; ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/; ls "/c/Program Files/Android/Android Studio/jbr/bin/" | grep -E "^(javac|java|jar|keytool)\.exe"; "/c/Program Files/Android/Android Studio/jbr/bin/java.exe" -version 2>&1 | head -1
```

## 137. Bash: Build the lite file manager with new package name
```
cd /c/orech_build/ext && PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -15
```

## 138. Bash: Verify package, permissions and guard conditions
```
unset MSYS_NO_PATHCONV
BT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/37.0.0"
A=C:/orech_build/ext/fmx_out/FileManager-Lite.apk
"$BT/aapt2.exe" dump badging $A | grep -E "^package:|application-label:|native-code|targetSdk"
"$BT/aapt2.exe" dump permissions $A | grep -E "uses-permission"
"$BT/aapt2.exe" dump xmltree --file AndroidManifest.xml $A | grep -iE "provider|authorities|mimeType|intent-filter" | head
python -I - <<'EOF'
import zipfile
z=zipfile.ZipFile(r'C:/orech_build/ext/fmx_out/FileManager-Lite.apk')
m=z.read('AndroidManifest.xml')
def has(b): return b in m or b.decode().encode('utf-16le') in m
print('manifest video/:',has(b'video/'),'| INSTALL_PACKAGES:',has(b'INSTALL_PACKAGES'))
print('native libs:',[n for n in z.namelist() if n.startswith('lib/')])
print('entries:',len(z.namelist()))
EOF
"$BT/apksigner.bat" verify --print-certs $A 2>&1 | head -3
```

## 139. Bash: Copy built APK to the project folder
```
cp /c/orech_build/ext/fmx_out/FileManager-Lite.apk "/c/פרוייקטים קלוד/zamir/FileManager-Lite_com.orech.files.apk" && ls -l "/c/פרוייקטים קלוד/zamir/FileManager-Lite_com.orech.files.apk"
```

## 140. Bash: Check the fork is readable, no auth used
```
cd /c/orech_build/ext/fmx && GIT_TERMINAL_PROMPT=0 git ls-remote https://github.com/shmuel-sinay/file-manager-x 2>&1 | head -3; git remote -v; git status --short | head
```

## 141. Bash: Set fork remote and create permanent signing key
```
cd /c/orech_build/ext/fmx && git remote rename origin upstream && git remote add origin https://github.com/shmuel-sinay/file-manager-x && git remote -v
mkdir -p /c/orech_build/ext/keys && cd /c/orech_build/ext/keys
PW=$(python -I -c "import secrets,string;print(''.join(secrets.choice(string.ascii_letters+string.digits) for _ in range(28)))")
printf '%s' "$PW" > fmx.pass
rm -f ../fmx_release.keystore
"/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -genkeypair -keystore fmx_release.keystore -alias fmx -keyalg RSA -keysize 4096 -validity 12000 -storepass "$PW" -keypass "$PW" -dname "CN=Orech Files" 2>&1 | tail -1
ls -l; "/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -list -keystore fmx_release.keystore -storepass "$PW" 2>&1 | grep -E "Valid|Certificate fingerprint|SHA-256|Alias" | head -4
```

## 142. Bash: Set fork remote and create permanent signing key
```
cd /c/orech_build/ext/fmx && git remote rename origin upstream && git remote add origin https://github.com/shmuel-sinay/file-manager-x && git remote -v
mkdir -p /c/orech_build/ext/keys && cd /c/orech_build/ext/keys && ls
PW=$(python -I -c "import secrets,string;print(''.join(secrets.choice(string.ascii_letters+string.digits) for _ in range(28)))")
printf '%s' "$PW" > fmx.pass
"/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -genkeypair -keystore fmx_release.keystore -alias fmx -keyalg RSA -keysize 4096 -validity 12000 -storepass "$PW" -keypass "$PW" -dname "CN=Orech Files" 2>&1 | tail -1
ls -l; "/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -list -v -keystore fmx_release.keystore -storepass "$PW" 2>&1 | grep -E "Valid from|SHA256" | head -3
```

## 143. Bash: Set fork remote and create permanent signing key
```
cd /c/orech_build/ext/fmx && git remote rename origin upstream && git remote add origin https://github.com/shmuel-sinay/file-manager-x && git remote -v
mkdir -p /c/orech_build/ext/keys && cd /c/orech_build/ext/keys && ls
PW=$(python -I -c "import secrets,string;print(''.join(secrets.choice(string.ascii_letters+string.digits) for _ in range(28)))")
printf '%s' "$PW" > fmx.pass
"/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -genkeypair -keystore fmx_release.keystore -alias fmx -keyalg RSA -keysize 4096 -validity 12000 -storepass "$PW" -keypass "$PW" -dname "CN=Orech Files" 2>&1 | tail -1
ls -l; "/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -list -v -keystore fmx_release.keystore -storepass "$PW" 2>&1 | grep -E "Valid from|SHA256" | head -3
```

## 144. Bash: Read Ui helper header and sizes
```
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && wc -l Ui.java HomeScreen.java BrowserScreen.java Drawer.java MainActivity.java && sed -n 1,80p Ui.java
```

## 145. Bash: Check gh login and read rest of Ui.java
```
gh auth status 2>&1 | head -6
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && sed -n 80,259p Ui.java; grep -n "COLOR\|SOFT" Media.java | head -8; grep -c "D97706\|FFF3DA\|FFEBC2" *.java | grep -v ":0"
```

## 146. Bash: Inspect setBars and color usages
```
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && grep -n "void setBars" -A8 MainActivity.java; grep -n "D97706\|FFF3DA\|FFEBC2" FileAdapter.java Ui.java; grep -n "Media.COLOR\|Media.SOFT\|\.soft" *.java | grep -v "^Media.java" | head -12; sed -n 10,20p Media.java
```

## 147. Bash: Apply flat-list style to the home screen and rebuild
```
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && python -I - <<'PYEOF'
import re
def edit(fn, pairs):
    s = open(fn, encoding='utf-8', newline='').read()
    crlf = '\r\n' in s
    if crlf: s = s.replace('\r\n', '\n')
    for old, new in pairs:
        assert old in s, (fn, old[:60])
        s = s.replace(old, new, 1)
    if crlf: s = s.replace('\n', '\r\n')
    open(fn, 'w', encoding='utf-8', newline='').write(s)

# --- tokens: warm paper, ink, steel blue; flat bar
edit('Ui.java', [
 ('''    static final int BG = 0xFFF4F6FA, SURFACE = 0xFFFFFFFF, PRIMARY = 0xFFD97706, PRIMARY_SOFT = 0xFFFFF3DA,
            TEXT = 0xFF0F172A, TEXT2 = 0xFF64748B, TEXT3 = 0xFF94A3B8, DIVIDER = 0xFFE7EBF1,
            DANGER = 0xFFDC2626, SELECTED = 0xFFFFEBC2, AMBER = 0xFFF59E0B, DARK = 0xFF1E293B;''',
  '''    static final int BG = 0xFFF3EFE6, SURFACE = 0xFFFBF8F1, PRIMARY = 0xFF2F6F9F, PRIMARY_SOFT = 0xFFDDE9F2,
            TEXT = 0xFF1D2327, TEXT2 = 0xFF6B665C, TEXT3 = 0xFF9A958A, DIVIDER = 0xFFD9D3C4,
            DANGER = 0xFFB3472F, SELECTED = 0xFFD3E3EE, AMBER = 0xFFC98A1B, DARK = 0xFF26343B;'''),
 ('csl(0x22D97706)', 'csl(0x222F6F9F)'),
 ('csl(0x1CD97706)', 'csl(0x1C2F6F9F)'),
 ('        bar.setBackgroundColor(SURFACE);\n        bar.setElevation(dp(2));', '        bar.setBackgroundColor(SURFACE);\n        bar.setElevation(0);'),
 # flat usage bar (square ends) for the list style
 ('''        float frac;
        int color = PRIMARY;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Bar(Context c) { super(c); }''',
  '''        float frac;
        int color = PRIMARY;
        boolean flat;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Bar(Context c) { super(c); }'''),
 ('            float h = getHeight(), w = getWidth(), rad = h / 2f;', '            float h = getHeight(), w = getWidth(), rad = flat ? 0 : h / 2f;'),
])
edit('FileAdapter.java', [('0xE6D97706', '0xE62F6F9F')])
edit('Media.java', [
 ('{0xFF8B5CF6, 0xFFEF4444, 0xFF0D9488, 0xFF2563EB, 0xFF22A35A, 0xFFB7791F, 0xFF475569, 0xFFDB2777}',
  '{0xFF9A5B2E, 0xFFB3472F, 0xFF2E7D5B, 0xFF2F6F9F, 0xFF4C7F3A, 0xFF8A6A1F, 0xFF6B665C, 0xFF7A4B63}'),
 ('{0xFFF1EBFE, 0xFFFDECEC, 0xFFDDF5F1, 0xFFE8EFFD, 0xFFE3F5EA, 0xFFF8EEDA, 0xFFEDF1F6, 0xFFFCE7F3}',
  '{0xFFF0E3D6, 0xFFF3E1DA, 0xFFDFEDE5, 0xFFDDE9F2, 0xFFE4EEDC, 0xFFF0E7D0, 0xFFE9E5DB, 0xFFEBDDE4}'),
])

# --- home screen: flat list
edit('HomeScreen.java', [
 ('/** Start screen: a grid of big tiles — storages, Downloads, categories and tools (like the original, but light). */',
  '/** Start screen: a flat list in three sections — storage, categories, tools — with a colour stripe per row. */'),
 ('''        int icon, color, soft;
        String title, sub = " ";''', '''        int icon, color, soft;
        String title, sub = " ", section = "";'''),
 ('''        LinearLayout bar = Ui.appBar(a);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_menu, Ui.TEXT, "תפריט", v -> a.openDrawer()));
        TextView t = Ui.line(a, Flavor.NAME, 20, Ui.TEXT, Ui.MEDIUM);
        t.setPaddingRelative(Ui.dp(8), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.TEXT, "חיפוש", v -> a.push(BrowserScreen.search(a))));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.TEXT, "אפשרויות", v -> menu()));''',
  '''        LinearLayout bar = Ui.appBar(a);
        bar.setBackgroundColor(Ui.DARK);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_menu, Ui.BG, "תפריט", v -> a.openDrawer()));
        TextView t = Ui.line(a, Flavor.NAME, 20, Ui.BG, Ui.BOLD);
        t.setPaddingRelative(Ui.dp(8), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.BG, "חיפוש", v -> a.push(BrowserScreen.search(a))));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.BG, "אפשרויות", v -> menu()));'''),
 ('        Ui.pad(col, 10, 10, 10, 10);', '        Ui.pad(col, 0, 0, 0, 16);'),
 ('''        TextView fh = Ui.text(a, "מועדפים", 15, Ui.TEXT, Ui.MEDIUM);
        favSection.addView(fh, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 10, 18, 10, 10));
        favBox = Ui.vbox(a);
        favBox.setBackground(Ui.round(Ui.SURFACE, 20));
        favBox.setClipToOutline(true);
        favSection.addView(favBox, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 6, 0, 6, 0));''',
  '''        favSection.addView(sectionHeader("מועדפים"), Ui.lp(Ui.MATCH, Ui.WRAP));
        favBox = Ui.vbox(a);
        favSection.addView(favBox, Ui.lp(Ui.MATCH, Ui.WRAP));'''),
 ('        a.setBars(Ui.SURFACE, true);', '        a.setBars(Ui.DARK, false);'),
])
s = open('HomeScreen.java', encoding='utf-8', newline='').read()
crlf = '\r\n' in s
if crlf: s = s.replace('\r\n', '\n')
a0 = s.index('    private View tileView(Tile t) {')
a1 = s.index('    private Tile catTile(int cat, long[] st) {')
new_block = '''    private TextView sectionHeader(String title) {
        TextView h = Ui.text(a, title, 12, Ui.TEXT2, Ui.MEDIUM);
        h.setLetterSpacing(0.08f);
        Ui.pad(h, 16, 14, 16, 4);
        return h;
    }

    private View rowView(Tile t) {
        FrameLayout wrap = new FrameLayout(a);
        wrap.setBackground(Ui.rippleRect());
        LinearLayout r = Ui.hbox(a);
        Ui.pad(r, 18, 11, 16, 11);
        r.addView(Ui.img(a, t.icon, t.color), Ui.lp(Ui.dp(24), Ui.dp(24)));
        LinearLayout mid = Ui.vbox(a);
        mid.setPaddingRelative(Ui.dp(14), 0, Ui.dp(10), 0);
        mid.addView(Ui.line(a, t.title, 16, Ui.TEXT, Ui.MEDIUM));
        if (t.frac >= 0) {
            Ui.Bar bar = new Ui.Bar(a);
            bar.flat = true;
            bar.set(t.frac, t.frac > 0.9f ? Ui.DANGER : t.frac > 0.75f ? Ui.AMBER : t.color);
            mid.addView(bar, Ui.margins(Ui.lp(Ui.MATCH, Ui.dp(4)), 0, 6, 0, 0));
        }
        r.addView(mid, Ui.lp(0, Ui.WRAP, 1));
        TextView s = Ui.line(a, t.sub, 13, Ui.TEXT2, Ui.REGULAR);
        s.setTextDirection(View.TEXT_DIRECTION_RTL);
        r.addView(s, Ui.lp(Ui.WRAP, Ui.WRAP));
        wrap.addView(r, new FrameLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        View stripe = new View(a);
        stripe.setBackgroundColor(t.color);
        wrap.addView(stripe, new FrameLayout.LayoutParams(Ui.dp(4), Ui.MATCH, Gravity.START));
        View line = new View(a);
        line.setBackgroundColor(Ui.DIVIDER);
        wrap.addView(line, new FrameLayout.LayoutParams(Ui.MATCH, Math.max(1, Ui.dp(1)), Gravity.BOTTOM));
        wrap.setOnClickListener(v -> t.click.run());
        return wrap;
    }

    private void layoutTiles(ArrayList<Tile> tiles) {
        grid.removeAllViews();
        String cur = null;
        for (Tile t : tiles) {
            if (!t.section.equals(cur)) {
                cur = t.section;
                grid.addView(sectionHeader(cur), Ui.lp(Ui.MATCH, Ui.WRAP));
            }
            grid.addView(rowView(t), Ui.lp(Ui.MATCH, Ui.WRAP));
        }
    }

'''
s = s[:a0] + new_block + s[a1:]
def rep(old, new, cnt=1):
    global s
    assert old in s, old[:70]
    s = s.replace(old, new, cnt)
# colours + sections
rep('        t.title = Media.TITLES[cat];\n', '        t.title = Media.TITLES[cat];\n        t.section = "קטגוריות";\n')
rep('''            t.color = v.primary ? 0xFF475569 : 0xFF4F46E5;
            t.soft = v.primary ? 0xFFEDF1F6 : 0xFFEEF0FE;
            t.title = v.label;''', '''            t.color = v.primary ? 0xFF2F6F9F : 0xFFB3472F;
            t.soft = v.primary ? 0xFFDDE9F2 : 0xFFF3E1DA;
            t.title = v.label;
            t.section = "אחסון";''')
rep('''            t.color = 0xFF334155;
            t.soft = 0xFFEDF1F6;
            t.title = "אחסון מערכת";''', '''            t.color = 0xFF4A5A63;
            t.soft = 0xFFE4E7E8;
            t.title = "אחסון מערכת";
            t.section = "אחסון";''')
rep('''        d.color = 0xFF0EA5E9;
        d.soft = 0xFFE0F2FB;
        d.title = "הורדות";''', '''        d.color = 0xFF2F6F9F;
        d.soft = 0xFFDDE9F2;
        d.title = "הורדות";
        d.section = "קטגוריות";''')
rep('''        tr.color = 0xFF64748B;
        tr.soft = 0xFFEDF1F6;
        tr.title = "סל מיחזור";''', '''        tr.color = 0xFF6B665C;
        tr.soft = 0xFFE9E5DB;
        tr.title = "סל מיחזור";
        tr.section = "כלים";''')
rep('''        an.color = 0xFFDB2777;
        an.soft = 0xFFFCE7F3;
        an.title = "ניתוח אחסון";''', '''        an.color = 0xFF7A4B63;
        an.soft = 0xFFEBDDE4;
        an.title = "ניתוח אחסון";
        an.section = "כלים";''')
# favourites: flat rows, no rounded tile / dividers inset
rep('''            if (i > 0) favBox.addView(Ui.divider(a), Ui.margins(Ui.lp(Ui.MATCH, Math.max(1, Ui.dp(1))), 68, 0, 0, 0));''',
    '''            if (i > 0) favBox.addView(Ui.divider(a));''')
rep('''            FrameLayout tile = new FrameLayout(a);
            tile.setBackground(Ui.round(0xFFFEF3DC, 12));
            tile.addView(Ui.img(a, R.drawable.ic_star, Ui.AMBER), new FrameLayout.LayoutParams(Ui.dp(22), Ui.dp(22), Gravity.CENTER));
            r.addView(tile, Ui.lp(Ui.dp(42), Ui.dp(42)));''', '''            r.addView(Ui.img(a, R.drawable.ic_star, Ui.AMBER), Ui.lp(Ui.dp(24), Ui.dp(24)));''')
if crlf: s = s.replace('\n', '\r\n')
open('HomeScreen.java', 'w', encoding='utf-8', newline='').write(s)
print('edited')
PYEOF
cd /c/orech_build/ext && PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -15
```

## 148. Bash: Apply style A edits and rebuild the APK
```
cd /c/orech_build/ext && PYTHONIOENCODING=utf-8 python -I apply_style_a.py 2>&1 | tail -5 && PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -12
```

## 149. Bash: Rebuild with permanent key and verify the new UI is in the APK
```
cd /c/orech_build/ext && grep -c "rowView" fmx_out/src/java/com/custom/files/HomeScreen.java; grep -n "boolean flat" fmx/app/src/main/java/com/custom/files/Ui.java | head -2
PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -6
unset MSYS_NO_PATHCONV
BT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/37.0.0"
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$BT/apksigner.bat" verify --print-certs C:/orech_build/ext/fmx_out/FileManager-Lite.apk 2>&1 | grep -E "Signer #1 certificate (DN|SHA-256)|WARNING|ERROR" | head -3
"$BT/aapt2.exe" dump badging C:/orech_build/ext/fmx_out/FileManager-Lite.apk | grep -E "^package:"
python -I -c "
import zipfile;z=zipfile.ZipFile(r'C:/orech_build/ext/fmx_out/FileManager-Lite.apk');d=z.read('classes.dex');print('rowView in dex:',b'rowView' in d,'| sectionHeader:',b'sectionHeader' in d,'| tileView:',b'tileView' in d)"
```

## 150. Bash: Rebuild with permanent key and verify the new UI is in the APK
```
cd /c/orech_build/ext && grep -c "rowView" fmx_out/src/java/com/custom/files/HomeScreen.java; grep -n "boolean flat" fmx/app/src/main/java/com/custom/files/Ui.java | head -2
PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -6
unset MSYS_NO_PATHCONV
BT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/37.0.0"
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$BT/apksigner.bat" verify --print-certs C:/orech_build/ext/fmx_out/FileManager-Lite.apk 2>&1 | grep -E "Signer #1 certificate (DN|SHA-256)|WARNING|ERROR" | head -3
"$BT/aapt2.exe" dump badging C:/orech_build/ext/fmx_out/FileManager-Lite.apk | grep -E "^package:"
python -I -c "
import zipfile;z=zipfile.ZipFile(r'C:/orech_build/ext/fmx_out/FileManager-Lite.apk');d=z.read('classes.dex');print('rowView in dex:',b'rowView' in d,'| sectionHeader:',b'sectionHeader' in d,'| tileView:',b'tileView' in d)"
```

## 151. Bash: Verify signature, install and screenshot the new home screen
```
unset MSYS_NO_PATHCONV
BT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/37.0.0"
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$BT/apksigner.bat" verify --verbose --print-certs C:/orech_build/ext/fmx_out/FileManager-Lite.apk 2>&1 | head -6
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" install -r C:/orech_build/ext/fmx_out/FileManager-Lite.apk 2>&1 | tail -2
"$ADB" shell "su -c 'appops set com.orech.files MANAGE_EXTERNAL_STORAGE allow'"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.orech.files/com.custom.files.MainActivity | tail -1"
sleep 4
"$ADB" exec-out screencap -p > "$SP/fm1.png"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 152. Bash: Check gitignore, identity and changes in fork
```
cd /c/orech_build/ext/fmx && cat .gitignore; git config user.name; git config user.email; git status --short
```

## 153. Bash: Add portable build script and verify it reproduces the APK
```
cd /c/orech_build/ext/fmx && printf '*.pass\nout/\n' >> .gitignore && export FMX_KEYSTORE='C:\orech_build\ext\keys\fmx_release.keystore' FMX_KEYPASS_FILE='C:\orech_build\ext\keys\fmx.pass' && PYTHONIOENCODING=utf-8 python -I build_orech.py 2>&1 | tail -4; cmp FileManager-Lite.apk /c/orech_build/ext/fmx_out/FileManager-Lite.apk && echo SAME_AS_TESTED || echo "differs (signature/timestamps), size: $(stat -c %s FileManager-Lite.apk) vs $(stat -c %s /c/orech_build/ext/fmx_out/FileManager-Lite.apk)"; git status --short
```

## 154. Bash: Commit locally in the fork; list stored GitHub credential targets
```
cd /c/orech_build/ext/fmx && printf '*.idsig\n' >> .gitignore && git add .gitignore build_orech.py app/src/main/java/com/custom/files/FileAdapter.java app/src/main/java/com/custom/files/HomeScreen.java app/src/main/java/com/custom/files/Media.java app/src/main/java/com/custom/files/Ui.java && git status --short && git commit -q -F - <<'EOF'
Flat list home screen, new colour tokens, portable build script

- Home screen: dark header, three sections (storage, categories, tools), one flat row per
  entry with a colour stripe at the start edge and a square usage bar; no rounded cards.
- Colour tokens: warm paper background, ink text, steel-blue accent, earthy category colours.
- App bar is flat (no elevation). Ripple/selection colours follow the new accent.
- build_orech.py builds the lite variant (no video, no app install) under a chosen package
  name and signs it with a persistent key taken from FMX_KEYSTORE / FMX_KEYPASS_FILE.
- Ignore *.pass, *.idsig and the out/ folder.

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>
EOF
git log --oneline -2; git status --short
echo "--- saved git credentials (targets only):"; cmdkey /list 2>/dev/null | grep -iE -B1 -A3 "github" | head -12
```

## 155. Bash: Back up signing key, update APK copy and handoff
```
mkdir -p "/c/Users/user1/Documents/fmx-keys-backup" && cp /c/orech_build/ext/keys/fmx_release.keystore /c/orech_build/ext/keys/fmx.pass "/c/Users/user1/Documents/fmx-keys-backup/" && ls -l "/c/Users/user1/Documents/fmx-keys-backup/"
cp /c/orech_build/ext/fmx_out/FileManager-Lite.apk "/c/פרוייקטים קלוד/zamir/FileManager-Lite_com.orech.files.apk"
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read().rstrip()
assert s.endswith('</div>')
s=s[:-6].rstrip('\n')+'''

## 10. סייר קבצים מובנה (`com.orech.files`)
- **מקור:** פורק של המשתמש, `github.com/shmuel-sinay/file-manager-x`, משוכפל ב-`C:/orech_build/ext/fmx` (`origin` = הפורק, `upstream` = `Xnet2/file-manager-x`). commit מקומי `ebf62e2`, **לא נדחף** (אין התחברות ל-GitHub בסביבה הזו; לא להשתמש בטוקן של אחרים).
- **מה הוא:** וריאנט lite (ללא וידיאו, בלי `REQUEST_INSTALL_PACKAGES`), שם חבילה `com.orech.files`, שם "מנהל קבצים". מסך הבית הוחלף לרשימה שטוחה (סגנון א שנבחר): כותרת כהה, מקטעים, פס צבע בצד. במניפסט אין `video/` ואין `INSTALL_PACKAGES`, ולכן הבדיקות במתקין וב-vguard לא חוסמות אותו.
- **בנייה:** `FMX_KEYSTORE` ו-`FMX_KEYPASS_FILE` מוגדרים, ואז `python -I build_orech.py` מתוך `ext/fmx`. סקריפט נוסף ב-`ext/build_fmx.py`.
- **חתימה קבועה:** `C:/orech_build/ext/keys/fmx_release.keystore` (RSA 4096, תקף עד 2059, alias `fmx`, סיסמה ב-`fmx.pass` באותה תיקייה) + עותק גיבוי ב-`Documents/fmx-keys-backup`. **אסור לקמיט, ואם המפתח אובד אי אפשר לעדכן עותקים מותקנים.** `ext/fmx_release.keystore` הישן הוא זמני ולא בשימוש.
- **נבדק:** נבנה, נחתם (v2/v3 אומתו), הותקן במכשיר, מסך הבית נצפה בצילום, 0 קריסות. **לא נבדק:** שאר המסכים בעיצוב החדש (תיקיות, הגדרות, חיפוש), התקנה כאפליקציית מערכת/מובנית, ופעולות קבצים.
- **לא התחיל:** הפיכתו למובנה במערכת (תיקיית `/product/app`), רשימת היתרים, וסריקות מבוססות אירועים.
</div>
'''
open(p,'w',encoding='utf-8',newline='\n').write(s)
print('ctrl',sum(1 for c in s if ord(c)<32 and c!='\n'))
PYEOF
```

## 156. Bash: Verify gh login account and local branch
```
gh auth status 2>&1 | head -6; cd /c/orech_build/ext/fmx && git branch --show-current && git log --oneline -2 && git remote get-url origin
```

## 157. Bash: Push the commit to the user's fork
```
cd /c/orech_build/ext/fmx && git show --stat --format= HEAD | tail -8 && git push origin main 2>&1 | tail -6; echo ---; git ls-remote origin main
```

## 158. Bash: Update handoff to reflect pushed state
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='commit מקומי `ebf62e2`, **לא נדחף** (אין התחברות ל-GitHub בסביבה הזו; לא להשתמש בטוקן של אחרים).'
assert a in s
s=s.replace(a,'commit `ebf62e2` **נדחף** ל-`main` בפורק (נבדק עם `git ls-remote`). `gh` מחובר כ-`shmuel-sinay` (התחברות של המשתמש בדפדפן); לא להשתמש בטוקן של אחרים.',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
PYEOF
grep -c 'נדחף' HANDOFF_M393.md
```

## 159. Bash: Check key repo name is free and fork visibility
```
gh repo view shmuel-sinay/orech-keys --json name,visibility 2>&1 | head -3; gh repo view shmuel-sinay/file-manager-x --json visibility -q .visibility 2>&1
```

## 160. Bash: Create private repo and push the signing key
```
D=/c/orech_build/ext/keys_repo; mkdir -p $D && cd $D && cp /c/orech_build/ext/keys/fmx_release.keystore /c/orech_build/ext/keys/fmx.pass . && cat > README.md <<'EOF'
# Signing key for com.orech.files (file manager)

PRIVATE repository. Do not make it public and do not copy these files into any other repository.

- `fmx_release.keystore`: RSA 4096, alias `fmx`, valid until 2059.
- `fmx.pass`: the keystore and key password.
- Used by `build_orech.py` in `file-manager-x` through FMX_KEYSTORE and FMX_KEYPASS_FILE.
- If this key is lost, installed copies of the app cannot be updated; if it leaks, anyone can sign an update.
EOF
git init -q -b main && git add . && git -c core.autocrlf=false commit -q -m "Signing key for the file manager

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>" && gh repo create shmuel-sinay/orech-keys --private --source . --remote origin --push 2>&1 | tail -3
gh repo view shmuel-sinay/orech-keys --json visibility,url -q '.visibility + " " + .url'; git ls-files
```

## 161. Bash: Inspect /product space, template app, integrity list
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'df /product | tail -1; ls -laZ /product/app/OrechMockLoc/; cat /mnt/vendor/protect_s/orech/paths.txt; echo ---; ls /mnt/vendor/protect_s/orech | grep noenforce; getenforce'"
"$ADB" shell "pm list packages -f com.orech.files; dumpsys package com.orech.files | grep -E 'versionCode|codePath|flags=' | head -3"
```

## 162. Bash: Deploy file manager as built-in app in /product/app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
APK=/c/orech_build/ext/fmx/FileManager-Lite.apk
SHA=$(sha256sum $APK | cut -d' ' -f1); SZ=$(stat -c %s $APK); echo "$SHA $SZ"
printf '%s\n' 'set -e' 'B=/mnt/vendor/protect_s/orech' 'touch $B/noenforce' \
 "[ \"\$(wc -c < /data/local/tmp/orechfiles.apk)\" = \"$SZ\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/orechfiles.apk\" | sha256sum -c -" \
 'pm uninstall com.orech.files || true' \
 'mount -o remount,rw /product' \
 'mkdir -p /product/app/OrechFiles' \
 'cat /data/local/tmp/orechfiles.apk > /product/app/OrechFiles/OrechFiles.apk' \
 'chmod 755 /product/app/OrechFiles; chmod 644 /product/app/OrechFiles/OrechFiles.apk' \
 'chown root:root /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'chcon --reference=/product/app/OrechMockLoc /product/app/OrechFiles' \
 'chcon --reference=/product/app/OrechMockLoc/OrechMockLoc.apk /product/app/OrechFiles/OrechFiles.apk' \
 'sync' \
 "echo \"$SHA  /product/app/OrechFiles/OrechFiles.apk\" | sha256sum -c -" \
 'touch /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'rm -rf /data/system/package_cache' \
 'grep -qx /product/app/OrechFiles/OrechFiles.apk $B/paths.txt || echo /product/app/OrechFiles/OrechFiles.apk >> $B/paths.txt' \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'ls -laZ /product/app/OrechFiles; tail -3 $B/paths.txt' > /c/orech_build/t_v9/deploy_files.sh
"$ADB" push $APK /data/local/tmp/orechfiles.apk | tail -1
"$ADB" push C:/orech_build/t_v9/deploy_files.sh /data/local/tmp/deploy_files.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_files.sh'"
```

## 163. Bash: Retry deployment with Windows path for push
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" push C:/orech_build/ext/fmx/FileManager-Lite.apk /data/local/tmp/orechfiles.apk | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_files.sh'"
```

## 164. Bash: Finish built-in deployment steps manually
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SHA=2416a510aad42075aba7b2e6f0f16f1369649c5e71a36c95a3d93ff2149390f9
printf '%s\n' 'B=/mnt/vendor/protect_s/orech' \
 'ls -laZ /product/app/OrechFiles' \
 'chcon u:object_r:system_file:s0 /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'sync' \
 "echo \"$SHA  /product/app/OrechFiles/OrechFiles.apk\" | sha256sum -c -" \
 'touch /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'rm -rf /data/system/package_cache' \
 'grep -qx /product/app/OrechFiles/OrechFiles.apk $B/paths.txt || echo /product/app/OrechFiles/OrechFiles.apk >> $B/paths.txt' \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'ls -laZ /product/app/OrechFiles; tail -2 $B/paths.txt; ls $B | grep noenforce' > /c/orech_build/t_v9/deploy_files2.sh
"$ADB" push C:/orech_build/t_v9/deploy_files2.sh /data/local/tmp/deploy_files2.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_files2.sh'"
```

## 165. Bash: Reboot and verify the built-in app registered as system app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /data/local/tmp/orechfiles.apk /data/local/tmp/deploy_files.sh /data/local/tmp/deploy_files2.sh"
"$ADB" reboot
sleep 45
for i in $(seq 1 40); do B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5; done
echo "boot_completed=$B (~$((45+i*5))s)"
"$ADB" shell "pm list packages -f -s com.orech.files; dumpsys package com.orech.files | grep -E 'codePath|flags=|versionCode' | head -3; logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; tail -2 /mnt/vendor/protect_s/orech/integrity.log'"
```

## 166. Bash: Grant storage access and launch the built-in file manager
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "su -c 'appops set com.orech.files MANAGE_EXTERNAL_STORAGE allow; cmd appops write-settings'; cmd appops get com.orech.files MANAGE_EXTERNAL_STORAGE"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.orech.files/com.custom.files.MainActivity | tail -1"
sleep 4
"$ADB" exec-out screencap -p > "$SP/fm2.png"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'; pm list packages -d | grep -c com.orech.files"
```

## 167. Bash: Update handoff and changelog for built-in app and key repo
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read().rstrip()
assert s.endswith('</div>')
a='- **לא התחיל:** הפיכתו למובנה במערכת (תיקיית `/product/app`), רשימת היתרים, וסריקות מבוססות אירועים.'
assert a in s
s=s.replace(a,'''- **מובנה במערכת (בוצע):** `/product/app/OrechFiles/OrechFiles.apk` (root:root, 755/644, הקשר `system_file`, sha256 `2416a510...`, 190036 בייט). הועתק עם `cat` (הקובץ קטן, יש מקום), הוסר העותק המותקן הקודם (`pm uninstall`), `package_cache` נמחק, הנתיב נוסף ל-`paths.txt` בבקרת השלמות (`approved 17 files`), אותחל. אומת: `flags=[ SYSTEM ...]`, 0 קריסות, `check` ריק. הרשאת `MANAGE_EXTERNAL_STORAGE` ניתנה ב-appops (`allow`). `chcon --reference` לא נתמך במכשיר, להשתמש ב-`chcon u:object_r:system_file:s0`.
- **עדכון האפליקציה בעתיד:** לבנות עם `build_orech.py` (אותו מפתח), לכתוב ל-`/product/app/OrechFiles/` (`mount -o remount,rw /product`), `rm -rf /data/system/package_cache`, `approve` ואתחול. הקובץ בבקרת השלמות: בלי `approve` המכשיר יכבה כשהבקרה פעילה.
- **גיבוי המפתח:** מאגר **פרטי** `github.com/shmuel-sinay/orech-keys` (מכיל `fmx_release.keystore` ו-`fmx.pass`). המפתח לא הועלה לפורק `file-manager-x` כי הוא ציבורי. לא להפוך את מאגר המפתחות לציבורי.
- **עדיין לא התחיל:** סריקות מבוססות אירועים במקום סריקות הרקע, ובדיקת שאר המסכים בעיצוב החדש.''',1)
open(p,'w',encoding='utf-8',newline='\n').write(s+'\n')
print('ctrl',sum(1 for c in s if ord(c)<32 and c!='\n'))
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- סייר קבצים מובנה `com.orech.files` ב-`/product/app/OrechFiles` (ללא וידיאו וללא הרשאת התקנה, חתום במפתח קבוע, בבקרת השלמות); גיבוי מפתח במאגר פרטי. פירוט ב-HANDOFF סעיף 10.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
PYEOF
grep -c "מובנה במערכת (בוצע)" HANDOFF_M393.md
```

## 168. Bash: Collect current device and repo state for the handoff
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices | tail -2
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime | cut -d' ' -f1; getprop ro.build.display.id; getprop persist.sys.orech_id; logcat -d | grep -c 'FATAL EXCEPTION'; df -h /data | tail -1; pm list packages | grep -E 'orech|gpsconn|vlc|alphainventor'"
"$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16; ls /data/system/orech_allow_install 2>&1; ls /mnt/vendor/protect_s/orech | grep noenforce; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; wc -l < /data/system/orech_vguard/blacklist.txt'"
echo "--- orech_build git:"; cd /c/orech_build && git branch --show-current && git log --oneline -3 && git status --short | grep -vE 'png$|r[0-9]+\.sh$' | head -15
echo "--- fork:"; cd /c/orech_build/ext/fmx && git log --oneline -1 && git status --short | head -3
```

## 169. Bash: Collect current device and repo state for the handoff
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices | tail -2
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime | cut -d' ' -f1; getprop ro.build.display.id; getprop persist.sys.orech_id; logcat -d | grep -c 'FATAL EXCEPTION'; df -h /data | tail -1; pm list packages | grep -E 'orech|gpsconn|vlc|alphainventor'"
"$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16; ls /data/system/orech_allow_install 2>&1; ls /mnt/vendor/protect_s/orech | grep noenforce; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; wc -l < /data/system/orech_vguard/blacklist.txt'"
echo "--- orech_build git:"; cd /c/orech_build && git branch --show-current && git log --oneline -3 && git status --short | grep -vE 'png$|r[0-9]+\.sh$' | head -15
echo "--- fork:"; cd /c/orech_build/ext/fmx && git log --oneline -1 && git status --short | head -3
```

## 170. Bash: Read blacklist and disabled packages
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'echo BL=[\$(cat /data/system/orech_vguard/blacklist.txt | tr \"\\n\" \" \")]; tail -6 /data/system/orech_vguard/vguard.log'"
"$ADB" shell "pm list packages -d | grep -E 'alphainventor|vlc|orech'; echo ---; dumpsys package com.alphainventor.filemanager | grep -E 'lastUpdateTime|installerPackageName' | head -2"
```

## 171. Bash: Add pointers to the full session doc and verify no control chars
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
marker='לקרוא לפני שנוגעים במכשיר'
i=s.index(marker)
j=s.index('\n',i)
add='\n\n> **סשן אחרון (22.11.2026): תיעוד מלא ב-`SESSION_2026-11-22_FULL.md`** (מצב מכשיר, מה נעשה, החלטות, מה נשאר). קרא אותו מיד אחרי הקובץ הזה.'
if 'SESSION_2026-11-22_FULL.md' not in s:
    s=s[:j]+add+s[j:]
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CLAUDE.md',encoding='utf-8').read().rstrip()
if 'SESSION_2026-11-22_FULL.md' not in c:
    c+='\nאחרי זה לקרוא `SESSION_2026-11-22_FULL.md` (תיעוד הסשן האחרון ומה שנשאר).\n'
open('CLAUDE.md','w',encoding='utf-8',newline='\n').write(c)
print(sum(1 for ch in open('SESSION_2026-11-22_FULL.md',encoding='utf-8').read() if ord(ch)<32 and ch not in '\n'), sum(1 for ch in s if ord(ch)<32 and ch!='\n'))
PYEOF
cat CLAUDE.md; grep -n "SESSION_2026" HANDOFF_M393.md | head -2; wc -l SESSION_2026-11-22_FULL.md
```

## 172. Bash: Check the orech repo visibility, branches and layout
```
gh repo view shmuel-sinay/orech --json name,visibility,defaultBranchRef,pushedAt,description -q '.name+" | "+.visibility+" | "+.defaultBranchRef.name+" | "+.pushedAt+" | "+.description' 2>&1
gh api repos/shmuel-sinay/orech/branches --jq '.[].name' 2>&1 | head
echo --- tree; gh api repos/shmuel-sinay/orech/git/trees/main --jq '.tree[] | .type[0:1]+" "+.path' 2>&1 | head -30
cd /c/orech_build && git remote -v && git branch -a | head
```

## 173. Bash: Uninstall blacklisted user apps and clean the blacklist
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cat > /c/orech_build/t_v9/rmbl.sh <<'EOF'
BL=/data/system/orech_vguard/blacklist.txt
echo "before: [$(cat $BL | tr '\n' ' ')]"
for p in $(cat $BL); do
  if pm list packages -s | grep -qx "package:$p"; then echo "SKIP system app $p"; continue; fi
  echo "uninstall $p: $(pm uninstall $p 2>&1)"
done
: > /dev/null
for p in $(cat $BL); do pm path $p >/dev/null 2>&1 || sed -i "/^$p\$/d" $BL; done
echo "after: [$(cat $BL | tr '\n' ' ')]"
pm list packages | grep -E 'vlc|alphainventor' | wc -l
EOF
"$ADB" push C:/orech_build/t_v9/rmbl.sh /data/local/tmp/rmbl.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/rmbl.sh; rm -f /data/local/tmp/rmbl.sh'"
"$ADB" shell "su -c 'tail -2 /data/system/orech_vguard/vguard.log'; pm list packages | grep -E 'orech|gpsconn'"
```

## 174. PowerShell: Poll adb for the device, read-only
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$r=""
for($i=0;$i -lt 12;$i++){ $r=(& $adb devices | Select-Object -Skip 1 | Where-Object {$_.Trim()}) -join "; "; if($r -match 'device$'){break}; Start-Sleep 5 }
"after ~$($i*5)s devices=[$r]"
```

## 175. Bash: Uninstall blacklisted user apps and clean the blacklist
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell getprop sys.boot_completed
"$ADB" push C:/orech_build/t_v9/rmbl.sh /data/local/tmp/rmbl.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/rmbl.sh; rm -f /data/local/tmp/rmbl.sh'"
"$ADB" shell "su -c 'tail -2 /data/system/orech_vguard/vguard.log'; pm list packages | grep -E 'orech|gpsconn'"
```

## 176. Bash: Wait for boot, restore list and uninstall safely
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 40); do B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5; done
echo "boot_completed=$B (~$((i*5))s)"
sleep 10
cat > /c/orech_build/t_v9/rmbl2.sh <<'EOF'
BL=/data/system/orech_vguard/blacklist.txt
pm list packages >/dev/null 2>&1 || { echo "package service not ready, abort"; exit 1; }
# restore the two entries removed by the earlier faulty run, if missing
for p in org.videolan.vlc com.alphainventor.filemanager; do grep -qx "$p" $BL || echo "$p" >> $BL; done
echo "before: [$(cat $BL | tr '\n' ' ')]"
for p in $(cat $BL); do
  if pm list packages -s | grep -qx "package:$p"; then echo "SKIP system app $p"; continue; fi
  out=$(pm uninstall $p 2>&1); echo "uninstall $p: $out"
  case "$out" in *Success*) sed -i "/^$p\$/d" $BL;; esac
done
echo "after: [$(cat $BL | tr '\n' ' ')]"
echo "still installed: $(pm list packages | grep -cE 'vlc|alphainventor')"
EOF
"$ADB" push C:/orech_build/t_v9/rmbl2.sh /data/local/tmp/rmbl2.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/rmbl2.sh; rm -f /data/local/tmp/rmbl2.sh'"
"$ADB" shell "pm list packages | grep -E 'orech|gpsconn'; logcat -d | grep -c 'FATAL EXCEPTION'; su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
```

## 177. Bash: Clone the public orech repo and inspect docs layout
```
cd /c/orech_build/ext && GIT_TERMINAL_PROMPT=0 git clone --depth 1 https://github.com/shmuel-sinay/orech orech_pub 2>&1 | tail -1; cd orech_pub && git log --oneline -3 && ls docs research tools | head -40 && sed -n 1,25p README.md && git ls-files | wc -l
```

## 178. Bash: Inspect keys folder in the public repo
```
cd /c/orech_build/ext/orech_pub && ls -la research/keys | head; git ls-files research/keys | head; sed -n 1,25p docs/safe_edit_workflow.md; git ls-files | grep -iE 'keystore|\.jks|\.pk8|\.pem|\.pass|\.key' | head
```

## 179. Bash: Scan public doc for sensitive terms and control characters
```
cd /c/orech_build/ext/orech_pub && f=docs/m393_changes_and_editing.md && echo "forbidden-term scan:" && grep -nEi 'noenforce|allow_install|protect_s|BROM|fmx\.pass|password|kt3182319|g47ben|T123|0123456789ABCDEF|orech-keys|blacklist|allowlist|integrity|שלמות|root|boot\.img|bootloader|X89U1' $f || echo "clean"; python -I -c "
s=open(r'$f',encoding='utf-8').read(); print('ctrl chars:',sum(1 for c in s if ord(c)<32 and c!=chr(10)))"
```

## 180. Bash: Push the public doc and create the private full-docs repo
```
cd /c/orech_build/ext/orech_pub && git add docs/m393_changes_and_editing.md && git -c core.autocrlf=false commit -q -F - <<'EOF'
Add a public summary of the M393 changes and how to edit safely

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>
EOF
git push origin main 2>&1 | tail -2; git ls-remote origin main | cut -c1-12; git log --oneline -1
echo "=== private full docs"
D=/c/orech_build/ext/private_docs; mkdir -p $D && cd $D && cp "/c/פרוייקטים קלוד/zamir/HANDOFF_M393.md" "/c/פרוייקטים קלוד/zamir/CHANGELOG_M393.md" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md" . && cat > README.md <<'EOF'
# Private operational documentation (M393)

PRIVATE. Full handoff, changelog and session log for the device customisation. These files describe protection
mechanisms and how they work; do not publish them and do not copy them into the public `orech` repository.
The public, filtered summary is `docs/m393_changes_and_editing.md` in `shmuel-sinay/orech`.
EOF
git init -q -b main && git add . && git -c core.autocrlf=false commit -q -m "Full operational documentation for the M393 customisation

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>" && gh repo create shmuel-sinay/orech-private-docs --private --source . --remote origin --push 2>&1 | tail -2
gh repo view shmuel-sinay/orech-private-docs --json visibility,url -q '.visibility+" "+.url'; git ls-files
```

## 181. Bash: Prepare vguard v10 (uninstall instead of disable), syntax check only
```
cd /c/orech_build/deploy && python -I - <<'PYEOF'
s=open('orech_vguard_v8.sh',encoding='utf-8',newline='').read()
assert '\r' not in s
old=s[s.index('blacklist() {'):s.index('pkg_of() {')]
new='''blacklist() {
  pm list packages -s | grep -qx "package:$1" && { echo "$(date '+%F %T') system app, not removed: $1 ($2)" >> "$LOG"; return; }
  case "$1" in com.orech.*) echo "$(date '+%F %T') own app, not removed: $1 ($2)" >> "$LOG"; return ;; esac
  # removed for good; no permanent ban is kept, so the package can be installed again later if it is allowed
  out=$(pm uninstall "$1" 2>&1)
  echo "$(date '+%F %T') uninstalled $1 ($2): $out" >> "$LOG"
}
'''
s=s.replace(old,new,1)
loop='''  # keep blacklisted apps disabled
  for p in $(cat "$BL" 2>/dev/null); do
    pm list packages -d | grep -qx "package:$p" || { pm path "$p" >/dev/null 2>&1 && blacklist "$p" "re-enabled"; }
  done
'''
assert loop in s
s=s.replace(loop,'',1)
s=s.replace('# orech video guard v5.','# orech video guard v10 (candidate, not deployed): offending packages are uninstalled instead of disabled.',1)
assert 'pm uninstall' in s and 'keep blacklisted' not in s
open('orech_vguard_v10.sh','w',encoding='utf-8',newline='\n').write(s)
print(len(s.encode()))
PYEOF
export MSYS_NO_PATHCONV=1; ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" push C:/orech_build/deploy/orech_vguard_v10.sh /data/local/tmp/t10.sh | tail -1
"$ADB" shell "sh -n /data/local/tmp/t10.sh && echo SYNTAX_OK; rm -f /data/local/tmp/t10.sh"
diff /c/orech_build/deploy/orech_vguard_v8.sh /c/orech_build/deploy/orech_vguard_v10.sh | head -30
```

## 182. Bash: Update session docs and sync them to the private repo
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='SESSION_2026-11-22_FULL.md'
s=open(p,encoding='utf-8').read()
i=s.index('- **`org.videolan.vlc` ו-`com.alphainventor.filemanager`')
j=s.index('\n',i)
s=s[:i]+'- **VLC (`org.videolan.vlc`) ו-File Manager + (`com.alphainventor.filemanager`) הוסרו מהמכשיר** (`pm uninstall` החזיר `Success` לשניהם) והרשימה השחורה `blacklist.txt` ריקה. לא נשמר איסור קבוע: בהמשך אפשר להתקין חבילה כזו שוב אם היא מותרת (בדיקות ההתקנה במתקין עדיין חוסמות אפליקציות עם `video/` במניפסט, ולכן צריך לתת לחבילה היתר: להוסיף ל-`allowlist.txt` ולבצע את ההתקנה כשבדיקת ההתקנה מכובה בצורה מבוקרת).'+s[j:]
i=s.index('- ביומן `vguard.log` נרשמת אותה השבתה')
j=s.index('\n',i)
s=s[:i]+'- ההשבתה החוזרת כל כ-2 דקות ביומן נפסקה כי החבילות הוסרו. במדיניות העתידית (v10) חבילה שנחסמת מוסרת פעם אחת ולא נשארת ברשימה.'+s[j:]
k=s.index('1. **להחליט מה עושים ב-VLC')
l=s.index('\n',k)
s=s[:k]+'1. **v10 של `vguard` (מוכן, לא נפרס):** `C:/orech_build/deploy/orech_vguard_v10.sh` (תחביר נבדק ב-`sh -n` על המכשיר בלבד). הוראת המשתמש: חבילה שנחסמת **מוסרת** (`pm uninstall`) ולא רק מושבתת, בלי איסור קבוע; חבילות מערכת ו-`com.orech.*` מדולגות. לפרוס רק אחרי תיאום עם הסשן "המשך מסעיף 6" (נשלחה לו הודעה) ובדיקה סינתטית (למשל קובץ `libffmpeg_test.so` באפליקציית בדיקה ואימות שהיא מוסרת). הוסר גם הלולאה "keep blacklisted apps disabled". עדיין שיפור הסוללה (סעיף 2) בנפרד.'+s[l:]
s=s.replace('- ניתוח זמני (ניתן למחוק)','- **תיעוד ב-GitHub:** מאגר ציבורי `shmuel-sinay/orech` קיבל `docs/m393_changes_and_editing.md` (מסונן, בלי מנגנוני הגנה; commit `fdca116` ב-`main`). התיעוד המלא (HANDOFF, CHANGELOG, וקובץ הסשן) נמצא במאגר **פרטי** `shmuel-sinay/orech-private-docs`. לא לפרסם אותו בציבורי: הוא כולל פרטי בקרת השלמות ודרכי עקיפה. אחרי שינוי בקבצים כאן יש לסנכרן ידנית למאגר הפרטי.\n- ניתוח זמני (ניתן למחוק)',1)
bad=sum(1 for c in s if ord(c)<32 and c!='\n'); print('ctrl',bad); assert bad==0
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- VLC ו-File Manager + הוסרו (הרשימה השחורה ריקה, ללא איסור קבוע). הוכן `vguard v10` (הסרה במקום השבתה), לא נפרס. תיעוד ציבורי מסונן נדחף ל-`orech`, ותיעוד מלא למאגר פרטי.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
PYEOF
cp HANDOFF_M393.md CHANGELOG_M393.md SESSION_2026-11-22_FULL.md /c/orech_build/ext/private_docs/ && cd /c/orech_build/ext/private_docs && git add -A && git -c core.autocrlf=false commit -q -m "Sync documentation: removals, v10 plan, public/private split

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>" && git push 2>&1 | tail -1
grep -c "orech-private-docs" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md"
```

## 183. Bash: Compare deployed v10 with the file and check device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cd /c/orech_build/deploy && ls -l orech_vguard_v*.sh && sha256sum orech_vguard_v10.sh | cut -c1-16 && diff orech_vguard_v8.sh orech_vguard_v10.sh
echo "--- device:"; "$ADB" shell "getprop sys.boot_completed; su -c 'sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16; ls /data/system/orech_allow_install 2>&1; ls /mnt/vendor/protect_s/orech | grep noenforce; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; getprop init.svc.orech_vguard; cat /data/system/orech_vguard/blacklist.txt | wc -l'; logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 184. Bash: Pull deployed vguard and diff against v8
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'cp /vendor/bin/orech_vguard.sh /data/local/tmp/vg_dev.sh; chmod 644 /data/local/tmp/vg_dev.sh'"
"$ADB" pull /data/local/tmp/vg_dev.sh C:/orech_build/t_v9/vg_device_v10.sh | tail -1
"$ADB" shell "rm -f /data/local/tmp/vg_dev.sh"
cd /c/orech_build && sha256sum t_v9/vg_device_v10.sh | cut -c1-16; wc -c < t_v9/vg_device_v10.sh
diff deploy/orech_vguard_v8.sh t_v9/vg_device_v10.sh
```

## 185. Bash: Rename candidate to v11 and deploy it with backup and verification
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cd /c/orech_build/deploy && cp /c/orech_build/t_v9/vg_device_v10.sh orech_vguard_v10_deployed.sh && mv orech_vguard_v10.sh orech_vguard_v11.sh && sed -i '2s/.*/# orech video guard v11: offending packages are uninstalled instead of disabled (system apps and com.orech.* are skipped)./' orech_vguard_v11.sh && sed -n 1,3p orech_vguard_v11.sh
SHA=$(sha256sum orech_vguard_v11.sh | cut -d' ' -f1); SZ=$(stat -c %s orech_vguard_v11.sh); echo "$SHA $SZ"
printf '%s\n' 'set -e' 'B=/mnt/vendor/protect_s/orech' 'touch $B/noenforce' \
 "[ \"\$(wc -c < /data/local/tmp/t11.sh)\" = \"$SZ\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/t11.sh\" | sha256sum -c -" \
 'sh -n /data/local/tmp/t11.sh' \
 'mount -o remount,rw /vendor' \
 'cp /vendor/bin/orech_vguard.sh /data/local/tmp/orech_vguard.v10dev.bak' \
 'dd if=/data/local/tmp/t11.sh of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1' \
 "truncate -s $SZ /vendor/bin/orech_vguard.sh" 'sync' \
 "echo \"$SHA  /vendor/bin/orech_vguard.sh\" | sha256sum -c -" \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard' > /c/orech_build/t_v9/deploy_v11.sh
"$ADB" push C:/orech_build/deploy/orech_vguard_v11.sh /data/local/tmp/t11.sh | tail -1
"$ADB" push C:/orech_build/t_v9/deploy_v11.sh /data/local/tmp/deploy_v11.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_v11.sh; rm -f /data/local/tmp/t11.sh /data/local/tmp/deploy_v11.sh'"
```

## 186. Bash: Check connected adb devices
```
"C:/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe" devices 2>&1 | head
```

## 187. PowerShell: Locate gradle, JDK and build dir contents
```
Get-ChildItem "$env:USERPROFILE\.gradle\wrapper\dists" -Recurse -Filter gradle.bat | Select-Object -ExpandProperty FullName; Test-Path "C:\Program Files\Android\Android Studio\jbr\bin\java.exe"; Get-ChildItem C:\orech_build -Force | Select-Object -ExpandProperty Name
```

## 188. PowerShell: Run unit tests and show result summary
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain 2>&1 | Select-Object -Last 30
Get-ChildItem C:\orech_build\app\build\test-results\testDebugUnitTest\*.xml | ForEach-Object { Select-String -Path $_.FullName -Pattern '<testsuite ' | ForEach-Object { $_.Line } }
```

## 189. PowerShell: Run tests against deliberately broken alignment
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern 'FAILED|tests completed|BUILD'
```

## 190. Bash: Move appended line inside RTL div
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='CHANGELOG_M393.md'
s=open(p,encoding='utf-8').read()
i=s.rindex('</div>')
tail=s[i+6:]
s=s[:i].rstrip('\n')+'\n'+tail.strip('\n')+'\n\n</div>\n'
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
tail -4 CHANGELOG_M393.md
```

## 191. Bash: Read-only device state and integrity check
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices
"$ADB" shell getprop sys.boot_completed
"$ADB" shell getprop ro.serialno
"$ADB" shell getprop init.svc.orech_vguard
"$ADB" shell "su -c 'ls -la /mnt/vendor/protect_s/orech/; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; tail -5 /mnt/vendor/protect_s/orech/integrity.log'"
"$ADB" shell "dumpsys window | grep -E 'mDreamingLockscreen|isKeyguardShowing|KeyguardShowing' | head -3"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 192. Bash: Restart adb server and list USB devices
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" kill-server; sleep 2; "$ADB" start-server 2>&1 | tail -2; sleep 5; "$ADB" devices -l
powershell -NoProfile -Command "Get-PnpDevice -PresentOnly | Where-Object { \$_.FriendlyName -match 'ADB|Android|MTP|Composite|MediaTek|PreLoader|VCOM' } | Select-Object Status,FriendlyName | Format-Table -AutoSize"
```

## 193. Bash: Check adb, git remote and GitHub access
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"; "$ADB" devices -l
cd /c/orech_build && git remote -v && git branch --show-current && git log --oneline -3
gh auth status 2>&1 | head -5
gh repo view shmuel-sinay/orech --json name,visibility,defaultBranchRef,pushedAt 2>&1 | head -5
```

## 194. Bash: Read-only check of public GitHub repo
```
cd /c/orech_build && GIT_TERMINAL_PROMPT=0 git ls-remote https://github.com/shmuel-sinay/orech 2>&1 | head -5
```

## 195. PowerShell: Find seller module gradle task
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" tasks --all --console=plain 2>&1 | Select-String -Pattern 'seller.*assembleDebug|^:?seller:' | Select-Object -First 5
Get-Content settings.gradle.kts
```

## 196. PowerShell: Build seller APK and rerun unit tests
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :seller:assembleDebug :app:testDebugUnitTest --console=plain 2>&1 | Select-String -Pattern 'error|FAILED|BUILD|^e: '
Get-ChildItem C:\orech_build\seller\build\outputs\apk\debug\*.apk | Select-Object FullName,Length,LastWriteTime
Select-String -Path C:\orech_build\app\build\test-results\testDebugUnitTest\*.xml -Pattern '<testsuite ' | ForEach-Object { $_.Line }
```

## 197. Bash: Copy APK and run read-only device checks
```
cp /c/orech_build/seller/build/outputs/apk/debug/seller-debug.apk "/c/פרוייקטים קלוד/zamir/seller-debug.apk" && ls -l "/c/פרוייקטים קלוד/zamir/seller-debug.apk"
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell getprop sys.boot_completed
"$ADB" shell getprop init.svc.orech_vguard
"$ADB" shell "su -c 'ls /mnt/vendor/protect_s/orech/; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell "dumpsys window | grep -iE 'mDreamingLockscreen|showing=' | head -3"
```

## 198. Bash: Install seller APK with temporary allow file
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'touch /data/system/orech_allow_install'"
"$ADB" install -r "C:/פרוייקטים קלוד/zamir/seller-debug.apk" 2>&1 | tail -3
"$ADB" shell "su -c 'rm -f /data/system/orech_allow_install; ls /data/system/orech_allow_install 2>&1'"
"$ADB" shell pm list packages | grep -i orech
"$ADB" shell "cmd package resolve-activity --brief -c android.intent.category.LAUNCHER com.orech.seller 2>&1 | tail -2"
```

## 199. Bash: Tap grant on root dialog and screenshot
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
sleep 4; "$ADB" shell input tap 122 632; sleep 3
"$ADB" exec-out screencap -p > "C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad/seller2.png"
```

## 200. Bash: Inspect raw bytes of the id-writing lines
```
sed -n '108,111p' /c/orech_build/seller/src/main/java/com/orech/seller/SellerActivity.kt | od -c | head -20; cd /c/orech_build && git diff --stat -- seller | tail -2; git log -3 --format='%h %s' -- seller
```

## 201. Bash: Open device-id dialog and type test id
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell input tap 210 258; sleep 2
"$ADB" shell input text T123; sleep 1
"$ADB" exec-out screencap -p > "$SP/seller3.png"
```

## 202. Bash: Save id in dialog and inspect results
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell input tap 88 543; sleep 3
"$ADB" exec-out screencap -p > "$SP/seller4.png"
"$ADB" shell "su -c 'cat /mnt/vendor/nvdata/orech_id | od -c | head -2; getprop persist.sys.orech_id'"
"$ADB" shell "su -c 'cat /sdcard/Android/data/com.orech.seller/files/sales.csv 2>&1'"
```

## 203. PowerShell: Rebuild seller APK and copy to zamir
```
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk" }
Set-Location C:\orech_build
& "C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat" :seller:assembleDebug --console=plain 2>&1 | Select-String -Pattern '^e: |FAILED|BUILD'
Copy-Item C:\orech_build\seller\build\outputs\apk\debug\seller-debug.apk "C:\פרוייקטים קלוד\zamir\seller-debug.apk" -Force
(Get-Item "C:\פרוייקטים קלוד\zamir\seller-debug.apk").LastWriteTime
```

## 204. Bash: Verify serial in Settings, integrity, crashes
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -a android.settings.DEVICE_INFO_SETTINGS >/dev/null; sleep 3
"$ADB" shell "uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; cat /sdcard/ui.xml" | grep -o 'text="[^"]*"' | grep -iE 'T123|סידורי|serial|Zamir' | head
"$ADB" exec-out screencap -p > "$SP/about.png"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech/ | grep noenforce'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 205. Bash: Update handoff and changelog with results
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
old='(א) אפליקציית המוכר על המכשיר:'
assert old in s
s=s.replace(old,'(א) **בוצע ונבדק** (מספר בדיקה `T123` נשאר על המכשיר ובקובץ `sales.csv`; אפשר לשנות מהאפליקציה; שעון המכשיר שגוי ולכן התאריך ב-CSV שגוי): נמצא ותוקן באג, `$id` חסר בפקודות הכתיבה, כך שהמספר לא נכתב מעולם (שוחזר על המכשיר לפני התיקון: `orech_id` ריק, "השמירה נכשלה"). אחרי התיקון המספר ב-`/mnt/vendor/nvdata/orech_id`, ב-`persist.sys.orech_id`, ב-`sales.csv` ובהגדרות. עוד תוקן ב-SellerActivity: הבקרה מופעלת ואפליקציית המוכר מסירה את עצמה רק אם `approve` הצליח ו-`check` החזיר ריק (קודם הופעלה גם בכשל, ואז כיבוי בעלייה הבאה). **תיקון זה לא נבדק על מכשיר** ושלב "החל" לא הורץ. מקור: `seller-debug.apk` בתיקייה. תיאור המשימה המקורי: אפליקציית המוכר על המכשיר:',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read()
i=c.rindex('</div>')
c=c[:i].rstrip('\n')+'\n- SellerActivity: תוקן `$id` חסר בכתיבת מספר המכשיר (נבדק על המכשיר: nvdata, prop, sales.csv, הגדרות). בנוסף: הפעלת הבקרה והסרה עצמית רק אחרי `approve` מוצלח ו-`check` ריק (לא נבדק על מכשיר). `seller-debug.apk` נבנה מחדש.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
EOF
grep -c 'T123' HANDOFF_M393.md CHANGELOG_M393.md
```

## 206. Bash: Inspect vguard state and scan logic
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'ls /data/system/orech_vguard/; echo ---; wc -l /data/system/orech_vguard/*; echo ---; tail -8 /data/system/orech_vguard/vguard.log; echo ---; grep -n \"data/data\|blacklist\|libffmpeg\|\\.so\" /vendor/bin/orech_vguard.sh 2>/dev/null | head -40'"
"$ADB" shell "su -c 'ls /vendor/bin/ | grep -i orech; ls /system/bin | grep -i orech'"
```

## 207. Bash: Grep vguard script for blacklist logic
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'F=\$(ls /vendor/bin/orech_vguard.sh /system/bin/orech_vguard.sh 2>/dev/null | head -1); echo \$F; wc -l \$F; grep -n -iE \"data/data|blacklist|libffmpeg|chmod|force-stop|log \" \$F | head -50'"
```

## 208. Bash: Plant dummy decoder lib and wait for vguard detection
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'mkdir -p /data/data/com.orech.seller/files && echo dummy > /data/data/com.orech.seller/files/libffmpeg_test.so && ls -l /data/data/com.orech.seller/files/'"
for i in 1 2 3 4 5 6 7 8; do
  sleep 10
  R=$("$ADB" shell "su -c 'grep -c com.orech.seller /data/system/orech_vguard/blacklist.txt'" | tr -d '\r')
  [ "$R" != "0" ] && break
done
echo "waited ~$((i*10))s, blacklist hits=$R"
"$ADB" shell "su -c 'cat /data/system/orech_vguard/blacklist.txt; tail -3 /data/system/orech_vguard/vguard.log; ls -l /data/data/com.orech.seller/files/libffmpeg_test.so; pm list packages -d | grep seller'"
```

## 209. Bash: Check /data/data symlink and scan loop
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'ls -ld /data/data /data/user/0; readlink /data/data; echo ---; find /data/data -maxdepth 5 -type f -name libffmpeg_test.so 2>&1 | head -3; echo ---find-slash; find /data/data/ -maxdepth 5 -type f -name libffmpeg_test.so 2>&1 | head -3; echo ---; sed -n 60,96p /vendor/bin/orech_vguard.sh; echo ....; sed -n 112,136p /vendor/bin/orech_vguard.sh'"
```

## 210. Bash: Keep polling for vguard detection
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 14); do
  sleep 10
  R=$("$ADB" shell "su -c 'grep -c com.orech.seller /data/system/orech_vguard/blacklist.txt'" | tr -d '\r')
  [ "$R" != "0" ] && break
done
echo "extra wait ~$((i*10))s, blacklist hits=$R"
"$ADB" shell "su -c 'cat /data/system/orech_vguard/blacklist.txt; tail -3 /data/system/orech_vguard/vguard.log; ls -l /data/data/com.orech.seller/files/libffmpeg_test.so; pm list packages -d | grep seller'"
```

## 211. Bash: Clean up blacklist test and re-enable app
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'grep -vx com.orech.seller /data/system/orech_vguard/blacklist.txt > /data/local/tmp/bl.new; cat /data/local/tmp/bl.new > /data/system/orech_vguard/blacklist.txt; rm -f /data/local/tmp/bl.new /data/data/com.orech.seller/files/libffmpeg_test.so; pm enable com.orech.seller; echo BL_LINES=\$(wc -l < /data/system/orech_vguard/blacklist.txt); pm list packages -d | grep -c seller; ls /data/data/com.orech.seller/files/'"
sleep 3
"$ADB" shell am start -n com.orech.seller/.SellerActivity | tail -1
```

## 212. Bash: Find where Kosher Tech appears in device info
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell getprop | grep -iE 'kosher|ro.build.display.id|ro.build.id\]|ro.product.model|ro.build.version.incremental|ro.vendor.build.fingerprint|ro.build.description' 
"$ADB" shell am start -a android.settings.DEVICE_INFO_SETTINGS >/dev/null; sleep 2
"$ADB" shell input swipe 200 800 200 200 300; sleep 1
"$ADB" shell input swipe 200 800 200 200 300; sleep 1
"$ADB" exec-out screencap -p > "$SP/about2.png"
ls -l /c/orech_build/tools 2>/dev/null | head -20
```

## 213. Bash: Inspect build.prop size and lines
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"; mkdir -p "$D"
"$ADB" shell "su -c 'ls -l /system/build.prop; wc -c /system/build.prop; sha256sum /system/build.prop; df /system | tail -1'"
"$ADB" shell "su -c 'cat /system/build.prop'" > "$D/build.prop.orig"
wc -lc "$D/build.prop.orig"; grep -n 'display.id' "$D/build.prop.orig"; grep -c '^#' "$D/build.prop.orig"; grep -c '^$' "$D/build.prop.orig"
file "$D/build.prop.orig"; head -c 300 "$D/build.prop.orig"
```

## 214. Bash: Build and validate new build.prop locally
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"
"$ADB" exec-out "su -c 'cat /system/build.prop'" > "$D/build.prop.orig"
python - <<'EOF'
import hashlib
D='C:/orech_build/t_bp/'
b=open(D+'build.prop.orig','rb').read()
print(len(b), hashlib.sha256(b).hexdigest())
assert len(b)==8103 and hashlib.sha256(b).hexdigest()=='5ce02a1d8830e8eeeee2f1414cf3ba9b859d59542efbee6a9ddeedc9e7c28005'
assert b'\r' not in b and b.endswith(b'\n')
lines=b.decode('ascii').split('\n')[:-1]
val='כושר טק - כל הזכויות שמורות | <email hidden>'
newline='ro.build.display.id='+val
out=[];need=len(newline.encode())-len('ro.build.display.id=Kosher Tech')
print('growth needed',need)
saved=0
for l in lines:
    if l.startswith('ro.build.display.id='):
        out.append(newline); continue
    out.append(l)
# drop comment lines (keep first line only) until we've saved enough
res=[];dropped=0
for l in out:
    if l.startswith('#') and dropped<need+40 and l!=out[0]:
        dropped+=len(l.encode())+1; continue
    res.append(l)
data=('\n'.join(res)+'\n').encode('utf-8')
print('new size',len(data),'dropped',dropped)
assert len(data)<=8103
# validate: every non-comment line is key=value, property keys preserved
def props(ls): return {l.split('=',1)[0]:l.split('=',1)[1] for l in ls if l and not l.startswith('#')}
po,pn=props(lines),props(res)
assert set(po)==set(pn)
diff=[k for k in po if po[k]!=pn[k]]
print('changed props:',diff)
assert diff==['ro.build.display.id']
data.decode('utf-8')
open(D+'build.prop.new','wb').write(data)
print('OK')
EOF
```

## 215. Bash: Pull exact build.prop and build validated new one
```
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"
"$ADB" shell "su -c 'cp /system/build.prop /data/local/tmp/bp.orig; chmod 644 /data/local/tmp/bp.orig'"
"$ADB" pull /data/local/tmp/bp.orig "$D/build.prop.orig" | tail -1
python - <<'EOF'
import hashlib
D='C:/orech_build/t_bp/'
b=open(D+'build.prop.orig','rb').read()
print(len(b), hashlib.sha256(b).hexdigest())
assert len(b)==8103 and hashlib.sha256(b).hexdigest()=='5ce02a1d8830e8eeeee2f1414cf3ba9b859d59542efbee6a9ddeedc9e7c28005'
assert b'\r' not in b and b.endswith(b'\n')
lines=b.decode('ascii').split('\n')[:-1]
val='כושר טק - כל הזכויות שמורות | <email hidden>'
newline='ro.build.display.id='+val
need=len(newline.encode())-len('ro.build.display.id=Kosher Tech')
print('growth needed',need)
out=[newline if l.startswith('ro.build.display.id=') else l for l in lines]
res=[];dropped=0
for idx,l in enumerate(out):
    if l.startswith('#') and idx>0 and dropped<need+40:
        dropped+=len(l.encode())+1; continue
    res.append(l)
data=('\n'.join(res)+'\n').encode('utf-8')
print('new size',len(data),'dropped',dropped)
assert len(data)<=8103
def props(ls): return {l.split('=',1)[0]:l.split('=',1)[1] for l in ls if l and not l.startswith('#')}
po,pn=props(lines),props(res)
assert set(po)==set(pn)
diff=[k for k in po if po[k]!=pn[k]]
print('changed props:',diff)
assert diff==['ro.build.display.id']
open(D+'build.prop.new','wb').write(data)
print('OK')
EOF
```

## 216. Bash: Write new build.prop with dd and verify sha256
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D="C:/orech_build/t_bp"
SHA=$(sha256sum "$D/build.prop.new" | cut -d' ' -f1); SZ=$(stat -c %s "$D/build.prop.new"); echo "$SHA $SZ"
printf '%s\n' 'set -e' \
 'mount -o remount,rw / 2>&1 || true' \
 'cp /system/build.prop /data/local/tmp/bp.backup.device' \
 "[ \"\$(wc -c < /data/local/tmp/bp.new)\" = \"$SZ\" ] || { echo SIZE_MISMATCH_SRC; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/bp.new\" | sha256sum -c -" \
 'dd if=/data/local/tmp/bp.new of=/system/build.prop conv=notrunc 2>&1' \
 "truncate -s $SZ /system/build.prop" \
 'sync' \
 "echo \"$SHA  /system/build.prop\" | sha256sum -c -" \
 'ls -l /system/build.prop' > "$D/write_bp.sh"
"$ADB" push "$D/build.prop.new" /data/local/tmp/bp.new | tail -1
"$ADB" push "$D/write_bp.sh" /data/local/tmp/write_bp.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/write_bp.sh'"
```

## 217. Bash: Approve integrity, reboot, verify boot and prop
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve; echo APPROVE_EXIT=\$?; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech/ | grep noenforce; rm -f /data/local/tmp/bp.new /data/local/tmp/bp.orig /data/local/tmp/write_bp.sh'"
"$ADB" reboot
sleep 45
for i in $(seq 1 30); do
  B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5
done
echo "boot_completed=$B after ~$((45+i*5))s"
"$ADB" shell getprop ro.build.display.id
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech/ | grep noenforce'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell getprop init.svc.orech_vguard
```

## 218. Bash: Open About phone and screenshot after reboot
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -a android.settings.DEVICE_INFO_SETTINGS | tail -1; sleep 3
"$ADB" shell input swipe 200 800 200 200 300; sleep 1; "$ADB" shell input swipe 200 800 200 200 300; sleep 1
"$ADB" exec-out screencap -p > "$SP/about3.png"
```

## 219. Bash: Update docs and save original build.prop
```
cd "/c/פרוייקטים קלוד/zamir" && cp /c/orech_build/t_bp/build.prop.orig build.prop.orig_before_branding && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='Build `Kosher Tech`'
assert a in s
s=s.replace(a,'Build (`ro.build.display.id`) = `כושר טק - כל הזכויות שמורות | <email hidden>` (היה `Kosher Tech`; הקובץ המקורי ב-`zamir\\build.prop.orig_before_branding`, sha256 `5ce02a1d...`, 8103 בייט; בגרסה החדשה 8040 בייט, הוסרו שורות הערה כדי לא לחרוג מהגודל המקורי)',1)
b='(ב) הרשימה השחורה:'
assert b in s
s=s.replace(b,'(ב) **נבדק בצורה סינתטית** (קובץ דמה `libffmpeg_test.so` ב-`/data/data` של אפליקציית המוכר, לא אפליקציה אמיתית): הזיהוי עבד, `chmod 000`, כניסה ל-`blacklist.txt`, שורה ב-`vguard.log`, השבתה. הזיהוי לקח כ-95 שניות ולא "תוך דקה" (סריקת `/data/data` רצה רק ב-`apply_lists`, אחת ל-30 מחזורים). אחרי הבדיקה: להוציא מהרשימה לפני `pm enable`, אחרת השירות משבית שוב. הנוסח המקורי של המשימה: הרשימה השחורה:',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read()
i=c.rindex('</div>')
c=c[:i].rstrip('\n')+'\n- מסך "מידע על הטלפון": ערך "מספר Build" שונה ל-`כושר טק - כל הזכויות שמורות | <email hidden>` דרך `/system/build.prop` (dd + truncate, אימות sha256, `approve`, אתחול; עלייה תקינה, 0 קריסות, `check` ריק). הרשימה השחורה נבדקה סינתטית ונוקתה.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
EOF
grep -c 'כושר טק - כל' HANDOFF_M393.md CHANGELOG_M393.md
```

## 220. Bash: Find APK with INSTALL_PACKAGES permission
```
export MSYS_NO_PATHCONV=1
AAPT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/*/aapt2.exe | tail -1); echo $AAPT
for a in "C:/פרוייקטים קלוד/zamir/seller-debug.apk" /c/orech_build/app/build/outputs/apk/debug/app-debug.apk; do
  echo "== $a"; "$AAPT" dump permissions "$a" 2>&1 | grep -iE 'INSTALL_PACKAGES|package:' | head -3
done
ls /c/orech_build/deploy | head -20
```

## 221. Bash: Build signed test APK with video mime type
```
export MSYS_NO_PATHCONV=1
BT=$(ls -d /c/Users/user1/AppData/Local/Android/Sdk/build-tools/*/ | tail -1); AAPT="${BT}aapt2.exe"; AS="${BT}apksigner.bat"
JAR=$(ls /c/Users/user1/AppData/Local/Android/Sdk/platforms/android-34/android.jar)
W=/c/orech_build/t_blocktest; mkdir -p $W && cd $W
cat > AndroidManifest.xml <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.orech.blocktest">
  <application android:label="blocktest">
    <activity android:name=".A" android:exported="true">
      <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="video/*"/>
      </intent-filter>
    </activity>
  </application>
</manifest>
EOF
"$AAPT" link -o blocktest_unsigned.apk --manifest AndroidManifest.xml -I "$JAR" --min-sdk-version 26 --target-sdk-version 33 2>&1 | tail -2
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"; export PATH="$JAVA_HOME/bin:$PATH"
ls ~/.android/debug.keystore
"$AS" sign --ks ~/.android/debug.keystore --ks-pass pass:android --out blocktest.apk blocktest_unsigned.apk 2>&1 | tail -2
ls -l blocktest.apk
"$AAPT" dump xmltree --file AndroidManifest.xml blocktest.apk | grep -c 'video/'
```

## 222. Bash: Find android.jar location
```
ls /c/Users/user1/AppData/Local/Android/Sdk/platforms/android-34 /c/Users/user1/AppData/Local/Android/Sdk/platforms/android-36 | head -20
```

## 223. Bash: Build signed test APK using Windows paths
```
unset MSYS_NO_PATHCONV
SDK="C:/Users/user1/AppData/Local/Android/Sdk"
BTV=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="$SDK/build-tools/$BTV/aapt2.exe"; AS="$SDK/build-tools/$BTV/apksigner.bat"
JAR="$SDK/platforms/android-34/android.jar"
cd /c/orech_build/t_blocktest
"$AAPT" link -o blocktest_unsigned.apk --manifest AndroidManifest.xml -I "$JAR" --min-sdk-version 26 --target-sdk-version 33 2>&1 | tail -2
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$AS" sign --ks "C:/Users/user1/.android/debug.keystore" --ks-pass pass:android --out blocktest.apk blocktest_unsigned.apk 2>&1 | tail -2
ls -l blocktest.apk
"$AAPT" dump xmltree --file AndroidManifest.xml blocktest.apk | grep -c 'video/'
```

## 224. Bash: Open test APK in package installer UI
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "su -c 'ls /data/system/orech_allow_install 2>&1'"
"$ADB" push C:/orech_build/t_blocktest/blocktest.apk /sdcard/Download/blocktest.apk | tail -1
"$ADB" shell am start -a android.intent.action.VIEW -d file:///sdcard/Download/blocktest.apk -t application/vnd.android.package-archive 2>&1 | tail -2
sleep 4
"$ADB" exec-out screencap -p > "$SP/inst1.png"
"$ADB" shell pm list packages | grep -c blocktest
```

## 225. Bash: Launch installer via INSTALL_PACKAGE action
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell cmd package query-activities -a android.intent.action.INSTALL_PACKAGE 2>&1 | grep -E 'name=|packageName' | head -6
"$ADB" shell am start -a android.intent.action.INSTALL_PACKAGE -d file:///sdcard/Download/blocktest.apk 2>&1 | tail -2
sleep 4
"$ADB" exec-out screencap -p > "$SP/inst2.png"
"$ADB" shell pm list packages | grep -c blocktest
```

## 226. Bash: Inspect package installer packages and state
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell pm list packages | grep -iE 'installer'
"$ADB" shell "dumpsys package com.google.android.packageinstaller 2>/dev/null | grep -iE 'Activity Resolver|InstallStart|InstallInstalling|enabled=|android.intent.action.(VIEW|INSTALL)' | head -15"
"$ADB" shell "pm list packages -d | grep -iE 'installer'"
```

## 227. Bash: Start installer activity explicitly with APK
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -n com.google.android.packageinstaller/com.android.packageinstaller.InstallStart -a android.intent.action.VIEW -d file:///sdcard/Download/blocktest.apk 2>&1 | tail -3
sleep 4
"$ADB" exec-out screencap -p > "$SP/inst3.png"
"$ADB" shell pm list packages | grep -c blocktest
"$ADB" shell "logcat -d -t 60 | grep -iE 'orech|installer|InstallStart' | tail -8"
```

## 228. Bash: Check CE storage unlock state and focused app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell getprop sys.user.0.ce_available
"$ADB" shell "dumpsys user | grep -iE 'State:|RUNNING' | head -3"
"$ADB" shell "dumpsys window | grep -E 'mCurrentFocus|mFocusedApp' | head -2"
```

## 229. Bash: Inspect installer component state
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "pm dump com.google.android.packageinstaller | grep -iE 'disabledComponents|enabledComponents|versionName|codePath|flags=|InstallStart' | head -12"
"$ADB" shell "cmd package resolve-activity --brief -a android.intent.action.INSTALL_PACKAGE -d content://x/y.apk -t application/vnd.android.package-archive 2>&1 | tail -3"
"$ADB" shell "pm list packages -d | grep -c installer"
```

## 230. Bash: Find MediaStore id for the test APK
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "cmd media_session >/dev/null 2>&1; am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Download/blocktest.apk >/dev/null 2>&1"; sleep 3
"$ADB" shell "content query --uri content://media/external/file --projection _id:_data --where \"_data='/storage/emulated/0/Download/blocktest.apk'\"" 2>&1 | head -3
"$ADB" shell "content query --uri content://media/external/downloads --projection _id:_data --where \"_data='/storage/emulated/0/Download/blocktest.apk'\"" 2>&1 | head -3
```

## 231. Bash: Check logcat for installer launch result
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d -t 400 | grep -iE 'InstallStart|packageinstaller|orech|blocktest|ActivityTaskManager: START' | tail -12"
"$ADB" shell "dumpsys window | grep -E 'mCurrentFocus' | head -1"
```

## 232. Bash: Launch installer with wait and capture focus
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" logcat -c
"$ADB" shell am start -W -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/1000011095 -t application/vnd.android.package-archive --grant-read-uri-permission 2>&1 | tail -6
for i in 1 2 3; do "$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"; sleep 1; done
"$ADB" exec-out screencap -p > "$SP/inst5.png"
"$ADB" shell "logcat -d | grep -iE 'InstallStart|packageinstaller|orech' | tail -10"
```

## 233. Bash: Wait for device to return to adb
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 24); do
  S=$("$ADB" get-state 2>/dev/null | tr -d '\r'); [ "$S" = "device" ] && break; sleep 5
done
echo "state=$S after ~$((i*5))s"
"$ADB" devices
"$ADB" shell "getprop sys.boot_completed; uptime; getprop init.svc.orech_vguard" 2>&1 | head -4
```

## 234. Bash: Diagnose unexpected reboot cause
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 30); do B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5; done
echo boot_completed=$B
"$ADB" shell "getprop sys.boot.reason; getprop ro.boot.bootreason; getprop persist.sys.boot.reason; getprop persist.sys.boot.reason.history | head -3"
echo "--- integrity.log tail"; "$ADB" shell "su -c 'tail -6 /mnt/vendor/protect_s/orech/integrity.log; ls /mnt/vendor/protect_s/orech | grep noenforce; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
echo "--- crash buffer"; "$ADB" shell "logcat -b crash -d | tail -15"
echo "--- vguard"; "$ADB" shell "su -c 'tail -3 /data/system/orech_vguard/vguard.log'"
```

## 235. Bash: Check who rebooted: terminal and seller app state
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell pm list packages | grep -E 'seller|blocktest'
"$ADB" shell "su -c 'ls -l /mnt/vendor/protect_s/orech/; ls /data/system/orech_allow_install 2>&1'"
"$ADB" shell "dumpsys package com.orech.seller | grep -E 'lastUpdateTime|firstInstallTime'"
```

## 236. Bash: Clean up test APK and document findings
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /sdcard/Download/blocktest.apk; am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Download/blocktest.apk >/dev/null 2>&1; ls /sdcard/Download/blocktest.apk 2>&1; pm list packages | grep -c blocktest"
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='(ג) מסך חסימת המתקין בעיצוב החדש.'
assert a in s
s=s.replace(a,'(ג) מסך חסימת המתקין בעיצוב החדש: **לא נבדק**. נבנה APK בדיקה חתום עם `video/*` במניפסט (`C:\\orech_build\\t_blocktest\\blocktest.apk`, בנוי עם aapt2+apksigner ומפתח debug). פתיחה במתקין: `file://` לא נפתר; צריך `content://`: להעתיק ל-`/sdcard/Download`, לאנדקס (`am broadcast MEDIA_SCANNER_SCAN_FILE`), לשלוף `_id` ב-`content query --uri content://media/external/file`, ואז `am start -W -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/<id> -t application/vnd.android.package-archive --grant-read-uri-permission` (חזר `Status: ok`, `InstallStart`, אך המסך לא נצפה). **בזמן הבדיקה סשנים אחרים עבדו על אותו מכשיר ואתחלו אותו** (`reboot,shell`), לכן לבדוק רק כשאין סשן אחר פעיל על המכשיר. ',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
grep -c 't_blocktest' HANDOFF_M393.md
```

## 237. Bash: Check handoff path text for escape damage
```
cd "/c/פרוייקטים קלוד/zamir" && grep -c 'לא נבדק\*\*. נבנה APK' HANDOFF_M393.md; grep -o 'C:.\{0,3\}orech_build.\{0,25\}blocktest.apk' HANDOFF_M393.md | cat -A | head -2
```

## 238. Bash: Fix mangled path and check for control chars
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
bad='C:\\\\orech_build\t_blocktest\x08locktest.apk'
print(bad in s)
s=s.replace(bad,'C:/orech_build/t_blocktest/blocktest.apk')
assert '\x08' not in s
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
grep -c 'C:/orech_build/t_blocktest/blocktest.apk' HANDOFF_M393.md; python -c "
s=open('HANDOFF_M393.md',encoding='utf-8').read(); print('ctrl chars:', sum(1 for c in s if ord(c)<32 and c not in '\n\t'))"
```

## 239. Bash: Locate control characters in handoff
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
s=open('HANDOFF_M393.md',encoding='utf-8').read()
for i,c in enumerate(s):
    if ord(c)<32 and c not in '\n':
        print(i,repr(c),repr(s[max(0,i-35):i+25]))
EOF
```

## 240. Bash: Repair both mangled paths and verify
```
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
s=s.replace('zamir\x08uild.prop.orig_before_branding','zamir/build.prop.orig_before_branding')
s=s.replace('C:\\\\orech_build\t_blocktest\x08locktest.apk','C:/orech_build/t_blocktest/blocktest.apk')
bad=[(i,c) for i,c in enumerate(s) if ord(c)<32 and c not in '\n']
print('remaining ctrl chars:',bad)
assert not bad
open(p,'w',encoding='utf-8',newline='\n').write(s)
EOF
grep -o 'zamir/build.prop.orig_before_branding' HANDOFF_M393.md | head -1; grep -o 'C:/orech_build/t_blocktest/blocktest.apk' HANDOFF_M393.md | head -1
python -c "
s=open('CHANGELOG_M393.md',encoding='utf-8').read(); print('changelog ctrl chars:', sum(1 for c in s if ord(c)<32 and c!='\n'))"
```

## 241. Bash: Read-only device and integrity status
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices -l
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime; getprop ro.boot.bootreason; getprop ro.build.display.id; logcat -d | grep -c 'FATAL EXCEPTION'" 2>&1 | head -6
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; tail -4 /mnt/vendor/protect_s/orech/integrity.log'" 2>&1
"$ADB" shell pm list packages | grep -iE 'orech|gps|mock'
```

## 242. PowerShell: Check USB device presence and fastboot
```
Get-PnpDevice -PresentOnly -ErrorAction SilentlyContinue | Where-Object { $_.FriendlyName -match 'ADB|Android|MTK|MediaTek|Preloader|VCOM|Fastboot|Bootloader' -or $_.InstanceId -match 'VID_0E8D|VID_18D1' } | Select-Object Status,FriendlyName,InstanceId | Format-Table -AutoSize | Out-String -Width 200
& "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices
& "C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\fastboot.exe" devices
```

## 243. PowerShell: Monitor USB enumeration and restart adb server
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
1..12 | ForEach-Object { $p = Get-PnpDevice -PresentOnly -ErrorAction SilentlyContinue | Where-Object { $_.InstanceId -match 'VID_0E8D&PID_201C' }; "{0:HH:mm:ss} present={1}" -f (Get-Date), [bool]$p; Start-Sleep 3 }
& $adb kill-server; Start-Sleep 2; & $adb start-server 2>&1 | Select-Object -Last 1; Start-Sleep 4; & $adb devices -l
Get-WinEvent -FilterHashtable @{LogName='System'; StartTime=(Get-Date).AddMinutes(-15)} -ErrorAction SilentlyContinue | Where-Object { $_.ProviderName -match 'Kernel-PnP|usbhub|USB' } | Select-Object -First 8 TimeCreated,Id,Message | Format-List | Out-String -Width 160
```

## 244. PowerShell: Re-check adb devices and background output
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
& $adb devices -l
Get-Content "C:\Users\user1\AppData\Local\Temp\claude\C----------------\b4a1b9f4-acb5-4092-933f-897217e378d3\tasks\bspaqkf5w.output" -Tail 12
```

## 245. PowerShell: List processes that may hold the USB ADB interface
```
Get-Process | Where-Object { $_.ProcessName -match 'adb|scrcpy|flash|SP_|MTK|mtk|qemu|HiSuite|android|studio64|python|node' } | Select-Object Id,ProcessName,StartTime,Path | Format-Table -AutoSize | Out-String -Width 220
Get-NetTCPConnection -LocalPort 5037 -ErrorAction SilentlyContinue | Select-Object LocalAddress,State,OwningProcess | Format-Table | Out-String
```

## 246. PowerShell: List recently modified project files
```
$cut=(Get-Date).AddHours(-4)
Get-ChildItem C:\orech_build -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.LastWriteTime -gt $cut -and $_.FullName -notmatch '\\(build|\.gradle|\.git|t_bp|t_blocktest)\\' -and $_.Extension -notin '.png','.class','.dex' } | Sort-Object LastWriteTime | Select-Object LastWriteTime,Length,@{n='Path';e={$_.FullName.Replace('C:\orech_build\','')}} | Format-Table -AutoSize | Out-String -Width 200
Get-ChildItem "C:\פרוייקטים קלוד\zamir" -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.LastWriteTime -gt $cut } | Select-Object LastWriteTime,Length,Name | Format-Table -AutoSize | Out-String -Width 200
```

## 247. Bash: Reconnect adb and scan mockloc code for risky calls
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" reconnect 2>&1 | tail -1; sleep 4; "$ADB" devices
cd /c/orech_build/mockloc/src/com/orech/mockloc && ls; echo ---; cat BootReceiver.java UsbAttachActivity.java
echo --- risky; grep -nE 'Runtime|exec\(|reboot|su |setprop|Settings\.(Secure|Global|System)|killProcess|System\.exit|PowerManager|shutdown|disable|setApplicationEnabled|Thread\.sleep|while *\(true\)' *.java | head -30
```

## 248. PowerShell: Poll adb devices only, no commands to device
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$r=""
for($i=0;$i -lt 12;$i++){ $r=(& $adb devices | Select-Object -Skip 1 | Where-Object {$_.Trim()}) -join "; "; if($r){break}; Start-Sleep 5 }
"after ~$($i*5)s devices=[$r]"
```

## 249. Bash: Read-only device health check after reconnect
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime; getprop ro.boot.bootreason; getprop sys.boot.reason; logcat -d | grep -c 'FATAL EXCEPTION'; getprop init.svc.orech_vguard; getprop ro.build.display.id"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; tail -3 /mnt/vendor/protect_s/orech/integrity.log; tail -3 /data/system/orech_vguard/vguard.log'"
"$ADB" shell pm list packages | grep -iE 'orech|gpsconn'
```

## 250. Bash: Compare deployed files with local, read-only
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
echo "device vguard:"; "$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh /product/app/OrechMockLoc/OrechMockLoc.apk; wc -l /vendor/bin/orech_vguard.sh'"
echo "local:"; sha256sum /c/orech_build/deploy/orech_vguard_v8.sh /c/orech_build/deploy/OrechMockLoc.apk; wc -l /c/orech_build/deploy/orech_vguard_v8.sh
"$ADB" shell "su -c 'tail -6 /data/system/orech_vguard/vguard.log; wc -l /data/system/orech_vguard/*.txt'"
"$ADB" shell "dumpsys package com.orech.mockloc | grep -E 'versionName|lastUpdateTime|codePath|granted=true' | head -12"
"$ADB" shell "settings get secure location_providers_allowed; dumpsys location 2>/dev/null | grep -iE 'mock|gps provider' | head -5"
```

## 251. Bash: Launch installer on blocked test APK and capture screens
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "su -c 'ls /data/system/orech_allow_install 2>&1'"
"$ADB" push C:/orech_build/t_blocktest/blocktest.apk /sdcard/Download/blocktest.apk | tail -1
"$ADB" shell "am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Download/blocktest.apk >/dev/null 2>&1"; sleep 3
ID=$("$ADB" shell "content query --uri content://media/external/file --projection _id --where \"_data='/storage/emulated/0/Download/blocktest.apk'\"" | tr -d '\r' | sed -n 's/.*_id=\([0-9]*\).*/\1/p' | head -1); echo "id=$ID"
"$ADB" shell "input keyevent KEYCODE_WAKEUP"
"$ADB" logcat -c
"$ADB" shell am start -W -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/$ID -t application/vnd.android.package-archive --grant-read-uri-permission | tail -4
for n in 1 2 3; do sleep 2; "$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"; "$ADB" exec-out screencap -p > "$SP/blk$n.png"; done
"$ADB" shell pm list packages | grep -c blocktest
"$ADB" shell "logcat -d | grep -iE 'InstallStart|packageinstaller|orech|blocktest' | tail -10"
```

## 252. Bash: Inspect installer activity flow in logcat
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E 'ActivityTaskManager|ActivityManager' | grep -iE 'packageinstaller|InstallFailed|blocktest|Displayed' | tail -12"
echo ---; "$ADB" shell "logcat -d --pid=3185 | tail -25"
echo ---; "$ADB" shell "dumpsys activity activities | grep -iE 'packageinstaller' | head -8"
echo ---; "$ADB" shell "pm dump com.google.android.packageinstaller | grep -E 'Activity Resolver|^        [0-9a-f]+ com.google.android.packageinstaller/' | sed 's/ filter.*//' | sort -u | head -12"
```

## 253. Bash: Open Files app at Download folder
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell am start -a android.provider.action.BROWSE -d "content://com.android.externalstorage.documents/document/primary%3ADownload" 2>&1 | tail -2
sleep 3
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/files1.png"
```

## 254. Bash: Try opening file manager via root URI
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "pm list packages | grep -iE 'documentsui|files|filemanager|mt.plus'"
"$ADB" shell am start -a android.intent.action.VIEW -d "content://com.android.externalstorage.documents/root/primary" -t vnd.android.document/root 2>&1 | tail -2
sleep 3
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/files2.png"
```

## 255. Bash: Choose Files by Google once and screenshot
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell input tap 265 732; sleep 1; "$ADB" shell input tap 178 886; sleep 4
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/files3.png"
```

## 256. Bash: Check device state after black screen
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" get-state 2>&1
sleep 3
"$ADB" shell "cat /proc/uptime; dumpsys window | grep -E 'mCurrentFocus|mScreenOn|mAwake' | head -3; dumpsys power | grep -E 'mWakefulness=' | head -1"
"$ADB" exec-out screencap -p > "$SP/files4.png"; ls -l "$SP/files4.png"
```

## 257. PowerShell: Wait for device to return, read-only
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$r=""
for($i=0;$i -lt 40;$i++){ $r=(& $adb devices | Select-Object -Skip 1 | Where-Object {$_.Trim()}) -join "; "; if($r -match 'device$'){break}; Start-Sleep 4 }
"after ~$($i*4)s devices=[$r]"
Get-PnpDevice -PresentOnly -ErrorAction SilentlyContinue | Where-Object { $_.InstanceId -match 'VID_0E8D' } | Select-Object Status,FriendlyName | Format-Table | Out-String
```

## 258. Bash: Diagnose reboot and Settings ANR, read-only
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime; getprop ro.boot.bootreason; getprop sys.boot.reason; getprop persist.sys.boot.reason.history | head -2"
echo "--- fatal/anr:"; "$ADB" shell "logcat -d | grep -E 'FATAL EXCEPTION|ANR in|am_anr|Process .* has died|system_server.*(crash|died)' | tail -8"
echo "--- crash buf:"; "$ADB" shell "logcat -b crash -d | tail -12"
echo "--- integrity:"; "$ADB" shell "su -c 'tail -3 /mnt/vendor/protect_s/orech/integrity.log; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce'"
echo "--- dropbox:"; "$ADB" shell "su -c 'ls -t /data/system/dropbox | head -5'"
```

## 259. Bash: Check screen and settings changed by taps
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1; settings get global adb_enabled; settings get global adb_wifi_enabled; settings get global development_settings_enabled; settings get global adb_allowed_connection_time; settings get global stay_on_while_plugged_in"
"$ADB" exec-out screencap -p > "$SP/now1.png"
"$ADB" shell "logcat -d | grep -iE 'ActivityTaskManager: START' | tail -6"
```

## 260. Bash: Screenshot current screen and look for dialog source
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "dumpsys window | grep mCurrentFocus | head -1"
"$ADB" exec-out screencap -p > "$SP/now2.png"
ls /c/orech_build/tools/dex_edit/installer_guard | head
grep -rn "showDialogInner\|DLG_PACKAGE_ERROR\|Parse_error\|dlg_text" /c/orech_build/tools/dex_edit/installer_guard /c/orech_build/tools/dex_edit/README.md 2>/dev/null | head -8
```

## 261. Bash: Pull installer APK and search Hebrew strings
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
W=/c/orech_build/t_inst; mkdir -p $W
"$ADB" shell "su -c 'ls -l /system/priv-app/GooglePackageInstaller/; cp /system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk /data/local/tmp/gpi.apk; chmod 644 /data/local/tmp/gpi.apk; sha256sum /data/local/tmp/gpi.apk'"
"$ADB" pull /data/local/tmp/gpi.apk C:/orech_build/t_inst/gpi.apk | tail -1
"$ADB" shell "rm -f /data/local/tmp/gpi.apk"
sha256sum $W/gpi.apk
BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe"
"$AAPT" dump strings C:/orech_build/t_inst/gpi.apk 2>&1 | grep -nE 'לא הותקנה|השתבש|נחסמה' | head
```

## 262. Bash: Map Hebrew install strings to resource names
```
BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe"
"$AAPT" dump resources C:/orech_build/t_inst/gpi.apk 2>&1 > /c/orech_build/t_inst/res.txt
python - <<'EOF'
import re
t=open('C:/orech_build/t_inst/res.txt',encoding='utf-8',errors='replace').read()
want=['האפליקציה לא הותקנה.','החבילה נחסמה להתקנה.','משהו השתבש','אירעה בעיה','לא ניתן להתקין']
blocks=re.split(r'\n(?=\s+resource 0x)',t)
for b in blocks:
    if 'string/' in b.split('\n')[0] and any(w in b for w in want):
        print(re.sub(r'\s+',' ',b)[:200])
EOF
```

## 263. Bash: Inspect existing editing tools and installer guard
```
cd /c/orech_build/tools && ls arsc_xml_edit overlays | head -20; sed -n 1,30p dex_edit/README.md; grep -n "installer_guard\|VideoGuard" dex_edit/README.md | head -5; grep -rn "showDialogInner\|const/4 v4\|DLG_" dex_edit/installer_guard/*.smali | head -5; cat dex_edit/installer_guard/VideoGuard.java | head -30
```

## 264. Bash: Read VideoGuard source and look for generic message
```
cd /c/orech_build/tools/dex_edit && sed -n 30,200p installer_guard/VideoGuard.java; echo ----; ls ../reference_smali | head; echo ----; cat classpath.txt | head -3; grep -n "משהו" /dev/null; BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1); "C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe" dump strings C:/orech_build/t_inst/gpi.apk 2>&1 | grep -n "משהו" | head
```

## 265. Bash: Read PM-level block patch and reference listener
```
cd /c/orech_build/tools/dex_edit && cat patches_m393/pm_preparePackageLI.txt; echo; echo ---- ; ls ../reference_smali; echo ----; head -40 ../reference_smali/installstart-blockedclicklistener.smali
```

## 266. Bash: Find which block path fired in user's attempt
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E 'Video-capable|INSTALL_FAILED|InstallFailed|InstallInstalling|InstallStaging|blocktest|STATUS_FAILURE|PackageInstaller' | tail -25"
echo ---; "$ADB" shell "logcat -d | grep -E 'ActivityTaskManager: START' | grep -iE 'packageinstaller|nbu.files|blocktest' | tail -8"
echo ---; "$ADB" shell "pm list packages | grep -c blocktest; ls -l /sdcard/Download/blocktest.apk"
```

## 267. Bash: Read the install session error from logcat
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E ' kjm ' | head -14"
"$ADB" shell "logcat -d -t 3000 | grep -iE 'Video-capable|PrepareFailure|verification|blocked on this' | head -8"
```

## 268. Bash: Check /data free space and biggest consumers
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "df -h /data /sdcard /storage/emulated 2>&1 | head -5"
"$ADB" shell "su -c 'du -sk /data/app /data/data /data/media/0 /data/system /data/dalvik-cache /data/local/tmp 2>/dev/null; ls -la /data/local/tmp | head -20'"
```

## 269. Bash: Verify block from logcat, read-only
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "logcat -d | grep -E 'ActivityTaskManager: START' | grep -iE 'packageinstaller' | tail -4"
"$ADB" shell "logcat -d | grep -iE 'Video-capable|VideoGuard|blocktest|orech' | tail -8"
"$ADB" shell "pm list packages | grep -c blocktest; su -c 'ls /data/system/orech_allow_install 2>&1; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 270. Bash: Remove test APK and record result in handoff
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /sdcard/Download/blocktest.apk; ls /sdcard/Download/blocktest.apk 2>&1"
cd "/c/פרוייקטים קלוד/zamir" && python - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='(ג) מסך חסימת המתקין בעיצוב החדש: **לא נבדק**.'
assert a in s
s=s.replace(a,'(ג) מסך חסימת המתקין: **נבדק (המשתמש פתח את blocktest.apk מ-Files by Google וראה את נוסח החסימה שלנו; ביומן: InstallStart ואז PackageInstallerActivity, החבילה לא הותקנה, 0 קריסות)**. הנוסח הנוכחי: טוסט "האפליקציה נחסמה: היא כוללת תמיכה בוידיאו" ואחריו דיאלוג שגיאה של המתקין. נוסחים נפרדים לפי סיבה (וידיאו / יכולת התקנת אפליקציות) הוצעו ולא יושמו. הודעה כללית "האפליקציה לא הותקנה" יכולה להגיע גם מחוסר מקום (נצפה: `Failed to allocate ... only 152986 allocatable`), לא רק מהחסימה. פרטי הבדיקה הקודמת שלא הושלמה: ',1)
open(p,'w',encoding='utf-8',newline='\n').write(s)
bad=[c for c in s if ord(c)<32 and c!='\n']
print('ctrl chars:',len(bad))
EOF
```

## 271. Bash: Show git status excluding temp files
```
cd /c/orech_build && git status --short | grep -vE '\.(png)$|^\?\? r[0-9]+\.sh' | head -40; git branch --show-current; git diff --stat -- app/build.gradle.kts seller app/src/main | tail -5
```

## 272. Bash: Stage own files and commit without push
```
cd /c/orech_build && git add app/build.gradle.kts app/src/test seller/src/main/java/com/orech/seller/SellerActivity.kt && git status --short | grep -E '^[AM]' && git -c core.safecrlf=false commit -q -F - <<'EOF'
Seller: fix device id write, arm integrity only after verified approve; add ZipUtil tests

- SellerActivity: the device id was never written ($id missing from the nvdata and
  persist.sys commands); reproduced on a device before the fix, verified after.
- SellerActivity: enable the integrity control and self-remove only when approve
  succeeded and check returned empty; otherwise keep noenforce and stay installed.
- Add JVM unit tests for ZipUtil (compression method preserved, 4-byte alignment of
  stored entries, replacement with valid CRC, alignInPlace); JUnit 4.13.2.

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>
EOF
git log --oneline -2; git show --stat --format= HEAD | tail -5; git status --short | grep -E '^[AM]' | wc -l
```

## 273. Bash: Find the blocked Files Plus APK on the device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "ls -la /sdcard/Download | grep -iE 'apk|file|plus|סייר'" | head -20
"$ADB" shell "pm list packages | grep -iE 'filemanager|files|fileexplorer|explorer|plus'"
"$ADB" shell "logcat -d | grep -iE 'Video-capable|nstall' | grep -iE 'plus|explorer|file' | tail -5"
```

## 274. PowerShell: Search PC downloads for file manager APKs
```
Get-ChildItem D:\Downloads,"$env:USERPROFILE\Downloads","C:\פרוייקטים קלוד" -Recurse -Include *.apk,*.apks,*.xapk,*.apkm -ErrorAction SilentlyContinue -Depth 3 | Where-Object { $_.Name -match 'file|plus|explor|manager|סייר|FX|solid' } | Sort-Object LastWriteTime -Descending | Select-Object -First 12 LastWriteTime,Length,FullName | Format-Table -AutoSize | Out-String -Width 220
```

## 275. Bash: List device storage root and APKs
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "ls -la /sdcard/ | head -40"
echo ---; "$ADB" shell "find /sdcard -maxdepth 2 -iname '*.apk*' 2>/dev/null | head -20"
```

## 276. Bash: Pull APK and read badging and permissions statically
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D=/c/orech_build/t_fp; mkdir -p $D
"$ADB" pull "/sdcard/פרימיום בעברית.apk" C:/orech_build/t_fp/fp.apk | tail -1
sha256sum $D/fp.apk; ls -l $D/fp.apk
BT=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/$BT/aapt2.exe"
"$AAPT" dump badging C:/orech_build/t_fp/fp.apk 2>&1 | grep -E "^package:|application-label:|application-label-he|sdkVersion|targetSdk|native-code" | head -8
echo "--- permissions:"; "$AAPT" dump permissions C:/orech_build/t_fp/fp.apk 2>&1 | grep -iE 'INSTALL|uses-permission' | head -30
```

## 277. Bash: Statically check why the APK is blocked
```
cd /c/orech_build/t_fp && python -I - <<'EOF'
import zipfile,re
z=zipfile.ZipFile('fp.apk')
names=z.namelist()
libs=[n for n in names if n.startswith('lib/') and n.endswith('.so')]
LIBS=["libvlc","libavcodec","libffmpeg","libmpv","libdav1d","libvpx","libde265","libopenh264","libx264","libijkffmpeg","libijksdl"]
print('native libs:',len(libs))
hit=[n for n in libs if any(n.rsplit('/',1)[1].lower().startswith(p) for p in LIBS)]
print('decoder libs hit:',hit[:10])
print('abis:',sorted({n.split('/')[1] for n in libs}))
m=z.read('AndroidManifest.xml')
u8=b'video/'; u16='video/'.encode('utf-16le')
print('manifest video/ (utf8):',u8 in m,' (utf16):',u16 in m)
for tag,name in (('INSTALL_PACKAGES','INSTALL_PACKAGES'),):
    print('manifest',tag,':',name.encode() in m or name.encode('utf-16le') in m)
# which strings contain 'video/'
for enc in ('utf-8','utf-16le'):
    try:
        s=m.decode(enc,errors='ignore')
        for mm in re.finditer(r'[\w./*-]*video/[\w.*-]*',s): print(enc,mm.group(0))
    except Exception as e: pass
other=[n for n in names if re.search(r'(ffmpeg|vlc|exoplayer|media3|codec|decoder)',n,re.I)]
print('other decoder-like entries:',other[:10])
EOF
```

## 278. Bash: Build vguard v9 and syntax-check on device
```
cd /c/orech_build/deploy && python -I - <<'EOF'
s=open('orech_vguard_v8.sh',encoding='utf-8',newline='').read()
assert '\r' not in s
s=s.replace('# orech video guard v5.','# orech video guard v9.\n#  - install-time guards are off (flag file below); enforcement happens after install:\n#    decoder libs are locked (chmod 000) without disabling the app; the app is blacklisted only if a locked\n#    decoder comes back to life, or if a decoder shows up in its data dir later',1)
# flag file (persistent install-time bypass)
s=s.replace('touch "$ALLOW" "$DENY" "$MOCK" "$STATE" "$BL" "$DIS"','touch "$ALLOW" "$DENY" "$MOCK" "$STATE" "$BL" "$DIS"\n# install-time VideoGuard (installer + package manager) is switched off by this flag; see scan_libs/scan_manifest\ntouch /data/system/orech_allow_install',1)
old_libs=s[s.index('scan_libs() {'):s.index('# manifest check of an installed app')]
new_libs='''scan_libs() {
  d=$1
  pkg=$(pkg_of "$d")
  grep -qx "$pkg" "$ALLOW" && return
  back=0
  for f in $(find "$d" -type f -name '*.so' 2>/dev/null); do
    is_video_lib "$f" || continue
    [ "$(stat -c %a "$f")" = "0" ] && continue
    # a lib we already locked is readable again: the decoder came back to life
    grep -qxF "$f" "$STATE" && back=1
    chmod 000 "$f"
    grep -qxF "$f" "$STATE" || echo "$f" >> "$STATE"
    echo "$(date '+%F %T') locked $f" >> "$LOG"
  done
  if [ "$back" = "1" ]; then
    blacklist "$pkg" "decoder came back"
  fi
}

'''
s=s.replace(old_libs,new_libs,1)
s=s.replace('# manifest check of an installed app: video/ mime type -> disable; installer permission -> deny the appop','# manifest check of an installed app: installer permission -> deny the appop (video/ mime types are allowed)',1)
old_v='''  if echo "$m" | grep -a -q 'video/'; then
    blacklist "$pkg" "manifest video/"
    return
  fi
'''
assert old_v in s
s=s.replace(old_v,'',1)
assert 'decoder came back' in s and 'manifest video/' not in s
open('orech_vguard_v9.sh','w',encoding='utf-8',newline='\n').write(s)
print(len(s.encode()))
EOF
diff orech_vguard_v8.sh orech_vguard_v9.sh | head -60; export MSYS_NO_PATHCONV=1; ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" push C:/orech_build/deploy/orech_vguard_v9.sh /data/local/tmp/t9.sh | tail -1
"$ADB" shell "sh -n /data/local/tmp/t9.sh && echo SYNTAX_OK; df /vendor | tail -1"
```

## 279. Bash: Clean temp file and confirm device unchanged
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /data/local/tmp/t9.sh; su -c 'sha256sum /vendor/bin/orech_vguard.sh; ls /data/system/orech_allow_install 2>&1; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
sha256sum /c/orech_build/deploy/orech_vguard_v8.sh /c/orech_build/deploy/orech_vguard_v9.sh | cut -c1-16,65-
```

## 280. Bash: Deploy vguard v9 with backup and verification
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
D=C:/orech_build/t_v9; mkdir -p /c/orech_build/t_v9
SHA=$(sha256sum /c/orech_build/deploy/orech_vguard_v9.sh | cut -d' ' -f1); SZ=$(stat -c %s /c/orech_build/deploy/orech_vguard_v9.sh); echo "$SHA $SZ"
printf '%s\n' 'set -e' \
 'B=/mnt/vendor/protect_s/orech' \
 'touch $B/noenforce' \
 "[ \"\$(wc -c < /data/local/tmp/t9.sh)\" = \"$SZ\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/t9.sh\" | sha256sum -c -" \
 'sh -n /data/local/tmp/t9.sh' \
 'mount -o remount,rw /vendor' \
 'cp /vendor/bin/orech_vguard.sh /data/local/tmp/orech_vguard.v8.bak' \
 'dd if=/data/local/tmp/t9.sh of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1' \
 "truncate -s $SZ /vendor/bin/orech_vguard.sh" \
 'sync' \
 "echo \"$SHA  /vendor/bin/orech_vguard.sh\" | sha256sum -c -" \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard' > /c/orech_build/t_v9/deploy_v9.sh
"$ADB" push C:/orech_build/deploy/orech_vguard_v9.sh /data/local/tmp/t9.sh | tail -1
"$ADB" push C:/orech_build/t_v9/deploy_v9.sh /data/local/tmp/deploy_v9.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_v9.sh'"
```

## 281. Bash: Phase 1: dummy decoder should be locked, not blacklisted
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /data/local/tmp/t9.sh /data/local/tmp/deploy_v9.sh; su -c 'ls -l /data/system/orech_allow_install; sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16'"
cat > /c/orech_build/t_v9/ph1.sh <<'EOF'
P=$(ls -d /data/app/*/com.orech.seller-*/ | head -1)
mkdir -p ${P}lib/arm64
echo dummy > ${P}lib/arm64/libffmpeg_test.so
chmod 644 ${P}lib/arm64/libffmpeg_test.so
echo "$P" > /data/local/tmp/v9test.path
ls -l ${P}lib/arm64/libffmpeg_test.so
EOF
"$ADB" push C:/orech_build/t_v9/ph1.sh /data/local/tmp/ph1.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/ph1.sh'"
for i in $(seq 1 16); do
  sleep 10
  L=$("$ADB" shell "su -c 'grep -c libffmpeg_test /data/system/orech_vguard/vguard.log'" | tr -d '\r'); [ "$L" != "0" ] && break
done
echo "waited ~$((i*10))s locked_log=$L"
"$ADB" shell "su -c 'tail -3 /data/system/orech_vguard/vguard.log; echo BL=[\$(cat /data/system/orech_vguard/blacklist.txt)]; pm list packages -d | grep -c seller; stat -c %a \$(cat /data/local/tmp/v9test.path)lib/arm64/libffmpeg_test.so'"
```

## 282. Bash: Phase 2: restored decoder should trigger blacklist
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'chmod 644 \$(cat /data/local/tmp/v9test.path)lib/arm64/libffmpeg_test.so'"
for i in $(seq 1 20); do
  sleep 10
  R=$("$ADB" shell "su -c 'grep -c \"decoder came back\" /data/system/orech_vguard/vguard.log'" | tr -d '\r'); [ "$R" != "0" ] && break
done
echo "waited ~$((i*10))s came_back_log=$R"
"$ADB" shell "su -c 'tail -3 /data/system/orech_vguard/vguard.log; echo BL=[\$(cat /data/system/orech_vguard/blacklist.txt)]; pm list packages -d | grep seller'"
```

## 283. Bash: Clean up test state and re-verify device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cat > /c/orech_build/t_v9/clean.sh <<'EOF'
BL=/data/system/orech_vguard/blacklist.txt
grep -vx com.orech.seller $BL > /data/local/tmp/bl.new
cat /data/local/tmp/bl.new > $BL
P=$(cat /data/local/tmp/v9test.path)
rm -f ${P}lib/arm64/libffmpeg_test.so
rmdir ${P}lib/arm64 ${P}lib 2>/dev/null
pm enable com.orech.seller
sed -i '/libffmpeg_test/d' /data/system/orech_vguard/locked.txt
rm -f /data/local/tmp/bl.new /data/local/tmp/v9test.path /data/local/tmp/ph1.sh /data/local/tmp/clean.sh
echo BL_LINES=$(wc -l < $BL)
pm list packages -d | grep -c seller
ls ${P}
EOF
"$ADB" push C:/orech_build/t_v9/clean.sh /data/local/tmp/clean.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/clean.sh'"
"$ADB" shell "pm list packages | grep -i vlc; pm list packages -d | grep -i vlc; su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'; logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 284. Bash: Verify install-time block is off with test APK
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" install C:/orech_build/t_blocktest/blocktest.apk 2>&1 | tail -2
"$ADB" shell "pm list packages | grep blocktest"
"$ADB" uninstall com.orech.blocktest 2>&1 | tail -1
"$ADB" shell "pm list packages | grep -c blocktest; su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce'; logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 285. Bash: Rebuild codeless test APK and verify clean install
```
unset MSYS_NO_PATHCONV
SDK="C:/Users/user1/AppData/Local/Android/Sdk"
BTV=$(ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/ | tail -1)
AAPT="$SDK/build-tools/$BTV/aapt2.exe"; AS="$SDK/build-tools/$BTV/apksigner.bat"; JAR="$SDK/platforms/android-34/android.jar"
cd /c/orech_build/t_blocktest && sed -i 's|<application android:label="blocktest">|<application android:label="blocktest" android:hasCode="false">|; s|<activity android:name=".A" android:exported="true">|<activity android:name="android.app.Activity" android:exported="true">|' AndroidManifest.xml
"$AAPT" link -o bt2_unsigned.apk --manifest AndroidManifest.xml -I "$JAR" --min-sdk-version 26 --target-sdk-version 33 2>&1 | tail -2
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$AS" sign --ks "C:/Users/user1/.android/debug.keystore" --ks-pass pass:android --out bt2.apk bt2_unsigned.apk 2>&1 | tail -2
ls -l bt2.apk
export MSYS_NO_PATHCONV=1; ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" install C:/orech_build/t_blocktest/bt2.apk 2>&1 | tail -2
"$ADB" shell "pm list packages | grep blocktest"
"$ADB" uninstall com.orech.blocktest 2>&1 | tail -1
"$ADB" shell "pm list packages | grep -c blocktest; su -c 'tail -2 /data/system/orech_vguard/vguard.log'"
```

## 286. Bash: Inspect install-permission appop state and Settings access
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'echo deny_install=[\$(cat /data/system/orech_vguard/deny_install.txt)]'"
for p in com.google.android.apps.nbu.files bin.mt.plus.canary com.topjohnwu.magisk; do echo "== $p"; "$ADB" shell "cmd appops get $p REQUEST_INSTALL_PACKAGES 2>&1 | head -2; pm list packages -s | grep -c $p"; done
echo "--- settings screen reachable?"
"$ADB" shell "cmd package query-activities -a android.settings.MANAGE_UNKNOWN_APP_SOURCES 2>&1 | grep -E 'name=|packageName' | head -3"
"$ADB" shell "cmd package query-activities -a android.settings.MANAGE_APP_EXTERNAL_SOURCES 2>&1 | grep -E 'name=' | head -3"
```

## 287. Bash: Document v9 policy and limits in handoff
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a='גרסת השירות `orech_vguard` הפרוסה: v7.'
assert a in s
s=s.replace(a,'גרסת השירות `orech_vguard` הפרוסה: **v9** (מקור: `C:/orech_build/deploy/orech_vguard_v9.sh`; הגרסה הקודמת v8: `deploy/orech_vguard_v8.sh` ו-`/data/local/tmp/orech_vguard.v8.bak`).',1)
s=s.rstrip()
assert s.endswith('</div>')
s=s[:-6].rstrip('\n')+'''

## 9. מדיניות התקנה v9 (פרוסה בסוף הסשן הזה)
- **בדיקות ההתקנה כבויות:** השירות יוצר בכל עלייה את `/data/system/orech_allow_install`, ולכן `VideoGuard` במתקין ובמנהל החבילות לא חוסם. הדגל כבר לא "זמני לפני התקנה": אל תמחק אותו אלא אם רוצים לחזור לחסימה בזמן התקנה (במקרה כזה גם להחזיר את v8 ולמחוק את השורה `touch /data/system/orech_allow_install`).
- **אכיפה אחרי התקנה:** ספריית פענוח (לפי שם) באפליקציה מותקנת ננעלת ב-`chmod 000` בלי להשבית את האפליקציה. האפליקציה מושבתת ונכנסת לרשימה השחורה רק אם ספרייה נעולה חוזרת להיות קריאה (`decoder came back`) או אם מופיעה ספריית פענוח ב-`/data/data` שלה. הצהרת `video/` במניפסט כבר לא חוסמת. הרשאת `REQUEST_INSTALL_PACKAGES` נשללת באמצעות appops (ראו מגבלות למטה).
- **נבדק על המכשיר:** (1) קובץ דמה `libffmpeg_test.so` בספריית האפליקציה ננעל תוך שניות, האפליקציה לא הושבתה. (2) החזרת הרשאת קריאה גרמה ל-`blacklisted+disabled (decoder came back)` תוך כ-80 שניות. (3) APK בדיקה עם `video/*` במניפסט הותקן בהצלחה (בלי חסימה) והוסר. הכל נוקה אחרי הבדיקה. `check` ריק, 0 קריסות.
- **נצפה:** VLC הותקן (אחרי הפעלת הדגל) וספריות הפענוח שלו ננעלו בלי השבתה. File Manager + (גרסה מתוקנת מפורום, ללא ספריות native) לא הותקן על ידי הסשן; התקנה ידנית.
- **מגבלות (לא להבטיח יותר):** שלילת `REQUEST_INSTALL_PACKAGES` היא appop לכל חבילה (`cmd appops set <חבילה> REQUEST_INSTALL_PACKAGES deny`), מחודשת בערך פעם בדקה ולחבילות חדשות; מסך "התקנת אפליקציות לא ידועות" (`Settings$ManageExternalSourcesActivity`) נגיש למשתמש. נצפה ש-`com.google.android.apps.nbu.files` נמצא ב-`deny_install.txt` אך מצב ה-appop שלו היה `allow`, כלומר השלילה אינה אכיפה מלאה. לא נבדק למה.
</div>
'''
open(p,'w',encoding='utf-8',newline='\n').write(s)
bad=[c for c in s if ord(c)<32 and c!='\n']
print('ctrl chars',len(bad))
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- פרישת `orech_vguard` v9: בדיקות התקנה כבויות דרך דגל, נעילת מפענחים אחרי התקנה, רשימה שחורה רק כשמפענח חוזר. נבדק בבדיקות סינתטיות ונוקה. פירוט וגבולות ב-HANDOFF סעיף 9.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
EOF
grep -c 'סעיף 9\|## 9. מדיניות' HANDOFF_M393.md CHANGELOG_M393.md
```

## 288. Bash: Test deny command effect on appop for Files by Google
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
P=com.google.android.apps.nbu.files
echo "== before:"; "$ADB" shell "cmd appops get $P REQUEST_INSTALL_PACKAGES; cmd appops get $P --uid REQUEST_INSTALL_PACKAGES 2>&1 | head -2"
echo "== set deny (as root, like vguard):"; "$ADB" shell "su -c 'cmd appops set $P REQUEST_INSTALL_PACKAGES deny; echo EXIT=\$?'"
echo "== after:"; "$ADB" shell "cmd appops get $P REQUEST_INSTALL_PACKAGES"
echo "== uid:"; "$ADB" shell "dumpsys package $P | grep -E 'userId=|sharedUser|android.permission.REQUEST_INSTALL' | head -4"
echo "== vguard uses:"; "$ADB" shell "su -c 'grep -n REQUEST_INSTALL /vendor/bin/orech_vguard.sh'"
```

## 289. Bash: Roll back to vguard v8 and remove the bypass flag
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SHA8=$(sha256sum /c/orech_build/deploy/orech_vguard_v8.sh | cut -d' ' -f1); SZ8=$(stat -c %s /c/orech_build/deploy/orech_vguard_v8.sh); echo "$SHA8 $SZ8"
printf '%s\n' 'set -e' 'B=/mnt/vendor/protect_s/orech' 'touch $B/noenforce' \
 'S=/data/local/tmp/orech_vguard.v8.bak' \
 "[ \"\$(wc -c < \$S)\" = \"$SZ8\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA8  \$S\" | sha256sum -c -" \
 'mount -o remount,rw /vendor' \
 'dd if=$S of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1' \
 "truncate -s $SZ8 /vendor/bin/orech_vguard.sh" 'sync' \
 "echo \"$SHA8  /vendor/bin/orech_vguard.sh\" | sha256sum -c -" \
 'rm -f /data/system/orech_allow_install' \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard' \
 'ls /data/system/orech_allow_install 2>&1 || true' > /c/orech_build/t_v9/rollback.sh
"$ADB" push C:/orech_build/t_v9/rollback.sh /data/local/tmp/rollback.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/rollback.sh; rm -f /data/local/tmp/rollback.sh'"
echo "--- inotifyd available?"; "$ADB" shell "which inotifyd; ls /system/bin/inotifyd /system/bin/toybox 2>&1 | head -2"
```

## 290. Bash: Update handoff and changelog after rollback
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'EOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
a=s.index('## 9. מדיניות התקנה v9')
s=s[:a]+'''## 9. ניסוי v9 שנדחה, והכיוון שהמשתמש בחר
- **v9 נפרס, נבדק ובוטל באותו סשן** לפי החלטת המשתמש. הפרוס בפועל: **v8** (sha256 `890863c7...`, 4938 בייט), הדגל `/data/system/orech_allow_install` **לא קיים** ובדיקות ההתקנה פעילות כמו קודם. המקור של v9 נשאר ב-`C:/orech_build/deploy/orech_vguard_v9.sh` לעיון בלבד (נעילת מפענחים בלי השבתה, השבתה רק כש"המפענח חוזר"; נבדק בבדיקות סינתטיות).
- **החלטות המשתמש:** (1) חסימת ההתקנה צריכה להישאר ולהיות חזקה יותר, לא להקל. (2) מוכן לוותר על File Manager + (גרסה מתוקנת מפורום; נחסמה בגלל `video/` במניפסט והרשאת `REQUEST_INSTALL_PACKAGES`, ואין בה ספריות native). (3) רוצה סייר קבצים **מובנה** ללא יכולת התקנה וללא תמיכה בוידיאו, וכל מה שיותקן יהיה רק דרכו. (4) **לא רוצה סריקות רקע תכופות**: שלילה חד-פעמית בהתקנה, בלי בזבוז סוללה והאטה.
- **מצב נוכחי של הסריקות (v8):** לולאה כל 2 שניות על `/data/app`, וסריקה מלאה של `/data/data` כל כ-60 שניות. לא נמדדה צריכת סוללה בפועל. `inotifyd` קיים ב-`/system/bin` ויכול להחליף חלק מהסריקות באירועים.
- **מגבלה ידועה של שלילת ההתקנה:** appop לכל חבילה (`cmd appops set <חבילה> REQUEST_INSTALL_PACKAGES deny`, בדיקה ידנית כ-root עבדה על `com.google.android.apps.nbu.files`, `EXIT=0`, מצב `deny`); מסך `Settings$ManageExternalSourcesActivity` נגיש למשתמש, ולכן זו לא אכיפה מלאה.
- **לא התחיל:** סייר קבצים מובנה, וסריקות מבוססות אירועים. אל תוריד אפליקציה לצורך זה בלי רשות מפורשת מהמשתמש.
</div>
'''
s=s.replace('הפרוסה: **v9** (מקור: `C:/orech_build/deploy/orech_vguard_v9.sh`; הגרסה הקודמת v8: `deploy/orech_vguard_v8.sh` ו-`/data/local/tmp/orech_vguard.v8.bak`).','הפרוסה: **v8** (`deploy/orech_vguard_v8.sh`; v9 נוסה ובוטל, ראו סעיף 9).',1)
assert 'v8** (`deploy' in s
bad=[c for c in s if ord(c)<32 and c!='\n']; print('ctrl',len(bad))
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c.replace('- פרישת `orech_vguard` v9: בדיקות התקנה כבויות דרך דגל, נעילת מפענחים אחרי התקנה, רשימה שחורה רק כשמפענח חוזר. נבדק בבדיקות סינתטיות ונוקה. פירוט וגבולות ב-HANDOFF סעיף 9.','- `orech_vguard` v9 נפרס ונבדק ואז **בוטל** לפי החלטת המשתמש; הוחזר v8 והדגל `orech_allow_install` הוסר (סעיף 9 ב-HANDOFF).')
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c+'\n')
EOF
grep -c 'בוטל' HANDOFF_M393.md CHANGELOG_M393.md; tail -c 200 CHANGELOG_M393.md
```

## 291. Bash: Clone the user-provided repo into an isolated folder
```
mkdir -p /c/orech_build/ext && cd /c/orech_build/ext && GIT_TERMINAL_PROMPT=0 git clone --depth 1 https://github.com/Xnet2/file-manager-x fmx 2>&1 | tail -3
cd fmx && ls -la | head -30; du -sh .; git log -1 --format='%h %an %ad %s' | head -1; ls | grep -iE 'licen|copying'
```

## 292. Bash: Check build kit and flavor flags usage
```
ls "$LOCALAPPDATA/VODOS-build" 2>&1 | head -5; cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && grep -n "NO_VIDEO\|NO_INSTALL" *.java | head -30; ls /c/orech_build/ext/fmx/צילומי*
```

## 293. Bash: Inspect app name strings and available toolchain
```
cd /c/orech_build/ext/fmx/app/src/main/res/values && grep -n "מנהל הקבצים\|app_name" values.xml | head; grep -rn "מנהל הקבצים\|Flavor.NAME\|com.custom" /c/orech_build/ext/fmx/app/src/main/java --include=*.java | grep -v "^.*:package com.custom" | head -12; ls /c/Users/user1/AppData/Local/Android/Sdk/build-tools/; ls "/c/Program Files/Android/Android Studio/jbr/bin/" | grep -E "^(javac|java|jar|keytool)\.exe"; "/c/Program Files/Android/Android Studio/jbr/bin/java.exe" -version 2>&1 | head -1
```

## 294. Bash: Build the lite file manager with new package name
```
cd /c/orech_build/ext && PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -15
```

## 295. Bash: Verify package, permissions and guard conditions
```
unset MSYS_NO_PATHCONV
BT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/37.0.0"
A=C:/orech_build/ext/fmx_out/FileManager-Lite.apk
"$BT/aapt2.exe" dump badging $A | grep -E "^package:|application-label:|native-code|targetSdk"
"$BT/aapt2.exe" dump permissions $A | grep -E "uses-permission"
"$BT/aapt2.exe" dump xmltree --file AndroidManifest.xml $A | grep -iE "provider|authorities|mimeType|intent-filter" | head
python -I - <<'EOF'
import zipfile
z=zipfile.ZipFile(r'C:/orech_build/ext/fmx_out/FileManager-Lite.apk')
m=z.read('AndroidManifest.xml')
def has(b): return b in m or b.decode().encode('utf-16le') in m
print('manifest video/:',has(b'video/'),'| INSTALL_PACKAGES:',has(b'INSTALL_PACKAGES'))
print('native libs:',[n for n in z.namelist() if n.startswith('lib/')])
print('entries:',len(z.namelist()))
EOF
"$BT/apksigner.bat" verify --print-certs $A 2>&1 | head -3
```

## 296. Bash: Copy built APK to the project folder
```
cp /c/orech_build/ext/fmx_out/FileManager-Lite.apk "/c/פרוייקטים קלוד/zamir/FileManager-Lite_com.orech.files.apk" && ls -l "/c/פרוייקטים קלוד/zamir/FileManager-Lite_com.orech.files.apk"
```

## 297. Bash: Check the fork is readable, no auth used
```
cd /c/orech_build/ext/fmx && GIT_TERMINAL_PROMPT=0 git ls-remote https://github.com/shmuel-sinay/file-manager-x 2>&1 | head -3; git remote -v; git status --short | head
```

## 298. Bash: Set fork remote and create permanent signing key
```
cd /c/orech_build/ext/fmx && git remote rename origin upstream && git remote add origin https://github.com/shmuel-sinay/file-manager-x && git remote -v
mkdir -p /c/orech_build/ext/keys && cd /c/orech_build/ext/keys
PW=$(python -I -c "import secrets,string;print(''.join(secrets.choice(string.ascii_letters+string.digits) for _ in range(28)))")
printf '%s' "$PW" > fmx.pass
rm -f ../fmx_release.keystore
"/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -genkeypair -keystore fmx_release.keystore -alias fmx -keyalg RSA -keysize 4096 -validity 12000 -storepass "$PW" -keypass "$PW" -dname "CN=Orech Files" 2>&1 | tail -1
ls -l; "/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -list -keystore fmx_release.keystore -storepass "$PW" 2>&1 | grep -E "Valid|Certificate fingerprint|SHA-256|Alias" | head -4
```

## 299. Bash: Set fork remote and create permanent signing key
```
cd /c/orech_build/ext/fmx && git remote rename origin upstream && git remote add origin https://github.com/shmuel-sinay/file-manager-x && git remote -v
mkdir -p /c/orech_build/ext/keys && cd /c/orech_build/ext/keys && ls
PW=$(python -I -c "import secrets,string;print(''.join(secrets.choice(string.ascii_letters+string.digits) for _ in range(28)))")
printf '%s' "$PW" > fmx.pass
"/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -genkeypair -keystore fmx_release.keystore -alias fmx -keyalg RSA -keysize 4096 -validity 12000 -storepass "$PW" -keypass "$PW" -dname "CN=Orech Files" 2>&1 | tail -1
ls -l; "/c/Program Files/Android/Android Studio/jbr/bin/keytool.exe" -list -v -keystore fmx_release.keystore -storepass "$PW" 2>&1 | grep -E "Valid from|SHA256" | head -3
```

## 300. Bash: Read Ui helper header and sizes
```
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && wc -l Ui.java HomeScreen.java BrowserScreen.java Drawer.java MainActivity.java && sed -n 1,80p Ui.java
```

## 301. Bash: Check gh login and read rest of Ui.java
```
gh auth status 2>&1 | head -6
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && sed -n 80,259p Ui.java; grep -n "COLOR\|SOFT" Media.java | head -8; grep -c "D97706\|FFF3DA\|FFEBC2" *.java | grep -v ":0"
```

## 302. Bash: Inspect setBars and color usages
```
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && grep -n "void setBars" -A8 MainActivity.java; grep -n "D97706\|FFF3DA\|FFEBC2" FileAdapter.java Ui.java; grep -n "Media.COLOR\|Media.SOFT\|\.soft" *.java | grep -v "^Media.java" | head -12; sed -n 10,20p Media.java
```

## 303. Bash: Apply flat-list style to the home screen and rebuild
```
cd /c/orech_build/ext/fmx/app/src/main/java/com/custom/files && python -I - <<'PYEOF'
import re
def edit(fn, pairs):
    s = open(fn, encoding='utf-8', newline='').read()
    crlf = '\r\n' in s
    if crlf: s = s.replace('\r\n', '\n')
    for old, new in pairs:
        assert old in s, (fn, old[:60])
        s = s.replace(old, new, 1)
    if crlf: s = s.replace('\n', '\r\n')
    open(fn, 'w', encoding='utf-8', newline='').write(s)

# --- tokens: warm paper, ink, steel blue; flat bar
edit('Ui.java', [
 ('''    static final int BG = 0xFFF4F6FA, SURFACE = 0xFFFFFFFF, PRIMARY = 0xFFD97706, PRIMARY_SOFT = 0xFFFFF3DA,
            TEXT = 0xFF0F172A, TEXT2 = 0xFF64748B, TEXT3 = 0xFF94A3B8, DIVIDER = 0xFFE7EBF1,
            DANGER = 0xFFDC2626, SELECTED = 0xFFFFEBC2, AMBER = 0xFFF59E0B, DARK = 0xFF1E293B;''',
  '''    static final int BG = 0xFFF3EFE6, SURFACE = 0xFFFBF8F1, PRIMARY = 0xFF2F6F9F, PRIMARY_SOFT = 0xFFDDE9F2,
            TEXT = 0xFF1D2327, TEXT2 = 0xFF6B665C, TEXT3 = 0xFF9A958A, DIVIDER = 0xFFD9D3C4,
            DANGER = 0xFFB3472F, SELECTED = 0xFFD3E3EE, AMBER = 0xFFC98A1B, DARK = 0xFF26343B;'''),
 ('csl(0x22D97706)', 'csl(0x222F6F9F)'),
 ('csl(0x1CD97706)', 'csl(0x1C2F6F9F)'),
 ('        bar.setBackgroundColor(SURFACE);\n        bar.setElevation(dp(2));', '        bar.setBackgroundColor(SURFACE);\n        bar.setElevation(0);'),
 # flat usage bar (square ends) for the list style
 ('''        float frac;
        int color = PRIMARY;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Bar(Context c) { super(c); }''',
  '''        float frac;
        int color = PRIMARY;
        boolean flat;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Bar(Context c) { super(c); }'''),
 ('            float h = getHeight(), w = getWidth(), rad = h / 2f;', '            float h = getHeight(), w = getWidth(), rad = flat ? 0 : h / 2f;'),
])
edit('FileAdapter.java', [('0xE6D97706', '0xE62F6F9F')])
edit('Media.java', [
 ('{0xFF8B5CF6, 0xFFEF4444, 0xFF0D9488, 0xFF2563EB, 0xFF22A35A, 0xFFB7791F, 0xFF475569, 0xFFDB2777}',
  '{0xFF9A5B2E, 0xFFB3472F, 0xFF2E7D5B, 0xFF2F6F9F, 0xFF4C7F3A, 0xFF8A6A1F, 0xFF6B665C, 0xFF7A4B63}'),
 ('{0xFFF1EBFE, 0xFFFDECEC, 0xFFDDF5F1, 0xFFE8EFFD, 0xFFE3F5EA, 0xFFF8EEDA, 0xFFEDF1F6, 0xFFFCE7F3}',
  '{0xFFF0E3D6, 0xFFF3E1DA, 0xFFDFEDE5, 0xFFDDE9F2, 0xFFE4EEDC, 0xFFF0E7D0, 0xFFE9E5DB, 0xFFEBDDE4}'),
])

# --- home screen: flat list
edit('HomeScreen.java', [
 ('/** Start screen: a grid of big tiles — storages, Downloads, categories and tools (like the original, but light). */',
  '/** Start screen: a flat list in three sections — storage, categories, tools — with a colour stripe per row. */'),
 ('''        int icon, color, soft;
        String title, sub = " ";''', '''        int icon, color, soft;
        String title, sub = " ", section = "";'''),
 ('''        LinearLayout bar = Ui.appBar(a);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_menu, Ui.TEXT, "תפריט", v -> a.openDrawer()));
        TextView t = Ui.line(a, Flavor.NAME, 20, Ui.TEXT, Ui.MEDIUM);
        t.setPaddingRelative(Ui.dp(8), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.TEXT, "חיפוש", v -> a.push(BrowserScreen.search(a))));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.TEXT, "אפשרויות", v -> menu()));''',
  '''        LinearLayout bar = Ui.appBar(a);
        bar.setBackgroundColor(Ui.DARK);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_menu, Ui.BG, "תפריט", v -> a.openDrawer()));
        TextView t = Ui.line(a, Flavor.NAME, 20, Ui.BG, Ui.BOLD);
        t.setPaddingRelative(Ui.dp(8), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.BG, "חיפוש", v -> a.push(BrowserScreen.search(a))));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.BG, "אפשרויות", v -> menu()));'''),
 ('        Ui.pad(col, 10, 10, 10, 10);', '        Ui.pad(col, 0, 0, 0, 16);'),
 ('''        TextView fh = Ui.text(a, "מועדפים", 15, Ui.TEXT, Ui.MEDIUM);
        favSection.addView(fh, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 10, 18, 10, 10));
        favBox = Ui.vbox(a);
        favBox.setBackground(Ui.round(Ui.SURFACE, 20));
        favBox.setClipToOutline(true);
        favSection.addView(favBox, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 6, 0, 6, 0));''',
  '''        favSection.addView(sectionHeader("מועדפים"), Ui.lp(Ui.MATCH, Ui.WRAP));
        favBox = Ui.vbox(a);
        favSection.addView(favBox, Ui.lp(Ui.MATCH, Ui.WRAP));'''),
 ('        a.setBars(Ui.SURFACE, true);', '        a.setBars(Ui.DARK, false);'),
])
s = open('HomeScreen.java', encoding='utf-8', newline='').read()
crlf = '\r\n' in s
if crlf: s = s.replace('\r\n', '\n')
a0 = s.index('    private View tileView(Tile t) {')
a1 = s.index('    private Tile catTile(int cat, long[] st) {')
new_block = '''    private TextView sectionHeader(String title) {
        TextView h = Ui.text(a, title, 12, Ui.TEXT2, Ui.MEDIUM);
        h.setLetterSpacing(0.08f);
        Ui.pad(h, 16, 14, 16, 4);
        return h;
    }

    private View rowView(Tile t) {
        FrameLayout wrap = new FrameLayout(a);
        wrap.setBackground(Ui.rippleRect());
        LinearLayout r = Ui.hbox(a);
        Ui.pad(r, 18, 11, 16, 11);
        r.addView(Ui.img(a, t.icon, t.color), Ui.lp(Ui.dp(24), Ui.dp(24)));
        LinearLayout mid = Ui.vbox(a);
        mid.setPaddingRelative(Ui.dp(14), 0, Ui.dp(10), 0);
        mid.addView(Ui.line(a, t.title, 16, Ui.TEXT, Ui.MEDIUM));
        if (t.frac >= 0) {
            Ui.Bar bar = new Ui.Bar(a);
            bar.flat = true;
            bar.set(t.frac, t.frac > 0.9f ? Ui.DANGER : t.frac > 0.75f ? Ui.AMBER : t.color);
            mid.addView(bar, Ui.margins(Ui.lp(Ui.MATCH, Ui.dp(4)), 0, 6, 0, 0));
        }
        r.addView(mid, Ui.lp(0, Ui.WRAP, 1));
        TextView s = Ui.line(a, t.sub, 13, Ui.TEXT2, Ui.REGULAR);
        s.setTextDirection(View.TEXT_DIRECTION_RTL);
        r.addView(s, Ui.lp(Ui.WRAP, Ui.WRAP));
        wrap.addView(r, new FrameLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        View stripe = new View(a);
        stripe.setBackgroundColor(t.color);
        wrap.addView(stripe, new FrameLayout.LayoutParams(Ui.dp(4), Ui.MATCH, Gravity.START));
        View line = new View(a);
        line.setBackgroundColor(Ui.DIVIDER);
        wrap.addView(line, new FrameLayout.LayoutParams(Ui.MATCH, Math.max(1, Ui.dp(1)), Gravity.BOTTOM));
        wrap.setOnClickListener(v -> t.click.run());
        return wrap;
    }

    private void layoutTiles(ArrayList<Tile> tiles) {
        grid.removeAllViews();
        String cur = null;
        for (Tile t : tiles) {
            if (!t.section.equals(cur)) {
                cur = t.section;
                grid.addView(sectionHeader(cur), Ui.lp(Ui.MATCH, Ui.WRAP));
            }
            grid.addView(rowView(t), Ui.lp(Ui.MATCH, Ui.WRAP));
        }
    }

'''
s = s[:a0] + new_block + s[a1:]
def rep(old, new, cnt=1):
    global s
    assert old in s, old[:70]
    s = s.replace(old, new, cnt)
# colours + sections
rep('        t.title = Media.TITLES[cat];\n', '        t.title = Media.TITLES[cat];\n        t.section = "קטגוריות";\n')
rep('''            t.color = v.primary ? 0xFF475569 : 0xFF4F46E5;
            t.soft = v.primary ? 0xFFEDF1F6 : 0xFFEEF0FE;
            t.title = v.label;''', '''            t.color = v.primary ? 0xFF2F6F9F : 0xFFB3472F;
            t.soft = v.primary ? 0xFFDDE9F2 : 0xFFF3E1DA;
            t.title = v.label;
            t.section = "אחסון";''')
rep('''            t.color = 0xFF334155;
            t.soft = 0xFFEDF1F6;
            t.title = "אחסון מערכת";''', '''            t.color = 0xFF4A5A63;
            t.soft = 0xFFE4E7E8;
            t.title = "אחסון מערכת";
            t.section = "אחסון";''')
rep('''        d.color = 0xFF0EA5E9;
        d.soft = 0xFFE0F2FB;
        d.title = "הורדות";''', '''        d.color = 0xFF2F6F9F;
        d.soft = 0xFFDDE9F2;
        d.title = "הורדות";
        d.section = "קטגוריות";''')
rep('''        tr.color = 0xFF64748B;
        tr.soft = 0xFFEDF1F6;
        tr.title = "סל מיחזור";''', '''        tr.color = 0xFF6B665C;
        tr.soft = 0xFFE9E5DB;
        tr.title = "סל מיחזור";
        tr.section = "כלים";''')
rep('''        an.color = 0xFFDB2777;
        an.soft = 0xFFFCE7F3;
        an.title = "ניתוח אחסון";''', '''        an.color = 0xFF7A4B63;
        an.soft = 0xFFEBDDE4;
        an.title = "ניתוח אחסון";
        an.section = "כלים";''')
# favourites: flat rows, no rounded tile / dividers inset
rep('''            if (i > 0) favBox.addView(Ui.divider(a), Ui.margins(Ui.lp(Ui.MATCH, Math.max(1, Ui.dp(1))), 68, 0, 0, 0));''',
    '''            if (i > 0) favBox.addView(Ui.divider(a));''')
rep('''            FrameLayout tile = new FrameLayout(a);
            tile.setBackground(Ui.round(0xFFFEF3DC, 12));
            tile.addView(Ui.img(a, R.drawable.ic_star, Ui.AMBER), new FrameLayout.LayoutParams(Ui.dp(22), Ui.dp(22), Gravity.CENTER));
            r.addView(tile, Ui.lp(Ui.dp(42), Ui.dp(42)));''', '''            r.addView(Ui.img(a, R.drawable.ic_star, Ui.AMBER), Ui.lp(Ui.dp(24), Ui.dp(24)));''')
if crlf: s = s.replace('\n', '\r\n')
open('HomeScreen.java', 'w', encoding='utf-8', newline='').write(s)
print('edited')
PYEOF
cd /c/orech_build/ext && PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -15
```

## 304. Bash: Apply style A edits and rebuild the APK
```
cd /c/orech_build/ext && PYTHONIOENCODING=utf-8 python -I apply_style_a.py 2>&1 | tail -5 && PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -12
```

## 305. Bash: Rebuild with permanent key and verify the new UI is in the APK
```
cd /c/orech_build/ext && grep -c "rowView" fmx_out/src/java/com/custom/files/HomeScreen.java; grep -n "boolean flat" fmx/app/src/main/java/com/custom/files/Ui.java | head -2
PYTHONIOENCODING=utf-8 python -I build_fmx.py 2>&1 | tail -6
unset MSYS_NO_PATHCONV
BT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/37.0.0"
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$BT/apksigner.bat" verify --print-certs C:/orech_build/ext/fmx_out/FileManager-Lite.apk 2>&1 | grep -E "Signer #1 certificate (DN|SHA-256)|WARNING|ERROR" | head -3
"$BT/aapt2.exe" dump badging C:/orech_build/ext/fmx_out/FileManager-Lite.apk | grep -E "^package:"
python -I -c "
import zipfile;z=zipfile.ZipFile(r'C:/orech_build/ext/fmx_out/FileManager-Lite.apk');d=z.read('classes.dex');print('rowView in dex:',b'rowView' in d,'| sectionHeader:',b'sectionHeader' in d,'| tileView:',b'tileView' in d)"
```

## 306. Bash: Verify signature, install and screenshot the new home screen
```
unset MSYS_NO_PATHCONV
BT="C:/Users/user1/AppData/Local/Android/Sdk/build-tools/37.0.0"
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"
"$BT/apksigner.bat" verify --verbose --print-certs C:/orech_build/ext/fmx_out/FileManager-Lite.apk 2>&1 | head -6
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" install -r C:/orech_build/ext/fmx_out/FileManager-Lite.apk 2>&1 | tail -2
"$ADB" shell "su -c 'appops set com.orech.files MANAGE_EXTERNAL_STORAGE allow'"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.orech.files/com.custom.files.MainActivity | tail -1"
sleep 4
"$ADB" exec-out screencap -p > "$SP/fm1.png"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 307. Bash: Check gitignore, identity and changes in fork
```
cd /c/orech_build/ext/fmx && cat .gitignore; git config user.name; git config user.email; git status --short
```

## 308. Bash: Add portable build script and verify it reproduces the APK
```
cd /c/orech_build/ext/fmx && printf '*.pass\nout/\n' >> .gitignore && export FMX_KEYSTORE='C:\orech_build\ext\keys\fmx_release.keystore' FMX_KEYPASS_FILE='C:\orech_build\ext\keys\fmx.pass' && PYTHONIOENCODING=utf-8 python -I build_orech.py 2>&1 | tail -4; cmp FileManager-Lite.apk /c/orech_build/ext/fmx_out/FileManager-Lite.apk && echo SAME_AS_TESTED || echo "differs (signature/timestamps), size: $(stat -c %s FileManager-Lite.apk) vs $(stat -c %s /c/orech_build/ext/fmx_out/FileManager-Lite.apk)"; git status --short
```

## 309. Bash: Commit locally in the fork; list stored GitHub credential targets
```
cd /c/orech_build/ext/fmx && printf '*.idsig\n' >> .gitignore && git add .gitignore build_orech.py app/src/main/java/com/custom/files/FileAdapter.java app/src/main/java/com/custom/files/HomeScreen.java app/src/main/java/com/custom/files/Media.java app/src/main/java/com/custom/files/Ui.java && git status --short && git commit -q -F - <<'EOF'
Flat list home screen, new colour tokens, portable build script

- Home screen: dark header, three sections (storage, categories, tools), one flat row per
  entry with a colour stripe at the start edge and a square usage bar; no rounded cards.
- Colour tokens: warm paper background, ink text, steel-blue accent, earthy category colours.
- App bar is flat (no elevation). Ripple/selection colours follow the new accent.
- build_orech.py builds the lite variant (no video, no app install) under a chosen package
  name and signs it with a persistent key taken from FMX_KEYSTORE / FMX_KEYPASS_FILE.
- Ignore *.pass, *.idsig and the out/ folder.

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>
EOF
git log --oneline -2; git status --short
echo "--- saved git credentials (targets only):"; cmdkey /list 2>/dev/null | grep -iE -B1 -A3 "github" | head -12
```

## 310. Bash: Back up signing key, update APK copy and handoff
```
mkdir -p "/c/Users/user1/Documents/fmx-keys-backup" && cp /c/orech_build/ext/keys/fmx_release.keystore /c/orech_build/ext/keys/fmx.pass "/c/Users/user1/Documents/fmx-keys-backup/" && ls -l "/c/Users/user1/Documents/fmx-keys-backup/"
cp /c/orech_build/ext/fmx_out/FileManager-Lite.apk "/c/פרוייקטים קלוד/zamir/FileManager-Lite_com.orech.files.apk"
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read().rstrip()
assert s.endswith('</div>')
s=s[:-6].rstrip('\n')+'''

## 10. סייר קבצים מובנה (`com.orech.files`)
- **מקור:** פורק של המשתמש, `github.com/shmuel-sinay/file-manager-x`, משוכפל ב-`C:/orech_build/ext/fmx` (`origin` = הפורק, `upstream` = `Xnet2/file-manager-x`). commit מקומי `ebf62e2`, **לא נדחף** (אין התחברות ל-GitHub בסביבה הזו; לא להשתמש בטוקן של אחרים).
- **מה הוא:** וריאנט lite (ללא וידיאו, בלי `REQUEST_INSTALL_PACKAGES`), שם חבילה `com.orech.files`, שם "מנהל קבצים". מסך הבית הוחלף לרשימה שטוחה (סגנון א שנבחר): כותרת כהה, מקטעים, פס צבע בצד. במניפסט אין `video/` ואין `INSTALL_PACKAGES`, ולכן הבדיקות במתקין וב-vguard לא חוסמות אותו.
- **בנייה:** `FMX_KEYSTORE` ו-`FMX_KEYPASS_FILE` מוגדרים, ואז `python -I build_orech.py` מתוך `ext/fmx`. סקריפט נוסף ב-`ext/build_fmx.py`.
- **חתימה קבועה:** `C:/orech_build/ext/keys/fmx_release.keystore` (RSA 4096, תקף עד 2059, alias `fmx`, סיסמה ב-`fmx.pass` באותה תיקייה) + עותק גיבוי ב-`Documents/fmx-keys-backup`. **אסור לקמיט, ואם המפתח אובד אי אפשר לעדכן עותקים מותקנים.** `ext/fmx_release.keystore` הישן הוא זמני ולא בשימוש.
- **נבדק:** נבנה, נחתם (v2/v3 אומתו), הותקן במכשיר, מסך הבית נצפה בצילום, 0 קריסות. **לא נבדק:** שאר המסכים בעיצוב החדש (תיקיות, הגדרות, חיפוש), התקנה כאפליקציית מערכת/מובנית, ופעולות קבצים.
- **לא התחיל:** הפיכתו למובנה במערכת (תיקיית `/product/app`), רשימת היתרים, וסריקות מבוססות אירועים.
</div>
'''
open(p,'w',encoding='utf-8',newline='\n').write(s)
print('ctrl',sum(1 for c in s if ord(c)<32 and c!='\n'))
PYEOF
```

## 311. Bash: Verify gh login account and local branch
```
gh auth status 2>&1 | head -6; cd /c/orech_build/ext/fmx && git branch --show-current && git log --oneline -2 && git remote get-url origin
```

## 312. Bash: Push the commit to the user's fork
```
cd /c/orech_build/ext/fmx && git show --stat --format= HEAD | tail -8 && git push origin main 2>&1 | tail -6; echo ---; git ls-remote origin main
```

## 313. Bash: Check key repo name is free and fork visibility
```
gh repo view shmuel-sinay/orech-keys --json name,visibility 2>&1 | head -3; gh repo view shmuel-sinay/file-manager-x --json visibility -q .visibility 2>&1
```

## 314. Bash: Create private repo and push the signing key
```
D=/c/orech_build/ext/keys_repo; mkdir -p $D && cd $D && cp /c/orech_build/ext/keys/fmx_release.keystore /c/orech_build/ext/keys/fmx.pass . && cat > README.md <<'EOF'
# Signing key for com.orech.files (file manager)

PRIVATE repository. Do not make it public and do not copy these files into any other repository.

- `fmx_release.keystore`: RSA 4096, alias `fmx`, valid until 2059.
- `fmx.pass`: the keystore and key password.
- Used by `build_orech.py` in `file-manager-x` through FMX_KEYSTORE and FMX_KEYPASS_FILE.
- If this key is lost, installed copies of the app cannot be updated; if it leaks, anyone can sign an update.
EOF
git init -q -b main && git add . && git -c core.autocrlf=false commit -q -m "Signing key for the file manager

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>" && gh repo create shmuel-sinay/orech-keys --private --source . --remote origin --push 2>&1 | tail -3
gh repo view shmuel-sinay/orech-keys --json visibility,url -q '.visibility + " " + .url'; git ls-files
```

## 315. Bash: Deploy file manager as built-in app in /product/app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
APK=/c/orech_build/ext/fmx/FileManager-Lite.apk
SHA=$(sha256sum $APK | cut -d' ' -f1); SZ=$(stat -c %s $APK); echo "$SHA $SZ"
printf '%s\n' 'set -e' 'B=/mnt/vendor/protect_s/orech' 'touch $B/noenforce' \
 "[ \"\$(wc -c < /data/local/tmp/orechfiles.apk)\" = \"$SZ\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/orechfiles.apk\" | sha256sum -c -" \
 'pm uninstall com.orech.files || true' \
 'mount -o remount,rw /product' \
 'mkdir -p /product/app/OrechFiles' \
 'cat /data/local/tmp/orechfiles.apk > /product/app/OrechFiles/OrechFiles.apk' \
 'chmod 755 /product/app/OrechFiles; chmod 644 /product/app/OrechFiles/OrechFiles.apk' \
 'chown root:root /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'chcon --reference=/product/app/OrechMockLoc /product/app/OrechFiles' \
 'chcon --reference=/product/app/OrechMockLoc/OrechMockLoc.apk /product/app/OrechFiles/OrechFiles.apk' \
 'sync' \
 "echo \"$SHA  /product/app/OrechFiles/OrechFiles.apk\" | sha256sum -c -" \
 'touch /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'rm -rf /data/system/package_cache' \
 'grep -qx /product/app/OrechFiles/OrechFiles.apk $B/paths.txt || echo /product/app/OrechFiles/OrechFiles.apk >> $B/paths.txt' \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'ls -laZ /product/app/OrechFiles; tail -3 $B/paths.txt' > /c/orech_build/t_v9/deploy_files.sh
"$ADB" push $APK /data/local/tmp/orechfiles.apk | tail -1
"$ADB" push C:/orech_build/t_v9/deploy_files.sh /data/local/tmp/deploy_files.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_files.sh'"
```

## 316. Bash: Retry deployment with Windows path for push
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" push C:/orech_build/ext/fmx/FileManager-Lite.apk /data/local/tmp/orechfiles.apk | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_files.sh'"
```

## 317. Bash: Finish built-in deployment steps manually
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SHA=2416a510aad42075aba7b2e6f0f16f1369649c5e71a36c95a3d93ff2149390f9
printf '%s\n' 'B=/mnt/vendor/protect_s/orech' \
 'ls -laZ /product/app/OrechFiles' \
 'chcon u:object_r:system_file:s0 /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'sync' \
 "echo \"$SHA  /product/app/OrechFiles/OrechFiles.apk\" | sha256sum -c -" \
 'touch /product/app/OrechFiles /product/app/OrechFiles/OrechFiles.apk' \
 'rm -rf /data/system/package_cache' \
 'grep -qx /product/app/OrechFiles/OrechFiles.apk $B/paths.txt || echo /product/app/OrechFiles/OrechFiles.apk >> $B/paths.txt' \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'ls -laZ /product/app/OrechFiles; tail -2 $B/paths.txt; ls $B | grep noenforce' > /c/orech_build/t_v9/deploy_files2.sh
"$ADB" push C:/orech_build/t_v9/deploy_files2.sh /data/local/tmp/deploy_files2.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_files2.sh'"
```

## 318. Bash: Reboot and verify the built-in app registered as system app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "rm -f /data/local/tmp/orechfiles.apk /data/local/tmp/deploy_files.sh /data/local/tmp/deploy_files2.sh"
"$ADB" reboot
sleep 45
for i in $(seq 1 40); do B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5; done
echo "boot_completed=$B (~$((45+i*5))s)"
"$ADB" shell "pm list packages -f -s com.orech.files; dumpsys package com.orech.files | grep -E 'codePath|flags=|versionCode' | head -3; logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell "su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; tail -2 /mnt/vendor/protect_s/orech/integrity.log'"
```

## 319. Bash: Grant storage access and launch the built-in file manager
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "su -c 'appops set com.orech.files MANAGE_EXTERNAL_STORAGE allow; cmd appops write-settings'; cmd appops get com.orech.files MANAGE_EXTERNAL_STORAGE"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.orech.files/com.custom.files.MainActivity | tail -1"
sleep 4
"$ADB" exec-out screencap -p > "$SP/fm2.png"
"$ADB" shell "logcat -d | grep -c 'FATAL EXCEPTION'; pm list packages -d | grep -c com.orech.files"
```

## 320. Bash: Update handoff and changelog for built-in app and key repo
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read().rstrip()
assert s.endswith('</div>')
a='- **לא התחיל:** הפיכתו למובנה במערכת (תיקיית `/product/app`), רשימת היתרים, וסריקות מבוססות אירועים.'
assert a in s
s=s.replace(a,'''- **מובנה במערכת (בוצע):** `/product/app/OrechFiles/OrechFiles.apk` (root:root, 755/644, הקשר `system_file`, sha256 `2416a510...`, 190036 בייט). הועתק עם `cat` (הקובץ קטן, יש מקום), הוסר העותק המותקן הקודם (`pm uninstall`), `package_cache` נמחק, הנתיב נוסף ל-`paths.txt` בבקרת השלמות (`approved 17 files`), אותחל. אומת: `flags=[ SYSTEM ...]`, 0 קריסות, `check` ריק. הרשאת `MANAGE_EXTERNAL_STORAGE` ניתנה ב-appops (`allow`). `chcon --reference` לא נתמך במכשיר, להשתמש ב-`chcon u:object_r:system_file:s0`.
- **עדכון האפליקציה בעתיד:** לבנות עם `build_orech.py` (אותו מפתח), לכתוב ל-`/product/app/OrechFiles/` (`mount -o remount,rw /product`), `rm -rf /data/system/package_cache`, `approve` ואתחול. הקובץ בבקרת השלמות: בלי `approve` המכשיר יכבה כשהבקרה פעילה.
- **גיבוי המפתח:** מאגר **פרטי** `github.com/shmuel-sinay/orech-keys` (מכיל `fmx_release.keystore` ו-`fmx.pass`). המפתח לא הועלה לפורק `file-manager-x` כי הוא ציבורי. לא להפוך את מאגר המפתחות לציבורי.
- **עדיין לא התחיל:** סריקות מבוססות אירועים במקום סריקות הרקע, ובדיקת שאר המסכים בעיצוב החדש.''',1)
open(p,'w',encoding='utf-8',newline='\n').write(s+'\n')
print('ctrl',sum(1 for c in s if ord(c)<32 and c!='\n'))
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- סייר קבצים מובנה `com.orech.files` ב-`/product/app/OrechFiles` (ללא וידיאו וללא הרשאת התקנה, חתום במפתח קבוע, בבקרת השלמות); גיבוי מפתח במאגר פרטי. פירוט ב-HANDOFF סעיף 10.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
PYEOF
grep -c "מובנה במערכת (בוצע)" HANDOFF_M393.md
```

## 321. Bash: Collect current device and repo state for the handoff
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" devices | tail -2
"$ADB" shell "getprop sys.boot_completed; cat /proc/uptime | cut -d' ' -f1; getprop ro.build.display.id; getprop persist.sys.orech_id; logcat -d | grep -c 'FATAL EXCEPTION'; df -h /data | tail -1; pm list packages | grep -E 'orech|gpsconn|vlc|alphainventor'"
"$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16; ls /data/system/orech_allow_install 2>&1; ls /mnt/vendor/protect_s/orech | grep noenforce; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; wc -l < /data/system/orech_vguard/blacklist.txt'"
echo "--- orech_build git:"; cd /c/orech_build && git branch --show-current && git log --oneline -3 && git status --short | grep -vE 'png$|r[0-9]+\.sh$' | head -15
echo "--- fork:"; cd /c/orech_build/ext/fmx && git log --oneline -1 && git status --short | head -3
```

## 322. Bash: Read blacklist and disabled packages
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'echo BL=[\$(cat /data/system/orech_vguard/blacklist.txt | tr \"\\n\" \" \")]; tail -6 /data/system/orech_vguard/vguard.log'"
"$ADB" shell "pm list packages -d | grep -E 'alphainventor|vlc|orech'; echo ---; dumpsys package com.alphainventor.filemanager | grep -E 'lastUpdateTime|installerPackageName' | head -2"
```

## 323. Bash: Add pointers to the full session doc and verify no control chars
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='HANDOFF_M393.md'
s=open(p,encoding='utf-8').read()
marker='לקרוא לפני שנוגעים במכשיר'
i=s.index(marker)
j=s.index('\n',i)
add='\n\n> **סשן אחרון (22.11.2026): תיעוד מלא ב-`SESSION_2026-11-22_FULL.md`** (מצב מכשיר, מה נעשה, החלטות, מה נשאר). קרא אותו מיד אחרי הקובץ הזה.'
if 'SESSION_2026-11-22_FULL.md' not in s:
    s=s[:j]+add+s[j:]
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CLAUDE.md',encoding='utf-8').read().rstrip()
if 'SESSION_2026-11-22_FULL.md' not in c:
    c+='\nאחרי זה לקרוא `SESSION_2026-11-22_FULL.md` (תיעוד הסשן האחרון ומה שנשאר).\n'
open('CLAUDE.md','w',encoding='utf-8',newline='\n').write(c)
print(sum(1 for ch in open('SESSION_2026-11-22_FULL.md',encoding='utf-8').read() if ord(ch)<32 and ch not in '\n'), sum(1 for ch in s if ord(ch)<32 and ch!='\n'))
PYEOF
cat CLAUDE.md; grep -n "SESSION_2026" HANDOFF_M393.md | head -2; wc -l SESSION_2026-11-22_FULL.md
```

## 324. Bash: Check the orech repo visibility, branches and layout
```
gh repo view shmuel-sinay/orech --json name,visibility,defaultBranchRef,pushedAt,description -q '.name+" | "+.visibility+" | "+.defaultBranchRef.name+" | "+.pushedAt+" | "+.description' 2>&1
gh api repos/shmuel-sinay/orech/branches --jq '.[].name' 2>&1 | head
echo --- tree; gh api repos/shmuel-sinay/orech/git/trees/main --jq '.tree[] | .type[0:1]+" "+.path' 2>&1 | head -30
cd /c/orech_build && git remote -v && git branch -a | head
```

## 325. Bash: Uninstall blacklisted user apps and clean the blacklist
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cat > /c/orech_build/t_v9/rmbl.sh <<'EOF'
BL=/data/system/orech_vguard/blacklist.txt
echo "before: [$(cat $BL | tr '\n' ' ')]"
for p in $(cat $BL); do
  if pm list packages -s | grep -qx "package:$p"; then echo "SKIP system app $p"; continue; fi
  echo "uninstall $p: $(pm uninstall $p 2>&1)"
done
: > /dev/null
for p in $(cat $BL); do pm path $p >/dev/null 2>&1 || sed -i "/^$p\$/d" $BL; done
echo "after: [$(cat $BL | tr '\n' ' ')]"
pm list packages | grep -E 'vlc|alphainventor' | wc -l
EOF
"$ADB" push C:/orech_build/t_v9/rmbl.sh /data/local/tmp/rmbl.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/rmbl.sh; rm -f /data/local/tmp/rmbl.sh'"
"$ADB" shell "su -c 'tail -2 /data/system/orech_vguard/vguard.log'; pm list packages | grep -E 'orech|gpsconn'"
```

## 326. PowerShell: Poll adb for the device, read-only
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$r=""
for($i=0;$i -lt 12;$i++){ $r=(& $adb devices | Select-Object -Skip 1 | Where-Object {$_.Trim()}) -join "; "; if($r -match 'device$'){break}; Start-Sleep 5 }
"after ~$($i*5)s devices=[$r]"
```

## 327. Bash: Wait for boot, restore list and uninstall safely
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 40); do B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5; done
echo "boot_completed=$B (~$((i*5))s)"
sleep 10
cat > /c/orech_build/t_v9/rmbl2.sh <<'EOF'
BL=/data/system/orech_vguard/blacklist.txt
pm list packages >/dev/null 2>&1 || { echo "package service not ready, abort"; exit 1; }
# restore the two entries removed by the earlier faulty run, if missing
for p in org.videolan.vlc com.alphainventor.filemanager; do grep -qx "$p" $BL || echo "$p" >> $BL; done
echo "before: [$(cat $BL | tr '\n' ' ')]"
for p in $(cat $BL); do
  if pm list packages -s | grep -qx "package:$p"; then echo "SKIP system app $p"; continue; fi
  out=$(pm uninstall $p 2>&1); echo "uninstall $p: $out"
  case "$out" in *Success*) sed -i "/^$p\$/d" $BL;; esac
done
echo "after: [$(cat $BL | tr '\n' ' ')]"
echo "still installed: $(pm list packages | grep -cE 'vlc|alphainventor')"
EOF
"$ADB" push C:/orech_build/t_v9/rmbl2.sh /data/local/tmp/rmbl2.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/rmbl2.sh; rm -f /data/local/tmp/rmbl2.sh'"
"$ADB" shell "pm list packages | grep -E 'orech|gpsconn'; logcat -d | grep -c 'FATAL EXCEPTION'; su -c 'sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?'"
```

## 328. Bash: Clone the public orech repo and inspect docs layout
```
cd /c/orech_build/ext && GIT_TERMINAL_PROMPT=0 git clone --depth 1 https://github.com/shmuel-sinay/orech orech_pub 2>&1 | tail -1; cd orech_pub && git log --oneline -3 && ls docs research tools | head -40 && sed -n 1,25p README.md && git ls-files | wc -l
```

## 329. Bash: Inspect keys folder in the public repo
```
cd /c/orech_build/ext/orech_pub && ls -la research/keys | head; git ls-files research/keys | head; sed -n 1,25p docs/safe_edit_workflow.md; git ls-files | grep -iE 'keystore|\.jks|\.pk8|\.pem|\.pass|\.key' | head
```

## 330. Bash: Scan public doc for sensitive terms and control characters
```
cd /c/orech_build/ext/orech_pub && f=docs/m393_changes_and_editing.md && echo "forbidden-term scan:" && grep -nEi 'noenforce|allow_install|protect_s|BROM|fmx\.pass|password|kt3182319|g47ben|T123|0123456789ABCDEF|orech-keys|blacklist|allowlist|integrity|שלמות|root|boot\.img|bootloader|X89U1' $f || echo "clean"; python -I -c "
s=open(r'$f',encoding='utf-8').read(); print('ctrl chars:',sum(1 for c in s if ord(c)<32 and c!=chr(10)))"
```

## 331. Bash: Push the public doc and create the private full-docs repo
```
cd /c/orech_build/ext/orech_pub && git add docs/m393_changes_and_editing.md && git -c core.autocrlf=false commit -q -F - <<'EOF'
Add a public summary of the M393 changes and how to edit safely

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>
EOF
git push origin main 2>&1 | tail -2; git ls-remote origin main | cut -c1-12; git log --oneline -1
echo "=== private full docs"
D=/c/orech_build/ext/private_docs; mkdir -p $D && cd $D && cp "/c/פרוייקטים קלוד/zamir/HANDOFF_M393.md" "/c/פרוייקטים קלוד/zamir/CHANGELOG_M393.md" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md" . && cat > README.md <<'EOF'
# Private operational documentation (M393)

PRIVATE. Full handoff, changelog and session log for the device customisation. These files describe protection
mechanisms and how they work; do not publish them and do not copy them into the public `orech` repository.
The public, filtered summary is `docs/m393_changes_and_editing.md` in `shmuel-sinay/orech`.
EOF
git init -q -b main && git add . && git -c core.autocrlf=false commit -q -m "Full operational documentation for the M393 customisation

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>" && gh repo create shmuel-sinay/orech-private-docs --private --source . --remote origin --push 2>&1 | tail -2
gh repo view shmuel-sinay/orech-private-docs --json visibility,url -q '.visibility+" "+.url'; git ls-files
```

## 332. Bash: Prepare vguard v10 (uninstall instead of disable), syntax check only
```
cd /c/orech_build/deploy && python -I - <<'PYEOF'
s=open('orech_vguard_v8.sh',encoding='utf-8',newline='').read()
assert '\r' not in s
old=s[s.index('blacklist() {'):s.index('pkg_of() {')]
new='''blacklist() {
  pm list packages -s | grep -qx "package:$1" && { echo "$(date '+%F %T') system app, not removed: $1 ($2)" >> "$LOG"; return; }
  case "$1" in com.orech.*) echo "$(date '+%F %T') own app, not removed: $1 ($2)" >> "$LOG"; return ;; esac
  # removed for good; no permanent ban is kept, so the package can be installed again later if it is allowed
  out=$(pm uninstall "$1" 2>&1)
  echo "$(date '+%F %T') uninstalled $1 ($2): $out" >> "$LOG"
}
'''
s=s.replace(old,new,1)
loop='''  # keep blacklisted apps disabled
  for p in $(cat "$BL" 2>/dev/null); do
    pm list packages -d | grep -qx "package:$p" || { pm path "$p" >/dev/null 2>&1 && blacklist "$p" "re-enabled"; }
  done
'''
assert loop in s
s=s.replace(loop,'',1)
s=s.replace('# orech video guard v5.','# orech video guard v10 (candidate, not deployed): offending packages are uninstalled instead of disabled.',1)
assert 'pm uninstall' in s and 'keep blacklisted' not in s
open('orech_vguard_v10.sh','w',encoding='utf-8',newline='\n').write(s)
print(len(s.encode()))
PYEOF
export MSYS_NO_PATHCONV=1; ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" push C:/orech_build/deploy/orech_vguard_v10.sh /data/local/tmp/t10.sh | tail -1
"$ADB" shell "sh -n /data/local/tmp/t10.sh && echo SYNTAX_OK; rm -f /data/local/tmp/t10.sh"
diff /c/orech_build/deploy/orech_vguard_v8.sh /c/orech_build/deploy/orech_vguard_v10.sh | head -30
```

## 333. Bash: Update session docs and sync them to the private repo
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='SESSION_2026-11-22_FULL.md'
s=open(p,encoding='utf-8').read()
i=s.index('- **`org.videolan.vlc` ו-`com.alphainventor.filemanager`')
j=s.index('\n',i)
s=s[:i]+'- **VLC (`org.videolan.vlc`) ו-File Manager + (`com.alphainventor.filemanager`) הוסרו מהמכשיר** (`pm uninstall` החזיר `Success` לשניהם) והרשימה השחורה `blacklist.txt` ריקה. לא נשמר איסור קבוע: בהמשך אפשר להתקין חבילה כזו שוב אם היא מותרת (בדיקות ההתקנה במתקין עדיין חוסמות אפליקציות עם `video/` במניפסט, ולכן צריך לתת לחבילה היתר: להוסיף ל-`allowlist.txt` ולבצע את ההתקנה כשבדיקת ההתקנה מכובה בצורה מבוקרת).'+s[j:]
i=s.index('- ביומן `vguard.log` נרשמת אותה השבתה')
j=s.index('\n',i)
s=s[:i]+'- ההשבתה החוזרת כל כ-2 דקות ביומן נפסקה כי החבילות הוסרו. במדיניות העתידית (v10) חבילה שנחסמת מוסרת פעם אחת ולא נשארת ברשימה.'+s[j:]
k=s.index('1. **להחליט מה עושים ב-VLC')
l=s.index('\n',k)
s=s[:k]+'1. **v10 של `vguard` (מוכן, לא נפרס):** `C:/orech_build/deploy/orech_vguard_v10.sh` (תחביר נבדק ב-`sh -n` על המכשיר בלבד). הוראת המשתמש: חבילה שנחסמת **מוסרת** (`pm uninstall`) ולא רק מושבתת, בלי איסור קבוע; חבילות מערכת ו-`com.orech.*` מדולגות. לפרוס רק אחרי תיאום עם הסשן "המשך מסעיף 6" (נשלחה לו הודעה) ובדיקה סינתטית (למשל קובץ `libffmpeg_test.so` באפליקציית בדיקה ואימות שהיא מוסרת). הוסר גם הלולאה "keep blacklisted apps disabled". עדיין שיפור הסוללה (סעיף 2) בנפרד.'+s[l:]
s=s.replace('- ניתוח זמני (ניתן למחוק)','- **תיעוד ב-GitHub:** מאגר ציבורי `shmuel-sinay/orech` קיבל `docs/m393_changes_and_editing.md` (מסונן, בלי מנגנוני הגנה; commit `fdca116` ב-`main`). התיעוד המלא (HANDOFF, CHANGELOG, וקובץ הסשן) נמצא במאגר **פרטי** `shmuel-sinay/orech-private-docs`. לא לפרסם אותו בציבורי: הוא כולל פרטי בקרת השלמות ודרכי עקיפה. אחרי שינוי בקבצים כאן יש לסנכרן ידנית למאגר הפרטי.\n- ניתוח זמני (ניתן למחוק)',1)
bad=sum(1 for c in s if ord(c)<32 and c!='\n'); print('ctrl',bad); assert bad==0
open(p,'w',encoding='utf-8',newline='\n').write(s)
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- VLC ו-File Manager + הוסרו (הרשימה השחורה ריקה, ללא איסור קבוע). הוכן `vguard v10` (הסרה במקום השבתה), לא נפרס. תיעוד ציבורי מסונן נדחף ל-`orech`, ותיעוד מלא למאגר פרטי.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
PYEOF
cp HANDOFF_M393.md CHANGELOG_M393.md SESSION_2026-11-22_FULL.md /c/orech_build/ext/private_docs/ && cd /c/orech_build/ext/private_docs && git add -A && git -c core.autocrlf=false commit -q -m "Sync documentation: removals, v10 plan, public/private split

Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>" && git push 2>&1 | tail -1
grep -c "orech-private-docs" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md"
```

## 334. Bash: Compare deployed v10 with the file and check device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cd /c/orech_build/deploy && ls -l orech_vguard_v*.sh && sha256sum orech_vguard_v10.sh | cut -c1-16 && diff orech_vguard_v8.sh orech_vguard_v10.sh
echo "--- device:"; "$ADB" shell "getprop sys.boot_completed; su -c 'sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16; ls /data/system/orech_allow_install 2>&1; ls /mnt/vendor/protect_s/orech | grep noenforce; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; getprop init.svc.orech_vguard; cat /data/system/orech_vguard/blacklist.txt | wc -l'; logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 335. Bash: Pull deployed vguard and diff against v8
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'cp /vendor/bin/orech_vguard.sh /data/local/tmp/vg_dev.sh; chmod 644 /data/local/tmp/vg_dev.sh'"
"$ADB" pull /data/local/tmp/vg_dev.sh C:/orech_build/t_v9/vg_device_v10.sh | tail -1
"$ADB" shell "rm -f /data/local/tmp/vg_dev.sh"
cd /c/orech_build && sha256sum t_v9/vg_device_v10.sh | cut -c1-16; wc -c < t_v9/vg_device_v10.sh
diff deploy/orech_vguard_v8.sh t_v9/vg_device_v10.sh
```

## 336. Bash: Rename candidate to v11 and deploy it with backup and verification
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
cd /c/orech_build/deploy && cp /c/orech_build/t_v9/vg_device_v10.sh orech_vguard_v10_deployed.sh && mv orech_vguard_v10.sh orech_vguard_v11.sh && sed -i '2s/.*/# orech video guard v11: offending packages are uninstalled instead of disabled (system apps and com.orech.* are skipped)./' orech_vguard_v11.sh && sed -n 1,3p orech_vguard_v11.sh
SHA=$(sha256sum orech_vguard_v11.sh | cut -d' ' -f1); SZ=$(stat -c %s orech_vguard_v11.sh); echo "$SHA $SZ"
printf '%s\n' 'set -e' 'B=/mnt/vendor/protect_s/orech' 'touch $B/noenforce' \
 "[ \"\$(wc -c < /data/local/tmp/t11.sh)\" = \"$SZ\" ] || { echo SIZE_MISMATCH; exit 1; }" \
 "echo \"$SHA  /data/local/tmp/t11.sh\" | sha256sum -c -" \
 'sh -n /data/local/tmp/t11.sh' \
 'mount -o remount,rw /vendor' \
 'cp /vendor/bin/orech_vguard.sh /data/local/tmp/orech_vguard.v10dev.bak' \
 'dd if=/data/local/tmp/t11.sh of=/vendor/bin/orech_vguard.sh conv=notrunc 2>&1' \
 "truncate -s $SZ /vendor/bin/orech_vguard.sh" 'sync' \
 "echo \"$SHA  /vendor/bin/orech_vguard.sh\" | sha256sum -c -" \
 'sh $B/orech_integrity.sh approve' \
 'echo check=[$(sh $B/orech_integrity.sh check)]' \
 'stop orech_vguard; start orech_vguard; sleep 3; getprop init.svc.orech_vguard' > /c/orech_build/t_v9/deploy_v11.sh
"$ADB" push C:/orech_build/deploy/orech_vguard_v11.sh /data/local/tmp/t11.sh | tail -1
"$ADB" push C:/orech_build/t_v9/deploy_v11.sh /data/local/tmp/deploy_v11.sh | tail -1
"$ADB" shell "su -c 'sh /data/local/tmp/deploy_v11.sh; rm -f /data/local/tmp/t11.sh /data/local/tmp/deploy_v11.sh'"
```

## 337. Bash: Confirm v11 is currently live on device
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; getprop init.svc.orech_vguard; cat /data/system/orech_vguard/blacklist.txt | wc -l'; logcat -d | grep -c 'FATAL EXCEPTION'"
sha256sum /c/orech_build/deploy/orech_vguard_v11.sh | cut -c1-16
```

## 338. Bash: Update and sync session doc with v11 state
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='SESSION_2026-11-22_FULL.md'
s=open(p,encoding='utf-8').read()
old_start='1. **v10 של `vguard` (מוכן, לא נפרס):**'
i=s.index(old_start)
j=s.index('\n2. **סריקות מבוססות אירועים')
new='''1. **`vguard` v11 (פרוס, הסרה חלקית אומתה):** הסשן "המשך מסעיף 6" פרס v10 משלו (תיקון רישום כפול בהשבתה, sha256 `a3e947a6...`, נשמר כ-`deploy/orech_vguard_v10_deployed.sh`). לקחתי את זה כבסיס והוספתי "הסרה (`pm uninstall`) במקום השבתה" לפי הוראת המשתמש; חבילות מערכת ו-`com.orech.*` מדולגות, בלי רשימת איסור קבועה. זה `deploy/orech_vguard_v11.sh` (sha256 `f6e4f976...`, 4955 בייט), **פרוס על המכשיר**. אומת: `approve`, אתחול נקי, `check` ריק, 0 קריסות, `noenforce` קיים. **לא אומת סינתטית** שחבילה חדשה שנחסמת אכן מוסרת (ההסרה הקודמת של VLC/File Manager+ הייתה ידנית, לא דרך הקוד הזה). שני הסשנים תואמו (הודעות הדדיות): לפני כל פריסה ל-`/vendor/bin/orech_vguard.sh` לבדוק קודם את ה-sha256 הנוכחי במכשיר.'''
s=s[:i]+new+s[j:]
bad=sum(1 for c in s if ord(c)<32 and c!='\n'); assert bad==0
open(p,'w',encoding='utf-8',newline='\n').write(s)
PYEOF
cp "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md" /c/orech_build/ext/private_docs/ && cd /c/orech_build/ext/private_docs && git add -A && git -c core.autocrlf=false commit -q -m "Document vguard v11 deployment and cross-session coordination

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>" && git push 2>&1 | tail -1
```

## 339. Bash: Inspect Magisk version, db tool availability, seller app uid
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'ls -l /data/adb/magisk.db; getprop ro.build.version.sdk; dumpsys package com.topjohnwu.magisk | grep -E \"versionName|versionCode\"'"
"$ADB" shell "dumpsys package com.orech.seller | grep -E 'userId='"
"$ADB" shell "su -c 'sqlite3 /data/adb/magisk.db .tables 2>&1 || echo NO_SQLITE3'"
"$ADB" shell "su -c 'ls /system/bin/sqlite3 /system/xbin/sqlite3 2>&1'"
```

## 340. Bash: Use magisk's built-in sqlite interface to inspect policy and settings tables
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'magisk --help 2>&1 | head -30'"
echo ---
"$ADB" shell "su -c \"magisk --sqlite 'select * from policies'\""
echo ---settings
"$ADB" shell "su -c \"magisk --sqlite 'select * from settings'\""
echo ---schema
"$ADB" shell "su -c \"magisk --sqlite \\\".schema\\\"\" 2>&1 | head -20"
```

## 341. Bash: Check root_access setting, table list, denylist, and identify uid 10127
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c \"magisk --sqlite \\\"select * from settings where key='root_access'\\\"\""
"$ADB" shell "su -c \"magisk --sqlite \\\"select name from sqlite_master where type='table'\\\"\""
echo ---denylist
"$ADB" shell "su -c 'magisk --denylist status; magisk --denylist ls 2>&1 | head -10'"
echo ---uid10127
"$ADB" shell "for p in \$(pm list packages | sed 's/package://'); do u=\$(dumpsys package \$p 2>/dev/null | grep -m1 'userId=' | sed 's/.*userId=//'); [ \"\$u\" = \"10127\" ] && echo \$p; done"
echo ---resetprop
"$ADB" shell "su -c 'resetprop -p persist.magisk.superuser 2>&1; getprop persist.magisk.superuser; getprop persist.sys.disable_su 2>&1'"
```

## 342. Bash: Launch Magisk app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.topjohnwu.magisk/.ui.MainActivity" 2>&1 | tail -2
sleep 4
"$ADB" exec-out screencap -p > "$SP/mg1.png"
```

## 343. Bash: Launch Magisk app
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.topjohnwu.magisk/.ui.MainActivity" 2>&1 | tail -2
sleep 4
"$ADB" exec-out screencap -p > "$SP/mg1.png"
```

## 344. Bash: Open Magisk app settings gear
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "input tap 32 67"; sleep 2
"$ADB" exec-out screencap -p > "$SP/mg2.png"
```

## 345. Bash: Scroll down Magisk settings
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "input swipe 200 800 200 200 400"; sleep 1
"$ADB" exec-out screencap -p > "$SP/mg3.png"
```

## 346. PowerShell: Wait for device reconnect, read-only
```
$adb="C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$r=""
for($i=0;$i -lt 12;$i++){ $r=(& $adb devices | Select-Object -Skip 1 | Where-Object {$_.Trim()}) -join "; "; if($r -match 'device$'){break}; Start-Sleep 5 }
"after ~$($i*5)s devices=[$r]"
```

## 347. Bash: Re-navigate to Magisk settings and scroll
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "getprop sys.boot_completed; logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.topjohnwu.magisk/.ui.MainActivity" | tail -1; sleep 3
"$ADB" shell "input tap 32 67"; sleep 2
"$ADB" shell "input swipe 200 800 200 200 400"; sleep 1
"$ADB" exec-out screencap -p > "$SP/mg3.png"
```

## 348. Bash: Wait for boot and check reboot reason / integrity
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
for i in $(seq 1 30); do B=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'); [ "$B" = "1" ] && break; sleep 5; done
echo "boot_completed=$B (~$((i*5))s)"
"$ADB" shell "getprop ro.boot.bootreason; getprop sys.boot.reason; logcat -d | grep -c 'FATAL EXCEPTION'"
"$ADB" shell "su -c 'tail -4 /mnt/vendor/protect_s/orech/integrity.log; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce'"
```

## 349. Bash: Re-navigate to Magisk settings and scroll, device now up
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "input keyevent KEYCODE_WAKEUP; am start -n com.topjohnwu.magisk/.ui.MainActivity" | tail -1; sleep 3
"$ADB" shell "input tap 32 67"; sleep 2
"$ADB" shell "input swipe 200 800 200 200 400"; sleep 1
"$ADB" exec-out screencap -p > "$SP/mg3.png"
```

## 350. Bash: Open Superuser access setting
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "input tap 200 912"; sleep 2
"$ADB" exec-out screencap -p > "$SP/mg4.png"
```

## 351. Bash: Cancel dialog and scroll further for more superuser options
```
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
SP="C:/Users/user1/AppData/Local/Temp/claude/C--Users-user1--claude-spares-spare-aib61A/b4a1b9f4-acb5-4092-933f-897217e378d3/scratchpad"
"$ADB" shell "input tap 94 630"; sleep 1
"$ADB" shell "input swipe 200 800 200 200 400"; sleep 1
"$ADB" exec-out screencap -p > "$SP/mg5.png"
```

## 352. Bash: Clone the user's Magisk fork read-only to inspect it
```
mkdir -p /c/orech_build/ext/magisk_fork && cd /c/orech_build/ext/magisk_fork && GIT_TERMINAL_PROMPT=0 git clone --depth 1 https://github.com/shmuel-sinay/Magisk . 2>&1 | tail -5
git log --oneline -5; git branch -a; git remote -v
cat README.md 2>/dev/null | head -20
ls
```

## 353. Bash: Inspect build config options for app package/name branding
```
cd /c/orech_build/ext/magisk_fork && cat config.prop.sample; echo ---; grep -rn "APP_PACKAGE_NAME\|APP_NAME\|applicationId\|package.*name" build.py app/build.gradle.kts 2>/dev/null | head -20; echo ---agents; cat AGENTS.md | head -40
```

## 354. Bash: Check build feasibility: WSL, Rust, NDK, submodules, package id location
```
cat /c/orech_build/ext/magisk_fork/docs/build.md 2>/dev/null | head -40
echo "--- WSL:"; which wsl.exe 2>&1; wsl.exe -l -v 2>&1 | head -5
echo "--- rust:"; which cargo rustc 2>&1
echo "--- ndk:"; ls "$LOCALAPPDATA/Android/Sdk/ndk" 2>&1
echo "--- python:"; python --version 2>&1
echo "--- git submodules:"; cd /c/orech_build/ext/magisk_fork && git submodule status 2>&1 | head -10
echo "--- app pkg id:"; grep -rn "applicationId\|namespace" app/apk/build.gradle.kts 2>/dev/null | head -10
find app -iname "*.properties" -o -iname "AndroidManifest.xml" | grep -v test | head -5
```

## 355. Bash: Find hardcoded package name references across native and app code
```
cd /c/orech_build/ext/magisk_fork && grep -rln "topjohnwu.magisk\|topjohnwu\\\\.magisk" --include=*.rs --include=*.cpp --include=*.hpp --include=*.h --include=*.kt --include=*.kts --include=*.java --include=*.xml native app | grep -v "/test" | head -40
```

## 356. Bash: Check constants, build files, and signature verification for the hardcoded identity
```
cd /c/orech_build/ext/magisk_fork && grep -n "topjohnwu\|APPLICATION_ID\|JAVA_PACKAGE_NAME\|applicationId\|namespace" native/src/include/consts.hpp native/src/include/consts.rs | head -30
echo ---gradle; grep -n "applicationId\|namespace" app/apk/build.gradle.kts app/core/build.gradle.kts app/build.gradle.kts 2>/dev/null
echo ---signature; grep -rln "cert\|signature\|Signature" native/src/core --include=*.cpp --include=*.rs | head -10
echo ---manifest; grep -n "package=" app/apk/src/main/AndroidManifest.xml app/core/src/main/AndroidManifest.xml 2>/dev/null
```

## 357. Bash: Read package.rs to understand manager identity and signature verification
```
cd /c/orech_build/ext/magisk_fork && sed -n '1,120p' native/src/core/package.rs
```

## 358. Bash: Find how TRUSTED_CERT is generated at build time
```
cd /c/orech_build/ext/magisk_fork && grep -rn "TRUSTED_CERT" native/src app/apk/build.gradle.kts build.py scripts 2>/dev/null | head -20
echo ---; grep -rn "check-signature\|gen_cert\|cert_hash\|signing" build.py | head -20
echo ---; grep -n "APP_NAME\b" native/src/include/consts.hpp native/src/include/consts.rs app/apk/build.gradle.kts 2>/dev/null
```

## 359. Bash: Read cert generation context and app name/applicationId locations
```
cd /c/orech_build/ext/magisk_fork && sed -n '270,320p' build.py
echo ---strings; grep -rn '"app_name"' app/apk/src/main/res/values/strings.xml | head -3
echo ---gradle_appid; grep -n "applicationId" app/apk/build.gradle.kts
```

## 360. Bash: One last lookup needed for the instructions: applicationId location
```
cd /c/orech_build/ext/magisk_fork && grep -n "applicationId\|namespace" app/apk/build.gradle.kts
```

## 361. Bash: Read app module gradle file for applicationId location
```
cd /c/orech_build/ext/magisk_fork && sed -n '1,40p' app/apk/build.gradle.kts
```

## 362. Bash: Verify v12 deployment claims from the other session, read-only
```
ls -l /c/orech_build/deploy/orech_vguard_v12.sh 2>&1
sha256sum /c/orech_build/deploy/orech_vguard_v12.sh 2>&1
export MSYS_NO_PATHCONV=1
ADB="/c/Users/user1/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell "su -c 'sha256sum /vendor/bin/orech_vguard.sh | cut -c1-16; sh /mnt/vendor/protect_s/orech/orech_integrity.sh check; echo CHECK_EXIT=\$?; ls /mnt/vendor/protect_s/orech | grep noenforce; ls /data/system/orech_allow_install 2>&1; cat /data/system/orech_vguard/blacklist.txt | wc -l; getprop init.svc.orech_vguard'; logcat -d | grep -c 'FATAL EXCEPTION'"
```

## 363. Bash: Update all three docs with v12 closure and sync to private repo
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='SESSION_2026-11-22_FULL.md'
s=open(p,encoding='utf-8').read()
old_start="1. **`vguard` v11 (פרוס, הסרה חלקית אומתה):**"
i=s.index(old_start)
j=s.index('\n2. **שאר המסכים')
new='''1. **`vguard` v12 (סגור, סעיף 6.2 הושלם):** הסשן "המשך מסעיף 6" השלים את שתי המשימות. קובץ: `deploy/orech_vguard_v12.sh`, sha256 `020833de41a1...`, 5511 בייט (בנוי על v11). **נבדק ואומת על ידי** (אני בדקתי read-only אחרי הדיווח): sha256 במכשיר תואם, `check` ריק, `noenforce` קיים, `blacklist.txt` ריק, אין דגל `orech_allow_install`, 0 קריסות, השירות `running`. בדיקות סינתטיות של הסשן השני (חבילות זמניות `com.zz.*`, נוקו): הסרה (`pm uninstall`, לא השבתה) תוך ~60 שניות לספרייה ב-`/data/data`; מסלול אירוע (`inotifyd`) על התקנה חדשה עם מפענח — נעילה והסרה תוך כ-3 שניות; מסלול טיימר על ספרייה שנוספה ל-`/data/data` אחרי ההתקנה — עד 5 דקות (טיימר 300 שניות, לעומת כ-60 שניות בגרסה הישנה; פשרה מודעת תמורת ביטול לולאת ה-2 שניות). המימוש: `inotifyd` על `/data/system` (שינויי חבילות) ועל תיקיית המצב, עם אירוע משולב `yn` (אירוע בודד `n` או `y` נדחה `Invalid argument` באינוטיפיי של המכשיר). `v10` (הביניים) לא נשמר בנפרד — התייתר.'''
s=s[:i]+new+s[j:]
bad=sum(1 for c in s if ord(c)<32 and c!='\n'); assert bad==0, bad
open(p,'w',encoding='utf-8',newline='\n').write(s)
h='HANDOFF_M393.md'
hs=open(h,encoding='utf-8').read()
old='גרסת השירות `orech_vguard` הפרוסה: **v8** (`deploy/orech_vguard_v8.sh`; v9 נוסה ובוטל, ראו סעיף 9).'
assert old in hs
hs=hs.replace(old,'גרסת השירות `orech_vguard` הפרוסה: **v12** (`deploy/orech_vguard_v12.sh`, sha256 `020833de41a1...`). שרשרת: v8 (בסיס) → v9 (נוסה ובוטל, סעיף 9) → v10 (תיקון הסשן השני) → v11 (הסרה במקום השבתה) → v12 (+ סריקות inotifyd). סעיף 9 מעודכן לפי המצב הסופי.',1)
bad=sum(1 for c in hs if ord(c)<32 and c!='\n'); assert bad==0, bad
open(h,'w',encoding='utf-8',newline='\n').write(hs)
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- `vguard v12` (הסשן "המשך מסעיף 6"): סריקות inotifyd במקום לולאת 2 שניות, הסרה (pm uninstall) במקום השבתה. נבדק סינתטית ואומת read-only. סעיף 6.2 ב-SESSION_2026-11-22_FULL.md סגור.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
PYEOF
cp "/c/פרוייקטים קלוד/zamir/HANDOFF_M393.md" "/c/פרוייקטים קלוד/zamir/CHANGELOG_M393.md" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md" /c/orech_build/ext/private_docs/ && cd /c/orech_build/ext/private_docs && git add -A && git -c core.autocrlf=false commit -q -m "Document vguard v12: event-based scans + uninstall-on-block, closed

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>" && git push 2>&1 | tail -1
```

## 364. Bash: Find the exact next-item heading text
```
cd "/c/פרוייקטים קלוד/zamir" && grep -n "^2\. \*\*" SESSION_2026-11-22_FULL.md | head -3
```

## 365. Bash: Replace stale v11 item and remove the now-resolved inotifyd TODO
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
p='SESSION_2026-11-22_FULL.md'
s=open(p,encoding='utf-8').read()
old_start="1. **`vguard` v11 (פרוס, הסרה חלקית אומתה):**"
i=s.index(old_start)
j=s.index('2. **סריקות מבוססות אירועים (סוללה)**')
jend=s.index('\n3. **שאר המסכים')
new='''1. **`vguard` v12 (סגור, סעיף 6.2 הושלם):** הסשן "המשך מסעיף 6" השלים את שתי המשימות שהיו פתוחות (הסרה במקום השבתה, וסריקות מבוססות אירועים). קובץ: `deploy/orech_vguard_v12.sh`, sha256 `020833de41a1...`, 5511 בייט (בנוי על v11). **נבדק ואומת על ידי** (קריאה בלבד, אחרי הדיווח): sha256 במכשיר תואם, `check` ריק, `noenforce` קיים, `blacklist.txt` ריק, אין דגל `orech_allow_install`, 0 קריסות, השירות `running`. בדיקות סינתטיות של הסשן השני (חבילות זמניות `com.zz.*`, נוקו): הסרה (`pm uninstall`, לא השבתה) תוך ~60 שניות לספרייה ב-`/data/data`; מסלול אירוע (`inotifyd`) על התקנה חדשה עם מפענח — נעילה והסרה תוך כ-3 שניות; מסלול טיימר על ספרייה שנוספה ל-`/data/data` אחרי ההתקנה — עד 5 דקות (טיימר 300 שניות, לעומת כ-60 שניות בגרסה הישנה; פשרה מודעת תמורת ביטול לולאת ה-2 שניות). המימוש: `inotifyd` על `/data/system` (שינויי חבילות) ועל תיקיית המצב, עם אירוע משולב `yn` (אירוע בודד `n` או `y` נדחה `Invalid argument` באינוטיפיי של המכשיר). `v10` (הביניים) לא נשמר בנפרד — התייתר.'''
s=s[:i]+new+s[jend:]
bad=sum(1 for c in s if ord(c)<32 and c!='\n'); assert bad==0, bad
assert 'סריקות מבוססות אירועים (סוללה)' not in s
open(p,'w',encoding='utf-8',newline='\n').write(s)
PYEOF
grep -n "^[0-9]*\. \*\*" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md" | sed -n '1,8p'
```

## 366. Bash: Confirm section 6 numbering context around the v12 replacement
```
cd "/c/פרוייקטים קלוד/zamir" && grep -n "vguard.*v12 (סגור\|שאר המסכים\|^## 6" SESSION_2026-11-22_FULL.md
```

## 367. Bash: Renumber section 6, update HANDOFF/CHANGELOG, sync private repo
```
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
import re
p='SESSION_2026-11-22_FULL.md'
s=open(p,encoding='utf-8').read()
start=s.index('## 6. מה נשאר')
end=s.index('\n## 7.')
block=s[start:end]
lines=block.split('\n')
n=1
out=[]
for l in lines:
    m=re.match(r'^(\d+)\. (\*\*.*)$', l)
    if m:
        out.append(f'{n}. {m.group(2)}')
        n+=1
    else:
        out.append(l)
s=s[:start]+'\n'.join(out)+s[end:]
bad=sum(1 for c in s if ord(c)<32 and c!='\n'); assert bad==0, bad
open(p,'w',encoding='utf-8',newline='\n').write(s)
PYEOF
grep -n "^[0-9]*\. \*\*" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md" | awk -F: '$1>53'
echo "---"
cd "/c/פרוייקטים קלוד/zamir" && python -I - <<'PYEOF'
h='HANDOFF_M393.md'
hs=open(h,encoding='utf-8').read()
old='גרסת השירות `orech_vguard` הפרוסה: **v8** (`deploy/orech_vguard_v8.sh`; v9 נוסה ובוטל, ראו סעיף 9).'
assert old in hs
hs=hs.replace(old,'גרסת השירות `orech_vguard` הפרוסה: **v12** (`deploy/orech_vguard_v12.sh`, sha256 `020833de41a1...`). שרשרת: v8 (בסיס) → v9 (נוסה ובוטל, סעיף 9) → v10 (תיקון הסשן השני) → v11 (הסרה במקום השבתה) → v12 (+ סריקות inotifyd, סעיף 6.2 ב-SESSION_2026-11-22_FULL.md סגור).',1)
bad=sum(1 for c in hs if ord(c)<32 and c!='\n'); assert bad==0, bad
open(h,'w',encoding='utf-8',newline='\n').write(hs)
c=open('CHANGELOG_M393.md',encoding='utf-8').read().rstrip()
c=c[:-6].rstrip('\n')+'\n- `vguard v12` (הסשן "המשך מסעיף 6"): סריקות inotifyd במקום לולאת 2 שניות, הסרה (pm uninstall) במקום השבתה. נבדק סינתטית ואומת read-only במכשיר. סעיף 6.2 ב-SESSION_2026-11-22_FULL.md סגור.\n\n</div>\n'
open('CHANGELOG_M393.md','w',encoding='utf-8',newline='\n').write(c)
PYEOF
cp "/c/פרוייקטים קלוד/zamir/HANDOFF_M393.md" "/c/פרוייקטים קלוד/zamir/CHANGELOG_M393.md" "/c/פרוייקטים קלוד/zamir/SESSION_2026-11-22_FULL.md" /c/orech_build/ext/private_docs/ && cd /c/orech_build/ext/private_docs && git add -A && git -c core.autocrlf=false commit -q -m "Document vguard v12: event-based scans + uninstall-on-block, closed

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>" && git push 2>&1 | tail -1
```

