package com.sbdor.onlinestoreclaude.features.cart.controller

import androidx.lifecycle.ViewModel
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: ICartRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<CartState>(CartState.Initial)
    val state: StateFlow<CartState> = _state.asStateFlow()

    fun load() {
        val items = cartRepository.getCartItems()
        _state.value = if (items.isEmpty()) {
            CartState.Empty
        } else {
            CartState.Completed(
                items = items,
                total = cartRepository.getCartTotal(),
            )
        }
    }

    fun removeItem(productId: Int) {
        cartRepository.removeFromCart(productId)
        load()
    }

    fun increaseQuantity(productId: Int) {
        val item = cartRepository.getCartItems().find { it.product.id == productId }
        if (item != null) {
            cartRepository.updateQuantity(productId, item.quantity + 1)
            load()
        }
    }

    fun decreaseQuantity(productId: Int) {
        val item = cartRepository.getCartItems().find { it.product.id == productId }
        if (item != null) {
            cartRepository.updateQuantity(productId, item.quantity - 1)
            load()
        }
    }
}
