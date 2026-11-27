<div dir="rtl">

# תיעוד מלא של הסשן (22.11.2026): מה נעשה, מצב נוכחי, מה נשאר

קרא קודם את `HANDOFF_M393.md` (כללי ברזל) ורק אחר כך את הקובץ הזה. הקובץ הזה מפרט את מה שנעשה בסשן האחרון, בסדר כרונולוגי, ואת מה שנשאר. כל מה שכתוב "נבדק" נבדק בפועל. כל מה שכתוב "לא נבדק" לא נבדק.

## 0. איך מתחילים
פתח שיחה חדשה בתיקיית `C:\פרוייקטים קלוד` וכתוב: **קרא את zamir\HANDOFF_M393.md ואז zamir\SESSION_2026-11-22_FULL.md והמשך מסעיף 6.** אל תבצע שום פעולה שמופיעה כאסורה ב-HANDOFF.

## 1. מצב המכשיר בסוף הסשן (נבדק בסוף)
- מחובר ב-adb (`0123456789ABCDEF`), עלה ופועל, 0 קריסות (`FATAL EXCEPTION`), אחסון `/data` מלא ב-68%.
- `orech_integrity.sh check`: ריק, קוד 0. **`noenforce` קיים** (מצב בדיקות: הבקרה לא מכבה). הבקרה נכנסת לתוקף רק כשאפליקציית המוכר מסיימת (או שמוחקים `noenforce` ידנית).
- שירות `orech_vguard`: **v8** (sha256 `890863c7...`), הדגל `/data/system/orech_allow_install` **לא קיים**, כלומר בדיקות ההתקנה במתקין ובמנהל החבילות פעילות.
- `ro.build.display.id` (המספר ב"מספר Build" בהגדרות): `כושר טק - כל הזכויות שמורות | kt3182319@gmail.com`.
- מספר מכשיר בדיקה `T123` (ב-`/mnt/vendor/nvdata/orech_id`, `persist.sys.orech_id`, `sales.csv`). **לשנות למספר אמיתי באפליקציית המוכר.** שעון המכשיר שגוי (2025-11-08), ולכן התאריכים ב-CSV וביומנים שגויים.
- חבילות מותקנות: `com.orech.seller` (אפליקציית המוכר, גרסת בדיקה), `com.orech.mockloc` (מיקום ב-USB), `com.orech.files` (סייר הקבצים המובנה, אפליקציית מערכת), `com.orech.overlay.*`, `de.pilablu.gpsconnector`.
- **VLC (`org.videolan.vlc`) ו-File Manager + (`com.alphainventor.filemanager`) הוסרו מהמכשיר** (`pm uninstall` החזיר `Success` לשניהם) והרשימה השחורה `blacklist.txt` ריקה. לא נשמר איסור קבוע: בהמשך אפשר להתקין חבילה כזו שוב אם היא מותרת (בדיקות ההתקנה במתקין עדיין חוסמות אפליקציות עם `video/` במניפסט, ולכן צריך לתת לחבילה היתר: להוסיף ל-`allowlist.txt` ולבצע את ההתקנה כשבדיקת ההתקנה מכובה בצורה מבוקרת).
- ההשבתה החוזרת כל כ-2 דקות ביומן נפסקה כי החבילות הוסרו. במדיניות העתידית (v10) חבילה שנחסמת מוסרת פעם אחת ולא נשארת ברשימה.

