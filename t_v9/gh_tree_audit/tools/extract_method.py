#!/usr/bin/env python3
"""
כלי קריאה-בלבד לחילוץ מתודה בודדת מתוך APK/JAR אמיתי, לצורך כתיבת smali
תחליף ביד - בלי להריץ apktool/baksmali על קובץ מערכת אמיתי, ובלי לבנות/לחתום/
להעביר כלום. פלט: רשימת instructions גולמית + כתובות byte, כדי שאפשר לחשב
בדיוק target-ים של קפיצות (branch offset בדקס הוא במידות של code-units,
כלומר *2 בבייטים, לא ישירות ה-+XXh שמודפס).

התקנה (פעם אחת): pip install androguard colorama

שימוש:
    python3 tools/extract_method.py <path.apk> <Lcom/foo/Bar;> <methodName>

דוגמה:
    python3 tools/extract_method.py MtkSystemUI.apk \\
        'Lcom/android/keyguard/KeyguardPatternViewController$UnlockPatternListener;' \\
        onPatternDetected
"""
import logging
import sys


def main():
    if len(sys.argv) != 4:
        print(__doc__)
        sys.exit(1)

    apk_path, class_name, method_name = sys.argv[1:4]

    logging.disable(logging.CRITICAL)
    from androguard.misc import AnalyzeAPK

    _, _, dx = AnalyzeAPK(apk_path)
    cls = dx.get_class_analysis(class_name)
    if cls is None:
        print(f"לא נמצאה מחלקה: {class_name}")
        sys.exit(1)

    found = False
    for m in cls.get_methods():
        meth = m.get_method()
        if meth.get_name() != method_name:
            continue
        found = True
        print(f".method public {method_name}{meth.get_descriptor()}")
        code = meth.get_code()
        if code is None:
            print("    ; אין קוד (abstract/native?)")
        else:
            print(f"    .registers {code.get_registers_size()}\n")
            for idx, ins in meth.get_instructions_idx():
                print(f"    {idx:04x}: {ins.get_name()} {ins.get_output(idx)}")
        print(".end method")

    if not found:
        print(f"לא נמצאה מתודה בשם {method_name} במחלקה {class_name}")
        sys.exit(1)

    print(
        "\n--- הערה ---\n"
        "הכתובות (0000, 0004, ...) הן byte offsets אמיתיים. אבל ה-offset המודפס\n"
        "בהוראות if-* / goto (למשל '+024h') הוא במידות של code units (2 בייט כל\n"
        "אחד) - צריך להכפיל ב-2 ולהוסיף לכתובת ההוראה הנוכחית כדי לקבל את כתובת\n"
        "המטרה בבייטים, ואז להתאים label לכתובת הזו כשכותבים smali תקין.\n"
        "אחרי כתיבת ה-smali - להריץ tools/verify_smali.sh כדי לבדוק הרכבה+פירוק\n"
        "לפני שמוסיפים את זה ל-patches.json.",
        file=sys.stderr,
    )


if __name__ == "__main__":
    main()
