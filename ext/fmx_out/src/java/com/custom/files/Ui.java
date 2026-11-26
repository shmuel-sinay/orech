package com.custom.files;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Design tokens and small view factories (the whole UI is built in code, no XML layouts). */
final class Ui {
    static final int BG = 0xFFF3EFE6, SURFACE = 0xFFFBF8F1, PRIMARY = 0xFF2F6F9F, PRIMARY_SOFT = 0xFFDDE9F2,
            TEXT = 0xFF1D2327, TEXT2 = 0xFF6B665C, TEXT3 = 0xFF9A958A, DIVIDER = 0xFFD9D3C4,
            DANGER = 0xFFB3472F, SELECTED = 0xFFD3E3EE, AMBER = 0xFFC98A1B, DARK = 0xFF26343B;
    static final int MATCH = -1, WRAP = -2;
    static float density = 1;
    static Typeface MEDIUM = Typeface.DEFAULT, BOLD = Typeface.DEFAULT_BOLD, REGULAR = Typeface.DEFAULT;

    static void init(Context c) {
        density = c.getResources().getDisplayMetrics().density;
        MEDIUM = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        BOLD = Typeface.create("sans-serif", Typeface.BOLD);
    }

    static int dp(float v) { return Math.round(v * density); }

    static ColorStateList csl(int c) { return ColorStateList.valueOf(c); }

