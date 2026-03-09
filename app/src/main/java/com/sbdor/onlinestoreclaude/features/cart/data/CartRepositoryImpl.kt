package com.sbdor.onlinestoreclaude.features.cart.data

import com.sbdor.onlinestoreclaude.features.cart.models.CartItem
import com.sbdor.onlinestoreclaude.features.products.models.Product

class CartRepositoryImpl : ICartRepository {

    private val _cartItems = mutableListOf<CartItem>()

    override fun getCartItems(): List<CartItem> = _cartItems.toList()

    override fun addToCart(product: Product) {
        val existingItem = _cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            // data class .copy() = creates a new instance with some fields changed.
            // Same as copyWith() in Dart.
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            _cartItems.add(CartItem(product = product, quantity = 1))
        }
    }

    override fun removeFromCart(productId: Int) {
        _cartItems.removeAll { it.product.id == productId }
    }

    override fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        val index = _cartItems.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = quantity)
        }
    }

    override fun clearCart() {
        _cartItems.clear()
    }

    override fun getCartTotal(): Double = _cartItems.sumOf { it.totalPrice }
}
