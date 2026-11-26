package com.custom.files;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** List / grid rows with view recycling and async thumbnails. */
final class FileAdapter extends BaseAdapter {
    final MainActivity a;
    final ArrayList<FileItem> items = new ArrayList<>();
    boolean grid, selecting, trash;
    final int thumbPx = Ui.dp(96);

    FileAdapter(MainActivity a) { this.a = a; }

    void set(List<FileItem> l) {
        items.clear();
        items.addAll(l);
        notifyDataSetChanged();
    }

    @Override public int getCount() { return items.size(); }

    @Override public Object getItem(int p) { return items.get(p); }

    @Override public long getItemId(int p) { return p; }

    @Override public int getViewTypeCount() { return 2; }

    @Override public int getItemViewType(int p) { return grid ? 1 : 0; }

    static final class H {
        View root;
        FrameLayout tile;
        ImageView icon, folder, thumb, badge, check;
        FrameLayout badgeBox;
        TextView name, info, date;
        Drawable bgNormal, bgSel;
    }

    @Override
    public View getView(int pos, View cv, ViewGroup parent) {
        H h;
        if (cv == null) {
            h = grid ? makeGrid() : makeRow();
            cv = h.root;
            cv.setTag(h);
        } else h = (H) cv.getTag();
        bind(h, items.get(pos));
        return cv;
    }

