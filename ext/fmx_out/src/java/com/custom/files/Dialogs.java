package com.custom.files;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/** Rounded, RTL dialogs and bottom sheets in the app's style. */
final class Dialogs {
    static final class Item {
        final int icon;
        final String label;
        final Runnable run;
        boolean danger;

        Item(int icon, String label, Runnable run) {
            this.icon = icon;
            this.label = label;
            this.run = run;
        }

        Item danger() { danger = true; return this; }
    }

    interface Str { void ok(String s); }

    interface Bool { void ok(boolean checked); }

    static Dialog make(Context c, View content, boolean bottom) {
        Dialog d = new Dialog(c);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // dialogs don't inherit the activity's direction
        content.setBackground(bottom ? Ui.topRound(Ui.SURFACE, 24) : Ui.round(Ui.SURFACE, 24));
        d.setContentView(content);
        Window w = d.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(0));
            w.setDimAmount(0.32f);
            if (bottom) {
                w.setGravity(Gravity.BOTTOM);
                w.setWindowAnimations(android.R.style.Animation_InputMethod);
            }
        }
        return d;
    }

    static void show(Dialog d, boolean bottom) {
        d.show();
        Window w = d.getWindow();
        if (w == null) return;
        int sw = d.getContext().getResources().getDisplayMetrics().widthPixels;
        if (bottom) w.setLayout(Math.min(sw, Ui.dp(600)), Ui.WRAP);
        else w.setLayout(Math.min(sw - Ui.dp(40), Ui.dp(440)), Ui.WRAP);
    }

    static void sheet(Activity a, String title, String sub, List<Item> items) {
        LinearLayout box = Ui.vbox(a);
        Ui.pad(box, 0, 10, 0, 10);
        View handle = new View(a);
        handle.setBackground(Ui.round(0xFFD5DBE3, 3));
        LinearLayout.LayoutParams hp = Ui.lp(Ui.dp(40), Ui.dp(4));
        hp.gravity = Gravity.CENTER_HORIZONTAL;
        hp.bottomMargin = Ui.dp(8);
        box.addView(handle, hp);
        if (title != null) {
            TextView t = Ui.line(a, title, 16.5f, Ui.TEXT, Ui.MEDIUM);
            Ui.pad(t, 22, 6, 22, 0);
            box.addView(t);
            if (sub != null) {
                TextView s = Ui.line(a, sub, 12.5f, Ui.TEXT2, Ui.REGULAR);
                s.setTextDirection(View.TEXT_DIRECTION_RTL);
                Ui.pad(s, 22, 2, 22, 0);
                box.addView(s);
            }
            View dv = Ui.divider(a);
            box.addView(dv, Ui.margins(Ui.lp(Ui.MATCH, Math.max(1, Ui.dp(1))), 0, 12, 0, 4));
        }
        Dialog d = make(a, box, true);
        LinearLayout list = Ui.vbox(a);
        for (Item it : items) {
            LinearLayout row = Ui.hbox(a);
            Ui.pad(row, 22, 0, 22, 0);
            row.setBackground(Ui.rippleRect());
            ImageView ic = Ui.img(a, it.icon, it.danger ? Ui.DANGER : Ui.TEXT2);
            row.addView(ic, Ui.lp(Ui.dp(22), Ui.dp(22)));
            TextView l = Ui.text(a, it.label, 15.5f, it.danger ? Ui.DANGER : Ui.TEXT);
            l.setPaddingRelative(Ui.dp(18), 0, 0, 0);
            row.addView(l, Ui.lp(0, Ui.WRAP, 1));
            row.setOnClickListener(v -> {
                d.dismiss();
                it.run.run();
            });
            list.addView(row, Ui.lp(Ui.MATCH, Ui.dp(52)));
        }
        ScrollView sv = new ScrollView(a);
        sv.setVerticalScrollBarEnabled(false);
        sv.addView(list);
        box.addView(sv, Ui.lp(Ui.MATCH, Ui.WRAP));
        show(d, true);
    }

    private static LinearLayout card(Activity a, String title) {
        LinearLayout box = Ui.vbox(a);
        Ui.pad(box, 24, 22, 24, 12);
        box.addView(Ui.text(a, title, 18.5f, Ui.TEXT, Ui.MEDIUM));
        return box;
    }

    private static LinearLayout buttons(Activity a, LinearLayout box) {
        LinearLayout btns = Ui.hbox(a);
        btns.setGravity(Gravity.END);
        box.addView(btns, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 18, 0, 0));
        return btns;
    }

    static void input(Activity a, String title, String init, String okText, boolean selectBase, Str cb) {
        LinearLayout box = card(a, title);
        EditText et = new EditText(a);
        et.setText(init);
        et.setSingleLine(true);
        et.setTextSize(16);
        et.setTextColor(Ui.TEXT);
        et.setBackground(Ui.stroke(0xFFF8FAFC, 0xFFCBD5E1, 12));
        Ui.pad(et, 14, 12, 14, 12);
        et.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        box.addView(et, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 16, 0, 0));
        Dialog d = make(a, box, false);
        Runnable ok = () -> {
            String s = et.getText().toString().trim();
            if (!Fx.validName(s)) { et.setError("שם לא תקין"); return; }
            d.dismiss();
            cb.ok(s);
        };
        LinearLayout btns = buttons(a, box);
        btns.addView(Ui.button(a, "ביטול", false, v -> d.dismiss()));
        btns.addView(Ui.button(a, okText, true, v -> ok.run()));
        et.setOnEditorActionListener((v, id, ev) -> { ok.run(); return true; });
        if (d.getWindow() != null)
            d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        show(d, false);
        et.requestFocus();
        int dot = init.lastIndexOf('.');
        if (selectBase && dot > 0) et.setSelection(0, dot);
        else et.selectAll();
    }

    static void confirm(Activity a, String title, String msg, String okText, boolean danger, String checkLabel, Bool cb) {
        LinearLayout box = card(a, title);
        if (msg != null) {
            TextView m = Ui.text(a, msg, 14.5f, Ui.TEXT2);
            m.setLineSpacing(0, 1.15f);
            box.addView(m, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 10, 0, 0));
        }
        CheckBox cbx = null;
        if (checkLabel != null) {
            cbx = new CheckBox(a);
            cbx.setText(checkLabel);
            cbx.setTextColor(Ui.TEXT);
            cbx.setTextSize(14.5f);
            cbx.setButtonTintList(Ui.csl(Ui.PRIMARY));
            box.addView(cbx, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), -6, 10, 0, 0));
        }
        Dialog d = make(a, box, false);
        final CheckBox fc = cbx;
        LinearLayout btns = buttons(a, box);
        btns.addView(Ui.button(a, "ביטול", false, v -> d.dismiss()));
        btns.addView(Ui.button(a, okText, true, danger ? Ui.DANGER : Ui.PRIMARY, v -> {
            d.dismiss();
            cb.ok(fc != null && fc.isChecked());
        }));
        show(d, false);
    }

    static void message(Activity a, String title, String msg) {
        LinearLayout box = card(a, title);
        TextView m = Ui.text(a, msg, 14.5f, Ui.TEXT2);
        m.setLineSpacing(0, 1.2f);
        box.addView(m, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 10, 0, 0));
        Dialog d = make(a, box, false);
        buttons(a, box).addView(Ui.button(a, "סגור", true, v -> d.dismiss()));
        show(d, false);
    }

    private static TextView row(Activity a, LinearLayout box, String label, String value) {
        LinearLayout r = Ui.vbox(a);
        r.addView(Ui.text(a, label, 12.5f, Ui.TEXT3));
        TextView v = Ui.text(a, value, 15, Ui.TEXT);
        v.setTextIsSelectable(true);
        r.addView(v);
        box.addView(r, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 12, 0, 0));
        return v;
    }

    static void details(MainActivity a, List<File> files) {
        LinearLayout box = card(a, "פרטים");
        boolean one = files.size() == 1;
        File f0 = files.get(0);
        boolean anyDir = false;
        for (File f : files) if (f.isDirectory()) anyDir = true;
        TextView sizeV, contV = null;
        if (one) {
            boolean dir = f0.isDirectory();
            row(a, box, "שם", f0.getName());
            row(a, box, "סוג", dir ? "תיקיה" : Fx.KIND_NAME[Fx.kind(f0.getName())]
                    + (Fx.ext(f0.getName()).isEmpty() ? "" : " (" + Fx.ext(f0.getName()).toUpperCase() + ")"));
            File par = f0.getParentFile();
            TextView loc = row(a, box, "מיקום", par == null ? "/" : Storage.pretty(a, par));
            loc.setTextDirection(View.TEXT_DIRECTION_RTL);
            sizeV = row(a, box, "גודל", dir ? "מחשב…" : Fx.size(f0.length()) + "  (" + Fx.num(f0.length()) + " בתים)");
            if (dir) contV = row(a, box, "תוכן", "מחשב…");
            row(a, box, "שונה לאחרונה", Fx.fullDate(f0.lastModified()));
        } else {
            row(a, box, "נבחרו", Fx.items(files.size()));
            File par = f0.getParentFile();
            if (par != null) row(a, box, "מיקום", Storage.pretty(a, par)).setTextDirection(View.TEXT_DIRECTION_RTL);
            sizeV = row(a, box, "גודל כולל", "מחשב…");
            if (anyDir) contV = row(a, box, "תוכן", "מחשב…");
        }
        Dialog d = make(a, box, false);
        LinearLayout btns = buttons(a, box);
        if (one) btns.addView(Ui.button(a, "העתק נתיב", false, v -> {
            Fx.copyText(a, f0.getAbsolutePath());
            a.toast("הנתיב הועתק");
        }));
        btns.addView(Ui.button(a, "סגור", true, v -> d.dismiss()));
        final Ops.Op op = new Ops.Op();
        d.setOnDismissListener(x -> op.cancel = true);
        show(d, false);
        if (one && !f0.isDirectory()) return;
        final TextView fs = sizeV, fc = contV;
        MainActivity.BG.execute(() -> {
            long[] acc = new long[3];
            try {
                for (File f : files) Fx.countTree(f, acc, op);
            } catch (Ops.Cancelled c) {
                return;
            }
            long folders = acc[2] - (one ? 1 : 0);
            MainActivity.H.post(() -> {
                fs.setText(Fx.size(acc[0]) + "  (" + Fx.num(acc[0]) + " בתים)");
                if (fc != null) fc.setText(Fx.files((int) acc[1]) + " · " + (folders == 1 ? "תיקיה אחת" : Fx.num(folders) + " תיקיות"));
            });
        });
    }
}
