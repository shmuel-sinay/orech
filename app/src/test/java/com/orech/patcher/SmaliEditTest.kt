package com.orech.patcher

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmaliEditTest {

    private val sample = """
        .class public Lcom/example/Foo;
        .super Ljava/lang/Object;

        .method public checkRecentTask()V
            .registers 2
            const/4 v0, 0x1
            return-void
        .end method

        .method public other()V
            .registers 1
            return-void
        .end method
    """.trimIndent()

    private val newBody = ".method private checkRecentTask()V\n    .registers 1\n\n    return-void\n.end method"

    @Test
    fun replaceMethod_swapsTheMatchingMethodBody() {
        val result = SmaliEdit.replaceMethod(sample, "checkRecentTask", newBody)!!

        assertTrue("new body should be present", result.contains(newBody))
        assertFalse("old body should be gone", result.contains("const/4 v0, 0x1"))
    }

    @Test
    fun replaceMethod_leavesOtherMethodsUntouched() {
        val result = SmaliEdit.replaceMethod(sample, "checkRecentTask", newBody)!!

        assertTrue(
            result.contains(
                ".method public other()V\n" +
                    "    .registers 1\n" +
                    "    return-void\n" +
                    ".end method"
            )
        )
    }

    @Test
    fun replaceMethod_preservesWhatComesBeforeTheMethod() {
        val result = SmaliEdit.replaceMethod(sample, "checkRecentTask", newBody)!!

        assertTrue(result.startsWith(".class public Lcom/example/Foo;\n.super Ljava/lang/Object;\n"))
    }

    @Test
    fun replaceMethod_missingMethod_returnsNull() {
        assertNull(SmaliEdit.replaceMethod(sample, "noSuchMethod", newBody))
    }

    @Test
    fun replaceMethod_doesNotMatchPrefixOfAnotherMethodName() {
        // "check" is a prefix of "checkRecentTask" - make sure the open-paren boundary is required.
        assertNull(SmaliEdit.replaceMethod(sample, "check", newBody))
    }

    @Test
    fun replaceMethod_trimsLeadingAndTrailingWhitespaceFromNewBody() {
        val result = SmaliEdit.replaceMethod(sample, "checkRecentTask", "  \n$newBody\n\n  ")!!
        // the trimmed body is followed directly by the untouched rest of the file, no stray blank lines from the input.
        assertTrue(result.contains(newBody + "\n\n.method public other()V"))
    }
}
