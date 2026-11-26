package com.custom.files;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Full-screen music player and the mini player bar shown above the file lists. */
final class Music {

    static void open(MainActivity a, FileItem start, List<FileItem> siblings) {
        ArrayList<File> list = new ArrayList<>();
        for (FileItem s : siblings) if (s.kind == Fx.K_AUDIO) list.add(s.file);
        if (!list.contains(start.file)) list.add(0, start.file);
        Player.play(a, list, list.indexOf(start.file));
        a.push(new Screen(a));
    }

    private static int artColorTop() { return 0xFFFFF1D0; }

    /** Centers its child as the largest square that fits. */
    static final class FitSquare extends FrameLayout {
        FitSquare(android.content.Context c, View child) {
            super(c);
            addView(child, new LayoutParams(Ui.MATCH, Ui.MATCH));
        }

        @Override
        protected void onMeasure(int w, int h) {
            int ws = MeasureSpec.getSize(w), hs = MeasureSpec.getSize(h);
            int s = Math.min(ws, hs > 0 ? hs : ws);
            s = Math.min(s, Ui.dp(380));
            int spec = MeasureSpec.makeMeasureSpec(s, MeasureSpec.EXACTLY);
            super.onMeasure(spec, spec);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
        }
    }

    /** Artwork with a pleasant fallback. */
    private static FrameLayout artView(MainActivity a, float radius, int noteDp, ImageView[] imgOut) {
        FrameLayout art = new FrameLayout(a);
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{0xFFFFD27A, 0xFFF59E0B});
        g.setCornerRadius(Ui.dp(radius));
        art.setBackground(g);
        art.setClipToOutline(true);
        art.addView(Ui.img(a, R.drawable.ic_audio, 0xCCFFFFFF), new FrameLayout.LayoutParams(Ui.dp(noteDp), Ui.dp(noteDp), Gravity.CENTER));
        ImageView img = new ImageView(a);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        art.addView(img, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        imgOut[0] = img;
        return art;
    }

    // ================================================================= full screen

    static final class Screen extends LinearLayout implements MainActivity.Screen, Player.Listener {
        private final MainActivity a;
        private final ImageView[] art = {null};
        private final TextView title, sub, pos, dur, from, speedChip, sleepChip, queueChip;
        private final SeekBar seek;
        private final ImageView play, shuffle, repeat;
        private boolean dragging;
        private final Runnable tick = new Runnable() {
            @Override public void run() {
                if (!isAttachedToWindow()) return;
                if (!dragging) {
                    seek.setMax(Math.max(1, Player.duration()));
                    seek.setProgress(Player.position());
                    pos.setText(Player.time(Player.position()));
                }
                if (Player.sleepAt > 0) sleepChip.setText("שינה · " + Player.time((int) (Player.sleepAt - System.currentTimeMillis())));
                MainActivity.H.postDelayed(this, 250);
            }
        };

        Screen(MainActivity a) {
            super(a);
            this.a = a;
            setOrientation(VERTICAL);
            setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{artColorTop(), Ui.SURFACE, Ui.SURFACE}));
            setClickable(true);

            LinearLayout bar = Ui.hbox(a);
            Ui.pad(bar, 4, 0, 4, 0);
            bar.addView(Ui.iconBtn(a, R.drawable.ic_down, Ui.TEXT, "סגור", v -> a.onBackPressed()));
            LinearLayout tv = Ui.vbox(a);
            tv.setGravity(Gravity.CENTER);
            TextView h = Ui.text(a, "מתנגן כעת", 13, Ui.TEXT2, Ui.MEDIUM);
            h.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            from = Ui.line(a, "", 14, Ui.TEXT, Ui.MEDIUM);
            from.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            tv.addView(h, Ui.lp(Ui.MATCH, Ui.WRAP));
            tv.addView(from, Ui.lp(Ui.MATCH, Ui.WRAP));
            bar.addView(tv, Ui.lp(0, Ui.WRAP, 1));
            bar.addView(Ui.iconBtn(a, R.drawable.ic_more, Ui.TEXT, "אפשרויות", v -> more()));
            addView(bar, Ui.lp(Ui.MATCH, Ui.dp(60)));

