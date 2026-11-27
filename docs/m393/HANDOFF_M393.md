<div dir="rtl">

# העברת עבודה: M393 (הזמיר). לקרוא לפני שנוגעים במכשיר

> **סשן אחרון (22.11.2026): תיעוד מלא ב-`SESSION_2026-11-22_FULL.md`** (מצב מכשיר, מה נעשה, החלטות, מה נשאר). קרא אותו מיד אחרי הקובץ הזה.

מסמך זה נכתב כדי שסשן חדש לא יחזור על טעויות. היומן המלא של מה שנעשה: `CHANGELOG_M393.md` באותה תיקייה. כל כלל כאן כתוב בצורה "אל תעשה X, אלא אם Y". אם יש פתרון, הוא כתוב מיד אחריו.

## 1. מצב המכשיר (נכון לסוף הסשן האחרון, המכשיר כבוי)
- Android 13, MT6765, root דרך Magisk מ-boot מעודכן, bootloader פתוח, SELinux במצב Permissive ואי אפשר לאכוף אותו.
- דגם `Zamir`, Build (`ro.build.display.id`) = `כושר טק - כל הזכויות שמורות | kt3182319@gmail.com` (היה `Kosher Tech`; הקובץ המקורי ב-`zamir/build.prop.orig_before_branding`, sha256 `5ce02a1d...`, 8103 בייט; בגרסה החדשה 8040 בייט, הוסרו שורות הערה כדי לא לחרוג מהגודל המקורי), serial מקורי `0123456789ABCDEF` עד שהמוכר מגדיר מספר.
- גרסת השירות `orech_vguard` הפרוסה: **v12** (`deploy/orech_vguard_v12.sh`, sha256 `020833de41a1...`). שרשרת: v8 (בסיס) → v9 (נוסה ובוטל, סעיף 9) → v10 (תיקון הסשן השני) → v11 (הסרה במקום השבתה) → v12 (+ סריקות inotifyd, סעיף 6.2 ב-SESSION_2026-11-22_FULL.md סגור). קבצים פרוסים: `services_v4.jar`, `MtkSettings_v5.apk`, `GooglePackageInstaller_v4.apk`, `OrechSettingsHe_v2.apk` (שכבת תרגום), `OrechMockLoc.apk`, `MtkSystemUI_v3.apk`. כולם מהתיקייה `zamir\m393` או `C:\orech_build\deploy`.
- אחרי אתחול אחסון המשתמש נעול עד הקשת קוד. בזמן הזה `am start` של אפליקציה חדשה נכשל ("does not exist"), ואי אפשר לצלם את ההגדרות מ-adb. זה לא באג. בקש מהמשתמש לשחרר נעילה לפני בדיקות ויזואליות.

