package com.custom.files;

import android.app.Dialog;
import android.media.MediaScannerConnection;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Runs a file operation in the background. A progress dialog appears only if the
 * operation takes longer than ~300 ms, so quick actions feel instant.
 */
final class Ops {
    static final class Cancelled extends RuntimeException {
        Cancelled() { super("cancelled"); }
    }

    static final class Op {
        volatile boolean cancel;
        volatile long total, done;
        volatile String cur = "";
        final ArrayList<String> scan = new ArrayList<>();

        void check() { if (cancel) throw new Cancelled(); }

        void scan(File f) {
            synchronized (scan) { if (scan.size() < 4000) scan.add(f.getAbsolutePath()); }
        }
    }

    interface Task { void run(Op op) throws Exception; }

    interface Done { void done(Op op, Exception err); }

    static void run(MainActivity a, String title, Task task, Done done) {
        final Op op = new Op();
        final boolean[] finished = {false};
        final Dialog[] dlg = {null};
        final Runnable show = () -> {
            if (finished[0] || a.isFinishing()) return;
            LinearLayout box = Ui.vbox(a);
            Ui.pad(box, 24, 22, 24, 12);
            box.addView(Ui.text(a, title, 18, Ui.TEXT, Ui.MEDIUM));
            TextView name = Ui.line(a, "", 13.5f, Ui.TEXT2, Ui.REGULAR);
            box.addView(name, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 14, 0, 0));
            ProgressBar pb = new ProgressBar(a, null, android.R.attr.progressBarStyleHorizontal);
            pb.setMax(1000);
            pb.setIndeterminate(true);
            pb.setProgressTintList(Ui.csl(Ui.PRIMARY));
            pb.setIndeterminateTintList(Ui.csl(Ui.PRIMARY));
            pb.setProgressBackgroundTintList(Ui.csl(Ui.DIVIDER));
            box.addView(pb, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 10, 0, 0));
            TextView pct = Ui.text(a, "", 12.5f, Ui.TEXT2);
            pct.setTextDirection(TextView.TEXT_DIRECTION_RTL);
            box.addView(pct, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 4, 0, 0));
            LinearLayout btns = Ui.hbox(a);
            btns.setGravity(Gravity.END);
            btns.addView(Ui.button(a, "ביטול", false, v -> {
                op.cancel = true;
                ((TextView) v).setText("מבטל…");
            }));
            box.addView(btns, Ui.margins(Ui.lp(Ui.MATCH, Ui.WRAP), 0, 10, 0, 0));
            Dialog d = Dialogs.make(a, box, false);
            d.setCancelable(false);
            dlg[0] = d;
            Dialogs.show(d, false);
            new Runnable() {
                @Override public void run() {
                    if (finished[0]) return;
                    name.setText(op.cur);
                    long T = op.total, D = op.done;
                    if (T > 0) {
                        pb.setIndeterminate(false);
                        pb.setProgress((int) Math.min(1000, D * 1000 / T));
                        pct.setText(Fx.size(D) + " מתוך " + Fx.size(T) + "  ·  " + Math.min(100, D * 100 / T) + "%");
                    }
                    MainActivity.H.postDelayed(this, 120);
                }
            }.run();
        };
        MainActivity.H.postDelayed(show, 300);
        MainActivity.BG.execute(() -> {
            Exception err = null;
            try {
                task.run(op);
            } catch (Cancelled c) {
                op.cancel = true;
            } catch (Exception e) {
                err = e;
            } catch (Throwable t) {
                err = new Exception(String.valueOf(t.getMessage()));
            }
            if (!op.scan.isEmpty()) {
                String[] paths;
                synchronized (op.scan) { paths = op.scan.toArray(new String[0]); }
                try { MediaScannerConnection.scanFile(a.getApplicationContext(), paths, null, null); } catch (Exception ignored) { }
            }
            final Exception fe = err;
            MainActivity.H.post(() -> {
                finished[0] = true;
                MainActivity.H.removeCallbacks(show);
                if (dlg[0] != null) try { dlg[0].dismiss(); } catch (Exception ignored) { }
                if (done != null) done.done(op, fe);
            });
        });
    }
}
