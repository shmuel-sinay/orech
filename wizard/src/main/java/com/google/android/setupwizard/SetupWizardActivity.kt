package com.google.android.setupwizard

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * אשף הגדרה ראשונית של כושר טק. מחליף את האשף של גוגל באותו שם חבילה ובאותו מיקום.
 *
 * שפה ואזור זמן מוגדרים כאן בלי שאלה (עברית, ישראל). המוכר בודק שעה, מזין מספר מכשיר ובוחר גרסה עם PIN.
 * האשף עצמו בלי root: הבקשה נשלחת בתכונת מערכת לשירות root (/vendor/bin/orech_wizard.sh) שמבצע ומחזיר תשובה.
 * דגל "הגרסה נבחרה" נשמר ב-protect_s ושורד איפוס, והשירות מפרסם אותו בעלייה כ-sys.orech.chosen: אם הוא קיים
 * האשף לא שואל כלום ומסיים. כך לקוח שמאפס את המכשיר לא יכול לבחור גרסה אחרת.
 */
class SetupWizardActivity : Activity() {

    private val bg = Color.parseColor("#FDF8F5")
    private val ink = Color.parseColor("#2B2420")
    private val muted = Color.parseColor("#6B625C")
    private val accent = Color.parseColor("#8C5A2B")
    private val line = Color.parseColor("#E3D8CF")
    private val sel = Color.parseColor("#F6E7D6")
    private val disabledBtn = Color.parseColor("#C9B9AA")

    private class Option(val id: String, val title: String, val desc: String)

    private val options = listOf(
        Option("locked", "שמור", "אפליקציות בסיס בלבד. אי אפשר להתקין שום דבר."),
        Option("store", "שמור עם חנות פנימית", "התקנה רק מתוך החנות הפנימית."),
        Option("open", "פתוחה", "אפשר להתקין גם קובצי אפליקציה מבחוץ.")
    )

