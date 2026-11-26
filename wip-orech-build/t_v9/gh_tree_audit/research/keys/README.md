# מפתחות החתימה הפומביים של AOSP — testkey ו-platform

אומת ישירות מול M36 Plus (SHA256 בפועל תואם 1:1):
- `framework-res.apk` (="android") חתום עם `platform.x509.pem`
- `MtkLauncher3QuickStep.apk` (הלאונצ'ר הנוכחי) חתום עם `testkey.x509.pem`

גם אומת: `.pk8` תואם ל-`.x509.pem` (modulus זהה) — זוג מפתחות שלם ותקין לכל אחד.

## שתי אפשרויות לחתימת לאונצ'ר חדש

**א. חתימה עם `testkey`** → זהה בביטים לחתימה של הלאונצ'ר המקורי. בטוח ביותר.

**ב. חתימה עם `platform`** → מקבל גם 4 הרשאות signature נוספות (widgets,
status bar, app-switches, slippery-touches) שהלאונצ'ר המקורי לא קיבל בכלל.

## חתימה בפועל (apksigner, אם יש גישה למחשב/טרמינל, לא רק MT Manager)
```
apksigner sign --key testkey.pk8 --cert testkey.x509.pem --out signed.apk unsigned.apk
```
(להחליף testkey ב-platform לאפשרות ב'.)

## הערה
אלה מפתחות **פומביים** של AOSP (לא סודיים, לא ייחודיים לך) — תקינים לחתימה
מקומית על מכשיר שבשליטתך, לא מעניקים "אבטחה" מול גורם שלישי.