## 2. כללי ברזל
1. **אל תשבית חבילת Google או apex אלא אם בדקת ש-PackageManager לא דורש אותה.** `com.google.android.sdksandbox` חובה: בלעדיה `system_server` קורס בעלייה ("exactly one sdk sandbox package") והמכשיר נכנס ל-recovery. תיקון אם קרה: `adb` עם root, מחיקת `/data/system/users/0/package-restrictions.xml`, אתחול (הקובץ נוצר מחדש), וריקון `/data/system/orech_vguard/disable_list.txt` כדי שהשירות לא ישבית שוב.
2. **אל תנסה להשבית WebView.** המערכת מפעילה אותו מחדש מעצמה. מחיקת ה-APK שלו תגרום לקריסות באפליקציות.
3. **אל תכתוב קובץ מערכת מ-pull שלא אימתת.** כבר נכתב `/system/build.prop` בגודל 0 בגלל pull שנכשל. לפני כל כתיבה: בדוק שהקובץ המקומי קיים, גודלו ומספר השורות שלו סבירים, ורק אז כתוב. השחזור היה מסריקת הבלוק.
4. **אל תשתמש ב-`cp` או ב-`>` לכתיבה ל-`/system`, אלא אם הקובץ החדש קטן או שווה למקורי.** המחיצה כמעט מלאה (93%) והכתיבה קטעה קבצים ושברה אפליקציות. שיטה נכונה: `dd if=... of=... conv=notrunc` ואז `truncate -s <גודל>`, ובדיקת sha256. אחרי כל אתחול צריך `mount -o remount,rw /` (או `/vendor`, `/product`).
5. **אל תפרוס קובץ מהרשימה בבקרת השלמות בלי `approve`, אלא אם אתה מוכן שהמכשיר יכבה.** הרשימה: `services.jar`, MtkSettings, MtkSystemUI, GooglePackageInstaller, `build.prop`, `orech_vguard.sh`, ה-rc של השירות והבקרה, ארבעת קבצי ה-codecs, `OrechMockLoc.apk`. אחרי פריסה: `sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve`. אם המכשיר כבר מכבה את עצמו: התחבר ב-adb תוך 120 שניות מהעלייה ו-`touch /mnt/vendor/protect_s/orech/noenforce`, תקן, `approve`, ומחק את `noenforce`.
6. **אל תערוך קבצי dex בכלי שכותב גרסה נמוכה מ-039.** DexPool כתב 035 וזה גרם ל-`IncompatibleClassChangeError` באחסון בהגדרות. הכלי `tools/dex_edit/DexEdit.java` שומר את גרסת המקור. חייבים לבדוק `dex version kept: 39` בפלט.
7. **אל תניח שמחלקות באותה אפליקציה נמצאות באותו קובץ dex.** `DexEdit` עובד על dex אחד בכל הרצה: ב-MtkSettings `classes.dex` ו-`classes2.dex` (settingslib) דורשים שתי הרצות נפרדות.
8. **אל תסיר מההגדרות את "מידע על הטלפון", "שימוש בנתונים" או "אפשרויות מפתחים".** הסרה גרמה לקריסות. מה שבוצע בפועל מתועד ב-`tools/arsc_xml_edit`.
9. **אל תאמת עריכת XML בבדיקת "המחרוזת נעלמה".** עריכה שנכשלה נראית כמו 0 התאמות. חובה לבדוק שקוד היציאה של `aapt2 dump xmltree` הוא 0.
10. **אל תריץ `setenforce 1`.** נכשל ב-`Invalid argument`, דורש שינוי בליבה.
11. **אל תכתוב כתובת Bluetooth ל-NVRAM ידנית.** ה-HAL מחזיר אותה בגלל סכום ביקורת. הפתרון שעובד: `persist.service.bdroid.bdaddr` (השירות מגריל בעלייה ראשונה כתובת `02:xx:xx:xx:xx:xx`). לא נבדק מול מכשיר שני.
12. **אל תשים מזהה דגם או שם מכשיר בהודעות commit.** שורת הסיום שלהן: `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`.
13. **אל תחטט בקבצי הרום שהמשתמש העלה** (תיקיית "גירסת יצרן m393") בלי רשות מפורשת. אם צריך `boot.img` מקורי כדי להסיר root, תבקש רשות.
14. **אל תוריד או תתקין אפליקציות מפורומים.** מותר לנתח קבצים סטטית בלי להריץ. אל תעתיק קוד מ-VidBlocker (המחבר אוסר), רק את הכיוון.
15. **אל תסמוך על `adb shell "su -c '...'"` עם מירכאות מורכבות.** ב-PowerShell וב-Git Bash זה נשבר ("no closing quote"). כתוב סקריפט לקובץ עם LF, `adb push` ל-`/data/local/tmp`, והרץ `su -c 'sh /data/local/tmp/x.sh'`. בקבצי רשימות (`paths.txt`) חייב LF ושורה אחרונה עם ירידת שורה, אחרת הקבצים מתמזגים.
16. **אל תריץ gradle מנתיב עם עברית.** העבודה נעשית ב-`C:\orech_build` (ASCII), עם `ANDROID_HOME` ו-`JAVA_HOME` של Android Studio, ועם gradle מ-`~\.gradle\wrapper\dists`. `C:\orech_b` הוא Junction לתיקייה העברית ולא עובד.
17. **אל תכבה את המחשב בסוף אם המשתמש כתב "אל תכבה".** ההוראה האחרונה קובעת. כיבוי המכשיר (`reboot -p`) הוא דבר נפרד ונעשה רק לפי בקשה.