## 2. מה נעשה בסשן, לפי סדר
1. **בדיקות ל-`ZipUtil`** (JVM, JUnit 4.13.2): `C:\orech_build\app\src\test\java\com\orech\patcher\ZipUtilTest.kt`, 4 בדיקות. תוצאה: `tests="4" skipped="0" failures="0" errors="0"`. שיבוש יישור (4 ל-1) הכשיל `rewrite_alignsStoredEntriesTo4Bytes` (`4 tests completed, 1 failed`), ואחרי החזרה הכל עבר. ל-`PatchEngine` עדיין אין בדיקות (`replaceMethod` פרטית, `apply` תלויה ב-`Context` וב-`RootShell`).
2. **אפליקציית המוכר (`SellerActivity.kt`)**: נמצאו ותוקנו שני באגים. (א) `$id` חסר בפקודות כתיבת מספר המכשיר, כך שהמספר לא נכתב לעולם; שוחזר על המכשיר לפני התיקון ואומת אחריו (nvdata, property, `sales.csv`, מסך "מידע על הטלפון"). (ב) הבקרה הופעלה והאפליקציה הסירה את עצמה גם כש-`approve` נכשל; כעת רק אם `approve` הצליח ו-`check` החזיר ריק, אחרת `noenforce` נשאר. **תיקון (ב) לא נבדק על מכשיר** ושלב "החל" מעולם לא הורץ על המכשיר.
3. **רשימה שחורה (בדיקה סינתטית)**: קובץ דמה `libffmpeg_test.so` ב-`/data/data` של אפליקציית המוכר. הזיהוי עבד: `chmod 000`, `blacklist.txt`, יומן, השבתה. הזיהוי לקח כ-95 שניות ולא "דקה". לפני `pm enable` חובה להוציא מהרשימה, אחרת השירות משבית שוב. נוקה. לא נבדק עם אפליקציה אמיתית.
4. **מספר Build בהגדרות**: `ro.build.display.id` שונה ב-`/system/build.prop` ל-`כושר טק - כל הזכויות שמורות | kt3182319@gmail.com` (כתיבה עם `dd`+`truncate`, אימות sha256, `approve`, אתחול). הקובץ החדש 8040 בייט (המקורי 8103): הוסרו שורות הערה כדי לא לחרוג מהגודל. המקורי שמור ב-`zamir\build.prop.orig_before_branding` (sha256 `5ce02a1d...`). איות: "כושר טק" (המשתמש כתב פעם "קושר טק", כנראה שגיאת הקלדה).
5. **מסך חסימת המתקין**: נבדק על ידי המשתמש (פתח `blocktest.apk` מ-Files by Google וראה את נוסח החסימה שלנו); ביומן: `InstallStart` ואז `PackageInstallerActivity`, החבילה לא הותקנה. הנוסח הנוכחי: טוסט "האפליקציה נחסמה: היא כוללת תמיכה בוידיאו" ואחריו דיאלוג שגיאה של המתקין. **המשתמש דחה נוסח נפרד לחסימה בגלל יכולת התקנת אפליקציות** ("לא לסיבה של מתקין מובנה נוסח שונה"). ההודעה הכללית "האפליקציה לא הותקנה" יכולה להגיע גם מחוסר מקום (נצפה `Failed to allocate 49960882 because only 152986 allocatable`).
6. **ניסוי v9 (בוטל)**: `deploy\orech_vguard_v9.sh` (בדיקות התקנה כבויות בדגל, נעילת מפענחים בלי השבתה, השבתה רק כשמפענח חוזר). נפרס ונבדק בבדיקות סינתטיות (נעילה, `decoder came back` ב-~80 שניות, התקנת APK עם `video/*` הצליחה). **המשתמש החליט לבטל**: הוחזר v8 והדגל הוסר. נשאר רק לעיון.
7. **סייר קבצים מובנה `com.orech.files`**: פורק של המשתמש `github.com/shmuel-sinay/file-manager-x` (קוד פתוח ב-Java, ללא רישיון בקובץ). וריאנט lite (ללא וידיאו וללא `REQUEST_INSTALL_PACKAGES`), שם חבילה `com.orech.files`, שם "מנהל קבצים". עיצוב מסך הבית הוחלף לסגנון א (רשימה שטוחה, כותרת כהה, פס צבע בצד). נחתם במפתח קבוע. הותקן כאפליקציית מערכת ב-`/product/app/OrechFiles/OrechFiles.apk` (sha256 `2416a510...`, 190036 בייט, `flags=[ SYSTEM ...]`), נוסף לבקרת השלמות (`approved 17 files`), ניתנה הרשאת `MANAGE_EXTERNAL_STORAGE` ב-appops, אומת אחרי אתחול ובצילום מסך.
8. **מפתח חתימה**: `C:\orech_build\ext\keys\fmx_release.keystore` (RSA 4096, alias `fmx`, עד 2059) + `fmx.pass`. גיבוי: `Documents\fmx-keys-backup` ומאגר **פרטי** `github.com/shmuel-sinay/orech-keys`. **לא הועלה לפורק הציבורי** (החלטה שלי מנימוקי אבטחה; המשתמש ביקש להעלות לפורק).
9. **GitHub**: `gh` מחובר כ-`shmuel-sinay` (התחברות של המשתמש בדפדפן). נדחף `ebf62e2` ל-`main` בפורק (אומת ב-`git ls-remote`). **לא להשתמש בטוקנים של אחרים** (הוראת המשתמש).
10. **Commit ב-`C:\orech_build`**: `b2b91ed` בענף `feature/seller-version-picker` (SellerActivity, בדיקות ZipUtil, build.gradle.kts). לא נדחף. לא כלולים ב-commit: `mockloc/` ו-`deploy/` (עבודת סשן אחר), `ext/`, ותיקיות זמניות `t_*`.

