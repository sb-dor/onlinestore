package com.sbdor.onlinestoreclaude

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sbdor.onlinestoreclaude.navigation.AppNavigation
import com.sbdor.onlinestoreclaude.ui.theme.OnlinestoreclaudeTheme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint tells Hilt to inject dependencies into this Activity.
// Every Activity/Fragment that uses Hilt must have this annotation.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlinestoreclaudeTheme {
                AppNavigation()
            }
        }
    }
}
