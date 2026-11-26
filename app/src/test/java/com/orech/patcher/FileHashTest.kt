package com.orech.patcher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.security.MessageDigest

class FileHashTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun sha256_matchesMessageDigestOnTheSameBytes() {
        val bytes = "hello orech".toByteArray()
        val file = tmp.newFile("a.bin").apply { writeBytes(bytes) }

        val expected = MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it) }

        assertEquals(expected, FileHash.sha256(file))
    }

    @Test
    fun sha256_differentContent_differentHash() {
        val f1 = tmp.newFile("a.bin").apply { writeBytes("one".toByteArray()) }
        val f2 = tmp.newFile("b.bin").apply { writeBytes("two".toByteArray()) }

        assertFalse(FileHash.sha256(f1) == FileHash.sha256(f2))
    }

    @Test
    fun sha256_handlesContentLargerThanTheReadBuffer() {
        // the implementation reads in 8192-byte chunks - make sure a file spanning several chunks still hashes right.
        val bytes = ByteArray(20000) { (it % 251).toByte() }
        val file = tmp.newFile("big.bin").apply { writeBytes(bytes) }

        val expected = MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it) }

        assertEquals(expected, FileHash.sha256(file))
    }

    @Test
    fun matches_nullExpected_isAlwaysTrue() {
        val file = tmp.newFile("a.bin").apply { writeBytes("anything".toByteArray()) }
        assertTrue(FileHash.matches(file, null))
    }

    @Test
    fun matches_sameHash_caseInsensitive() {
        val file = tmp.newFile("a.bin").apply { writeBytes("hello orech".toByteArray()) }
        val hash = FileHash.sha256(file)

        assertTrue(FileHash.matches(file, hash.uppercase()))
        assertTrue(FileHash.matches(file, hash.lowercase()))
    }

    @Test
    fun matches_wrongHash_isFalse() {
        val file = tmp.newFile("a.bin").apply { writeBytes("hello orech".toByteArray()) }
        assertFalse(FileHash.matches(file, "0".repeat(64)))
    }
}
