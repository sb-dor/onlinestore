package com.sbdor.onlinestoreclaude.features.order.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.order.data.IOrderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------------------
// @AssistedInject PATTERN — dynamic dependency injection at runtime
// ---------------------------------------------------------------------------
// Previously, OrderViewModel used @HiltViewModel + @Inject constructor, which means
// Hilt fully controlled which IOrderRepository implementation to inject — decided
// at compile time in AppModule via @Binds.
//
// PROBLEM with the old approach: you could only ever use ONE implementation of
// IOrderRepository across the entire app (whichever AppModule bound).
// To use a different impl on a different screen, you'd need @Named + two separate
// ViewModels or SavedStateHandle tricks.
//
// SOLUTION — @AssistedInject:
// Hilt still injects everything it knows about (ICartRepository, etc.),
// but the @Assisted parameter (IOrderRepository) is passed BY THE CALLER at runtime.
// This means each screen can pass a DIFFERENT implementation of IOrderRepository
// to the same ViewModel class — no AppModule changes, no @Named qualifiers needed.
//
// COMPARE:
// Old:  Hilt creates OrderViewModel and decides the repository — fixed at compile time.
// New:  Caller creates OrderViewModel via factory and passes the repository — chosen at runtime.
// ---------------------------------------------------------------------------

// @AssistedFactory = Hilt generates the implementation of this interface automatically.
// It acts as the bridge between Hilt's DI graph and the assisted (runtime) parameter.
// The screen calls factory.create(someRepository) to get an OrderViewModel instance
// with that specific repository injected.
@AssistedFactory
interface OrderViewModelFactory {
    fun create(orderRepository: IOrderRepository): OrderViewModel
}

// @HiltViewModel(assistedFactory = ...) = tells Hilt this ViewModel uses assisted injection.
// @AssistedInject = replaces @Inject. Hilt handles all normal params, caller provides @Assisted params.
// @Assisted = marks which constructor param is provided by the caller (not by Hilt's DI graph).
//
// cartRepository has NO @Assisted — Hilt still injects it automatically from AppModule.
// orderRepository HAS @Assisted — the caller (OrderScreen) provides it at creation time.
@HiltViewModel(assistedFactory = OrderViewModelFactory::class)
class OrderViewModel @AssistedInject constructor(
    @Assisted private val orderRepository: IOrderRepository,
    private val cartRepository: ICartRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<OrderState>(OrderState.Initial)
    val state: StateFlow<OrderState> = _state.asStateFlow()

    fun placeOrder() {
        val items = cartRepository.getCartItems()

        if (items.isEmpty()) {
            _state.value = OrderState.Error("Cart is empty")
            return
        }

        viewModelScope.launch {
            _state.value = OrderState.Loading
            try {
                val order = orderRepository.placeOrder(items)
                // Clear the cart after a successful order
                cartRepository.clearCart()
                _state.value = OrderState.Completed(order = order)
            } catch (e: Exception) {
                _state.value = OrderState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // ---------------------------------------------------------------------------
    // ALTERNATIVE IMPLEMENTATION — suspend fun (no viewModelScope.launch inside)
    // ---------------------------------------------------------------------------
    // This version makes placeOrder itself a suspend function.
    // The coroutine responsibility moves UP to the caller (the UI layer).
    //
    // PROBLEM: the Composable must now use rememberCoroutineScope() and call
    // scope.launch { viewModel.placeOrder() } on button click — the UI becomes
    // aware of async details, which is NOT recommended.
    //
    // USE THIS when: placeOrder is called from another suspend fun or coroutine
    // inside the ViewModel itself, not directly from the UI.
    //
    // suspend fun placeOrder() {
    //     val items = cartRepository.getCartItems()
    //
    //     if (items.isEmpty()) {
    //         _state.value = OrderState.Error("Cart is empty")
    //         return
    //     }
    //
    //     _state.value = OrderState.Loading
    //     try {
    //         val order = orderRepository.placeOrder(items)  // no launch needed — already in coroutine
    //         cartRepository.clearCart()
    //         _state.value = OrderState.Completed(order = order)
    //     } catch (e: Exception) {
    //         _state.value = OrderState.Error(e.message ?: "Unknown error")
    //     }
    // }
    //
    // Caller in Composable would look like:
    //
    //     val scope = rememberCoroutineScope()
    //     Button(onClick = {
    //         scope.launch { viewModel.placeOrder() }  // UI manages the coroutine — bad practice
    //     })
    //
    // vs the current approach (preferred):
    //
    //     Button(onClick = { viewModel.placeOrder() }) // UI stays simple — good practice
    // ---------------------------------------------------------------------------
}
