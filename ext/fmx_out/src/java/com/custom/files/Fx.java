package com.custom.files;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/** File types, formatting and the actual file operations. */
final class Fx {
    static final int K_DIR = 0, K_IMAGE = 1, K_VIDEO = 2, K_AUDIO = 3, K_DOC = 4, K_SHEET = 5, K_SLIDE = 6,
            K_PDF = 7, K_APK = 8, K_ARCHIVE = 9, K_TEXT = 10, K_OTHER = 11;
    static final int[] ICON = {R.drawable.ic_folder, R.drawable.ic_image, R.drawable.ic_video, R.drawable.ic_audio,
            R.drawable.ic_doc, R.drawable.ic_doc, R.drawable.ic_doc, R.drawable.ic_doc, R.drawable.ic_apk,
            R.drawable.ic_archive, R.drawable.ic_doc, R.drawable.ic_file};
    static final int[] COLOR = {0xFFE09A2E, 0xFF8B5CF6, 0xFFEF4444, 0xFFF59E0B, 0xFF2563EB, 0xFF16A34A, 0xFFEA580C,
            0xFFDC2626, 0xFF22A35A, 0xFFB7791F, 0xFF64748B, 0xFF64748B};
    static final int[] SOFT = {0xFFFFF1D6, 0xFFF0EAFE, 0xFFFDECEC, 0xFFFEF3DC, 0xFFE4ECFD, 0xFFE2F5E8, 0xFFFDEDE2,
            0xFFFCE7E7, 0xFFE3F5EA, 0xFFF8EEDA, 0xFFEEF1F5, 0xFFEEF1F5};
    static final ColorStateList[] COLOR_CSL = new ColorStateList[COLOR.length], SOFT_CSL = new ColorStateList[SOFT.length];
    static final String[] KIND_NAME = {"תיקיה", "תמונה", "וידאו", "שמע", "מסמך", "גיליון", "מצגת", "PDF",
            "אפליקציה (APK)", "ארכיון", "טקסט", "קובץ"};

    private static final HashMap<String, Integer> KIND = new HashMap<>();

    static {
        for (int i = 0; i < COLOR.length; i++) {
            COLOR_CSL[i] = ColorStateList.valueOf(COLOR[i]);
            SOFT_CSL[i] = ColorStateList.valueOf(SOFT[i]);
        }
        reg(K_IMAGE, "jpg jpeg png gif webp bmp heic heif svg ico avif");
        reg(K_VIDEO, "mp4 mkv avi mov webm 3gp m4v wmv flv ts mpg mpeg");
        reg(K_AUDIO, "mp3 wav ogg m4a aac flac opus wma amr mid midi");
        reg(K_DOC, "doc docx odt rtf");
        reg(K_SHEET, "xls xlsx ods csv");
        reg(K_SLIDE, "ppt pptx odp");
        reg(K_PDF, "pdf");
        reg(K_APK, "apk apks xapk");
        reg(K_ARCHIVE, "zip rar 7z tar gz tgz bz2 xz jar");
        reg(K_TEXT, "txt log md json xml html htm js css java py c cpp h ini cfg conf yml yaml srt vcf epub");
    }

    private static void reg(int k, String exts) { for (String e : exts.split(" ")) KIND.put(e, k); }

