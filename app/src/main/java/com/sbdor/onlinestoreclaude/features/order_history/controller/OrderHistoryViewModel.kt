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

// ---------------------------------------------------------------------------
// OrderHistoryViewModel — demonstrates OPTION A for manual DI (no companion factory)
// ---------------------------------------------------------------------------
// Same pattern as CartViewModel: IOrderHistoryRepository is injected via constructor.
// The screen creates the ViewModel with the viewModelFactory{} helper:
//
//   val viewModel: OrderHistoryViewModel = viewModel(
//       factory = viewModelFactory { OrderHistoryViewModel(container.orderHistoryRepository) }
//   )
//
// HILT EQUIVALENT (before migration, this used @AssistedInject):
//   @HiltViewModel(assistedFactory = OrderHistoryControllerFactory::class)
//   class OrderHistoryViewModel @AssistedInject constructor(
//       @Assisted private val orderHistoryRepository: IOrderHistoryRepository,
//   ) : ViewModel()
//
//   @AssistedFactory
//   interface OrderHistoryControllerFactory {
//       fun create(repo: IOrderHistoryRepository): OrderHistoryViewModel
//   }
//
//   Screen used: hiltViewModel<OrderHistoryViewModel, OrderHistoryControllerFactory> { factory ->
//       factory.create(EntryPointAccessors.fromApplication(...).orderHistoryRepository())
//   }
//
// With manual DI: 4 lines in the screen replaces all that boilerplate.
// ---------------------------------------------------------------------------
class OrderHistoryViewModel(
    private val orderHistoryRepository: IOrderHistoryRepository,
) : ViewModel() {

    // OPTION B — companion object factory (alternative to inline viewModelFactory in screen).
    //
    // companion object {
    //     fun factory(orderHistoryRepository: IOrderHistoryRepository) = viewModelFactory {
    //         initializer { OrderHistoryViewModel(orderHistoryRepository) }
    //     }
    // }
    //
    // Screen would then use:
    //   val viewModel: OrderHistoryViewModel = viewModel(
    //       factory = OrderHistoryViewModel.factory(container.orderHistoryRepository)
    //   )

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
