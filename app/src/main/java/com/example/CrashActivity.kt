package com.example

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.File

/** Standalone crash screen; kept in a separate process so it can survive an app crash. */
class CrashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val report = intent.getStringExtra(EXTRA_REPORT)
            ?: runCatching { File(filesDir, CrashHandler.REPORT_FILE).readText(Charsets.UTF_8) }
                .getOrDefault("Crash report could not be read. Please relaunch the app and reproduce the crash.")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(16))
            setBackgroundColor(0xFFF7F9FC.toInt())
        }

        val heading = TextView(this).apply {
            text = "e-Barangay Sua stopped"
            textSize = 22f
            setTextColor(0xFF142B45.toInt())
            setTypeface(typeface, Typeface.BOLD)
        }
        root.addView(heading, matchWrap())

        val description = TextView(this).apply {
            text = "The app encountered an unexpected error. Copy the report below and send it to the developer. The report stays on this device unless you choose to share it."
            textSize = 14f
            setTextColor(0xFF334155.toInt())
            setPadding(0, dp(10), 0, dp(14))
        }
        root.addView(description, matchWrap())

        val copyButton = Button(this).apply {
            text = "Copy error report"
            isAllCaps = false
            setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("e-Barangay Sua crash report", report))
                Toast.makeText(this@CrashActivity, "Crash report copied", Toast.LENGTH_SHORT).show()
            }
        }
        root.addView(copyButton, matchWrap())

        val closeButton = Button(this).apply {
            text = "Close"
            isAllCaps = false
            setOnClickListener { finishAndRemoveTask() }
        }
        root.addView(closeButton, matchWrap())

        val reportView = TextView(this).apply {
            text = report
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setTextColor(0xFF111827.toInt())
            setTextIsSelectable(true)
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setBackgroundColor(0xFFE9EEF5.toInt())
        }
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            addView(reportView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ).apply { topMargin = dp(12) })

        setContentView(root)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun matchWrap() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    companion object {
        const val EXTRA_REPORT = "com.example.extra.CRASH_REPORT"
    }
}
