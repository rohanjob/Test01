package com.example.varahanest

import android.app.Application
import com.example.varahanest.di.AppContainer

class VarahaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("VarahaCrash", "CRASH DETECTED", throwable)
            try {
                val file = java.io.File(externalCacheDir, "crash_log.txt")
                file.writeText(android.util.Log.getStackTraceString(throwable))
            } catch (e: Exception) {
                // Ignore writing error
            }
            // Let the system handle the crash after logging
            System.exit(1)
        }

        super.onCreate()
        container = AppContainer(this)
    }
}

