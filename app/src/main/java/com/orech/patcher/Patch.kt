package com.orech.patcher

import org.json.JSONArray
import org.json.JSONObject

/** עריכה בודדת בתוך dex אחד: או החלפת מתודה קיימת, או כתיבת קובץ smali שלם (גם למחלקה חדשה). */
data class Edit(
    val kind: String, // "method" | "class"
    val className: String, // לדוגמה Lcom/android/server/wm/RecentTasks;
    val methodName: String? = null, // ל-kind=method
    val newMethodSmali: String? = null, // ל-kind=method
    val newClassSmali: String? = null // ל-kind=class
) {
    companion object {
        fun fromJson(o: JSONObject) = Edit(
            kind = o.getString("kind"),
            className = o.getString("className"),
            methodName = o.optString("methodName").ifEmpty { null },
            newMethodSmali = o.optString("newMethodSmali").ifEmpty { null },
            newClassSmali = o.optString("newClassSmali").ifEmpty { null }
        )
    }
}

/**
 * תיאור כללי של תיקון אחד - לא קוד קשיח. להוסיף תיקון עתידי = להוסיף אובייקט
 * כזה ל-assets/patches/patches.json, בלי לגעת בקוד של האפליקציה.
 *
 * kind:
 *  - "edit"    - פירוק/עריכה/הרכבה/חתימה/החזרה של קובץ apk/jar (צריך edits + signingKey)
 *  - "delete"  - מחיקת קובץ מהמכשיר (צריך targetFile+remount, גיבוי אוטומטי לפני מחיקה)
 *  - "setting" - שינוי ערך ב-Settings (settings put) - לא נוגע בשום קובץ
 *  - "info"    - לא מבצע שום פעולה במכשיר, רק מציג הוראות/הערות למשתמש
 */
data class Patch(
    val id: String,
    val title: String,
    val kind: String,
    val device: String, // "M36 Plus" / "M393" / "כל המכשירים"
    val targetFile: String? = null,
    val remount: String? = null,
    val signingKey: String? = null,
    /** SHA-256 (lowercase hex) של הקובץ המקורי שהתיקון הזה אומת מולו. אם מוגדר,
     * המנוע בודק את הקובץ שהוא שלף/מצא *לפני* שהוא נוגע בו, ומסרב להמשיך אם
     * זה לא תואם בדיוק - כדי לא להחיל תיקון על קושחה/קובץ שלא אומת מולו. */
    val expectedSha256: String? = null,
    val edits: List<Edit> = emptyList(),
    val settingNamespace: String? = null,
    val settingKey: String? = null,
    val settingValue: String? = null,
    val infoText: String? = null,
    val note: String? = null // אזהרה/הערת ביטחון שתוצג למשתמש
) {
    companion object {
        fun loadAll(json: String): List<Patch> {
            val arr = JSONArray(json)
            val out = mutableListOf<Patch>()
            for (i in 0 until arr.length()) {
                val o: JSONObject = arr.getJSONObject(i)
                val editsArr = o.optJSONArray("edits")
                val edits = mutableListOf<Edit>()
                if (editsArr != null) {
                    for (j in 0 until editsArr.length()) edits.add(Edit.fromJson(editsArr.getJSONObject(j)))
                }
                out.add(
                    Patch(
                        id = o.getString("id"),
                        title = o.getString("title"),
                        kind = o.getString("kind"),
                        device = o.optString("device", "M36 Plus"),
                        targetFile = o.optString("targetFile").ifEmpty { null },
                        remount = o.optString("remount").ifEmpty { null },
                        signingKey = o.optString("signingKey").ifEmpty { null },
                        expectedSha256 = o.optString("expectedSha256").ifEmpty { null },
                        edits = edits,
                        settingNamespace = o.optString("settingNamespace").ifEmpty { null },
                        settingKey = o.optString("settingKey").ifEmpty { null },
                        settingValue = o.optString("settingValue").ifEmpty { null },
                        infoText = o.optString("infoText").ifEmpty { null },
                        note = o.optString("note").ifEmpty { null }
                    )
                )
            }
            return out
        }
    }
}
