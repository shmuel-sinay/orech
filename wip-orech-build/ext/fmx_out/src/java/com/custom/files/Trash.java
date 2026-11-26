package com.custom.files;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Recycle bin. Deleted items are renamed into a hidden ".fm_trash" folder on the same
 * volume (instant, no copying) and the original path is remembered in preferences.
 */
final class Trash {
    static final String DIR = ".fm_trash";
    static final long KEEP_MS = 30L * 24 * 60 * 60 * 1000;

    private static SharedPreferences sp(Context c) { return c.getSharedPreferences("trash", 0); }

    static boolean isInTrash(File f) { return f.getPath().contains("/" + DIR + "/"); }

    static void put(Context c, File f) throws IOException {
        Storage.Vol v = Storage.of(c, f);
        File root = v != null ? v.root : Storage.primaryRoot(c);
        File td = new File(root, DIR);
        if (!td.exists()) {
            td.mkdirs();
            try { new File(td, ".nomedia").createNewFile(); } catch (IOException ignored) { }
        }
        long now = System.currentTimeMillis();
        File dst = new File(td, now + "_" + f.getName());
        if (!f.renameTo(dst)) throw new IOException("לא ניתן להעביר לסל המיחזור: " + f.getName());
        sp(c).edit().putString(dst.getPath(), f.getPath() + "\n" + now).apply();
    }

    static ArrayList<FileItem> items(Context c) {
        ArrayList<FileItem> out = new ArrayList<>();
        SharedPreferences.Editor ed = null;
        for (Map.Entry<String, ?> e : sp(c).getAll().entrySet()) {
            File t = new File(e.getKey());
            String[] v = String.valueOf(e.getValue()).split("\n");
            if (!t.exists() || v.length < 2) {
                if (ed == null) ed = sp(c).edit();
                ed.remove(e.getKey());
                continue;
            }
            FileItem it = new FileItem(t);
            it.origPath = v[0];
            it.name = new File(v[0]).getName();
            try { it.trashTime = Long.parseLong(v[1]); } catch (NumberFormatException ignored) { }
            out.add(it);
        }
        if (ed != null) ed.apply();
        out.sort((a, b) -> Long.compare(b.trashTime, a.trashTime));
        return out;
    }

    static int count(Context c) { return sp(c).getAll().size(); }

    static File restore(Context c, FileItem it) throws IOException {
        File o = new File(it.origPath);
        File parent = o.getParentFile();
        if (parent != null) parent.mkdirs();
        if (o.exists()) o = Fx.unique(parent, o.getName());
        if (!it.file.renameTo(o)) throw new IOException("השחזור נכשל: " + it.name);
        sp(c).edit().remove(it.file.getPath()).apply();
        return o;
    }

    static void deleteForever(Context c, FileItem it, Ops.Op op) throws IOException {
        Fx.deleteTree(it.file, op);
        sp(c).edit().remove(it.file.getPath()).apply();
    }

    /** Remove items older than 30 days. */
    static void purge(Context c) {
        long now = System.currentTimeMillis();
        for (FileItem it : items(c)) {
            if (now - it.trashTime > KEEP_MS) {
                try { deleteForever(c, it, null); } catch (Exception ignored) { }
            }
        }
    }
}
