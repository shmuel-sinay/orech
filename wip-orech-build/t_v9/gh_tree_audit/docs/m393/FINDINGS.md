# הזמיר (Kosher Tech) - ממצאי חתימות והשוואה ל-M36 Plus

תאריך: 2026-11-19. מקור: adb במכשיר 0123456789ABCDEF, root=magisk.

## מכשיר
- ro.product.model=Kosher Tech, device=KosherTech, android 13 (sdk 33), board k62v1_64_bsp, platform mt6765
- ro.build.date=Wed Nov 5 2025, display.id=<email hidden>
- fingerprint system: NODROPOUT/T25/T25:8.1.0/O11019/1539845665 (לא תואם לאנדרואיד 13 - כנראה מחרוזת של היצרן)

## חתימות (SHA-256 של התעודה)
| קובץ | חתימה | פירוש |
|---|---|---|
| GooglePackageInstaller.apk | 197f667a...8e9d | מפתח OEM, לא AOSP. זהה לחתימה שהריפו מתעד ב-M36 Plus |
| MtkSettings.apk | c8a2e9bc...2ab8 | מפתח platform הפומבי של AOSP |
| MtkSystemUI.apk | c8a2e9bc...2ab8 | מפתח platform הפומבי של AOSP |
| framework-res.apk | c8a2e9bc...2ab8 | מפתח platform הפומבי של AOSP |
| services.jar / framework.jar | אין חתימה | כמו ב-M36 |

MtkSettings ו-MtkSystemUI: חתימת v3 לא מתאימה לתוכן (CHUNKED_SHA256 mismatch), כלומר הקבצים שונו אחרי החתימה.

## השוואת hash מול ROM_MAP של M36 Plus
- GooglePackageInstaller.apk: 10548d8b...69e5 = זהה ל-stock של M36 Plus. התיקון של המתקין חל ישירות.
- MtkSystemUI.apk: ebeaf4d1... (ב-M36: 394d23cf...) - שונה
- MtkSettings.apk: 81fa00c8... (ב-M36: 16f19026...) - שונה
- services.jar: 216747ee... (ב-M36: 53bf5ee3...) - שונה

## קבצים
כל ה-APK וה-JAR בתיקייה הזו. system_packages.txt = 163 חבילות מערכת.
