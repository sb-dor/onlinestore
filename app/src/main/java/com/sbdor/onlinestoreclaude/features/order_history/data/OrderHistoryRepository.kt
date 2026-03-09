package com.sbdor.onlinestoreclaude.features.order_history.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sbdor.onlinestoreclaude.core.SharedPreferencesManager
import com.sbdor.onlinestoreclaude.features.order.models.Order
import javax.inject.Inject

interface IOrderHistoryRepository {
    fun getOrderHistory(): List<Order>

}


class OrderHistoryImpl @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
) : IOrderHistoryRepository {

    companion object {
        // The key under which the full order history JSON is stored.
        // Using a constant prevents typos — same as defining key names in Dart's
        // SharedPreferences wrapper class.
        private const val KEY_ORDER_HISTORY = "order_history"
    }

    // Gson converts Kotlin objects <-> JSON strings.
    // Equivalent to jsonEncode() / jsonDecode() in Dart.
    private val gson = Gson()

    override fun getOrderHistory(): List<Order> {
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