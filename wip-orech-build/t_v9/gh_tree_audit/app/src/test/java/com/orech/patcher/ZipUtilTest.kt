package com.orech.patcher

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ZipUtilTest {

    @get:Rule
    val tmp = TemporaryFolder()

    // תוכן ייחודי וארוך מספיק כדי שאפשר יהיה למצוא אותו בבייטים הגולמיים של ה-zip
    private val arscBytes = ByteArray(300) { (0x41 + it % 23).toByte() } + "ARSC-MARKER".toByteArray()
    private val dexBytes = ByteArray(2000) { (it % 7).toByte() }

    private fun buildZip(): File {
        val f = tmp.newFile("in.zip")
        ZipOutputStream(f.outputStream()).use { zos ->
            // שם באורך אי-זוגי כדי שהיישור לא יהיה "במקרה" נכון
            val stored = ZipEntry("resources.arsc")
            stored.method = ZipEntry.STORED
            stored.size = arscBytes.size.toLong()
            stored.compressedSize = arscBytes.size.toLong()
            stored.crc = CRC32().also { it.update(arscBytes) }.value
            zos.putNextEntry(stored)
            zos.write(arscBytes)
            zos.closeEntry()

            val deflated = ZipEntry("classes.dex")
            deflated.method = ZipEntry.DEFLATED
            zos.putNextEntry(deflated)
            zos.write(dexBytes)
            zos.closeEntry()

            val stored2 = ZipEntry("a.bin")
            val small = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
            stored2.method = ZipEntry.STORED
            stored2.size = small.size.toLong()
            stored2.compressedSize = small.size.toLong()
            stored2.crc = CRC32().also { it.update(small) }.value
            zos.putNextEntry(stored2)
            zos.write(small)
            zos.closeEntry()
        }
        return f
    }

    private fun indexOf(haystack: ByteArray, needle: ByteArray): Int {
        outer@ for (i in 0..haystack.size - needle.size) {
            for (j in needle.indices) if (haystack[i + j] != needle[j]) continue@outer
            return i
        }
        return -1
    }

    @Test
    fun rewrite_keepsCompressionMethodAndContent() {
        val src = buildZip()
        val dst = tmp.newFile("out.zip")
        ZipUtil.rewrite(src, dst)
        ZipFile(dst).use { zf ->
            assertEquals(ZipEntry.STORED, zf.getEntry("resources.arsc").method)
            assertEquals(ZipEntry.DEFLATED, zf.getEntry("classes.dex").method)
            assertArrayEquals(arscBytes, zf.getInputStream(zf.getEntry("resources.arsc")).readBytes())
            assertArrayEquals(dexBytes, zf.getInputStream(zf.getEntry("classes.dex")).readBytes())
        }
    }

    @Test
    fun rewrite_alignsStoredEntriesTo4Bytes() {
        val dst = tmp.newFile("out.zip")
        ZipUtil.rewrite(buildZip(), dst)
        val raw = dst.readBytes()
        val arscOffset = indexOf(raw, arscBytes)
        val smallOffset = indexOf(raw, byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
        assertTrue("resources.arsc לא נמצא בפלט", arscOffset >= 0)
        assertTrue("a.bin לא נמצא בפלט", smallOffset >= 0)
        assertEquals("resources.arsc לא מיושר ל-4", 0, arscOffset % 4)
        assertEquals("a.bin לא מיושר ל-4", 0, smallOffset % 4)
    }

    @Test
    fun rewrite_replacementWithDifferentSizeHasValidCrc() {
        val replacement = tmp.newFile("new.arsc")
        val newBytes = ByteArray(517) { (it * 3).toByte() }
        replacement.writeBytes(newBytes)
        val dst = tmp.newFile("out.zip")
        ZipUtil.rewrite(buildZip(), dst, mapOf("resources.arsc" to replacement))
        ZipFile(dst).use { zf ->
            val e = zf.getEntry("resources.arsc")
            assertEquals(ZipEntry.STORED, e.method)
            assertEquals(newBytes.size.toLong(), e.size)
            // קריאה מלאה בודקת CRC; חריגה כאן = CRC שגוי
            assertArrayEquals(newBytes, zf.getInputStream(e).readBytes())
            // שאר הרשומות לא נפגעו
            assertArrayEquals(dexBytes, zf.getInputStream(zf.getEntry("classes.dex")).readBytes())
        }
        assertEquals(0, indexOf(dst.readBytes(), newBytes) % 4)
    }

    @Test
    fun alignInPlace_keepsEntriesAndLeavesNoTempFile() {
        val f = buildZip()
        ZipUtil.alignInPlace(f)
        ZipFile(f).use { zf ->
            assertEquals(3, zf.size())
            assertArrayEquals(arscBytes, zf.getInputStream(zf.getEntry("resources.arsc")).readBytes())
        }
        assertEquals(0, indexOf(f.readBytes(), arscBytes) % 4)
        assertTrue(!File(f.parentFile, f.name + ".aligned").exists())
    }
}
