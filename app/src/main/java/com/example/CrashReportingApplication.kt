package com.example

import android.app.Application
import android.util.Log

class CrashReportingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this, previous))
        Log.i("EBargSuaCrashHandler", "Crash report handler installed")
    }
}
