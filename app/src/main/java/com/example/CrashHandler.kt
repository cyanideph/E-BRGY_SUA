package com.example

import android.content.Intent
import android.os.Build
import android.os.Process
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Captures uncaught app crashes locally and opens a separate-process screen
 * where the user can copy the complete diagnostic report.
 */
class CrashHandler(
    private val app: android.app.Application,
    private val previousHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        if (!handling.compareAndSet(false, true)) {
            previousHandler?.uncaughtException(thread, throwable)
            return
        }

        val report = buildString {
            appendLine("e-Barangay Sua crash report")
            appendLine("Time: " + SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z", Locale.US).format(Date()))
            appendLine("App version: " + runCatching {
                val info = app.packageManager.getPackageInfo(app.packageName, 0)
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    info.longVersionCode
                } else {
                    info.versionCode.toLong()
                }
                "${info.versionName} ($versionCode)"
            }.getOrDefault("unknown"))
            appendLine("Package: ${app.packageName}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Thread: ${thread.name} (id=${thread.id})")
            appendLine()
            appendLine("Exception:")
            val writer = StringWriter()
            throwable.printStackTrace(PrintWriter(writer))
            append(writer.toString())
        }

        runCatching {
            File(app.filesDir, REPORT_FILE).writeText(report, Charsets.UTF_8)
        }.onFailure {
            Log.e(TAG, "Could not save crash report", it)
        }

        runCatching {
            val intent = Intent(app, CrashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(CrashActivity.EXTRA_REPORT, report)
            }
            Log.e(TAG, "Requesting crash report activity launch")
            app.startActivity(intent)
            // Activity launch is asynchronous. Give Android time to start the
            // dedicated :crash process before terminating the broken process.
            // Without this handoff window, startup crashes can kill the task
            // before the report screen becomes visible on some devices.
            try {
                Thread.sleep(1_000)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }.onFailure {
            Log.e(TAG, "Could not open crash report screen", it)
            previousHandler?.uncaughtException(thread, throwable)
            return
        }

        // The crash screen runs in :crash, so terminate the broken app process
        // only after giving the separate process a chance to become visible.
        Log.e(TAG, "Crash report activity launch requested; terminating crashed process")
        Process.killProcess(Process.myPid())
        kotlin.system.exitProcess(10)
    }

    companion object {
        const val REPORT_FILE = "last_crash_report.txt"
        private const val TAG = "EBargSuaCrashHandler"
        private val handling = AtomicBoolean(false)
    }
}
