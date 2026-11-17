# הסרת שורת ה-WiFi מ-MtkSettings.apk — בפקודות, מהמחשב, שורד איפוס

> רץ על ה-Windows שלך (לא על הטלפון). צריך: Java מותקן (יש לך, כי adb/keytool
> כבר עבדו אצלך), ו-Python 3. קובץ `patch_wifi_rows.py` המצורף עושה את
> העריכה עצמה אוטומטית — לא עורכים XML ביד.

## 0. הורדת apktool (כלי פומבי, לא שלי)
מ-https://github.com/iBotPeaches/apktool/releases — קובץ `.jar` האחרון.
שמור אותו כ-`apktool.jar` באותה תיקייה שבה תעבוד.

## 1. שליפת הקובץ מהמכשיר
```
adb shell su -c "cp /system/system_ext/priv-app/MtkSettings/MtkSettings.apk /sdcard/"
adb pull /sdcard/MtkSettings.apk
```

## 2. פירוק
```
java -jar apktool.jar d -f -o settings_decoded MtkSettings.apk
```

## 3. עריכה אוטומטית (הקובץ שצירפתי)
```
python patch_wifi_rows.py settings_decoded
```
אמור להדפיס "הוסר 1 אלמנט" שלוש פעמים (אחת לכל קובץ).

## 4. בנייה מחדש
```
java -jar apktool.jar b settings_decoded -o MtkSettings_patched_unsigned.apk
```

## 5. בניית keystore מהמפתח האמיתי (platform.pk8 + platform.x509.pem ששלחתי)
צריך openssl (מגיע עם Git for Windows, או git-bash):
```
openssl pkcs8 -inform DER -nocrypt -in platform.pk8 -out platform_key.pem
openssl pkcs12 -export -in platform.x509.pem -inkey platform_key.pem -out platform.p12 -name platformkey -password pass:android
```

## 6. חתימה
```
jarsigner -keystore platform.p12 -storetype PKCS12 -storepass android -signedjar MtkSettings_patched.apk MtkSettings_patched_unsigned.apk platformkey
```

## 7. החזרה למכשיר — שורד איפוס, ואפשר לשאוב ולהעביר למכשירים נוספים
```
adb push MtkSettings_patched.apk /sdcard/MtkSettings_patched.apk
adb shell su -c "mount -o remount,rw /system"
adb shell su -c "cp /sdcard/MtkSettings_patched.apk /system/system_ext/priv-app/MtkSettings/MtkSettings.apk"
adb shell su -c "chmod 644 /system/system_ext/priv-app/MtkSettings/MtkSettings.apk"
adb reboot
```

למכשיר נוסף — רק שלב 7, עם אותו `MtkSettings_patched.apk` (לא צריך לחזור על 1–6).

## אם משהו משתבש
ב-apktool decode/build לפעמים יש אזהרות על משאבים לא מזוהים (`framework`) —
בד"כ לא קריטי לקובץ XML פשוט כמו זה. אם ה-build נכשל לגמרי, תעתיק את הודעת
השגיאה המלאה ותשלח לי — לא לנחש תיקון.

**גיבוי לפני שלב 7**: לשמור עותק של `MtkSettings.apk` המקורי (משלב 1) בצד.