## 3. החלטות והעדפות של המשתמש (לא לשכוח)
- חסימת ההתקנה צריכה להישאר ולהיות חזקה יותר, לא להקל; מוכן לוותר על File Manager +.
- רוצה סייר מובנה שרק הוא מותקן/זמין, ללא התקנות וללא וידיאו (נעשה).
- **לא רוצה סריקות רקע תכופות** (סוללה והאטה): שלילה חד-פעמית בהתקנה; אירועים במקום סקריפט שרץ כל 2 שניות.
- לשפת הממשק: עברית, RTL. בלי עיצוב "AI" (בלי כרטיסים עגולים זהים, בלי גרדיאנטים סגולים).
- הקשר מסחרי: מכשירים כשרים שהמשתמש מוכר ומאבטח, שירות צריבה מחדש של "כושר טק"; אבטחה = חסימה מרבית.

## 4. הסשנים האחרים
במהלך העבודה פעלו במקביל סשנים מקומיים נוספים על אותו מכשיר ("התאמת orech ל-M393 Pro", "התקנת אפליקציות בתפזורת" ועוד). אחד מהם אתחל את המכשיר באמצע בדיקה (`reboot,shell`) ופרס `vguard v8` ו-`OrechMockLoc` חדש (מיקום ב-USB). **לפני כל פעולה על המכשיר: `ListAgents`; אם סשן אחר עסוק, לא לפרוס.**

## 5. מלכודות חדשות (נוסף לכללי הברזל ב-HANDOFF)
- **Python עם `\` בנתיבים**: `\b` ו-`\t` הפכו לתווי בקרה בקבצי התיעוד. להשתמש ב-`/` בנתיבים ולבדוק אחרי כתיבה שאין תווי בקרה.
- **`adb.exe` ונתיבי Git Bash**: `adb push/pull` חייבים נתיבי Windows (`C:/...`); נתיבי מכשיר דורשים `MSYS_NO_PATHCONV=1`. כלי Windows (`aapt2.exe`, `apksigner.bat`) לא מבינים `/c/...`.
- **`su -c cat` מוסיף CRLF** (גם עם `exec-out`): לשלוף קבצים עם `cp` ל-`/data/local/tmp` ואז `adb pull`.
- **`chcon --reference` לא נתמך** במכשיר; להשתמש ב-`chcon u:object_r:system_file:s0`.
- **הפעלת המתקין מ-adb נדחית** (`uid 2000 needs to declare REQUEST_INSTALL_PACKAGES`); צריך להפעיל מאפליקציה (Files by Google). `file://` לא נפתר, צריך `content://media/external/file/<id>` (אחרי `MEDIA_SCANNER_SCAN_FILE`).
- **ניתוק adb אחרי אתחול** יכול להיות אישור USB שנעלם: הפעלה מחדש של "ניפוי USB" והאישור למחשב החזירו אותו.
- **מחיקה (`rm`) חסומה** בבדיקת בטיחות כשנתיב לא נפתר; לא לעקוף, לדלג ולדווח.
- **בדיקת הרשימה השחורה/סריקות**: סריקת `/data/data` רצה כל כ-60 עד 95 שניות; להמתין.
- **אחרי התקנת אפליקציה על המכשיר** (גם בדיקה): `appops` של `MANAGE_EXTERNAL_STORAGE` צריך `allow` ידני לאפליקציית קבצים.

