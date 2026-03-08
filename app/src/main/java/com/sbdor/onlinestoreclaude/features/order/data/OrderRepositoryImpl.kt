package com.sbdor.onlinestoreclaude.features.order.data

import com.sbdor.onlinestoreclaude.features.cart.models.CartItem
import com.sbdor.onlinestoreclaude.features.order.models.Order
import com.sbdor.onlinestoreclaude.features.order.models.OrderStatus
import kotlinx.coroutines.delay
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor() : IOrderRepository {

    private var orderIdCounter = 1

    override suspend fun placeOrder(items: List<CartItem>): Order {
        // Simulate a network request delay
        delay(1500)
        val total = items.sumOf { it.totalPrice }
        return Order(
            id = orderIdCounter++,
            items = items,
            totalPrice = total,
            status = OrderStatus.CONFIRMED,
        )
    }
}