            // artwork, square and as large as fits
            FrameLayout artWrap = new FrameLayout(a);
            FrameLayout artBox = artView(a, 28, 96, art);
            artBox.setElevation(Ui.dp(10));
            artWrap.addView(new FitSquare(a, artBox), new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH, Gravity.CENTER));
            Ui.pad(artWrap, 32, 12, 32, 12);
            addView(artWrap, Ui.lp(Ui.MATCH, 0, 1));
            GestureDetector gd = new GestureDetector(a, new GestureDetector.SimpleOnGestureListener() {
                @Override public boolean onDown(MotionEvent e) { return true; }

                @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                    if (Math.abs(vx) < Math.abs(vy) || Math.abs(vx) < 500) return false;
                    if (vx > 0) Player.next(); else Player.prev(); // RTL: drag right = next
                    return true;
                }

                @Override public boolean onDoubleTap(MotionEvent e) { Player.toggle(); return true; }
            });
            artWrap.setOnTouchListener((v, e) -> gd.onTouchEvent(e));

            LinearLayout info = Ui.vbox(a);
            Ui.pad(info, 28, 8, 28, 0);
            title = Ui.line(a, "", 22, Ui.TEXT, Ui.BOLD);
            title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            title.setMarqueeRepeatLimit(-1);
            title.setSelected(true);
            title.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            sub = Ui.line(a, "", 14.5f, Ui.TEXT2, Ui.REGULAR);
            sub.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            info.addView(title, Ui.lp(Ui.MATCH, Ui.WRAP));
            info.addView(sub, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 4, 0, 0));
            addView(info);

            // seek bar (time always flows left → right)
            LinearLayout seekBox = Ui.vbox(a);
            seekBox.setLayoutDirection(LAYOUT_DIRECTION_LTR);
            Ui.pad(seekBox, 14, 14, 14, 0);
            seek = new SeekBar(a);
            seek.setProgressTintList(Ui.csl(Ui.PRIMARY));
            seek.setThumbTintList(Ui.csl(Ui.PRIMARY));
            seek.setProgressBackgroundTintList(Ui.csl(0xFFB0B8C4));
            seekBox.addView(seek, Ui.lp(Ui.MATCH, Ui.WRAP));
            LinearLayout times = Ui.hbox(a);
            Ui.pad(times, 16, 0, 16, 0);
            pos = Ui.text(a, "0:00", 12.5f, Ui.TEXT2);
            dur = Ui.text(a, "0:00", 12.5f, Ui.TEXT2);
            times.addView(pos, Ui.lp(0, Ui.WRAP, 1));
            times.addView(dur);
            seekBox.addView(times);
            addView(seekBox);
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onProgressChanged(SeekBar s, int p, boolean user) { if (user) pos.setText(Player.time(p)); }

                @Override public void onStartTrackingTouch(SeekBar s) { dragging = true; }

                @Override public void onStopTrackingTouch(SeekBar s) {
                    dragging = false;
                    Player.seek(s.getProgress());
                }
            });

            // transport controls (prev on the left, next on the right, like every player)
            LinearLayout ctl = Ui.hbox(a);
            ctl.setGravity(Gravity.CENTER);
            ctl.setLayoutDirection(LAYOUT_DIRECTION_LTR);
            shuffle = Ui.iconBtn(a, R.drawable.ic_shuffle, Ui.TEXT2, "ערבוב", v -> Player.toggleShuffle());
            ImageView prev = Ui.iconBtn(a, R.drawable.ic_prev, Ui.TEXT, "הקודם", v -> Player.prev());
            play = Ui.img(a, R.drawable.ic_play, 0xFFFFFFFF);
            play.setBackground(new android.graphics.drawable.RippleDrawable(Ui.csl(0x40FFFFFF), Ui.oval(Ui.PRIMARY), null));
            play.setElevation(Ui.dp(6));
            int pp = Ui.dp(20);
            play.setPadding(pp, pp, pp, pp);
            play.setOnClickListener(v -> Player.toggle());
            play.setContentDescription("נגן / השהה");
            ImageView next = Ui.iconBtn(a, R.drawable.ic_next, Ui.TEXT, "הבא", v -> Player.next());
            repeat = Ui.iconBtn(a, R.drawable.ic_repeat, Ui.TEXT2, "חזרה", v -> {
                Player.cycleRepeat();
                a.toast(Player.repeat == Player.REPEAT_OFF ? "ללא חזרה" : Player.repeat == Player.REPEAT_ALL ? "חזרה על הרשימה" : "חזרה על השיר");
            });
            prev.setPadding(Ui.dp(10), Ui.dp(10), Ui.dp(10), Ui.dp(10));
            next.setPadding(Ui.dp(10), Ui.dp(10), Ui.dp(10), Ui.dp(10));
            ctl.addView(shuffle);
            ctl.addView(prev, Ui.margins(Ui.lp(Ui.dp(56), Ui.dp(56)), 14, 0, 0, 0));
            ctl.addView(play, Ui.margins(Ui.lp(Ui.dp(76), Ui.dp(76)), 18, 0, 18, 0));
            ctl.addView(next, Ui.margins(Ui.lp(Ui.dp(56), Ui.dp(56)), 0, 0, 14, 0));
            ctl.addView(repeat);
            addView(ctl, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 10, 0, 0));

            // chips
            LinearLayout chips = Ui.hbox(a);
            chips.setGravity(Gravity.CENTER);
            Ui.pad(chips, 16, 14, 16, 22);
            speedChip = chip(R.drawable.ic_speed, v -> speedSheet());
            sleepChip = chip(R.drawable.ic_timer, v -> sleepSheet());
            queueChip = chip(R.drawable.ic_queue, v -> queueSheet(a));
            chips.addView((View) speedChip.getParent());
            chips.addView((View) sleepChip.getParent());
            chips.addView((View) queueChip.getParent());
            addView(chips);
            changed();
        }

        private TextView chip(int icon, OnClickListener l) {
            LinearLayout c = Ui.hbox(a);
            c.setBackground(Ui.ripple(Ui.stroke(Ui.SURFACE, Ui.DIVIDER, 18), 18));
            Ui.pad(c, 12, 0, 14, 0);
            c.addView(Ui.img(a, icon, Ui.TEXT2), Ui.lp(Ui.dp(18), Ui.dp(18)));
            TextView t = Ui.text(a, "", 13, Ui.TEXT, Ui.MEDIUM);
            t.setPaddingRelative(Ui.dp(6), 0, 0, 0);
            t.setTextDirection(TEXT_DIRECTION_RTL);
            c.addView(t);
            c.setOnClickListener(l);
            c.setLayoutParams(Ui.margins(Ui.lp(Ui.WRAP, Ui.dp(36)), 4, 0, 4, 0));
            return t;
        }

        @Override
        public void changed() {
            if (!Player.active()) {
                if (a.top() == this) a.onBackPressed();
                return;
            }
            title.setText(Player.title);
            String s = Player.artist.isEmpty() ? Player.album : Player.artist + (Player.album.isEmpty() ? "" : " · " + Player.album);
            sub.setText(s);
            File f = Player.current();
            from.setText(f != null && f.getParentFile() != null ? f.getParentFile().getName() : "");
            art[0].setImageBitmap(Player.art);
            play.setImageResource(Player.playing() ? R.drawable.ic_pause : R.drawable.ic_play);
            shuffle.setImageTintList(Ui.csl(Player.shuffle ? Ui.PRIMARY : Ui.TEXT3));
            repeat.setImageResource(Player.repeat == Player.REPEAT_ONE ? R.drawable.ic_repeat_one : R.drawable.ic_repeat);
            repeat.setImageTintList(Ui.csl(Player.repeat == Player.REPEAT_OFF ? Ui.TEXT3 : Ui.PRIMARY));
            dur.setText(Player.time(Player.duration()));
            seek.setMax(Math.max(1, Player.duration()));
            speedChip.setText(fmtSpeed(Player.speed));
            sleepChip.setText(Player.sleepAt > 0 ? "שינה" : "טיימר");
            queueChip.setText((Player.idx + 1) + "/" + Player.queue.size());
        }

        private void speedSheet() {
            float[] sp = {0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f};
            ArrayList<Dialogs.Item> m = new ArrayList<>();
            for (float s : sp)
                m.add(new Dialogs.Item(Math.abs(Player.speed - s) < 0.01 ? R.drawable.ic_check : 0, fmtSpeed(s) + (s == 1f ? "  (רגיל)" : ""), () -> Player.setSpeed(s)));
            Dialogs.sheet(a, "מהירות ניגון", null, m);
        }

        private void sleepSheet() {
            int[] mins = {15, 30, 45, 60, 90};
            ArrayList<Dialogs.Item> m = new ArrayList<>();
            if (Player.sleepAt > 0) m.add(new Dialogs.Item(R.drawable.ic_close, "ביטול הטיימר", () -> Player.sleepIn(0)));
            for (int mi : mins) m.add(new Dialogs.Item(R.drawable.ic_timer, mi + " דקות", () -> {
                Player.sleepIn(mi * 60_000L);
                a.toast("המוזיקה תיעצר בעוד " + mi + " דקות");
            }));
            int left = Player.duration() - Player.position();
            if (left > 0) m.add(new Dialogs.Item(R.drawable.ic_audio, "בסוף השיר הנוכחי",
                    () -> Player.sleepIn((long) (left / Math.max(0.25f, Player.speed)) + 300)));
            Dialogs.sheet(a, "טיימר שינה", "עצירה אוטומטית של המוזיקה", m);
        }

        private void more() {
            File f = Player.current();
            if (f == null) return;
            ArrayList<Dialogs.Item> m = new ArrayList<>();
            m.add(new Dialogs.Item(R.drawable.ic_folder, "פתח את התיקיה", () -> a.push(BrowserScreen.dir(a, f.getParentFile()))));
            m.add(new Dialogs.Item(R.drawable.ic_share, "שיתוף", () -> Fx.share(a, java.util.Collections.singletonList(f))));
            m.add(new Dialogs.Item(R.drawable.ic_info, "פרטים", () -> Dialogs.details(a, java.util.Collections.singletonList(f))));
            m.add(new Dialogs.Item(R.drawable.ic_open, "פתח באמצעות…", () -> Fx.open(a, f, true)));
            m.add(new Dialogs.Item(R.drawable.ic_close, "עצירה וסגירת הנגן", Player::stop).danger());
            Dialogs.sheet(a, Player.title, f.getName(), m);
        }

        @Override public boolean onBack() { return false; }

        @Override public void onShow() { a.setBars(artColorTop(), true); }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Player.add(this);
            onShow();
            changed();
            tick.run();
        }

        @Override
        protected void onDetachedFromWindow() {
            Player.remove(this);
            MainActivity.H.removeCallbacks(tick);
            super.onDetachedFromWindow();
        }
    }

    static String fmtSpeed(float s) {
        String t = s == (int) s ? String.valueOf((int) s) : String.valueOf(s);
        return "⁦" + t + "x⁩";
    }

    // ================================================================= queue

    static void queueSheet(MainActivity a) {
        LinearLayout box = Ui.vbox(a);
        Ui.pad(box, 0, 10, 0, 6);
        View handle = new View(a);
        handle.setBackground(Ui.round(0xFFD5DBE3, 3));
        LinearLayout.LayoutParams hp = Ui.lp(Ui.dp(40), Ui.dp(4));
        hp.gravity = Gravity.CENTER_HORIZONTAL;
        box.addView(handle, hp);
        TextView t = Ui.text(a, "רשימת השמעה · " + Fx.num(Player.queue.size()), 16.5f, Ui.TEXT, Ui.MEDIUM);
        Ui.pad(t, 22, 10, 22, 8);
        box.addView(t);
        ListView lv = new ListView(a);
        lv.setDivider(null);
        lv.setSelector(new android.graphics.drawable.ColorDrawable(0));
        Dialog d = Dialogs.make(a, box, true);
        BaseAdapter ad = new BaseAdapter() {
            @Override public int getCount() { return Player.queue.size(); }

            @Override public Object getItem(int p) { return Player.queue.get(p); }

            @Override public long getItemId(int p) { return p; }

            @Override
            public View getView(int p, View cv, ViewGroup parent) {
                LinearLayout r = (LinearLayout) cv;
                if (r == null) {
                    r = Ui.hbox(a);
                    Ui.pad(r, 22, 0, 8, 0);
                    r.setLayoutParams(new android.widget.AbsListView.LayoutParams(Ui.MATCH, Ui.dp(56)));
                    r.addView(Ui.img(a, R.drawable.ic_audio, Ui.TEXT3), Ui.lp(Ui.dp(20), Ui.dp(20)));
                    TextView n = Ui.line(a, "", 15, Ui.TEXT, Ui.REGULAR);
                    n.setPaddingRelative(Ui.dp(16), 0, Ui.dp(8), 0);
                    r.addView(n, Ui.lp(0, Ui.WRAP, 1));
                    r.addView(Ui.iconBtn(a, R.drawable.ic_close, Ui.TEXT3, "הסר מהרשימה", null));
                }
                boolean cur = p == Player.idx;
                ImageView ic = (ImageView) r.getChildAt(0);
                ic.setImageTintList(Ui.csl(cur ? Ui.PRIMARY : Ui.TEXT3));
                TextView n = (TextView) r.getChildAt(1);
                n.setText(Fx.base(Player.queue.get(p).getName()));
                n.setTextColor(cur ? Ui.PRIMARY : Ui.TEXT);
                n.setTypeface(cur ? Ui.MEDIUM : Ui.REGULAR);
                r.setBackgroundColor(cur ? Ui.PRIMARY_SOFT : 0);
                final int pos = p;
                r.getChildAt(2).setOnClickListener(v -> {
                    Player.removeAt(pos);
                    notifyDataSetChanged();
                    if (!Player.active()) d.dismiss();
                });
                return r;
            }
        };
        lv.setAdapter(ad);
        lv.setOnItemClickListener((parent, view, p, id) -> {
            Player.playAt(p);
            ad.notifyDataSetChanged();
        });
        int h = (int) (a.getResources().getDisplayMetrics().heightPixels * 0.6f);
        box.addView(lv, Ui.lp(Ui.MATCH, Math.min(h, Ui.dp(56) * Math.max(1, Player.queue.size()))));
        Dialogs.show(d, true);
        lv.setSelection(Math.max(0, Player.idx - 2));
    }

    // ================================================================= mini player

    static final class Mini extends LinearLayout implements Player.Listener {
        private final MainActivity a;
        private final ImageView[] art = {null};
        private final TextView title, sub;
        private final ImageView play;
        private final View progress;
        private final Runnable tick = new Runnable() {
            @Override public void run() {
                if (!isAttachedToWindow() || getVisibility() != VISIBLE) return;
                updateProgress();
                MainActivity.H.postDelayed(this, 500);
            }
        };

        Mini(MainActivity a) {
            super(a);
            this.a = a;
            setOrientation(VERTICAL);
            setBackgroundColor(Ui.SURFACE);
            setElevation(Ui.dp(12));
            FrameLayout track = new FrameLayout(a);
            track.setBackgroundColor(Ui.DIVIDER);
            progress = new View(a);
            progress.setBackgroundColor(Ui.PRIMARY);
            track.addView(progress, new FrameLayout.LayoutParams(0, Ui.MATCH, Gravity.START));
            addView(track, Ui.lp(Ui.MATCH, Ui.dp(2)));
            LinearLayout row = Ui.hbox(a);
            Ui.pad(row, 12, 0, 4, 0);
            row.setBackground(Ui.rippleRect());
            FrameLayout artBox = artView(a, 10, 20, art);
            row.addView(artBox, Ui.lp(Ui.dp(44), Ui.dp(44)));
            LinearLayout t = Ui.vbox(a);
            t.setPaddingRelative(Ui.dp(12), 0, Ui.dp(4), 0);
            title = Ui.line(a, "", 15, Ui.TEXT, Ui.MEDIUM);
            sub = Ui.line(a, "", 12.5f, Ui.TEXT2, Ui.REGULAR);
            t.addView(title);
            t.addView(sub);
            row.addView(t, Ui.lp(0, Ui.WRAP, 1));
            play = Ui.iconBtn(a, R.drawable.ic_play, Ui.TEXT, "נגן / השהה", v -> Player.toggle());
            row.addView(play);
            row.addView(Ui.iconBtn(a, R.drawable.ic_next, Ui.TEXT, "הבא", v -> Player.next()));
            row.addView(Ui.iconBtn(a, R.drawable.ic_close, Ui.TEXT3, "סגור", v -> Player.stop()));
            row.setOnClickListener(v -> a.openMusic());
            addView(row, Ui.lp(Ui.MATCH, Ui.dp(62)));
            setVisibility(GONE);
        }

        private void updateProgress() {
            int d = Player.duration();
            int w = getWidth();
            ViewGroup.LayoutParams lp = progress.getLayoutParams();
            lp.width = d > 0 ? (int) ((long) w * Player.position() / d) : 0;
            progress.setLayoutParams(lp);
        }

        void refresh() {
            View top = a.top();
            boolean show = Player.active() && !(top instanceof Screen) && !(top instanceof VideoPlayer)
                    && !(top instanceof Viewers.Image);
            boolean was = getVisibility() == VISIBLE;
            setVisibility(show ? VISIBLE : GONE);
            if (!show) return;
            title.setText(Player.title);
            sub.setText(Player.artist.isEmpty() ? Player.album : Player.artist);
            art[0].setImageBitmap(Player.art);
            play.setImageResource(Player.playing() ? R.drawable.ic_pause : R.drawable.ic_play);
            if (!was) {
                MainActivity.H.removeCallbacks(tick);
                post(tick);
            }
        }

        @Override public void changed() { refresh(); }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Player.add(this);
            refresh();
        }

        @Override
        protected void onDetachedFromWindow() {
            Player.remove(this);
            super.onDetachedFromWindow();
        }
    }
}
