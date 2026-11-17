#!/usr/bin/env python3
"""
מסיר שורות/קישורים הקשורים ל-WiFi מתוך פרויקט MtkSettings.apk שכבר פורק
ע"י apktool (python3 patch_wifi_rows.py <decoded_dir>). רץ לפני "apktool b".

הרשימה נבנתה מסקירה מלאה של כל res/xml/*.xml ב-MtkSettings.apk האמיתי -
כל הכניסות (entry points) שמובילות למסכי WiFi, מכל מקום שהם מופיעים
(הגדרות ראשיות, מכשירים מחוברים, טת'רינג, רשת סלולרית, מיקום, הרשאות,
מידע על המכשיר). לא נוגעים ב:
  - development_settings.xml (אפשרויות מפתחים - נשארות זמינות בכוונה)
  - מסכי "עלים" כמו wifi_settings.xml/wifi_tether_settings.xml וכו' - הם
    הופכים לבלתי נגישים ברגע שהכניסה אליהם מוסרת, אז אין צורך לגעת בהם
    בנפרד (וגם ה-WiFi כבוי לגמרי ברמת החומרה בכל מקרה).

כל ערך ב-TARGETS הוא (קובץ יחסי, android:key להסרה). קובץ יכול להופיע
כמה פעמים עם keys שונים.
"""
import re
import sys
import os

TARGETS = [
    ("res/xml/top_level_settings.xml", "main_toggle_wifi"),
    ("res/xml/network_and_internet.xml", "main_toggle_wifi"),
    ("res/xml/network_provider_settings.xml", "main_toggle_wifi"),
    ("res/xml/network_provider_settings.xml", "wifi_status_message_footer"),
    ("res/xml/connected_devices_advanced.xml", "wifi_display_settings"),
    ("res/xml/tether_prefs.xml", "wifi_tether"),
    ("res/xml/mobile_network_settings.xml", "carrier_wifi_toggle"),
    ("res/xml/mobile_network_settings.xml", "carrier_wifi_network"),
    ("res/xml/mobile_network_settings.xml", "wifi_calling"),
    ("res/xml/advanced_calling_settings.xml", "wifi_calling_key"),
    ("res/xml/location_services.xml", "location_services_wifi_scanning"),
    ("res/xml/special_access.xml", "change_wifi_state"),
    ("res/xml/my_device_info.xml", "wifi_ip_address"),
    ("res/xml/my_device_info.xml", "saved_accesspoints_wifi_mac_address"),
    ("res/xml/my_device_info.xml", "wifi_mac_address"),
    ("res/xml/device_info_status.xml", "wifi_ip_address"),
    ("res/xml/device_info_status.xml", "wifi_mac_address"),
]

# תגית כלשהי (Preference/SwitchPreference/PrimarySwitchPreference/...) עם
# android:key="KEY" הנתון, בצורה self-closing. זה כיסה את כל המקרים שנבדקו.
SELF_CLOSING = re.compile(
    r'<[\w.$]+\b[^>]*?android:key="{key}"[^>]*?/>\s*', re.DOTALL
)


def remove_element(xml_text, key):
    pattern = re.compile(SELF_CLOSING.pattern.format(key=re.escape(key)), re.DOTALL)
    return pattern.subn("", xml_text)


def main(root):
    by_file = {}
    for rel, key in TARGETS:
        by_file.setdefault(rel, []).append(key)

    for rel, keys in by_file.items():
        path = os.path.join(root, rel)
        if not os.path.exists(path):
            print(f"!! לא נמצא: {rel} (דלג)")
            continue
        text = open(path, encoding="utf-8").read()
        for key in keys:
            new_text, n = remove_element(text, key)
            if n == 0:
                print(f"!! לא נמצא key=\"{key}\" ב-{rel} - בדוק ידנית")
                continue
            text = new_text
            print(f"הוסר key=\"{key}\" מתוך {rel}")
        open(path, "w", encoding="utf-8").write(text)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: patch_wifi_rows.py <decoded_apk_dir>")
        sys.exit(1)
    main(sys.argv[1])
