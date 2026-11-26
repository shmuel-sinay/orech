package com.orech.mockloc;

/** מפענח NMEA מינימלי: GGA (מיקום, גובה, לוויינים) ו-RMC (מהירות וכיוון). */
final class Nmea {
    double lat, lon, alt, speedMps, bearing, hdop;
    int fix, sats;
    boolean hasFix, hasRmc;

    /** מחזיר true כשהתקבל משפט GGA עם קיבוע, כלומר אפשר לפרסם מיקום. */
    boolean feed(String line) {
        line = line.trim();
        if (line.length() < 12 || line.charAt(0) != '$') return false;
        int star = line.indexOf('*');
        String body = star > 0 ? line.substring(1, star) : line.substring(1);
        if (star > 0 && star + 3 <= line.length()) {
            int cs = 0;
            for (int i = 0; i < body.length(); i++) cs ^= body.charAt(i);
            try {
                if (cs != Integer.parseInt(line.substring(star + 1, star + 3), 16)) return false;
            } catch (NumberFormatException e) { return false; }
        }
        String[] f = body.split(",", -1);
        String type = f[0].length() >= 3 ? f[0].substring(f[0].length() - 3) : f[0];
        if (type.equals("GGA") && f.length > 9) {
            fix = parseInt(f[6]);
            sats = parseInt(f[7]);
            hdop = parseDouble(f[8]);
            if (fix > 0 && !f[2].isEmpty() && !f[4].isEmpty()) {
                lat = coord(f[2], f[3]);
                lon = coord(f[4], f[5]);
                alt = parseDouble(f[9]);
                hasFix = true;
                return true;
            }
            hasFix = false;
        } else if (type.equals("RMC") && f.length > 8) {
            if (f[2].equals("A")) {
                speedMps = parseDouble(f[7]) * 0.514444;
                bearing = parseDouble(f[8]);
                hasRmc = true;
            }
        }
        return false;
    }

    private static double coord(String v, String hemi) {
        int dot = v.indexOf('.');
        int degLen = dot - 2;
        if (degLen < 1) return 0;
        double d = Double.parseDouble(v.substring(0, degLen)) + Double.parseDouble(v.substring(degLen)) / 60.0;
        return (hemi.equals("S") || hemi.equals("W")) ? -d : d;
    }

    private static int parseInt(String s) { try { return Integer.parseInt(s); } catch (Exception e) { return 0; } }
    private static double parseDouble(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0; } }
}