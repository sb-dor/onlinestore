package com.sbdor.onlinestoreclaude.features.order.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.order.data.IOrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: IOrderRepository,
    private val cartRepository: ICartRepository,
) : ViewModel() {

    companion object {
        fun factory(
            orderRepository: IOrderRepository,
            cartRepository: ICartRepository,
        ) = viewModelFactory {
            initializer { OrderViewModel(orderRepository, cartRepository) }
        }
    }

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
