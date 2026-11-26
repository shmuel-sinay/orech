package com.custom.files;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.LruCache;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Asynchronous thumbnails with a memory cache. Newest requests first (LIFO) so visible rows load first. */
final class Thumbs {
    private static final LruCache<String, Bitmap> CACHE =
            new LruCache<String, Bitmap>((int) Math.min(Runtime.getRuntime().maxMemory() / 6, 64L << 20)) {
                @Override protected int sizeOf(String k, Bitmap b) { return b.getByteCount(); }
            };
    private static final Set<String> FAILED = Collections.synchronizedSet(new HashSet<>());
    private static final ThreadPoolExecutor EX = new ThreadPoolExecutor(3, 3, 30, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>() {
                @Override public boolean offer(Runnable r) { return offerFirst(r); }
            });

    static boolean supports(int kind) {
        return kind == Fx.K_IMAGE || (kind == Fx.K_VIDEO && !Flavor.NO_VIDEO) || kind == Fx.K_AUDIO || kind == Fx.K_APK;
    }

    static Bitmap cached(File f) { return CACHE.get(f.getPath()); }

    static void forget(File f) {
        CACHE.remove(f.getPath());
        FAILED.remove(f.getPath());
    }

    static void load(Context c, ImageView thumb, File file, int kindArg, int px) {
        final String key = file.getPath();
        thumb.setTag(key);
        boolean apk = kindArg == Fx.K_APK;
        int pad = apk ? Ui.dp(5) : 0;
        thumb.setPadding(pad, pad, pad, pad);
        thumb.setScaleType(apk ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
        Bitmap b = CACHE.get(key);
        if (b != null) {
            thumb.setImageBitmap(b);
            thumb.setAlpha(1f);
            thumb.setVisibility(View.VISIBLE);
            return;
        }
        thumb.setImageDrawable(null);
        thumb.setVisibility(View.GONE);
        if (FAILED.contains(key)) return;
        final Context app = c.getApplicationContext();
        final File f = file;
        final int kind = kindArg;
        EX.execute(() -> {
            if (!key.equals(thumb.getTag())) return; // row was recycled while waiting
            Bitmap bm = make(app, f, kind, px);
            if (bm == null) { FAILED.add(key); return; }
            CACHE.put(key, bm);
            thumb.post(() -> {
                if (!key.equals(thumb.getTag())) return;
                thumb.setImageBitmap(bm);
                thumb.setVisibility(View.VISIBLE);
                thumb.setAlpha(0f);
                thumb.animate().alpha(1f).setDuration(150).start();
            });
        });
    }

    private static Bitmap make(Context c, File f, int kind, int px) {
        try {
            Size sz = new Size(px, px);
            switch (kind) {
                case Fx.K_IMAGE:
                    if (Build.VERSION.SDK_INT >= 29) return ThumbnailUtils.createImageThumbnail(f, sz, null);
                    return decodeSampled(f, px);
                case Fx.K_VIDEO:
                    if (Build.VERSION.SDK_INT >= 29) return ThumbnailUtils.createVideoThumbnail(f, sz, null);
                    return ThumbnailUtils.createVideoThumbnail(f.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                case Fx.K_AUDIO:
                    if (Build.VERSION.SDK_INT >= 29) return ThumbnailUtils.createAudioThumbnail(f, sz, null);
                    MediaMetadataRetriever r = new MediaMetadataRetriever();
                    try {
                        r.setDataSource(f.getPath());
                        byte[] art = r.getEmbeddedPicture();
                        return art == null ? null : BitmapFactory.decodeByteArray(art, 0, art.length);
                    } finally {
                        r.release();
                    }
                case Fx.K_APK:
                    PackageManager pm = c.getPackageManager();
                    PackageInfo pi = pm.getPackageArchiveInfo(f.getPath(), 0);
                    if (pi == null || pi.applicationInfo == null) return null;
                    ApplicationInfo ai = pi.applicationInfo;
                    ai.sourceDir = f.getPath();
                    ai.publicSourceDir = f.getPath();
                    Drawable d = ai.loadIcon(pm);
                    Bitmap b = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
                    d.setBounds(0, 0, px, px);
                    d.draw(new Canvas(b));
                    return b;
            }
        } catch (Throwable ignored) { }
        return null;
    }

    private static Bitmap decodeSampled(File f, int px) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(f.getPath(), o);
        int s = 1;
        while (o.outWidth / (s * 2) >= px && o.outHeight / (s * 2) >= px) s *= 2;
        o.inJustDecodeBounds = false;
        o.inSampleSize = s;
        return BitmapFactory.decodeFile(f.getPath(), o);
    }
}
