package com.sbdor.onlinestoreclaude.features.order_history.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.features.order.controller.OrderViewModel
import com.sbdor.onlinestoreclaude.features.order_history.data.IOrderHistoryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@AssistedFactory
interface OrderHistoryControllerFactory {
    fun create(orderHistoryRepository: IOrderHistoryRepository): OrderHistoryViewModel
}

@HiltViewModel(assistedFactory = OrderHistoryControllerFactory::class)
class OrderHistoryViewModel @AssistedInject constructor(
    @Assisted private val orderHistoryRepository: IOrderHistoryRepository,
) : ViewModel(){

    private val _state = MutableStateFlow<OrderHistoryState>(OrderHistoryState.Initial)
    val state : StateFlow<OrderHistoryState> = _state.asStateFlow()


    fun load() {
        _state.value = OrderHistoryState.InProgress

        viewModelScope.launch {
            val orderHistory = orderHistoryRepository.getOrderHistory()
            _state.value = OrderHistoryState.Completed(orderHistory)
        }
    }
}