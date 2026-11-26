package com.custom.files;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/** Storage volumes (internal, SD card, USB) with their usage. */
final class Storage {
    static final class Vol {
        File root;
        String label;
        boolean primary;
        long total, free;
    }

    private static volatile ArrayList<Vol> cache;

    static ArrayList<Vol> list(Context c, boolean refresh) {
        ArrayList<Vol> l = cache;
        if (l != null && !refresh) return l;
        l = new ArrayList<>();
        StorageManager sm = (StorageManager) c.getSystemService(Context.STORAGE_SERVICE);
        File[] dirs;
        try { dirs = c.getExternalFilesDirs(null); } catch (Exception e) { dirs = new File[0]; }
        for (File d : dirs) {
            if (d == null) continue;
            String p = d.getPath();
            int i = p.indexOf("/Android/data/");
            if (i <= 0) continue;
            Vol v = new Vol();
            v.root = new File(p.substring(0, i));
            StorageVolume sv = null;
            try { sv = sm.getStorageVolume(v.root); } catch (Exception ignored) { }
            v.primary = sv != null ? sv.isPrimary() : l.isEmpty();
            if (v.primary) v.label = "אחסון פנימי";
            else {
                String desc = "";
                try { desc = sv == null ? "" : sv.getDescription(c); } catch (Exception ignored) { }
                v.label = desc != null && desc.toLowerCase(Locale.ROOT).contains("usb") ? "כונן USB" : "כרטיס SD";
            }
            try {
                StatFs s = new StatFs(v.root.getPath());
                v.total = s.getTotalBytes();
                v.free = s.getAvailableBytes();
            } catch (Exception ignored) { }
            l.add(v);
        }
        if (l.isEmpty()) {
            Vol v = new Vol();
            v.root = Environment.getExternalStorageDirectory();
            v.label = "אחסון פנימי";
            v.primary = true;
            l.add(v);
        }
        cache = l;
        return l;
    }

    static Vol of(Context c, File f) {
        String p = f.getAbsolutePath();
        Vol best = null;
        for (Vol v : list(c, false)) {
            String r = v.root.getPath();
            if ((p.equals(r) || p.startsWith(r + "/")) && (best == null || r.length() > best.root.getPath().length())) best = v;
        }
        return best;
    }

    static File primaryRoot(Context c) {
        for (Vol v : list(c, false)) if (v.primary) return v.root;
        return Environment.getExternalStorageDirectory();
    }

    /** "אחסון פנימי › Download › Telegram" style location. */
    static String pretty(Context c, File f) {
        Vol v = of(c, f);
        if (v == null) return f.getPath();
        String rest = f.getAbsolutePath().substring(v.root.getPath().length());
        StringBuilder sb = new StringBuilder(v.label);
        for (String s : rest.split("/")) if (!s.isEmpty()) sb.append(" › ").append(s);
        return sb.toString();
    }
}
