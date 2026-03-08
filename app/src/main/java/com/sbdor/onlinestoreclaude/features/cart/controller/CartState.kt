package com.sbdor.onlinestoreclaude.features.cart.controller

import com.sbdor.onlinestoreclaude.features.cart.models.CartItem

sealed class CartState {
    object Initial : CartState()
    object Empty : CartState()
    data class Completed(
        val items: List<CartItem>,
        val total: Double,
    ) : CartState()
}
