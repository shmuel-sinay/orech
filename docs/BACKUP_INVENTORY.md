# מלאי גיבוי: M36 / M393 (נכון ל-26.11.2026)

תיקייה מקומית בלבד, לא מפורסמת. כל ה-mirrors נבדקו עם `git fsck`: הכל תקין.

## 1. מה נמצא איפה
| פריט | מיקום | הערה |
|---|---|---|
| ריפויי GitHub (19 mirrors) | `C:\backup_repos\mirrors\*.git` | כולל כל הענפים |
| אורך מקומי | `mirrors\local-orech.git` | כולל `feature/seller-version-picker` |
| vsr-guard-m393 מקומי | `mirrors\local-vsr-guard-m393.git` | |
| orech_build מקומי | `mirrors\local-orech_build.git` | ענפים בלבד, בלי השינויים הלא מקומטים |
| 79 שינויים לא מקומטים | `worktrees\orech_build_working.tgz` | ארכיון בלי `build/` ו-`.gradle/`, כ-300MB, 2362 קבצים |
| מפתח חתימה | `keys\` ו-`D:\orech_keys_backup\` | שני עותקים זהים (hash נבדק) |
| ריפו ציבורי אחד | `github.com/shmuel-sinay/orech` (main) | מכיל m393, vsr-guard, m36. בלי ro2rw (הוסר בקומיט) |

## 2. ענפים שלא מוזגו
- `orech` / `feature/seller-version-picker`: מקדים את main ב-22 קומיטים. נשמר ב-GitHub וב-`mirrors\local-orech.git`. לא מוזג.
- ענפי `claude/*` ב-ro2rw, -atar-, YouTube-downloads: נשמרו ב-mirrors. לא מוזגו, ולא נוגעים בהם בשלב הזה.

## 3. מה לא נמצא ב-GitHub
- 79 השינויים ב-`C:\orech_build` (רק בארכיון המקומי).
- ה-mirror של `ANDROID` כולל רק את הענפים שב-GitHub. ההיסטוריה המלאה שם, לא ב-`orech`.

## 4. לא לגעת בלי אישור
- מחיקה של ריפויים (ANDROID, ro2rw ואחרים).
- שכתוב היסטוריה של `orech` (כדי להסיר את ro2rw מההיסטוריה).
- מיזוג של `feature/seller-version-picker`.

## עדכון: ענף WIP
ענף `wip/orech-build-uncommitted` מכיל את 79 השינויים מ-`C:\orech_build` (תיקייה `wip-orech-build/`). לא הועלו: מפתחות חתימה (`ext/keys`, `fmx_release.keystore`) ו-APK של Google (`SetupWizard*.apk`).
