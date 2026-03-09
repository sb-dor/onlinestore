package com.sbdor.onlinestoreclaude.features.order_history.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sbdor.onlinestoreclaude.features.order_history.data.IOrderHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderHistoryViewModel(
    private val orderHistoryRepository: IOrderHistoryRepository,
) : ViewModel() {

    companion object {
        fun factory(orderHistoryRepository: IOrderHistoryRepository) = viewModelFactory {
            initializer { OrderHistoryViewModel(orderHistoryRepository) }
        }
    }

    private val _state = MutableStateFlow<OrderHistoryState>(OrderHistoryState.Initial)
    val state: StateFlow<OrderHistoryState> = _state.asStateFlow()

    fun load() {
        _state.value = OrderHistoryState.InProgress

        viewModelScope.launch {
            val orderHistory = orderHistoryRepository.getOrderHistory()
            _state.value = OrderHistoryState.Completed(orderHistory)
        }
    }
}
