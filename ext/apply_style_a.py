import os
os.chdir(r'C:\orech_build\ext\fmx\app\src\main\java\com\custom\files')

def edit(fn, pairs):
    s = open(fn, encoding='utf-8', newline='').read()
    crlf = '\r\n' in s
    if crlf: s = s.replace('\r\n', '\n')
    for old, new in pairs:
        assert old in s, (fn, old[:60])
        s = s.replace(old, new, 1)
    if crlf: s = s.replace('\n', '\r\n')
    open(fn, 'w', encoding='utf-8', newline='').write(s)

# tokens: warm paper, ink, steel blue; flat bar
edit('Ui.java', [
 ('''    static final int BG = 0xFFF4F6FA, SURFACE = 0xFFFFFFFF, PRIMARY = 0xFFD97706, PRIMARY_SOFT = 0xFFFFF3DA,
            TEXT = 0xFF0F172A, TEXT2 = 0xFF64748B, TEXT3 = 0xFF94A3B8, DIVIDER = 0xFFE7EBF1,
            DANGER = 0xFFDC2626, SELECTED = 0xFFFFEBC2, AMBER = 0xFFF59E0B, DARK = 0xFF1E293B;''',
  '''    static final int BG = 0xFFF3EFE6, SURFACE = 0xFFFBF8F1, PRIMARY = 0xFF2F6F9F, PRIMARY_SOFT = 0xFFDDE9F2,
            TEXT = 0xFF1D2327, TEXT2 = 0xFF6B665C, TEXT3 = 0xFF9A958A, DIVIDER = 0xFFD9D3C4,
            DANGER = 0xFFB3472F, SELECTED = 0xFFD3E3EE, AMBER = 0xFFC98A1B, DARK = 0xFF26343B;'''),
 ('csl(0x22D97706)', 'csl(0x222F6F9F)'),
 ('csl(0x1CD97706)', 'csl(0x1C2F6F9F)'),
 ('        bar.setBackgroundColor(SURFACE);\n        bar.setElevation(dp(2));', '        bar.setBackgroundColor(SURFACE);\n        bar.setElevation(0);'),
 ('''        float frac;
        int color = PRIMARY;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Bar(Context c) { super(c); }''',
  '''        float frac;
        int color = PRIMARY;
        boolean flat;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Bar(Context c) { super(c); }'''),
 ('            float h = getHeight(), w = getWidth(), rad = h / 2f;', '            float h = getHeight(), w = getWidth(), rad = flat ? 0 : h / 2f;'),
])
edit('FileAdapter.java', [('0xE6D97706', '0xE62F6F9F')])
edit('Media.java', [
 ('{0xFF8B5CF6, 0xFFEF4444, 0xFF0D9488, 0xFF2563EB, 0xFF22A35A, 0xFFB7791F, 0xFF475569, 0xFFDB2777}',
  '{0xFF9A5B2E, 0xFFB3472F, 0xFF2E7D5B, 0xFF2F6F9F, 0xFF4C7F3A, 0xFF8A6A1F, 0xFF6B665C, 0xFF7A4B63}'),
 ('{0xFFF1EBFE, 0xFFFDECEC, 0xFFDDF5F1, 0xFFE8EFFD, 0xFFE3F5EA, 0xFFF8EEDA, 0xFFEDF1F6, 0xFFFCE7F3}',
  '{0xFFF0E3D6, 0xFFF3E1DA, 0xFFDFEDE5, 0xFFDDE9F2, 0xFFE4EEDC, 0xFFF0E7D0, 0xFFE9E5DB, 0xFFEBDDE4}'),
])