    /** Icon area: soft tile + type icon, big folder art, thumbnail, folder badge and selection check. */
    private FrameLayout tile(H h, int size, int iconDp, float radius) {
        FrameLayout tile = new FrameLayout(a);
        tile.setBackground(Ui.round(0xFFFFFFFF, radius));
        tile.setClipToOutline(true);
        h.icon = new ImageView(a);
        tile.addView(h.icon, new FrameLayout.LayoutParams(Ui.dp(iconDp), Ui.dp(iconDp), Gravity.CENTER));
        h.folder = new ImageView(a);
        h.folder.setImageResource(R.drawable.ic_folder_big);
        tile.addView(h.folder, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        h.thumb = new ImageView(a);
        tile.addView(h.thumb, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        int bs = Ui.dp(size > 60 ? 26 : 20);
        h.badgeBox = new FrameLayout(a);
        h.badgeBox.setBackground(Ui.round(0xFFFFFFFF, 6));
        h.badgeBox.setElevation(Ui.dp(1));
        h.badge = new ImageView(a);
        int bp = Ui.dp(3);
        h.badge.setPadding(bp, bp, bp, bp);
        h.badgeBox.addView(h.badge, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(bs, bs, Gravity.BOTTOM | Gravity.START);
        blp.setMargins(Ui.dp(2), 0, Ui.dp(2), Ui.dp(3));
        tile.addView(h.badgeBox, blp);
        h.check = Ui.img(a, R.drawable.ic_check, 0xFFFFFFFF);
        h.check.setBackground(Ui.round(0xE62F6F9F, radius));
        int p = Ui.dp(size > 60 ? 26 : 13);
        h.check.setPadding(p, p, p, p);
        tile.addView(h.check, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        h.tile = tile;
        return tile;
    }

    private H makeRow() {
        H h = new H();
        LinearLayout row = Ui.hbox(a);
        Ui.pad(row, 14, 0, 16, 0);
        row.setLayoutParams(new AbsListView.LayoutParams(Ui.MATCH, Ui.dp(72)));
        row.addView(tile(h, 48, 26, 12), Ui.lp(Ui.dp(48), Ui.dp(48)));
        LinearLayout texts = Ui.vbox(a);
        texts.setPaddingRelative(Ui.dp(14), 0, 0, 0);
        h.name = Ui.line(a, "", 16, Ui.TEXT, Ui.REGULAR);
        h.name.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        texts.addView(h.name);
        LinearLayout second = Ui.hbox(a);
        h.info = Ui.line(a, "", 13, Ui.TEXT2, Ui.REGULAR);
        h.info.setTextDirection(View.TEXT_DIRECTION_RTL);
        second.addView(h.info, Ui.lp(0, Ui.WRAP, 1));
        h.date = Ui.line(a, "", 13, Ui.TEXT3, Ui.REGULAR);
        h.date.setTextDirection(View.TEXT_DIRECTION_RTL);
        second.addView(h.date);
        texts.addView(second, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 3, 0, 0));
        row.addView(texts, Ui.lp(0, Ui.WRAP, 1));
        h.bgNormal = Ui.rippleRect();
        h.bgSel = new ColorDrawable(Ui.SELECTED);
        h.root = row;
        return h;
    }

    private H makeGrid() {
        H h = new H();
        LinearLayout cell = Ui.vbox(a);
        cell.setGravity(Gravity.CENTER_HORIZONTAL);
        Ui.pad(cell, 5, 6, 5, 8);
        cell.setLayoutParams(new AbsListView.LayoutParams(Ui.MATCH, Ui.WRAP));
        FrameLayout t = tile(h, 100, 40, 16);
        t.setLayoutParams(Ui.lp(Ui.MATCH, Ui.WRAP));
        cell.addView(new SquareFrame(a, t), Ui.lp(Ui.MATCH, Ui.WRAP));
        h.name = Ui.text(a, "", 13, Ui.TEXT);
        h.name.setSingleLine(true);
        h.name.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        h.name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cell.addView(h.name, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 6, 0, 0));
        h.info = Ui.line(a, "", 11.5f, Ui.TEXT2, Ui.REGULAR);
        h.info.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        h.info.setTextDirection(View.TEXT_DIRECTION_RTL);
        cell.addView(h.info, Ui.lp(Ui.MATCH, Ui.WRAP));
        h.bgNormal = Ui.ripple(0x00FFFFFF, 16);
        h.bgSel = Ui.round(Ui.SELECTED, 16);
        h.root = cell;
        return h;
    }

    /** Keeps the grid thumbnail square. */
    static final class SquareFrame extends FrameLayout {
        SquareFrame(android.content.Context c, View child) {
            super(c);
            addView(child, new LayoutParams(Ui.MATCH, Ui.MATCH));
        }

        @Override protected void onMeasure(int w, int h) { super.onMeasure(w, w); }
    }

    private void bind(H h, FileItem it) {
        h.name.setText(grid && it.dir && it.count >= 0 ? it.name + " (" + it.count + ")" : it.name);
        int k = it.kind;
        boolean folderArt = it.dir && it.cover == null;
        h.folder.setVisibility(folderArt ? View.VISIBLE : View.GONE);
        h.icon.setVisibility(folderArt ? View.GONE : View.VISIBLE);
        h.tile.setBackgroundTintList(folderArt ? Ui.csl(0x00FFFFFF) : Fx.SOFT_CSL[it.cover != null ? it.coverKind : k]);
        if (!folderArt) {
            int ik = it.cover != null ? it.coverKind : k;
            h.icon.setImageResource(Fx.ICON[ik]);
            h.icon.setImageTintList(Fx.COLOR_CSL[ik]);
        }
        // thumbnails: media files, or the cover of a media-folder group
        if (it.cover != null && Thumbs.supports(it.coverKind)) Thumbs.load(a, h.thumb, it.cover, it.coverKind, thumbPx);
        else if (!it.dir && Thumbs.supports(k)) Thumbs.load(a, h.thumb, it.file, k, thumbPx);
        else {
            h.thumb.setTag(null);
            h.thumb.setImageDrawable(null);
            h.thumb.setVisibility(View.GONE);
        }
        int badge = it.cover != null ? R.drawable.ic_folder : it.dir ? Fx.folderBadge(it.name) : 0;
        h.badgeBox.setVisibility(badge != 0 ? View.VISIBLE : View.GONE);
        if (badge != 0) {
            h.badge.setImageResource(badge);
            h.badge.setImageTintList(Ui.csl(it.cover != null ? 0xFFE09A2E : Fx.folderBadgeColor(badge)));
        }
        h.check.setVisibility(it.selected ? View.VISIBLE : View.GONE);
        h.root.setBackground(it.selected ? h.bgSel : h.bgNormal);
        h.root.setAlpha(it.name.startsWith(".") ? 0.6f : 1f);
        h.info.setText(info(it, grid));
        if (h.date != null) h.date.setText(trash ? Fx.date(it.trashTime) : Fx.date(it.modified));
    }

    /** Second line: item count / size (+ where it lives for search & categories). */
    String info(FileItem it, boolean compact) {
        if (trash) {
            File p = it.origPath == null ? null : new File(it.origPath).getParentFile();
            String from = p == null ? "" : p.getName();
            return compact ? Fx.size(it.size) : (it.dir ? "תיקיה" : Fx.size(it.size)) + (from.isEmpty() ? "" : " · מתוך " + from);
        }
        String s;
        if (it.dir) {
            if (compact) return it.cover != null ? Fx.size(it.size) : "";
            s = it.count < 0 ? "" : it.count == 1 ? "פריט אחד" : Fx.num(it.count) + " פריטים";
            if (it.cover != null) s += " · " + Fx.size(it.size);
        } else s = Fx.size(it.size);
        if (!compact && it.showParent) {
            File p = it.file.getParentFile();
            if (p != null) s += " · " + p.getName();
        }
        return s;
    }

    /** Full subtitle for menus. */
    String sub(FileItem it) {
        String i = info(it, false);
        String d = Fx.date(trash ? it.trashTime : it.modified);
        return i.isEmpty() ? d : i + "  ·  " + d;
    }
}
