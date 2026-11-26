package com.orech.patcher

import java.io.File
import java.security.MessageDigest

/** חישוב sha256 של קובץ - בלי תלות באנדרואיד, כדי שאפשר לבדוק ב-JVM רגיל. */
object FileHash {

    fun sha256(file: File): String {
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

    /** true אם expected הוא null (אין בדיקה מוגדרת) או תואם ל-sha256 בפועל של הקובץ (לא רגיש לאותיות גדולות/קטנות). */
    fun matches(file: File, expected: String?): Boolean {
        if (expected == null) return true
        return sha256(file).equals(expected, ignoreCase = true)
    }
}
