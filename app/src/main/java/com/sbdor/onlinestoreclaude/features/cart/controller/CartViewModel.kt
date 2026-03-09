package com.sbdor.onlinestoreclaude.features.cart.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel(
    private val cartRepository: ICartRepository,
) : ViewModel() {

    companion object {
        fun factory(cartRepository: ICartRepository) = viewModelFactory {
            initializer { CartViewModel(cartRepository) }
        }
    }

    private val _state = MutableStateFlow<CartState>(CartState.Initial)
    val state: StateFlow<CartState> = _state.asStateFlow()

    // NOTICE: load() is a normal fun with NO viewModelScope.launch inside.
    // This is because getCartItems() and getCartTotal() are regular synchronous functions
    // (not suspend). They return immediately — no network, no delay, no async work.
    // Contrast with ProductsViewModel.load() which uses viewModelScope.launch because
    // productRepository.searchProducts() IS a suspend fun (simulates async work).
    //
    // ALTERNATIVE: if the cart were backed by a Room database, getCartItems() would be
    // a suspend fun and you would need viewModelScope.launch here:
    //
    // fun load() {
    //     viewModelScope.launch {
    //         val items = cartRepository.getCartItems()  // suspend — database call
    //         _state.value = if (items.isEmpty()) CartState.Empty else CartState.Completed(...)
    //     }
    // }
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

    // removeItem, increaseQuantity, decreaseQuantity all call load() after mutating state.
    // This is a simple "mutate then reload" pattern — synchronous, no coroutines needed.
    // ALTERNATIVE: if CartRepositoryImpl exposed a StateFlow or Flow of cart items,
    // the ViewModel could collect it reactively and load() would not be needed at all:
    //
    // val state: StateFlow<CartState> = cartRepository.cartItems
    //     .map { items -> if (items.isEmpty()) CartState.Empty else CartState.Completed(items) }
    //     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CartState.Initial)
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
