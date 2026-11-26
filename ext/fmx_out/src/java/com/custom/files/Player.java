package com.custom.files;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.PlaybackParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Music playback engine (one per process). Survives leaving the screen; MusicService keeps the
 * process alive and shows the media notification / lock-screen controls.
 */
final class Player {
    static final int REPEAT_OFF = 0, REPEAT_ALL = 1, REPEAT_ONE = 2;

    interface Listener { void changed(); }

    static final ArrayList<File> queue = new ArrayList<>();
    static int idx = -1;
    static int repeat = REPEAT_ALL;
    static boolean shuffle;
    static float speed = 1f;
    static String title = "", artist = "", album = "";
    static Bitmap art;
    static long sleepAt; // 0 = no sleep timer

    private static MediaPlayer mp;
    private static boolean prepared, wantPlay;
    private static Context app;
    private static AudioManager am;
    private static AudioFocusRequest focus;
    private static boolean noisyRegistered, pausedByFocus;
    private static final ArrayList<Listener> listeners = new ArrayList<>();
    private static final Random rnd = new Random();
    private static int metaGen;

    private static final BroadcastReceiver noisy = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) { pause(); } // headphones unplugged
    };

    private static final Runnable sleeper = () -> {
        sleepAt = 0;
        pause();
        notifyAll2();
    };

    static void add(Listener l) { if (!listeners.contains(l)) listeners.add(l); }

    static void remove(Listener l) { listeners.remove(l); }

    private static void notifyAll2() {
        for (Listener l : new ArrayList<>(listeners)) l.changed();
    }

    static boolean active() { return idx >= 0 && idx < queue.size(); }

    static File current() { return active() ? queue.get(idx) : null; }

    static boolean playing() {
        try { return mp != null && prepared && mp.isPlaying(); } catch (Exception e) { return false; }
    }

    static int position() {
        try { return mp != null && prepared ? mp.getCurrentPosition() : 0; } catch (Exception e) { return 0; }
    }

    static int duration() {
        try { return mp != null && prepared ? mp.getDuration() : 0; } catch (Exception e) { return 0; }
    }

    // ------------------------------------------------------------ control

    static void play(Context c, List<File> list, int index) {
        app = c.getApplicationContext();
        queue.clear();
        queue.addAll(list);
        load(index, true);
        c.startService(new Intent(app, MusicService.class));
    }

    static void playAt(int index) { load(index, true); }

    private static void load(int index, boolean autoplay) {
        if (index < 0 || index >= queue.size()) return;
        idx = index;
        release();
        File f = queue.get(idx);
        title = Fx.base(f.getName());
        artist = "";
        album = f.getParentFile() == null ? "" : f.getParentFile().getName();
        art = null;
        wantPlay = autoplay;
        try {
            mp = new MediaPlayer();
            mp.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            mp.setWakeMode(app, android.os.PowerManager.PARTIAL_WAKE_LOCK);
            mp.setDataSource(f.getPath());
            mp.setOnPreparedListener(p -> {
                prepared = true;
                applySpeed();
                if (wantPlay) start();
                notifyAll2();
            });
            mp.setOnCompletionListener(p -> onComplete());
            mp.setOnErrorListener((p, w, e) -> {
                prepared = false;
                if (queue.size() > 1) MainActivity.H.postDelayed(Player::next, 300);
                return true;
            });
            mp.prepareAsync();
        } catch (Exception e) {
            prepared = false;
        }
        loadMeta(f);
        notifyAll2();
    }

    private static void loadMeta(File f) {
        final int g = ++metaGen;
        MainActivity.BG.execute(() -> {
            String t = null, ar = null, al = null;
            Bitmap b = null;
            MediaMetadataRetriever r = new MediaMetadataRetriever();
            try {
                r.setDataSource(f.getPath());
                t = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                ar = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                al = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                byte[] pic = r.getEmbeddedPicture();
                if (pic != null) {
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(pic, 0, pic.length, o);
                    int s = 1;
                    while (Math.max(o.outWidth, o.outHeight) / s > 900) s *= 2;
                    o.inJustDecodeBounds = false;
                    o.inSampleSize = s;
                    b = BitmapFactory.decodeByteArray(pic, 0, pic.length, o);
                }
            } catch (Exception ignored) {
            } finally {
                try { r.release(); } catch (Exception ignored) { }
            }
            final String ft = t, far = ar, fal = al;
            final Bitmap fb = b;
            MainActivity.H.post(() -> {
                if (g != metaGen) return;
                if (ft != null && !ft.trim().isEmpty()) title = ft.trim();
                if (far != null && !far.trim().isEmpty() && !far.contains("<unknown>")) artist = far.trim();
                if (fal != null && !fal.trim().isEmpty()) album = fal.trim();
                art = fb;
                notifyAll2();
            });
        });
    }

    private static void onComplete() {
        if (repeat == REPEAT_ONE) {
            seek(0);
            start();
            return;
        }
        int n = nextIndex();
        if (n < 0) {
            seek(0);
            pause();
            notifyAll2();
            return;
        }
        load(n, true);
    }

    private static int nextIndex() {
        if (queue.isEmpty()) return -1;
        if (shuffle && queue.size() > 1) {
            int n;
            do { n = rnd.nextInt(queue.size()); } while (n == idx);
            return n;
        }
        if (idx + 1 < queue.size()) return idx + 1;
        return repeat == REPEAT_ALL ? 0 : -1;
    }

    static void next() {
        int n = nextIndex();
        if (n < 0) n = 0;
        load(n, true);
    }

    static void prev() {
        if (position() > 3000) { seek(0); return; }
        int n = idx - 1;
        if (n < 0) n = repeat == REPEAT_ALL ? queue.size() - 1 : 0;
        load(n, true);
    }

    static void toggle() {
        if (playing()) pause();
        else start();
    }

    static void start() {
        wantPlay = true;
        if (mp == null) { if (active()) load(idx, true); return; }
        if (!prepared) return;
        if (!requestFocus()) return;
        mp.start();
        if (!noisyRegistered && app != null) {
            app.registerReceiver(noisy, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
            noisyRegistered = true;
        }
        notifyAll2();
    }

    static void pause() {
        wantPlay = false;
        try { if (mp != null && prepared && mp.isPlaying()) mp.pause(); } catch (Exception ignored) { }
        unregisterNoisy();
        notifyAll2();
    }

    static void seek(int ms) {
        try { if (mp != null && prepared) mp.seekTo(Math.max(0, ms)); } catch (Exception ignored) { }
        notifyAll2();
    }

    static void setSpeed(float s) {
        speed = s;
        applySpeed();
        notifyAll2();
    }

    private static void applySpeed() {
        if (mp == null || !prepared) return;
        try {
            boolean was = mp.isPlaying();
            PlaybackParams pp = mp.getPlaybackParams();
            mp.setPlaybackParams(pp.setSpeed(speed));
            if (!was) mp.pause(); // setPlaybackParams starts playback
        } catch (Exception ignored) { }
    }

    static void cycleRepeat() {
        repeat = (repeat + 1) % 3;
        notifyAll2();
    }

    static void toggleShuffle() {
        shuffle = !shuffle;
        notifyAll2();
    }

    static void sleepIn(long ms) {
        MainActivity.H.removeCallbacks(sleeper);
        if (ms <= 0) sleepAt = 0;
        else {
            sleepAt = System.currentTimeMillis() + ms;
            MainActivity.H.postDelayed(sleeper, ms);
        }
        notifyAll2();
    }

    /** Remove one entry from the queue. */
    static void removeAt(int i) {
        if (i < 0 || i >= queue.size()) return;
        if (i == idx) {
            queue.remove(i);
            if (queue.isEmpty()) { stop(); return; }
            load(Math.min(i, queue.size() - 1), playing() || wantPlay);
            return;
        }
        queue.remove(i);
        if (i < idx) idx--;
        notifyAll2();
    }

    static void stop() {
        sleepIn(0);
        release();
        queue.clear();
        idx = -1;
        art = null;
        abandonFocus();
        notifyAll2();
    }

    private static void release() {
        prepared = false;
        unregisterNoisy();
        if (mp != null) {
            try { mp.release(); } catch (Exception ignored) { }
            mp = null;
        }
    }

    private static void unregisterNoisy() {
        if (noisyRegistered && app != null) {
            try { app.unregisterReceiver(noisy); } catch (Exception ignored) { }
            noisyRegistered = false;
        }
    }

    // ------------------------------------------------------------ audio focus

    private static boolean requestFocus() {
        if (app == null) return true;
        if (am == null) am = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
        if (focus == null) {
            focus = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                    .setWillPauseWhenDucked(false)
                    .setOnAudioFocusChangeListener(ch -> {
                        switch (ch) {
                            case AudioManager.AUDIOFOCUS_LOSS:
                                pausedByFocus = false;
                                pause();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (playing()) { pausedByFocus = true; pause(); }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                if (mp != null) try { mp.setVolume(0.25f, 0.25f); } catch (Exception ignored) { }
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (mp != null) try { mp.setVolume(1f, 1f); } catch (Exception ignored) { }
                                if (pausedByFocus) { pausedByFocus = false; start(); }
                                break;
                        }
                    }).build();
        }
        return am.requestAudioFocus(focus) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private static void abandonFocus() {
        if (am != null && focus != null) am.abandonAudioFocusRequest(focus);
    }

    static String time(int ms) {
        int s = Math.max(0, ms) / 1000;
        return s >= 3600 ? String.format(java.util.Locale.US, "%d:%02d:%02d", s / 3600, s / 60 % 60, s % 60)
                : String.format(java.util.Locale.US, "%d:%02d", s / 60, s % 60);
    }
}
