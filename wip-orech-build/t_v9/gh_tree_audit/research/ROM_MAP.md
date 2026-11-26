# ROM_MAP — מפת המכשיר/ה-ROM (M36 Plus) והסביבה

> מקור האמת להמשך עבודה בשיחה חדשה. נכון ל-2026-11-18. כל מה שמסומן **[מאומת]** נבדק ישירות על קובץ/מכשיר אמיתי; **[הנחה]** = לא נבדק.

## 1. מכשירים
| מכשיר | SoC | Android | הערות |
|---|---|---|---|
| **M36 Plus** (עיקרי, מכשיר פיזי של המשתמש, rooted) | MT6765 (scatter `MT6765_Android_scatter.txt`, fstab.mt6762/6765) | 13 | AVB פעיל ברום, אבל `ro.boot.verifiedbootstate=orange` במכשיר [מאומת] |
| M36 רגיל | — | — | לא התחלנו, חסרים קישורי firmware |
| M393 | — | — | פורום פעיל; רק services-3.jar קהילתי נבדק (באג adb). נתיב הקובץ במכשיר לא ידוע |
| Qin F21 Pro (כמה רומים), F22 Pro, Tiq M5 | MT6761/MT6768 | 11–13 | נסרקו בריפו `shmuel-sinay/android` (אטלס) |

המשתמש: מפעיל kosher-tek.com, כל התקשורת בעברית, עובד מ-Windows (adb + MT Manager + BT-Bridge). לעולם לא לסייע בהפיכת מכשיר לבלתי-ניתן-לשחזור.

## 2. היכן יושב כל דבר
### על המכשיר (נתיבים אמיתיים)
- המכשיר הוא **system-as-root**: מחיצת system מצוברת ב-`/` עצמו. **אין mountpoint `/system`** (`mount -o remount,rw /system` נכשל: `'/system' not in /proc/mounts`). לכתיבה: `mount -o remount,rw /`. [מאומת]
- `/vendor` הוא מחיצה נפרדת וה-remount שלה עובד: `mount -o remount,rw /vendor`. [מאומת]
- **אין "system/system" כפול** על המכשיר. הנתיב הוא `/system/<path>` (המשתמש אישר). בדאמפ המקומי של ה-ROM זה נראה `fs/system/system/<path>` כי תיקיית הדאמפ `fs/system` מכילה את שורש התמונה. כלל: מכשיר `/system/X` = דאמפ `fs/system/system/X`.
- כתיבה לתיקיית ה-cache **הפנימית** של האפליקציה דרך su נכשלת/יוצרת קובץ שבור; **תיקיית ה-external files** (`/storage/emulated/0/Android/data/com.orech.patcher/files/...`) עובדת. [מאומת]

### בדאמפ המקומי של ה-ROM (בסביבת הסנדבוקס, לא בריפו — 9.9GB)
- `/home/user/roms/m36plus/fs/{system,vendor,product,apex_extracted,...}`; תמונות גולמיות ב-`/home/user/roms/m36plus/R02_62_4_32_4.02inch_M417B_F_20250923/` (boot.img, lk.img, logo.bin, preloader, scp, ...), `m36.bin`, `super`.
- גם `/home/user/roms/{f21-gtr,f21-restore,f22pro}`.
- הדאמפ לא נשמר בריפו (מדיניות: אין קבצי מערכת/מזהים). **להוריד מחדש/להעלות מחדש בשיחה חדשה אם צריך** — הדאמפ אובד עם הסנדבוקס.

