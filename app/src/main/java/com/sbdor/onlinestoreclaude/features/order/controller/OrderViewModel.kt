package com.sbdor.onlinestoreclaude.features.order.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.order.data.IOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
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
}