## 3. ארכיטקטורת החסימות, בקצרה
- **codecs:** קודקי וידיאו הוסרו מקבצי `media_codecs_*.xml` ב-vendor.
- **התקנה:** `services.jar` (`InstallPackageHelper.preparePackageLI`) והמתקין (`processPackageUri`) חוסמים APK עם `video/` או `INSTALL_PACKAGES` במניפסט, ועם ספריות פענוח לפי שם. עקיפה: הקובץ `/data/system/orech_allow_install` (ליצור לפני התקנה ולמחוק אחריה).
- **שירות `orech_vguard`:** כל דקה סורק `/data/app` ו-`/data/data`, נועל ספריות פענוח לפי שם, מנטרל אפליקציות עם `video/` במניפסט, שולל הרשאת התקנה לרשימה, מעניק מיקום מדומה לרשימה, מחזיק רשימה שחורה, ומשבית אפליקציות מערכת מהרשימה. רשימות ב-`/data/system/orech_vguard/` (`allowlist`, `deny_install`, `mock_allow`, `blacklist`, `disable_list`). אפליקציית מערכת שזוהתה רק ננעלת, לא מושבתת.
- **בקרת שלמות:** `/mnt/vendor/protect_s/orech/` (סקריפט, `paths.txt`, `manifest.sha256`, `integrity.log`). שני מפעילים: `/vendor/etc/init/orech_integrity.rc` ו-`/data/adb/service.d/orech_integrity.sh`.
- **מיקום מדומה:** `com.orech.mockloc` ב-`/product/app/OrechMockLoc/`. אריח ב"מערכת" (נדרש `ACCESS_MOCK_LOCATION` באפליקציית ה-GPS). לא נבדק מול ממשק, ויתכן שחסימת hidden API תמנע את `setMode`. גיבוי: `appops set <חבילה> android:mock_location allow` ורשימת `mock_allow.txt`.

## 4. מה לא נבדק (אל תציג כנבדק)
כיבוי אמיתי של בקרת השלמות, הופעת האריח של המיקום המדומה ופעולת המתגים, הרשימה השחורה וסריקת `/data/data` עם אפליקציה אמיתית, אפליקציית המוכר על מכשיר (כולל הזנת מספר מכשיר), מסך החסימה של המתקין בעיצוב החדש, כתובת Bluetooth מול מכשיר שני, פרצת WebView לוידיאו באתרים, הקוד הנכון לאפשרויות מפתחים (לא ניתן להקליד עברית דרך adb).

## 5. מה פתוח
1. לשחרר נעילה במכשיר ולאמת את שלושת הפריטים הראשונים בסעיף 4.
2. לבדוק אם WebView מנגן וידיאו, ואם כן לחסום את ספריית הפענוח שלו (לא להשבית את האפליקציה).
3. הסרת root בסוף תהליך המוכר: דורשת `boot.img` מקורי, ורק אחרי רשות. נעילת bootloader: רק אחרי בדיקה על מכשיר רזרבי.
4. חנות פנימית (התקנה עצמאית): ממתין להגדרה מהמשתמש.
5. SelfDestruct לא הופעל מעולם על המכשיר.
6. להכין ל-PatchEngine בדיקות אוטומטיות (אין כרגע, והכללים דורשים בדיקה לכל באג).

## 6. פקודות שימושיות
- adb: `C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe`.
- בדיקת שלמות: `sh /mnt/vendor/protect_s/orech/orech_integrity.sh check` (פלט ריק = תקין), `approve` לאישור מצב נוכחי.
- בדיקת שירות הוידיאו: `getprop init.svc.orech_vguard`, יומן ב-`/data/system/orech_vguard/vguard.log`.
- ניטור עלייה: `getprop sys.boot_completed`. עלייה תקינה אורכת כ-61 עד 68 שניות.
- קריסות: `logcat -d | grep -c 'FATAL EXCEPTION'` (צריך 0).


