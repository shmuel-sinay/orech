package com.orech.mockloc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** מסך "מיקום ב-USB" בהגדרות: משדר GPS מובנה (USB או בלוטוס) ובחירת אפליקציות חיצוניות. */
public class MockActivity extends Activity {
    private static final String OP = "android:mock_location";
    private SharedPreferences sp;
    private AppOpsManager ops;
    private PackageManager pm;
    private TextView statusView;
    private final Handler h = new Handler(Looper.getMainLooper());
    private final List<String> btAddrs = new ArrayList<>();

    private final Runnable tick = new Runnable() {
        public void run() {
            Location l = GpsFeedService.last;
            String s = "מצב: " + GpsFeedService.status;
            if (l != null) s += String.format("\nמיקום אחרון: %.5f, %.5f", l.getLatitude(), l.getLongitude());
            statusView.setText(s);
            h.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setTitle("מיקום ב-USB");
        sp = getSharedPreferences(GpsFeedService.PREFS, MODE_PRIVATE);
        ops = getSystemService(AppOpsManager.class);
        pm = getPackageManager();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(48, 24, 48, 48);
        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        setContentView(sv);

        root.addView(header("משדר GPS מובנה"));
        statusView = text("מצב: כבוי");
        root.addView(statusView);

        Switch run = new Switch(this);
        run.setText("הפעל משדר");
        run.setTextSize(17f);
        run.setPadding(0, 24, 0, 24);
        run.setChecked(GpsFeedService.running);
        run.setOnCheckedChangeListener((v, on) -> {
            Intent i = new Intent(this, GpsFeedService.class);
            if (on) startForegroundService(i); else { i.setAction(GpsFeedService.ACTION_STOP); startService(i); }
        });
        root.addView(run);

        root.addView(label("מקור נתונים"));
        Spinner src = spinner(new String[]{"USB", "בלוטוס"}, sp.getString("source", "usb").equals("bt") ? 1 : 0, pos -> sp.edit().putString("source", pos == 1 ? "bt" : "usb").apply());
        root.addView(src);

        root.addView(label("קצב נתונים (baud) ל-USB"));
        String[] bauds = {"4800", "9600", "19200", "38400", "57600", "115200"};
        int bi = Math.max(0, java.util.Arrays.asList(bauds).indexOf(sp.getString("baud", "9600")));
        root.addView(spinner(bauds, bi, pos -> sp.edit().putString("baud", bauds[pos]).apply()));

        root.addView(label("התקן בלוטוס (הצמד קודם בהגדרות הבלוטוס)"));
        List<String> names = new ArrayList<>();
        try {
            BluetoothAdapter ad = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> bonded = ad != null ? ad.getBondedDevices() : null;
            if (bonded != null) for (BluetoothDevice d : bonded) { names.add(d.getName() + "  " + d.getAddress()); btAddrs.add(d.getAddress()); }
        } catch (SecurityException ignored) { }
        if (names.isEmpty()) { names.add("אין התקנים מוצמדים"); btAddrs.add(""); }
        int sel = Math.max(0, btAddrs.indexOf(sp.getString("btaddr", "")));
        root.addView(spinner(names.toArray(new String[0]), sel, pos -> sp.edit().putString("btaddr", btAddrs.get(pos)).apply()));

        root.addView(check("התחבר מחדש אוטומטית", "reconnect", true));
        root.addView(check("הפעל משדר בהדלקת המכשיר", "boot", false));
        root.addView(check("הפעל משדר בחיבור התקן USB", "autostart_usb", true));

        Button demo = new Button(this);
        demo.setText("בדיקת סימולציה (מיקום לדוגמה)");
        demo.setOnClickListener(v -> { GpsFeedService.injectSample(getApplicationContext()); Toast.makeText(this, "נשלח מיקום לדוגמה", Toast.LENGTH_SHORT).show(); });
        root.addView(demo);

        root.addView(header("אפליקציות חיצוניות שמותר להן מיקום ב-USB"));
        List<ApplicationInfo> apps = new ArrayList<>();
        for (PackageInfo p : pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)) {
            if (p.requestedPermissions == null || p.packageName.equals(getPackageName())) continue;
            for (String r : p.requestedPermissions) {
                if ("android.permission.ACCESS_MOCK_LOCATION".equals(r)) { apps.add(p.applicationInfo); break; }
            }
        }
        Collections.sort(apps, (x, y) -> pm.getApplicationLabel(x).toString().compareToIgnoreCase(pm.getApplicationLabel(y).toString()));
        if (apps.isEmpty()) root.addView(text("אין אפליקציות כאלה"));
        for (ApplicationInfo ai : apps) {
            Switch s = new Switch(this);
            s.setText(pm.getApplicationLabel(ai));
            s.setTextSize(17f);
            s.setPadding(0, 24, 0, 24);
            s.setChecked(allowed(ai));
            s.setOnCheckedChangeListener((cb, on) -> set(ai, on));
            root.addView(s);
        }
    }

    @Override protected void onResume() { super.onResume(); h.post(tick); }
    @Override protected void onPause() { super.onPause(); h.removeCallbacks(tick); }

    private interface Pick { void on(int pos); }

    private Spinner spinner(String[] items, int sel, Pick p) {
        Spinner s = new Spinner(this);
        s.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
        s.setSelection(sel);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> a, View v, int pos, long id) { p.on(pos); }
            public void onNothingSelected(AdapterView<?> a) { }
        });
        return s;
    }

    private CheckBox check(String title, String key, boolean def) {
        CheckBox c = new CheckBox(this);
        c.setText(title);
        c.setTextSize(16f);
        c.setChecked(sp.getBoolean(key, def));
        c.setOnCheckedChangeListener((v, on) -> sp.edit().putBoolean(key, on).apply());
        return c;
    }

    private TextView header(String t) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(20f);
        v.setPadding(0, 40, 0, 12);
        v.setGravity(Gravity.START);
        return v;
    }

    private TextView label(String t) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(14f);
        v.setPadding(0, 24, 0, 4);
        return v;
    }

    private TextView text(String t) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(15f);
        return v;
    }

    private boolean allowed(ApplicationInfo ai) {
        return ops.unsafeCheckOpRawNoThrow(OP, ai.uid, ai.packageName) == AppOpsManager.MODE_ALLOWED;
    }

    private void set(ApplicationInfo ai, boolean on) {
        try {
            AppOpsManager.class.getMethod("setMode", String.class, int.class, String.class, int.class)
                .invoke(ops, OP, ai.uid, ai.packageName, on ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
        } catch (Throwable t) {
            Throwable c = t.getCause() != null ? t.getCause() : t;
            Toast.makeText(this, "נכשל: " + c, Toast.LENGTH_LONG).show();
        }
    }
}