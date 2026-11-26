package com.custom.files;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileNotFoundException;

/** Minimal FileProvider replacement (no AndroidX): content://com.custom.files.provider/root/<path> */
public final class Provider extends ContentProvider {
    static final String AUTH = Flavor.AUTH;

    static Uri uri(File f) {
        return new Uri.Builder().scheme("content").authority(AUTH).path("/root" + f.getAbsolutePath()).build();
    }

    private static File file(Uri u) throws FileNotFoundException {
        String p = u.getPath();
        if (p == null || !p.startsWith("/root/")) throw new FileNotFoundException(String.valueOf(u));
        return new File(p.substring(5));
    }

    @Override public boolean onCreate() { return true; }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return ParcelFileDescriptor.open(file(uri), ParcelFileDescriptor.parseMode(mode));
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] args, String sort) {
        File f;
        try { f = file(uri); } catch (FileNotFoundException e) { return null; }
        if (proj == null) proj = new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};
        Object[] row = new Object[proj.length];
        for (int i = 0; i < proj.length; i++) {
            String c = proj[i];
            if (OpenableColumns.DISPLAY_NAME.equals(c)) row[i] = f.getName();
            else if (OpenableColumns.SIZE.equals(c)) row[i] = f.length();
            else if ("_data".equals(c)) row[i] = f.getAbsolutePath();
            else if ("mime_type".equals(c)) row[i] = getType(uri);
            else if ("date_modified".equals(c) || "last_modified".equals(c)) row[i] = f.lastModified();
        }
        MatrixCursor mc = new MatrixCursor(proj, 1);
        mc.addRow(row);
        return mc;
    }

    @Override
    public String getType(Uri uri) {
        String m = Fx.mime(uri.getLastPathSegment() == null ? "" : uri.getLastPathSegment());
        return m == null ? "application/octet-stream" : m;
    }

    @Override public Uri insert(Uri uri, ContentValues v) { return null; }

    @Override public int delete(Uri uri, String s, String[] a) { return 0; }

    @Override public int update(Uri uri, ContentValues v, String s, String[] a) { return 0; }
}
