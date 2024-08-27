package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.repository.OrderLineRepository
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = DetailedOrderViewModel.DetailedOrderViewModelFactory::class)
class DetailedOrderViewModel @AssistedInject constructor(
    orderRepository: OrderRepository,
    orderLineRepository: OrderLineRepository,
    @Assisted private val orderId: String
) : ViewModel() {
    private val _order = orderRepository.get(orderId)
    private val _orderLines = orderLineRepository.get(orderId)
    val uiState: StateFlow<DetailedOrderUiState> = combine(
        _order,
        _orderLines
    ) { order, orderLines ->
        DetailedOrderUiState(
            order = order,
            lines = orderLines
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
        initialValue = DetailedOrderUiState(isLoading = true)
    )

    @AssistedFactory
    interface DetailedOrderViewModelFactory {
        fun create(orderId: String): DetailedOrderViewModel
    }
}