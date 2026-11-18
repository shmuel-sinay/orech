### שימוש: רק ל-M393, בתוך services-3.jar (לא M36 Plus - שם הבאג הזה לא קיים!)
### קובץ: com/android/server/usb/UsbDeviceManager$UsbHandler.smali (classes2.dex)
### פעולה: החלף את כל התוכן בין ".method private setAdbEnabled(Z)V" ל-".end method" (כולל שתי השורות) בזה:

.method private setAdbEnabled(Z)V
    .registers 5

    const-string/jumbo v0, "persist.sys.usb.config"

    if-eqz v4, :cond_a

    const-string v1, "adb"

    invoke-virtual {v3, v0, v1}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setSystemProperty(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_10

    :cond_a
    const-string v1, "mtp"

    invoke-virtual {v3, v0, v1}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setSystemProperty(Ljava/lang/String;Ljava/lang/String;)V

    :goto_10
    iget-wide v0, v3, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->mCurrentFunctions:J

    const/4 v2, 0x1

    invoke-virtual {v3, v0, v1, v2}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->setEnabledFunctions(JZ)V

    const/4 v0, 0x0

    invoke-virtual {v3, v0}, Lcom/android/server/usb/UsbDeviceManager$UsbHandler;->updateAdbNotification(Z)V

    return-void
.end method
