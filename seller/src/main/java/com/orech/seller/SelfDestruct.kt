package com.orech.seller

import android.content.Context
import com.orech.patcher.RootShell

/**
 * סיום התהליך: מסירה מהמכשיר כל מה שמאפשר ללקוח לבטל את החסימות, מסירה את האפליקציה עצמה ומאתחלת.
 * הכול רץ מתוך קונכייה מנותקת שממשיכה לרוץ אחרי שתהליך האפליקציה נהרג בהסרה.
 *
 *  1. הרשאות root: מוחקים את *כל* ההרשאות בטבלת policies של Magisk (גם של adb shell ושל כלי עריכה),
 *     כך שאחרי ההסרה אף אפליקציה לא נשארת עם root.
 *  2. adb ואפשרויות מפתחים: מכבים את שניהם.
 *  3. כלי פירוק והחלפה של קבצי מערכת (MT Manager, apktool) מוסרים.
 *  4. קבצים שנשארו באחסון המשותף, הסרה עצמית ואתחול.
 *
 * ההרשאה ב-Magisk נשמרת לפי uid (טבלת policies). כל שלב רץ בנפרד ונכשל בשקט בלי לעצור את ההסרה.
 * הרשימה של הכלים להסרה נמצאת כאן ואפשר להרחיב אותה.
 */
object SelfDestruct {

    /** חבילות של כלי פירוק/עריכת מערכת שלא צריכות להישאר אצל לקוח. */
    private val toolPackages = listOf(
        "bin.mt.plus.canary",
        "bin.mt.plus",
        "ru.maximoff.apktool"
    )

    /**
     * חבילת אפליקציית הניהול של הפורק (com.vexlo.panel7q). הפורק נותן לה root
     * בשקט לפי שם+חתימה (build_su_info ב-su/daemon.rs), לא לפי טבלת policies -
     * לכן היא חייבת להיות ברשימת ההסרה כאן, לא רק אפליקציית המוכר עצמה.
     */
    private const val MANAGER_PACKAGE_NAME = "com.vexlo.panel7q"

    fun run(context: Context) {
        val pkg = context.packageName
        val steps = mutableListOf<String>()

        // DELETE FROM policies הוא no-op מול הפורק הנוכחי - su/daemon.rs בודק
        // שם חבילה+חתימה (manager/seller) ואת adb_root ב-DbSettings, לא את
        // טבלת policies של Magisk המקורי. נשאר כאן לשם ניקיון, לא כהגנה.
        steps += "magisk --sqlite \"DELETE FROM policies\""
        steps += "magisk --sqlite \"DELETE FROM logs\""
        // ההגנה האמיתית מול הפורק: לכבות adb_root ולהסיר את אפליקציית הניהול -
        // בלעדיה אין יותר אף אפליקציה ש-build_su_info יזהה ויעניק לה root בשקט.
        steps += "magisk --sqlite \"UPDATE settings SET value=0 WHERE key='adb_root'\""
        steps += "pm uninstall $MANAGER_PACKAGE_NAME"

        steps += "settings put global adb_enabled 0"
        steps += "settings put global development_settings_enabled 0"

        for (tool in toolPackages) steps += "pm uninstall $tool"

        steps += "rm -rf /sdcard/Android/data/$pkg /sdcard/Android/media/$pkg"
        steps += "pm uninstall $pkg"
        // אתחול נדרש כדי שהמתקין המתוקן וההגדרות ייכנסו לתוקף.
        steps += "reboot"

        val script = steps.joinToString("; ")
        RootShell.exec("nohup sh -c 'sleep 3; $script' </dev/null >/dev/null 2>&1 &")
    }
}
