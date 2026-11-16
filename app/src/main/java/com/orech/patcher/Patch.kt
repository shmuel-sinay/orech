package com.orech.patcher

import org.json.JSONArray
import org.json.JSONObject

/**
 * תיאור כללי של עריכה אחת - לא קוד קשיח. להוסיף תיקון עתידי = להוסיף אובייקט
 * כזה ל-assets/patches/patches.json, בלי לגעת בקוד של האפליקציה בכלל.
 */
data class Patch(
    val id: String,
    val title: String,
    /** נתיב על המכשיר לקובץ שצריך לערוך (apk/jar) */
    val targetFile: String,
    /** איזה partition לעשות לו remount,rw לפני כתיבה בחזרה (/system, /vendor וכו') */
    val remount: String,
    /** איזה classes*.dex בתוך הקובץ (בד"כ "classes.dex" או "classes2.dex") */
    val dexEntry: String,
    /** שם המחלקה המלא בפורמט smali, לדוגמה Lcom/android/server/wm/RecentTasks; */
    val className: String,
    /** שם המתודה שצריך להחליף */
    val methodName: String,
    /** תוכן ה-smali החדש של המתודה כולה (כולל .method/.end method) */
    val newMethodSmali: String,
    /** שם מפתח החתימה לשימוש (קובץ ב-assets/keys/, בלי סיומת) */
    val signingKey: String
) {
    companion object {
        fun loadAll(json: String): List<Patch> {
            val arr = JSONArray(json)
            val out = mutableListOf<Patch>()
            for (i in 0 until arr.length()) {
                val o: JSONObject = arr.getJSONObject(i)
                out.add(
                    Patch(
                        id = o.getString("id"),
                        title = o.getString("title"),
                        targetFile = o.getString("targetFile"),
                        remount = o.getString("remount"),
                        dexEntry = o.getString("dexEntry"),
                        className = o.getString("className"),
                        methodName = o.getString("methodName"),
                        newMethodSmali = o.getString("newMethodSmali"),
                        signingKey = o.getString("signingKey")
                    )
                )
            }
            return out
        }
    }
}
