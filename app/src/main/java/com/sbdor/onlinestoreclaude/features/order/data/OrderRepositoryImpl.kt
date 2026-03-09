package com.sbdor.onlinestoreclaude.features.order.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sbdor.onlinestoreclaude.core.SharedPreferencesManager
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
// SharedPreferencesManager is injected to persist order history across app restarts.
// Gson converts Order (complex object) to a JSON String — the only way to store
// objects in SharedPreferences, which only supports primitive types and Strings.
//
// Used when the screen wants to simulate a real checkout with network delay.
// Passed to OrderViewModel via @AssistedInject: factory.create(OrderRepositoryImpl())
// ---------------------------------------------------------------------------
class OrderRepositoryImpl @Inject constructor(
    // Hilt injects SharedPreferencesManager automatically — it is @Singleton so
    // the same instance is shared across the whole app.
    private val sharedPreferencesManager: SharedPreferencesManager,
) : IOrderRepository {

    companion object {
        private const val KEY_ORDER_HISTORY = "order_history"
        private const val KEY_ORDER_ID_COUNTER = "order_id_counter"
    }

    private val gson = Gson()

    private fun nextOrderId(): Int {
        val current = sharedPreferencesManager.getInt(KEY_ORDER_ID_COUNTER, 0)
        val next = current + 1
        sharedPreferencesManager.putInt(KEY_ORDER_ID_COUNTER, next)
        return next
    }

    override suspend fun placeOrder(items: List<CartItem>): Order {
        // Simulate a network request delay
        delay(4000)
        val total = items.sumOf { it.totalPrice }
        val order = Order(
            id = nextOrderId(),
            items = items,
            totalPrice = total,
            status = OrderStatus.CONFIRMED,
        )

        // Save the new order to history after placing it.
        saveOrderToHistory(order)

        return order
    }


    // Private helper — reads current history, appends the new order, saves back.
    // This is the "read-modify-write" pattern for SharedPreferences list storage.
    private fun saveOrderToHistory(order: Order) {
        val currentHistory =
            LocalOrderHistory().getOrderHistory(sharedPreferencesManager)
                .toMutableList()
        currentHistory.add(0, order) // add at the top so newest appears first

        // Convert the updated list to a JSON string and store it.
        // Dart equivalent: await prefs.setString('key', jsonEncode(list));
        val json = gson.toJson(currentHistory)
        sharedPreferencesManager.putString(KEY_ORDER_HISTORY, json)
    }
}

// ---------------------------------------------------------------------------
// SECOND IMPLEMENTATION — simulates local storage / offline order saving
// ---------------------------------------------------------------------------
// No delay — returns instantly, as if saving to a local database (e.g. Room).
// Also persists order history via SharedPreferencesManager, same as the remote impl.
//
// Used when the screen wants instant order confirmation without network.
// Passed to OrderViewModel via @AssistedInject: factory.create(OrderLocalRepositoryImpl())
//
// NOTICE: both classes have @Inject constructor — this lets Hilt construct them
// when needed (e.g. when the screen calls factory.create(OrderLocalRepositoryImpl())).
// However, neither is bound in AppModule via @Binds because @AssistedInject
// does not require AppModule bindings for the @Assisted parameter.
// ---------------------------------------------------------------------------
class OrderLocalRepositoryImpl @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
) : IOrderRepository {

    companion object {
        private const val KEY_ORDER_HISTORY = "order_history"
        private const val KEY_ORDER_ID_COUNTER = "order_id_counter"
    }

    private val gson = Gson()

    private fun nextOrderId(): Int {
        val current = sharedPreferencesManager.getInt(KEY_ORDER_ID_COUNTER, 0)
        val next = current + 1
        sharedPreferencesManager.putInt(KEY_ORDER_ID_COUNTER, next)
        return next
    }

    override suspend fun placeOrder(items: List<CartItem>): Order {
        val total = items.sumOf { it.totalPrice }
        val order =
            Order(
                id = nextOrderId(),
                items = items,
                totalPrice = total,
                status = OrderStatus.CONFIRMED
            )
        saveOrderToHistory(order)
        return order
    }


    private fun saveOrderToHistory(order: Order) {
        val currentHistory =
            LocalOrderHistory().getOrderHistory(sharedPreferencesManager).toMutableList()
        currentHistory.add(0, order)
        sharedPreferencesManager.putString(KEY_ORDER_HISTORY, gson.toJson(currentHistory))
    }
}

private class LocalOrderHistory {
    companion object {
        // The key under which the full order history JSON is stored.
        // Using a constant prevents typos — same as defining key names in Dart's
        // SharedPreferences wrapper class.
        private const val KEY_ORDER_HISTORY = "order_history"
    }

    private val gson = Gson()

    fun getOrderHistory(sharedPreferencesManager: SharedPreferencesManager): List<Order> {
        // Read the stored JSON string. Returns "" if nothing saved yet.
        val json = sharedPreferencesManager.getString(KEY_ORDER_HISTORY)

        // If no history saved yet, return an empty list.
        if (json.isEmpty()) return emptyList()

        // TypeToken tells Gson the exact generic type to deserialize into.
        // This is required because Kotlin/Java erases generic types at runtime —
        // Gson needs a hint to know it should produce List<Order> not just List<Any>.
        //
        // Dart equivalent:
        // final list = jsonDecode(json) as List;
        // return list.map((e) => Order.fromJson(e)).toList();
        val type = object : TypeToken<List<Order>>() {}.type
        return gson.fromJson(json, type)
    }
}