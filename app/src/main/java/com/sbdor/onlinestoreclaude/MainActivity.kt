package com.sbdor.onlinestoreclaude

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.sbdor.onlinestoreclaude.core.LocalAppContainer
import com.sbdor.onlinestoreclaude.navigation.AppNavigation
import com.sbdor.onlinestoreclaude.ui.theme.OnlinestoreclaudeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as App).container
        setContent {
            OnlinestoreclaudeTheme {
                CompositionLocalProvider(LocalAppContainer provides container) {
                    AppNavigation()
                }
            }
        }
    }
}
