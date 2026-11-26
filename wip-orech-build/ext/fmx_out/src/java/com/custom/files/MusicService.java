package com.custom.files;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.IBinder;

/** Keeps music playing in the background, with notification, lock-screen and headset controls. */
public final class MusicService extends Service implements Player.Listener {
    static final String A_TOGGLE = "fmx.toggle", A_NEXT = "fmx.next", A_PREV = "fmx.prev", A_CLOSE = "fmx.close";
    private static final int ID = 7;
    private MediaSession session;
    private boolean foreground;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        NotificationChannel ch = new NotificationChannel("music", "נגן מוזיקה", NotificationManager.IMPORTANCE_LOW);
        ch.setShowBadge(false);
        nm.createNotificationChannel(ch);
        session = new MediaSession(this, "fmx");
        session.setCallback(new MediaSession.Callback() {
            @Override public void onPlay() { Player.start(); }

            @Override public void onPause() { Player.pause(); }

            @Override public void onSkipToNext() { Player.next(); }

            @Override public void onSkipToPrevious() { Player.prev(); }

            @Override public void onSeekTo(long pos) { Player.seek((int) pos); }

            @Override public void onStop() { Player.stop(); }
        });
        session.setActive(true);
        Player.add(this);
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        String a = i == null ? null : i.getAction();
        if (A_TOGGLE.equals(a)) Player.toggle();
        else if (A_NEXT.equals(a)) Player.next();
        else if (A_PREV.equals(a)) Player.prev();
        else if (A_CLOSE.equals(a)) Player.stop();
        changed();
        return START_NOT_STICKY;
    }

    private PendingIntent action(String a, int code) {
        return PendingIntent.getService(this, code, new Intent(this, MusicService.class).setAction(a),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Notification.Action act(int icon, String title, String a, int code) {
        return new Notification.Action.Builder(Icon.createWithResource(this, icon), title, action(a, code)).build();
    }

    @Override
    public void changed() {
        if (!Player.active()) {
            stopForeground(true);
            foreground = false;
            stopSelf();
            return;
        }
        boolean playing = Player.playing();
        MediaMetadata.Builder md = new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, Player.title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, Player.artist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, Player.album)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, Player.duration());
        if (Player.art != null) md.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, Player.art);
        session.setMetadata(md.build());
        session.setPlaybackState(new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE | PlaybackState.ACTION_PLAY_PAUSE
                        | PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackState.ACTION_SEEK_TO | PlaybackState.ACTION_STOP)
                .setState(playing ? PlaybackState.STATE_PLAYING : PlaybackState.STATE_PAUSED, Player.position(),
                        playing ? Player.speed : 0f)
                .build());

        Intent open = new Intent(this, MainActivity.class).putExtra(MainActivity.EXTRA_MUSIC, true)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Notification.Builder b = new Notification.Builder(this, "music")
                .setSmallIcon(R.drawable.ic_audio)
                .setContentTitle(Player.title)
                .setContentText(Player.artist.isEmpty() ? Player.album : Player.artist)
                .setLargeIcon(Player.art)
                .setContentIntent(PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(action(A_CLOSE, 4))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(act(R.drawable.ic_prev, "הקודם", A_PREV, 1))
                .addAction(act(playing ? R.drawable.ic_pause : R.drawable.ic_play, playing ? "השהה" : "נגן", A_TOGGLE, 2))
                .addAction(act(R.drawable.ic_next, "הבא", A_NEXT, 3))
                .addAction(act(R.drawable.ic_close, "סגור", A_CLOSE, 4))
                .setStyle(new Notification.MediaStyle().setMediaSession(session.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2));
        Notification n = b.build();
        if (playing) {
            try {
                startForeground(ID, n);
                foreground = true;
            } catch (Exception e) { // background start not allowed: still show the controls
                getSystemService(NotificationManager.class).notify(ID, n);
            }
        } else {
            if (foreground) {
                stopForeground(false); // keep the notification so it can be resumed
                foreground = false;
            }
            getSystemService(NotificationManager.class).notify(ID, n);
        }
    }

    @Override
    public void onDestroy() {
        Player.remove(this);
        session.release();
        getSystemService(NotificationManager.class).cancel(ID);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!Player.playing()) {
            Player.stop();
            stopSelf();
        }
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
