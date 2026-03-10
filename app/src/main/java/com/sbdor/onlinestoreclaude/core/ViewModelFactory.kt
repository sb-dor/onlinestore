package com.sbdor.onlinestoreclaude.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ---------------------------------------------------------------------------
// viewModelFactory — helper that creates a ViewModelProvider.Factory from a lambda
// ---------------------------------------------------------------------------
// ViewModelProvider.Factory is the interface Android uses to construct ViewModels.
// Without it, the system can only create ViewModels that have a no-arg constructor.
// When your ViewModel takes constructor parameters (repositories, IDs, etc.)
// you must provide a factory so Android knows how to build it.
//
// This helper removes the boilerplate of writing the full anonymous object every time.
//
// WITHOUT this helper (verbose, but equivalent):
//
//   val factory = object : ViewModelProvider.Factory {
//       override fun <T : ViewModel> create(modelClass: Class<T>): T {
//           return CartViewModel(cartRepository) as T
//       }
//   }
//   val viewModel: CartViewModel = viewModel(factory = factory)
//
// WITH this helper (concise):
//
//   val viewModel: CartViewModel = viewModel(
//       factory = viewModelFactory { CartViewModel(cartRepository) }
//   )
//
// ---------------------------------------------------------------------------
// TWO WAYS TO USE THIS IN SCREENS
// ---------------------------------------------------------------------------
//
// OPTION A — inline factory in the screen (no companion object needed in ViewModel):
//
//   val container = LocalDependenciesScope.current
//   val viewModel: CartViewModel = viewModel(
//       factory = viewModelFactory { CartViewModel(container.cartRepository) }
//   )
//
// OPTION B — companion object factory inside the ViewModel class:
//
//   class CartViewModel(...) : ViewModel() {
//       companion object {
//           fun factory(repo: ICartRepository) = viewModelFactory {
//               CartViewModel(repo)
//           }
//       }
//   }
//   // In screen:
//   val viewModel: CartViewModel = viewModel(
//       factory = CartViewModel.factory(container.cartRepository)
//   )
//
// Both options produce identical runtime behaviour. Option A keeps the ViewModel
// clean (no companion object). Option B keeps the screen clean (factory logic
// lives next to the ViewModel). Choose based on preference — this project uses
// both patterns intentionally so you can compare them.
//
// HILT EQUIVALENT:
//   With Hilt you wrote nothing — hiltViewModel() called the generated factory
//   automatically. Manual DI requires this one extra step per ViewModel.
// ---------------------------------------------------------------------------
fun <VM : ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}
