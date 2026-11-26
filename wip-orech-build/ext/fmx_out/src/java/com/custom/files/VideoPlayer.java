package com.custom.files;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.media.TimedText;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Full-screen video player.
 * Gestures: tap = controls · double-tap sides = ±10 s · drag sideways = seek ·
 * drag up/down on the right = volume, on the left = brightness · hold = 2× speed.
 */
final class VideoPlayer extends FrameLayout implements MainActivity.Screen, Viewers.Closeable, TextureView.SurfaceTextureListener {
    private static final int FIT = 0, FILL = 1, STRETCH = 2;
    private static final String[] FIT_NAMES = {"התאמה למסך", "מילוי המסך", "מתיחה"};

    private final MainActivity a;
    private final ArrayList<File> list = new ArrayList<>();
    private int idx;
    private final TextureView tv;
    private MediaPlayer mp;
    private Surface surface;
    private boolean prepared, wantPlay = true, locked, shown, dragging, closed, subsOn = true, hasSubs;
    private int vw, vh, fit = FIT;
    private float speed = 1f;
    private final SharedPreferences resume;
    private AudioManager am;
    private AudioFocusRequest focus;

    private final FrameLayout controls;
    private final TextView title, counter, pos, dur, subs, speedBtn;
    private TextView center;
    private ImageView unlockBtn;
    private final SeekBar seek;
    private final ImageView play, lockBtn, subsBtn, prevBtn, nextBtn;
    private final ProgressBar loading;

