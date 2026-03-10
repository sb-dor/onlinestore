package com.sbdor.onlinestoreclaude.features.order_history_deletion.controller

sealed  class OrderHistoryDeletionState {
    object Initial : OrderHistoryDeletionState()
    object InProgress : OrderHistoryDeletionState()
    data class Error(val message: String?) : OrderHistoryDeletionState()
    object Completed: OrderHistoryDeletionState()
}