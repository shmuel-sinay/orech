# עריכת XML בינארי ב-APK (ARSCLib), נבדק על M393

שימוש: `java -Xmx3g -cp ARSCLib-1.3.5.jar XmlKeys.java patch in.apk out.apk keys.txt` (או `scan in.apk "regex"`).
שורה בקובץ: `res/xml/x.xml|key` להסרת אלמנט, או `res/xml/x.xml|*` לריקון הקובץ.

חובה, אחרת הקובץ נשבר: מסמך אחד לכל קובץ, `refresh()` לפני `getBytes()`, החלפה עם `ByteInputSource`,
ואז `zipalign -f -p 4` ורק אחר כך `apksigner` עם מפתח platform. לאמת עם `aapt2 dump xmltree` (קוד יציאה 0).
ב-M393 לכתוב למכשיר רק עם `dd conv=notrunc` ואחריו `truncate`, אחרי `mount -o remount,rw /`.
