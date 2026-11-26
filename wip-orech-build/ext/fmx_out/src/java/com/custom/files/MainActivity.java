package com.custom.files;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    static final Handler H = new Handler(Looper.getMainLooper());
    static final ExecutorService BG = Executors.newCachedThreadPool();
    static final String EXTRA_PATH = "path", EXTRA_MUSIC = "music";

    interface Screen {
        boolean onBack();
        void onShow();
    }

    Prefs prefs;
    FrameLayout host, screens;
    Drawer drawer;
    Music.Mini mini;
    final ArrayList<View> stack = new ArrayList<>();
    final ArrayList<File> clip = new ArrayList<>();
    boolean clipCut;
    private boolean permShown, firstResume = true, fullNotified;
    private Toast toast;
    private String pendingPath;

    private final BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) {
            if (!prefs.usb) return;
            H.postDelayed(() -> {
                Storage.list(MainActivity.this, true);
                View t = top();
                if (t instanceof HomeScreen) ((HomeScreen) t).refresh();
                if (Intent.ACTION_MEDIA_MOUNTED.equals(i.getAction())) toast("זוהה התקן אחסון חדש");
            }, 600);
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Ui.init(this);
        prefs = new Prefs(this);
        Window w = getWindow();
        w.setNavigationBarColor(Ui.SURFACE);
        // Hebrew first: the whole UI is right-to-left regardless of the device language
        w.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setBars(Ui.SURFACE, true);
        host = new FrameLayout(this);
        host.setBackgroundColor(Ui.BG);
        screens = new FrameLayout(this);
        LinearLayout column = Ui.vbox(this);
        column.addView(screens, Ui.lp(Ui.MATCH, 0, 1));
        mini = new Music.Mini(this);
        column.addView(mini, Ui.lp(Ui.MATCH, Ui.WRAP));
        host.addView(column, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        drawer = new Drawer(this);
        host.addView(drawer, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        setContentView(host);
        pendingPath = getIntent().getStringExtra(EXTRA_PATH);
        if (hasPerm()) showHome();
        else showPermission();
        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_MEDIA_MOUNTED);
        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        f.addAction(Intent.ACTION_MEDIA_REMOVED);
        f.addDataScheme("file");
        try { registerReceiver(mediaReceiver, f); } catch (Exception ignored) { }
    }

    @Override
    protected void onDestroy() {
        try { unregisterReceiver(mediaReceiver); } catch (Exception ignored) { }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        if (i.getBooleanExtra(EXTRA_MUSIC, false) && hasPerm() && !permShown) { openMusic(); return; }
        String p = i.getStringExtra(EXTRA_PATH);
        if (p != null && hasPerm() && !permShown) {
            goHome();
            openPath(p);
        }
    }

    private void openPath(String p) {
        File f = new File(p);
        if (f.isDirectory()) push(BrowserScreen.dir(this, f), false);
        else toast("התיקיה לא קיימת");
    }

    @Override
    protected void onPause() {
        View t = top();
        if (t instanceof Viewers.Closeable) ((Viewers.Closeable) t).pauseHint();
        super.onPause();
    }

    /** Open the full music player (or bring it to the front). */
    void openMusic() {
        if (!Player.active()) return;
        View t = top();
        if (t instanceof Music.Screen) return;
        for (View v : new ArrayList<>(stack)) if (v instanceof Music.Screen) { stack.remove(v); screens.removeView(v); }
        push(new Music.Screen(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstResume) { firstResume = false; return; }
        if (permShown) {
            if (hasPerm()) showHome();
            return;
        }
        View t = top();
        if (t instanceof Screen) ((Screen) t).onShow();
    }

    // ------------------------------------------------------------ permission

    boolean hasPerm() {
        if (Build.VERSION.SDK_INT >= 30) return Environment.isExternalStorageManager();
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPerm() {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
            } catch (Exception e) {
                try { startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)); }
                catch (Exception ignored) { toast("פתח את ההגדרות ואפשר גישה לכל הקבצים"); }
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        if (code == 1 && hasPerm() && permShown) showHome();
    }

    private void showPermission() {
        permShown = true;
        screens.removeAllViews();
        stack.clear();
        LinearLayout col = Ui.vbox(this);
        col.setGravity(Gravity.CENTER);
        Ui.pad(col, 32, 0, 32, 0);
        col.setBackgroundColor(Ui.SURFACE);
        FrameLayout tile = new FrameLayout(this);
        tile.setBackground(Ui.round(Ui.PRIMARY_SOFT, 28));
        ImageView ic = Ui.img(this, R.drawable.ic_folder, Ui.PRIMARY);
        tile.addView(ic, new FrameLayout.LayoutParams(Ui.dp(44), Ui.dp(44), Gravity.CENTER));
        col.addView(tile, Ui.lp(Ui.dp(96), Ui.dp(96)));
        TextView t = Ui.text(this, "נדרשת גישה לקבצים", 22, Ui.TEXT, Ui.BOLD);
        t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        col.addView(t, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 24, 0, 0));
        TextView m = Ui.text(this, "כדי להציג, להעתיק, להעביר ולמחוק קבצים, יש לאפשר למנהל הקבצים גישה לכל הקבצים במכשיר.\nהאפליקציה פועלת ללא אינטרנט וכל המידע נשאר במכשיר.",
                15, Ui.TEXT2);
        m.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        m.setLineSpacing(0, 1.25f);
        col.addView(m, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 12, 0, 0));
        TextView btn = Ui.button(this, "מתן הרשאה", true, v -> requestPerm());
        btn.setTextSize(16);
        Ui.pad(btn, 32, 14, 32, 14);
        col.addView(btn, Ui.margins(Ui.lp(Ui.WRAP, Ui.WRAP), 0, 32, 0, 0));
        screens.addView(col, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
    }

    // ------------------------------------------------------------ navigation

    View top() { return stack.isEmpty() ? null : stack.get(stack.size() - 1); }

    void showHome() {
        permShown = false;
        screens.removeAllViews();
        stack.clear();
        BG.execute(() -> Trash.purge(getApplicationContext()));
        push(new HomeScreen(this), false);
        if (getIntent().getBooleanExtra(EXTRA_MUSIC, false)) openMusic();
        if (pendingPath != null) {
            openPath(pendingPath);
            pendingPath = null;
        }
    }

    void push(View v) { push(v, true); }

    void push(View v, boolean anim) {
        final View prev = top();
        hideKeyboard();
        screens.addView(v, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        stack.add(v);
        if (anim && prev != null) {
            v.setAlpha(0f);
            v.setTranslationX(-Ui.dp(36)); // RTL: forward slides in from the left
            v.animate().alpha(1f).translationX(0).setDuration(200).setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> { if (top() == v) prev.setVisibility(View.GONE); }).start();
        } else if (prev != null) prev.setVisibility(View.GONE);
        if (mini != null) mini.refresh();
    }

    void pop() {
        if (stack.size() <= 1) { finish(); return; }
        final View v = stack.remove(stack.size() - 1);
        if (v instanceof Viewers.Closeable) ((Viewers.Closeable) v).close();
        View prev = top();
        prev.setVisibility(View.VISIBLE);
        prev.setAlpha(1f);
        hideKeyboard();
        v.animate().cancel();
        v.animate().alpha(0f).translationX(-Ui.dp(36)).setDuration(160)
                .withEndAction(() -> screens.removeView(v)).start();
        if (prev instanceof Screen) ((Screen) prev).onShow();
        if (mini != null) mini.refresh();
    }

    void goHome() {
        while (stack.size() > 1) {
            View v = stack.remove(stack.size() - 1);
            if (v instanceof Viewers.Closeable) ((Viewers.Closeable) v).close();
            screens.removeView(v);
        }
        View h = top();
        if (h == null) return;
        h.setVisibility(View.VISIBLE);
        h.setAlpha(1f);
        if (h instanceof Screen) ((Screen) h).onShow();
        if (mini != null) mini.refresh();
    }

    void openDrawer() {
        hideKeyboard();
        drawer.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.open) { drawer.close(); return; }
        View t = top();
        if (t instanceof Screen && ((Screen) t).onBack()) return;
        if (stack.size() > 1) pop();
        else super.onBackPressed();
    }

    // ------------------------------------------------------------ helpers

    /** Status bar color; light = dark icons on a light bar. */
    void setBars(int color, boolean light) {
        Window w = getWindow();
        w.setStatusBarColor(color);
        int flags = light ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0;
        if (Build.VERSION.SDK_INT >= 27) flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        w.getDecorView().setSystemUiVisibility(flags);
        w.setNavigationBarColor(light ? Ui.SURFACE : 0xFF000000);
    }

    void setClip(List<File> files, boolean cut) {
        clip.clear();
        clip.addAll(files);
        clipCut = cut;
    }

    void clearClip() { clip.clear(); }

    void toast(String s) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    void hideKeyboard() {
        View f = getCurrentFocus();
        if (f == null) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
        f.clearFocus();
    }

    void showKeyboard(View v) {
        v.requestFocus();
        H.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }, 150);
    }

    /** Pin a launcher shortcut that opens a folder. */
    void addShortcut(File dir) {
        ShortcutManager sm = getSystemService(ShortcutManager.class);
        if (sm == null || !sm.isRequestPinShortcutSupported()) { toast("מסך הבית אינו תומך בקיצורי דרך"); return; }
        Intent i = new Intent(this, MainActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        i.putExtra(EXTRA_PATH, dir.getAbsolutePath());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String label = dir.getName().isEmpty() ? "קבצים" : dir.getName();
        ShortcutInfo si = new ShortcutInfo.Builder(this, "dir:" + dir.getAbsolutePath())
                .setShortLabel(label)
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(i)
                .build();
        try { sm.requestPinShortcut(si, null); } catch (Exception e) { toast("לא ניתן להוסיף קיצור דרך"); }
    }

    /** "Storage full" notification when a volume is over 96%. */
    void checkStorageFull(List<Storage.Vol> vols) {
        if (!prefs.notifyFull || fullNotified) return;
        for (Storage.Vol v : vols) {
            if (v.total <= 0 || (double) (v.total - v.free) / v.total < 0.96) continue;
            fullNotified = true;
            if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 2);
                return;
            }
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm == null) return;
            nm.createNotificationChannel(new NotificationChannel("storage", "אחסון מלא", NotificationManager.IMPORTANCE_DEFAULT));
            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
            Notification n = new Notification.Builder(this, "storage")
                    .setSmallIcon(R.drawable.ic_pie)
                    .setContentTitle(v.label + " כמעט מלא")
                    .setContentText("נותרו " + Fx.size(v.free) + " פנויים. הקישו לניתוח האחסון.")
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            nm.notify(1, n);
            return;
        }
    }
}
