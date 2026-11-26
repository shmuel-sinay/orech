package com.custom.files;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

/** Settings, grouped like the original app. */
final class SettingsScreen extends LinearLayout implements MainActivity.Screen {
    private final MainActivity a;
    private final Prefs p;
    private final LinearLayout body;

    private interface Getter { boolean get(); }

    private interface Setter { void set(boolean v); }

    SettingsScreen(MainActivity a) {
        super(a);
        this.a = a;
        this.p = a.prefs;
        setOrientation(VERTICAL);
        setBackgroundColor(Ui.BG);
        LinearLayout bar = Ui.appBar(a);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_back, Ui.TEXT, "חזרה", v -> a.onBackPressed()));
        TextView t = Ui.line(a, "הגדרות", 20, Ui.TEXT, Ui.MEDIUM);
        t.setPaddingRelative(Ui.dp(6), 0, 0, 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        addView(bar, Ui.lp(Ui.MATCH, Ui.dp(60)));
        ScrollView sv = new ScrollView(a);
        body = Ui.vbox(a);
        Ui.pad(body, 12, 4, 12, 12);
        sv.addView(body);
        addView(sv, Ui.lp(Ui.MATCH, 0, 1));
        build();
    }

    @Override public boolean onBack() { return false; }

    @Override public void onShow() { a.setBars(Ui.SURFACE, true); }

    private LinearLayout section(String title) {
        TextView h = Ui.text(a, title, 13.5f, Ui.PRIMARY, Ui.MEDIUM);
        body.addView(h, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 10, 20, 10, 8));
        LinearLayout c = Ui.vbox(a);
        c.setBackground(Ui.round(Ui.SURFACE, 18));
        c.setClipToOutline(true);
        body.addView(c);
        return c;
    }

    private void add(LinearLayout card, View row) {
        if (card.getChildCount() > 0) card.addView(Ui.divider(a), Ui.margins(Ui.lp(Ui.MATCH, Math.max(1, Ui.dp(1))), 20, 0, 20, 0));
        card.addView(row);
    }

    private CheckBox check(LinearLayout card, String title, String sub, Getter g, Setter s) {
        CheckBox cb = new CheckBox(a);
        cb.setChecked(g.get());
        add(card, Ui.settingRow(a, title, sub, cb, v -> {
            boolean n = !cb.isChecked();
            cb.setChecked(n);
            s.set(n);
            p.save();
        }));
        return cb;
    }

    private void link(LinearLayout card, String title, String sub, Runnable r) {
        add(card, Ui.settingRow(a, title, sub, null, v -> r.run()));
    }

    private void build() {
        LinearLayout c = section("אפליקציות ברירת מחדל");
        link(c, "אפליקציות ברירת מחדל", "ניהול האפליקציות שנפתחות כברירת מחדל לכל סוג קובץ", () -> {
            try { a.startActivity(new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)); }
            catch (Exception e) { a.toast("לא זמין במכשיר זה"); }
        });

        c = section("אפליקציות מובנות");
        check(c, "מציג תמונות", "החלקה בין תמונות, זום וסיבוב", () -> p.viewImage, v -> p.viewImage = v);
        if (!Flavor.NO_VIDEO) check(c, "נגן וידאו", null, () -> p.viewVideo, v -> p.viewVideo = v);
        check(c, "נגן מוזיקה", "ניגון כל השירים בתיקיה ברצף", () -> p.viewAudio, v -> p.viewAudio = v);
        check(c, "עורך טקסט", "פתיחה ועריכה של קבצי טקסט", () -> p.viewText, v -> p.viewText = v);

        c = section("הגדרות הודעה");
        check(c, "האחסון מלא", "הצג הודעה כאשר האחסון מלא מעל 96%", () -> p.notifyFull, v -> p.notifyFull = v);

        c = section("הגדרות סל המיחזור");
        LinearLayout card = c;
        final CheckBox[] confirm = {null};
        check(c, "השתמש בסל המיחזור כברירת מחדל", "קבצים שנמחקים נשמרים 30 יום וניתנים לשחזור", () -> p.trashDefault, v -> {
            p.trashDefault = v;
            if (confirm[0] != null) ((View) confirm[0].getParent()).setAlpha(v ? 1f : 0.45f);
        });
        confirm[0] = check(card, "הצג אישור מחיקה", "שאלה לפני העברה לסל המיחזור", () -> p.confirmDelete, v -> {
            if (!p.trashDefault) { p.confirmDelete = true; confirm[0].setChecked(true); a.toast("מחיקה לצמיתות תמיד דורשת אישור"); return; }
            p.confirmDelete = v;
        });
        ((View) confirm[0].getParent()).setAlpha(p.trashDefault ? 1f : 0.45f);

        c = section("הגדרות מתקדמות");
        check(c, "הצג תמיד את סמל הניווט (☰)", "בתוך תיקיות: תפריט צד במקום חץ חזרה", () -> p.navIcon, v -> p.navIcon = v);
        check(c, "זיהוי חיבור USB", "רענון אוטומטי כשמחברים כרטיס SD או כונן USB", () -> p.usb, v -> p.usb = v);
        check(c, "הצג היסטוריה", "תיקיות אחרונות בתפריט הצד", () -> p.history, v -> {
            p.history = v;
            if (!v) p.clearRecent();
        });
        check(c, "הצג אחסון מערכת", "גישה לתיקיית השורש (/) לקריאה בלבד", () -> p.systemStorage, v -> p.systemStorage = v);
        final TextView[] unitSub = {null};
        LinearLayout unitRow = Ui.settingRow(a, "יחידת גודל קובץ", unitText(), null, v -> {
            ArrayList<Dialogs.Item> m = new ArrayList<>();
            m.add(new Dialogs.Item(!p.decimalUnits ? R.drawable.ic_check : 0, "בינארית (1 KB = 1024 בתים)", () -> setUnits(false, unitSub[0])));
            m.add(new Dialogs.Item(p.decimalUnits ? R.drawable.ic_check : 0, "עשרונית (1 KB = 1000 בתים)", () -> setUnits(true, unitSub[0])));
            Dialogs.sheet(a, "יחידת גודל קובץ", null, m);
        });
        unitSub[0] = (TextView) ((LinearLayout) unitRow.getChildAt(0)).getChildAt(1);
        add(c, unitRow);

        c = section("הצג תפריטים מתקדמים");
        check(c, "הוסף סימניה", null, () -> p.menuBookmark, v -> p.menuBookmark = v);
        check(c, "הוסף למסך הבית", null, () -> p.menuShortcut, v -> p.menuShortcut = v);
        check(c, "הסתרה / ביטול הסתרה", null, () -> p.menuHide, v -> p.menuHide = v);
        check(c, "פתח כ…", null, () -> p.menuOpenAs, v -> p.menuOpenAs = v);

        c = section("אודות האפליקציה");
        link(c, Flavor.NAME, "גרסה 2.2 · פועל ללא אינטרנט", () -> Dialogs.message(a, Flavor.NAME + " 2.2",
                "מנהל קבצים מהיר ונקי, מותאם לעברית.\n\n• עיון, העתקה, העברה, שינוי שם ומחיקה\n• סל מיחזור עם שחזור\n• דחיסה וחילוץ ZIP\n• חיפוש מהיר, קטגוריות וניתוח אחסון\n• מציג תמונות, נגן וידאו ומוזיקה ועורך טקסט מובנים"));
    }

    private String unitText() { return p.decimalUnits ? "עשרונית (1000)" : "בינארית (1024)"; }

    private void setUnits(boolean dec, TextView sub) {
        p.decimalUnits = dec;
        p.save();
        sub.setText(unitText());
    }
}
