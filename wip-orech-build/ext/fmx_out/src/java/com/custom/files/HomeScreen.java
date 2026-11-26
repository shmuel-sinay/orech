package com.custom.files;

import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/** Start screen: a flat list in three sections (storage, categories, tools) with a colour stripe per row. */
final class HomeScreen extends LinearLayout implements MainActivity.Screen {
    private final MainActivity a;
    private final LinearLayout grid, favBox, favSection;
    private int gen;

    /** One tile on the home grid. */
    private static final class Tile {
        int icon, color, soft;
        String title, sub = " ", section = "";
        float frac = -1;
        Runnable click;
    }

    HomeScreen(MainActivity a) {
        super(a);
        this.a = a;
        setOrientation(VERTICAL);
        setBackgroundColor(Ui.BG);

        LinearLayout bar = Ui.appBar(a);
        bar.setBackgroundColor(Ui.DARK);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_menu, Ui.BG, "תפריט", v -> a.openDrawer()));
        TextView t = Ui.line(a, Flavor.NAME, 20, Ui.BG, Ui.BOLD);
        t.setPaddingRelative(Ui.dp(8), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.BG, "חיפוש", v -> a.push(BrowserScreen.search(a))));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.BG, "אפשרויות", v -> menu()));
        addView(bar, Ui.lp(Ui.MATCH, Ui.dp(60)));

        ScrollView sv = new ScrollView(a);
        sv.setFillViewport(true);
        sv.setVerticalScrollBarEnabled(false);
        LinearLayout col = Ui.vbox(a);
        Ui.pad(col, 0, 0, 0, 16);
        sv.addView(col, new ScrollView.LayoutParams(Ui.MATCH, Ui.WRAP));
        addView(sv, Ui.lp(Ui.MATCH, 0, 1));

        grid = Ui.vbox(a);
        col.addView(grid);

        favSection = Ui.vbox(a);
        favSection.addView(sectionHeader("מועדפים"), Ui.lp(Ui.MATCH, Ui.WRAP));
        favBox = Ui.vbox(a);
        favSection.addView(favBox, Ui.lp(Ui.MATCH, Ui.WRAP));
        col.addView(favSection);

        refresh();
    }

    @Override public boolean onBack() { return false; }

    @Override
    public void onShow() {
        a.setBars(Ui.DARK, false);
        refresh();
    }

    // ------------------------------------------------------------ tiles

    private TextView sectionHeader(String title) {
        TextView h = Ui.text(a, title, 12, Ui.TEXT2, Ui.MEDIUM);
        h.setLetterSpacing(0.08f);
        Ui.pad(h, 16, 14, 16, 4);
        return h;
    }

    private View rowView(Tile t) {
        FrameLayout wrap = new FrameLayout(a);
        wrap.setBackground(Ui.rippleRect());
        LinearLayout r = Ui.hbox(a);
        Ui.pad(r, 18, 11, 16, 11);
        r.addView(Ui.img(a, t.icon, t.color), Ui.lp(Ui.dp(24), Ui.dp(24)));
        LinearLayout mid = Ui.vbox(a);
        mid.setPaddingRelative(Ui.dp(14), 0, Ui.dp(10), 0);
        mid.addView(Ui.line(a, t.title, 16, Ui.TEXT, Ui.MEDIUM));
        if (t.frac >= 0) {
            Ui.Bar bar = new Ui.Bar(a);
            bar.flat = true;
            bar.set(t.frac, t.frac > 0.9f ? Ui.DANGER : t.frac > 0.75f ? Ui.AMBER : t.color);
            mid.addView(bar, Ui.margins(Ui.lp(Ui.MATCH, Ui.dp(4)), 0, 6, 0, 0));
        }
        r.addView(mid, Ui.lp(0, Ui.WRAP, 1));
        TextView s = Ui.line(a, t.sub, 13, Ui.TEXT2, Ui.REGULAR);
        s.setTextDirection(View.TEXT_DIRECTION_RTL);
        r.addView(s, Ui.lp(Ui.WRAP, Ui.WRAP));
        wrap.addView(r, new FrameLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        View stripe = new View(a);
        stripe.setBackgroundColor(t.color);
        wrap.addView(stripe, new FrameLayout.LayoutParams(Ui.dp(4), Ui.MATCH, Gravity.START));
        View line = new View(a);
        line.setBackgroundColor(Ui.DIVIDER);
        wrap.addView(line, new FrameLayout.LayoutParams(Ui.MATCH, Math.max(1, Ui.dp(1)), Gravity.BOTTOM));
        wrap.setOnClickListener(v -> t.click.run());
        return wrap;
    }

    private void layoutTiles(ArrayList<Tile> tiles) {
        grid.removeAllViews();
        String cur = null;
        for (Tile t : tiles) {
            if (!t.section.equals(cur)) {
                cur = t.section;
                grid.addView(sectionHeader(cur), Ui.lp(Ui.MATCH, Ui.WRAP));
            }
            grid.addView(rowView(t), Ui.lp(Ui.MATCH, Ui.WRAP));
        }
    }

    private Tile catTile(int cat, long[] st) {
        Tile t = new Tile();
        t.icon = Media.ICON[cat];
        t.color = Media.COLOR[cat];
        t.soft = Media.SOFT[cat];
        t.title = Media.TITLES[cat];
        t.section = "קטגוריות";
        if (st != null && st[0] >= 0) t.sub = Fx.size(st[1]) + " (" + Fx.num(st[0]) + ")";
        t.click = () -> a.push(BrowserScreen.cat(a, cat));
        return t;
    }

    private ArrayList<Tile> buildTiles(ArrayList<Storage.Vol> vols, long[] dl, long[][] cats, int trash) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (Storage.Vol v : vols) {
            Tile t = new Tile();
            t.icon = v.primary ? R.drawable.ic_phone : R.drawable.ic_sd;
            t.color = v.primary ? 0xFF2F6F9F : 0xFFB3472F;
            t.soft = v.primary ? 0xFFDDE9F2 : 0xFFF3E1DA;
            t.title = v.label;
            t.section = "אחסון";
            if (v.total > 0) {
                t.sub = Fx.size(v.total - v.free) + " / " + Fx.size(v.total);
                t.frac = (float) (v.total - v.free) / v.total;
            }
            t.click = () -> a.push(BrowserScreen.dir(a, v.root));
            tiles.add(t);
        }
        if (a.prefs.systemStorage) {
            Tile t = new Tile();
            t.icon = R.drawable.ic_system;
            t.color = 0xFF4A5A63;
            t.soft = 0xFFE4E7E8;
            t.title = "אחסון מערכת";
            t.section = "אחסון";
            t.sub = "/";
            t.click = () -> a.push(BrowserScreen.dir(a, new File("/")));
            tiles.add(t);
        }
        Tile d = new Tile();
        d.icon = R.drawable.ic_download;
        d.color = 0xFF2F6F9F;
        d.soft = 0xFFDDE9F2;
        d.title = "הורדות";
        d.section = "קטגוריות";
        if (dl != null && dl[0] >= 0) d.sub = Fx.size(dl[1]) + " (" + Fx.num(dl[0]) + ")";
        File dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        d.click = () -> a.push(BrowserScreen.dir(a, dlDir));
        tiles.add(d);
        int[] order = {Media.IMAGES, Media.AUDIO, Media.VIDEO, Media.DOCS, Media.APPS, Media.RECENT, Media.ARCHIVES, Media.LARGE};
        for (int c : order) if (!(Flavor.NO_VIDEO && c == Media.VIDEO)) tiles.add(catTile(c, cats == null ? null : cats[c]));
        Tile tr = new Tile();
        tr.icon = R.drawable.ic_delete;
        tr.color = 0xFF6B665C;
        tr.soft = 0xFFE9E5DB;
        tr.title = "סל מיחזור";
        tr.section = "כלים";
        tr.sub = trash < 0 ? " " : trash == 0 ? "ריק" : Fx.items(trash);
        tr.click = () -> a.push(BrowserScreen.trash(a));
        tiles.add(tr);
        Tile an = new Tile();
        an.icon = R.drawable.ic_pie;
        an.color = 0xFF7A4B63;
        an.soft = 0xFFEBDDE4;
        an.title = "ניתוח אחסון";
        an.section = "כלים";
        an.sub = "מה תופס מקום";
        an.click = () -> a.push(new AnalysisScreen(a, Storage.primaryRoot(a)));
        tiles.add(an);
        return tiles;
    }

    private ArrayList<Storage.Vol> lastVols;
    private long[] lastDl;
    private long[][] lastCats;

    void refresh() {
        final int g = ++gen;
        // draw immediately with what we know, then fill in the numbers
        layoutTiles(buildTiles(lastVols != null ? lastVols : Storage.list(a, false), lastDl, lastCats, -1));
        refreshFavs();
        MainActivity.BG.execute(() -> {
            ArrayList<Storage.Vol> vols = Storage.list(a, true);
            int trash = Trash.count(a);
            MainActivity.H.post(() -> {
                if (g != gen) return;
                lastVols = vols;
                layoutTiles(buildTiles(vols, lastDl, lastCats, trash));
                a.checkStorageFull(vols);
            });
            long[] dl = Media.statsUnder(a, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            long[][] cats = new long[Media.N][];
            for (int i = 0; i < Media.N; i++) cats[i] = Media.stats(a, i);
            MainActivity.H.post(() -> {
                if (g != gen) return;
                lastDl = dl;
                lastCats = cats;
                layoutTiles(buildTiles(vols, dl, cats, trash));
            });
        });
    }

    private void refreshFavs() {
        ArrayList<String> bm = a.prefs.bookmarks();
        favSection.setVisibility(bm.isEmpty() ? GONE : VISIBLE);
        favBox.removeAllViews();
        for (int i = 0; i < bm.size(); i++) {
            String p = bm.get(i);
            File f = new File(p);
            if (i > 0) favBox.addView(Ui.divider(a));
            LinearLayout r = Ui.hbox(a);
            Ui.pad(r, 14, 10, 10, 10);
            r.setBackground(Ui.rippleRect());
            r.addView(Ui.img(a, R.drawable.ic_star, Ui.AMBER), Ui.lp(Ui.dp(24), Ui.dp(24)));
            LinearLayout texts = Ui.vbox(a);
            texts.setPaddingRelative(Ui.dp(14), 0, Ui.dp(8), 0);
            texts.addView(Ui.line(a, f.getName().isEmpty() ? p : f.getName(), 15.5f, Ui.TEXT, Ui.REGULAR));
            TextView s = Ui.line(a, Storage.pretty(a, f), 12.5f, Ui.TEXT2, Ui.REGULAR);
            s.setTextDirection(View.TEXT_DIRECTION_RTL);
            texts.addView(s);
            r.addView(texts, Ui.lp(0, Ui.WRAP, 1));
            r.addView(Ui.img(a, R.drawable.ic_chevron, Ui.TEXT3), Ui.lp(Ui.dp(20), Ui.dp(20)));
            r.setOnClickListener(v -> {
                if (f.isDirectory()) a.push(BrowserScreen.dir(a, f));
                else a.toast("התיקיה לא קיימת");
            });
            r.setOnLongClickListener(v -> {
                Dialogs.confirm(a, "להסיר מהמועדפים?", f.getName(), "הסר", false, null, c -> {
                    a.prefs.setBookmarked(p, false);
                    refreshFavs();
                });
                return true;
            });
            favBox.addView(r);
        }
    }

    private void menu() {
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        m.add(new Dialogs.Item(R.drawable.ic_eye, a.prefs.hidden ? "הסתרת קבצים נסתרים" : "הצגת קבצים נסתרים", () -> {
            a.prefs.hidden = !a.prefs.hidden;
            a.prefs.save();
            refresh();
        }));
        m.add(new Dialogs.Item(R.drawable.ic_pie, "ניתוח אחסון", () -> a.push(new AnalysisScreen(a, Storage.primaryRoot(a)))));
        m.add(new Dialogs.Item(R.drawable.ic_refresh, "רענון", this::refresh));
        m.add(new Dialogs.Item(R.drawable.ic_settings, "הגדרות", () -> a.push(new SettingsScreen(a))));
        Dialogs.sheet(a, null, null, m);
    }
}
