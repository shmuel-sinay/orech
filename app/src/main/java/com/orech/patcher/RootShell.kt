package com.orech.patcher

import java.io.DataOutputStream

/** עטיפה מינימלית סביב פקודת su - בלי ספריית root חיצונית. */
object RootShell {

    fun exec(vararg commands: String): Pair<Int, String> {
        val process = ProcessBuilder("su").redirectErrorStream(true).start()
        val out = DataOutputStream(process.outputStream)
        for (cmd in commands) {
            out.writeBytes(cmd + "\n")
        }
        out.writeBytes("exit\n")
        out.flush()
        val output = process.inputStream.bufferedReader().readText()
        val code = process.waitFor()
        return code to output
    }

    fun pull(remotePath: String, localPath: String): Boolean {
        val (code, _) = exec("cat '$remotePath' > '$localPath'", "chmod 666 '$localPath'")
        return code == 0
    }

    fun push(localPath: String, remotePath: String, remount: String): Boolean {
        val (code, _) = exec(
            "mount -o remount,rw $remount",
            "cp '$localPath' '$remotePath'",
            "chmod 644 '$remotePath'"
        )
        return code == 0
    }
}
