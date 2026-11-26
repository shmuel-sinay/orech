package com.orech.patcher

/**
 * עריכת טקסט smali בלי שום תלות באנדרואיד (לא Context, לא RootShell) - הוצא
 * החוצה מ-PatchEngine כדי שהלוגיקה הזאת עצמה תהיה ניתנת לבדיקה ב-JVM רגיל.
 */
object SmaliEdit {

    /**
     * מחליף מתודה שלמה (מ-".method ... methodName(" ועד ה-".end method" התואם) בטקסט חדש.
     * מחזיר null אם המתודה לא נמצאה בטקסט - בלי לשנות כלום.
     */
    fun replaceMethod(text: String, methodName: String, newMethodBody: String): String? {
        val startRegex = Regex("""\.method[^\n]*\s$methodName\(""")
        val match = startRegex.find(text) ?: return null
        val methodStart = text.lastIndexOf(".method", match.range.first)
        val endIdx = text.indexOf(".end method", match.range.last)
        if (methodStart == -1 || endIdx == -1) return null
        val afterEnd = endIdx + ".end method".length
        return text.substring(0, methodStart) + newMethodBody.trim() + text.substring(afterEnd)
    }
}
