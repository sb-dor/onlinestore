package com.sbdor.onlinestoreclaude.di

import androidx.compose.runtime.staticCompositionLocalOf

// ---------------------------------------------------------------------------
// LocalDependenciesScope — makes Dependencies available anywhere in the Compose tree
// ---------------------------------------------------------------------------
// Composable functions cannot use @Inject (they are not managed by a DI framework).
// CompositionLocal is Compose's built-in mechanism for passing values implicitly
// down the composition tree — without threading them through every function parameter.
//
// HOW IT WORKS:
//   1. MainActivity wraps the whole UI in:
//        CompositionLocalProvider(LocalDependenciesScope provides container) { ... }
//   2. Any composable inside that tree can read it with:
//        val container = LocalDependenciesScope.current
//
// HILT EQUIVALENT:
//   With Hilt, screens called hiltViewModel() which internally used EntryPointAccessors
//   to pull bindings out of the Hilt component. No explicit container reference needed.
//   With manual DI, LocalDependenciesScope plays that role instead.
//
// WHY staticCompositionLocalOf (not compositionLocalOf):
//   - compositionLocalOf: reading composables re-compose when value changes (dynamic)
//   - staticCompositionLocalOf: the ENTIRE subtree re-composes on change (static)
//   Dependencies is created once in Application.onCreate() and never replaced,
//   so staticCompositionLocalOf is correct and more efficient here.
//
// The lambda `{ error(...) }` is the default value — it runs if someone reads
// LocalDependenciesScope without a CompositionLocalProvider above it in the tree,
// which would be a programming mistake. Crashing early makes the bug obvious.
// ---------------------------------------------------------------------------
val LocalDependenciesScope = staticCompositionLocalOf<Dependencies> {
    error("No AppContainer provided")
}
