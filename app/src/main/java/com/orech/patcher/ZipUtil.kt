package com.orech.patcher

import java.io.BufferedOutputStream
import java.io.File
import java.io.FilterOutputStream
import java.io.OutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * כתיבה מחדש של zip בלי לאבד את שיטת הדחיסה של כל רשומה, ועם יישור ל-4 בתים של רשומות
 * לא דחוסות (resources.arsc חייב להיות לא דחוס ומיושר באפליקציות שמכוונות ל-R ומעלה).
 * נבדק מול zipalign -c על שלושה APK שלא היו מיושרים.
 */
object ZipUtil {

    private class CountingOutputStream(out: OutputStream) : FilterOutputStream(out) {
        var count = 0L
        override fun write(b: Int) { out.write(b); count++ }
        override fun write(b: ByteArray, off: Int, len: Int) { out.write(b, off, len); count += len }
    }

    /** מחליף רשומות לפי שם (replacements) ומיישר את כל השאר. הפלט נכתב ל-dst. */
    fun rewrite(src: File, dst: File, replacements: Map<String, File> = emptyMap(), alignment: Int = 4) {
        ZipFile(src).use { zf ->
            val counting = CountingOutputStream(BufferedOutputStream(dst.outputStream()))
            ZipOutputStream(counting).use { zos ->
                val entries = zf.entries()
                while (entries.hasMoreElements()) {
                    val e = entries.nextElement()
                    val replacement = replacements[e.name]
                    val ne = ZipEntry(e.name)
                    ne.time = e.time
                    if (e.method == ZipEntry.STORED) {
                        ne.method = ZipEntry.STORED
                        if (replacement != null) {
                            val crc = CRC32()
                            replacement.inputStream().use { input ->
                                val buf = ByteArray(64 * 1024)
                                while (true) {
                                    val n = input.read(buf)
                                    if (n <= 0) break
                                    crc.update(buf, 0, n)
                                }
                            }
                            ne.size = replacement.length()
                            ne.compressedSize = replacement.length()
                            ne.crc = crc.value
                        } else {
                            ne.size = e.size
                            ne.compressedSize = e.size
                            ne.crc = e.crc
                        }
                        val nameLen = e.name.toByteArray(Charsets.UTF_8).size
                        val dataOffset = counting.count + 30 + nameLen
                        var pad = ((alignment - (dataOffset % alignment)) % alignment).toInt()
                        if (pad > 0) {
                            if (pad < 4) pad += 4
                            val extra = ByteArray(pad)
                            extra[0] = 0xD9.toByte()
                            extra[1] = 0x35
                            extra[2] = ((pad - 4) and 0xFF).toByte()
                            extra[3] = (((pad - 4) shr 8) and 0xFF).toByte()
                            ne.extra = extra
                        }
                    }
                    zos.putNextEntry(ne)
                    if (replacement != null) {
                        replacement.inputStream().use { it.copyTo(zos) }
                    } else {
                        zf.getInputStream(e).use { it.copyTo(zos) }
                    }
                    zos.closeEntry()
                }
            }
        }
    }

    /** יישור במקום: כותב לקובץ זמני ומחליף. */
    fun alignInPlace(file: File) {
        val tmp = File(file.parentFile, file.name + ".aligned")
        rewrite(file, tmp)
        tmp.copyTo(file, overwrite = true)
        tmp.delete()
    }
}
