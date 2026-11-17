#!/usr/bin/env python3
"""
בונה את app/src/main/assets/patches/patches.json מתוך קבצי ה-smali האמיתיים
שנמצאים ב-tools/reference_smali/ - כדי שאפשר יהיה לבדוק/לעדכן כל תיקון כקובץ
smali נפרד, בלי לערוך JSON ענקי ביד.

הרצה: python3 tools/build_patches_json.py   (מתוך שורש הריפו)
"""
import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
REF = os.path.join(ROOT, "tools", "reference_smali")
OUT = os.path.join(ROOT, "app", "src", "main", "assets", "patches", "patches.json")


def read(name):
    with open(os.path.join(REF, name), encoding="utf-8") as f:
        return f.read().strip()


patches = [
    {
        "id": "recent_apps_limit",
        "title": "ביטול מגבלת 3 אפליקציות אחרונות",
        "kind": "edit",
        "device": "M36 Plus",
        "targetFile": "/system/system/framework/services.jar",
        "remount": "/system",
        "signingKey": "platform",
        "edits": [
            {
                "kind": "method",
                "className": "Lcom/android/server/wm/RecentTasks;",
                "methodName": "checkRecentTask",
                "newMethodSmali": read("recent_apps_limit_checkRecentTask.smali"),
            }
        ],
        "note": "אומת ישירות על services.jar המקורי של M36 Plus. המנוע מאתר בעצמו את ה-classes*.dex הנכון, אז אין צורך לדעת מראש אם זה classes.dex או classes2.dex.",
    },
    {
        "id": "wifi_disable",
        "title": "ביטול WiFi לגמרי",
        "kind": "delete",
        "device": "M36 Plus",
        "targetFile": "/vendor/bin/hw/wpa_supplicant",
        "remount": "/vendor",
        "note": "נבדק בפועל על המכשיר - מוחק את הבינארי הזה (ולא את android.hardware.wifi.xml, שמוחק אותו שובר את עליית המכשיר!) מכבה WiFi לגמרי בלי לפגוע בעלייה.",
    },
    {
        "id": "whatsapp_removal",
        "title": "הסרת WhatsApp המובנה",
        "kind": "delete",
        "device": "M36 Plus",
        "targetFile": "/system/system/preinstall/whatsapp.apk",
        "remount": "/system",
    },
    {
        "id": "hide_status_bar_icons",
        "title": "הסתרת סמלי WiFi/סלולר/cast משורת הסטטוס",
        "kind": "setting",
        "device": "M36 Plus",
        "settingNamespace": "secure",
        "settingKey": "icon_blacklist",
        "settingValue": "wifi,mobile,cast",
        "note": "צריך reboot מלא (לא רק הפעלה מחדש של SystemUI) כדי שהסמלים באמת יתעלמו.",
    },
    {
        "id": "installer_password_lock",
        "title": "חסימת התקנת אפליקציות בסיסמה (תודה להשם)",
        "kind": "edit",
        "device": "M36 Plus",
        "targetFile": "/system/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk",
        "remount": "/system",
        "signingKey": "platform",
        "edits": [
            {
                "kind": "class",
                "className": "Lcom/android/packageinstaller/InstallStart;",
                "newClassSmali": read("installstart.smali"),
            },
            {
                "kind": "class",
                "className": "Lcom/android/packageinstaller/InstallStart$CancelClickListener;",
                "newClassSmali": read("installstart-cancelclicklistener.smali"),
            },
            {
                "kind": "class",
                "className": "Lcom/android/packageinstaller/InstallStart$PasswordClickListener;",
                "newClassSmali": read("installstart-passwordclicklistener.smali"),
            },
            {
                "kind": "class",
                "className": "Lcom/android/packageinstaller/InstallStart$BlockedClickListener;",
                "newClassSmali": read("installstart-blockedclicklistener.smali"),
            },
        ],
        "note": "מקור: פאץ' מהפורום (קרדיט למשתמש בסדבסד), שהורחב בשיחה הזו עם מסך חסימה + 7 הקשות לחשיפת שדה הסיסמה, והסיסמה שונתה ל\"תודה להשם\". זוהי החלפת קבצי smali שלמים (כולל שלוש מחלקות פנימיות חדשות), לא רק מתודה בודדת. מפתח החתימה platform נבחר כי GooglePackageInstaller הוא priv-app שחולק הרשאות ברמת platform - לא אומת ב-100% איזה מפתח חתם בפועל על ההתקנה המקורית.",
    },
    {
        "id": "m393_adb_always_on_fix",
        "title": "תיקון באג 'adb פתוח תמיד' (M393 בלבד!)",
        "kind": "edit",
        "device": "M393",
        "targetFile": "__TODO__ נתיב ל-services-3.jar במכשיר M393 - לא אומת, להשלים",
        "remount": "/system",
        "signingKey": "platform",
        "edits": [
            {
                "kind": "method",
                "className": "Lcom/android/server/usb/UsbDeviceManager$UsbHandler;",
                "methodName": "setAdbEnabled",
                "newMethodSmali": read("m393_adb_setAdbEnabled.smali"),
            }
        ],
        "note": "חשוב: הבאג הזה קיים רק בגרסת services-3.jar שעבר פאץ' קהילתי של M393 - אומת שהוא לא קיים ב-services.jar המקורי של M36 Plus! אל תפעיל את זה על M36 Plus. ה-targetFile עדיין לא הושלם - צריך למלא את הנתיב האמיתי בקובץ ב-M393 לפני הפעלה.",
    },
    {
        "id": "keyguard_backdoor",
        "title": "הסרת דלת אחורית בנעילת המסך (תבנית Z הפוכה)",
        "kind": "edit",
        "device": "M36 Plus",
        "targetFile": "/system/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk",
        "remount": "/system",
        "signingKey": "testkey",
        "edits": [
            {
                "kind": "method",
                "className": "Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;",
                "methodName": "onPatternDetected",
                "newMethodSmali": read("keyguard_onPatternDetected.smali"),
            }
        ],
        "note": (
            "המתודה האמיתית חולצה בעצמי מתוך MtkSystemUI.apk האמיתי (קריאה בלבד - "
            "androguard, בלי לבנות/לחתום/להעביר כלום בעצמי - ראו tools/extract_method.py), "
            "אומתה שהבלוק של '9875321'/persist.sys.secret הוא אכן הדלת האחורית (לפי "
            "מתמטיקת ה-offsets האמיתית של הקפיצות), והוחלפה ב-smali שנבדק בהרכבה+פירוק "
            "מלאה (round-trip, ראו tools/verify_smali.sh) על dex נפרד - לא נבדק עדיין "
            "בפועל על המכשיר. מפתח החתימה testkey נבחר לפי האפיון הקודם של המכשיר - "
            "לא נבדק ספציפית על הקובץ הזה."
        ),
    },
    {
        "id": "settings_wifi_rows_removal",
        "title": "הסרת שורת ה-WiFi מתפריט ההגדרות (לא אוטומטי באפליקציה)",
        "kind": "info",
        "device": "M36 Plus",
        "infoText": (
            "זו עריכת משאבים בינארית (resources.arsc / AXML) בתוך MtkSettings.apk - "
            "לא dex, ולכן המנוע הנוכחי של האפליקציה (שעובד רק על smali/dex) לא יכול "
            "לבצע אותה. התהליך שעבד בעבר רץ על המחשב עם apktool - ראו docs/wifi_row_removal_README.md "
            "ו-tools/patch_wifi_rows.py. אפשר להוסיף תמיכה כזו לאפליקציה בעזרת ספריית "
            "עיבוד AXML/ARSC (כמו ARSCLib) - זה פרויקט בפני עצמו, לא נעשה עדיין."
        ),
    },
]

if __name__ == "__main__":
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w", encoding="utf-8") as f:
        json.dump(patches, f, ensure_ascii=False, indent=2)
    print(f"נכתב בהצלחה: {OUT} ({len(patches)} תיקונים)")
