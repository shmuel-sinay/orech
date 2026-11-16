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
 * מנוע כללי: מקבל Patch, מבצע פירוק->עריכת טקסט->הרכבה->חתימה->החזרה.
 *
 * הערה חשובה: זה נכתב לפי הידע שלי על ה-API הציבורי של smali/baksmali/apksig,
 * אבל לא קומפל ולא נבדק בפועל (אין לי יכולת להריץ כלי בנייה על קוד שעורך
 * אפליקציות מערכת). ייתכן שצריך להתאים שמות מתודות מדויקים כשתקמפלו את זה
 * ב-Android Studio ותראו שגיאות קומפילציה - אלה כנראה רק אי-התאמות API קטנות,
 * לא בעיה בעיקרון הכללי.
 */
class PatchEngine(private val context: Context, private val workDir: File) {

    fun apply(patch: Patch, log: (String) -> Unit): Boolean {
        log("[${patch.id}] שולף את ${patch.targetFile}")
        val localOriginal = File(workDir, "${patch.id}_original")
        if (!RootShell.pull(patch.targetFile, localOriginal.absolutePath)) {
            log("[${patch.id}] נכשל ב-pull"); return false
        }

        val dexFile = File(workDir, "${patch.id}_${patch.dexEntry}")
        if (!extractEntry(localOriginal, patch.dexEntry, dexFile)) {
            log("[${patch.id}] לא נמצא ${patch.dexEntry} בתוך הקובץ"); return false
        }

        val smaliOutDir = File(workDir, "${patch.id}_smali").also { it.deleteRecursively(); it.mkdirs() }
        log("[${patch.id}] מפרק ל-smali...")
        val dex = DexFileFactory.loadDexFile(dexFile, Opcodes.getDefault())
        val baksmaliOptions = BaksmaliOptions()
        Baksmali.disassembleDexFile(dex, smaliOutDir, 1, baksmaliOptions)

        val classRelPath = patch.className.removePrefix("L").removeSuffix(";") + ".smali"
        val smaliFile = File(smaliOutDir, classRelPath)
        if (!smaliFile.exists()) {
            log("[${patch.id}] לא נמצא קובץ smali למחלקה ${patch.className}"); return false
        }

        log("[${patch.id}] מחליף את ${patch.methodName}...")
        if (!replaceMethod(smaliFile, patch.methodName, patch.newMethodSmali)) {
            log("[${patch.id}] לא נמצאה המתודה ${patch.methodName} בקובץ - לא בוצע שינוי"); return false
        }

        log("[${patch.id}] מרכיב מחדש ל-dex...")
        val newDexFile = File(workDir, "${patch.id}_new.dex")
        val smaliOptions = SmaliOptions()
        smaliOptions.outputDexFile = newDexFile.absolutePath
        val ok = Smali.assemble(smaliOptions, listOf(smaliOutDir.absolutePath))
        if (!ok) {
            log("[${patch.id}] הרכבה מחדש נכשלה"); return false
        }

        val patchedZip = File(workDir, "${patch.id}_patched.apk")
        replaceEntryInZip(localOriginal, patchedZip, patch.dexEntry, newDexFile)

        val signedZip = File(workDir, "${patch.id}_signed.apk")
        log("[${patch.id}] חותם עם ${patch.signingKey}...")
        if (!Signer.sign(context, patchedZip, signedZip, patch.signingKey)) {
            log("[${patch.id}] חתימה נכשלה"); return false
        }

        log("[${patch.id}] מעביר בחזרה למכשיר...")
        val ok2 = RootShell.push(signedZip.absolutePath, patch.targetFile, patch.remount)
        log("[${patch.id}] ${if (ok2) "הושלם" else "נכשל"}")
        return ok2
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
