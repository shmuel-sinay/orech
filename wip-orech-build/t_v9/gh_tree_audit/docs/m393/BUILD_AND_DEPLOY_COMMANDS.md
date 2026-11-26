<div dir="rtl">

# הפקודות המדויקות: בנייה, פריסה ושחזור (M393)

כל מה שנעשה בפועל, באותו סדר ובאותן פקודות. הסקריפטים המלאים נמצאים ב-`deploy_scripts/` ובתיקייה `tools/`. כללי הברזל ב-`HANDOFF_M393.md` גוברים על כל פקודה כאן.

## 0. סביבה
- Windows, PowerShell. אין Python. Node 22 ו-JBR (Java) של Android Studio זמינים.
- `ADB=C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- `BT=C:\Users\user1\AppData\Local\Android\Sdk\build-tools\37.0.0` (aapt2, zipalign, apksigner, d8)
- `JAVA_HOME=C:\Program Files\Android\Android Studio\jbr`, `ANDROID_HOME=C:\Users\user1\AppData\Local\Android\Sdk`
- gradle: `C:\Users\user1\.gradle\wrapper\dists\gradle-9.0.0-bin\d6wjpkvcgsg3oed0qlfss3wgl\gradle-9.0.0\bin\gradle.bat`
- **Gradle ו-aapt2 לא עובדים מנתיב עם עברית.** שכפול ASCII: `C:\orech_build` (`git pull "C:\פרוייקטים קלוד\orech" feature/seller-version-picker`). `C:\orech_b` הוא Junction לתיקייה העברית ולא מתאים.
- מפתחות החתימה: `research/keys/platform.pk8` + `platform.x509.pem` (תעודה `c8a2e9bc…2ab8`, זהה למכשיר).

## 1. עריכת dex (services.jar, MtkSettings, GooglePackageInstaller)
`tools/dex_edit/DexEdit.java` עם ה-classpath מ-`tools/dex_edit/classpath.txt`. מצבי עבודה: `show`, `grep`, `align`, `edit`. `edit` מחליף מתודה, מתודה עם `subst:` (תחליף טקסטואלי, התאמה אחת בדיוק), או מחלקה שלמה (`-`).
```powershell
$cp=(Get-Content tools\dex_edit\classpath.txt -Raw).Trim()
& "$env:JAVA_HOME\bin\java.exe" -Xmx4g -cp $cp tools\dex_edit\DexEdit.java edit <in> <out> '<Lclass;>' <method|-|subst:method> <file> [...]
```
- חובה לראות בפלט `dex version kept: 39`.
- הרצה אחת עובדת על dex אחד. MtkSettings: מחלקות `com/android/settings/*` ב-`classes.dex`, `settingslib` ב-`classes2.dex`. עושים שתי הרצות.
- התיקונים עצמם: `tools/dex_edit/patches_m393/*`. מחלקות VideoGuard: `tools/dex_edit/installer_guard/`.

## 2. עריכת XML בינארי (הסרת שורות הגדרות)
```powershell
java -Xmx3g -cp <ARSCLib-1.3.5.jar> tools\arsc_xml_edit\XmlKeys.java patch <in.apk> <out.apk> <keys.txt>
```
שורות מפתחות: `path|key` להסרת אלמנט, `path|key|-attr` להסרת תכונה. הקבצים בפועל ב-`build_inputs/keys*.txt`. אימות: `aapt2 dump xmltree --file <path> <apk>` וקוד יציאה 0 (לא לבדוק היעדר מחרוזת).

## 3. אריזה וחתימה של APK
```powershell
& "$BT\zipalign.exe" -f -p 4 un.apk al.apk
& "$BT\apksigner.bat" sign --key platform.pk8 --cert platform.x509.pem --out Out.apk al.apk
& "$BT\apksigner.bat" verify --print-certs Out.apk ; & "$BT\zipalign.exe" -c -p 4 Out.apk
```

## 4. שכבות תרגום (RRO) ואפליקציית המיקום
- שכבת תרגום: מקור `tools/overlays/settings_he/`. `aapt2 compile --dir res -o res.zip`, `aapt2 link -o un.apk -I android.jar --manifest AndroidManifest.xml res.zip --min-sdk-version 33 --target-sdk-version 33`, ואז חתימה. לבנות מנתיב ASCII (`C:\orech_build\ov`). יעד: `/vendor/overlay/OrechSettingsHe/OrechSettingsHe.apk`.
- אפליקציית "מיקום ב-USB" (`tools/mockloc`): `aapt2 compile` ו-`link --java gen`, `javac -encoding UTF-8 -source 17 -target 17`, `d8 --lib android.jar --min-api 33`, הוספת `classes.dex` ל-apk, `zipalign`, חתימה בפלטפורמה. יעד: `/product/app/OrechMockLoc/OrechMockLoc.apk`. תג בהגדרות: פעולה `com.android.settings.action.IA_SETTINGS` (לא `SETTINGS`), קטגוריה `com.android.settings.category.ia.system`.
- אפליקציית המוכר: `gradle :seller:assembleDebug` מ-`C:\orech_build`.

## 5. פריסה למכשיר (תבנית)
כתיבה בטוחה: דחיפה ל-`/data/local/tmp`, אימות sha256, `dd conv=notrunc`, `truncate -s <גודל>`, אימות sha256 על היעד (פונקציית `WriteSys` ב-`deploy_scripts/deploy4.ps1` עד `deploy8.ps1`). אחרי כל אתחול: `mount -o remount,rw /` (או `/vendor`, `/product`).
```sh
# בתוך su, דרך קובץ סקריפט (לא מירכאות מקוננות):
B=/mnt/vendor/protect_s/orech
touch $B/noenforce                      # לפני פריסת קבצים מנוטרים
# ... כתיבת הקבצים ...
sh $B/orech_integrity.sh approve        # אישור המצב החדש
echo check=[$(sh $B/orech_integrity.sh check)]   # צריך להיות ריק
```
שינוי APK בתיקיית מערכת במקום לא נקלט בעלייה: גם `touch` לתיקייה ולקובץ, ו-`rm -rf /data/system/package_cache`, ואז אתחול.
- רשימת הקבצים המנוטרים: `deploy_scripts/paths.txt` (LF, שורה אחרונה עם ירידת שורה, אחרת הערכים מתמזגים).
- שירות הוידיאו: `deploy_scripts/orech_vguard_v*.sh` (המאוחר ביותר שנפרס ואומת: v8; יש גם v9 עד v12 מסשנים מקבילים, ראה `SESSION_2026-11-22_FULL.md`). רשימות במכשיר: `/data/system/orech_vguard/`.

## 6. build.prop
דגם `ro.product.model=Zamir`, `ro.build.display.id=Kosher Tech`. לפני כתיבה: גודל 8103 בתים, 233 שורות. כתיבה ב-`dd conv=notrunc` + `truncate`, אימות sha256, אתחול.

## 7. בקרת שלמות
- קבצים: `deploy_scripts/orech_integrity.sh` (מצבים `boot`, `monitor`, `approve`, `check`), `orech_integrity.rc` (שירות `post-fs-data` וניטור), `orech_integrity_launcher.sh` (גיבוי ב-`/data/adb/service.d`).
- מצב בדיקות: `noenforce` קיים. מצב מופעל: נמחק. בדיקה מדומה ללא כיבוי: `touch testbreak`.

## 8. התאוששות
| תקלה | פתרון |
|---|---|
| קריסת `system_server` ("exactly one sdk sandbox package") | adb עם root: `rm /data/system/users/0/package-restrictions.xml`, ריקון `/data/system/orech_vguard/disable_list.txt`, אתחול |
| `/system/build.prop` בגודל 0 | סריקת הבלוק של המחיצה, אחזור 8133 בתים, כתיבה עם בדיקת sha |
| המכשיר כבה מבקרת השלמות | התחברות adb תוך 120 שניות מהעלייה, `touch noenforce`, תיקון, `approve`. בכיבוי מוקדם אין חלון: צריבה מחדש |
| אשף ההגדרה תקוע אחרי איפוס ואין adb | `zamir/boot_unlock_oneshot.img` ל-`boot_a` (בנוי ב-`build_inputs/build.js`), ואחר כך צריבה חזרה של ה-boot המקורי המתוקן |

## 9. צריבה
SP Flash Tool עם `MT6765_Android_scatter.txt`, רק `boot_a`, Download Only. או `fastboot flash boot_a <file>`. אין לצרוב super או userdata בלי החלטה מפורשת. ה-`boot.img` בתיקייה הראשית של `גירסת יצרן m393` הוא המתוקן עם Magisk ושייך למכשיר. זה שבתיקיית המשנה הוא stock בלי root.

## 10. קבצים שלא בריפו, ולמה
קבצי firmware של היצרן ושל Google (`services.jar`, `MtkSettings.apk`, תמונות boot ו-super, GPS Connector) ובינאריים שנבנו מהם אינם בריפו. הם נשמרו בגיבוי המקומי `D:\Backups\M393_2026-11-26`. כדי לשחזר: ליטול את המקורות מה-ROM ולהריץ את הצינור שלמעלה. ה-hash של כל קובץ ב-`CHANGELOG_M393.md`.

</div>
