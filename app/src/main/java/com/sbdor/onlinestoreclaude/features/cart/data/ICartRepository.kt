package com.sbdor.onlinestoreclaude.features.cart.data

import com.sbdor.onlinestoreclaude.features.cart.models.CartItem
import com.sbdor.onlinestoreclaude.features.products.models.Product

// Cart operations are synchronous (in-memory), so no suspend keyword needed here.
// suspend = the function can pause and resume (used for network/disk I/O).
interface ICartRepository {
    fun getCartItems(): List<CartItem>
    fun addToCart(product: Product)
    fun removeFromCart(productId: Int)
    fun updateQuantity(productId: Int, quantity: Int)
    fun clearCart()
    fun getCartTotal(): Double
}