## 7. עדכונים מהסשן האחרון
- **בקרת השלמות כעת חוסמת עלייה** (`post-fs-data`). מי שמשנה קובץ מהרשימה בלי `approve` יגרום לכיבוי מיידי בעלייה הבאה, בלי חלון adb. לפני כל פריסה: `touch /mnt/vendor/protect_s/orech/noenforce`, לפרוס, `approve`, ו-`rm` של `noenforce`. אל תשאיר `noenforce` בסוף העבודה.
- **אל תבדוק כיבוי אמיתי בעלייה על המכשיר היחיד.** הוא ייכבה בכל הדלקה וצריך צריבה. הבדיקה המדומה: `touch testbreak` ושיבוש שורה במניפסט, ואז `integrity.log` מראה `TESTBREAK`.
- **אריחי הגדרות בגרסה הזו:** משתמשים ב-`com.android.settings.action.IA_SETTINGS` ולא ב-`SETTINGS`. אחרי שינוי באפליקציית מערכת: `touch` לתיקייה ולקובץ, `rm -rf /data/system/package_cache`, אתחול.
- **`setMode` של appops דורש `MANAGE_APP_OPS_MODES`** ולא רק `MANAGE_APPOPS`.
- המתג של המיקום המדומה נבדק ועובד. השירות לא מנהל יותר את `mock_allow.txt` (ריק).
- **המכשיר במצב בדיקות:** `noenforce` קיים, הבקרה לא מכבה. אל תמחק אותו ידנית עד לבדיקה הסופית. אפליקציית המוכר מפעילה את הבקרה.

