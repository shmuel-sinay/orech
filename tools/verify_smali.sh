#!/usr/bin/env bash
# בודק שקובץ/תיקיית smali מתרכבת (assemble) ומתפרקת (disassemble) חזרה בלי
# שגיאות, לפני שסומכים עליה ומכניסים אותה ל-patches.json. לא נוגע בשום קובץ
# אמיתי של מכשיר - עובד על dex זמני ומבודד בלבד.
#
# שימוש: tools/verify_smali.sh <smali_dir_or_file> [class_name_filter]
#   smali_dir_or_file: תיקייה עם מבנה com/foo/Bar.smali, או קובץ smali בודד
#
# מוריד את smali.jar/baksmali.jar הסטנדרטיים (JesusFreke/smali) לתיקיית
# tools/.cache פעם אחת, ומשתמש בהם מכאן ואילך.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CACHE_DIR="$SCRIPT_DIR/.cache"
WORK_DIR="$(mktemp -d)"
trap 'rm -rf "$WORK_DIR"' EXIT

INPUT="${1:?שימוש: verify_smali.sh <smali_dir_or_file>}"

mkdir -p "$CACHE_DIR"
if [ ! -f "$CACHE_DIR/smali.jar" ]; then
    curl -sL -o "$CACHE_DIR/smali.jar" "https://bitbucket.org/JesusFreke/smali/downloads/smali-2.5.2.jar"
fi
if [ ! -f "$CACHE_DIR/baksmali.jar" ]; then
    curl -sL -o "$CACHE_DIR/baksmali.jar" "https://bitbucket.org/JesusFreke/smali/downloads/baksmali-2.5.2.jar"
fi

SMALI_IN="$WORK_DIR/in"
mkdir -p "$SMALI_IN"
if [ -d "$INPUT" ]; then
    cp -r "$INPUT"/. "$SMALI_IN"/
else
    cp "$INPUT" "$SMALI_IN"/
fi

echo "מרכיב (assemble)..."
java -jar "$CACHE_DIR/smali.jar" assemble "$SMALI_IN" -o "$WORK_DIR/test.dex"

echo "מפרק בחזרה (disassemble) לבדיקת round-trip..."
java -jar "$CACHE_DIR/baksmali.jar" disassemble "$WORK_DIR/test.dex" -o "$WORK_DIR/out"

echo
echo "עבר בהצלחה. פלט ה-round-trip ב: $WORK_DIR/out (יימחק בסיום הסקריפט - להעתיק אם רוצים לשמור)"
find "$WORK_DIR/out" -name "*.smali"
