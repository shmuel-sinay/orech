# עריכת dex ל-M393 (PC)

`DexEdit.java` מחליף מתודה/מחלקה בתוך classes*.dex של jar/apk, בלי לגעת בשאר המחלקות.
הרצה: `java -Xmx4g -cp <classpath.txt> DexEdit.java edit in out <class> <method|subst:method|-> <file> ...`
- `method` : החלפת המתודה כולה מקובץ smali.
- `subst:method`: החלפת קטע (שורות) בתוך המתודה; הקובץ הוא `דפוס\n====\nהחלפה`.
- `-` : קובץ smali של מחלקה שלמה (חדשה או מחליפה).
אחרי עריכת APK: `zipalign -f -p 4`, ואז `apksigner` עם מפתח platform.

התיקונים של M393 ב-`patches_m393/`: מגבלת אפליקציות אחרונות, באג adb (הסרת setAdbEnabled(true) קבוע),
דלת אחורית בנעילת המסך (9875321), סיסמת אפשרויות מפתחים, ובדיקת וידיאו בהתקנה (`installer_guard`).
כתיבה למכשיר: `dd conv=notrunc` + `truncate`, אחרי `mount -o remount,rw`.