    static String ext(String n) {
        int i = n.lastIndexOf('.');
        return i <= 0 || i == n.length() - 1 ? "" : n.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    static String base(String n) {
        int i = n.lastIndexOf('.');
        return i <= 0 ? n : n.substring(0, i);
    }

    static int kind(String name) {
        Integer k = KIND.get(ext(name));
        return k == null ? K_OTHER : k;
    }

    static String mime(String name) {
        String e = ext(name);
        if (e.isEmpty()) return null;
        switch (e) {
            case "apk": return "application/vnd.android.package-archive";
            case "mkv": return "video/x-matroska";
            case "opus": return "audio/ogg";
            case "heic": return "image/heic";
            case "md": case "log": case "ini": case "cfg": case "conf": case "yml": case "yaml": case "java":
            case "py": case "c": case "cpp": case "h": case "srt": return "text/plain";
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(e);
    }

    // ---------------------------------------------------------------- formatting

    static volatile boolean decimal;

    static String size(long b) {
        String s;
        int k = decimal ? 1000 : 1024;
        if (b < k) s = b + " B";
        else {
            double v = b;
            String[] u = {"KB", "MB", "GB", "TB"};
            int i = -1;
            do { v /= k; i++; } while (v >= k && i < 3);
            s = (v >= 100 ? String.format(Locale.US, "%.0f", v) : String.format(Locale.US, "%.1f", v)) + " " + u[i];
        }
        return "⁦" + s + "⁩"; // isolate the LTR number inside Hebrew text
    }

    static String num(long n) { return String.format(Locale.US, "%,d", n); }

    static String items(int n) {
        if (n == 0) return "ריקה";
        return n == 1 ? "פריט אחד" : num(n) + " פריטים";
    }

    static String files(int n) { return n == 1 ? "קובץ אחד" : num(n) + " קבצים"; }

    private static final Locale HE = new Locale("he");
    private static final SimpleDateFormat DF = new SimpleDateFormat("d MMM yyyy", HE),
            TF = new SimpleDateFormat("HH:mm", HE), FULL = new SimpleDateFormat("d בMMMM yyyy, HH:mm", HE);
    private static long dayStart;

    static String date(long t) {
        long now = System.currentTimeMillis();
        if (now - dayStart >= 86400000L || now < dayStart) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dayStart = c.getTimeInMillis();
        }
        Date d = new Date(t);
        if (t >= dayStart) return "היום, " + TF.format(d);
        if (t >= dayStart - 86400000L) return "אתמול, " + TF.format(d);
        return DF.format(d);
    }

    static String fullDate(long t) { return FULL.format(new Date(t)); }

    static boolean validName(String n) {
        return !n.isEmpty() && !n.equals(".") && !n.equals("..") && n.indexOf('/') < 0 && n.indexOf('\0') < 0;
    }

    // ---------------------------------------------------------------- sorting

    static Comparator<FileItem> comparator(int by, boolean asc) {
        return (x, y) -> {
            if (x.dir != y.dir) return x.dir ? -1 : 1;
            int r;
            switch (by) {
                case Prefs.BY_DATE: r = Long.compare(x.modified, y.modified); break;
                case Prefs.BY_SIZE: r = x.dir ? 0 : Long.compare(x.size, y.size); break;
                case Prefs.BY_TYPE: r = ext(x.name).compareTo(ext(y.name)); break;
                default: r = 0;
            }
            if (r == 0) r = natural(x.name, y.name);
            return asc ? r : -r;
        };
    }

    /** Case-insensitive "natural" order: file2 < file10. */
    static int natural(String a, String b) {
        int i = 0, j = 0, na = a.length(), nb = b.length();
        while (i < na && j < nb) {
            char ca = a.charAt(i), cb = b.charAt(j);
            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                int si = i, sj = j;
                while (si < na && a.charAt(si) == '0') si++;
                while (sj < nb && b.charAt(sj) == '0') sj++;
                int ei = si, ej = sj;
                while (ei < na && Character.isDigit(a.charAt(ei))) ei++;
                while (ej < nb && Character.isDigit(b.charAt(ej))) ej++;
                int la = ei - si, lb = ej - sj;
                if (la != lb) return la - lb;
                for (int k = 0; k < la; k++) {
                    int d = a.charAt(si + k) - b.charAt(sj + k);
                    if (d != 0) return d;
                }
                i = ei;
                j = ej;
            } else {
                ca = Character.toLowerCase(ca);
                cb = Character.toLowerCase(cb);
                if (ca != cb) return ca - cb;
                i++;
                j++;
            }
        }
        return (na - i) - (nb - j);
    }

    // ---------------------------------------------------------------- file operations

    static File unique(File dir, String name) {
        File f = new File(dir, name);
        if (!f.exists()) return f;
        String b = name, e = "";
        int i = name.lastIndexOf('.');
        if (i > 0 && !f.isDirectory()) { b = name.substring(0, i); e = name.substring(i); }
        for (int n = 1; ; n++) {
            f = new File(dir, b + " (" + n + ")" + e);
            if (!f.exists()) return f;
        }
    }

