package com.orech.patcher

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var patches: List<Patch>
    private val selected = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val json = assets.open("patches/patches.json").bufferedReader().readText()
        patches = Patch.loadAll(json)

        val list = findViewById<ListView>(R.id.patchList)
        val logView = findViewById<TextView>(R.id.logView)
        val applyButton = findViewById<Button>(R.id.applyButton)

        list.adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_multiple_choice,
            patches.map { it.title }
        )
        list.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        applyButton.setOnClickListener {
            selected.clear()
            for (i in patches.indices) {
                if (list.isItemChecked(i)) selected.add(patches[i].id)
            }
            logView.text = "מתחיל...\n"
            thread {
                val workDir = File(cacheDir, "patchwork").also { it.mkdirs() }
                val engine = PatchEngine(this, workDir)
                for (patch in patches.filter { it.id in selected }) {
                    engine.apply(patch) { line ->
                        runOnUiThread { logView.append(line + "\n") }
                    }
                }
                runOnUiThread { logView.append("סיום.\n") }
            }
        }
    }
}
