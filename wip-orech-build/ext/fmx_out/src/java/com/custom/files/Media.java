package com.custom.files;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Categories and global search through the MediaStore index — instant, no disk walking. */
final class Media {
    static final int IMAGES = 0, VIDEO = 1, AUDIO = 2, DOCS = 3, APPS = 4, ARCHIVES = 5, RECENT = 6, LARGE = 7, N = 8;
    static final String[] TITLES = {"תמונות", "וידאו", "שמע", "מסמכים", "אפליקציות", "ארכיונים", "קבצים חדשים", "קבצים גדולים"};
    static final int[] ICON = {R.drawable.ic_image, R.drawable.ic_video, R.drawable.ic_audio, R.drawable.ic_doc,
            R.drawable.ic_apk, R.drawable.ic_archive, R.drawable.ic_history, R.drawable.ic_data};
    static final int[] COLOR = {0xFF9A5B2E, 0xFFB3472F, 0xFF2E7D5B, 0xFF2F6F9F, 0xFF4C7F3A, 0xFF8A6A1F, 0xFF6B665C, 0xFF7A4B63};
    static final int[] SOFT = {0xFFF0E3D6, 0xFFF3E1DA, 0xFFDFEDE5, 0xFFDDE9F2, 0xFFE4EEDC, 0xFFF0E7D0, 0xFFE9E5DB, 0xFFEBDDE4};

    private static final Uri FILES = MediaStore.Files.getContentUri("external");

    static boolean groupable(int cat) { return cat == IMAGES || cat == VIDEO || cat == AUDIO; }

    private static String like(String exts) {
        StringBuilder sb = new StringBuilder("(");
        for (String e : exts.split(" ")) {
            if (sb.length() > 1) sb.append(" OR ");
            sb.append("_display_name LIKE '%.").append(e).append("'");
        }
        return sb.append(")").toString();
    }

    private static String selection(int cat) {
        long now = System.currentTimeMillis() / 1000;
        switch (cat) {
            case IMAGES: return "media_type=1";
            case VIDEO: return "media_type=3";
            case AUDIO: return "media_type=2";
            case DOCS: return like("pdf doc docx xls xlsx ppt pptx txt rtf odt ods odp csv epub");
            case APPS: return like("apk apks xapk");
            case ARCHIVES: return like("zip rar 7z tar gz tgz");
            case RECENT: return "mime_type IS NOT NULL AND date_modified>" + (now - 7L * 86400);
            default: return "mime_type IS NOT NULL AND _size>" + (50L << 20);
        }
    }

    private static String esc(String s) { return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_"); }

    static ArrayList<FileItem> category(Context c, int cat, boolean hidden) {
        String order = cat == LARGE ? "_size DESC" : "date_modified DESC";
        ArrayList<FileItem> out = query(c, selection(cat), null, order, hidden, cat == RECENT ? 500 : 50000, false);
        for (FileItem it : out) it.showParent = !groupable(cat);
        return out;
    }

    /** Files of a category inside one folder (not recursive). */
    static ArrayList<FileItem> categoryIn(Context c, int cat, File dir, boolean hidden) {
        ArrayList<FileItem> all = query(c, selection(cat) + " AND _data LIKE ? ESCAPE '\\'",
                new String[]{esc(dir.getAbsolutePath()) + "/%"}, "date_modified DESC", hidden, 50000, false);
        ArrayList<FileItem> out = new ArrayList<>();
        for (FileItem it : all) if (dir.equals(it.file.getParentFile())) out.add(it);
        return out;
    }

    /** Folders that contain files of the category, with a cover file for the thumbnail. */
    static ArrayList<FileItem> groups(Context c, int cat, boolean hidden) {
        LinkedHashMap<String, long[]> stats = new LinkedHashMap<>();
        LinkedHashMap<String, File> covers = new LinkedHashMap<>();
        for (FileItem it : category(c, cat, hidden)) {
            File p = it.file.getParentFile();
            if (p == null) continue;
            String k = p.getPath();
            long[] s = stats.get(k);
            if (s == null) { s = new long[3]; stats.put(k, s); covers.put(k, it.file); }
            s[0]++;
            s[1] += it.size;
            s[2] = Math.max(s[2], it.modified);
        }
        int kind = cat == IMAGES ? Fx.K_IMAGE : cat == VIDEO ? Fx.K_VIDEO : Fx.K_AUDIO;
        ArrayList<FileItem> out = new ArrayList<>();
        for (String k : stats.keySet()) {
            long[] s = stats.get(k);
            out.add(new FileItem(new File(k), (int) s[0], s[1], s[2], covers.get(k), kind));
        }
        return out;
    }

    /** {count, bytes} for a category. */
    static long[] stats(Context c, int cat) {
        return sum(c, selection(cat), null);
    }

    /** {count, bytes} of everything indexed below a folder. */
    static long[] statsUnder(Context c, File dir) {
        return sum(c, "mime_type IS NOT NULL AND _data LIKE ? ESCAPE '\\'", new String[]{esc(dir.getAbsolutePath()) + "/%"});
    }

    private static long[] sum(Context c, String sel, String[] args) {
        long[] r = {0, 0};
        try (Cursor cur = c.getContentResolver().query(FILES, new String[]{"_size"}, sel, args, null)) {
            if (cur == null) return r;
            while (cur.moveToNext()) {
                r[0]++;
                r[1] += cur.getLong(0);
            }
        } catch (Exception e) {
            r[0] = -1;
        }
        return r;
    }

    static ArrayList<FileItem> search(Context c, String q, boolean hidden) {
        ArrayList<FileItem> out = query(c, "_display_name LIKE ? ESCAPE '\\'", new String[]{"%" + esc(q) + "%"},
                "date_modified DESC", hidden, 1500, true);
        for (FileItem it : out) it.showParent = true;
        return out;
    }

    private static ArrayList<FileItem> query(Context c, String sel, String[] args, String order, boolean hidden,
                                             int max, boolean stat) {
        ArrayList<FileItem> out = new ArrayList<>();
        String[] proj = {"_data", "_size", "date_modified"};
        try (Cursor cur = c.getContentResolver().query(FILES, proj, sel, args, order)) {
            if (cur == null) return out;
            while (cur.moveToNext() && out.size() < max) {
                String path = cur.getString(0);
                if (path == null) continue;
                if (!hidden && path.contains("/.")) continue;
                if (path.contains("/" + Trash.DIR + "/")) continue;
                File f = new File(path);
                if (!f.exists()) continue;
                out.add(stat ? new FileItem(f) : new FileItem(f, cur.getLong(1), cur.getLong(2) * 1000));
            }
        } catch (Exception ignored) { }
        return out;
    }
}
