package com.sbdor.onlinestoreclaude.features.cart.models

import com.sbdor.onlinestoreclaude.features.products.models.Product

data class CartItem(
    val product: Product,
    val quantity: Int,
) {
    // Computed property — no need to store this value, it's derived from other fields.
    // Equivalent to a getter in Dart: double get totalPrice => product.price * quantity;
    val totalPrice: Double get() = (product.price ?: 0.0) * quantity
}
