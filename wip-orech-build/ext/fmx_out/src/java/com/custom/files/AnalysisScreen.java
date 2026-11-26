package com.custom.files;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/** Storage analysis: what takes space in a folder, biggest first, with a breakdown by file type. */
final class AnalysisScreen extends LinearLayout implements MainActivity.Screen {
    private final MainActivity a;
    private final ArrayList<File> hist = new ArrayList<>();
    private final TextView title, subtitle;
    private final LinearLayout body;
    private Ops.Op op;

    private static final class Entry {
        File f;
        long size;
        int files;
        boolean dir;
    }

    // type groups for the stacked bar
    private static final String[] GROUPS = {"תמונות", "וידאו", "שמע", "מסמכים", "אפליקציות", "ארכיונים", "אחר"};
    private static final int[] GCOL = {0xFF8B5CF6, 0xFFEF4444, 0xFF0D9488, 0xFF2563EB, 0xFF22A35A, 0xFFB7791F, 0xFF94A3B8};

    private static int group(int kind) {
        switch (kind) {
            case Fx.K_IMAGE: return 0;
            case Fx.K_VIDEO: return 1;
            case Fx.K_AUDIO: return 2;
            case Fx.K_DOC: case Fx.K_SHEET: case Fx.K_SLIDE: case Fx.K_PDF: case Fx.K_TEXT: return 3;
            case Fx.K_APK: return 4;
            case Fx.K_ARCHIVE: return 5;
            default: return 6;
        }
    }

