# חסימת PackageInstaller — הוראות ללינוקס (לשרת/ל-AI שמריץ שם)

מטרה: `com.android.packageinstaller.InstallStart` (נקודת הכניסה היחידה לכל התקנה
במערכת) תמיד תציג הודעת חסימה ותסגור את עצמה, בלי להתקין כלום. לא נוגעים ב-services.jar.

## 0. דרישות (לינוקס רגיל, כל חלוקה)
```
sudo apt-get install -y default-jre   # או כל JRE/JDK אחר, גרסה 11+
```

## 1. משיכת ה-APK מהמכשיר
אם ה-adb מחובר ישירות לשרת:
```
adb pull /system/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk
```
אם לא — להעביר את הקובץ מהמחשב של המשתמש לשרת בכל דרך (scp/USB/כונן משותף).

## 2. הורדת apktool (פעם אחת)
```
wget https://github.com/iBotPeaches/apktool/releases/latest/download/apktool_2.9.3.jar -O apktool.jar
```

## 3. פירוק
```
java -jar apktool.jar d GooglePackageInstaller.apk -o installer_decoded
```

## 4. העריכה — להחליף קובץ שלם
לפתוח:
```
installer_decoded/smali/com/android/packageinstaller/InstallStart.smali
```
לחפש את המתודה `.method protected onCreate(Landroid/os/Bundle;)V` (עד ה-`.end method`
התואם), **למחוק את כל הגוף שלה** (כולל ה-`.method`/`.end method`), ולהדביק במקומה
בדיוק את זה:

```smali
.method protected onCreate(Landroid/os/Bundle;)V
    .locals 2

    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    const-string v0, "ההתקנה חסומה במכשיר זה"

    const/4 v1, 0x1

    invoke-static {p0, v0, v1}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v0

    invoke-virtual {v0}, Landroid/widget/Toast;->show()V

    invoke-virtual {p0}, Lcom/android/packageinstaller/InstallStart;->finish()V

    return-void
.end method
```

(ה-`const-string` המקודד הוא "ההתקנה חסומה במכשיר זה" ב-unicode — אפשר להשאיר כך,
זה עובד בלי קשר לקידוד הטרמינל.)

**לא לגעת בשום מתודה אחרת בקובץ** — רק `onCreate` הזו.

## 5. בנייה מחדש
```
java -jar apktool.jar b installer_decoded -o GooglePackageInstaller_patched_unsigned.apk
```

## 6. חתימה
צריך את מפתח ה-`platform` (אותו אחד שכבר שלחתי לך קודם ל-launcher/Settings — זה
אמור להיות המפתח הנכון גם כאן, כי זה גם priv-app עם sharedUserId דומה):
```
keytool -genkeypair ... # לא צריך — יש לך כבר platform.pk8/platform.x509.pem
openssl pkcs8 -inform DER -nocrypt -in platform.pk8 -out platform_key.pem
openssl pkcs12 -export -in platform.x509.pem -inkey platform_key.pem \
  -out platform.p12 -name platformkey -password pass:android
jarsigner -keystore platform.p12 -storetype PKCS12 -storepass android \
  -signedjar GooglePackageInstaller_patched.apk \
  GooglePackageInstaller_patched_unsigned.apk platformkey
```

## 7. בדיקה לפני פריסה למכשיר
```
unzip -t GooglePackageInstaller_patched.apk   # לוודא שהקובץ לא פגום
```
אם זה עובר תקין — זה מוכן להעביר למכשיר (אותו תהליך remount+copy+chmod+reboot
שכבר עשינו ל-Settings/SystemUI).

## רמת ביטחון
מתודת `onCreate` ונקודת ההתערבות: **ודאית**, פורקה ישירות מה-dex האמיתי.
קטע ה-smali החדש: תבנית סטנדרטית (Toast+finish), לא נבדק בפועל על מכשיר אמיתי —
זה הניסוי הראשון. לגבות את ה-APK המקורי לפני ההחלפה.
