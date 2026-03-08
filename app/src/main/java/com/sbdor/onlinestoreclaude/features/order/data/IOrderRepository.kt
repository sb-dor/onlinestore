package com.sbdor.onlinestoreclaude.features.order.data

import com.sbdor.onlinestoreclaude.features.cart.models.CartItem
import com.sbdor.onlinestoreclaude.features.order.models.Order

interface IOrderRepository {
    suspend fun placeOrder(items: List<CartItem>): Order
}
