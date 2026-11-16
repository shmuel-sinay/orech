#!/system/bin/sh
# רץ מוקדם מאוד בעלייה, עם root מלא (Magisk).
# מוחק קבצים שצריכים להיעלם לצמיתות - לא עריכה, רק מחיקה פשוטה.

mount -o remount,rw /vendor 2>/dev/null
rm -f /vendor/bin/hw/*wpa_supplicant* 2>/dev/null

mount -o remount,rw /system 2>/dev/null
rm -f /system/system/preinstall/whatsapp.apk 2>/dev/null