    static boolean isLink(File f) {
        try { return Files.isSymbolicLink(f.toPath()); } catch (Exception e) { return false; }
    }

    static long sizeTree(File f, Ops.Op op) {
        if (op != null) op.check();
        if (f.isDirectory() && !isLink(f)) {
            long s = 0;
            File[] c = f.listFiles();
            if (c != null) for (File x : c) s += sizeTree(x, op);
            return s;
        }
        return f.length();
    }

    /** acc = {bytes, files, folders} */
    static void countTree(File f, long[] acc, Ops.Op op) {
        op.check();
        if (f.isDirectory() && !isLink(f)) {
            acc[2]++;
            File[] c = f.listFiles();
            if (c != null) for (File x : c) countTree(x, acc, op);
        } else {
            acc[0] += f.length();
            acc[1]++;
        }
    }

    static void copyTree(File src, File dst, Ops.Op op) throws IOException {
        op.check();
        if (src.isDirectory()) {
            if (!dst.exists() && !dst.mkdirs()) throw new IOException("לא ניתן ליצור את התיקיה " + dst.getName());
            File[] c = src.listFiles();
            if (c != null) for (File x : c) copyTree(x, new File(dst, x.getName()), op);
            dst.setLastModified(src.lastModified());
            return;
        }
        op.cur = src.getName();
        try (FileInputStream in = new FileInputStream(src); FileOutputStream out = new FileOutputStream(dst)) {
            FileChannel ic = in.getChannel(), oc = out.getChannel();
            long size = ic.size(), pos = 0;
            while (pos < size) {
                op.check();
                long n = ic.transferTo(pos, Math.min(8L << 20, size - pos), oc); // kernel-side copy, very fast
                if (n <= 0) break;
                pos += n;
                op.done += n;
            }
        } catch (Ops.Cancelled | IOException e) {
            dst.delete();
            throw e;
        }
        dst.setLastModified(src.lastModified());
        op.scan(dst);
    }

    static void deleteTree(File f, Ops.Op op) throws IOException {
        if (op != null) op.check();
        if (f.isDirectory() && !isLink(f)) {
            File[] c = f.listFiles();
            if (c != null) for (File x : c) deleteTree(x, op);
        }
        if (op != null) { op.cur = f.getName(); op.scan(f); }
        if (!f.delete() && f.exists()) throw new IOException("לא ניתן למחוק את " + f.getName());
    }

