package com.sbdor.onlinestoreclaude.features.order.controller

import com.sbdor.onlinestoreclaude.features.order.models.Order

sealed class OrderState {
    object Initial : OrderState()
    object Loading : OrderState()
    data class Error(val message: String) : OrderState()
    data class Completed(val order: Order) : OrderState()
}
