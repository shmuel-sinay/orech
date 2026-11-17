#!/usr/bin/env python3
"""
מסיר את שורת ה-main_toggle_wifi משלושת קבצי ה-XML, בתוך פרויקט שכבר פורק
ע"י apktool (python3 patch_wifi_rows.py <decoded_dir>).
רץ לפני "apktool b". לא נוגע בשום דבר אחר בקובץ.
"""
import re
import sys
import os

FILES = {
    "res/xml/top_level_settings.xml": "com.android.settings.widget.HomepagePreference",
    "res/xml/network_and_internet.xml": "com.android.settingslib.PrimarySwitchPreference",
    "res/xml/network_provider_settings.xml": "com.android.settingslib.RestrictedSwitchPreference",
}


def remove_element(xml_text, tag, key_value="main_toggle_wifi"):
    # Matches <tag ...key="main_toggle_wifi"... /> (self-closing) across lines.
    pattern = re.compile(
        r'<' + re.escape(tag) + r'\b[^>]*?android:key="' + re.escape(key_value) + r'"[^>]*?/>\s*',
        re.DOTALL)
    new_text, n = pattern.subn('', xml_text)
    return new_text, n


def main(root):
    for rel, tag in FILES.items():
        path = os.path.join(root, rel)
        if not os.path.exists(path):
            print(f"!! לא נמצא: {rel} (דלג)")
            continue
        text = open(path, encoding="utf-8").read()
        new_text, n = remove_element(text, tag)
        if n == 0:
            print(f"!! לא נמצאה שורת main_toggle_wifi ב-{rel} — בדוק ידנית")
            continue
        open(path, "w", encoding="utf-8").write(new_text)
        print(f"הוסר {n} אלמנט מתוך {rel}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: patch_wifi_rows.py <decoded_apk_dir>")
        sys.exit(1)
    main(sys.argv[1])
