package com.orech.patcher

import java.io.DataOutputStream
import java.io.File

/** עטיפה מינימלית סביב פקודת su - בלי ספריית root חיצונית. */
object RootShell {

    /** מריץ כל פקודה בנפרד ומחזיר לכל אחת (קוד יציאה, פלט) - כדי שיהיה אפשר לאתר בדיוק איפה זה נשבר. */
    fun exec(vararg commands: String): List<Triple<String, Int, String>> {
        val process = ProcessBuilder("su").redirectErrorStream(true).start()
        val out = DataOutputStream(process.outputStream)
        val marker = "__ORECH_RC__"
        for (cmd in commands) {
            out.writeBytes(cmd + "\n")
            out.writeBytes("echo $marker:$?\n")
        }
        out.writeBytes("exit\n")
        out.flush()
        val rawOutput = process.inputStream.bufferedReader().readText()
        process.waitFor()

        val results = mutableListOf<Triple<String, Int, String>>()
        var remaining = rawOutput
        for (cmd in commands) {
            val idx = remaining.indexOf("$marker:")
            if (idx == -1) {
                results.add(Triple(cmd, -1, remaining.trim()))
                remaining = ""
                continue
            }
            val chunk = remaining.substring(0, idx)
            val afterMarker = remaining.substring(idx + marker.length + 1)
            val lineEnd = afterMarker.indexOf('\n').let { if (it == -1) afterMarker.length else it }
            val rc = afterMarker.substring(0, lineEnd).trim().toIntOrNull() ?: -1
            remaining = if (lineEnd < afterMarker.length) afterMarker.substring(lineEnd + 1) else ""
            results.add(Triple(cmd, rc, chunk.trim()))
        }
        return results
    }

    /** מחזיר (הצלחה, לוג מפורט) - ההצלחה נקבעת לפי קיום הקובץ וגודלו, לא לפי קוד היציאה של su. */
    fun pull(remotePath: String, localPath: String): Pair<Boolean, String> {
        val results = exec("cat '$remotePath' > '$localPath' 2>&1", "chmod 666 '$localPath'")
        val ok = File(localPath).let { it.exists() && it.length() > 0 }
        val log = results.joinToString("\n") { "\$ ${it.first}\n  rc=${it.second} ${it.third}" }
        return ok to log
    }

    fun push(localPath: String, remotePath: String, remount: String): Pair<Boolean, String> {
        val results = exec(
            "mount -o remount,rw $remount",
            "cp '$localPath' '$remotePath'",
            "chmod 644 '$remotePath'"
        )
        val ok = results.all { it.second == 0 }
        val log = results.joinToString("\n") { "\$ ${it.first}\n  rc=${it.second} ${it.third}" }
        return ok to log
    }
}
