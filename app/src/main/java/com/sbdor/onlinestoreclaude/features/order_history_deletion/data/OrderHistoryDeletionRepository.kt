package com.sbdor.onlinestoreclaude.features.order_history_deletion.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sbdor.onlinestoreclaude.core.SharedPreferencesManager
import com.sbdor.onlinestoreclaude.features.order.models.Order
import kotlinx.coroutines.delay


interface IOrderHistoryDeletionRepository {
    suspend fun delete(orderId: Int): Boolean
}

class OrderHistoryDeletionRepository(private val sharedPreferencesManager: SharedPreferencesManager) :
    IOrderHistoryDeletionRepository {

    companion object {
        private const val KEY_ORDER_HISTORY = "order_history"
    }

    private val json = Gson()

    override suspend fun delete(orderId: Int): Boolean {
        val orderHistoryString = sharedPreferencesManager.getString(KEY_ORDER_HISTORY)
        if (orderHistoryString.isEmpty()) return false

        // TypeToken tells Gson the exact generic type — required for List<Order>
        val type = object : TypeToken<List<Order>>() {}.type
        val convertedList = json.fromJson<List<Order>>(orderHistoryString, type).toMutableList()

        // filter by Order.id, not product.id — Order is the top-level object here
        val removed = convertedList.removeAll { it.id == orderId }
        if (!removed) return false

        // write the updated list back to SharedPreferences
        sharedPreferencesManager.putString(KEY_ORDER_HISTORY, json.toJson(convertedList))
        delay(2000)
        return true
    }
}