## 8. משימות לסשן הבא (בסדר הזה, כל אחת עם הוראות מדויקות)
1. **אימות שלא נעשה:** (א) **בוצע ונבדק** (מספר בדיקה `T123` נשאר על המכשיר ובקובץ `sales.csv`; אפשר לשנות מהאפליקציה; שעון המכשיר שגוי ולכן התאריך ב-CSV שגוי): נמצא ותוקן באג, `$id` חסר בפקודות הכתיבה, כך שהמספר לא נכתב מעולם (שוחזר על המכשיר לפני התיקון: `orech_id` ריק, "השמירה נכשלה"). אחרי התיקון המספר ב-`/mnt/vendor/nvdata/orech_id`, ב-`persist.sys.orech_id`, ב-`sales.csv` ובהגדרות. עוד תוקן ב-SellerActivity: הבקרה מופעלת ואפליקציית המוכר מסירה את עצמה רק אם `approve` הצליח ו-`check` החזיר ריק (קודם הופעלה גם בכשל, ואז כיבוי בעלייה הבאה). **תיקון זה לא נבדק על מכשיר** ושלב "החל" לא הורץ. מקור: `seller-debug.apk` בתיקייה. תיאור המשימה המקורי: אפליקציית המוכר על המכשיר: להתקין `zamir\seller-debug.apk`, להזין מספר מכשיר בן 4 ספרות, לבדוק שהוא מופיע ב"מספר סידורי" בהגדרות ובקובץ `sales.csv`. (ב) **נבדק בצורה סינתטית** (קובץ דמה `libffmpeg_test.so` ב-`/data/data` של אפליקציית המוכר, לא אפליקציה אמיתית): הזיהוי עבד, `chmod 000`, כניסה ל-`blacklist.txt`, שורה ב-`vguard.log`, השבתה. הזיהוי לקח כ-95 שניות ולא "תוך דקה" (סריקת `/data/data` רצה רק ב-`apply_lists`, אחת ל-30 מחזורים). אחרי הבדיקה: להוציא מהרשימה לפני `pm enable`, אחרת השירות משבית שוב. הנוסח המקורי של המשימה: הרשימה השחורה: להתקין אפליקציה עם ספריית פענוח בשם `libffmpeg*.so` שמורדת ל-`/data/data/<חבילה>/files`, ולבדוק ש-`blacklist.txt` ו-`vguard.log` מתעדכנים תוך דקה. (ג) מסך חסימת המתקין: **נבדק (המשתמש פתח את blocktest.apk מ-Files by Google וראה את נוסח החסימה שלנו; ביומן: InstallStart ואז PackageInstallerActivity, החבילה לא הותקנה, 0 קריסות)**. הנוסח הנוכחי: טוסט "האפליקציה נחסמה: היא כוללת תמיכה בוידיאו" ואחריו דיאלוג שגיאה של המתקין. נוסחים נפרדים לפי סיבה (וידיאו / יכולת התקנת אפליקציות) הוצעו ולא יושמו. הודעה כללית "האפליקציה לא הותקנה" יכולה להגיע גם מחוסר מקום (נצפה: `Failed to allocate ... only 152986 allocatable`), לא רק מהחסימה. פרטי הבדיקה הקודמת שלא הושלמה:  נבנה APK בדיקה חתום עם `video/*` במניפסט (`C:/orech_build/t_blocktest/blocktest.apk`, בנוי עם aapt2+apksigner ומפתח debug). פתיחה במתקין: `file://` לא נפתר; צריך `content://`: להעתיק ל-`/sdcard/Download`, לאנדקס (`am broadcast MEDIA_SCANNER_SCAN_FILE`), לשלוף `_id` ב-`content query --uri content://media/external/file`, ואז `am start -W -a android.intent.action.INSTALL_PACKAGE -d content://media/external/file/<id> -t application/vnd.android.package-archive --grant-read-uri-permission` (חזר `Status: ok`, `InstallStart`, אך המסך לא נצפה). **בזמן הבדיקה סשנים אחרים עבדו על אותו מכשיר ואתחלו אותו** (`reboot,shell`), לכן לבדוק רק כשאין סשן אחר פעיל על המכשיר.  אל תתקין אפליקציות מפורומים. אם צריך APK לבדיקה, בקש מהמשתמש.
2. **WebView:** לבדוק אם סרטון מתנגן בדפדפן מבוסס WebView. אם כן, לא להשבית את `com.google.android.webview` (המערכת מפעילה אותו מחדש) אלא לבדוק חסימת ספריית הפענוח שלו בשם, בלי למחוק את ה-APK. לפני כל שינוי: גיבוי, ובדיקה שאחרי אתחול אין קריסות.
3. **הפעלת הבקרה בסוף:** המכשיר במצב בדיקות (`noenforce` קיים). במכשיר סופי הבקרה מופעלת רק על ידי אפליקציית המוכר. לבדיקה ידנית: `rm /mnt/vendor/protect_s/orech/noenforce` ורק אחרי ש-`orech_integrity.sh check` מחזיר ריק. **לא לבדוק כיבוי אמיתי בעלייה על המכשיר היחיד.**
4. **מגבלות הגנה שצריך להסביר למשתמש (לא להבטיח יותר):** סקריפט הבקרה והמניפסט נמצאים ב-`protect_s`. מי שמפעיל אותם הוא קובץ rc ב-vendor ומפעיל גיבוי של Magisk ב-`/data/adb/service.d`. אי אפשר להפעיל קוד ממחיצה נידחת כמו `protect1/2`, `nvdata` או `proinfo`, כי אנדרואיד מריץ קוד רק ממחיצות מערכת. לכן צריבה מלאה של כל המחיצות (SP Flash Tool במצב BROM) עוקפת הכל. נעילת bootloader מועילה רק אם גם "OEM unlocking" כבוי: יש לבדוק `getprop sys.oem_unlock_allowed` ואת מצב `seccfg`. בלי Secure Boot בחומרה אי אפשר למנוע פתיחה, רק להקשות. אם המשתמש רוצה להמשיך בכיוון: לבדוק האם ה-SoC תומך ב-SBC, ולהציע בדיקת `seccfg` לפני כל נעילה, על מכשיר רזרבי בלבד.
5. **הסרת root בסוף התהליך:** דורש `boot.img` מקורי מתיקיית היצרן ("גירסת יצרן m393"). **לבקש רשות מפורשת לפני שנוגעים בתיקייה.** אחרי הסרת root, `approve` כבר לא יעבוד דרך האפליקציה: לאשר את המצב לפני הצריבה.
6. **חנות פנימית (התקנה עצמאית):** להמתין להגדרה מהמשתמש. לא לנחש.
7. **בדיקות אוטומטיות:** קיימות 4 בדיקות ל-`ZipUtil` ב-`C:\orech_build\app\src\test\java\com\orech\patcher\ZipUtilTest.kt` (תלות `junit:junit:4.13.2`). הרצה (PowerShell): `JAVA_HOME=C:\Program Files\Android\Android Studio\jbr`, `ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk`, ואז מ-`C:\orech_build`: `gradle :app:testDebugUnitTest --console=plain` (gradle 9.0.0 מ-`~\.gradle\wrapper\dists`), תוצאות ב-`app\build\test-results\testDebugUnitTest`. תוצאה אחרונה: `tests="4" failures="0" errors="0"`; שיבוש יישור ל-1 גרם ל-`rewrite_alignsStoredEntriesTo4Bytes FAILED`. עדיין אין בדיקות ל-`PatchEngine`: `replaceMethod` פרטית ו-`apply` תלויה ב-`Context`/`RootShell`. להוציא את `replaceMethod` ו-`checkExpectedHash` למחלקת עזר ללא תלות באנדרואיד, ואז לבדוק ב-JVM.
9. **אל תשבית אפליקציות מערכת נוספות לשיפור זמן עלייה.** נמדד: אין שיפור (61 עד 73 שניות), והסיכון גבוה.
10. **משדר GPS ב-USB, מובנה במערכת (בקשת המשתמש, לא בוצע):** מטרה: להחליף את GPS Connector (אפליקציית חוץ שדורשת הורדה מהפורום) באפליקציה `com.orech.mockloc`, כך שהמשתמש בוחר התקן USB ו-baudrate (9600 כברירת מחדל) ומקבל מיקום מדומה. **לא להוריד ולהתקין את ה-APK מהפורום** (מקור לא מהימן). הוראות:
    - **קודם לבקש מהמשתמש לחבר את המשדר למכשיר** ולהריץ `adb shell dumpsys usb | grep -i -E "vid|pid|manufacturer|product"` כדי לדעת איזה שבב USB-סידורי יש בו (CDC-ACM של u-blox, CH340, CP210x, PL2303 או FTDI). בלי זה אי אפשר לבדוק, אל תכתוב מנהל "על עיוור".
    - המכשיר תומך ב-USB host (`android.hardware.usb.host`).
    - לממש בתוך `tools/mockloc`: מנהל USB לשבב שזוהה (`UsbManager`, `UsbDeviceConnection`, העברות בקרה להגדרת baudrate), קורא NMEA (`$GPGGA`, `$GPRMC`), ומזריק דרך `LocationManager.addTestProvider` ו-`setTestProviderLocation` לספקי `gps` ו-`network`. לאפליקציה צריך אישור `android:mock_location` לעצמה (אפשר עם `setMode`, כבר יש הרשאה).
    - שירות קדמי (foreground service) כדי שלא ייהרג, בחירת התקן ו-baudrate במסך האריח, הרשאת USB קבועה (`android.hardware.usb.action.USB_DEVICE_ATTACHED` עם device_filter).
    - אחרי כל פריסה: `approve`, `touch` לתיקיית האפליקציה, `rm -rf /data/system/package_cache`, אתחול.
    - לבדוק בשטח פתוח, הקליטה הראשונה לוקחת דקות. לצלם מצב בהגדרות מיקום ולוודא `dumpsys location`.
