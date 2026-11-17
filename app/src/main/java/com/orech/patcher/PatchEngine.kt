package com.orech.patcher

import android.content.Context
import com.android.tools.smali.baksmali.Baksmali
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.smali.Smali
import com.android.tools.smali.smali.SmaliOptions
import com.reandroid.apk.ApkModule
import com.reandroid.arsc.chunk.xml.ResXmlAttribute
import com.reandroid.arsc.chunk.xml.ResXmlElement
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/** פעולה אמיתית שנשארה "ממתינה" אחרי ריצה מוצלחת ב"בדיקה בלבד" - כדי ש-commit() יוכל לבצע אותה בלי לחזור על כל הצנרת (pull/פירוק/עריכה/הרכבה/חתימה/אימות). */
private sealed class PendingCommit {
    data class PushFile(val localFile: File, val remotePath: String, val remount: String) : PendingCommit()
    data class DeleteFile(val remotePath: String, val remount: String) : PendingCommit()
    data class PutSetting(val namespace: String, val key: String, val value: String) : PendingCommit()
}

/**
 * מנוע כללי: מקבל Patch ומבצע את הפעולה המתאימה ל-kind שלו.
 * כל שגיאה - כולל חריגה לא צפויה - נתפסת ונרשמת ללוג המלא, לא נופלת בשקט.
 * מופע אחד חי לכל חיי ה-Activity (לא נוצר מחדש בכל לחיצה), כדי ש-commit()
 * יוכל להסתמך על מה שכבר הוכן ונבדק ב"בדיקה בלבד" הקודמת.
 */
/**
 * לפי בקשה מפורשת של המשתמש (ולפי ניסיון אישי שלו על המכשיר הזה): לא לחתום
 * בכלל על קבצים אחרי עריכה, גם לא APK-ים. קוד החתימה (Signer.kt) לא נמחק -
 * אם מתברר שצריך אותו בעתיד, להחזיר ל-true זה כל מה שצריך.
 */
private const val SIGN_EDITED_FILES = true