## 6. מה נשאר, לפי סדר
1. **`vguard` v12 (סגור, סעיף 6.2 הושלם):** הסשן "המשך מסעיף 6" השלים את שתי המשימות שהיו פתוחות (הסרה במקום השבתה, וסריקות מבוססות אירועים). קובץ: `deploy/orech_vguard_v12.sh`, sha256 `020833de41a1...`, 5511 בייט (בנוי על v11). **נבדק ואומת על ידי** (קריאה בלבד, אחרי הדיווח): sha256 במכשיר תואם, `check` ריק, `noenforce` קיים, `blacklist.txt` ריק, אין דגל `orech_allow_install`, 0 קריסות, השירות `running`. בדיקות סינתטיות של הסשן השני (חבילות זמניות `com.zz.*`, נוקו): הסרה (`pm uninstall`, לא השבתה) תוך ~60 שניות לספרייה ב-`/data/data`; מסלול אירוע (`inotifyd`) על התקנה חדשה עם מפענח — נעילה והסרה תוך כ-3 שניות; מסלול טיימר על ספרייה שנוספה ל-`/data/data` אחרי ההתקנה — עד 5 דקות (טיימר 300 שניות, לעומת כ-60 שניות בגרסה הישנה; פשרה מודעת תמורת ביטול לולאת ה-2 שניות). המימוש: `inotifyd` על `/data/system` (שינויי חבילות) ועל תיקיית המצב, עם אירוע משולב `yn` (אירוע בודד `n` או `y` נדחה `Invalid argument` באינוטיפיי של המכשיר). `v10` (הביניים) לא נשמר בנפרד — התייתר.
2. **שאר המסכים של הסייר בעיצוב החדש**: תיקיות, הגדרות, חיפוש, תפריט צד, פעולות על קבצים. הצבעים משותפים אבל לא נצפו.
3. **WebView (8.2)**: לבדוק אם סרטון מתנגן בדפדפן מבוסס WebView; אם כן לחסום את ספריית הפענוח לפי שם, בלי להשבית את `com.google.android.webview`. גיבוי ובדיקת אתחול נקי לפני ואחרי.
4. **אימות אפליקציית המוכר מקצה לקצה**: שלב "החל" (כולל התנהגות התיקון: הבקרה מופעלת רק אחרי `approve` תקין) על מכשיר רזרבי בלבד. אסור לבדוק כיבוי אמיתי על המכשיר היחיד.
5. **הפעלת הבקרה בסוף התהליך** (`noenforce` נמחק על ידי אפליקציית המוכר). מכשיר שלא עבר דרכה אינו מוגן.
6. **הסרת root** בסוף: דורשת `boot.img` מקורי מתיקיית היצרן ("גירסת יצרן m393") **ורשות מפורשת לפני שנוגעים בתיקייה**. נעילת bootloader: רק על מכשיר רזרבי ורק עם "OEM unlocking" כבוי; בלי Secure Boot בחומרה אי אפשר למנוע צריבה מלאה במצב BROM.
7. **חנות פנימית**: ממתין להגדרה מהמשתמש. לא לנחש.
8. **בדיקות ל-`PatchEngine`**: להוציא `replaceMethod` ו-`checkExpectedHash` למחלקת עזר ללא תלות באנדרואיד ולבדוק ב-JVM.
9. **ניקוי**: כ-580MB של קבצי פריסה ישנים ב-`/data/local/tmp` (סקריפטים של סשנים אחרים מפנים אליהם, לא למחוק בלי תיאום); לשנות את `T123` למספר אמיתי; לתקן את שעון המכשיר; `ext\fmx_release.keystore` הישן (זמני, לא בשימוש) לא נמחק כי בדיקת בטיחות חסמה; commit ל-`mockloc/`/`deploy/` רק בתיאום עם הסשן שיצר אותם.
10. **רישיון הסייר**: במאגר המקורי `Xnet2/file-manager-x` אין קובץ רישיון; לברר מול הבעלים לפני הפצה.
11. **מאגר `orech_build`**: ענף `feature/seller-version-picker` לא נדחף ל-GitHub (לא הוחלט).