edit('HomeScreen.java', [
 ('/** Start screen: a grid of big tiles — storages, Downloads, categories and tools (like the original, but light). */',
  '/** Start screen: a flat list in three sections (storage, categories, tools) with a colour stripe per row. */'),
 ('''        int icon, color, soft;
        String title, sub = " ";''', '''        int icon, color, soft;
        String title, sub = " ", section = "";'''),
 ('''        LinearLayout bar = Ui.appBar(a);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_menu, Ui.TEXT, "תפריט", v -> a.openDrawer()));
        TextView t = Ui.line(a, Flavor.NAME, 20, Ui.TEXT, Ui.MEDIUM);
        t.setPaddingRelative(Ui.dp(8), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.TEXT, "חיפוש", v -> a.push(BrowserScreen.search(a))));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.TEXT, "אפשרויות", v -> menu()));''',
  '''        LinearLayout bar = Ui.appBar(a);
        bar.setBackgroundColor(Ui.DARK);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_menu, Ui.BG, "תפריט", v -> a.openDrawer()));
        TextView t = Ui.line(a, Flavor.NAME, 20, Ui.BG, Ui.BOLD);
        t.setPaddingRelative(Ui.dp(8), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_search, Ui.BG, "חיפוש", v -> a.push(BrowserScreen.search(a))));
        bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.BG, "אפשרויות", v -> menu()));'''),
 ('        Ui.pad(col, 10, 10, 10, 10);', '        Ui.pad(col, 0, 0, 0, 16);'),
 ('''        TextView fh = Ui.text(a, "מועדפים", 15, Ui.TEXT, Ui.MEDIUM);
        favSection.addView(fh, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 10, 18, 10, 10));
        favBox = Ui.vbox(a);
        favBox.setBackground(Ui.round(Ui.SURFACE, 20));
        favBox.setClipToOutline(true);
        favSection.addView(favBox, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 6, 0, 6, 0));''',
  '''        favSection.addView(sectionHeader("מועדפים"), Ui.lp(Ui.MATCH, Ui.WRAP));
        favBox = Ui.vbox(a);
        favSection.addView(favBox, Ui.lp(Ui.MATCH, Ui.WRAP));'''),
 ('        a.setBars(Ui.SURFACE, true);', '        a.setBars(Ui.DARK, false);'),
])

s = open('HomeScreen.java', encoding='utf-8', newline='').read()
crlf = '\r\n' in s
if crlf: s = s.replace('\r\n', '\n')
a0 = s.index('    private View tileView(Tile t) {')
a1 = s.index('    private Tile catTile(int cat, long[] st) {')
new_block = '''    private TextView sectionHeader(String title) {
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

'''
s = s[:a0] + new_block + s[a1:]

def rep(old, new):
    global s
    assert old in s, old[:70]
    s = s.replace(old, new, 1)

rep('        t.title = Media.TITLES[cat];\n', '        t.title = Media.TITLES[cat];\n        t.section = "קטגוריות";\n')
rep('''            t.color = v.primary ? 0xFF475569 : 0xFF4F46E5;
            t.soft = v.primary ? 0xFFEDF1F6 : 0xFFEEF0FE;
            t.title = v.label;''', '''            t.color = v.primary ? 0xFF2F6F9F : 0xFFB3472F;
            t.soft = v.primary ? 0xFFDDE9F2 : 0xFFF3E1DA;
            t.title = v.label;
            t.section = "אחסון";''')
rep('''            t.color = 0xFF334155;
            t.soft = 0xFFEDF1F6;
            t.title = "אחסון מערכת";''', '''            t.color = 0xFF4A5A63;
            t.soft = 0xFFE4E7E8;
            t.title = "אחסון מערכת";
            t.section = "אחסון";''')
rep('''        d.color = 0xFF0EA5E9;
        d.soft = 0xFFE0F2FB;
        d.title = "הורדות";''', '''        d.color = 0xFF2F6F9F;
        d.soft = 0xFFDDE9F2;
        d.title = "הורדות";
        d.section = "קטגוריות";''')
rep('''        tr.color = 0xFF64748B;
        tr.soft = 0xFFEDF1F6;
        tr.title = "סל מיחזור";''', '''        tr.color = 0xFF6B665C;
        tr.soft = 0xFFE9E5DB;
        tr.title = "סל מיחזור";
        tr.section = "כלים";''')
rep('''        an.color = 0xFFDB2777;
        an.soft = 0xFFFCE7F3;
        an.title = "ניתוח אחסון";''', '''        an.color = 0xFF7A4B63;
        an.soft = 0xFFEBDDE4;
        an.title = "ניתוח אחסון";
        an.section = "כלים";''')
rep('''            if (i > 0) favBox.addView(Ui.divider(a), Ui.margins(Ui.lp(Ui.MATCH, Math.max(1, Ui.dp(1))), 68, 0, 0, 0));''',
    '''            if (i > 0) favBox.addView(Ui.divider(a));''')
rep('''            FrameLayout tile = new FrameLayout(a);
            tile.setBackground(Ui.round(0xFFFEF3DC, 12));
            tile.addView(Ui.img(a, R.drawable.ic_star, Ui.AMBER), new FrameLayout.LayoutParams(Ui.dp(22), Ui.dp(22), Gravity.CENTER));
            r.addView(tile, Ui.lp(Ui.dp(42), Ui.dp(42)));''', '''            r.addView(Ui.img(a, R.drawable.ic_star, Ui.AMBER), Ui.lp(Ui.dp(24), Ui.dp(24)));''')
if crlf: s = s.replace('\n', '\r\n')
open('HomeScreen.java', 'w', encoding='utf-8', newline='').write(s)
print('edited')
