package com.orech.mockloc;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** בחירת אפליקציות שמותר להן מיקום מדומה. לא תלוי באפשרויות מפתחים או בניפוי באגים. */
public class MockActivity extends Activity {
    private static final String OP = "android:mock_location";
    private final List<ApplicationInfo> apps = new ArrayList<>();
    private AppOpsManager ops;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setTitle("מיקום מדומה");
        ops = getSystemService(AppOpsManager.class);
        pm = getPackageManager();
        for (PackageInfo p : pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)) {
            if (p.requestedPermissions == null) continue;
            for (String r : p.requestedPermissions) {
                if ("android.permission.ACCESS_MOCK_LOCATION".equals(r)) { apps.add(p.applicationInfo); break; }
            }
        }
        Collections.sort(apps, (x, y) -> pm.getApplicationLabel(x).toString().compareToIgnoreCase(pm.getApplicationLabel(y).toString()));
        ListView lv = new ListView(this);
        lv.setAdapter(new BaseAdapter() {
            public int getCount() { return apps.size(); }
            public Object getItem(int i) { return apps.get(i); }
            public long getItemId(int i) { return i; }
            public View getView(int i, View v, ViewGroup g) {
                Switch s = v instanceof Switch ? (Switch) v : new Switch(MockActivity.this);
                s.setPadding(48, 36, 48, 36);
                s.setTextSize(17f);
                ApplicationInfo ai = apps.get(i);
                s.setOnCheckedChangeListener(null);
                s.setText(pm.getApplicationLabel(ai));
                s.setChecked(allowed(ai));
                s.setOnCheckedChangeListener((cb, on) -> set(ai, on));
                return s;
            }
        });
        if (apps.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("אין אפליקציות שמבקשות הרשאת מיקום מדומה");
            t.setPadding(48, 48, 48, 48);
            setContentView(t);
        } else setContentView(lv);
    }

    private boolean allowed(ApplicationInfo ai) {
        return ops.unsafeCheckOpNoThrow(OP, ai.uid, ai.packageName) == AppOpsManager.MODE_ALLOWED;
    }

    private void set(ApplicationInfo ai, boolean on) {
        try {
            AppOpsManager.class.getMethod("setMode", String.class, int.class, String.class, int.class)
                .invoke(ops, OP, ai.uid, ai.packageName, on ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
        } catch (Throwable t) {
            Toast.makeText(this, "נכשל: " + t, Toast.LENGTH_LONG).show();
        }
    }
}