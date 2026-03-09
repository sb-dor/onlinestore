package com.sbdor.onlinestoreclaude.features.order_history.controller

import com.sbdor.onlinestoreclaude.features.order.models.Order

sealed class OrderHistoryState {
    object Initial : OrderHistoryState()
    object InProgress : OrderHistoryState()
    data class Error(val error: String?) : OrderHistoryState()
    data class Completed(val orderHistory: List<Order>) : OrderHistoryState()
}