    private val ui = Handler(Looper.getMainLooper())
    private var devId = ""
    private var chosen: String? = null
    private var notice = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDefaults()
        if (prop("sys.orech.chosen").isNotEmpty()) {
            complete()
            return
        }
        showTime()
    }

    @Deprecated("אין חזרה אחורה מהאשף")
    override fun onBackPressed() {}

    // ---------- ברירות מחדל ----------

    private fun applyDefaults() {
        try {
            val lang = Locale.getDefault().language
            if (lang != "iw" && lang != "he") {
                Class.forName("com.android.internal.app.LocalePicker")
                    .getMethod("updateLocale", Locale::class.java)
                    .invoke(null, Locale("he", "IL"))
            }
        } catch (_: Throwable) {
        }
        try {
            if (TimeZone.getDefault().id != "Asia/Jerusalem") {
                (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setTimeZone("Asia/Jerusalem")
            }
            Settings.Global.putInt(contentResolver, Settings.Global.AUTO_TIME_ZONE, 0)
        } catch (_: Throwable) {
        }
        try {
            Settings.System.putString(contentResolver, Settings.System.TIME_12_24, "24")
        } catch (_: Throwable) {
        }
    }

    private fun prop(key: String): String = try {
        Class.forName("android.os.SystemProperties").getMethod("get", String::class.java, String::class.java)
            .invoke(null, key, "") as String
    } catch (_: Throwable) {
        ""
    }

    private fun setProp(key: String, value: String): Boolean = try {
        Class.forName("android.os.SystemProperties").getMethod("set", String::class.java, String::class.java)
            .invoke(null, key, value)
        true
    } catch (_: Throwable) {
        false
    }

    // ---------- בניית מסכים ----------

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun label(text: String, size: Float, color: Int) = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(color)
        setLineSpacing(0f, 1.15f)
    }

    private fun btnBg(fill: Int, stroke: Int) = GradientDrawable().apply {
        setColor(fill)
        setStroke(dp(1), stroke)
        cornerRadius = dp(2).toFloat()
    }

    private fun button(text: String, primary: Boolean, onClick: () -> Unit) = TextView(this).apply {
        this.text = text
        textSize = 15f
        gravity = Gravity.CENTER
        setPadding(dp(12), dp(12), dp(12), dp(12))
        setTextColor(if (primary) Color.WHITE else accent)
        background = btnBg(if (primary) accent else Color.TRANSPARENT, accent)
        setOnClickListener { if (isEnabled) onClick() }
    }

    private fun setEnabledLook(b: TextView, enabled: Boolean) {
        b.isEnabled = enabled
        b.background = if (enabled) btnBg(accent, accent) else btnBg(disabledBtn, disabledBtn)
    }

    /** מסך: כותרת עליונה, גוף שנגלל, ושורת כפתורים תחתונה. */
    private fun page(step: String, title: String, body: LinearLayout, actions: List<View>) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setBackgroundColor(bg)
        }
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(8))
            addView(label("כושר טק · $step", 12f, muted))
            addView(label(title, 24f, ink))
        })
        if (notice.isNotEmpty()) {
            root.addView(label(notice, 13f, Color.parseColor("#9B2C2C")).apply {
                setPadding(dp(20), dp(4), dp(20), dp(4))
            })
            notice = ""
        }
        root.addView(ScrollView(this).apply { addView(body) },
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        if (actions.isNotEmpty()) {
            val bar = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(dp(16), dp(12), dp(16), dp(18))
            }
            actions.forEachIndexed { i, v ->
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                if (i > 0) lp.marginStart = dp(10)
                bar.addView(v, lp)
            }
            root.addView(bar)
        }
        setContentView(root)
    }

    private fun body() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(8), dp(20), dp(8))
    }

    // ---------- שלב 1: תאריך ושעה ----------

    private fun showTime() {
        val b = body()
        val now = label("", 22f, ink).apply { setPadding(0, dp(12), 0, dp(4)) }
        fun refresh() {
            now.text = SimpleDateFormat("EEEE, d בMMMM yyyy\nHH:mm", Locale("he", "IL")).format(Date())
        }
        refresh()
        b.addView(label("בדוק שהשעון במכשיר נכון. שעה שגויה פוגעת באתרים ובאפליקציות.", 14f, muted))
        b.addView(now)
        b.addView(label("אזור זמן: ישראל", 13f, muted).apply { setPadding(0, 0, 0, dp(16)) })
        b.addView(button("שינוי תאריך", false) {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                val n = Calendar.getInstance()
                n.set(y, m, d)
                setClock(n)
                refresh()
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]).show()
        }, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
        b.addView(button("שינוי שעה", false) {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, h, min ->
                val n = Calendar.getInstance()
                n.set(Calendar.HOUR_OF_DAY, h)
                n.set(Calendar.MINUTE, min)
                n.set(Calendar.SECOND, 0)
                setClock(n)
                refresh()
            }, c[Calendar.HOUR_OF_DAY], c[Calendar.MINUTE], true).show()
        }, LinearLayout.LayoutParams(-1, -2))
        page("שלב 1 מתוך 3", "תאריך ושעה", b, listOf(button("המשך", true) { showId() }))
    }

    private fun setClock(picked: Calendar) {
        try {
            (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setTime(picked.timeInMillis)
        } catch (_: Throwable) {
        }
    }

    // ---------- שלב 2: מספר מכשיר ----------

    private fun showId() {
        val b = body()
        b.addView(label("מספר המכשיר נרשם ברשימת המכירות ומופיע בהגדרות. אפשר לדלג.", 14f, muted))
        val input = EditText(this).apply {
            hint = "3-8 תווים: אותיות באנגלית ומספרים"
            setText(devId)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            filters = arrayOf(InputFilter.AllCaps(), InputFilter.LengthFilter(8))
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            textSize = 20f
            setTextColor(ink)
        }
        b.addView(input, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(16) })
        page("שלב 2 מתוך 3", "מספר מכשיר", b, listOf(
            button("דלג", false) { devId = ""; showVersion() },
            button("המשך", true) {
                val v = input.text.toString().trim().uppercase()
                if (Regex("[A-Z0-9]{3,8}").matches(v)) {
                    devId = v
                    showVersion()
                } else {
                    input.error = "3-8 תווים, אותיות באנגלית ומספרים בלבד"
                }
            }
        ))
    }

    // ---------- שלב 3: בחירת גרסה ----------

    private fun rowBackground(selected: Boolean) = LayerDrawable(
        arrayOf(ColorDrawable(if (selected) sel else Color.TRANSPARENT), ColorDrawable(line))
    ).apply {
        setLayerGravity(1, Gravity.BOTTOM)
        setLayerHeight(1, dp(1))
    }

    private fun showVersion() {
        val b = body().apply { setPadding(dp(8), dp(8), dp(8), dp(8)) }
        val rows = mutableMapOf<String, LinearLayout>()
        val apply = button("החל על המכשיר", true) { askPin() }
        for (o in options) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(14), dp(14), dp(14), dp(14))
                background = rowBackground(chosen == o.id)
                addView(label(o.title, 17f, ink))
                addView(label(o.desc, 13f, muted).apply { setPadding(0, dp(2), 0, 0) })
                setOnClickListener {
                    chosen = o.id
                    for ((id, v) in rows) v.background = rowBackground(id == o.id)
                    setEnabledLook(apply, true)
                }
            }
            rows[o.id] = row
            b.addView(row)
        }
        setEnabledLook(apply, chosen != null)
        page("שלב 3 מתוך 3", "גרסה ללקוח", b, listOf(apply))
    }

    private fun askPin() {
        val id = chosen ?: return
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "קוד PIN"
            filters = arrayOf(InputFilter.LengthFilter(16))
            layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
        val title = options.first { it.id == id }.title
        AlertDialog.Builder(this)
            .setTitle("לעבור ל\"$title\"?")
            .setMessage("אי אפשר לשנות את הבחירה אחרי האישור. המכשיר יופעל מחדש.")
            .setView(input)
            .setPositiveButton("אישור") { _, _ -> send(id, input.text.toString()) }
            .setNegativeButton("חזרה", null)
            .show()
    }

    // ---------- שליחה לשירות root והמתנה לתשובה ----------

    private fun send(profile: String, pin: String) {
        val b = body().apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(20), dp(60), dp(20), dp(8))
        }
        b.addView(ProgressBar(this))
        b.addView(label("מחיל את ההגדרות. אל תכבה את המכשיר.", 15f, muted).apply { setPadding(0, dp(16), 0, 0) })
        page("מסיים", "רק רגע", b, emptyList())
        setProp("sys.orech.result", "")
        val nonce = System.currentTimeMillis() % 100000
        if (!setProp("sys.orech.req", "$profile:$pin:$devId:$nonce")) {
            fail("fail:send")
            return
        }
        var waited = 0
        val poll = object : Runnable {
            override fun run() {
                val r = prop("sys.orech.result")
                when {
                    r == "ok" -> finishUp()
                    r.startsWith("fail") -> fail(r)
                    waited > 120_000 -> fail("fail:timeout")
                    else -> {
                        waited += 500
                        ui.postDelayed(this, 500)
                    }
                }
            }
        }
        ui.postDelayed(poll, 500)
    }

    private fun fail(code: String) {
        if (code == "fail:already") {
            complete()
            return
        }
        notice = when (code) {
            "fail:pin" -> "קוד שגוי."
            "fail:locked" -> "יותר מדי ניסיונות שגויים. הבחירה ננעלה."
            "fail:id" -> "מספר המכשיר לא תקין."
            else -> "הפעולה נכשלה ($code). אפשר לנסות שוב."
        }
        showVersion()
    }

    private fun finishUp() {
        complete()
        setProp("sys.orech.finish", "1")
    }

    /** סיום ההגדרה: מסמן שהמכשיר הוגדר, משבית את האשף ופותח את מסך הבית. */
    private fun complete() {
        try {
            Settings.Global.putInt(contentResolver, Settings.Global.DEVICE_PROVISIONED, 1)
            Settings.Secure.putInt(contentResolver, "user_setup_complete", 1)
            // בלי זה המכשיר לא עבד עד הסוף באשף הקודם (ראו PROMPT_NEXT_SESSION.md) - קוסמטי, אבל נשמר מההרגל.
            Settings.Global.putInt(contentResolver, "disable_window_blurs", 1)
        } catch (_: Throwable) {
        }
        try {
            packageManager.setComponentEnabledSetting(
                ComponentName(this, SetupWizardActivity::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
            )
        } catch (_: Throwable) {
        }
        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }
}
