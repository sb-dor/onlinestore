package com.sbdor.onlinestoreclaude

import android.app.Application
import com.sbdor.onlinestoreclaude.core.AppContainer

class App : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