### קבצי היעד המרכזיים (נתיבי מכשיר)
| קובץ | נתיב במכשיר | SHA-256 המקור (stock) | חתימה אמיתית | הערות |
|---|---|---|---|---|
| services.jar | `/system/framework/services.jar` | `53bf5ee3fd1aa0cb00ff108df63d3d71259b1dc1ee58ac3b442305ec4ec572d6` | אין META-INF/CERT (לא נבדק ע"י PackageManager) | multi-dex: classes.dex+classes2.dex; `RecentTasks` ב-classes2 |
| GooglePackageInstaller.apk | `/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk` | `10548d8bcfb83b45840d725d5e82d5135b7e4ba968f3e05b57560dda53d069e5` | **מפתח OEM לא-AOSP** (fp `19:7F:66:7A:E1:26:AF:53:...:8D:9D`) — לא testkey/platform/shared/media | package `com.google.android.packageinstaller`, **ללא sharedUserId**; dex יחיד classes.dex |
| MtkSystemUI.apk | `/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk` | `394d23cfcfdbac31de1aeca9d02d276928b54f8106e93fc6b3d4909067ca9ba4` | **platform** | `sharedUserId=android.uid.systemui`; classes.dex |
| MtkSettings.apk | `/system/system_ext/priv-app/MtkSettings/MtkSettings.apk` | `16f19026c15e34aabe98f69e2106cc193c2ffb8fc2260c8f5f72efcbaa7e7b9b` | **platform** | `sharedUserId=android.uid.system`; ~42MB, resources.arsc ענק; classes.dex |
| whatsapp.apk | `/system/preinstall/whatsapp.apk` | `ae2370a1f073d72a907c49c70d6d1f946d898daa93555087fcc5248a7b7a20a3` | — | נמחק בהצלחה |
| wpa_supplicant | `/vendor/bin/hw/wpa_supplicant` | `09de00181e7877ec2fce5dd2429ada14bae9c3f40eef776ab839025417eea7f1` | — | נמחק בהצלחה |
| MtkLauncher3QuickStep.apk | (system_ext priv-app) | — | testkey | מהסריקה הקודמת |
| framework-res.apk | — | — | platform | |

### טביעות אצבע של מפתחות AOSP (SHA-256 של התעודה)
- **platform** `C8:A2:E9:BC:CF:59:7C:2F:B6:DC:66:BE:E2:93:FC:13:F2:FC:47:EC:77:BC:6B:2B:0D:52:C1:1F:51:19:2A:B8`
- **testkey** `A4:0D:A8:0A:59:D1:70:CA:A9:50:CF:15:C1:8C:45:4D:47:A3:9B:26:98:9D:8B:64:0E:CD:74:5B:A7:1B:F5:DC`
- shared `28:BB:FE:4A:...:FD`, media `46:59:83:F7:...:1E`
- הקבצים (.pk8/.x509.pem, פומביים של AOSP) ב-`research/keys/` וב-`app/src/main/assets/keys/`.
- **AVB**: שורש האמון של `vbmeta.img` = `testkey_rsa2048` הפומבי של AOSP (SHA1 `cdbb77177f731920bbe0a0f94f84d9038ae0617d`), והמודולוס נמצא גם בתוך `lk.img` ב-offset 754056 → אפשר תיאורטית לחתום vbmeta מחדש (לא נבדק בצריבה). קובץ: `research/keys/avb_testkey_rsa2048.pem`.

## 3. ממצאי הסריקה (מהאטלס, ריפו `android`: `roms/m36-plus/`)
- 6,615 קבצים (4.31GB כולל APEX), 202 אפליקציות, 30 חבילות APEX (1,719 קבצים; ב-A13 רכיבי מפתח בתוך APEX), 11,808 מתגי תצורה (config_*/bool/integer), SystemUI: 109 מתגי status bar, Settings: 734.
- removal_recipes: 180 חבילות, 8 גורמות bootloop. VINTF: 0 HAL חובה חסרים. native: 3,000+ ELF, 0 תלויות חסרות.
- מסמכי עומק: `android/roms/m36-plus/deps/*.md` (avb_testkey_finding, block_video_system_level, installer, kosher_hardening, overlays, removal_closure, replace_launcher, secure_boot_oem, ui_removal, vendor_edit_lesson, video_gatekeeper_design).
- ה-JSONs: apks, avb, boot_guards, boot_roles, bundled_decoders, coverage, init_graph, lp_metadata, misc_files, native_deps, overlays, removal_recipes, resources, selinux, statusbar_slots, ui_deep, ui_knobs, xml_config.

## 4. Status bar
- slugs תקפים ל-`icon_blacklist` (מחרוזות ב-MtkSystemUI): `airplane, alarm_clock, bluetooth, cast, data_saver, hotspot, location, managed_profile, mobile, rotate, volume, wifi, zen`. אין slug ל-ethernet/vpn/"no internet".
- הערך המוגדר: `settings put secure icon_blacklist wifi,mobile,cast` — דורש reboot מלא; **נמחק בצריבת super חדשה** (צריך להריץ שוב).
- גיבוי קבוע בתוך ה-APK: מערך `config_statusBarIconsToExclude` ב-MtkSystemUI (קיים `rotate`; להוסיף wifi/mobile/cast) — לא בוצע.

## 5. כל כניסות ה-WiFi ב-MtkSettings.apk (סריקה מלאה של 308 קבצי `res/xml`)
26 קבצים מזכירים wifi. כניסות (מוסרות ב-`mtksettings_fixes`, 17 הסרות לפי `android:key`):
`top_level_settings`(main_toggle_wifi) · `network_and_internet`(main_toggle_wifi) · `network_provider_settings`(main_toggle_wifi, wifi_status_message_footer) · `connected_devices_advanced`(wifi_display_settings) · `tether_prefs`(wifi_tether) · `mobile_network_settings`(carrier_wifi_toggle, carrier_wifi_network, wifi_calling) · `advanced_calling_settings`(wifi_calling_key) · `location_services`(location_services_wifi_scanning) · `special_access`(change_wifi_state) · `my_device_info`(wifi_ip_address, saved_accesspoints_wifi_mac_address, wifi_mac_address) · `device_info_status`(wifi_ip_address, wifi_mac_address).
**בכוונה לא נוגעים**: `development_settings.xml` (אפשרויות מפתחים), ומסכי עלה (`wifi_settings`, `wifi_tether_settings`, `wifi_calling_settings`, `wifi_configure_settings`, `all_tether_prefs`, `location_services_wifi_scanning`, `change_wifi_state_details`, `data_usage_wifi`, `data_usage_metered_prefs`, `wifi_*` אחרים) — בלתי נגישים אחרי הסרת הכניסה. **לא נבדק**: חיפוש "wifi" בתוך חיפוש ההגדרות (search index) — עשוי להמשיך להציג תוצאות; דורש בדיקה נפרדת (SearchIndexProvider).

## 6. מצב נוכחי של המכשיר (לפי הלוגים האחרונים)
| תיקון | מצב |
|---|---|
| wifi_disable | הופעל, WiFi כבוי [מאומת] |
| keyguard_backdoor (Z הפוך) | הופעל עם platform, עובד [מאומת ע"י המשתמש] |
| recent_apps_limit | הופעל ("הושלם בהצלחה") — לא אומת התנהגותית |
| whatsapp_removal | הופעל — לא אומת ויזואלית |
| installer_password_lock | הופעל — לא נבדקה ההתנהגות (דיאלוג 7 הקשות + סיסמה) |
| hide_status_bar_icons | **לא הופעל בפועל** אחרי הצריבה (רק dry-run) → סמל רשת עדיין מוצג |
| mtksettings_fixes | dry-run קרס ב-OutOfMemory (ARSCLib טעינת resources.arsc); נדחף תיקון largeHeap (build 831ab0a) — **טרם נבדק** |
| m393_adb_always_on_fix | לא רלוונטי ל-M36; נתיב placeholder |
| Magisk | המשתמש מנסה להתקין Magisk 26 ולא מצליח — **לא נחקר** (חסר פירוט שגיאה) |
| המכשיר איטי מאוד | הוזכר, הושעה — לא נחקר |