11. **שמירת מצב ההרשאות:** appops נכתב לדיסק באיחור. השירות (v8) מריץ `cmd appops write-settings` כל דקה, כדי ששינוי במתג מיקום מדומה יישמר גם אחרי אתחול. נבדק: הערך נשמר כשנכתב.
12. **עדכון לסעיף 10:** המשדר המובנה נבנה ("מיקום ב-USB" ב"מערכת"). הנותר: לחבר את משדר ה-u-blox ולבדוק קליטה אמיתית (`dumpsys location`, מצב במסך), לבדוק בלוטוס, ולהוסיף מנהלים לשבבי USB נוספים רק אם המשתמש מציג משדר כזה (קודם `dumpsys usb`). לא להוריד אפליקציות מהפורום.

## 9. ניסוי v9 שנדחה, והכיוון שהמשתמש בחר
- **v9 נפרס, נבדק ובוטל באותו סשן** לפי החלטת המשתמש. הפרוס בפועל: **v8** (sha256 `890863c7...`, 4938 בייט), הדגל `/data/system/orech_allow_install` **לא קיים** ובדיקות ההתקנה פעילות כמו קודם. המקור של v9 נשאר ב-`C:/orech_build/deploy/orech_vguard_v9.sh` לעיון בלבד (נעילת מפענחים בלי השבתה, השבתה רק כש"המפענח חוזר"; נבדק בבדיקות סינתטיות).
- **החלטות המשתמש:** (1) חסימת ההתקנה צריכה להישאר ולהיות חזקה יותר, לא להקל. (2) מוכן לוותר על File Manager + (גרסה מתוקנת מפורום; נחסמה בגלל `video/` במניפסט והרשאת `REQUEST_INSTALL_PACKAGES`, ואין בה ספריות native). (3) רוצה סייר קבצים **מובנה** ללא יכולת התקנה וללא תמיכה בוידיאו, וכל מה שיותקן יהיה רק דרכו. (4) **לא רוצה סריקות רקע תכופות**: שלילה חד-פעמית בהתקנה, בלי בזבוז סוללה והאטה.
- **מצב נוכחי של הסריקות (v8):** לולאה כל 2 שניות על `/data/app`, וסריקה מלאה של `/data/data` כל כ-60 שניות. לא נמדדה צריכת סוללה בפועל. `inotifyd` קיים ב-`/system/bin` ויכול להחליף חלק מהסריקות באירועים.
- **מגבלה ידועה של שלילת ההתקנה:** appop לכל חבילה (`cmd appops set <חבילה> REQUEST_INSTALL_PACKAGES deny`, בדיקה ידנית כ-root עבדה על `com.google.android.apps.nbu.files`, `EXIT=0`, מצב `deny`); מסך `Settings$ManageExternalSourcesActivity` נגיש למשתמש, ולכן זו לא אכיפה מלאה.
- **לא התחיל:** סייר קבצים מובנה, וסריקות מבוססות אירועים. אל תוריד אפליקציה לצורך זה בלי רשות מפורשת מהמשתמש.

