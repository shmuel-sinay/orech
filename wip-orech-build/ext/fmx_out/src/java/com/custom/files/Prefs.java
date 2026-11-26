package com.custom.files;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/** All user settings in one place. */
final class Prefs {
    static final int BY_NAME = 0, BY_DATE = 1, BY_SIZE = 2, BY_TYPE = 3;

    final SharedPreferences sp;
    int sortBy;
    boolean asc, hidden, grid, groupMedia;
    // built-in viewers
    boolean viewImage, viewVideo, viewAudio, viewText;
    // notifications / bin / advanced
    boolean notifyFull, trashDefault, confirmDelete, navIcon, usb, history, systemStorage;
    boolean decimalUnits;
    // advanced menu items
    boolean menuBookmark, menuShortcut, menuHide, menuOpenAs;

    Prefs(Context c) {
        sp = c.getSharedPreferences("settings", 0);
        sortBy = sp.getInt("sortBy", BY_NAME);
        asc = sp.getBoolean("asc", true);
        hidden = sp.getBoolean("hidden", false);
        grid = sp.getBoolean("grid", false);
        groupMedia = sp.getBoolean("groupMedia", true);
        viewImage = sp.getBoolean("viewImage", true);
        viewVideo = sp.getBoolean("viewVideo", true);
        viewAudio = sp.getBoolean("viewAudio", true);
        viewText = sp.getBoolean("viewText", true);
        notifyFull = sp.getBoolean("notifyFull", true);
        trashDefault = sp.getBoolean("trashDefault", true);
        confirmDelete = sp.getBoolean("confirmDelete", true);
        navIcon = sp.getBoolean("navIcon", false);
        usb = sp.getBoolean("usb", true);
        history = sp.getBoolean("history", true);
        systemStorage = sp.getBoolean("systemStorage", false);
        decimalUnits = sp.getBoolean("decimalUnits", false);
        menuBookmark = sp.getBoolean("menuBookmark", true);
        menuShortcut = sp.getBoolean("menuShortcut", true);
        menuHide = sp.getBoolean("menuHide", false);
        menuOpenAs = sp.getBoolean("menuOpenAs", true);
        Fx.decimal = decimalUnits;
    }

    void save() {
        Fx.decimal = decimalUnits;
        sp.edit().putInt("sortBy", sortBy).putBoolean("asc", asc)
                .putBoolean("hidden", hidden).putBoolean("grid", grid).putBoolean("groupMedia", groupMedia)
                .putBoolean("viewImage", viewImage).putBoolean("viewVideo", viewVideo)
                .putBoolean("viewAudio", viewAudio).putBoolean("viewText", viewText)
                .putBoolean("notifyFull", notifyFull).putBoolean("trashDefault", trashDefault)
                .putBoolean("confirmDelete", confirmDelete).putBoolean("navIcon", navIcon)
                .putBoolean("usb", usb).putBoolean("history", history)
                .putBoolean("systemStorage", systemStorage).putBoolean("decimalUnits", decimalUnits)
                .putBoolean("menuBookmark", menuBookmark).putBoolean("menuShortcut", menuShortcut)
                .putBoolean("menuHide", menuHide).putBoolean("menuOpenAs", menuOpenAs)
                .apply();
    }

    private ArrayList<String> list(String key) {
        ArrayList<String> out = new ArrayList<>();
        for (String s : sp.getString(key, "").split("\n")) if (!s.isEmpty()) out.add(s);
        return out;
    }

    ArrayList<String> bookmarks() { return list("bm"); }

    boolean isBookmarked(String path) { return bookmarks().contains(path); }

    void setBookmarked(String path, boolean on) {
        ArrayList<String> b = bookmarks();
        b.remove(path);
        if (on) b.add(path);
        sp.edit().putString("bm", String.join("\n", b)).apply();
    }

    ArrayList<String> recent() { return list("recent"); }

    void addRecent(String path) {
        if (!history) return;
        ArrayList<String> r = recent();
        r.remove(path);
        r.add(0, path);
        while (r.size() > 8) r.remove(r.size() - 1);
        sp.edit().putString("recent", String.join("\n", r)).apply();
    }

    void clearRecent() { sp.edit().remove("recent").apply(); }
}