    AnalysisScreen(MainActivity a, File root) {
        super(a);
        this.a = a;
        setOrientation(VERTICAL);
        setBackgroundColor(Ui.BG);
        LinearLayout bar = Ui.appBar(a);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_back, Ui.TEXT, "חזרה", v -> a.onBackPressed()));
        LinearLayout tv = Ui.vbox(a);
        Ui.pad(tv, 6, 0, 4, 0);
        title = Ui.line(a, "", 19, Ui.TEXT, Ui.MEDIUM);
        subtitle = Ui.line(a, "", 12.5f, Ui.TEXT2, Ui.REGULAR);
        subtitle.setTextDirection(View.TEXT_DIRECTION_RTL);
        tv.addView(title);
        tv.addView(subtitle);
        bar.addView(tv, Ui.lp(0, Ui.WRAP, 1));
        addView(bar, Ui.lp(Ui.MATCH, Ui.dp(60)));
        ScrollView sv = new ScrollView(a);
        body = Ui.vbox(a);
        Ui.pad(body, 12, 12, 12, 12);
        sv.addView(body);
        addView(sv, Ui.lp(Ui.MATCH, 0, 1));
        hist.add(root);
        load();
    }

    @Override
    public boolean onBack() {
        if (hist.size() > 1) {
            hist.remove(hist.size() - 1);
            load();
            return true;
        }
        if (op != null) op.cancel = true;
        return false;
    }

    @Override public void onShow() { a.setBars(Ui.SURFACE, true); }

    private void load() {
        if (op != null) op.cancel = true;
        final Ops.Op my = new Ops.Op();
        op = my;
        File dir = hist.get(hist.size() - 1);
        Storage.Vol v = Storage.of(a, dir);
        title.setText(v != null && v.root.equals(dir) ? "ניתוח · " + v.label : "ניתוח · " + dir.getName());
        subtitle.setText("מחשב…");
        body.removeAllViews();
        ProgressBar pb = new ProgressBar(a);
        pb.setIndeterminateTintList(Ui.csl(Ui.PRIMARY));
        body.addView(pb, Ui.margins(Ui.lp(Ui.dp(40), Ui.dp(40)), 0, 60, 0, 0));
        ((LayoutParams) pb.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
        final boolean hidden = true; // analysis counts everything
        MainActivity.BG.execute(() -> {
            ArrayList<Entry> entries = new ArrayList<>();
            long[] byGroup = new long[GROUPS.length];
            long total = 0;
            File[] fs = dir.listFiles();
            try {
                if (fs != null) for (File f : fs) {
                    Entry e = new Entry();
                    e.f = f;
                    e.dir = f.isDirectory() && !Fx.isLink(f);
                    long[] acc = new long[2];
                    walk(f, acc, byGroup, my);
                    e.size = acc[0];
                    e.files = (int) acc[1];
                    total += e.size;
                    entries.add(e);
                }
            } catch (Ops.Cancelled c) {
                return;
            }
            entries.sort((x, y) -> Long.compare(y.size, x.size));
            final long tot = total;
            MainActivity.H.post(() -> { if (op == my) show(dir, v, entries, byGroup, tot); });
        });
    }

    private static void walk(File f, long[] acc, long[] byGroup, Ops.Op op) {
        op.check();
        if (f.isDirectory()) {
            if (Fx.isLink(f)) return;
            File[] c = f.listFiles();
            if (c != null) for (File x : c) walk(x, acc, byGroup, op);
        } else {
            long s = f.length();
            acc[0] += s;
            acc[1]++;
            byGroup[group(Fx.kind(f.getName()))] += s;
        }
    }

    private LinearLayout card() {
        LinearLayout c = Ui.vbox(a);
        c.setBackground(Ui.round(Ui.SURFACE, 20));
        c.setClipToOutline(true);
        return c;
    }

    private void show(File dir, Storage.Vol v, ArrayList<Entry> entries, long[] byGroup, long total) {
        body.removeAllViews();
        subtitle.setText(Fx.size(total) + " · " + Fx.items(entries.size()));

        // summary card
        LinearLayout sum = card();
        Ui.pad(sum, 18, 18, 18, 16);
        if (v != null && v.total > 0 && v.root.equals(dir)) {
            float frac = (float) (v.total - v.free) / v.total;
            TextView big = Ui.text(a, Math.round(frac * 100) + "% בשימוש", 24, Ui.TEXT, Ui.BOLD);
            big.setTextDirection(View.TEXT_DIRECTION_RTL);
            sum.addView(big);
            TextView s = Ui.text(a, Fx.size(v.total - v.free) + " בשימוש · " + Fx.size(v.free) + " פנויים · " + Fx.size(v.total) + " סה\"כ", 13, Ui.TEXT2);
            s.setTextDirection(View.TEXT_DIRECTION_RTL);
            sum.addView(s);
        } else {
            TextView big = Ui.text(a, Fx.size(total), 24, Ui.TEXT, Ui.BOLD);
            big.setTextDirection(View.TEXT_DIRECTION_RTL);
            sum.addView(big);
            sum.addView(Ui.text(a, "בתיקיה זו ובתיקיות המשנה שלה", 13, Ui.TEXT2));
        }
        // stacked bar by type
        LinearLayout stack = Ui.hbox(a);
        stack.setBackground(Ui.round(Ui.DIVIDER, 6));
        stack.setClipToOutline(true);
        for (int i = 0; i < GROUPS.length; i++) {
            if (byGroup[i] <= 0 || total <= 0) continue;
            View seg = new View(a);
            seg.setBackgroundColor(GCOL[i]);
            stack.addView(seg, Ui.lp(0, Ui.MATCH, (float) byGroup[i] / total));
        }
        sum.addView(stack, Ui.margins(Ui.lp(Ui.MATCH, Ui.dp(12)), 0, 14, 0, 10));
        for (int i = 0; i < GROUPS.length; i++) {
            if (byGroup[i] <= 0) continue;
            LinearLayout r = Ui.hbox(a);
            View dot = new View(a);
            dot.setBackground(Ui.oval(GCOL[i]));
            r.addView(dot, Ui.lp(Ui.dp(10), Ui.dp(10)));
            TextView n = Ui.text(a, GROUPS[i], 14, Ui.TEXT);
            n.setPaddingRelative(Ui.dp(10), 0, 0, 0);
            r.addView(n, Ui.lp(0, Ui.WRAP, 1));
            TextView s = Ui.text(a, Fx.size(byGroup[i]), 14, Ui.TEXT2);
            s.setTextDirection(View.TEXT_DIRECTION_RTL);
            r.addView(s);
            sum.addView(r, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 6, 0, 0));
        }
        body.addView(sum);

        TextView h = Ui.text(a, "הפריטים הגדולים ביותר", 15, Ui.TEXT, Ui.MEDIUM);
        body.addView(h, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 8, 20, 8, 10));
        LinearLayout list = card();
        int shown = 0;
        for (Entry e : entries) {
            if (shown++ >= 200) break;
            if (shown > 1) list.addView(Ui.divider(a), Ui.margins(Ui.lp(Ui.MATCH, Math.max(1, Ui.dp(1))), 68, 0, 0, 0));
            list.addView(row(e, total));
        }
        if (entries.isEmpty()) {
            TextView t = Ui.text(a, "התיקיה ריקה", 14, Ui.TEXT2);
            Ui.pad(t, 18, 18, 18, 18);
            list.addView(t);
        }
        body.addView(list);
    }

    private View row(Entry e, long total) {
        LinearLayout r = Ui.hbox(a);
        Ui.pad(r, 14, 12, 16, 12);
        r.setBackground(Ui.rippleRect());
        FrameLayout tile = new FrameLayout(a);
        int k = e.dir ? Fx.K_DIR : Fx.kind(e.f.getName());
        if (e.dir) {
            ImageView fi = new ImageView(a);
            fi.setImageResource(R.drawable.ic_folder_big);
            tile.addView(fi, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        } else {
            tile.setBackground(Ui.round(Fx.SOFT[k], 12));
            tile.addView(Ui.img(a, Fx.ICON[k], Fx.COLOR[k]), new FrameLayout.LayoutParams(Ui.dp(22), Ui.dp(22), Gravity.CENTER));
        }
        r.addView(tile, Ui.lp(Ui.dp(42), Ui.dp(42)));
        LinearLayout t = Ui.vbox(a);
        t.setPaddingRelative(Ui.dp(14), 0, 0, 0);
        LinearLayout top = Ui.hbox(a);
        top.addView(Ui.line(a, e.f.getName(), 15, Ui.TEXT, Ui.REGULAR), Ui.lp(0, Ui.WRAP, 1));
        TextView sz = Ui.text(a, Fx.size(e.size), 13.5f, Ui.TEXT, Ui.MEDIUM);
        sz.setTextDirection(View.TEXT_DIRECTION_RTL);
        top.addView(sz);
        t.addView(top);
        float frac = total > 0 ? (float) e.size / total : 0;
        Ui.Bar bar = new Ui.Bar(a);
        bar.set(frac, e.dir ? 0xFFF0A63A : Fx.COLOR[k]);
        t.addView(bar, Ui.margins(Ui.lp(Ui.MATCH, Ui.dp(5)), 0, 7, 0, 5));
        TextView sub = Ui.line(a, (e.dir ? Fx.files(e.files) + " · " : "") + Math.round(frac * 1000) / 10f + "%", 12, Ui.TEXT2, Ui.REGULAR);
        sub.setTextDirection(View.TEXT_DIRECTION_RTL);
        t.addView(sub);
        r.addView(t, Ui.lp(0, Ui.WRAP, 1));
        r.setOnClickListener(v -> {
            if (e.dir) {
                hist.add(e.f);
                load();
            } else a.push(BrowserScreen.dir(a, e.f.getParentFile()));
        });
        r.setOnLongClickListener(v -> {
            a.push(BrowserScreen.dir(a, e.dir ? e.f : e.f.getParentFile()));
            return true;
        });
        return r;
    }
}
