package com.sbdor.onlinestoreclaude

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.navigation.AppNavigation
import com.sbdor.onlinestoreclaude.ui.theme.OnlinestoreclaudeTheme

// ---------------------------------------------------------------------------
// MainActivity — wires the dependency container into the Compose tree
// ---------------------------------------------------------------------------
// HILT EQUIVALENT:
//   @AndroidEntryPoint
//   class MainActivity : ComponentActivity()
//
//   @AndroidEntryPoint told Hilt to inject dependencies into this Activity
//   (and enabled hiltViewModel() inside its Compose content).
//
// With manual DI:
//   1. We retrieve the container from the Application (created in App.onCreate).
//   2. We provide it to the entire Compose tree via CompositionLocalProvider.
//   Any composable inside AppNavigation() can now call
//   `val container = LocalDependenciesScope.current` to access repositories.
//
// WHY (application as App).container?
//   `application` is the Application instance for this process.
//   We cast it to App (our subclass) to access the `container` property.
//   This is the standard pattern for accessing app-level singletons
//   when not using a DI framework.
// ---------------------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as App).container
        setContent {
            OnlinestoreclaudeTheme {
                // CompositionLocalProvider makes `container` available to every
                // composable in the tree without passing it as a function parameter.
                CompositionLocalProvider(LocalDependenciesScope provides container) {
                    AppNavigation()
                }
            }
        }
    }
}
