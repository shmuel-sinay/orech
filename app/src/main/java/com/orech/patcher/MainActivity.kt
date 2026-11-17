package com.orech.patcher

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var patches: List<Patch>
    private val selected = mutableSetOf<String>()
    private var lastLogger: Logger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val json = assets.open("patches/patches.json").bufferedReader().readText()
        patches = Patch.loadAll(json)

        val list = findViewById<ListView>(R.id.patchList)
        val logView = findViewById<TextView>(R.id.logView)
        val applyButton = findViewById<Button>(R.id.applyButton)
        val dryRunCheck = findViewById<CheckBox>(R.id.dryRunCheck)
        val shareLogButton = findViewById<Button>(R.id.shareLogButton)

        list.adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_multiple_choice,
            patches.map { "[${it.device}] ${it.title}" }
        )
        list.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        applyButton.setOnClickListener {
            selected.clear()
            for (i in patches.indices) {
                if (list.isItemChecked(i)) selected.add(patches[i].id)
            }
            val dryRun = dryRunCheck.isChecked
            val logger = Logger(this)
            lastLogger = logger
            logView.text = "מתחיל... (${if (dryRun) "מצב בדיקה בלבד" else "מצב אמיתי - כותב למכשיר!"})\n"
            logger.log("=== הפעלה: dryRun=$dryRun, patches=$selected ===")

            thread {
                val workDir = File(getExternalFilesDir(null), "patchwork").also { it.mkdirs() }
                val engine = PatchEngine(this, workDir, logger, dryRun)
                for (patch in patches.filter { it.id in selected }) {
                    engine.apply(patch) { line ->
                        runOnUiThread { logView.append(line + "\n") }
                    }
                }
                runOnUiThread {
                    logView.append("סיום. הלוג המלא נשמר ב:\n${logger.file.absolutePath}\n")
                }
            }
        }

        shareLogButton.setOnClickListener {
            val logger = lastLogger
            if (logger == null || !logger.file.exists()) {
                logView.append("אין עדיין לוג לשלוח - תריץ תיקון קודם.\n")
            } else {
                startActivity(Intent.createChooser(logger.shareIntent(), "שליחת לוג"))
            }
        }

        val restoreButton = findViewById<Button>(R.id.restoreButton)
        restoreButton.setOnClickListener { showRestoreDialog(logView) }
    }

    private fun showRestoreDialog(logView: TextView) {
        val backupsDir = getExternalFilesDir("backups")
        val files = backupsDir?.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
        if (files.isEmpty()) {
            logView.append("אין עדיין קבצי גיבוי.\n")
            return
        }

        val stamp = SimpleDateFormat("dd/MM HH:mm:ss", Locale.US)
        val labels = files.map { f ->
            val patchId = f.name.substringBeforeLast("_")
            val patch = patches.find { it.id == patchId }
            val title = patch?.title ?: patchId
            "$title - ${stamp.format(Date(f.lastModified()))}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("בחר גיבוי לשחזור")
            .setItems(labels) { _, index ->
                val f = files[index]
                val patchId = f.name.substringBeforeLast("_")
                val patch = patches.find { it.id == patchId }
                val targetFile: String? = patch?.targetFile
                val remount: String? = patch?.remount
                if (targetFile == null || remount == null) {
                    logView.append("לא נמצא נתיב מקור לגיבוי הזה - שחזור ידני דרוש.\n")
                    return@setItems
                }
                AlertDialog.Builder(this)
                    .setTitle("לשחזר?")
                    .setMessage("זה יכתוב את ${f.name} בחזרה אל $targetFile. להמשיך?")
                    .setPositiveButton("שחזר") { _, _ ->
                        val logger = lastLogger ?: Logger(this).also { lastLogger = it }
                        logView.append("משחזר...\n")
                        thread {
                            val workDir = File(getExternalFilesDir(null), "patchwork").also { it.mkdirs() }
                            val engine = PatchEngine(this, workDir, logger, dryRun = false)
                            engine.restore(f, targetFile, remount) { line ->
                                runOnUiThread { logView.append(line + "\n") }
                            }
                        }
                    }
                    .setNegativeButton("ביטול", null)
                    .show()
            }
            .show()
    }
}