    static void zip(List<File> src, File out, Ops.Op op) throws IOException {
        long t = 0;
        for (File f : src) t += sizeTree(f, op);
        op.total = t;
        byte[] buf = new byte[1 << 16];
        try (ZipOutputStream z = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(out), 1 << 16))) {
            for (File f : src) addZip(z, f, f.getName(), buf, op, out);
        } catch (Ops.Cancelled | IOException e) {
            out.delete();
            throw e;
        }
        op.scan(out);
    }

    private static void addZip(ZipOutputStream z, File f, String name, byte[] buf, Ops.Op op, File out) throws IOException {
        op.check();
        if (f.equals(out)) return;
        if (f.isDirectory()) {
            ZipEntry e = new ZipEntry(name + "/");
            e.setTime(f.lastModified());
            z.putNextEntry(e);
            z.closeEntry();
            File[] c = f.listFiles();
            if (c != null) for (File x : c) addZip(z, x, name + "/" + x.getName(), buf, op, out);
            return;
        }
        op.cur = f.getName();
        ZipEntry e = new ZipEntry(name);
        e.setTime(f.lastModified());
        z.putNextEntry(e);
        try (InputStream in = new FileInputStream(f)) {
            int n;
            while ((n = in.read(buf)) > 0) {
                op.check();
                z.write(buf, 0, n);
                op.done += n;
            }
        }
        z.closeEntry();
    }

    private static ZipFile openZip(File zip) throws IOException {
        try {
            ZipFile zf = new ZipFile(zip, StandardCharsets.UTF_8);
            try {
                for (Enumeration<? extends ZipEntry> e = zf.entries(); e.hasMoreElements(); ) e.nextElement();
                return zf;
            } catch (IllegalArgumentException bad) {
                zf.close();
            }
        } catch (IllegalArgumentException ignored) { }
        // Archives made on Hebrew Windows store names in the DOS code page
        Charset cs;
        try { cs = Charset.forName("IBM862"); } catch (Exception e) { cs = StandardCharsets.ISO_8859_1; }
        return new ZipFile(zip, cs);
    }

    static void unzip(File zip, File dest, Ops.Op op) throws IOException {
        ZipFile zf = openZip(zip);
        try {
            long total = 0;
            for (Enumeration<? extends ZipEntry> en = zf.entries(); en.hasMoreElements(); )
                total += Math.max(0, en.nextElement().getSize());
            op.total = total;
            if (!dest.exists() && !dest.mkdirs()) throw new IOException("לא ניתן ליצור את התיקיה " + dest.getName());
            String root = dest.getCanonicalPath() + File.separator;
            byte[] buf = new byte[1 << 16];
            for (Enumeration<? extends ZipEntry> en = zf.entries(); en.hasMoreElements(); ) {
                op.check();
                ZipEntry ze = en.nextElement();
                File out = new File(dest, ze.getName());
                if (!out.getCanonicalPath().startsWith(root)) continue; // zip-slip protection
                if (ze.isDirectory()) { out.mkdirs(); continue; }
                File par = out.getParentFile();
                if (par != null) par.mkdirs();
                op.cur = out.getName();
                try (InputStream in = zf.getInputStream(ze); OutputStream os = new FileOutputStream(out)) {
                    int n;
                    while ((n = in.read(buf)) > 0) {
                        op.check();
                        os.write(buf, 0, n);
                        op.done += n;
                    }
                }
                if (ze.getTime() > 0) out.setLastModified(ze.getTime());
                op.scan(out);
            }
        } catch (Ops.Cancelled c) {
            deleteTree(dest, null);
            throw c;
        } finally {
            zf.close();
        }
    }

    // ---------------------------------------------------------------- intents

    static Intent view(File f, String mime) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Provider.uri(f), mime);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return i;
    }

    /** Small badge drawn on well-known folders (DCIM, Download, Music …), 0 = none. */
    static int folderBadge(String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "dcim": case "camera": return R.drawable.ic_camera;
            case "download": case "downloads": return R.drawable.ic_download;
            case "movies": case "video": case "videos": return R.drawable.ic_video;
            case "music": case "audio": case "ringtones": case "notifications": case "alarms": case "podcasts":
                return R.drawable.ic_audio;
            case "pictures": case "screenshots": case "images": return R.drawable.ic_image;
            case "documents": return R.drawable.ic_doc;
            case "android": return R.drawable.ic_apk;
            default: return 0;
        }
    }

    static int folderBadgeColor(int res) {
        if (res == R.drawable.ic_download) return 0xFF0EA5E9;
        if (res == R.drawable.ic_video) return 0xFFEF4444;
        if (res == R.drawable.ic_audio) return 0xFF0D9488;
        if (res == R.drawable.ic_image) return 0xFF8B5CF6;
        if (res == R.drawable.ic_doc) return 0xFF2563EB;
        if (res == R.drawable.ic_apk) return 0xFF22A35A;
        return 0xFF334155;
    }

    /** Open a file: built-in viewer when enabled, otherwise an external app. */
    /** Restricted version: videos cannot be played and APKs cannot be installed. */
    static boolean blocked(MainActivity a, String name) {
        int k = kind(name);
        if (Flavor.NO_VIDEO && k == K_VIDEO) { a.toast("צפייה בסרטונים אינה זמינה בגרסה זו"); return true; }
        if (Flavor.NO_INSTALL && k == K_APK) { a.toast("התקנת אפליקציות אינה זמינה בגרסה זו"); return true; }
        return false;
    }

    static void open(MainActivity a, FileItem it, List<FileItem> siblings) {
        if (blocked(a, it.name)) return;
        Prefs p = a.prefs;
        File f = it.file;
        switch (it.kind) {
            case K_IMAGE:
                if (p.viewImage && !"svg".equals(ext(it.name))) { a.push(new Viewers.Image(a, it, siblings)); return; }
                break;
            case K_VIDEO:
                if (p.viewVideo) { a.push(new VideoPlayer(a, it, siblings)); return; }
                break;
            case K_AUDIO:
                if (p.viewAudio) { Music.open(a, it, siblings); return; }
                break;
            case K_TEXT:
                if (p.viewText && !"epub".equals(ext(it.name)) && !"html".equals(ext(it.name)) && !"htm".equals(ext(it.name))) {
                    a.push(new Viewers.Text(a, f));
                    return;
                }
                break;
            case K_OTHER:
                if (p.viewText && f.length() < (256 << 10) && ext(it.name).isEmpty()) { a.push(new Viewers.Text(a, f)); return; }
                break;
        }
        open(a, f, false);
    }

    static void open(MainActivity a, File f, boolean chooser) {
        if (blocked(a, f.getName())) return;
        String m = mime(f.getName());
        if (m == null) { openAs(a, f); return; }
        Intent i = view(f, m);
        try {
            a.startActivity(chooser ? Intent.createChooser(i, "פתח באמצעות") : i);
        } catch (ActivityNotFoundException e) {
            openAs(a, f);
        } catch (Exception e) {
            a.toast("לא ניתן לפתוח את הקובץ");
        }
    }

    static void openAs(MainActivity a, File f) {
        if (blocked(a, f.getName())) return;
        String[] labels = {"טקסט", "תמונה", "שמע", "וידאו", "אחר"};
        String[] types = {"text/plain", "image/*", "audio/*", "video/*", "*/*"};
        int[] icons = {R.drawable.ic_text, R.drawable.ic_image, R.drawable.ic_audio, R.drawable.ic_video, R.drawable.ic_file};
        ArrayList<Dialogs.Item> m = new ArrayList<>();
        for (int k = 0; k < labels.length; k++) {
            final String t = types[k];
            if (Flavor.NO_VIDEO && (k == 3 || k == 4)) continue; // video/* and */* could play a video
            if (k == 0 && a.prefs.viewText) {
                m.add(new Dialogs.Item(icons[k], "טקסט (עורך מובנה)", () -> a.push(new Viewers.Text(a, f))));
                continue;
            }
            m.add(new Dialogs.Item(icons[k], labels[k], () -> {
                try { a.startActivity(Intent.createChooser(view(f, t), "פתח באמצעות")); }
                catch (Exception e) { a.toast("לא נמצאה אפליקציה מתאימה"); }
            }));
        }
        Dialogs.sheet(a, "פתח כ…", f.getName(), m);
    }

    static void share(MainActivity a, List<File> files) {
        ArrayList<Uri> uris = new ArrayList<>();
        String type = null;
        for (File f : files) {
            if (f.isDirectory()) continue;
            if (Flavor.NO_VIDEO && kind(f.getName()) == K_VIDEO) continue;
            uris.add(Provider.uri(f));
            String m = mime(f.getName());
            if (m == null) m = "application/octet-stream";
            if (type == null) type = m;
            else if (!type.equals(m)) {
                String t1 = type.split("/")[0], t2 = m.split("/")[0];
                type = t1.equals(t2) ? t1 + "/*" : "*/*";
            }
        }
        if (uris.isEmpty()) { a.toast("לא ניתן לשתף תיקיות"); return; }
        Intent i;
        if (uris.size() == 1) {
            i = new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        } else {
            i = new Intent(Intent.ACTION_SEND_MULTIPLE);
            i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }
        i.setType(type);
        ClipData cd = ClipData.newRawUri("", uris.get(0));
        for (int k = 1; k < uris.size(); k++) cd.addItem(new ClipData.Item(uris.get(k)));
        i.setClipData(cd);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try { a.startActivity(Intent.createChooser(i, "שיתוף")); }
        catch (Exception e) { a.toast("לא נמצאה אפליקציה לשיתוף"); }
    }

    static void copyText(Activity a, String s) {
        ClipboardManager cm = (ClipboardManager) a.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("path", s));
    }
}
