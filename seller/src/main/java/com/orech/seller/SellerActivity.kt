package com.orech.seller

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.orech.patcher.Logger
import com.orech.patcher.Patch
import com.orech.patcher.PatchEngine
import com.orech.patcher.RootShell
import org.json.JSONArray
import java.io.File
import java.security.MessageDigest
import kotlin.concurrent.thread

/** בורר גרסה ללקוח: מסך יחיד, אישור ב-PIN, החלה, והסרה עצמית. */
class SellerActivity : AppCompatActivity() {

    private data class Profile(
        val id: String,
        val title: String,
        val desc: String,
        val icon: String,
        val patchIds: List<String>,
        val storeInstallPending: Boolean
    )

    private enum class State { OPEN, LOCKED, UNKNOWN }

    private val installerPath = "/system/priv-app/GooglePackageInstaller/GooglePackageInstaller.apk"

    private lateinit var profiles: List<Profile>
    private lateinit var patches: List<Patch>
    private var state = State.UNKNOWN
    private var selectedId: String? = null
    private var failedPins = 0
    private var busy = false

    private val logger: Logger by lazy { Logger(this) }
    private val engine: PatchEngine by lazy {
        PatchEngine(this, File(getExternalFilesDir(null), "patchwork").also { it.mkdirs() }, logger)
    }

