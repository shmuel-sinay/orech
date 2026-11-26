# M36 Plus — הסתרת סמלי wifi / mobile / cast משורת הסטטוס

## 1. פקודות ל-adb (מהמחשב, עם המכשיר מחובר)

בדיקה שהערך נשמר נכון:
```
adb shell settings get secure icon_blacklist
```
צריך להחזיר בדיוק:
```
wifi,mobile,cast
```

אם לא קיים או שונה, קובעים אותו:
```
adb shell settings put secure icon_blacklist wifi,mobile,cast
```

**חובה אחרי קביעת הערך:** reboot מלא (לא `am crash`) כדי שה-SystemUI יקרא את הערך
מחדש בעלייה:
```
adb reboot
```

## 2. עריכה קבועה בתוך קובץ המערכת (שורדת איפוס, אפשר להעביר לכל מכשיר)

קובץ לעריכה (ב-MT Manager, לחיצה ארוכה → "עריכת משאבים"):
```
/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk
```

מערך לעריכה (סוג `array`, בחבילה `com.android.systemui`):
```
config_statusBarIconsToExclude
```

הערך הקיים כרגע במערך הזה ב-M36 Plus (לא למחוק!):
```
rotate
```

להוסיף לצידו (לא למחוק את rotate):
```
wifi
mobile
cast
```

לאחר השמירה: MT Manager יציע לחתום — לחתום (מפתח מובנה). להחליף/לשמור את הקובץ
בנתיב המקורי בדיוק, לגבות עותק של המקור קודם אם עדיין לא נשמר בצד.

הערה: קביעה זו היא *ברירת מחדל* — נכנסת לתוקף רק אם אין ערך פעיל ב-Settings
(כלומר: רלוונטית בעיקר למכשיר אחרי איפוס, או למכשיר נוסף שעדיין לא הורץ עליו
ה-adb command). כדי לבדוק אותה בנפרד מהערך שכבר הוגדר ב-adb:
```
adb shell settings delete secure icon_blacklist
adb reboot
```

## 3. סטטוס נוכחי (לפי השיחה)
- הערך ב-Settings כבר אושר כנכון (`wifi,mobile,cast`).
- `am crash com.android.systemui` לא הספיק כדי שהסמלים יעלמו בפועל.
- **עדיין לא בוצע reboot מלא ואומת** — זה הצעד הבא שממנו תלויה ההמשך האבחנה.
- הקוד של שלושת הסמלים (wifi/mobile/cast) נבדק ישירות ב-decompile של M36 Plus
  ואומת כתקין (ראו `deps/ui_removal.md` בריפו android). אין OEM override פעיל.
