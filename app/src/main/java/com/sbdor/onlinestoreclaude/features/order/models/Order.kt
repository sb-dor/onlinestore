package com.sbdor.onlinestoreclaude.features.order.models

import com.sbdor.onlinestoreclaude.features.cart.models.CartItem

data class Order(
    val id: Int,
    val items: List<CartItem>,
    val totalPrice: Double,
    val status: OrderStatus,
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    DELIVERED,
}
