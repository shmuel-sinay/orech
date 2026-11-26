package com.orech.mockloc;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;

import java.io.InputStream;
import java.util.UUID;

/** שירות קדמי: קורא NMEA ממשדר GPS (USB או בלוטוס) ומזריק מיקום מדומה למערכת. */
public class GpsFeedService extends Service {
    static final String PREFS = "feed";
    static final String ACTION_STOP = "com.orech.mockloc.STOP";
    static volatile String status = "כבוי";
    static volatile Location last;
    static volatile boolean running;

    private volatile boolean stop;
    private Thread worker;
    private LocationManager lm;
    private final Nmea nmea = new Nmea();

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) { stopSelf(); return START_NOT_STICKY; }
        if (running) return START_STICKY;
        running = true;
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel("feed", "משדר GPS", NotificationManager.IMPORTANCE_LOW));
        Notification n = new Notification.Builder(this, "feed")
            .setSmallIcon(R.drawable.ic_mock).setContentTitle("מיקום ב-USB פעיל")
            .setContentText("קורא נתונים ממשדר GPS").build();
        startForeground(1, n);
        stop = false;
        worker = new Thread(this::loop, "gps-feed");
        worker.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stop = true;
        running = false;
        status = "כבוי";
        if (worker != null) worker.interrupt();
        removeProviders();
        super.onDestroy();
    }

    private void loop() {
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        lm = getSystemService(LocationManager.class);
        try {
            AppOpsManager ops = getSystemService(AppOpsManager.class);
            AppOpsManager.class.getMethod("setMode", String.class, int.class, String.class, int.class)
                .invoke(ops, "android:mock_location", Process.myUid(), getPackageName(), AppOpsManager.MODE_ALLOWED);
            addProviders();
        } catch (Throwable t) {
            status = "שגיאה בהפעלת מיקום: " + (t.getCause() != null ? t.getCause() : t);
        }
        while (!stop) {
            try {
                String src = p.getString("source", "usb");
                int baud = Integer.parseInt(p.getString("baud", "9600"));
                if (src.equals("bt")) readBluetooth(p.getString("btaddr", ""));
                else readUsb(baud);
            } catch (Throwable t) {
                status = "שגיאה: " + t.getMessage();
            }
            if (stop) break;
            if (!p.getBoolean("reconnect", true)) { status = "נותק"; break; }
            try { Thread.sleep(5000); } catch (InterruptedException e) { break; }
        }
        stopSelf();
    }

    private void readUsb(int baud) throws Exception {
        UsbManager um = getSystemService(UsbManager.class);
        UsbDevice dev = null;
        for (UsbDevice d : um.getDeviceList().values()) {
            if (d.getVendorId() == 0x1546 || findAcm(d) != null) { dev = d; break; }
        }
        if (dev == null) { status = "ממתין להתקן USB"; Thread.sleep(2000); return; }
        if (!um.hasPermission(dev)) { status = "אין הרשאת USB להתקן. נתק וחבר מחדש ואשר"; Thread.sleep(3000); return; }
        UsbInterface[] ifs = findAcm(dev);
        if (ifs == null) { status = "ההתקן אינו CDC-ACM"; Thread.sleep(3000); return; }
        UsbDeviceConnection c = um.openDevice(dev);
        if (c == null) { status = "פתיחת ההתקן נכשלה"; return; }
        try {
            UsbInterface comm = ifs[0], data = ifs[1];
            if (comm != null) c.claimInterface(comm, true);
            c.claimInterface(data, true);
            UsbEndpoint in = null;
            for (int i = 0; i < data.getEndpointCount(); i++) {
                UsbEndpoint e = data.getEndpoint(i);
                if (e.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && e.getDirection() == UsbConstants.USB_DIR_IN) in = e;
            }
            if (in == null) { status = "אין נקודת קריאה"; return; }
            if (comm != null) {
                byte[] lc = {(byte) baud, (byte) (baud >> 8), (byte) (baud >> 16), (byte) (baud >> 24), 0, 0, 8};
                c.controlTransfer(0x21, 0x20, 0, comm.getId(), lc, 7, 1000);
                c.controlTransfer(0x21, 0x22, 3, comm.getId(), null, 0, 1000);
            }
            status = "מחובר ל-USB, ממתין לנתונים";
            byte[] buf = new byte[512];
            StringBuilder sb = new StringBuilder();
            while (!stop) {
                int n = c.bulkTransfer(in, buf, buf.length, 1500);
                if (n > 0) {
                    sb.append(new String(buf, 0, n, "US-ASCII"));
                    drain(sb);
                } else if (n < 0 && !um.getDeviceList().containsValue(dev)) {
                    status = "ההתקן נותק";
                    return;
                }
            }
        } finally {
            c.close();
        }
    }

    /** מחזיר {ממשק בקרה, ממשק נתונים} של CDC-ACM, או ממשק נתונים בודד כשאין ממשק בקרה. */
    private static UsbInterface[] findAcm(UsbDevice d) {
        UsbInterface comm = null, data = null;
        for (int i = 0; i < d.getInterfaceCount(); i++) {
            UsbInterface u = d.getInterface(i);
            if (u.getInterfaceClass() == 2) comm = u;
            else if (u.getInterfaceClass() == 10) data = u;
        }
        if (data != null) return new UsbInterface[]{comm, data};
        for (int i = 0; i < d.getInterfaceCount(); i++) {
            UsbInterface u = d.getInterface(i);
            for (int e = 0; e < u.getEndpointCount(); e++) {
                if (u.getEndpoint(e).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
                    && u.getEndpoint(e).getDirection() == UsbConstants.USB_DIR_IN && d.getVendorId() == 0x1546)
                    return new UsbInterface[]{null, u};
            }
        }
        return null;
    }

    private void readBluetooth(String addr) throws Exception {
        BluetoothAdapter ad = BluetoothAdapter.getDefaultAdapter();
        if (ad == null || !ad.isEnabled()) { status = "הבלוטוס כבוי"; Thread.sleep(3000); return; }
        if (addr.isEmpty()) { status = "לא נבחר התקן בלוטוס"; Thread.sleep(3000); return; }
        BluetoothDevice d = ad.getRemoteDevice(addr);
        BluetoothSocket s = d.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        try {
            ad.cancelDiscovery();
            status = "מתחבר לבלוטוס...";
            s.connect();
            status = "מחובר לבלוטוס, ממתין לנתונים";
            InputStream is = s.getInputStream();
            byte[] buf = new byte[512];
            StringBuilder sb = new StringBuilder();
            int n;
            while (!stop && (n = is.read(buf)) > 0) {
                sb.append(new String(buf, 0, n, "US-ASCII"));
                drain(sb);
            }
        } finally {
            try { s.close(); } catch (Exception ignored) { }
        }
    }

    private void drain(StringBuilder sb) {
        int nl;
        while ((nl = sb.indexOf("\n")) >= 0) {
            String line = sb.substring(0, nl);
            sb.delete(0, nl + 1);
            if (nmea.feed(line)) publish();
        }
        if (sb.length() > 4096) sb.setLength(0);
    }

    /** גם בדיקת סימולציה משתמשת בנתיב הזה. */
    static void injectSample(Context c) {
        new Thread(() -> {
            GpsFeedService s = new GpsFeedService();
            s.lm = c.getSystemService(LocationManager.class);
            try {
                AppOpsManager ops = c.getSystemService(AppOpsManager.class);
                AppOpsManager.class.getMethod("setMode", String.class, int.class, String.class, int.class)
                    .invoke(ops, "android:mock_location", Process.myUid(), c.getPackageName(), AppOpsManager.MODE_ALLOWED);
                s.addProviders();
                String[] lines = {
                    "$GPRMC,123519,A,3150.0000,N,03512.0000,E,000.5,084.4,230394,003.1,W*6A",
                    "$GPGGA,123519,3150.0000,N,03512.0000,E,1,08,0.9,545.4,M,46.9,M,,*47"};
                for (int i = 0; i < 3; i++) {
                    for (String l : lines) if (s.nmea.feed(l)) s.publish();
                    Thread.sleep(1000);
                }
                Thread.sleep(15000);
                if (!running) s.removeProviders();
            } catch (Throwable t) {
                status = "בדיקה נכשלה: " + (t.getCause() != null ? t.getCause() : t);
            }
        }).start();
    }

    private void addProviders() {
        for (String prov : new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER}) {
            try { lm.removeTestProvider(prov); } catch (Exception ignored) { }
            lm.addTestProvider(prov, false, false, false, false, true, true, true,
                ProviderProperties.POWER_USAGE_LOW, ProviderProperties.ACCURACY_FINE);
            lm.setTestProviderEnabled(prov, true);
        }
    }

    private void removeProviders() {
        if (lm == null) return;
        for (String prov : new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER}) {
            try { lm.removeTestProvider(prov); } catch (Exception ignored) { }
        }
    }

    private void publish() {
        for (String prov : new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER}) {
            Location l = new Location(prov);
            l.setLatitude(nmea.lat);
            l.setLongitude(nmea.lon);
            l.setAltitude(nmea.alt);
            l.setAccuracy((float) Math.max(3, nmea.hdop * 5));
            if (nmea.hasRmc) { l.setSpeed((float) nmea.speedMps); l.setBearing((float) nmea.bearing); }
            l.setTime(System.currentTimeMillis());
            l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            try { lm.setTestProviderLocation(prov, l); } catch (Exception e) { status = "הזרקה נכשלה: " + e; return; }
            last = l;
        }
        status = "קיבוע: " + nmea.sats + " לוויינים";
    }
}