package com.custom.files;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The file list. One screen keeps its own history (folder → sub-folder → search …),
 * so "back" returns exactly where you were, including the scroll position.
 */
final class BrowserScreen extends FrameLayout implements MainActivity.Screen {
    static final int M_DIR = 0, M_CAT = 1, M_DEEP = 2, M_GLOBAL = 3, M_TRASH = 4;
    static final int P_REPLACE = 0, P_KEEP = 1, P_SKIP = 2;

    static final class Loc {
        int mode;
        File dir;          // M_DIR folder, M_DEEP root, M_CAT folder filter (null = whole category)
        int cat;
        boolean groups;    // M_CAT: show media folders instead of files
        String query = "";
        int pos, top;
        boolean customSort;
        String focus;
    }

    private final MainActivity a;
    private final Prefs p;
    private final ArrayList<Loc> hist = new ArrayList<>();
    private final ArrayList<FileItem> all = new ArrayList<>();
    private final FileAdapter ad;

    private LinearLayout bar, selBar, searchBar, actionBar, pasteBar, crumbs, emptyView, usageChip;
    private HorizontalScrollView crumbScroll;
    private View crumbRow;
    private TextView title, subtitle, selTitle, pasteTitle, pasteSub, emptyText, usageText;
    private Ui.Pie usagePie;
    private EditText searchEt;
    private ImageView searchGo, searchClear, leading;
    private ListView list;
    private GridView grid;
    private ProgressBar progress;

    private String filter = "";
    private boolean searchOpen, selecting, mute, loading;
    private int gen, selCount;

    private final Runnable globalSearch = () -> {
        cur().query = searchEt.getText().toString().trim();
        reload();
    };
    private final Runnable showProgress = () -> {
        if (!loading) return;
        progress.setVisibility(VISIBLE);
        emptyView.setVisibility(GONE);
    };

    // ------------------------------------------------------------ factories

    static BrowserScreen dir(MainActivity a, File d) { return new BrowserScreen(a, dirLoc(d)); }

    static BrowserScreen cat(MainActivity a, int c) {
        Loc l = new Loc();
        l.mode = M_CAT;
        l.cat = c;
        l.groups = Media.groupable(c) && a.prefs.groupMedia;
        return new BrowserScreen(a, l);
    }

    static BrowserScreen trash(MainActivity a) {
        Loc l = new Loc();
        l.mode = M_TRASH;
        return new BrowserScreen(a, l);
    }

    static BrowserScreen search(MainActivity a) {
        Loc l = new Loc();
        l.mode = M_GLOBAL;
        return new BrowserScreen(a, l);
    }

    private static Loc dirLoc(File d) {
        Loc l = new Loc();
        l.mode = M_DIR;
        l.dir = d;
        return l;
    }

    BrowserScreen(MainActivity a, Loc first) {
        super(a);
        this.a = a;
        this.p = a.prefs;
        setBackgroundColor(Ui.SURFACE);
        ad = new FileAdapter(a);

        LinearLayout col = Ui.vbox(a);
        addView(col, new LayoutParams(Ui.MATCH, Ui.MATCH));

        FrameLayout top = new FrameLayout(a);
        top.setElevation(Ui.dp(2));
        col.addView(top, Ui.lp(Ui.MATCH, Ui.dp(60)));
        buildBar(top);
        buildSelBar(top);
        buildSearchBar(top);
        buildCrumbs(col);

        FrameLayout content = new FrameLayout(a);
        col.addView(content, Ui.lp(Ui.MATCH, 0, 1));
        buildContent(content);

        buildPasteBar(col);
        actionBar = Ui.hbox(a);
        actionBar.setBackgroundColor(Ui.SURFACE);
        actionBar.setElevation(Ui.dp(10));
        col.addView(actionBar, Ui.lp(Ui.MATCH, Ui.dp(66)));

        hist.add(first);
        if (first.mode == M_DIR) p.addRecent(first.dir.getAbsolutePath());
        ad.trash = first.mode == M_TRASH;
        applyViewMode();
        updateChrome();
        reload();
        if (first.mode == M_GLOBAL) openSearch();
    }

    private Loc cur() { return hist.get(hist.size() - 1); }

    // ------------------------------------------------------------ building the UI