    private lateinit var optionsBox: LinearLayout
    private lateinit var currentView: TextView
    private lateinit var applyButton: TextView
    private lateinit var logScroll: ScrollView
    private lateinit var logView: TextView
    private val rows = mutableMapOf<String, View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)

        patches = Patch.loadAll(assets.open("patches/patches.json").bufferedReader().readText())
        profiles = loadProfiles()

        optionsBox = findViewById(R.id.options)
        currentView = findViewById(R.id.currentState)
        applyButton = findViewById(R.id.applyButton)
        logScroll = findViewById(R.id.logScroll)
        logView = findViewById(R.id.logView)

        findViewById<View>(R.id.cancelButton).setOnClickListener { if (!busy) finish() }
        applyButton.setOnClickListener { if (!busy) askPin() }

        buildRows()
        buildIdRow()
        refreshApply()
        detectState()
    }


    private lateinit var idView: TextView

    /** מספר מכשיר קצר: נשמר ב-nvdata (שורד איפוס יצרן), מוצג בהגדרות, ונרשם גם ברשימת המכירות של המוכר. */
    private fun buildIdRow() {
        idView = TextView(this).apply {
            textSize = 16f
            setPadding(32, 24, 32, 24)
            text = "מספר מכשיר: ..."
            setOnClickListener { if (!busy) askDeviceId() }
        }
        optionsBox.addView(idView, 0)
        thread {
            val r = RootShell.exec("cat /mnt/vendor/nvdata/orech_id 2>/dev/null").firstOrNull()
            val cur = r?.takeIf { it.second == 0 }?.third?.trim().orEmpty()
            runOnUiThread { idView.text = "מספר מכשיר: " + (if (cur.isEmpty()) "לא הוגדר" else cur) + "  (לחיצה לשינוי)" }
        }
    }

    private fun askDeviceId() {
        val input = EditText(this).apply { hint = "3-8 תווים, אותיות ומספרים"; inputType = InputType.TYPE_CLASS_TEXT }
        AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
            .setTitle("מספר מכשיר")
            .setView(input)
            .setPositiveButton("שמור") { _, _ ->
                val id = input.text.toString().trim().uppercase()
                if (!Regex("[A-Z0-9]{3,8}").matches(id)) { log("מספר לא תקין"); return@setPositiveButton }
                thread {
                    val r = RootShell.exec(
                        "echo  > /mnt/vendor/nvdata/orech_id",
                        "setprop persist.sys.orech_id "
                    )
                    val ok = r.all { it.second == 0 }
                    if (ok) {
                        val f = File(getExternalFilesDir(null), "sales.csv")
                        f.appendText(java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date()) + "," + id + "\n")
                    }
                    runOnUiThread {
                        log(if (ok) "מספר מכשיר נשמר: $id (רשום ב-sales.csv)" else "השמירה נכשלה")
                        if (ok) idView.text = "מספר מכשיר: $id  (לחיצה לשינוי)"
                    }
                }
            }
            .setNegativeButton("ביטול", null)
            .show()
    }

    private fun loadProfiles(): List<Profile> {
        val arr = JSONArray(assets.open("seller_profiles.json").bufferedReader().readText())
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val ids = o.getJSONArray("patches")
            Profile(
                id = o.getString("id"),
                title = o.getString("title"),
                desc = o.getString("desc"),
                icon = o.getString("icon"),
                patchIds = (0 until ids.length()).map { ids.getString(it) },
                storeInstallPending = o.optString("storeInstall") == "pending"
            )
        }
    }

    private fun buildRows() {
        val inflater = LayoutInflater.from(this)
        for (p in profiles) {
            val row = inflater.inflate(R.layout.row_option, optionsBox, false)
            row.findViewById<TextView>(R.id.title).text = p.title
            row.findViewById<TextView>(R.id.desc).text = p.desc
            row.findViewById<ImageView>(R.id.icon).setImageResource(
                when (p.icon) {
                    "lock" -> R.drawable.ic_lock
                    "store" -> R.drawable.ic_store
                    else -> R.drawable.ic_unlock
                }
            )
            row.setOnClickListener {
                if (busy) return@setOnClickListener
                selectedId = p.id
                for ((id, v) in rows) {
                    v.isSelected = id == p.id
                    v.findViewById<View>(R.id.radio).isSelected = id == p.id
                }
                refreshApply()
            }
            rows[p.id] = row
            optionsBox.addView(row)
        }
    }

    /** מצב נוכחי לפי hash של קובץ המתקין: מקורי = פתוחה, אחר = חסום. */
    private fun detectState() {
        val stock = patches.firstOrNull { it.id == "installer_password_lock" }?.expectedSha256
        thread {
            val r = RootShell.exec("sha256sum '$installerPath'").firstOrNull()
            val hash = r?.takeIf { it.second == 0 }?.third?.trim()?.split(Regex("\\s+"))?.firstOrNull()
            val s = when {
                hash == null || hash.length != 64 -> State.UNKNOWN
                hash == stock -> State.OPEN
                else -> State.LOCKED
            }
            runOnUiThread {
                state = s
                currentView.text = when (s) {
                    State.OPEN -> "מותקנת כעת: פתוחה"
                    State.LOCKED -> "מותקנת כעת: שמור"
                    State.UNKNOWN -> "אי אפשר לקרוא את המצב. אין הרשאת root?"
                }
                refreshApply()
            }
        }
    }

    private fun selected(): Profile? = profiles.firstOrNull { it.id == selectedId }

    /** האם הבחירה זהה למצב הנוכחי (אין מה להחיל, רק לסיים). */
    private fun isNoChange(p: Profile) = when (state) {
        State.OPEN -> p.id == "open"
        State.LOCKED -> p.id != "open"
        State.UNKNOWN -> false
    }

    /** מעבר מחסום חזרה לפתוחה דורש קובץ מקורי, ואין גיבוי כי האפליקציה מוחקת את עצמה. */
    private fun isBlocked(p: Profile) = state == State.LOCKED && p.id == "open"

    private fun refreshApply() {
        val p = selected()
        val enabled = p != null && state != State.UNKNOWN && !isBlocked(p) && !busy
        applyButton.isEnabled = enabled
        applyButton.text = when {
            p == null -> "בחר גרסה"
            isBlocked(p) -> "אי אפשר לחזור לפתוחה מכאן"
            isNoChange(p) -> "אין שינוי. סיום והסרה"
            else -> "החל על המכשיר"
        }
    }

    private fun sha256Hex(s: String) =
        MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }

    private fun askPin() {
        val p = selected() ?: return
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "קוד PIN"
            layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        val msg = if (isNoChange(p)) {
            "לא יבוצע שינוי במכשיר. האפליקציה תסיר את עצמה והמכשיר יופעל מחדש."
        } else {
            "המכשיר יופעל מחדש, והאפליקציה תסיר את עצמה. אי אפשר לבטל מכאן."
        }
        AlertDialog.Builder(this)
            .setTitle("לעבור ל\"${p.title}\"?")
            .setMessage(msg)
            .setView(input)
            .setPositiveButton("אישור") { _, _ ->
                if (sha256Hex(input.text.toString()) == BuildConfig.PIN_SHA256) {
                    run(p)
                } else {
                    failedPins++
                    if (failedPins >= 5) finish()
                    else AlertDialog.Builder(this).setMessage("קוד שגוי").setPositiveButton("סגור", null).show()
                }
            }
            .setNegativeButton("חזרה", null)
            .show()
    }

    private fun log(line: String) = runOnUiThread {
        logView.append(line + "\n")
        logScroll.post { logScroll.fullScroll(View.FOCUS_DOWN) }
    }

    private fun run(p: Profile) {
        busy = true
        optionsBox.visibility = View.GONE
        logScroll.visibility = View.VISIBLE
        refreshApply()
        logView.text = ""

        thread {
            val toRun = p.patchIds.map { id -> patches.first { it.id == id } }
            var ok = true
            if (!isNoChange(p)) {
                // קודם בדיקה בלבד על הכול, ורק אם הכול עבר מחילים בפועל.
                for (patch in toRun) if (ok && !engine.apply(patch, true) { log(it) }) ok = false
                if (ok) for (patch in toRun) if (ok && !engine.commit(patch) { log(it) }) ok = false
            }
            if (ok && p.storeInstallPending) log("התקנת החנות הפנימית עדיין לא הוגדרה. מדלג.")

            if (ok) {
                // השינוי אושר ב-PIN: מעדכנים את מצב הבקרה, ומפעילים אותה (מוחקים את noenforce של שלב הבדיקות)
                val ap = RootShell.exec(
                    "sh /mnt/vendor/protect_s/orech/orech_integrity.sh approve",
                    "rm -f /mnt/vendor/protect_s/orech/noenforce"
                ).firstOrNull()
                if (ap == null || ap.second != 0) log("אזהרה: עדכון בקרת השלמות נכשל")
                log("הושלם. האפליקציה מסירה את עצמה והמכשיר יופעל מחדש.")
                SelfDestruct.run(this)
            } else {
                log("נכשל. לא הוסרה האפליקציה, אפשר לנסות שוב.")
                runOnUiThread {
                    busy = false
                    optionsBox.visibility = View.VISIBLE
                    refreshApply()
                }
            }
        }
    }
}
