package com.sbdor.onlinestoreclaude.features.order.data

import com.sbdor.onlinestoreclaude.features.cart.models.CartItem
import com.sbdor.onlinestoreclaude.features.order.models.Order
import com.sbdor.onlinestoreclaude.features.order.models.OrderStatus
import kotlinx.coroutines.delay
import javax.inject.Inject

// ---------------------------------------------------------------------------
// FIRST IMPLEMENTATION — simulates a remote API / network call
// ---------------------------------------------------------------------------
// This is the "real" implementation that would talk to a backend server.
// delay(4000) simulates network latency — in a real app this would be a Retrofit/Ktor call.
// orderIdCounter increments on each order, simulating a server-generated ID.
//
// Used when the screen wants to simulate a real checkout with network delay.
// Passed to OrderViewModel via @AssistedInject: factory.create(OrderRepositoryImpl())
// ---------------------------------------------------------------------------
class OrderRepositoryImpl @Inject constructor(): IOrderRepository {

    private var orderIdCounter = 1

    override suspend fun placeOrder(items: List<CartItem>): Order {
        // Simulate a network request delay
        delay(4000)
        val total = items.sumOf { it.totalPrice }
        return Order(
            id = orderIdCounter++,
            items = items,
            totalPrice = total,
            status = OrderStatus.CONFIRMED,
        )
    }
}

// ---------------------------------------------------------------------------
// SECOND IMPLEMENTATION — simulates local storage / offline order saving
// ---------------------------------------------------------------------------
// No delay — returns instantly, as if saving to a local database (e.g. Room).
// Always returns id = 100 (hardcoded), simulating a locally generated ID.
//
// Used when the screen wants instant order confirmation without network.
// Passed to OrderViewModel via @AssistedInject: factory.create(OrderLocalRepositoryImpl())
//
// NOTICE: both classes have @Inject constructor — this lets Hilt construct them
// when needed (e.g. when the screen calls factory.create(OrderLocalRepositoryImpl())).
// However, neither is bound in AppModule via @Binds because @AssistedInject
// does not require AppModule bindings for the @Assisted parameter.
// ---------------------------------------------------------------------------
class OrderLocalRepositoryImpl @Inject constructor(): IOrderRepository {
    override suspend fun placeOrder(items: List<CartItem>): Order {
        val total = items.sumOf { it.totalPrice }
        return Order(id = 100, items = items, totalPrice = total, status = OrderStatus.CONFIRMED)
    }
}