package com.android.packageinstaller;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Install-time check: refuse APKs that carry a video decoder library or that declare any
 * "video/" mime type in their manifest. Accepts a single APK or an install directory
 * (base.apk and splits). Pure java, no framework calls.
 */
public final class VideoGuard {
    private static final String[] LIBS = {
        "libvlc", "libavcodec", "libffmpeg", "libmpv", "libdav1d", "libvpx",
        "libde265", "libopenh264", "libx264", "libijkffmpeg", "libijksdl"
    };

    private VideoGuard() {}

    public static boolean blocked(String path) {
        if (path == null) return false;
        try {
            File f = new File(path);
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                if (files == null) return false;
                for (int i = 0; i < files.length; i++) {
                    String n = files[i].getName().toLowerCase();
                    if (n.endsWith(".apk") && blockedApk(files[i])) return true;
                }
                return false;
            }
            return blockedApk(f);
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean blockedApk(File apk) {
        ZipFile z = null;
        try {
            z = new ZipFile(apk);
            Enumeration<? extends ZipEntry> e = z.entries();
            while (e.hasMoreElements()) {
                ZipEntry en = e.nextElement();
                String n = en.getName().toLowerCase();
                if (n.startsWith("lib/") && n.endsWith(".so")) {
                    String base = n.substring(n.lastIndexOf('/') + 1);
                    for (int i = 0; i < LIBS.length; i++) {
                        if (base.startsWith(LIBS[i])) return true;
                    }
                } else if (n.equals("androidmanifest.xml")) {
                    if (manifestDeclaresVideo(z, en)) return true;
                }
            }
        } catch (Throwable t) {
            return false;
        } finally {
            try { if (z != null) z.close(); } catch (Throwable ignored) { }
        }
        return false;
    }

    private static boolean manifestDeclaresVideo(ZipFile z, ZipEntry en) throws Exception {
        InputStream in = z.getInputStream(en);
        try {
            byte[] buf = new byte[(int) Math.max(en.getSize(), 1024)];
            int len = 0;
            int r;
            while ((r = in.read(buf, len, buf.length - len)) > 0) {
                len += r;
                if (len == buf.length) {
                    byte[] nb = new byte[buf.length * 2];
                    System.arraycopy(buf, 0, nb, 0, len);
                    buf = nb;
                }
            }
            // string pool is UTF-8 or UTF-16LE: look for "video/" in both
            byte[] u8 = {'v', 'i', 'd', 'e', 'o', '/'};
            byte[] u16 = {'v', 0, 'i', 0, 'd', 0, 'e', 0, 'o', 0, '/', 0};
            return indexOf(buf, len, u8) >= 0 || indexOf(buf, len, u16) >= 0;
        } finally {
            in.close();
        }
    }

    private static int indexOf(byte[] h, int hl, byte[] n) {
        outer:
        for (int i = 0; i + n.length <= hl; i++) {
            for (int j = 0; j < n.length; j++) {
                if (h[i + j] != n[j]) continue outer;
            }
            return i;
        }
        return -1;
    }
}
