package com.example.varahanest

import android.app.Application
import com.example.varahanest.di.AppContainer

class VarahaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