## 10. סייר קבצים מובנה (`com.orech.files`)
- **מקור:** פורק של המשתמש, `github.com/shmuel-sinay/file-manager-x`, משוכפל ב-`C:/orech_build/ext/fmx` (`origin` = הפורק, `upstream` = `Xnet2/file-manager-x`). commit `ebf62e2` **נדחף** ל-`main` בפורק (נבדק עם `git ls-remote`). `gh` מחובר כ-`shmuel-sinay` (התחברות של המשתמש בדפדפן); לא להשתמש בטוקן של אחרים.
- **מה הוא:** וריאנט lite (ללא וידיאו, בלי `REQUEST_INSTALL_PACKAGES`), שם חבילה `com.orech.files`, שם "מנהל קבצים". מסך הבית הוחלף לרשימה שטוחה (סגנון א שנבחר): כותרת כהה, מקטעים, פס צבע בצד. במניפסט אין `video/` ואין `INSTALL_PACKAGES`, ולכן הבדיקות במתקין וב-vguard לא חוסמות אותו.
- **בנייה:** `FMX_KEYSTORE` ו-`FMX_KEYPASS_FILE` מוגדרים, ואז `python -I build_orech.py` מתוך `ext/fmx`. סקריפט נוסף ב-`ext/build_fmx.py`.
- **חתימה קבועה:** `C:/orech_build/ext/keys/fmx_release.keystore` (RSA 4096, תקף עד 2059, alias `fmx`, סיסמה ב-`fmx.pass` באותה תיקייה) + עותק גיבוי ב-`Documents/fmx-keys-backup`. **אסור לקמיט, ואם המפתח אובד אי אפשר לעדכן עותקים מותקנים.** `ext/fmx_release.keystore` הישן הוא זמני ולא בשימוש.
- **נבדק:** נבנה, נחתם (v2/v3 אומתו), הותקן במכשיר, מסך הבית נצפה בצילום, 0 קריסות. **לא נבדק:** שאר המסכים בעיצוב החדש (תיקיות, הגדרות, חיפוש), התקנה כאפליקציית מערכת/מובנית, ופעולות קבצים.
- **מובנה במערכת (בוצע):** `/product/app/OrechFiles/OrechFiles.apk` (root:root, 755/644, הקשר `system_file`, sha256 `2416a510...`, 190036 בייט). הועתק עם `cat` (הקובץ קטן, יש מקום), הוסר העותק המותקן הקודם (`pm uninstall`), `package_cache` נמחק, הנתיב נוסף ל-`paths.txt` בבקרת השלמות (`approved 17 files`), אותחל. אומת: `flags=[ SYSTEM ...]`, 0 קריסות, `check` ריק. הרשאת `MANAGE_EXTERNAL_STORAGE` ניתנה ב-appops (`allow`). `chcon --reference` לא נתמך במכשיר, להשתמש ב-`chcon u:object_r:system_file:s0`.
- **עדכון האפליקציה בעתיד:** לבנות עם `build_orech.py` (אותו מפתח), לכתוב ל-`/product/app/OrechFiles/` (`mount -o remount,rw /product`), `rm -rf /data/system/package_cache`, `approve` ואתחול. הקובץ בבקרת השלמות: בלי `approve` המכשיר יכבה כשהבקרה פעילה.
- **גיבוי המפתח:** מאגר **פרטי** `github.com/shmuel-sinay/orech-keys` (מכיל `fmx_release.keystore` ו-`fmx.pass`). המפתח לא הועלה לפורק `file-manager-x` כי הוא ציבורי. לא להפוך את מאגר המפתחות לציבורי.
- **עדיין לא התחיל:** סריקות מבוססות אירועים במקום סריקות הרקע, ובדיקת שאר המסכים בעיצוב החדש.

