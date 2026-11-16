# אורך — מודול Magisk לכל תיקוני M36 Plus

## איך זה עובד
1. דוחף קבצים (מה-`system/` בתוך המודול) ל-`/system` באופן "systemless" (overlay של Magisk,
   לא עריכה ישירה של המחיצה).
2. מוחק קבצים שצריכים להיעלם (`post-fs-data.sh`) בכל עלייה.

## מה **עדיין חסר** — זה לא "בונה בעצמו"
המודול הזה הוא רק **מנגנון ההפצה**. הוא **לא** מפרק/עורך/חותם שום דבר בעצמו.
את הקבצים המתוקנים עצמם (אלה שכבר ערכת ב-MT Manager) צריך **אתה** לשים בתיקיות
הנכונות, לפני שאורזים את המודול לזיפ ומתקינים.

## איפה לשים כל קובץ מתוקן

| קובץ מתוקן | איפה לשים בתוך המודול |
|---|---|
| `MtkSystemUI.apk` (Z הפוך + icon_blacklist + QS tiles) | `system/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk` |
| `MtkSettings.apk` (שורות WiFi) | `system/system/system_ext/priv-app/MtkSettings/MtkSettings.apk` |
| `GooglePackageInstaller.apk` (נעילת סיסמה) | `system/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk` |
| `services.jar` (מגבלת 3 אפליקציות — **רק M36 Plus**, לא M393) | `system/system/framework/services.jar` |

(התיקיות כבר קיימות בריפו, ריקות — פשוט להעתיק/לדחוף אליהן את הקובץ המתאים.)

## מה נמחק אוטומטית בכל עלייה (`post-fs-data.sh`)
- `wpa_supplicant` מ-`/vendor/bin/hw/` — מנטרל WiFi לגמרי (לא רק מסתיר).
- `whatsapp.apk` מ-`/system/system/preinstall/` — כדי שלא יחזור אחרי איפוס.

## סיכום כל תיקון, עם המקור בשיחה
| תיקון | קובץ | מתודה/מיקום | סטטוס |
|---|---|---|---|
| Z הפוך (דלת אחורית של היצרן) | MtkSystemUI.apk | `KeyguardPatternViewController$UnlockPatternListener.onPatternDetected` | מתועד, לא בוצע עדיין |
| הסתרת/ניטרול WiFi | MtkSystemUI.apk + מחיקת wpa_supplicant | `config_statusBarIconsToExclude`, `quick_settings_tiles_default` | בוצע |
| הסרת שורות WiFi מהגדרות | MtkSettings.apk | `top_level_settings.xml`, `network_and_internet.xml`, `network_provider_settings.xml` | בוצע |
| נעילת סיסמה להתקנות | GooglePackageInstaller.apk | `InstallStart` + 2 מחלקות חדשות | מתועד, מוכן |
| מגבלת 3 אפליקציות אחרונות | services.jar | `RecentTasks.checkRecentTask` | בוצע ומאומת |
| מחיקת WhatsApp קבועה | preinstall/whatsapp.apk | — | בסקריפט המודול |

## התקנה (במכשיר שכבר יש לו את כל הקבצים המתוקנים בפנים)
1. לארוז את כל תוכן הריפו לקובץ zip (לא את תיקיית `.git`).
2. Magisk → מודולים → התקן מ-Zip → לבחור את הקובץ.
3. הפעלה מחדש.