class PatchEngine(
    private val context: Context,
    private val workDir: File,
    private val logger: Logger
) {
    private val pending = mutableMapOf<String, PendingCommit>()

    fun apply(patch: Patch, dryRun: Boolean, log: (String) -> Unit): Boolean {
        fun l(msg: String) {
            log(msg)
            logger.log("[${patch.id}] $msg")
        }

        return try {
            patch.note?.let { l("הערה: $it") }
            when (patch.kind) {
                "info" -> {
                    l("זו עריכה ידנית בלבד - האפליקציה לא מבצעת כאן שום פעולה במכשיר:")
                    l(patch.infoText ?: "(אין טקסט הסברה)")
                    true
                }
                "setting" -> applySetting(patch, dryRun, ::l)
                "delete" -> applyDelete(patch, dryRun, ::l)
                "edit" -> applyEdit(patch, dryRun, ::l)
                else -> {
                    l("סוג תיקון לא מוכר: ${patch.kind}")
                    false
                }
            }
        } catch (e: Throwable) {
            l("שגיאה לא צפויה: ${e.message}")
            logger.log("[${patch.id}] EXCEPTION:\n${e.stackTraceToString()}")
            false
        }
    }

    /**
     * מבצע בפועל פעולה שנשארה ממתינה מריצת "בדיקה בלבד" קודמת - בלי לחזור על
     * pull/פירוק/עריכה/הרכבה/חתימה/אימות. אם אין פעולה ממתינה (לא רץ עדיין
     * "בדיקה בלבד" בהצלחה על התיקון הזה) - נכשל עם הודעה ברורה.
     */
    fun commit(patch: Patch, log: (String) -> Unit): Boolean {
        fun l(msg: String) {
            log(msg)
            logger.log("[${patch.id}] $msg")
        }

        val p = pending[patch.id] ?: return false.also {
            l("אין פעולה ממתינה לתיקון הזה - צריך להריץ קודם ב'בדיקה בלבד' בהצלחה")
        }

        val result = when (p) {
            is PendingCommit.PushFile -> {
                l("מעביר בחזרה למכשיר (מהריצה הקודמת שעברה בדיקה)...")
                val (ok, out) = RootShell.push(p.localFile.absolutePath, p.remotePath, p.remount)
                logger.log(out)
                l(if (ok) "הושלם בהצלחה" else "נכשל בהעברה חזרה - ראה את הלוג המלא")
                ok
            }
            is PendingCommit.DeleteFile -> {
                l("מוחק בפועל (מהריצה הקודמת שעברה בדיקה)...")
                val (ok, out) = RootShell.deleteFile(p.remotePath, p.remount)
                logger.log(out)
                l(if (ok) "הושלם" else "נכשל - ראה את הלוג המלא")
                ok
            }
            is PendingCommit.PutSetting -> {
                l("קובע settings בפועל...")
                val (ok, out) = RootShell.putSetting(p.namespace, p.key, p.value)
                logger.log(out)
                l(if (ok) "הושלם - נדרש reboot מלא כדי שהשינוי ישפיע" else "נכשל - ראה את הלוג המלא")
                ok
            }
        }
        if (result) pending.remove(patch.id)
        return result
    }

    private fun applySetting(patch: Patch, dryRun: Boolean, l: (String) -> Unit): Boolean {
        val ns = patch.settingNamespace ?: return false.also { l("חסר settingNamespace") }
        val key = patch.settingKey ?: return false.also { l("חסר settingKey") }
        val value = patch.settingValue ?: return false.also { l("חסר settingValue") }
        if (dryRun) {
            l("מצב בדיקה בלבד - לא משנה settings. הפקודה שתרוץ: settings put $ns $key $value")
            pending[patch.id] = PendingCommit.PutSetting(ns, key, value)
            return true
        }
        l("קובע $ns/$key = $value ...")
        val (ok, out) = RootShell.putSetting(ns, key, value)
        logger.log(out)
        l(if (ok) "הושלם - נדרש reboot מלא כדי שהשינוי ישפיע" else "נכשל - ראה את הלוג המלא")
        return ok
    }

    private fun applyDelete(patch: Patch, dryRun: Boolean, l: (String) -> Unit): Boolean {
        val target = patch.targetFile ?: return false.also { l("חסר targetFile") }
        val remount = patch.remount ?: return false.also { l("חסר remount") }

        if (RootShell.exists(target)) {
            val backupFile = File(context.getExternalFilesDir("backups"), "${patch.id}_${System.currentTimeMillis()}.bak")
            backupFile.parentFile?.mkdirs()
            val (pulled, pullLog) = RootShell.pull(target, backupFile.absolutePath)
            logger.log(pullLog)
            if (pulled) l("גיבוי לפני מחיקה נשמר ב: ${backupFile.absolutePath}")
            else l("אזהרה: הגיבוי לפני המחיקה נכשל - ממשיך בכל זאת")

            if (pulled && !checkExpectedHash(patch, backupFile, l)) return false
        } else {
            l("הקובץ $target לא קיים כבר - אין מה למחוק")
            return true
        }

        if (dryRun) {
            l("מצב בדיקה בלבד - לא נמחק בפועל. היה נמחק: $target")
            pending[patch.id] = PendingCommit.DeleteFile(target, remount)
            return true
        }

        l("מוחק את $target ...")
        val (ok, out) = RootShell.deleteFile(target, remount)
        logger.log(out)
        l(if (ok) "הושלם" else "נכשל - ראה את הלוג המלא")
        return ok
    }

    private fun applyEdit(patch: Patch, dryRun: Boolean, l: (String) -> Unit): Boolean {
        val target = patch.targetFile ?: return false.also { l("חסר targetFile") }
        val remount = patch.remount ?: return false.also { l("חסר remount") }
        val signingKey = patch.signingKey
        if (SIGN_EDITED_FILES && signingKey == null) return false.also { l("חסר signingKey") }
        if (patch.edits.isEmpty() && patch.resourceRemovals.isEmpty()) {
            return false.also { l("אין edits ואין resourceRemovals בתיקון הזה") }
        }

        l("שולף את $target")
        val localOriginal = File(workDir, "${patch.id}_original")
        val (pulled, pullLog) = RootShell.pull(target, localOriginal.absolutePath)
        logger.log(pullLog)
        if (!pulled) {
            l("נכשל ב-pull - ראה את הלוג המלא")
            return false
        }

        val backupFile = File(context.getExternalFilesDir("backups"), "${patch.id}_${System.currentTimeMillis()}.orig")
        backupFile.parentFile?.mkdirs()
        localOriginal.copyTo(backupFile, overwrite = true)
        l("גיבוי הקובץ המקורי נשמר ב: ${backupFile.absolutePath}")

        if (!checkExpectedHash(patch, localOriginal, l)) return false

        // patchedZip מתחיל כעותק של המקור, ושתי שכבות העריכה (dex ואז משאבים)
        // עובדות ברצף על אותו קובץ - כך שאפשר לשלב את שתיהן בפאץ' אחד על אותו
        // קובץ, בלי ששתיהן "יתחרו" זו בזו על hash המקור.
        var patchedZip = File(workDir, "${patch.id}_patched.apk")
        localOriginal.copyTo(patchedZip, overwrite = true)

        if (patch.edits.isNotEmpty()) {
            l("מאתר באיזה classes*.dex נמצאות המחלקות הרלוונטיות...")
            val found = findDexContaining(patchedZip, patch.edits.map { it.className })
            if (found == null) {
                l("לא נמצא classes*.dex שמכיל אף אחת מהמחלקות של התיקון הזה - כנראה גרסה לא תואמת")
                return false
            }
            val (dexEntryName, dexLocalFile) = found
            l("נמצא ב: $dexEntryName")

            val smaliOutDir = File(workDir, "${patch.id}_smali").also { it.deleteRecursively(); it.mkdirs() }
            l("מפרק ל-smali...")
            val dex = DexFileFactory.loadDexFile(dexLocalFile, Opcodes.getDefault())
            Baksmali.disassembleDexFile(dex, smaliOutDir, 1, BaksmaliOptions())

            for (edit in patch.edits) {
                val classRelPath = edit.className.removePrefix("L").removeSuffix(";") + ".smali"
                val smaliFile = File(smaliOutDir, classRelPath)
                when (edit.kind) {
                    "method" -> {
                        if (!smaliFile.exists()) {
                            l("לא נמצא קובץ smali למחלקה ${edit.className} - דולג")
                            return false
                        }
                        l("מחליף את ${edit.methodName} ב-${edit.className}...")
                        if (!replaceMethod(smaliFile, edit.methodName ?: "", edit.newMethodSmali ?: "")) {
                            l("לא נמצאה המתודה ${edit.methodName} - לא בוצע שינוי")
                            return false
                        }
                    }
                    "class" -> {
                        l("כותב קובץ smali שלם ל-${edit.className} (${if (smaliFile.exists()) "מחליף קיים" else "מחלקה חדשה"})...")
                        smaliFile.parentFile?.mkdirs()
                        smaliFile.writeText((edit.newClassSmali ?: "").trim() + "\n")
                    }
                    else -> {
                        l("סוג edit לא מוכר: ${edit.kind}")
                        return false
                    }
                }
            }

            l("מרכיב מחדש ל-dex...")
            val newDexFile = File(workDir, "${patch.id}_new.dex")
            val smaliOptions = SmaliOptions()
            smaliOptions.outputDexFile = newDexFile.absolutePath
            val assembled = Smali.assemble(smaliOptions, listOf(smaliOutDir.absolutePath))
            if (!assembled) {
                l("הרכבה מחדש נכשלה")
                return false
            }

            val afterDex = File(workDir, "${patch.id}_after_dex.apk")
            replaceEntryInZip(patchedZip, afterDex, dexEntryName, newDexFile)
            patchedZip = afterDex
        }

        if (patch.resourceRemovals.isNotEmpty()) {
            // לשחרר כל מה שעיבוד ה-dex (baksmali/smali) עוד מחזיק לפני שטוענים
            // את כל טבלת המשאבים לזיכרון - ARSCLib טוען הכל בבת אחת.
            System.gc()
            if (!applyResourceRemovals(patch, patchedZip, l)) return false
        }

        val signedZip: File
        if (SIGN_EDITED_FILES && signingKey != null) {
            signedZip = File(workDir, "${patch.id}_signed.apk")
            l("חותם עם ${patch.signingKey}...")
            Signer.sign(context, patchedZip, signedZip, signingKey)
            l("קובץ חתום מוכן: ${signedZip.absolutePath} (${signedZip.length()} בייט)")

            l("מאמת את החתימה לפני שממשיכים...")
            val (verified, verifyLog) = Signer.verify(signedZip)
            logger.log(verifyLog)
            if (!verified) {
                l("אימות החתימה נכשל - לא ממשיכים להחזיר למכשיר, ראה את הלוג המלא")
                return false
            }
            l("החתימה אומתה בהצלחה")
        } else {
            signedZip = patchedZip
            l("ללא חתימה (לפי בחירה מפורשת) - הקובץ יוחזר כמו שהוא, רק ה-dex הוחלף")
        }

        if (dryRun) {
            l("מצב בדיקה בלבד - לא הוחזר למכשיר")
            pending[patch.id] = PendingCommit.PushFile(signedZip, target, remount)
            return true
        }

        l("מעביר בחזרה למכשיר...")
        val (pushed, pushLog) = RootShell.push(signedZip.absolutePath, target, remount)
        logger.log(pushLog)
        l(if (pushed) "הושלם בהצלחה" else "נכשל בהעברה חזרה - ראה את הלוג המלא")
        return pushed
    }

    /**
     * מסיר אלמנטי Preference (או כל אלמנט עם android:key) ממשאבי XML בינאריים
     * בתוך ה-APK, לפי (path, key) - בלי apktool/PC, עם ARSCLib. דורס את
     * patchedZip בקובץ חדש; patch.resourceRemovals מגיע מ-patchedZip ולא
     * מ-localOriginal, כך שאפשר לשלב את זה עם עריכות dex על אותו קובץ.
     */
    private fun applyResourceRemovals(patch: Patch, patchedZip: File, l: (String) -> Unit): Boolean {
        l("עורך משאבים בינאריים (${patch.resourceRemovals.size} הסרות)...")
        val module = ApkModule.loadApkFile(patchedZip)
        var totalRemoved = 0
        for (removal in patch.resourceRemovals) {
            val doc = module.getResXmlDocument(removal.path)
            if (doc == null) {
                l("לא נמצא קובץ משאב: ${removal.path} - דולג")
                continue
            }
            val targets = mutableListOf<ResXmlElement>()
            val it = doc.recursiveElements()
            while (it.hasNext()) {
                val e = it.next()
                val attr: ResXmlAttribute? = e.searchAttributeByName("key")
                if (attr != null && removal.key == attr.valueString) targets.add(e)
            }
            if (targets.isEmpty()) {
                l("לא נמצא key=\"${removal.key}\" ב-${removal.path} - דולג")
                continue
            }
            for (t in targets) t.removeSelf()
            l("הוסר key=\"${removal.key}\" מתוך ${removal.path} (${targets.size})")
            totalRemoved += targets.size
        }
        if (totalRemoved == 0) {
            l("אף הסרה לא בוצעה בפועל - לא ממשיכים")
            return false
        }
        val afterResources = File(workDir, "${patch.id}_after_res.apk")
        module.writeApk(afterResources)
        afterResources.copyTo(patchedZip, overwrite = true)
        l("עריכת המשאבים הושלמה (${totalRemoved} אלמנטים הוסרו בסה\"כ)")
        return true
    }

    /** משחזר קובץ גיבוי בחזרה לנתיב המקורי שלו - החזרה חזרה בודדת, בלי לגעת בשום dex. */
    fun restore(backupFile: File, targetFile: String, remount: String, log: (String) -> Unit): Boolean {
        fun l(msg: String) {
            log(msg)
            logger.log("[restore] $msg")
        }
        l("משחזר את ${backupFile.name} בחזרה אל $targetFile ...")
        val (ok, out) = RootShell.push(backupFile.absolutePath, targetFile, remount)
        logger.log(out)
        l(if (ok) "השחזור הושלם" else "השחזור נכשל - ראה את הלוג המלא")
        return ok
    }

    /**
     * מוודא שהקובץ שנשלף הוא בדיוק הקובץ שהתיקון הזה אומת מולו, *לפני* שנוגעים
     * בו - לפי בקשה מפורשת: לבדוק זהות אחד-לאחד לקובץ מוכר ורק אז להחליף.
     * אם expectedSha256 לא מוגדר בתיקון, אין בדיקה (לא כל תיקון יש לו עוד hash
     * מאומת - למשל M393 שהנתיב שלו עדיין לא אומת כלל).
     */
    private fun checkExpectedHash(patch: Patch, file: File, l: (String) -> Unit): Boolean {
        val expected = patch.expectedSha256 ?: return true
        val actual = sha256(file)
        if (actual.equals(expected, ignoreCase = true)) {
            l("בדיקת SHA-256 עברה - זה בדיוק הקובץ שהתיקון אומת מולו")
            return true
        }
        l("עצירה: ה-SHA-256 של הקובץ לא תואם למה שהתיקון הזה אומת מולו - לא ממשיך")
        l("  צפוי:  $expected")
        l("  בפועל: $actual")
        l("  (קובץ שונה/גרסה אחרת/קובץ שכבר תוקן בעבר - לא מניחים שההחלפה בטוחה)")
        return false
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buf = ByteArray(8192)
            while (true) {
                val n = input.read(buf)
                if (n < 0) break
                digest.update(buf, 0, n)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /** סורק את כל ה-classes*.dex בתוך ה-zip ומחזיר את הראשון שמכיל אחת מהמחלקות הנתונות. */
    private fun findDexContaining(zipFile: File, classNames: List<String>): Pair<String, File>? {
        ZipFile(zipFile).use { zf ->
            val dexEntryNames = zf.entries().asSequence()
                .map { it.name }
                .filter { it.matches(Regex("""classes\d*\.dex""")) }
                .sorted()
                .toList()

            for (name in dexEntryNames) {
                val tmp = File(workDir, "probe_${name.replace('/', '_')}")
                zf.getInputStream(zf.getEntry(name)).use { input ->
                    tmp.outputStream().use { output -> input.copyTo(output) }
                }
                val dex = DexFileFactory.loadDexFile(tmp, Opcodes.getDefault())
                val hasAny = dex.classes.any { it.type in classNames }
                if (hasAny) return name to tmp
            }
        }
        return null
    }

    private fun replaceEntryInZip(src: File, dst: File, entryName: String, newEntryContent: File) {
        ZipFile(src).use { zf ->
            ZipOutputStream(dst.outputStream()).use { zos ->
                val entries = zf.entries()
                while (entries.hasMoreElements()) {
                    val e: ZipEntry = entries.nextElement()
                    if (e.name == entryName) {
                        zos.putNextEntry(ZipEntry(entryName))
                        newEntryContent.inputStream().use { it.copyTo(zos) }
                    } else {
                        zos.putNextEntry(ZipEntry(e.name))
                        zf.getInputStream(e).use { it.copyTo(zos) }
                    }
                    zos.closeEntry()
                }
            }
        }
    }

    /**
     * מחליף טקסטואלית מתודה שלמה (מ-".method ... methodName" ועד ה-".end method"
     * התואם) - בדיוק השיטה שהשתמשנו בה ידנית ב-MT Manager, רק אוטומטית.
     */
    private fun replaceMethod(smaliFile: File, methodName: String, newMethodBody: String): Boolean {
        val text = smaliFile.readText()
        val startRegex = Regex("""\.method[^\n]*\s$methodName\(""")
        val match = startRegex.find(text) ?: return false
        val methodStart = text.lastIndexOf(".method", match.range.first)
        val endIdx = text.indexOf(".end method", match.range.last)
        if (methodStart == -1 || endIdx == -1) return false
        val afterEnd = endIdx + ".end method".length
        val newText = text.substring(0, methodStart) + newMethodBody.trim() + text.substring(afterEnd)
        smaliFile.writeText(newText)
        return true
    }
}
