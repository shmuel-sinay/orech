package com.orech.patcher

import android.content.Context
import com.android.tools.smali.baksmali.Baksmali
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.smali.Smali
import com.android.tools.smali.smali.SmaliOptions
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * מנוע כללי: מקבל Patch, מבצע פירוק->עריכת טקסט->הרכבה->חתימה->(אופציונלי) החזרה.
 * כל שגיאה - כולל חריגה לא צפויה - נתפסת ונרשמת ללוג המלא, לא נופלת בשקט.
 */
class PatchEngine(
    private val context: Context,
    private val workDir: File,
    private val logger: Logger,
    private val dryRun: Boolean
) {

    fun apply(patch: Patch, log: (String) -> Unit): Boolean {
        fun l(msg: String) {
            log(msg)
            logger.log("[${patch.id}] $msg")
        }

        return try {
            l("שולף את ${patch.targetFile}")
            val localOriginal = File(workDir, "${patch.id}_original")
            val (pulled, pullLog) = RootShell.pull(patch.targetFile, localOriginal.absolutePath)
            logger.log(pullLog)
            if (!pulled) {
                l("נכשל ב-pull - ראה את הלוג המלא לפרטים")
                return false
            }

            val backupFile = File(context.getExternalFilesDir("backups"), "${patch.id}_${System.currentTimeMillis()}.orig")
            backupFile.parentFile?.mkdirs()
            localOriginal.copyTo(backupFile, overwrite = true)
            l("גיבוי הקובץ המקורי נשמר ב: ${backupFile.absolutePath}")

            val dexFile = File(workDir, "${patch.id}_${patch.dexEntry}")
            if (!extractEntry(localOriginal, patch.dexEntry, dexFile)) {
                l("לא נמצא ${patch.dexEntry} בתוך הקובץ - אולי המחלקה נמצאת בקובץ dex אחר")
                return false
            }

            val smaliOutDir = File(workDir, "${patch.id}_smali").also { it.deleteRecursively(); it.mkdirs() }
            l("מפרק ל-smali...")
            val dex = DexFileFactory.loadDexFile(dexFile, Opcodes.getDefault())
            Baksmali.disassembleDexFile(dex, smaliOutDir, 1, BaksmaliOptions())

            val classRelPath = patch.className.removePrefix("L").removeSuffix(";") + ".smali"
            val smaliFile = File(smaliOutDir, classRelPath)
            if (!smaliFile.exists()) {
                l("לא נמצא קובץ smali למחלקה ${patch.className}")
                return false
            }

            l("מחליף את ${patch.methodName}...")
            if (!replaceMethod(smaliFile, patch.methodName, patch.newMethodSmali)) {
                l("לא נמצאה המתודה ${patch.methodName} בקובץ - לא בוצע שינוי")
                return false
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

            val patchedZip = File(workDir, "${patch.id}_patched.apk")
            replaceEntryInZip(localOriginal, patchedZip, patch.dexEntry, newDexFile)

            val signedZip = File(workDir, "${patch.id}_signed.apk")
            l("חותם עם ${patch.signingKey}...")
            if (!Signer.sign(context, patchedZip, signedZip, patch.signingKey)) {
                l("חתימה נכשלה")
                return false
            }
            l("קובץ חתום מוכן: ${signedZip.absolutePath} (${signedZip.length()} בייט)")

            if (dryRun) {
                l("מצב בדיקה בלבד - לא הוחזר למכשיר")
                return true
            }

            l("מעביר בחזרה למכשיר...")
            val (pushed, pushLog) = RootShell.push(signedZip.absolutePath, patch.targetFile, patch.remount)
            logger.log(pushLog)
            l(if (pushed) "הושלם בהצלחה" else "נכשל בהעברה חזרה - ראה את הלוג המלא")
            pushed
        } catch (e: Throwable) {
            l("שגיאה לא צפויה: ${e.message}")
            logger.log("[${patch.id}] EXCEPTION:\n${e.stackTraceToString()}")
            false
        }
    }

    private fun extractEntry(zipFile: File, entryName: String, outFile: File): Boolean {
        ZipFile(zipFile).use { zf ->
            val entry = zf.getEntry(entryName) ?: return false
            zf.getInputStream(entry).use { input ->
                outFile.outputStream().use { output -> input.copyTo(output) }
            }
        }
        return true
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