## 11. אשף הגדרה חלופי (`com.google.android.setupwizard`), נבדק 22.11.2026
פרטים מלאים ב-`SESSION_2026-11-22_FULL.md` סעיף 6א. בקצרה:
- **כלל ברזל חדש:** אל תפרוס שינוי ל-`/vendor/bin/orech_wizard.sh` או ל-APK של האשף בלי ליצור קודם `/data/misc/orech_keep_root` (עוצר את `finish()` לפני שהוא מוחק את כל ה-root ומאתחל). למחוק אותו רק אחרי שה-`finish()` האמיתי אושר ידנית.
- **PIN:** נבדק רק עם גיבוב **זמני** שנוצר לצורך הבדיקה, לא עם ה-PIN האמיתי של המוכר. לא לחשוף/להדפיס את ה-PIN האמיתי בשום פלט או לוג.
- **מצב נוכחי:** `noenforce` קיים (מצב בדיקות הוחזר), `version_chosen`/`sys.orech.chosen` אופסו (האשף יציג שוב את 3 השלבים בפעם הבאה שירוץ — דורש `pm enable com.google.android.setupwizard/.SetupWizardActivity` כי הוא השבית את עצמו בבדיקה האחרונה).
- **לא נבדק:** `finish()` האמיתי (מחיקת root, כיבוי adb, אתחול) עם ה-PIN האמיתי, ומסכי "שמור עם חנות פנימית"/"פתוחה".

## 9. תקלה: אחרי איפוס אשף ההגדרה נתקע ב"רק שנייה", וה-adb כבוי
- **תופעה:** אחרי איפוס יצרן האשף (`com.google.android.setupwizard`) לא מסיים, והמכשיר לא מגיע למסך הבית. אין adb כי הוא כבוי כברירת מחדל. הסיבה לא ידועה (אין יומנים).
- **פתרון חד-פעמי, בוצע בלי להשתמש במסך:** `zamir\boot_unlock_oneshot.img` הוא עותק של ה-boot המתוקן עם Magisk (זהה ל-`גירסת יצרן m393\boot.img`, hash `464CBDC0…`), פלוס `overlay.d/orech_setup.rc`. בעלייה (`sys.boot_completed=1`) ובפעם אחת (סימון `/data/misc/orech_unlock_done`) הוא מגדיר `device_provisioned=1`, `user_setup_complete=1`, מפעיל `adb_enabled`, ומוסיף את מפתח ה-adb של המחשב ל-`/data/misc/adb/adb_keys`. נוצר עם `tools/boot_unlock/build.js`. נבדק: הקרנל, ה-dtb והכותרת זהים למקור, והרמדיסק נפתח.
- **לא נבדק על מכשיר:** ההנחה ש-Magisk טוען קבצי `.rc` מ-`overlay.d` בשורש.
- **חובה אחרי השימוש:** לצרוב חזרה את `גירסת יצרן m393\boot.img` (המקורי המתוקן). אחרת איפוס יצרן אצל לקוח יפעיל adb בלי אישור. לא למכור מכשיר עם `boot_unlock_oneshot.img`.
- **שחזור אם לא עולה:** לצרוב את `גירסת יצרן m393\boot.img` ל-`boot_a`.
- **צריבה:** SP Flash Tool, scatter `MT6765_Android_scatter.txt`, לבטל הכל חוץ מ-`boot_a`, להחליף את הקובץ ל-`boot_unlock_oneshot.img`, "Download Only". (או `fastboot flash boot_a <קובץ>` ממצב fastboot.) לא לצרוב super או userdata.
</div>
