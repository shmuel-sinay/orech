package com.custom.files;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Built-in image viewer, video player, music player and text editor. */
final class Viewers {
    interface Closeable {
        void close();

        /** The activity is going to the background. */
        default void pauseHint() { }
    }

    private static LinearLayout overlayBar(MainActivity a, String titleText, TextView[] titleOut) {
        LinearLayout bar = Ui.hbox(a);
        Ui.pad(bar, 4, 0, 4, 0);
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xB0000000, 0x00000000});
        bar.setBackground(g);
        bar.addView(Ui.iconBtn(a, R.drawable.ic_back, 0xFFFFFFFF, "חזרה", v -> a.onBackPressed()));
        TextView t = Ui.line(a, titleText, 17, 0xFFFFFFFF, Ui.MEDIUM);
        t.setPaddingRelative(Ui.dp(6), 0, Ui.dp(6), 0);
        bar.addView(t, Ui.lp(0, Ui.WRAP, 1));
        if (titleOut != null) titleOut[0] = t;
        return bar;
    }

    // ================================================================= image viewer

    /** ImageView with pinch-zoom, pan, double-tap and swipe-to-next. */
    static final class ZoomView extends ImageView {
        interface Listener { void swipe(int dir); void tap(); }

        final Matrix m = new Matrix();
        final float[] v = new float[9];
        float base = 1, scale = 1;
        Listener listener;
        final ScaleGestureDetector sg;
        final GestureDetector gd;

        ZoomView(Context c) {
            super(c);
            setScaleType(ScaleType.MATRIX);
            sg = new ScaleGestureDetector(c, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override public boolean onScale(ScaleGestureDetector d) {
                    float f = Math.max(1f / scale, Math.min(d.getScaleFactor(), 6f / scale));
                    scale *= f;
                    m.postScale(f, f, d.getFocusX(), d.getFocusY());
                    clamp();
                    return true;
                }
            });
            gd = new GestureDetector(c, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onDown(MotionEvent e) { return true; }

                @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (listener != null) listener.tap();
                    return true;
                }

                @Override public boolean onDoubleTap(MotionEvent e) {
                    if (scale > 1.05f) fit();
                    else {
                        scale = 2.5f;
                        m.postScale(2.5f, 2.5f, e.getX(), e.getY());
                        clamp();
                    }
                    return true;
                }

                @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                    if (scale <= 1.01f) return false;
                    m.postTranslate(-dx, -dy);
                    clamp();
                    return true;
                }

                @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                    if (scale <= 1.01f && Math.abs(vx) > Math.abs(vy) * 1.5f && Math.abs(vx) > 600 && listener != null) {
                        listener.swipe(vx > 0 ? 1 : -1); // RTL: dragging right reveals the next image
                        return true;
                    }
                    return false;
                }
            });
        }

        @Override
        public void setImageBitmap(Bitmap bm) {
            super.setImageBitmap(bm);
            fit();
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
            fit();
        }

        void fit() {
            if (getDrawable() == null || getWidth() == 0) return;
            float dw = getDrawable().getIntrinsicWidth(), dh = getDrawable().getIntrinsicHeight();
            base = Math.min(getWidth() / dw, getHeight() / dh);
            scale = 1;
            m.reset();
            m.postScale(base, base);
            m.postTranslate((getWidth() - dw * base) / 2f, (getHeight() - dh * base) / 2f);
            setImageMatrix(m);
        }

        private void clamp() {
            if (getDrawable() == null) return;
            m.getValues(v);
            float w = getDrawable().getIntrinsicWidth() * v[Matrix.MSCALE_X], h = getDrawable().getIntrinsicHeight() * v[Matrix.MSCALE_Y];
            float x = v[Matrix.MTRANS_X], y = v[Matrix.MTRANS_Y], dx = 0, dy = 0;
            if (w <= getWidth()) dx = (getWidth() - w) / 2f - x;
            else if (x > 0) dx = -x;
            else if (x + w < getWidth()) dx = getWidth() - (x + w);
            if (h <= getHeight()) dy = (getHeight() - h) / 2f - y;
            else if (y > 0) dy = -y;
            else if (y + h < getHeight()) dy = getHeight() - (y + h);
            m.postTranslate(dx, dy);
            setImageMatrix(m);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            sg.onTouchEvent(e);
            gd.onTouchEvent(e);
            return true;
        }
    }

    static final class Image extends FrameLayout implements MainActivity.Screen, Closeable {
        private final MainActivity a;
        private final ArrayList<File> files = new ArrayList<>();
        private final ZoomView zv;
        private final LinearLayout top;
        private final TextView[] title = {null};
        private final TextView counter;
        private final ProgressBar pb;
        private int idx, gen;
        private boolean chrome = true;

        Image(MainActivity a, FileItem it, List<FileItem> siblings) {
            super(a);
            this.a = a;
            setBackgroundColor(0xFF000000);
            for (FileItem s : siblings) if (s.kind == Fx.K_IMAGE && !"svg".equals(Fx.ext(s.name))) files.add(s.file);
            idx = files.indexOf(it.file);
            if (idx < 0) { files.add(0, it.file); idx = 0; }
            zv = new ZoomView(a);
            addView(zv, new LayoutParams(Ui.MATCH, Ui.MATCH));
            pb = new ProgressBar(a);
            pb.setIndeterminateTintList(Ui.csl(0xFFFFFFFF));
            addView(pb, new LayoutParams(Ui.dp(36), Ui.dp(36), Gravity.CENTER));
            top = overlayBar(a, "", title);
            counter = Ui.text(a, "", 13, 0xCCFFFFFF);
            counter.setTextDirection(View.TEXT_DIRECTION_RTL);
            top.addView(counter);
            top.addView(Ui.iconBtn(a, R.drawable.ic_share, 0xFFFFFFFF, "שיתוף", v -> Fx.share(a, Collections.singletonList(files.get(idx)))));
            top.addView(Ui.iconBtn(a, R.drawable.ic_delete, 0xFFFFFFFF, "מחיקה", v -> delete()));
            top.addView(Ui.iconBtn(a, R.drawable.ic_open, 0xFFFFFFFF, "פתח באמצעות", v -> Fx.open(a, files.get(idx), true)));
            addView(top, new LayoutParams(Ui.MATCH, Ui.dp(72), Gravity.TOP));
            zv.listener = new ZoomView.Listener() {
                @Override public void swipe(int dir) { show(idx + dir); }

                @Override public void tap() {
                    chrome = !chrome;
                    top.animate().alpha(chrome ? 1f : 0f).setDuration(150).start();
                }
            };
            show(idx);
        }

        private void show(int i) {
            if (i < 0 || i >= files.size()) return;
            idx = i;
            File f = files.get(i);
            title[0].setText(f.getName());
            counter.setText(files.size() > 1 ? (i + 1) + "/" + files.size() : "");
            Bitmap thumb = Thumbs.cached(f);
            if (thumb != null) zv.setImageBitmap(thumb);
            else zv.setImageDrawable(null);
            pb.setVisibility(VISIBLE);
            final int g = ++gen;
            DisplayMetrics dm = getResources().getDisplayMetrics();
            final int maxPx = Math.max(dm.widthPixels, dm.heightPixels) * 2;
            MainActivity.BG.execute(() -> {
                Bitmap b = decode(f, maxPx);
                MainActivity.H.post(() -> {
                    if (g != gen) return;
                    pb.setVisibility(GONE);
                    if (b != null) zv.setImageBitmap(b);
                    else if (thumb == null) a.toast("לא ניתן להציג את התמונה");
                });
            });
        }

        private static Bitmap decode(File f, int maxPx) {
            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    return ImageDecoder.decodeBitmap(ImageDecoder.createSource(f), (dec, info, src) -> {
                        int w = info.getSize().getWidth(), h = info.getSize().getHeight();
                        int s = 1;
                        while (Math.max(w, h) / s > maxPx) s *= 2;
                        if (s > 1) dec.setTargetSize(w / s, h / s);
                        dec.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
                    });
                }
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(f.getPath(), o);
                int s = 1;
                while (Math.max(o.outWidth, o.outHeight) / s > maxPx) s *= 2;
                o.inJustDecodeBounds = false;
                o.inSampleSize = s;
                return BitmapFactory.decodeFile(f.getPath(), o);
            } catch (Throwable t) {
                return null;
            }
        }

        private void delete() {
            File f = files.get(idx);
            Dialogs.confirm(a, "למחוק את התמונה?", a.prefs.trashDefault ? "התמונה תועבר לסל המיחזור." : "לא ניתן יהיה לשחזר אותה.",
                    "מחק", true, null, c -> {
                        try {
                            if (a.prefs.trashDefault) Trash.put(a, f);
                            else Fx.deleteTree(f, null);
                        } catch (Exception e) {
                            a.toast(e.getMessage());
                            return;
                        }
                        Thumbs.forget(f);
                        android.media.MediaScannerConnection.scanFile(a.getApplicationContext(), new String[]{f.getPath()}, null, null);
                        files.remove(idx);
                        if (files.isEmpty()) a.onBackPressed();
                        else show(Math.min(idx, files.size() - 1));
                    });
        }

        @Override public boolean onBack() { return false; }

        @Override public void onShow() { a.setBars(0xFF000000, false); }

        @Override protected void onAttachedToWindow() { super.onAttachedToWindow(); onShow(); }

        @Override public void close() { gen++; }

        @Override public void pauseHint() { }
    }

    // ================================================================= text editor

    static final class Text extends LinearLayout implements MainActivity.Screen, Closeable {
        private final MainActivity a;
        private final File f;
        private final EditText et;
        private final ImageView save;
        private final TextView title;
        private Charset cs = StandardCharsets.UTF_8;
        private boolean dirty, loaded;

        Text(MainActivity a, File f) {
            super(a);
            this.a = a;
            this.f = f;
            setOrientation(VERTICAL);
            setBackgroundColor(Ui.SURFACE);
            LinearLayout bar = Ui.appBar(a);
            bar.addView(Ui.iconBtn(a, R.drawable.ic_back, Ui.TEXT, "חזרה", v -> a.onBackPressed()));
            title = Ui.line(a, f.getName(), 18, Ui.TEXT, Ui.MEDIUM);
            title.setPaddingRelative(Ui.dp(6), 0, 0, 0);
            bar.addView(title, Ui.lp(0, Ui.WRAP, 1));
            save = Ui.iconBtn(a, R.drawable.ic_save, Ui.PRIMARY, "שמירה", v -> save(null));
            save.setAlpha(0.35f);
            bar.addView(save);
            bar.addView(Ui.iconBtn(a, R.drawable.ic_open, Ui.TEXT, "פתח באמצעות", v -> Fx.open(a, f, true)));
            addView(bar, Ui.lp(Ui.MATCH, Ui.dp(60)));
            ScrollView sv = new ScrollView(a);
            sv.setFillViewport(true);
            et = new EditText(a);
            et.setBackground(null);
            et.setGravity(Gravity.TOP | Gravity.START);
            et.setTextSize(15);
            et.setTextColor(Ui.TEXT);
            et.setLineSpacing(0, 1.15f);
            et.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
            et.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            et.setHint("קובץ ריק");
            Ui.pad(et, 18, 14, 18, 40);
            et.setEnabled(false);
            sv.addView(et, new ScrollView.LayoutParams(Ui.MATCH, Ui.WRAP));
            addView(sv, Ui.lp(Ui.MATCH, 0, 1));
            et.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int af) { }

                @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }

                @Override public void afterTextChanged(Editable s) {
                    if (!loaded || dirty) return;
                    dirty = true;
                    save.setAlpha(1f);
                    title.setText("• " + f.getName());
                }
            });
            if (f.length() > (3 << 20)) {
                a.toast("הקובץ גדול מדי לעורך המובנה");
                MainActivity.H.post(() -> { a.onBackPressed(); Fx.open(a, f, false); });
                return;
            }
            MainActivity.BG.execute(() -> {
                String text;
                try {
                    byte[] data = Files.readAllBytes(f.toPath());
                    text = decode(data);
                } catch (Exception e) {
                    text = null;
                }
                final String ft = text;
                MainActivity.H.post(() -> {
                    if (ft == null) { a.toast("לא ניתן לקרוא את הקובץ"); return; }
                    et.setText(ft);
                    et.setEnabled(f.canWrite());
                    loaded = true;
                    if (!f.canWrite()) a.toast("קובץ לקריאה בלבד");
                });
            });
        }

        /** UTF-8 first; Hebrew Windows files fall back to windows-1255. */
        private String decode(byte[] data) {
            try {
                CharsetDecoder d = StandardCharsets.UTF_8.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
                return d.decode(ByteBuffer.wrap(data)).toString();
            } catch (Exception e) {
                try { cs = Charset.forName("windows-1255"); } catch (Exception x) { cs = StandardCharsets.ISO_8859_1; }
                return new String(data, cs);
            }
        }

        private void save(Runnable after) {
            if (!dirty) { if (after != null) after.run(); return; }
            String s = et.getText().toString();
            MainActivity.BG.execute(() -> {
                boolean ok;
                try (FileOutputStream out = new FileOutputStream(f)) {
                    out.write(s.getBytes(cs));
                    ok = true;
                } catch (Exception e) {
                    ok = false;
                }
                final boolean fok = ok;
                MainActivity.H.post(() -> {
                    if (!fok) { a.toast("השמירה נכשלה"); return; }
                    dirty = false;
                    save.setAlpha(0.35f);
                    title.setText(f.getName());
                    a.toast("נשמר");
                    if (after != null) after.run();
                });
            });
        }

        @Override
        public boolean onBack() {
            if (!dirty) return false;
            ArrayList<Dialogs.Item> m = new ArrayList<>();
            m.add(new Dialogs.Item(R.drawable.ic_save, "שמור וצא", () -> save(() -> { dirty = false; a.onBackPressed(); })));
            m.add(new Dialogs.Item(R.drawable.ic_close, "צא בלי לשמור", () -> { dirty = false; a.onBackPressed(); }).danger());
            Dialogs.sheet(a, "יש שינויים שלא נשמרו", f.getName(), m);
            return true;
        }

        @Override public void onShow() { a.setBars(Ui.SURFACE, true); }

        @Override protected void onAttachedToWindow() { super.onAttachedToWindow(); onShow(); }

        @Override public void close() { }
    }
}
