package com.sbdor.onlinestoreclaude.features.order_history_deletion.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.core.viewModelFactory
import com.sbdor.onlinestoreclaude.features.order_history_deletion.data.IOrderHistoryDeletionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderHistoryDeletionViewModel(
    private val orderHistoryDeletionRepo: IOrderHistoryDeletionRepository,
) : ViewModel() {

    companion object {
        fun factory(repo: IOrderHistoryDeletionRepository) = viewModelFactory {
            OrderHistoryDeletionViewModel(repo)
        }
    }

    private val _state = MutableStateFlow<OrderHistoryDeletionState>(OrderHistoryDeletionState.Initial)
    val state: StateFlow<OrderHistoryDeletionState> = _state.asStateFlow()

    fun delete(orderId: Int) {
        viewModelScope.launch {
            _state.value = OrderHistoryDeletionState.InProgress
            val success = orderHistoryDeletionRepo.delete(orderId)
            _state.value = if (success) {
                OrderHistoryDeletionState.Completed
            } else {
                OrderHistoryDeletionState.Error("Order #$orderId not found")
            }

            /// so after deletion set Initial
            /// if there is any unexpected behaviour try to set _state.value = Initial
            delay(100)
            _state.value = OrderHistoryDeletionState.Initial
        }
    }
}