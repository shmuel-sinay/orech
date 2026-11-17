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
        "targetFile": "/system/framework/services.jar",
        "remount": "/",
        "signingKey": "platform",
        "expectedSha256": "53bf5ee3fd1aa0cb00ff108df63d3d71259b1dc1ee58ac3b442305ec4ec572d6",
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
        "expectedSha256": "09de00181e7877ec2fce5dd2429ada14bae9c3f40eef776ab839025417eea7f1",
        "note": "נבדק בפועל על המכשיר - מוחק את הבינארי הזה (ולא את android.hardware.wifi.xml, שמוחק אותו שובר את עליית המכשיר!) מכבה WiFi לגמרי בלי לפגוע בעלייה.",
    },
    {
        "id": "whatsapp_removal",
        "title": "הסרת WhatsApp המובנה",
        "kind": "delete",
        "device": "M36 Plus",
        "targetFile": "/system/preinstall/whatsapp.apk",
        "remount": "/",
        "expectedSha256": "ae2370a1f073d72a907c49c70d6d1f946d898daa93555087fcc5248a7b7a20a3",
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
        "targetFile": "/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk",
        "remount": "/",
        "signingKey": "platform",
        "expectedSha256": "10548d8bcfb83b45840d725d5e82d5135b7e4ba968f3e05b57560dda53d069e5",
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
        "note": "מקור: פאץ' מהפורום (קרדיט למשתמש בסדבסד), שהורחב בשיחה הזו עם מסך חסימה + 7 הקשות לחשיפת שדה הסיסמה, והסיסמה שונתה ל\"תודה להשם\". זוהי החלפת קבצי smali שלמים (כולל שלוש מחלקות פנימיות חדשות), לא רק מתודה בודדת. בדקתי ישירות: המפתח האמיתי שחתם על GooglePackageInstaller.apk במקור הוא לא testkey/platform/shared/media (ארבעת מפתחות AOSP הסטנדרטיים) - כנראה מפתח ייחודי ל-MediaTek/OEM שאין לי גישה אליו. זה בסדר כאן: ה-APK לא מגדיר sharedUserId (אין צורך בחתימה תואמת לאפליקציה אחרת), אז חתימה עם platform אמורה לעבוד תפקודית גם אם היא לא זהה למקור.",
    },
    {
        "id": "m393_adb_always_on_fix",
        "title": "תיקון באג 'adb פתוח תמיד' (M393 בלבד!)",
        "kind": "edit",
        "device": "M393",
        "targetFile": "__TODO__ נתיב ל-services-3.jar במכשיר M393 - לא אומת, להשלים",
        "remount": "/",
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
        "targetFile": "/system/system_ext/priv-app/MtkSystemUI/MtkSystemUI.apk",
        "remount": "/",
        "signingKey": "platform",
        "expectedSha256": "394d23cfcfdbac31de1aeca9d02d276928b54f8106e93fc6b3d4909067ca9ba4",
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
            "מלאה (round-trip, ראו tools/verify_smali.sh) על dex נפרד - נכשל בפועל "
            "במכשיר (SystemUI בלופ קריסות) כש-signingKey היה testkey. אומת ישירות "
            "(השוואת SHA-256 של המפתח האמיתי בתוך MtkSystemUI.apk) ש-platform הוא "
            "המפתח הנכון - וש-MtkSystemUI.apk מגדיר sharedUserId=android.uid.systemui, "
            "כך שחתימה לא תואמת גורמת לאובדן הרשאות ברמת signature ולקריסה. "
            "signingKey תוקן ל-platform."
        ),
    },
    {
        "id": "mtksettings_fixes",
        "title": "הגדרות: חסימת 'אפשרויות מפתחים' בסיסמה + הסרת כל כניסות ה-WiFi",
        "kind": "edit",
        "device": "M36 Plus",
        "targetFile": "/system/system_ext/priv-app/MtkSettings/MtkSettings.apk",
        "remount": "/",
        "signingKey": "platform",
        "expectedSha256": "16f19026c15e34aabe98f69e2106cc193c2ffb8fc2260c8f5f72efcbaa7e7b9b",
        "edits": [
            {
                "kind": "method",
                "className": "Lcom/android/settings/deviceinfo/BuildNumberPreferenceController;",
                "methodName": "enableDevelopmentSettings",
                "newMethodSmali": read("devoptions_enableDevelopmentSettings.smali"),
            },
            {
                "kind": "class",
                "className": "Lcom/orech/patcher/DevOptionsUnlockListener;",
                "newClassSmali": read("DevOptionsUnlockListener.smali"),
            },
        ],
        "resourceRemovals": [
            {"path": "res/xml/top_level_settings.xml", "key": "main_toggle_wifi"},
            {"path": "res/xml/network_and_internet.xml", "key": "main_toggle_wifi"},
            {"path": "res/xml/network_provider_settings.xml", "key": "main_toggle_wifi"},
            {"path": "res/xml/network_provider_settings.xml", "key": "wifi_status_message_footer"},
            {"path": "res/xml/connected_devices_advanced.xml", "key": "wifi_display_settings"},
            {"path": "res/xml/tether_prefs.xml", "key": "wifi_tether"},
            {"path": "res/xml/mobile_network_settings.xml", "key": "carrier_wifi_toggle"},
            {"path": "res/xml/mobile_network_settings.xml", "key": "carrier_wifi_network"},
            {"path": "res/xml/mobile_network_settings.xml", "key": "wifi_calling"},
            {"path": "res/xml/advanced_calling_settings.xml", "key": "wifi_calling_key"},
            {"path": "res/xml/location_services.xml", "key": "location_services_wifi_scanning"},
            {"path": "res/xml/special_access.xml", "key": "change_wifi_state"},
            {"path": "res/xml/my_device_info.xml", "key": "wifi_ip_address"},
            {"path": "res/xml/my_device_info.xml", "key": "saved_accesspoints_wifi_mac_address"},
            {"path": "res/xml/my_device_info.xml", "key": "wifi_mac_address"},
            {"path": "res/xml/device_info_status.xml", "key": "wifi_ip_address"},
            {"path": "res/xml/device_info_status.xml", "key": "wifi_mac_address"},
        ],
        "note": (
            "שני דברים על אותו קובץ באותה עריכה אחת (כדי שלא יתחרו על ה-hash של "
            "המקור): (1) אחרי 7 הקשות על מספר ה-build, enableDevelopmentSettings() "
            "הוא המתודה היחידה שבאמת מפעילה Developer options - עודכן כך שבמקום "
            "להפעיל ישר, היא מציגה דיאלוג סיסמה ('תודה להשם', אותה סיסמה כמו "
            "בחסימת ההתקנות), ורק בסיסמה נכונה קוראת בעצמה ל-DevelopmentSettingsEnabler. "
            "(2) הוסרו כל נקודות הכניסה האמיתיות ל-WiFi שנמצאו בסקירה מלאה של כל "
            "res/xml/*.xml ב-MtkSettings.apk האמיתי (Cast, Hotspot, WiFi Calling, "
            "מיקום, הרשאות מיוחדות, מידע מכשיר) - בלי apktool/PC, עם ARSCLib ישירות "
            "על resources.arsc/AXML. Developer Options עצמן לא נגעו בהן בכוונה. "
            "מסכי WiFi 'עלים' (wifi_settings.xml וכו') לא נגעו בהם - בלתי נגישים "
            "ברגע שהכניסה מוסרת, וממש כך WiFi כבוי ברמת החומרה בכל מקרה. "
            "מפתח החתימה platform אומת ישירות על הקובץ הזה (כולל "
            "sharedUserId=android.uid.system). שני החלקים אומתו בנפרד (round-trip "
            "לחלק ה-dex, הרצה אמיתית מול MtkSettings.apk האמיתי לחלק המשאבים) - "
            "אבל עדיין לא נבדקו משולבים ובפועל על המכשיר."
        ),
    },
]

if __name__ == "__main__":
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w", encoding="utf-8") as f:
        json.dump(patches, f, ensure_ascii=False, indent=2)
    print(f"נכתב בהצלחה: {OUT} ({len(patches)} תיקונים)")
