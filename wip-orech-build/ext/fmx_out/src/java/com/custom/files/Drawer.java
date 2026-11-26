package com.custom.files;

import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/** Side navigation panel (slides in from the right in RTL). */
final class Drawer extends FrameLayout {
    private final MainActivity a;
    private final View scrim;
    private final ScrollView panel;
    private final LinearLayout col;
    boolean open;

    Drawer(MainActivity a) {
        super(a);
        this.a = a;
        scrim = new View(a);
        scrim.setBackgroundColor(0x66000000);
        scrim.setOnClickListener(v -> close());
        addView(scrim, new LayoutParams(Ui.MATCH, Ui.MATCH));
        panel = new ScrollView(a);
        panel.setBackgroundColor(Ui.SURFACE);
        panel.setElevation(Ui.dp(16));
        panel.setVerticalScrollBarEnabled(false);
        col = Ui.vbox(a);
        Ui.pad(col, 0, 0, 0, 16);
        panel.addView(col);
        int w = Math.min(Ui.dp(310), (int) (a.getResources().getDisplayMetrics().widthPixels * 0.84f));
        addView(panel, new LayoutParams(w, Ui.MATCH, Gravity.START));
        setVisibility(GONE);
    }

    void show() {
        build();
        open = true;
        setVisibility(VISIBLE);
        int w = panel.getLayoutParams().width;
        panel.setTranslationX(w); // RTL: hidden off the right edge
        scrim.setAlpha(0f);
        panel.animate().translationX(0).setDuration(220).setInterpolator(new DecelerateInterpolator()).start();
        scrim.animate().alpha(1f).setDuration(220).start();
    }

    void close() {
        if (!open) return;
        open = false;
        panel.animate().translationX(panel.getWidth()).setDuration(180).start();
        scrim.animate().alpha(0f).setDuration(180).withEndAction(() -> setVisibility(GONE)).start();
    }

    private void header(String s) {
        TextView t = Ui.text(a, s, 13, Ui.TEXT2, Ui.MEDIUM);
        Ui.pad(t, 22, 16, 22, 6);
        col.addView(t);
    }

    private void item(int icon, int color, String title, String sub, Runnable r) {
        LinearLayout row = Ui.hbox(a);
        Ui.pad(row, 22, 0, 16, 0);
        row.setBackground(Ui.rippleRect());
        row.addView(Ui.img(a, icon, color), Ui.lp(Ui.dp(22), Ui.dp(22)));
        LinearLayout t = Ui.vbox(a);
        t.setPaddingRelative(Ui.dp(20), 0, 0, 0);
        t.addView(Ui.line(a, title, 15, Ui.TEXT, Ui.REGULAR));
        if (sub != null) {
            TextView s = Ui.line(a, sub, 12, Ui.TEXT2, Ui.REGULAR);
            s.setTextDirection(View.TEXT_DIRECTION_RTL);
            t.addView(s);
        }
        row.addView(t, Ui.lp(0, Ui.WRAP, 1));
        row.setOnClickListener(v -> {
            close();
            a.goHome();
            r.run();
        });
        col.addView(row, Ui.lp(Ui.MATCH, Ui.dp(sub == null ? 50 : 58)));
    }

    private void build() {
        col.removeAllViews();
        LinearLayout head = Ui.hbox(a);
        Ui.pad(head, 22, 36, 22, 20);
        head.setBackgroundColor(Ui.PRIMARY_SOFT);
        FrameLayout tile = new FrameLayout(a);
        tile.setBackground(Ui.round(Ui.PRIMARY, 14));
        tile.addView(Ui.img(a, R.drawable.ic_folder, 0xFFFFFFFF), new LayoutParams(Ui.dp(26), Ui.dp(26), Gravity.CENTER));
        head.addView(tile, Ui.lp(Ui.dp(48), Ui.dp(48)));
        LinearLayout ht = Ui.vbox(a);
        ht.setPaddingRelative(Ui.dp(14), 0, 0, 0);
        ht.addView(Ui.text(a, Flavor.NAME, 19, Ui.TEXT, Ui.BOLD));
        ht.addView(Ui.text(a, "מהיר, נקי ובעברית", 12.5f, Ui.TEXT2));
        head.addView(ht);
        col.addView(head);

        header("אחסון");
        for (Storage.Vol v : Storage.list(a, false)) {
            String sub = v.total > 0 ? Fx.size(v.free) + " פנויים מתוך " + Fx.size(v.total) : null;
            item(v.primary ? R.drawable.ic_phone : R.drawable.ic_sd, Ui.TEXT2, v.label, sub, () -> a.push(BrowserScreen.dir(a, v.root)));
        }
        if (a.prefs.systemStorage)
            item(R.drawable.ic_system, Ui.TEXT2, "אחסון מערכת", "/", () -> a.push(BrowserScreen.dir(a, new File("/"))));

        header("ספריות");
        int[] order = {Media.IMAGES, Media.AUDIO, Media.VIDEO, Media.DOCS, Media.APPS, Media.RECENT};
        for (int c : order) if (!(Flavor.NO_VIDEO && c == Media.VIDEO)) item(Media.ICON[c], Media.COLOR[c], Media.TITLES[c], null, () -> a.push(BrowserScreen.cat(a, c)));

        ArrayList<String> bm = a.prefs.bookmarks();
        if (!bm.isEmpty()) {
            header("מועדפים");
            for (String p : bm) {
                File f = new File(p);
                item(R.drawable.ic_star, Ui.AMBER, f.getName().isEmpty() ? p : f.getName(), null, () -> a.push(BrowserScreen.dir(a, f)));
            }
        }
        if (a.prefs.history) {
            ArrayList<String> rec = a.prefs.recent();
            if (!rec.isEmpty()) {
                header("היסטוריה");
                for (String p : rec) {
                    File f = new File(p);
                    item(R.drawable.ic_history, Ui.TEXT2, f.getName().isEmpty() ? p : f.getName(), Storage.pretty(a, f.getParentFile() == null ? f : f.getParentFile()),
                            () -> a.push(BrowserScreen.dir(a, f)));
                }
            }
        }
        header("כלים");
        item(R.drawable.ic_delete, Ui.TEXT2, "סל מיחזור", null, () -> a.push(BrowserScreen.trash(a)));
        item(R.drawable.ic_pie, Ui.TEXT2, "ניתוח אחסון", null, () -> a.push(new AnalysisScreen(a, Storage.primaryRoot(a))));
        item(R.drawable.ic_settings, Ui.TEXT2, "הגדרות", null, () -> a.push(new SettingsScreen(a)));
    }
}
