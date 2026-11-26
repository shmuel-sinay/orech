package com.orech.patcher

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** לוג מלא שנשמר לקובץ (לא רק למסך), כדי שאפשר לשלוח אותו החוצה - למשל להתייעצות עם AI. */
class Logger(context: Context) {
    private val context = context.applicationContext
    val file: File

    init {
        val dir = File(context.getExternalFilesDir(null), "logs").also { it.mkdirs() }
        val stamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
        file = File(dir, "orech_$stamp.log")
    }

    @Synchronized
    fun log(line: String) {
        file.appendText(line + "\n")
    }

    fun shareIntent(): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