    private final Runnable autoHide = () -> { if (playing() && !dragging) showControls(false); };
    private final Runnable hideCenter = () -> center.animate().alpha(0f).setDuration(200).start();
    private final Runnable hideUnlock = () -> unlockBtn.setVisibility(GONE);
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (closed) return;
            if (prepared && !dragging && shown) {
                int p = mp.getCurrentPosition();
                seek.setProgress(p);
                pos.setText(Player.time(p));
            }
            MainActivity.H.postDelayed(this, 300);
        }
    };

    VideoPlayer(MainActivity a, FileItem it, List<FileItem> siblings) {
        super(a);
        this.a = a;
        resume = a.getSharedPreferences("resume", 0);
        setBackgroundColor(0xFF000000);
        setKeepScreenOn(true);
        for (FileItem s : siblings) if (s.kind == Fx.K_VIDEO) list.add(s.file);
        if (!list.contains(it.file)) list.add(0, it.file);
        idx = list.indexOf(it.file);

        tv = new TextureView(a);
        tv.setSurfaceTextureListener(this);
        addView(tv, new LayoutParams(Ui.MATCH, Ui.MATCH));
        addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> applyFit());

        subs = Ui.text(a, "", 17, 0xFFFFFFFF);
        subs.setGravity(Gravity.CENTER);
        subs.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        subs.setTextDirection(TEXT_DIRECTION_FIRST_STRONG);
        subs.setShadowLayer(Ui.dp(3), 0, Ui.dp(1), 0xFF000000);
        subs.setBackground(Ui.round(0x66000000, 6));
        Ui.pad(subs, 8, 2, 8, 2);
        subs.setVisibility(GONE);
        LayoutParams sp = new LayoutParams(Ui.WRAP, Ui.WRAP, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        sp.bottomMargin = Ui.dp(36);
        sp.leftMargin = sp.rightMargin = Ui.dp(24);
        addView(subs, sp);

        loading = new ProgressBar(a);
        loading.setIndeterminateTintList(Ui.csl(0xFFFFFFFF));
        addView(loading, new LayoutParams(Ui.dp(44), Ui.dp(44), Gravity.CENTER));

        center = Ui.text(a, "", 20, 0xFFFFFFFF, Ui.MEDIUM);
        center.setBackground(Ui.round(0x99000000, 16));
        Ui.pad(center, 20, 12, 20, 12);
        center.setTextDirection(TEXT_DIRECTION_RTL);
        center.setAlpha(0f);
        addView(center, new LayoutParams(Ui.WRAP, Ui.WRAP, Gravity.CENTER));

        // ---------------- controls overlay
        controls = new FrameLayout(a);
        addView(controls, new LayoutParams(Ui.MATCH, Ui.MATCH));

        LinearLayout top = Ui.hbox(a);
        top.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xCC000000, 0x00000000}));
        Ui.pad(top, 4, 8, 4, 24);
        top.addView(Ui.iconBtn(a, R.drawable.ic_back, 0xFFFFFFFF, "חזרה", v -> a.onBackPressed()));
        LinearLayout tt = Ui.vbox(a);
        title = Ui.line(a, "", 16.5f, 0xFFFFFFFF, Ui.MEDIUM);
        counter = Ui.line(a, "", 12, 0xB3FFFFFF, Ui.REGULAR);
        counter.setTextDirection(TEXT_DIRECTION_RTL);
        tt.addView(title);
        tt.addView(counter);
        top.addView(tt, Ui.lp(0, Ui.WRAP, 1));
        subsBtn = Ui.iconBtn(a, R.drawable.ic_subtitles, 0xFFFFFFFF, "כתוביות", v -> toggleSubs());
        top.addView(subsBtn);
        speedBtn = Ui.text(a, "1x", 14, 0xFFFFFFFF, Ui.MEDIUM);
        speedBtn.setGravity(Gravity.CENTER);
        speedBtn.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        speedBtn.setBackground(Ui.rippleBorderless());
        speedBtn.setOnClickListener(v -> speedSheet());
        speedBtn.setContentDescription("מהירות");
        top.addView(speedBtn, Ui.lp(Ui.dp(48), Ui.dp(48)));
        top.addView(Ui.iconBtn(a, R.drawable.ic_aspect, 0xFFFFFFFF, "גודל תמונה", v -> cycleFit()));
        top.addView(Ui.iconBtn(a, R.drawable.ic_rotate, 0xFFFFFFFF, "סיבוב", v -> rotate()));
        top.addView(Ui.iconBtn(a, R.drawable.ic_more, 0xFFFFFFFF, "עוד", v -> more()));
        controls.addView(top, new LayoutParams(Ui.MATCH, Ui.WRAP, Gravity.TOP));

        // center transport (time direction is left → right)
        LinearLayout mid = Ui.hbox(a);
        mid.setGravity(Gravity.CENTER);
        mid.setLayoutDirection(LAYOUT_DIRECTION_LTR);
        prevBtn = roundBtn(R.drawable.ic_prev, 52, 13, "הקודם", v -> go(idx - 1));
        ImageView rw = roundBtn(R.drawable.ic_rewind, 52, 13, "10 שניות אחורה", v -> skip(-10_000));
        play = roundBtn(R.drawable.ic_pause, 72, 18, "נגן / השהה", v -> togglePlay());
        play.setBackground(Ui.oval(0x66000000));
        ImageView ff = roundBtn(R.drawable.ic_ffwd, 52, 13, "10 שניות קדימה", v -> skip(10_000));
        nextBtn = roundBtn(R.drawable.ic_next, 52, 13, "הבא", v -> go(idx + 1));
        mid.addView(prevBtn);
        mid.addView(rw, Ui.margins(Ui.lp(Ui.dp(52), Ui.dp(52)), 14, 0, 0, 0));
        mid.addView(play, Ui.margins(Ui.lp(Ui.dp(72), Ui.dp(72)), 22, 0, 22, 0));
        mid.addView(ff, Ui.margins(Ui.lp(Ui.dp(52), Ui.dp(52)), 0, 0, 14, 0));
        mid.addView(nextBtn);
        controls.addView(mid, new LayoutParams(Ui.WRAP, Ui.WRAP, Gravity.CENTER));

        LinearLayout bottom = Ui.vbox(a);
        bottom.setLayoutDirection(LAYOUT_DIRECTION_LTR);
        bottom.setBackground(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xCC000000, 0x00000000}));
        Ui.pad(bottom, 8, 28, 8, 10);
        LinearLayout row = Ui.hbox(a);
        lockBtn = Ui.iconBtn(a, R.drawable.ic_unlock, 0xFFFFFFFF, "נעילת מסך", v -> setLocked(true));
        row.addView(lockBtn);
        pos = Ui.text(a, "0:00", 13, 0xFFFFFFFF, Ui.MEDIUM);
        row.addView(pos);
        seek = new SeekBar(a);
        seek.setProgressTintList(Ui.csl(Ui.AMBER));
        seek.setThumbTintList(Ui.csl(Ui.AMBER));
        seek.setProgressBackgroundTintList(Ui.csl(0x80FFFFFF));
        row.addView(seek, Ui.lp(0, Ui.WRAP, 1));
        dur = Ui.text(a, "0:00", 13, 0xFFFFFFFF, Ui.MEDIUM);
        row.addView(dur);
        row.addView(new View(a), Ui.lp(Ui.dp(12), 1));
        bottom.addView(row);
        controls.addView(bottom, new LayoutParams(Ui.MATCH, Ui.WRAP, Gravity.BOTTOM));
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean user) {
                if (user) { pos.setText(Player.time(p)); flash(Player.time(p) + " / " + Player.time(s.getMax())); }
            }

            @Override public void onStartTrackingTouch(SeekBar s) { dragging = true; MainActivity.H.removeCallbacks(autoHide); }

            @Override public void onStopTrackingTouch(SeekBar s) {
                dragging = false;
                if (prepared) mp.seekTo(s.getProgress());
                scheduleHide();
            }
        });

        unlockBtn = roundBtn(R.drawable.ic_lock, 56, 14, "ביטול נעילה", v -> setLocked(false));
        unlockBtn.setBackground(Ui.oval(0x80000000));
        LayoutParams up = new LayoutParams(Ui.dp(56), Ui.dp(56), Gravity.CENTER_VERTICAL | Gravity.LEFT);
        up.leftMargin = Ui.dp(24);
        unlockBtn.setVisibility(GONE);
        addView(unlockBtn, up);

        setupGestures();
        showControls(true);
        tick.run();
    }

    private ImageView roundBtn(int icon, int size, int pad, String desc, OnClickListener l) {
        ImageView b = Ui.img(a, icon, 0xFFFFFFFF);
        b.setBackground(Ui.rippleBorderless());
        int p = Ui.dp(pad);
        b.setPadding(p, p, p, p);
        b.setContentDescription(desc);
        b.setOnClickListener(l);
        b.setLayoutParams(Ui.lp(Ui.dp(size), Ui.dp(size)));
        return b;
    }

    // ------------------------------------------------------------ playback

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) {
        surface = new Surface(st);
        load(idx);
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) { applyFit(); }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture st) { }

    private boolean playing() {
        try { return prepared && mp.isPlaying(); } catch (Exception e) { return false; }
    }

    private void load(int i) {
        if (i < 0 || i >= list.size() || surface == null) return;
        savePosition();
        idx = i;
        releasePlayer();
        File f = list.get(i);
        title.setText(f.getName());
        counter.setText(list.size() > 1 ? (i + 1) + " מתוך " + list.size() : "");
        prevBtn.setAlpha(i > 0 ? 1f : 0.35f);
        nextBtn.setAlpha(i + 1 < list.size() ? 1f : 0.35f);
        subs.setVisibility(GONE);
        hasSubs = false;
        subsBtn.setVisibility(GONE);
        loading.setVisibility(VISIBLE);
        if (Player.playing()) Player.pause();
        try {
            mp = new MediaPlayer();
            mp.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).build());
            mp.setSurface(surface);
            mp.setDataSource(f.getPath());
            mp.setOnVideoSizeChangedListener((p, w, h) -> {
                vw = w;
                vh = h;
                applyFit();
            });
            mp.setOnPreparedListener(p -> onPrepared(f));
            mp.setOnCompletionListener(p -> {
                resume.edit().remove(f.getPath()).apply();
                if (idx + 1 < list.size()) {
                    flash("הבא: " + list.get(idx + 1).getName());
                    go(idx + 1);
                } else {
                    play.setImageResource(R.drawable.ic_play);
                    showControls(true);
                }
            });
            mp.setOnErrorListener((p, w, e) -> {
                loading.setVisibility(GONE);
                a.toast("לא ניתן לנגן את הסרטון — נפתח באפליקציה אחרת");
                a.onBackPressed();
                Fx.open(a, f, false);
                return true;
            });
            mp.setOnTimedTextListener((p, text) -> subs.post(() -> showSub(text)));
            mp.setOnInfoListener((p, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) loading.setVisibility(VISIBLE);
                else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END || what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                    loading.setVisibility(GONE);
                return false;
            });
            mp.prepareAsync();
        } catch (Exception e) {
            a.toast("לא ניתן לפתוח את הסרטון");
        }
    }

    private void onPrepared(File f) {
        if (closed) return;
        prepared = true;
        loading.setVisibility(GONE);
        vw = mp.getVideoWidth();
        vh = mp.getVideoHeight();
        applyFit();
        int d = mp.getDuration();
        seek.setMax(Math.max(1, d));
        dur.setText(Player.time(d));
        // landscape videos open in landscape
        if (vw > vh && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        int saved = resume.getInt(f.getPath(), 0);
        if (saved > 5000 && saved < d - 10_000) {
            mp.seekTo(saved);
            flash("ממשיך מ-" + Player.time(saved));
        }
        attachSubtitles(f);
        if (speed != 1f) applySpeed();
        if (wantPlay) start();
        scheduleHide();
    }

    private void start() {
        if (!prepared) return;
        requestAudio();
        mp.start();
        play.setImageResource(R.drawable.ic_pause);
        scheduleHide();
    }

    private void pause() {
        if (prepared && mp.isPlaying()) mp.pause();
        play.setImageResource(R.drawable.ic_play);
        savePosition();
        MainActivity.H.removeCallbacks(autoHide);
    }

    private void togglePlay() {
        if (!prepared) return;
        if (mp.isPlaying()) { wantPlay = false; pause(); showControls(true); }
        else { wantPlay = true; start(); }
    }

    private void skip(int ms) {
        if (!prepared) return;
        int p = Math.max(0, Math.min(mp.getDuration(), mp.getCurrentPosition() + ms));
        mp.seekTo(p);
        flash((ms > 0 ? "‎+" : "‎−") + Math.abs(ms / 1000) + " שניות");
        scheduleHide();
    }

    private void go(int i) {
        if (i < 0 || i >= list.size()) return;
        wantPlay = true;
        load(i);
    }

    private void savePosition() {
        if (!prepared || mp == null) return;
        try {
            int p = mp.getCurrentPosition(), d = mp.getDuration();
            String k = list.get(idx).getPath();
            if (p > 5000 && p < d - 10_000) resume.edit().putInt(k, p).apply();
            else resume.edit().remove(k).apply();
        } catch (Exception ignored) { }
    }

    private void releasePlayer() {
        prepared = false;
        if (mp != null) {
            try { mp.release(); } catch (Exception ignored) { }
            mp = null;
        }
    }

    private void applySpeed() {
        if (!prepared) return;
        try {
            boolean was = mp.isPlaying();
            PlaybackParams pp = mp.getPlaybackParams();
            mp.setPlaybackParams(pp.setSpeed(speed));
            if (!was) mp.pause();
        } catch (Exception ignored) { }
        speedBtn.setText(Music.fmtSpeed(speed));
    }

    private void requestAudio() {
        if (am == null) am = (AudioManager) a.getSystemService(Context.AUDIO_SERVICE);
        if (focus == null) focus = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(ch -> {
                    if (ch == AudioManager.AUDIOFOCUS_LOSS || ch == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) post(this::pause);
                }).build();
        am.requestAudioFocus(focus);
    }

    // ------------------------------------------------------------ picture size

    private void applyFit() {
        int w = tv.getWidth(), h = tv.getHeight();
        if (w == 0 || h == 0 || vw == 0 || vh == 0) return;
        Matrix m = new Matrix();
        if (fit != STRETCH) {
            float s = fit == FIT ? Math.min((float) w / vw, (float) h / vh) : Math.max((float) w / vw, (float) h / vh);
            m.setScale(vw * s / w, vh * s / h, w / 2f, h / 2f);
        }
        tv.setTransform(m);
    }

    private void cycleFit() {
        fit = (fit + 1) % 3;
        applyFit();
        flash(FIT_NAMES[fit]);
        scheduleHide();
    }

    private void rotate() {
        boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        a.setRequestedOrientation(land ? ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        scheduleHide();
    }

    // ------------------------------------------------------------ subtitles

    private void attachSubtitles(File video) {
        File dir = video.getParentFile();
        if (dir == null) return;
        String base = Fx.base(video.getName()).toLowerCase(java.util.Locale.ROOT);
        File[] fs = dir.listFiles();
        File srt = null;
        if (fs != null) for (File f : fs) {
            String n = f.getName().toLowerCase(java.util.Locale.ROOT);
            if (n.endsWith(".srt") && n.startsWith(base)) { srt = f; break; }
        }
        if (srt == null) return;
        try {
            File src = toUtf8(srt);
            mp.addTimedTextSource(src.getPath(), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
            MediaPlayer.TrackInfo[] tr = mp.getTrackInfo();
            for (int i = tr.length - 1; i >= 0; i--) {
                if (tr[i].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT) {
                    mp.selectTrack(i);
                    hasSubs = true;
                    break;
                }
            }
        } catch (Exception ignored) { }
        subsBtn.setVisibility(hasSubs ? VISIBLE : GONE);
        subsBtn.setImageTintList(Ui.csl(subsOn ? 0xFFFFFFFF : 0x66FFFFFF));
    }

    /** Hebrew .srt files are often windows-1255; MediaPlayer wants UTF-8. */
    private File toUtf8(File srt) {
        try {
            byte[] data = Files.readAllBytes(srt.toPath());
            try {
                StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(data));
                return srt;
            } catch (Exception notUtf8) {
                String s = new String(data, Charset.forName("windows-1255"));
                File out = new File(a.getCacheDir(), "subs.srt");
                try (FileOutputStream o = new FileOutputStream(out)) { o.write(s.getBytes(StandardCharsets.UTF_8)); }
                return out;
            }
        } catch (Exception e) {
            return srt;
        }
    }

    private void showSub(TimedText t) {
        String s = t == null ? null : t.getText();
        if (!subsOn || s == null || s.trim().isEmpty()) { subs.setVisibility(GONE); return; }
        subs.setText(s.replaceAll("<[^>]+>", ""));
        subs.setVisibility(VISIBLE);
    }

    private void toggleSubs() {
        subsOn = !subsOn;
        if (!subsOn) subs.setVisibility(GONE);
        subsBtn.setImageTintList(Ui.csl(subsOn ? 0xFFFFFFFF : 0x66FFFFFF));
        flash(subsOn ? "כתוביות מופעלות" : "כתוביות כבויות");
    }

    // ------------------------------------------------------------ controls & menus

    private void showControls(boolean show) {
        shown = show;
        if (locked) show = false;
        controls.animate().cancel();
        if (show) {
            controls.setVisibility(VISIBLE);
            controls.animate().alpha(1f).setDuration(150).start();
        } else controls.animate().alpha(0f).setDuration(200).withEndAction(() -> controls.setVisibility(GONE)).start();
        immersive();
    }

    private void scheduleHide() {
        MainActivity.H.removeCallbacks(autoHide);
        MainActivity.H.postDelayed(autoHide, 3500);
    }

    private void flash(String s) {
        center.setText(s);
        center.animate().cancel();
        center.setAlpha(1f);
        MainActivity.H.removeCallbacks(hideCenter);
        MainActivity.H.postDelayed(hideCenter, 900);
    }

    private void setLocked(boolean l) {
        locked = l;
        if (l) {
            showControls(false);
            unlockBtn.setVisibility(VISIBLE);
            MainActivity.H.removeCallbacks(hideUnlock);
            MainActivity.H.postDelayed(hideUnlock, 2000);
            flash("המסך נעול");
        } else {
            unlockBtn.setVisibility(GONE);
            showControls(true);
            scheduleHide();
        }
    }

    private void speedSheet() {
        float[] sp = {0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f};
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        for (float s : sp)
            m.add(new Dialogs.Item(Math.abs(speed - s) < 0.01 ? R.drawable.ic_check : 0, Music.fmtSpeed(s) + (s == 1f ? "  (רגיל)" : ""), () -> {
                speed = s;
                applySpeed();
            }));
        Dialogs.sheet(a, "מהירות ניגון", null, m);
    }

    private void more() {
        File f = list.get(idx);
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        m.add(new Dialogs.Item(R.drawable.ic_share, "שיתוף", () -> Fx.share(a, Collections.singletonList(f))));
        m.add(new Dialogs.Item(R.drawable.ic_info, "פרטים", () -> Dialogs.details(a, Collections.singletonList(f))));
        m.add(new Dialogs.Item(R.drawable.ic_open, "פתח באמצעות…", () -> {
            pause();
            Fx.open(a, f, true);
        }));
        Dialogs.sheet(a, f.getName(), null, m);
    }

    private void immersive() {
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        a.getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    // ------------------------------------------------------------ gestures

    private static final int G_NONE = 0, G_SEEK = 1, G_VOLUME = 2, G_BRIGHT = 3;
    private int gMode, gStartPos, gTarget, gStartVol;
    private float gx, gy, gStartBright;
    private boolean boosted;
    private GestureDetector gd;

    private void setupGestures() {
        gd = new GestureDetector(a, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onDown(MotionEvent e) { return true; }

            @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                if (locked) {
                    unlockBtn.setVisibility(VISIBLE);
                    MainActivity.H.removeCallbacks(hideUnlock);
                    MainActivity.H.postDelayed(hideUnlock, 2000);
                    return true;
                }
                showControls(!shown);
                if (shown) scheduleHide();
                return true;
            }

            @Override public boolean onDoubleTap(MotionEvent e) {
                if (locked) return true;
                float x = e.getX(), w = getWidth();
                if (x < w / 3f) skip(-10_000);
                else if (x > w * 2 / 3f) skip(10_000);
                else togglePlay();
                return true;
            }

            @Override public void onLongPress(MotionEvent e) {
                if (locked || !playing() || gMode != G_NONE) return;
                boosted = true;
                float old = speed;
                speed = 2f;
                applySpeed();
                speed = old;
                center.setText("2x ▶▶");
                center.animate().cancel();
                center.setAlpha(1f);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        gd.onTouchEvent(e);
        if (locked || !prepared) return true;
        int slop = ViewConfiguration.get(a).getScaledTouchSlop() * 2;
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                gx = e.getX();
                gy = e.getY();
                gMode = G_NONE;
                gStartPos = mp.getCurrentPosition();
                if (am == null) am = (AudioManager) a.getSystemService(Context.AUDIO_SERVICE);
                gStartVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                float b = a.getWindow().getAttributes().screenBrightness;
                gStartBright = b < 0 ? 0.5f : b;
                break;
            case MotionEvent.ACTION_MOVE: {
                if (boosted) break;
                float dx = e.getX() - gx, dy = e.getY() - gy;
                if (gMode == G_NONE) {
                    if (Math.abs(dx) > slop && Math.abs(dx) > Math.abs(dy)) gMode = G_SEEK;
                    else if (Math.abs(dy) > slop) gMode = gx > getWidth() / 2f ? G_VOLUME : G_BRIGHT;
                }
                if (gMode == G_SEEK) {
                    int d = mp.getDuration();
                    int range = Math.min(d, 120_000);
                    gTarget = Math.max(0, Math.min(d, gStartPos + (int) (dx / getWidth() * range)));
                    int diff = (gTarget - gStartPos) / 1000;
                    center.setText((diff >= 0 ? "‎+" : "‎−") + Player.time(Math.abs(diff) * 1000) + "   " + Player.time(gTarget) + " / " + Player.time(d));
                    center.animate().cancel();
                    center.setAlpha(1f);
                } else if (gMode == G_VOLUME) {
                    int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int v = Math.max(0, Math.min(max, gStartVol + Math.round(-dy / (getHeight() * 0.6f) * max)));
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
                    center.setText("עוצמה " + (v * 100 / Math.max(1, max)) + "%");
                    center.animate().cancel();
                    center.setAlpha(1f);
                } else if (gMode == G_BRIGHT) {
                    float v = Math.max(0.01f, Math.min(1f, gStartBright - dy / (getHeight() * 0.6f)));
                    WindowManager.LayoutParams lp = a.getWindow().getAttributes();
                    lp.screenBrightness = v;
                    a.getWindow().setAttributes(lp);
                    center.setText("בהירות " + Math.round(v * 100) + "%");
                    center.animate().cancel();
                    center.setAlpha(1f);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (boosted) {
                    boosted = false;
                    applySpeed();
                }
                if (gMode == G_SEEK) mp.seekTo(gTarget);
                if (gMode != G_NONE || center.getAlpha() > 0) {
                    MainActivity.H.removeCallbacks(hideCenter);
                    MainActivity.H.postDelayed(hideCenter, 500);
                }
                gMode = G_NONE;
                break;
        }
        return true;
    }

    // ------------------------------------------------------------ lifecycle

    @Override
    public boolean onBack() {
        if (locked) {
            unlockBtn.setVisibility(VISIBLE);
            a.toast("המסך נעול — הקישו על המנעול לביטול");
            return true;
        }
        return false;
    }

    @Override
    public void onShow() {
        a.setBars(0xFF000000, false);
        immersive();
        if (prepared && wantPlay && !playing() && !closed) start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onShow();
    }

    @Override
    public void pauseHint() {
        if (playing()) {
            pause();
            wantPlay = true; // resume when the user comes back
        }
    }

    @Override
    public void close() {
        closed = true;
        savePosition();
        releasePlayer();
        MainActivity.H.removeCallbacks(tick);
        MainActivity.H.removeCallbacks(autoHide);
        if (am != null && focus != null) am.abandonAudioFocusRequest(focus);
        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        a.getWindow().setAttributes(lp);
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (surface != null) surface.release();
    }
}
