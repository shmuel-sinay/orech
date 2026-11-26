package com.orech.mockloc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/** נפתחת בחיבור משדר USB: מקבלת הרשאת USB מהמערכת ומפעילה את השירות אם הוגדר. */
public class UsbAttachActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (getSharedPreferences(GpsFeedService.PREFS, Context.MODE_PRIVATE).getBoolean("autostart_usb", true)) {
            startForegroundService(new Intent(this, GpsFeedService.class));
        }
        finish();
    }
}