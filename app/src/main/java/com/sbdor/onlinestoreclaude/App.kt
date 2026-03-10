package com.sbdor.onlinestoreclaude

import android.app.Application
import com.sbdor.onlinestoreclaude.di.Dependencies

// ---------------------------------------------------------------------------
// App — Application subclass, entry point for the whole process
// ---------------------------------------------------------------------------
// Application is created once when the process starts — before any Activity,
// ViewModel, or Composable. It is the correct place to initialise app-wide
// singletons like the dependency container.
//
// `lateinit var container` means the property is non-null but assigned after
// construction (in onCreate). This is safe because Android always calls
// onCreate() before the app does anything useful.
//
// HILT EQUIVALENT:
//   @HiltAndroidApp
//   class App : Application()
//
//   The @HiltAndroidApp annotation told Hilt's code generator to:
//     1. Create a Hilt component (SingletonComponent) tied to this Application
//     2. Wire all @Singleton bindings into it
//     3. Inject them on demand via @Inject or hiltViewModel()
//
// With manual DI: we do the same thing ourselves — create Dependencies(this) once
// and store it. No annotation, no generated code, no magic.
// ---------------------------------------------------------------------------
class App : Application() {
    lateinit var container: Dependencies

    override fun onCreate() {
        super.onCreate()
        // `this` is the Application Context — safe to hold long-term.
        // Never pass an Activity context here — Activities are destroyed and recreated.
        container = Dependencies(this)
    }
}