    static GradientDrawable round(int color, float r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(r));
        return g;
    }

    static GradientDrawable stroke(int fill, int line, float r) {
        GradientDrawable g = round(fill, r);
        g.setStroke(Math.max(1, dp(1)), line);
        return g;
    }

    static GradientDrawable oval(int color) {
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.OVAL);
        g.setColor(color);
        return g;
    }

    static GradientDrawable topRound(int color, float r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        float R = dp(r);
        g.setCornerRadii(new float[]{R, R, R, R, 0, 0, 0, 0});
        return g;
    }

    static Drawable ripple(Drawable content, float r) {
        return new RippleDrawable(csl(0x222F6F9F), content, round(0xFFFFFFFF, r));
    }

    static Drawable ripple(int color, float r) { return ripple(round(color, r), r); }

    static Drawable rippleRect() {
        return new RippleDrawable(csl(0x1C2F6F9F), null, new ColorDrawable(0xFFFFFFFF));
    }

    static Drawable rippleBorderless() {
        return new RippleDrawable(csl(0x1F0F172A), null, null);
    }

    static TextView text(Context c, CharSequence s, float sp, int color) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        t.setTextColor(color);
        // Align to the view's start (right in RTL) even when the text itself is English
        t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        return t;
    }

    static TextView text(Context c, CharSequence s, float sp, int color, Typeface tf) {
        TextView t = text(c, s, sp, color);
        t.setTypeface(tf);
        return t;
    }

    static TextView line(Context c, CharSequence s, float sp, int color, Typeface tf) {
        TextView t = text(c, s, sp, color, tf);
        t.setSingleLine(true);
        t.setEllipsize(TextUtils.TruncateAt.END);
        return t;
    }

    static ImageView img(Context c, int res, int tint) {
        ImageView v = new ImageView(c);
        if (res != 0) v.setImageResource(res);
        v.setImageTintList(csl(tint));
        return v;
    }

    static ImageView iconBtn(Context c, int res, int tint, String desc, View.OnClickListener l) {
        ImageView v = img(c, res, tint);
        int p = dp(12);
        v.setPadding(p, p, p, p);
        v.setBackground(rippleBorderless());
        v.setContentDescription(desc);
        if (desc != null) v.setTooltipText(desc);
        v.setOnClickListener(l);
        v.setLayoutParams(new LinearLayout.LayoutParams(dp(48), dp(48)));
        return v;
    }

    static LinearLayout hbox(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    static LinearLayout vbox(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    static LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w, h); }

    static LinearLayout.LayoutParams lp(int w, int h, float weight) { return new LinearLayout.LayoutParams(w, h, weight); }

    static LinearLayout.LayoutParams margins(LinearLayout.LayoutParams p, float s, float t, float e, float b) {
        p.setMarginStart(dp(s));
        p.topMargin = dp(t);
        p.setMarginEnd(dp(e));
        p.bottomMargin = dp(b);
        return p;
    }

    static void pad(View v, float s, float t, float e, float b) {
        v.setPaddingRelative(dp(s), dp(t), dp(e), dp(b));
    }

    static View divider(Context c) {
        View v = new View(c);
        v.setBackgroundColor(DIVIDER);
        v.setLayoutParams(lp(MATCH, Math.max(1, dp(1))));
        return v;
    }

    static TextView button(Context c, String s, boolean filled, int color, View.OnClickListener l) {
        TextView b = text(c, s, 14.5f, filled ? 0xFFFFFFFF : color, MEDIUM);
        b.setGravity(Gravity.CENTER);
        b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pad(b, 18, 10, 18, 10);
        b.setMinWidth(dp(72));
        b.setBackground(filled ? ripple(color, 22) : ripple(0x00FFFFFF, 22));
        b.setOnClickListener(l);
        return b;
    }

    static TextView button(Context c, String s, boolean filled, View.OnClickListener l) {
        return button(c, s, filled, PRIMARY, l);
    }

    /** Small usage pie (ring) for the breadcrumb chip. */
    static final class Pie extends View {
        float frac;
        int color = PRIMARY;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Pie(Context c) { super(c); }

        void set(float f, int col) { frac = f; color = col; invalidate(); }

        @Override
        protected void onDraw(Canvas c) {
            float s = Math.min(getWidth(), getHeight());
            r.set(0, 0, s, s);
            p.setColor(DIVIDER);
            c.drawOval(r, p);
            p.setColor(color);
            c.drawArc(r, -90, 360 * frac, true, p);
        }
    }

    /** Standard top app bar: [leading icon] title … actions. */
    static LinearLayout appBar(Context c) {
        LinearLayout bar = hbox(c);
        pad(bar, 4, 0, 4, 0);
        bar.setBackgroundColor(SURFACE);
        bar.setElevation(0);
        return bar;
    }

    /** A row in a settings-like list with optional check box. */
    static LinearLayout settingRow(Context c, String title, String sub, android.widget.CheckBox box, View.OnClickListener l) {
        LinearLayout r = hbox(c);
        pad(r, 20, 12, 20, 12);
        r.setMinimumHeight(dp(60));
        r.setBackground(rippleRect());
        LinearLayout t = vbox(c);
        t.addView(text(c, title, 16, TEXT));
        if (sub != null) {
            TextView s = text(c, sub, 13, TEXT2);
            s.setTextDirection(View.TEXT_DIRECTION_RTL);
            t.addView(s);
        }
        r.addView(t, lp(0, WRAP, 1));
        if (box != null) {
            box.setClickable(false);
            box.setFocusable(false);
            box.setButtonTintList(csl(PRIMARY));
            r.addView(box);
        }
        r.setOnClickListener(l);
        return r;
    }

    /** Rounded usage bar, fills from the start side (right in RTL). */
    static final class Bar extends View {
        float frac;
        int color = PRIMARY;
        boolean flat;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF r = new RectF();

        Bar(Context c) { super(c); }

        void set(float f, int col) {
            frac = Math.max(0, Math.min(1, f));
            color = col;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas c) {
            float h = getHeight(), w = getWidth(), rad = flat ? 0 : h / 2f;
            p.setColor(DIVIDER);
            r.set(0, 0, w, h);
            c.drawRoundRect(r, rad, rad, p);
            if (frac <= 0) return;
            float fw = Math.max(h, w * frac);
            p.setColor(color);
            if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) r.set(w - fw, 0, w, h);
            else r.set(0, 0, fw, h);
            c.drawRoundRect(r, rad, rad, p);
        }
    }
}