## 6א. אשף הגדרה חלופי (`com.google.android.setupwizard`) — חדש, נבדק
- **מה זה:** האשף המקורי של גוגל נתקע ב"רק שנייה" (ראו HANDOFF סעיף 9). במקומו נכתב אשף חלופי באותו שם חבילה ובאותו מיקום (`/system/system_ext/priv-app/SetupWizard/SetupWizard.apk`), חתום במפתח debug (test key, לא מפתח גוגל — אין התנגשות חתימות אחרי איפוס). קוד: `C:\orech_build\wizard` (מודול gradle חדש `:wizard`, נוסף ל-`settings.gradle.kts`).
- **מסלול:** 3 שלבים — תאריך/שעה (עברית, אזור זמן ישראל, בלי שאלה), מספר מכשיר (אפשר לדלג), בחירת גרסה (שמור/שמור עם חנות/פתוחה) עם אותו PIN וגיבוב כמו אפליקציית המוכר (`BuildConfig.PIN_SHA256`). חסימת ניסיונות אחרי 10 טעויות (`/data/misc/orech_pin_fail`).
- **אין root לאשף עצמו** (רץ כאפליקציית מערכת רגילה, לא seller app). הבקשה עוברת ב-system property `sys.orech.req` ("<פרופיל>:<PIN>:<מספר מכשיר>:<nonce>") לשירות root חדש: `/vendor/bin/orech_wizard.sh` + `/vendor/etc/init/orech_wizard.rc` (3 שירותי init: boot/pick/finish, מופעלים ע"י property triggers). תשובה חוזרת ב-`sys.orech.result` (`ok` או `fail:<סיבה>`), האשף סוקר כל 500ms עד 120 שניות.
- **נעילה קבועה (שורדת איפוס):** דגל `/mnt/vendor/protect_s/orech/version_chosen` נכתב בהצלחה, והשירות מפרסם אותו כ-`sys.orech.chosen` בעלייה (`post-fs-data`). אם הוא קיים, `onCreate` של האשף קורא ל-`complete()` מיד בלי להציג כלום — לקוח שמאפס לא יכול לבחור גרסה שנייה.
- **`finish()`:** אחרי הצלחה, האשף שולח `sys.orech.finish=1`. אם `/data/misc/orech_keep_root` **לא** קיים: מוחק את כל ה-policies/logs של Magisk, מכבה `adb_enabled` ו-`development_settings_enabled`, ומאתחל. דגל `/data/misc/orech_finished` מונע הרצה כפולה. **מצב בדיקה:** `orech_keep_root` מדלג על הנעילה והאתחול (רק רושם ביומן) — נמחק עכשיו, המכשיר חזר למצב רגיל.
- **נבדק בפועל ועבד (22.11.2026):**
  - שלושת השלבים דרך ה-UI האמיתי (לא shell): תאריך/שעה, דילוג על מספר מכשיר, בחירה, PIN שגוי → "קוד שגוי" באדום, אפשר לנסות שוב (`fail:pin` + `bad pin` ב-`wizard.log`).
  - PIN נכון עם **גיבוב בדיקה זמני** (לא הגיבוב האמיתי של המוכר, כדי לא לחשוף אותו): `approve` עבר, `check` ריק, המתקין כבר היה בגרסה הנעולה (hash זהה מראש) ולכן לא נכתב מחדש, `noenforce` נמחק (בקרה הופעלה **באמת**), `version_chosen=locked` נכתב, רשומה ב-`sales.csv`.
  - **אתחול עם בקרה פעילה באמת (לא noenforce) עלה נקי**: `sys.boot_completed=1`, `check` ריק, 0 `FATAL EXCEPTION`, `sys.orech.chosen` התפרסם נכון מהדגל ששרד את האתחול.
  - הרצה חוזרת של האשף אחרי שהבחירה כבר קיימת: מדלג מיד (`complete()`), משבית את עצמו (`enabled=0`), פותח מסך בית. `device_provisioned=1`, `user_setup_complete=1`, 0 קריסות.
  - `finish()` עם `orech_keep_root`: רשם ביומן "test mode, root and adb kept, no reboot", `adb_enabled` נשאר 1, המכשיר לא התאתחל, root לא הוסר.
  - **ניקוי אחרי הבדיקה (בוצע):** `noenforce` **נוצר מחדש** (המכשיר חזר למצב בדיקות), `version_chosen` ו-`sys.orech.chosen` נמחקו/אופסו (הבחירה "מבוטלת" — האשף יציג שוב את 3 השלבים בפעם הבאה), `sales.csv` נוקה מהרשומה המדומה, `/data/misc/orech_finished`/`orech_pin_fail`/`orech_keep_root` נמחקו, קובץ הבדיקה עם הגיבוב הזמני נמחק מהמכשיר. אתחול אחרון: נקי, `chosen` ריק, `check` ריק, `noenforce` קיים, Magisk עדיין מותקן עם root.
- **לא נבדק:** מסך "מספר מכשיר" עם ערך אמיתי (רק דילוג), בחירת "שמור עם חנות פנימית" ו"פתוחה" (רק "שמור" נבדק), `finish()` האמיתי (בלי `keep_root`) — כלומר מחיקת policies, כיבוי adb, ואתחול אמיתי בסוף, עם ה-PIN **האמיתי** של המוכר (לא נבדק כלל בסשן הזה, רק גיבוב בדיקה זמני).
- **קבצים חדשים במכשיר:** `/vendor/bin/orech_wizard.sh`, `/vendor/etc/init/orech_wizard.rc`, `/vendor/etc/orech/GooglePackageInstaller_locked.apk` (כולם ברשימת `paths.txt` של בקרת השלמות, `approve` בוצע). `build.prop`: `ro.product.locale=he-IL`, `persist.sys.timezone=Asia/Jerusalem` (היו `en-US`/`America/Los_Angeles`).
- **עדכון מאוחר יותר (אותו סשן):** לבקשת המשתמש, `complete()` עכשיו קובע גם `settings put global disable_window_blurs 1` (לצד `device_provisioned` ו-`user_setup_complete`, שלוש הפקודות שהיו נחוצות ידנית בעבר כדי שהמכשיר "יעבוד עד הסוף"). נבנה ונחתם מחדש: `SetupWizard.apk` sha256 `aec0cc712cb401b3de2e9c8f4dae838ef8b6e43b9f5053211ea71c294b4b215e`, 2,053,360 בייט. **נפרס ונבדק**: `approve`+`check` ריק, אתחול נקי (0 קריסות), ואז הרצה חוזרת עם PIN בדיקה זמני אישרה ששלושת ה-settings נקבעים יחד (`disable_window_blurs: null → 1`). **נוקה אחרי הבדיקה** בדיוק כמו בפעם הקודמת: `noenforce` הוחזר, `version_chosen`/`sys.orech.chosen` אופסו, שלושת ה-settings הוחזרו ל-0 (`device_provisioned`, `user_setup_complete`, `disable_window_blurs`), קבצי בדיקה הוסרו. המכשיר במצב "עוד לא הוגדר", מוכן לבחירה אמיתית.
- **גיבוי:** `SetupWizard.apk` המקורי של גוגל (8,969,577 בייט, sha256 `c5ba62825f8...`) שמור ב-`C:\orech_build\wizard\orig\SetupWizard.apk`.

## 7. איפה הכל
- תיעוד: `zamir\HANDOFF_M393.md` (כללי ברזל, סעיפים 1-10), `zamir\CHANGELOG_M393.md`, הקובץ הזה.
- קוד האפליקציות: `C:\orech_build` (app, seller, mockloc, tools, deploy). שימו לב: gradle רק מנתיב ASCII (`C:\orech_build`).
- סייר הקבצים: `C:\orech_build\ext\fmx` (פורק, `origin` = `shmuel-sinay/file-manager-x`, `upstream` = `Xnet2/file-manager-x`), בנייה: `python -I build_orech.py` עם `FMX_KEYSTORE` ו-`FMX_KEYPASS_FILE`; דגם העיצוב: `ext\fmx_mock\index.html`.
- מפתח: `ext\keys\` + גיבוי `Documents\fmx-keys-backup` + מאגר פרטי `shmuel-sinay/orech-keys`.
- קבצים בתיקייה `zamir`: `seller-debug.apk` (מעודכן), `FileManager-Lite_com.orech.files.apk`, `build.prop.orig_before_branding`, `OrechMockLoc.apk`.
- **תיעוד ב-GitHub:** מאגר ציבורי `shmuel-sinay/orech` קיבל `docs/m393_changes_and_editing.md` (מסונן, בלי מנגנוני הגנה; commit `fdca116` ב-`main`). התיעוד המלא (HANDOFF, CHANGELOG, וקובץ הסשן) נמצא במאגר **פרטי** `shmuel-sinay/orech-private-docs`. לא לפרסם אותו בציבורי: הוא כולל פרטי בקרת השלמות ודרכי עקיפה. אחרי שינוי בקבצים כאן יש לסנכרן ידנית למאגר הפרטי.
- ניתוח זמני (ניתן למחוק): `ext\fmx_out`, `t_bp`, `t_inst`, `t_fp` (עותק `פרימיום בעברית.apk` לניתוח סטטי בלבד, לא להתקין), `t_blocktest`, `t_v9`.

## 8. פקודות שימושיות
- adb: `C:\Users\user1\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- בקרה: `sh /mnt/vendor/protect_s/orech/orech_integrity.sh check` (ריק = תקין), `approve` לאישור מצב. לפני פריסה של קובץ ברשימה: `touch /mnt/vendor/protect_s/orech/noenforce` (קיים כרגע).
- שירות: `getprop init.svc.orech_vguard`, יומן `/data/system/orech_vguard/vguard.log`, רשימות `/data/system/orech_vguard/*.txt`.
- בדיקות JVM: מ-`C:\orech_build` עם `JAVA_HOME` של Android Studio ו-`ANDROID_HOME`: `gradle :app:testDebugUnitTest --console=plain`, ותוצאה ב-`app\build\test-results\testDebugUnitTest`.
- עלייה תקינה: כ-65 עד 75 שניות (`sys.boot_completed`). קריסות: `logcat -d | grep -c 'FATAL EXCEPTION'` (צריך 0).
- הוספת אפליקציה מובנית: קובץ ל-`/product/app/<שם>/`, 755/644, `chcon u:object_r:system_file:s0`, `rm -rf /data/system/package_cache`, הוספה ל-`paths.txt`, `approve`, אתחול.

</div>