    private void buildBar(FrameLayout top) {
        bar = Ui.appBar(a);
        bar.setElevation(0);
        leading = Ui.iconBtn(a, R.drawable.ic_back, Ui.TEXT, "חזרה", v -> {
            if (p.navIcon) a.openDrawer();
            else a.onBackPressed();
        });
        bar.addView(leading);
        LinearLayout tv = Ui.vbox(a);
        Ui.pad(tv, 6, 0, 4, 0);
        title = Ui.line(a, "", 19, Ui.TEXT, Ui.MEDIUM);
        subtitle = Ui.line(a, "", 12.5f, Ui.TEXT2, Ui.REGULAR);
        subtitle.setTextDirection(View.TEXT_DIRECTION_RTL);
        tv.addView(title);
        tv.addView(subtitle);
        bar.addView(tv, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.TEXT, "חיפוש", v -> openSearch()));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.TEXT, "אפשרויות נוספות", v -> moreSheet()));
        top.addView(bar, new LayoutParams(Ui.MATCH, Ui.MATCH));
    }

    private void buildSelBar(FrameLayout top) {
        selBar = Ui.hbox(a);
        Ui.pad(selBar, 4, 0, 4, 0);
        selBar.setBackgroundColor(Ui.SELECTED);
        selBar.addView(Ui.iconBtn(a, R.drawable.ic_close, Ui.TEXT, "ביטול בחירה", v -> exitSelection()));
        selTitle = Ui.line(a, "", 19, Ui.TEXT, Ui.MEDIUM);
        selTitle.setTextDirection(View.TEXT_DIRECTION_RTL);
        selTitle.setPaddingRelative(Ui.dp(6), 0, 0, 0);
        selBar.addView(selTitle, Ui.lp(0, Ui.WRAP, 1));
        selBar.addView(Ui.iconBtn(a, R.drawable.ic_select_all, Ui.TEXT, "בחר הכל", v -> selectAll()));
        selBar.addView(Ui.iconBtn(a, R.drawable.ic_select_range, Ui.TEXT, "בחר טווח", v -> selectRange()));
        top.addView(selBar, new LayoutParams(Ui.MATCH, Ui.MATCH));
    }

    private void buildSearchBar(FrameLayout top) {
        searchBar = Ui.appBar(a);
        searchBar.setElevation(0);
        searchBar.addView(Ui.iconBtn(a, R.drawable.ic_back, Ui.TEXT, "חזרה", v -> {
            if (cur().mode == M_GLOBAL) a.onBackPressed();
            else closeSearch();
        }));
        LinearLayout field = Ui.hbox(a);
        field.setBackground(Ui.round(Ui.BG, 24));
        Ui.pad(field, 16, 0, 2, 0);
        searchEt = new EditText(a);
        searchEt.setBackground(null);
        searchEt.setSingleLine(true);
        searchEt.setTextSize(16);
        searchEt.setTextColor(Ui.TEXT);
        searchEt.setHintTextColor(Ui.TEXT3);
        searchEt.setPadding(0, 0, 0, 0);
        searchEt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        searchEt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        field.addView(searchEt, Ui.lp(0, Ui.WRAP, 1));
        searchClear = Ui.iconBtn(a, R.drawable.ic_close, Ui.TEXT2, "נקה", v -> searchEt.setText(""));
        searchClear.setPadding(Ui.dp(11), Ui.dp(11), Ui.dp(11), Ui.dp(11));
        searchClear.setVisibility(INVISIBLE);
        field.addView(searchClear, Ui.lp(Ui.dp(42), Ui.dp(42)));
        searchBar.addView(field, Ui.margins(Ui.lp(0, Ui.dp(46), 1), 0, 0, 4, 0));
        searchGo = Ui.iconBtn(a, R.drawable.ic_search, Ui.PRIMARY, "חיפוש גם בתיקיות משנה", v -> deepSearch());
        searchBar.addView(searchGo);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int af) { }

            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }

            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString();
                searchClear.setVisibility(q.isEmpty() ? INVISIBLE : VISIBLE);
                if (mute) return;
                if (cur().mode == M_GLOBAL) {
                    MainActivity.H.removeCallbacks(globalSearch);
                    MainActivity.H.postDelayed(globalSearch, 220);
                } else {
                    filter = q.trim().toLowerCase(Locale.ROOT);
                    applyFilter();
                }
            }
        });
        searchEt.setOnEditorActionListener((v, id, e) -> {
            if (cur().mode == M_DIR) deepSearch();
            else a.hideKeyboard();
            return true;
        });
        top.addView(searchBar, new LayoutParams(Ui.MATCH, Ui.MATCH));
    }

    private void buildCrumbs(LinearLayout col) {
        LinearLayout row = Ui.hbox(a);
        row.setBackgroundColor(Ui.SURFACE);
        crumbScroll = new HorizontalScrollView(a);
        crumbScroll.setHorizontalScrollBarEnabled(false);
        crumbs = Ui.hbox(a);
        Ui.pad(crumbs, 8, 0, 8, 0);
        crumbScroll.addView(crumbs, new LayoutParams(Ui.WRAP, Ui.MATCH));
        row.addView(crumbScroll, Ui.lp(0, Ui.MATCH, 1));
        usageChip = Ui.hbox(a);
        usageChip.setBackground(Ui.ripple(Ui.stroke(Ui.SURFACE, Ui.DIVIDER, 16), 16));
        Ui.pad(usageChip, 10, 0, 12, 0);
        usagePie = new Ui.Pie(a);
        usageChip.addView(usagePie, Ui.lp(Ui.dp(16), Ui.dp(16)));
        usageText = Ui.text(a, "", 12.5f, Ui.TEXT, Ui.MEDIUM);
        usageText.setTextDirection(View.TEXT_DIRECTION_RTL);
        usageText.setPaddingRelative(Ui.dp(7), 0, 0, 0);
        usageChip.addView(usageText);
        row.addView(usageChip, Ui.margins(Ui.lp(Ui.WRAP, Ui.dp(32)), 4, 0, 12, 0));
        LinearLayout wrap = Ui.vbox(a);
        wrap.addView(row, Ui.lp(Ui.MATCH, Ui.dp(52)));
        wrap.addView(Ui.divider(a));
        crumbRow = wrap;
        col.addView(wrap);
    }

    private void buildContent(FrameLayout content) {
        list = new ListView(a);
        list.setDivider(null);
        list.setSelector(new ColorDrawable(0));
        list.setPadding(0, 0, 0, 0);
        list.setFastScrollEnabled(true);
        list.setAdapter(ad);
        content.addView(list, new LayoutParams(Ui.MATCH, Ui.MATCH));

        grid = new GridView(a);
        grid.setNumColumns(GridView.AUTO_FIT);
        grid.setColumnWidth(Ui.dp(108));
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setVerticalSpacing(Ui.dp(2));
        grid.setSelector(new ColorDrawable(0));
        grid.setClipToPadding(false);
        grid.setPadding(Ui.dp(6), Ui.dp(6), Ui.dp(6), Ui.dp(6));
        grid.setFastScrollEnabled(true);
        grid.setAdapter(ad);
        content.addView(grid, new LayoutParams(Ui.MATCH, Ui.MATCH));

        for (AbsListView v : new AbsListView[]{list, grid}) {
            v.setOnItemClickListener((parent, view, pos, id) -> onClick(pos));
            v.setOnItemLongClickListener((parent, view, pos, id) -> {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                onLongClick(pos);
                return true;
            });
            v.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override public void onScrollStateChanged(AbsListView view, int state) {
                    if (state != SCROLL_STATE_IDLE) a.hideKeyboard();
                }

                @Override public void onScroll(AbsListView view, int f, int c, int t) { }
            });
        }

        emptyView = Ui.vbox(a);
        emptyView.setGravity(Gravity.CENTER);
        Ui.pad(emptyView, 32, 0, 32, 80);
        ImageView art = new ImageView(a);
        art.setImageResource(R.drawable.ic_folder_big);
        art.setAlpha(0.55f);
        emptyView.addView(art, Ui.lp(Ui.dp(96), Ui.dp(96)));
        emptyText = Ui.text(a, "", 15, Ui.TEXT2);
        emptyText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyView.addView(emptyText, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 12, 0, 0));
        emptyView.setVisibility(GONE);
        content.addView(emptyView, new LayoutParams(Ui.MATCH, Ui.MATCH));

        progress = new ProgressBar(a);
        progress.setIndeterminateTintList(Ui.csl(Ui.PRIMARY));
        progress.setVisibility(GONE);
        content.addView(progress, new LayoutParams(Ui.dp(40), Ui.dp(40), Gravity.CENTER));
    }

    private void buildPasteBar(LinearLayout col) {
        pasteBar = Ui.hbox(a);
        pasteBar.setBackground(Ui.round(Ui.DARK, 18));
        Ui.pad(pasteBar, 16, 10, 10, 10);
        pasteBar.setElevation(Ui.dp(6));
        pasteBar.addView(Ui.img(a, R.drawable.ic_paste, 0xFFFFFFFF), Ui.lp(Ui.dp(22), Ui.dp(22)));
        LinearLayout t = Ui.vbox(a);
        t.setPaddingRelative(Ui.dp(14), 0, Ui.dp(6), 0);
        pasteTitle = Ui.line(a, "", 14.5f, 0xFFFFFFFF, Ui.MEDIUM);
        pasteTitle.setTextDirection(View.TEXT_DIRECTION_RTL);
        pasteSub = Ui.line(a, "", 12, 0xFF94A3B8, Ui.REGULAR);
        t.addView(pasteTitle);
        t.addView(pasteSub);
        pasteBar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        pasteBar.addView(Ui.button(a, "ביטול", false, 0xFFCBD5E1, v -> {
            a.clearClip();
            updateChrome();
        }));
        pasteBar.addView(Ui.button(a, "הדבק כאן", true, v -> paste()));
        col.addView(pasteBar, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 12, 0, 12, 12));
    }

    private void buildActions() {
        actionBar.removeAllViews();
        if (cur().mode == M_TRASH) {
            action(R.drawable.ic_restore, "שחזר", true, () -> restore(selected()));
            action(R.drawable.ic_delete, "מחק לצמיתות", true, () -> deleteForever(selected()));
        } else {
            action(R.drawable.ic_copy, "העתק", true, () -> toClip(selected(), false));
            action(R.drawable.ic_cut, "העבר", true, () -> toClip(selected(), true));
            action(R.drawable.ic_edit, "שנה שם", selCount == 1, () -> { if (selCount == 1) rename(selected().get(0)); });
            action(R.drawable.ic_delete, "מחק", true, () -> delete(selected()));
            action(R.drawable.ic_more, "עוד", true, this::selMore);
        }
    }

    private void action(int icon, String label, boolean enabled, Runnable r) {
        LinearLayout b = Ui.vbox(a);
        b.setGravity(Gravity.CENTER);
        b.setBackground(Ui.rippleBorderless());
        b.addView(Ui.img(a, icon, Ui.PRIMARY), Ui.lp(Ui.dp(24), Ui.dp(24)));
        TextView t = Ui.text(a, label, 12, Ui.TEXT, Ui.MEDIUM);
        t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        b.addView(t, Ui.margins(Ui.lp(Ui.WRAP, Ui.WRAP), 0, 4, 0, 0));
        b.setAlpha(enabled ? 1f : 0.35f);
        b.setOnClickListener(v -> r.run());
        actionBar.addView(b, Ui.lp(0, Ui.MATCH, 1));
    }

    // ------------------------------------------------------------ chrome state

    private boolean showSearch() { return searchOpen || cur().mode == M_GLOBAL; }

    private void updateChrome() {
        Loc l = cur();
        boolean dirMode = l.mode == M_DIR;
        bar.setVisibility(!selecting && !showSearch() ? VISIBLE : GONE);
        selBar.setVisibility(selecting ? VISIBLE : GONE);
        searchBar.setVisibility(!selecting && showSearch() ? VISIBLE : GONE);
        leading.setImageResource(p.navIcon ? R.drawable.ic_menu : R.drawable.ic_back);
        searchGo.setVisibility(dirMode ? VISIBLE : GONE);
        searchEt.setHint(l.mode == M_GLOBAL ? "חיפוש בכל הקבצים" : dirMode ? "סינון בתיקיה זו" : "סינון");
        boolean crumbsOn = l.mode == M_DIR || l.mode == M_CAT;
        crumbRow.setVisibility(crumbsOn ? VISIBLE : GONE);
        if (crumbsOn) buildCrumbs();
        title.setText(titleOf(l));
        actionBar.setVisibility(selecting ? VISIBLE : GONE);
        if (selecting) buildActions();
        boolean paste = dirMode && !selecting && !a.clip.isEmpty();
        pasteBar.setVisibility(paste ? VISIBLE : GONE);
        if (paste) {
            int n = a.clip.size();
            pasteTitle.setText((n == 1 ? a.clip.get(0).getName() : Fx.items(n)) + (a.clipCut ? " · להעברה" : " · להעתקה"));
            pasteSub.setText("נווטו לתיקיית היעד והקישו \"הדבק כאן\"");
        }
        a.setBars(selecting ? Ui.SELECTED : Ui.SURFACE, true);
    }

    private String titleOf(Loc l) {
        switch (l.mode) {
            case M_DIR: {
                Storage.Vol v = Storage.of(a, l.dir);
                if (v != null && v.root.equals(l.dir)) return v.label;
                return l.dir.getPath().equals("/") ? "אחסון מערכת" : l.dir.getName();
            }
            case M_CAT: return l.dir != null ? l.dir.getName() : Media.TITLES[l.cat];
            case M_DEEP: return "חיפוש: " + l.query;
            case M_TRASH: return "סל מיחזור";
            default: return "חיפוש";
        }
    }

    private View crumbChip(int icon, int color, String label, boolean last, Runnable click) {
        LinearLayout c = Ui.hbox(a);
        Ui.pad(c, 8, 0, 8, 0);
        c.setBackground(last ? Ui.round(Ui.PRIMARY_SOFT, 14) : Ui.ripple(0x00FFFFFF, 14));
        if (icon != 0) c.addView(Ui.img(a, icon, last ? Ui.PRIMARY : color), Ui.lp(Ui.dp(20), Ui.dp(20)));
        if (label != null) {
            TextView t = Ui.text(a, label, 13.5f, last ? Ui.PRIMARY : Ui.TEXT2, last ? Ui.MEDIUM : Ui.REGULAR);
            t.setSingleLine(true);
            t.setPaddingRelative(icon != 0 ? Ui.dp(6) : 0, 0, 0, 0);
            c.addView(t);
        }
        if (!last && click != null) c.setOnClickListener(v -> click.run());
        c.setLayoutParams(Ui.margins(Ui.lp(Ui.WRAP, Ui.dp(34)), 1, 0, 1, 0));
        return c;
    }

    private void sep() { crumbs.addView(Ui.img(a, R.drawable.ic_chevron, Ui.TEXT3), Ui.lp(Ui.dp(16), Ui.dp(16))); }

    private void buildCrumbs() {
        crumbs.removeAllViews();
        Loc l = cur();
        crumbs.addView(crumbChip(R.drawable.ic_home, 0xFFEF4444, null, false, a::goHome));
        if (l.mode == M_CAT) {
            sep();
            boolean last = l.dir == null;
            crumbs.addView(crumbChip(Media.ICON[l.cat], Media.COLOR[l.cat], Media.TITLES[l.cat], last, () -> {
                while (hist.size() > 1 && cur().dir != null) hist.remove(hist.size() - 1);
                updateChrome();
                reload();
            }));
            if (!last) {
                sep();
                crumbs.addView(crumbChip(0, 0, l.dir.getName(), true, null));
            }
            usageChip.setVisibility(GONE);
        } else {
            File dir = l.dir;
            Storage.Vol v = Storage.of(a, dir);
            File root = v != null ? v.root : new File("/");
            ArrayList<File> chain = new ArrayList<>();
            for (File f = dir; f != null; f = f.getParentFile()) {
                chain.add(0, f);
                if (f.equals(root)) break;
            }
            for (int i = 0; i < chain.size(); i++) {
                final File f = chain.get(i);
                boolean last = i == chain.size() - 1;
                sep();
                if (i == 0) {
                    int ic = v == null ? R.drawable.ic_system : v.primary ? R.drawable.ic_phone : R.drawable.ic_sd;
                    crumbs.addView(crumbChip(ic, Ui.TEXT2, chain.size() == 1 ? (v != null ? v.label : "/") : null, last, () -> go(dirLoc(f))));
                } else crumbs.addView(crumbChip(0, 0, f.getName(), last, () -> go(dirLoc(f))));
            }
            if (v != null && v.total > 0) {
                float frac = (float) (v.total - v.free) / v.total;
                usagePie.set(frac, frac > 0.9f ? Ui.DANGER : frac > 0.75f ? Ui.AMBER : Ui.PRIMARY);
                usageText.setText(Math.round(frac * 100) + "% בשימוש");
                usageChip.setVisibility(VISIBLE);
                usageChip.setOnClickListener(x -> a.push(new AnalysisScreen(a, v.root)));
            } else usageChip.setVisibility(GONE);
        }
        crumbScroll.post(() -> crumbScroll.fullScroll(View.FOCUS_LEFT)); // RTL: deepest crumb is on the left
    }

    private void updateSubtitle() {
        Loc l = cur();
        int n = ad.items.size();
        String s;
        switch (l.mode) {
            case M_DIR:
                s = n == 0 ? "תיקיה ריקה" : Fx.items(n);
                if (!filter.isEmpty()) s += " תואמים";
                break;
            case M_TRASH:
                s = n == 0 ? "ריק" : Fx.items(n) + " · נמחקים אוטומטית אחרי 30 יום";
                break;
            default:
                if (l.groups) {
                    int files = 0;
                    long tot = 0;
                    for (FileItem it : ad.items) { files += Math.max(0, it.count); tot += it.size; }
                    s = (n == 1 ? "תיקיה אחת" : Fx.num(n) + " תיקיות") + " · " + Fx.files(files) + " · " + Fx.size(tot);
                } else {
                    long tot = 0;
                    for (FileItem it : ad.items) tot += it.size;
                    s = (l.mode == M_DEEP && loading ? "מחפש… " : "") + Fx.files(n) + (tot > 0 ? " · " + Fx.size(tot) : "");
                }
        }
        subtitle.setText(s);
        if (selecting) selTitle.setText(selCount + "/" + ad.items.size());
    }

    private void applyViewMode() {
        AbsListView old = ad.grid ? grid : list;
        int pos = old.getFirstVisiblePosition();
        ad.grid = p.grid;
        list.setVisibility(p.grid ? GONE : VISIBLE);
        grid.setVisibility(p.grid ? VISIBLE : GONE);
        ad.notifyDataSetChanged();
        if (p.grid) grid.setSelection(pos);
        else list.setSelection(pos);
    }

    // ------------------------------------------------------------ loading

    private void go(Loc l) {
        saveScroll();
        hist.add(l);
        if (l.mode == M_DIR) p.addRecent(l.dir.getAbsolutePath());
        leaveTransient();
        ad.trash = l.mode == M_TRASH;
        updateChrome();
        reload();
    }

    private void leaveTransient() {
        if (selecting) exitSelection();
        if (searchOpen) {
            searchOpen = false;
            filter = "";
            mute = true;
            searchEt.setText("");
            mute = false;
            a.hideKeyboard();
        }
    }

    private void saveScroll() {
        Loc l = cur();
        AbsListView v = p.grid ? grid : list;
        l.pos = v.getFirstVisiblePosition();
        View c = v.getChildAt(0);
        l.top = c == null ? 0 : c.getTop() - v.getPaddingTop();
    }

    /** Reload keeping the current scroll position (after an operation or on return). */
    private void refresh() {
        saveScroll();
        reload();
    }

    private void reload() {
        final Loc l = cur();
        final int g = ++gen;
        final boolean hidden = p.hidden;
        final int sortBy = p.sortBy;
        final boolean asc = p.asc;
        loading = true;
        MainActivity.H.removeCallbacks(showProgress);
        MainActivity.H.postDelayed(showProgress, 200);
        if (l.mode == M_DEEP) { deep(l, g, hidden); return; }
        MainActivity.BG.execute(() -> {
            ArrayList<FileItem> items = new ArrayList<>();
            String err = null;
            try {
                switch (l.mode) {
                    case M_DIR: {
                        File[] fs = l.dir.listFiles();
                        if (fs == null) err = l.dir.exists() ? "אין גישה לתיקיה זו" : "התיקיה לא קיימת";
                        else for (File f : fs) {
                            if (!hidden && f.getName().startsWith(".")) continue;
                            items.add(new FileItem(f));
                        }
                        break;
                    }
                    case M_CAT:
                        if (l.dir != null) items = Media.categoryIn(a, l.cat, l.dir, hidden);
                        else if (l.groups) items = Media.groups(a, l.cat, hidden);
                        else items = Media.category(a, l.cat, hidden);
                        break;
                    case M_GLOBAL: if (!l.query.isEmpty()) items = Media.search(a, l.query, hidden); break;
                    case M_TRASH: items = Trash.items(a); break;
                }
                if (l.mode == M_DIR || l.customSort || (l.mode == M_CAT && l.groups && l.dir == null))
                    Collections.sort(items, Fx.comparator(sortBy, asc));
            } catch (Exception e) {
                err = e.getMessage();
            }
            final ArrayList<FileItem> fi = items;
            final String fe = err;
            MainActivity.H.post(() -> { if (g == gen) loaded(fi, fe); });
            if (l.mode == M_DIR) countChildren(fi, g, hidden);
        });
    }

    private void countChildren(ArrayList<FileItem> items, int g, boolean hidden) {
        int k = 0;
        for (FileItem it : items) {
            if (!it.dir) continue;
            if (g != gen) return;
            String[] n = it.file.list();
            int c = 0;
            if (n != null) {
                if (hidden) c = n.length;
                else for (String s : n) if (!s.startsWith(".")) c++;
            }
            it.count = c;
            if (++k % 48 == 0) MainActivity.H.post(() -> { if (g == gen) ad.notifyDataSetChanged(); });
        }
        if (k > 0) MainActivity.H.post(() -> { if (g == gen) ad.notifyDataSetChanged(); });
    }

    private void loaded(ArrayList<FileItem> items, String err) {
        loading = false;
        MainActivity.H.removeCallbacks(showProgress);
        progress.setVisibility(GONE);
        all.clear();
        all.addAll(items);
        selCount = 0;
        if (selecting) {
            selecting = false;
            ad.selecting = false;
            updateChrome();
        }
        emptyText.setText(err != null ? err : emptyMessage());
        applyFilter();
        restoreScroll();
    }

    private String emptyMessage() {
        Loc l = cur();
        switch (l.mode) {
            case M_DIR: return filter.isEmpty() ? "התיקיה ריקה" : "לא נמצאו פריטים תואמים.\nהקישו על 🔍 לחיפוש גם בתיקיות המשנה";
            case M_TRASH: return "סל המיחזור ריק";
            case M_GLOBAL: return l.query.isEmpty() ? "הקלידו שם של קובץ או תיקיה" : "לא נמצאו תוצאות";
            case M_DEEP: return "לא נמצאו תוצאות";
            default: return "אין כאן קבצים";
        }
    }

    private void restoreScroll() {
        Loc l = cur();
        if (l.focus != null) {
            for (int i = 0; i < ad.items.size(); i++) {
                if (ad.items.get(i).name.equals(l.focus)) {
                    l.pos = i;
                    l.top = Ui.dp(120);
                    break;
                }
            }
            l.focus = null;
        }
        if (p.grid) grid.setSelection(l.pos);
        else list.setSelectionFromTop(l.pos, l.top);
    }

    private void applyFilter() {
        ArrayList<FileItem> shown;
        if (filter.isEmpty() || cur().mode == M_GLOBAL) shown = all;
        else {
            shown = new ArrayList<>();
            for (FileItem it : all) if (it.name.toLowerCase(Locale.ROOT).contains(filter)) shown.add(it);
        }
        ad.set(shown);
        if (!filter.isEmpty()) emptyText.setText(emptyMessage());
        emptyView.setVisibility(shown.isEmpty() && !loading ? VISIBLE : GONE);
        updateSubtitle();
    }

    /** Recursive search below the current folder; results stream in while searching. */
    private void deep(Loc l, int g, boolean hidden) {
        all.clear();
        ad.set(all);
        emptyView.setVisibility(GONE);
        updateSubtitle();
        final String q = l.query.toLowerCase(Locale.ROOT);
        MainActivity.BG.execute(() -> {
            ArrayDeque<File> st = new ArrayDeque<>();
            st.push(l.dir);
            ArrayList<FileItem> batch = new ArrayList<>();
            long last = SystemClock.uptimeMillis();
            while (!st.isEmpty()) {
                if (g != gen) return;
                File[] fs = st.pop().listFiles();
                if (fs == null) continue;
                for (File f : fs) {
                    String n = f.getName();
                    if (!hidden && n.startsWith(".")) continue;
                    if (n.toLowerCase(Locale.ROOT).contains(q)) {
                        FileItem it = new FileItem(f);
                        it.showParent = true;
                        batch.add(it);
                    }
                    if (f.isDirectory() && !Fx.isLink(f)) st.push(f);
                }
                if (!batch.isEmpty() && SystemClock.uptimeMillis() - last > 250) {
                    final ArrayList<FileItem> b = new ArrayList<>(batch);
                    batch.clear();
                    last = SystemClock.uptimeMillis();
                    MainActivity.H.post(() -> {
                        if (g != gen) return;
                        all.addAll(b);
                        applyFilter();
                    });
                }
            }
            final ArrayList<FileItem> b = new ArrayList<>(batch);
            MainActivity.H.post(() -> {
                if (g != gen) return;
                ArrayList<FileItem> res = new ArrayList<>(all);
                res.addAll(b);
                loaded(res, null);
            });
        });
    }

    // ------------------------------------------------------------ Screen

    private void back() {
        hist.remove(hist.size() - 1);
        Loc l = cur();
        ad.trash = l.mode == M_TRASH;
        if (l.mode == M_GLOBAL) {
            mute = true;
            searchEt.setText(l.query);
            mute = false;
        }
        updateChrome();
        reload();
    }

    @Override
    public boolean onBack() {
        if (selecting) { exitSelection(); return true; }
        if (searchOpen) { closeSearch(); return true; }
        if (hist.size() > 1) { back(); return true; }
        return false;
    }

    @Override
    public void onShow() {
        updateChrome();
        Loc l = cur();
        if (l.mode == M_DIR || l.mode == M_TRASH) refresh();
        else prune();
    }

    /** Drop rows whose files disappeared (cheap alternative to re-querying). */
    private void prune() {
        boolean changed = all.removeIf(it -> !it.file.exists());
        if (changed) applyFilter();
        else ad.notifyDataSetChanged();
    }

    // ------------------------------------------------------------ search

    private void openSearch() {
        if (cur().mode != M_GLOBAL) searchOpen = true;
        updateChrome();
        a.showKeyboard(searchEt);
    }

    private void closeSearch() {
        searchOpen = false;
        filter = "";
        mute = true;
        searchEt.setText("");
        mute = false;
        a.hideKeyboard();
        applyFilter();
        updateChrome();
    }

    private void deepSearch() {
        String q = searchEt.getText().toString().trim();
        if (q.isEmpty() || cur().mode != M_DIR) return;
        Loc n = new Loc();
        n.mode = M_DEEP;
        n.dir = cur().dir;
        n.query = q;
        go(n);
    }

    // ------------------------------------------------------------ clicks & selection

    private void onClick(int pos) {
        if (pos < 0 || pos >= ad.items.size()) return;
        FileItem it = ad.items.get(pos);
        if (selecting) { toggle(it); return; }
        a.hideKeyboard();
        Loc l = cur();
        if (l.mode == M_TRASH) { trashMenu(it); return; }
        if (it.cover != null) { // media folder group
            Loc n = new Loc();
            n.mode = M_CAT;
            n.cat = l.cat;
            n.dir = it.file;
            go(n);
            return;
        }
        if (it.dir) go(dirLoc(it.file));
        else openFile(it);
    }

    private void onLongClick(int pos) {
        if (pos < 0 || pos >= ad.items.size()) return;
        FileItem it = ad.items.get(pos);
        if (!selecting) {
            selecting = true;
            ad.selecting = true;
            a.hideKeyboard();
        }
        it.selected = !it.selected;
        selCount += it.selected ? 1 : -1;
        if (selCount <= 0) { exitSelection(); return; }
        ad.notifyDataSetChanged();
        updateChrome();
        updateSubtitle();
    }

    private void toggle(FileItem it) {
        it.selected = !it.selected;
        selCount += it.selected ? 1 : -1;
        if (selCount <= 0) { exitSelection(); return; }
        ad.notifyDataSetChanged();
        buildActions();
        updateSubtitle();
    }

    private void exitSelection() {
        selecting = false;
        ad.selecting = false;
        for (FileItem it : all) it.selected = false;
        selCount = 0;
        ad.notifyDataSetChanged();
        updateChrome();
    }

    private void selectAll() {
        if (selCount == ad.items.size()) { exitSelection(); return; }
        selecting = true;
        ad.selecting = true;
        for (FileItem it : ad.items) it.selected = true;
        selCount = ad.items.size();
        ad.notifyDataSetChanged();
        updateChrome();
        updateSubtitle();
    }

    /** Select everything between the first and the last selected item. */
    private void selectRange() {
        int lo = -1, hi = -1;
        for (int i = 0; i < ad.items.size(); i++) {
            if (ad.items.get(i).selected) {
                if (lo < 0) lo = i;
                hi = i;
            }
        }
        if (lo < 0 || lo == hi) { a.toast("סמנו פריט ראשון ואחרון כדי לבחור את כל מה שביניהם"); return; }
        for (int i = lo; i <= hi; i++) ad.items.get(i).selected = true;
        selCount = 0;
        for (FileItem it : ad.items) if (it.selected) selCount++;
        ad.notifyDataSetChanged();
        buildActions();
        updateSubtitle();
    }

    private ArrayList<FileItem> selected() {
        ArrayList<FileItem> s = new ArrayList<>();
        for (FileItem it : all) if (it.selected) s.add(it);
        return s;
    }

    private static ArrayList<File> files(List<FileItem> items) {
        ArrayList<File> f = new ArrayList<>();
        for (FileItem it : items) f.add(it.file);
        return f;
    }

    private void openFile(FileItem it) {
        if ("zip".equals(Fx.ext(it.name))) {
            ArrayList<Dialogs.Item> m = new ArrayList<>();
            m.add(new Dialogs.Item(R.drawable.ic_archive, "חלץ לתיקיה כאן", () -> extract(it.file)));
            m.add(new Dialogs.Item(R.drawable.ic_open, "פתח באמצעות…", () -> Fx.open(a, it.file, true)));
            Dialogs.sheet(a, it.name, ad.sub(it), m);
            return;
        }
        Fx.open(a, it, ad.items);
    }

    // ------------------------------------------------------------ menus

    private void selMore() {
        ArrayList<FileItem> s = selected();
        if (s.isEmpty()) return;
        FileItem f0 = s.get(0);
        boolean one = s.size() == 1;
        boolean anyFile = false;
        for (FileItem it : s) if (!it.dir) anyFile = true;
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        if (anyFile) m.add(new Dialogs.Item(R.drawable.ic_share, "שיתוף", () -> Fx.share(a, files(s))));
        if (one && "zip".equals(Fx.ext(f0.name))) m.add(new Dialogs.Item(R.drawable.ic_archive, "חלץ לתיקיה כאן", () -> extract(f0.file)));
        m.add(new Dialogs.Item(R.drawable.ic_archive, "דחוס", () -> compress(s)));
        if (one && !f0.dir) m.add(new Dialogs.Item(R.drawable.ic_open, "פתח באמצעות…", () -> Fx.open(a, f0.file, true)));
        if (one && !f0.dir && p.menuOpenAs) m.add(new Dialogs.Item(R.drawable.ic_file, "פתח כ…", () -> Fx.openAs(a, f0.file)));
        if (one && cur().mode != M_DIR) m.add(new Dialogs.Item(R.drawable.ic_folder, "פתח את המיקום", () -> openLocation(f0)));
        if (one && f0.dir && p.menuBookmark) m.add(bookmarkItem(f0.file));
        if (one && f0.dir && p.menuShortcut) m.add(new Dialogs.Item(R.drawable.ic_shortcut, "הוסף למסך הבית", () -> {
            exitSelection();
            a.addShortcut(f0.file);
        }));
        if (p.menuHide && cur().mode == M_DIR) {
            boolean hiddenAll = true;
            for (FileItem it : s) if (!it.name.startsWith(".")) hiddenAll = false;
            final boolean unhide = hiddenAll;
            m.add(new Dialogs.Item(unhide ? R.drawable.ic_eye : R.drawable.ic_hide, unhide ? "ביטול הסתרה" : "הסתרה", () -> hide(s, !unhide)));
        }
        if (one) m.add(new Dialogs.Item(R.drawable.ic_copy, "העתקת הנתיב", () -> {
            Fx.copyText(a, f0.file.getAbsolutePath());
            a.toast("הנתיב הועתק");
        }));
        m.add(new Dialogs.Item(R.drawable.ic_info, "פרטים", () -> Dialogs.details(a, files(s))));
        m.add(new Dialogs.Item(R.drawable.ic_settings, "הגדרות", () -> a.push(new SettingsScreen(a))));
        Dialogs.sheet(a, one ? f0.name : Fx.items(s.size()) + " נבחרו", one ? ad.sub(f0) : null, m);
    }

    private Dialogs.Item bookmarkItem(File f) {
        boolean on = p.isBookmarked(f.getPath());
        return new Dialogs.Item(R.drawable.ic_star, on ? "הסר סימניה" : "הוסף סימניה", () -> {
            p.setBookmarked(f.getPath(), !on);
            a.toast(on ? "הוסר מהמועדפים" : "נוסף למועדפים");
        });
    }

    private void moreSheet() {
        Loc l = cur();
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        if (l.mode == M_DIR) m.add(new Dialogs.Item(R.drawable.ic_add, "חדש", this::newSheet));
        m.add(new Dialogs.Item(R.drawable.ic_tune, "תצוגה", this::viewDialog));
        if (l.mode == M_DIR) m.add(new Dialogs.Item(R.drawable.ic_pie, "ניתוח", () -> a.push(new AnalysisScreen(a, l.dir))));
        if (l.mode == M_TRASH && !all.isEmpty())
            m.add(new Dialogs.Item(R.drawable.ic_delete, "ריקון סל המיחזור", this::emptyTrash).danger());
        if (!all.isEmpty() && l.mode != M_GLOBAL) m.add(new Dialogs.Item(R.drawable.ic_select_all, "בחירת הכל", this::selectAll));
        if (l.mode == M_DIR && p.menuBookmark) m.add(bookmarkItem(l.dir));
        if (l.mode == M_DIR && p.menuShortcut) m.add(new Dialogs.Item(R.drawable.ic_shortcut, "הוסף למסך הבית", () -> a.addShortcut(l.dir)));
        if (l.mode == M_DIR) m.add(new Dialogs.Item(R.drawable.ic_info, "פרטי התיקיה",
                () -> Dialogs.details(a, Collections.singletonList(l.dir))));
        m.add(new Dialogs.Item(R.drawable.ic_refresh, "רענון", this::refresh));
        m.add(new Dialogs.Item(R.drawable.ic_settings, "הגדרות", () -> a.push(new SettingsScreen(a))));
        Dialogs.sheet(a, null, null, m);
    }

    private void newSheet() {
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        m.add(new Dialogs.Item(R.drawable.ic_new_folder, "תיקיה חדשה", this::newFolder));
        m.add(new Dialogs.Item(R.drawable.ic_file, "קובץ טקסט חדש", this::newFile));
        Dialogs.sheet(a, "חדש", null, m);
    }

    /** View options: layout, sort order, hidden files, media grouping. */
    private void viewDialog() {
        Loc l = cur();
        LinearLayout box = Ui.vbox(a);
        Ui.pad(box, 0, 20, 0, 10);
        TextView t = Ui.text(a, "תצוגה", 18.5f, Ui.TEXT, Ui.MEDIUM);
        Ui.pad(t, 24, 0, 24, 8);
        box.addView(t);
        Dialog d = Dialogs.make(a, box, false);
        final int[] sort = {p.sortBy};
        final boolean[] asc = {p.asc}, gridOn = {p.grid};

        LinearLayout seg = Ui.hbox(a);
        seg.setBackground(Ui.round(Ui.BG, 14));
        Ui.pad(seg, 4, 4, 4, 4);
        TextView[] segs = new TextView[2];
        String[] segNames = {"רשימה", "רשת"};
        Runnable paintSeg = () -> {
            for (int i = 0; i < 2; i++) {
                boolean on = (i == 1) == gridOn[0];
                segs[i].setBackground(on ? Ui.round(Ui.SURFACE, 11) : null);
                segs[i].setTextColor(on ? Ui.PRIMARY : Ui.TEXT2);
                segs[i].setElevation(on ? Ui.dp(1) : 0);
            }
        };
        for (int i = 0; i < 2; i++) {
            final int k = i;
            segs[i] = Ui.text(a, segNames[i], 14.5f, Ui.TEXT2, Ui.MEDIUM);
            segs[i].setGravity(Gravity.CENTER);
            segs[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            segs[i].setCompoundDrawablePadding(Ui.dp(6));
            segs[i].setOnClickListener(v -> { gridOn[0] = k == 1; paintSeg.run(); });
            seg.addView(segs[i], Ui.lp(0, Ui.dp(38), 1));
        }
        paintSeg.run();
        box.addView(seg, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 24, 4, 24, 8));

        TextView sh = Ui.text(a, "מיון לפי", 13, Ui.TEXT2, Ui.MEDIUM);
        Ui.pad(sh, 24, 10, 24, 4);
        box.addView(sh);
        String[] names = {"שם", "תאריך שינוי", "גודל", "סוג"};
        ImageView[] checks = new ImageView[4];
        Runnable paintSort = () -> { for (int i = 0; i < 4; i++) checks[i].setVisibility(sort[0] == i ? VISIBLE : INVISIBLE); };
        for (int i = 0; i < 4; i++) {
            final int k = i;
            LinearLayout r = Ui.hbox(a);
            Ui.pad(r, 24, 0, 24, 0);
            r.setBackground(Ui.rippleRect());
            r.addView(Ui.text(a, names[i], 15.5f, Ui.TEXT), Ui.lp(0, Ui.WRAP, 1));
            checks[i] = Ui.img(a, R.drawable.ic_check, Ui.PRIMARY);
            r.addView(checks[i], Ui.lp(Ui.dp(22), Ui.dp(22)));
            r.setOnClickListener(v -> {
                if (sort[0] != k) asc[0] = k == Prefs.BY_NAME || k == Prefs.BY_TYPE;
                sort[0] = k;
                paintSort.run();
            });
            box.addView(r, Ui.lp(Ui.MATCH, Ui.dp(46)));
        }
        paintSort.run();

        LinearLayout dir = Ui.hbox(a);
        dir.setBackground(Ui.round(Ui.BG, 14));
        Ui.pad(dir, 4, 4, 4, 4);
        TextView[] dirs = new TextView[2];
        String[] dirNames = {"סדר עולה", "סדר יורד"};
        Runnable paintDir = () -> {
            for (int i = 0; i < 2; i++) {
                boolean on = (i == 0) == asc[0];
                dirs[i].setBackground(on ? Ui.round(Ui.SURFACE, 11) : null);
                dirs[i].setTextColor(on ? Ui.PRIMARY : Ui.TEXT2);
            }
        };
        for (int i = 0; i < 2; i++) {
            final int k = i;
            dirs[i] = Ui.text(a, dirNames[i], 14, Ui.TEXT2, Ui.MEDIUM);
            dirs[i].setGravity(Gravity.CENTER);
            dirs[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dirs[i].setOnClickListener(v -> { asc[0] = k == 0; paintDir.run(); });
            dir.addView(dirs[i], Ui.lp(0, Ui.dp(36), 1));
        }
        paintDir.run();
        box.addView(dir, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 24, 6, 24, 8));

        CheckBox hid = new CheckBox(a);
        hid.setChecked(p.hidden);
        box.addView(Ui.settingRow(a, "הצג קבצים נסתרים", null, hid, v -> hid.setChecked(!hid.isChecked())));
        CheckBox grp = null;
        if (l.mode == M_CAT && Media.groupable(l.cat)) {
            CheckBox g = new CheckBox(a);
            g.setChecked(p.groupMedia);
            box.addView(Ui.settingRow(a, "קבץ לפי תיקיות", null, g, v -> g.setChecked(!g.isChecked())));
            grp = g;
        }
        final CheckBox fgrp = grp;
        LinearLayout btns = Ui.hbox(a);
        btns.setGravity(Gravity.END);
        Ui.pad(btns, 16, 8, 16, 0);
        btns.addView(Ui.button(a, "ביטול", false, v -> d.dismiss()));
        btns.addView(Ui.button(a, "אישור", true, v -> {
            d.dismiss();
            boolean reloadNeeded = p.hidden != hid.isChecked();
            boolean sortChanged = p.sortBy != sort[0] || p.asc != asc[0];
            p.sortBy = sort[0];
            p.asc = asc[0];
            p.hidden = hid.isChecked();
            boolean gridChanged = p.grid != gridOn[0];
            p.grid = gridOn[0];
            if (fgrp != null && fgrp.isChecked() != p.groupMedia) {
                p.groupMedia = fgrp.isChecked();
                if (cur().dir == null) { cur().groups = p.groupMedia; reloadNeeded = true; }
            }
            p.save();
            if (gridChanged) applyViewMode();
            if (sortChanged) cur().customSort = true;
            if (reloadNeeded) refresh();
            else if (sortChanged) {
                Collections.sort(all, Fx.comparator(p.sortBy, p.asc));
                applyFilter();
                (p.grid ? grid : list).setSelection(0);
            }
        }));
        box.addView(btns);
        Dialogs.show(d, false);
    }

    private void trashMenu(FileItem it) {
        List<FileItem> one = Collections.singletonList(it);
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        m.add(new Dialogs.Item(R.drawable.ic_restore, "שחזור", () -> restore(one)));
        m.add(new Dialogs.Item(R.drawable.ic_info, "פרטים", () -> Dialogs.details(a, files(one))));
        m.add(new Dialogs.Item(R.drawable.ic_delete, "מחיקה לצמיתות", () -> deleteForever(one)).danger());
        Dialogs.sheet(a, it.name, it.origPath == null ? null : "מקור: " + Storage.pretty(a, new File(it.origPath)), m);
    }

    // ------------------------------------------------------------ operations

    private void finishOp(Ops.Op op, Exception err, String ok, String focus) {
        if (err != null) a.toast(err.getMessage() == null ? "הפעולה נכשלה" : err.getMessage());
        else if (op.cancel) a.toast("הפעולה בוטלה");
        else if (ok != null) a.toast(ok);
        if (selecting) exitSelection();
        Loc l = cur();
        if (l.mode == M_DIR || l.mode == M_TRASH) {
            if (focus != null && err == null && !op.cancel) l.focus = focus;
            refresh();
        } else prune();
        updateChrome();
    }

    private void toClip(List<FileItem> s, boolean cut) {
        if (s.isEmpty()) return;
        a.setClip(files(s), cut);
        if (selecting) exitSelection();
        updateChrome();
        if (cur().mode != M_DIR) a.toast((cut ? "מוכן להעברה" : "מוכן להעתקה") + " — פתחו תיקיה והקישו \"הדבק כאן\"");
    }

    private void paste() {
        final File dest = cur().dir;
        final ArrayList<File> src = new ArrayList<>(a.clip);
        final boolean cut = a.clipCut;
        String dp = dest.getAbsolutePath();
        int conflicts = 0;
        boolean allHere = true;
        for (File f : src) {
            String fp = f.getAbsolutePath();
            if (f.isDirectory() && (dp.equals(fp) || dp.startsWith(fp + "/"))) {
                a.toast("לא ניתן להעתיק תיקיה לתוך עצמה");
                return;
            }
            boolean here = dest.equals(f.getParentFile());
            if (!here) allHere = false;
            if (!here && new File(dest, f.getName()).exists()) conflicts++;
        }
        if (cut && allHere) {
            a.clearClip();
            updateChrome();
            a.toast("הפריטים כבר נמצאים בתיקיה זו");
            return;
        }
        if (conflicts == 0) { doPaste(dest, src, cut, P_KEEP); return; }
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        m.add(new Dialogs.Item(R.drawable.ic_copy, "שמירת שניהם (שם חדש)", () -> doPaste(dest, src, cut, P_KEEP)));
        m.add(new Dialogs.Item(R.drawable.ic_paste, "החלפת הקיימים", () -> doPaste(dest, src, cut, P_REPLACE)));
        m.add(new Dialogs.Item(R.drawable.ic_close, "דילוג על הקיימים", () -> doPaste(dest, src, cut, P_SKIP)));
        Dialogs.sheet(a, conflicts == 1 ? "פריט בשם זה כבר קיים" : conflicts + " פריטים בשם זהה כבר קיימים", "מה לעשות?", m);
    }

    private void doPaste(File dest, List<File> src, boolean cut, int policy) {
        final String[] first = {null};
        Ops.run(a, cut ? "מעביר…" : "מעתיק…", op -> {
            ArrayList<File[]> pending = new ArrayList<>();
            for (File f : src) {
                op.check();
                File d = new File(dest, f.getName());
                if (d.getAbsolutePath().equals(f.getAbsolutePath())) {
                    if (cut) continue;
                    d = Fx.unique(dest, f.getName()); // copy inside the same folder → "name (1)"
                } else if (d.exists()) {
                    if (policy == P_SKIP) continue;
                    if (policy == P_KEEP) d = Fx.unique(dest, f.getName());
                    else Fx.deleteTree(d, op);
                }
                if (first[0] == null) first[0] = d.getName();
                if (cut && f.renameTo(d)) { // same volume: instant
                    op.scan(f);
                    op.scan(d);
                    continue;
                }
                pending.add(new File[]{f, d});
            }
            long t = 0;
            for (File[] pr : pending) t += Fx.sizeTree(pr[0], op);
            op.total = t;
            for (File[] pr : pending) {
                Fx.copyTree(pr[0], pr[1], op);
                if (cut) Fx.deleteTree(pr[0], op);
            }
        }, (op, err) -> {
            a.clearClip();
            finishOp(op, err, (cut ? "הועברו " : "הועתקו ") + Fx.items(src.size()), first[0]);
        });
    }

    private void delete(List<FileItem> s) {
        if (s.isEmpty()) return;
        String what = s.size() == 1 ? "את \"" + s.get(0).name + "\"" : Fx.items(s.size());
        if (p.trashDefault) {
            if (!p.confirmDelete) { runDelete(s, false); return; }
            Dialogs.confirm(a, "למחוק " + what + "?", "הפריטים יועברו לסל המיחזור ויישמרו בו 30 יום.", "מחק", true,
                    "מחיקה לצמיתות (ללא סל מיחזור)", permanent -> runDelete(s, permanent));
        } else {
            Dialogs.confirm(a, "למחוק " + what + "?", "לא ניתן יהיה לשחזר את הפריטים.", "מחק", true,
                    "העבר לסל המיחזור במקום", toTrash -> runDelete(s, !toTrash));
        }
    }

    private void runDelete(List<FileItem> s, boolean permanent) {
        Ops.run(a, "מוחק…", op -> {
            for (FileItem it : s) {
                op.check();
                op.cur = it.name;
                if (permanent) Fx.deleteTree(it.file, op);
                else {
                    Trash.put(a, it.file);
                    op.scan(it.file);
                }
                Thumbs.forget(it.file);
            }
        }, (op, err) -> finishOp(op, err, permanent ? "נמחק לצמיתות" : "הועבר לסל המיחזור", null));
    }

    private void hide(List<FileItem> s, boolean hide) {
        int fail = 0;
        for (FileItem it : s) {
            String n = hide ? "." + it.name : it.name.replaceFirst("^\\.+", "");
            if (n.isEmpty() || n.equals(it.name)) continue;
            File dst = new File(it.file.getParentFile(), n);
            if (dst.exists() || !it.file.renameTo(dst)) fail++;
        }
        if (fail > 0) a.toast("לא ניתן היה לשנות " + fail + " פריטים");
        else a.toast(hide ? (p.hidden ? "הוסתר" : "הוסתר — הפעילו \"הצג קבצים נסתרים\" כדי לראות") : "ההסתרה בוטלה");
        exitSelection();
        refresh();
    }

    private void restore(List<FileItem> s) {
        if (s.isEmpty()) return;
        Ops.run(a, "משחזר…", op -> {
            for (FileItem it : s) {
                op.check();
                op.cur = it.name;
                op.scan(Trash.restore(a, it));
            }
        }, (op, err) -> finishOp(op, err, s.size() == 1 ? "שוחזר למיקום המקורי" : "שוחזרו " + Fx.items(s.size()), null));
    }

    private void deleteForever(List<FileItem> s) {
        if (s.isEmpty()) return;
        Dialogs.confirm(a, "למחוק לצמיתות?", (s.size() == 1 ? "\"" + s.get(0).name + "\"" : Fx.items(s.size()))
                + " יימחקו ולא יהיה ניתן לשחזר אותם.", "מחק", true, null, c -> Ops.run(a, "מוחק…", op -> {
            for (FileItem it : s) Trash.deleteForever(a, it, op);
        }, (op, err) -> finishOp(op, err, "נמחק לצמיתות", null)));
    }

    private void emptyTrash() {
        final ArrayList<FileItem> items = new ArrayList<>(all);
        Dialogs.confirm(a, "לרוקן את סל המיחזור?", Fx.items(items.size()) + " יימחקו לצמיתות.", "רוקן", true, null,
                c -> Ops.run(a, "מרוקן…", op -> {
                    for (FileItem it : items) Trash.deleteForever(a, it, op);
                }, (op, err) -> finishOp(op, err, "סל המיחזור רוקן", null)));
    }

    private void rename(FileItem it) {
        Dialogs.input(a, "שינוי שם", it.name, "שמור", !it.dir, name -> {
            if (name.equals(it.name)) return;
            File parent = it.file.getParentFile();
            File dst = new File(parent, name);
            boolean caseOnly = name.equalsIgnoreCase(it.name);
            if (!caseOnly && dst.exists()) { a.toast("כבר קיים פריט בשם זה"); return; }
            boolean ok;
            if (caseOnly) {
                File tmp = new File(parent, ".rename_" + System.nanoTime());
                ok = it.file.renameTo(tmp) && tmp.renameTo(dst);
            } else ok = it.file.renameTo(dst);
            if (!ok) { a.toast("שינוי השם נכשל"); return; }
            Thumbs.forget(it.file);
            if (it.dir && p.isBookmarked(it.file.getPath())) {
                p.setBookmarked(it.file.getPath(), false);
                p.setBookmarked(dst.getPath(), true);
            }
            android.media.MediaScannerConnection.scanFile(a.getApplicationContext(),
                    new String[]{it.file.getAbsolutePath(), dst.getAbsolutePath()}, null, null);
            if (selecting) exitSelection();
            if (cur().mode == M_DIR) {
                cur().focus = name;
                refresh();
            } else {
                int i = all.indexOf(it);
                FileItem n = new FileItem(dst);
                n.showParent = it.showParent;
                if (i >= 0) all.set(i, n);
                applyFilter();
            }
        });
    }

    private void compress(List<FileItem> s) {
        if (s.isEmpty()) return;
        File parent = s.get(0).file.getParentFile();
        if (parent == null) return;
        String def = (s.size() == 1 ? Fx.base(s.get(0).name) : parent.getName()) + ".zip";
        Dialogs.input(a, "דחיסה לקובץ ZIP", def, "דחוס", true, name -> {
            String n = name.toLowerCase(Locale.ROOT).endsWith(".zip") ? name : name + ".zip";
            File out = new File(parent, n);
            if (out.exists()) { a.toast("כבר קיים קובץ בשם זה"); return; }
            List<File> fs = files(s);
            Ops.run(a, "דוחס…", op -> Fx.zip(fs, out, op), (op, err) -> finishOp(op, err, "נוצר " + out.getName(), out.getName()));
        });
    }

    private void extract(File zip) {
        File parent = zip.getParentFile();
        if (parent == null) return;
        File dest = Fx.unique(parent, Fx.base(zip.getName()));
        Ops.run(a, "מחלץ…", op -> Fx.unzip(zip, dest, op), (op, err) -> finishOp(op, err, "חולץ אל \"" + dest.getName() + "\"", dest.getName()));
    }

    private void openLocation(FileItem it) {
        File par = it.file.getParentFile();
        if (par == null) return;
        Loc l = dirLoc(par);
        l.focus = it.file.getName();
        go(l);
    }

    private void newFolder() {
        Dialogs.input(a, "תיקיה חדשה", "תיקיה חדשה", "צור", false, name -> {
            File d = new File(cur().dir, name);
            if (d.exists()) a.toast("כבר קיים פריט בשם זה");
            else if (d.mkdir()) {
                cur().focus = name;
                refresh();
            } else a.toast("לא ניתן ליצור את התיקיה כאן");
        });
    }

    private void newFile() {
        Dialogs.input(a, "קובץ חדש", "קובץ חדש.txt", "צור", true, name -> {
            File f = new File(cur().dir, name);
            boolean ok = false;
            try { ok = !f.exists() && f.createNewFile(); } catch (Exception ignored) { }
            if (!ok) { a.toast(f.exists() ? "כבר קיים פריט בשם זה" : "לא ניתן ליצור את הקובץ כאן"); return; }
            cur().focus = name;
            refresh();
        });
    }
}
