package com.orech.mockloc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (c.getSharedPreferences(GpsFeedService.PREFS, Context.MODE_PRIVATE).getBoolean("boot", false)) {
            c.startForegroundService(new Intent(c, GpsFeedService.class));
        }
    }
}