package com.sbdor.onlinestoreclaude

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp triggers Hilt's code generation for DI.
// Every Android app using Hilt must have this on the Application class.
@HiltAndroidApp
class App : Application()
