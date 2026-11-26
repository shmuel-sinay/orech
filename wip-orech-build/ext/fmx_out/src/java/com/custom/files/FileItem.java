package com.custom.files;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/** One row in a listing. Attributes are read with a single stat() call. */
final class FileItem {
    final File file;
    String name;
    final boolean dir;
    final long size, modified;
    final int kind;
    volatile int count = -1;      // folder child count, filled in the background
    boolean selected;
    boolean showParent;           // search / category results show where the file lives
    String origPath;              // recycle bin: original location
    long trashTime;

    FileItem(File f) {
        file = f;
        name = f.getName();
        boolean d;
        long s, m;
        try {
            BasicFileAttributes at = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            d = at.isDirectory();
            s = at.size();
            m = at.lastModifiedTime().toMillis();
        } catch (Exception e) {
            d = f.isDirectory();
            s = d ? 0 : f.length();
            m = f.lastModified();
        }
        dir = d;
        size = d ? 0 : s;
        modified = m;
        kind = d ? Fx.K_DIR : Fx.kind(name);
    }

    File cover;                   // media folder groups: first file, used as thumbnail
    int coverKind;

    /** A folder group in a media category ("Music (14)"). */
    FileItem(File dir, int count, long size, long modified, File cover, int coverKind) {
        file = dir;
        name = dir.getName();
        this.dir = true;
        this.size = size;
        this.modified = modified;
        this.count = count;
        this.cover = cover;
        this.coverKind = coverKind;
        kind = Fx.K_DIR;
    }

    /** From a MediaStore row: no disk access needed. */
    FileItem(File f, long size, long modified) {
        file = f;
        name = f.getName();
        dir = false;
        this.size = size;
        this.modified = modified;
        kind = Fx.kind(name);
    }
}
