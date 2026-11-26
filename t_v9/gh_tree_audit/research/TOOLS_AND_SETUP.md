# TOOLS_AND_SETUP — כלים, סביבה, וכיצד להמשיך בשיחה נקייה

## 1. ריפואים
- **`shmuel-sinay/orech`** (זה): אפליקציית אנדרואיד (Kotlin) שמבצעת את כל התיקונים on-device עם root. CI ב-`.github/workflows/build.yml`: כל push ל-`main` → `gradle assembleDebug` → artifact + **Release קבוע** `build-<תאריך>-<sha>` עם `app-debug.apk`.
- **`shmuel-sinay/android`**: אטלס ROM (סקריפטי ניתוח ב-`tools/`, תוצאות `roms/<device>/`, אתר `docs/site`). `shmuel-sinay/-atar-`: האתר/חנות kosher-tek (לא נגענו).
- הורדת APK אחרון: `gh api repos/shmuel-sinay/orech/releases/latest` (לא `gh release` — GraphQL חסום ב-session).

## 2. מבנה orech
```
app/src/main/java/com/orech/patcher/
  MainActivity.kt   UI: רשימה, "בדיקה בלבד", "בצע", "החל בפועל", שלח לוג, שחזור מגיבוי
  Patch.kt          Patch/Edit/ResourceRemoval + parser JSON
  PatchEngine.kt    apply(patch, dryRun) / commit(patch) / restore(...) ; SIGN_EDITED_FILES=true
  RootShell.kt      su, pull (cat>file), push (remount+cp+chmod), deleteFile, putSetting
  Signer.kt         חתימה (apksig) + אימות (ApkVerifier ל-APK, JarFile ל-JAR)
  Logger.kt         לוג מצטבר לקובץ + share דרך FileProvider
app/src/main/assets/keys/   testkey, platform (.pk8/.x509.pem, פומביים)
app/src/main/assets/patches/patches.json   נוצר אוטומטית — לא לערוך ביד
tools/  build_patches_json.py, reference_smali/, extract_method.py, verify_smali.sh, patch_wifi_rows.py (PC חלופי)
docs/   מסמכי תהליך ישנים
research/  המסמכים האלה + scripts + keys + logs
```
Gradle: AGP 8.5.0, Kotlin 1.9.24, compileSdk/target 34, minSdk 26, JDK17. תלויות: smali/baksmali/dexlib2 3.0.3, apksig 8.5.0, ARSCLib 1.3.5, androidx. `android:largeHeap="true"`. **הכל מקומפל לתוך ה-APK — אפס רשת בזמן ריצה** (דרישה: המכשיר ללא רשת).

סוגי תיקון ב-`patches.json`: `edit` (dex `edits` ו/או `resourceRemovals` על אותו קובץ, פעם אחת: pull→גיבוי→SHA-256→dex→ARSCLib→חתימה→אימות→push), `delete` (גיבוי+מחיקה), `setting` (`settings put`), `info`. שדות: `targetFile`, `remount` (`/` או `/vendor`), `signingKey`, `expectedSha256`.
זרימת האפליקציה: dry-run מכין קובץ חתום ומחזיק `pending` בזיכרון; "החל בפועל" רק דוחף. pending נעלם בסגירת האפליקציה. לוג: `/storage/emulated/0/Android/data/com.orech.patcher/files/logs/`, גיבויים ב-`.../backups/`, עבודה ב-`.../patchwork/`.

## 3. כלים מקומיים בסנדבוקס (לא בריפו; להתקין מחדש)
| כלי | התקנה/מיקום |
|---|---|
| androguard 4.1.4 | `pip install androguard colorama` (חובה `colorama`); להשתיק לוגים: `logging.disable(logging.CRITICAL)`; **לא** `python -I` (חוסם חבילות site) כשמריצים androguard |
| smali/baksmali 2.5.2 (JesusFreke) | `https://bitbucket.org/JesusFreke/smali/downloads/{smali,baksmali}-2.5.2.jar` ; `java -jar smali.jar assemble DIR -o out.dex` ; `tools/verify_smali.sh` עושה round-trip |
| ARSCLib 1.3.5 jar | `https://repo1.maven.org/maven2/io/github/reandroid/ARSCLib/1.3.5/ARSCLib-1.3.5.jar` |
| מקורות API | `https://android.googlesource.com/platform/tools/apksig/+/refs/heads/main/src/main/java/...?format=TEXT` (base64) ; `raw.githubusercontent.com/google/smali/...` |
| סקריפטי ניתוח | `research/scripts/` (`callees.py`, `field_xref.py`, `method_xref.py`, `find_pms.py` — דורשים `android/tools/ag_compat.py`, עותק ב-`research/scripts/`); `tools/extract_method.py` — חילוץ מתודה מ-APK |
| ROM dump | `/home/user/roms/...` (אובד עם הסנדבוקס; לא בריפו) |
| gh | CLI מוגדר ל-proxy; `gh run list/view --log-failed`, `gh api` |

פלואו חילוץ מתודה בטוח: `python3 tools/extract_method.py APK 'Lcls;' method` → כתיבת smali ביד → `tools/verify_smali.sh` → הוספה ל-`tools/reference_smali/` + `build_patches_json.py` → push → CI → Release.

## 4. סביבת המשתמש
Windows PC + adb (USB), MT Manager (עורך DEX+/משאבים), BT-Bridge (מראה מסך; Accessibility הוענק בפקודת `settings put secure enabled_accessibility_services com.btbridge.app/com.btbridge.app.BridgeAccessibility`). המכשיר rooted (su עובד מהאפליקציה). צריבה: צריבת super מלאה היא ההתאוששות היחידה מ-bootloop.

## 5. בטיחות — כללים שנלמדו (לשמור בכל שיחה)
1. אסור למחוק `android.hardware.wifi.xml`; WiFi נכבה רק ע"י מחיקת `wpa_supplicant`.
2. APK מערכת: לחתום במפתח **המקורי** (השוואת fp) — MtkSystemUI/MtkSettings = platform; לא לנחש.
3. כל קובץ יעד נבדק ב-SHA-256 מול ה-stock לפני נגיעה; גיבוי אוטומטי לפני כל כתיבה; להתחיל תמיד ב-dry-run.
4. שינוי אחד קריטי בכל פעם (SystemUI/Settings/services.jar), reboot ובדיקה, ורק אז הבא. תיקונים על אותו קובץ — לאחד בתיקון אחד.
5. כל הודעות למשתמש בעברית; לא להמציא מראי מקום/נתיבים; להודות במה שלא נבדק.
6. הכללים הדתיים/סגנוניים בהעדפות המשתמש (שקט, לא להזכיר) חלים תמיד.

## 6. מה עוד קריטי לשמור/לדעת
- `research/keys/` — מפתחות AOSP פומביים בלבד (testkey/platform/AVB testkey). אין מפתחות פרטיים של היצרן; מפתח GooglePackageInstaller אינו ידוע.
- ה-ROM dump (9.9GB) לא בגיבוי. אם יאבד: צריך להעלות שוב `m36.bin`/super (קישורי הורדה אצל המשתמש).
- פתוחים: בדיקת mtksettings_fixes אחרי largeHeap; הפעלת hide_status_bar_icons; חיפוש "wifi" בהגדרות; Magisk 26 לא מותקן; איטיות המכשיר; M36 רגיל ו-M393; האתר kosher-tek.com "לא שמיש" (לא אובחן, חסר צילום).
