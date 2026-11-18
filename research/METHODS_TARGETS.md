# METHODS_TARGETS — מחלקות, מתודות וחתימות שזוהו

smali ב-`tools/reference_smali/` (מקור האמת; `patches.json` נוצר מהם ב-`tools/build_patches_json.py`).

## 1. מטרות שבוצעו (קוד ב-repo)
| תיקון | קובץ / dex | מחלקה | מתודה / סוג | smali |
|---|---|---|---|---|
| recent_apps_limit | services.jar / **classes2.dex** | `Lcom/android/server/wm/RecentTasks;` | `private checkRecentTask()V` → `.registers 1; return-void` | `recent_apps_limit_checkRecentTask.smali` |
| keyguard_backdoor | MtkSystemUI / classes.dex | `Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;` | `public onPatternDetected(Ljava/util/List;)V` (`.registers 8`, p0=v6, pattern=v7) | `keyguard_onPatternDetected.smali` |
| installer_password_lock | GooglePackageInstaller / classes.dex | `Lcom/android/packageinstaller/InstallStart;` (החלפת קובץ שלם, 741 שורות) + 3 מחלקות חדשות `InstallStart$CancelClickListener`, `$PasswordClickListener`, `$BlockedClickListener` | class-kind | `installstart*.smali` |
| dev options lock | MtkSettings / classes.dex | `Lcom/android/settings/deviceinfo/BuildNumberPreferenceController;` | `public enableDevelopmentSettings()V` (`.registers 7`, p0=this) + מחלקה חדשה `Lcom/orech/patcher/DevOptionsUnlockListener;` | `devoptions_enableDevelopmentSettings.smali`, `DevOptionsUnlockListener.smali` |
| m393 adb | services-3.jar (M393 בלבד) / classes2.dex | `Lcom/android/server/usb/UsbDeviceManager$UsbHandler;` | `private setAdbEnabled(Z)V` (`.registers 5`; הבאג: `const/4 v4, 0` שהוזרק לפני `if-eqz v4`) | `m393_adb_setAdbEnabled.smali` |
| מחיקה | `/vendor/bin/hw/wpa_supplicant`, `/system/preinstall/whatsapp.apk` | — | delete | — |

## 2. פרטי הדלת האחורית (Z הפוך)
בתוך `onPatternDetected`: אחרי לוג `Log.d("hqb","LockPatternUtils.patternToString(pattern)="+...)` יש
`if (LockPatternUtils.patternToString(p).equals("9875321")) { p = LockPatternUtils.stringToPattern(SystemProperties.get("persist.sys.secret","")); Log.d(...)}` ואז `LockPatternChecker.checkCredential(..., LockscreenCredential.createPattern(p), ...)`.
"9875321" = ציור Z הפוך (מימין-למטה → שמאל-למטה → מרכז → ימין-למעלה → שמאל-למעלה). הוסר הבלוק כולו (offsets 0x118–0x16e של הדקס); 0x174 נשאר הנחיתה.
**חישוב קפיצות**: בהוראות `if-*`/`goto` ה-offset הוא ב-code units (×2 לבייטים) יחסית לכתובת ההוראה. אומת: if-eqz@0x12c +0x24 → 0x174.
לוג דמפ גולמי: `research/reference_outputs/keyguard_onPatternDetected_raw_androguard_dump.txt`.

## 3. התקנות — מבנה InstallStart (סיסמה "תודה להשם")
- שדות: `mPasswordVerified:Z`, `mPasswordEditText:Landroid/widget/EditText;`; מתודות חדשות `showPasswordDialog()V`, `showBlockedDialog()`; `BlockedClickListener` סופר 7 לחיצות (`mCount`, `if-lt v0, 7`) ואז `showPasswordDialog`; `PasswordClickListener` משווה ל-`"תודה להשם"` וקורא `doInstall()`, אחרת Toast "סיסמא שגויה" + `finish()`.
- מקור: פאץ' מהפורום (קרדיט: בסדבסד), הורחב עם מסך חסימה ו-7 הקשות.

## 4. מטרות שזוהו וטרם בוצעו
- **סמל "אינטרנט" בסטטוס בר** — להפעיל `hide_status_bar_icons` בפועל; אם ממשיך — לברר איזה slot (יש רק הרשימה ב-ROM_MAP §4); אופציה: `config_statusBarIconsToExclude` (arrays, `com.android.systemui`).
- **חיפוש "wifi" בהגדרות** מציג הרבה תוצאות — אינדקס החיפוש (`SearchIndexProvider` של כל Fragment, ו-`XmlResourceInflater`) לא טופל; להסיר את התוצאות דורש להסתיר/לנטרל את ה-fragments (`WifiSettings`, `WifiTetherSettings`, `ConfigureWifiSettings`, `SavedAccessPointsWifiSettings2`, `WifiScanningFragment`, `WifiDisplaySettings`, `WifiP2pSettings`) מהאינדקס — לא נחקר.
- **Quick Settings tiles** (`quick_settings_tiles_default` — wifi/cast) — היה בסשן ידני ישן, אין תיעוד מדויק; לאמת מחדש.
- **settings_cache.java / pms_required.java** (`research/reference_outputs/`) — פלטי דקומפילציה קריאה-בלבד: `Settings$NameValueCache.getStringForUser` ו-`PackageManagerService.getRequiredInstallerLPr` (דורש בדיוק installer אחד ל-`INSTALL_PACKAGE`!) — לכן **אסור** להוסיף installer שני, וצריך להחליף את `InstallStart` (כמו שעשינו) ולא להתקין חדש.
- ביטול רשימת whitelist של 9 אפליקציות מוזיקה בתוך `checkRecentTask` (Spotify וכו') — לא רלוונטי כי הפונקציה הוחלפה ב-no-op.
- M36 רגיל / M393 — דורש firmware.

## 5. הערות API שנלמדו
- smali/baksmali (google fork, Maven `com.android.tools.smali:*:3.0.3`): `Baksmali.disassembleDexFile(dex, outDir, jobs, BaksmaliOptions())`, `Smali.assemble(SmaliOptions{outputDexFile=...}, listOf(dir))`.
- apksig `com.android.tools.build:apksig:8.5.0`: `ApkSigner.Builder(configs).setInputApk/OutputApk/MinSdkVersion/V1/V2.build().sign()`; ApkVerifier לא מתאים ל-JAR ללא manifest.
- ARSCLib `io.github.reandroid:ARSCLib:1.3.5` (בגרסה זו API שונה מ-master ב-GitHub!): `ApkModule.loadApkFile(File)`, `module.getResXmlDocument(path)`, `doc.recursiveElements()` (ללא predicate), `element.searchAttributeByName("key")?.valueString`, `element.removeSelf()`, `module.writeApk(File)`. נבדק לוקלית מול MtkSettings.apk האמיתי (הסרת main_toggle_wifi → zip תקין).
- baksmali מציג מחרוזות עבריות כ-`\uXXXX` — זה תקין (round-trip).
