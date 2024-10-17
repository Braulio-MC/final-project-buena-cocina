package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.bmc.buenacocina.domain.repository.OrderLineRepository
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.domain.usecase.CreateGetStreamChannel
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderIntent
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderUiResultState
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailedOrderViewModel.DetailedOrderViewModelFactory::class)
class DetailedOrderViewModel @AssistedInject constructor(
    private val createGetStreamChannel: CreateGetStreamChannel,
    orderRepository: OrderRepository,
    orderLineRepository: OrderLineRepository,
    @Assisted private val orderId: String
) : ViewModel() {
    private val _order = orderRepository.get(orderId)
    private val _orderLines = orderLineRepository.get(orderId)
    private val _resultState = MutableStateFlow(DetailedOrderUiResultState())
    private val _events = Channel<DetailedOrderEvent>()
    val events = _events.receiveAsFlow()
    val resultState = _resultState.asStateFlow()
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

    fun onIntent(intent: DetailedOrderIntent) {
        when (intent) {
            DetailedOrderIntent.CreateChannel -> {
                createChannel()
            }
        }
    }

    private fun createChannel() {
        uiState.value.order?.let { order ->
            _resultState.update { currentState ->
                currentState.copy(isWaitingForChannelResult = true)
            }
            viewModelScope.launch {
                val result = createGetStreamChannel(
                    orderId = order.id,
                    storeOwnerId = order.store.ownerId,
                    storeName = order.store.name,
                    userId = order.user.id,
                    userName = order.user.name
                )
                when (result) {
                    is Result.Error -> {
                        processCreateChannelFailed(result.error)
                    }

                    is Result.Success -> {
                        processCreateChannelSuccess(result.data)
                    }
                }
            }
        }
    }

    private fun processCreateChannelSuccess(channelId: String) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForChannelResult = false)
            }
            _events.send(DetailedOrderEvent.CreateChannelSuccess(channelId))
        }
    }

    private fun processCreateChannelFailed(e: DataError) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForChannelResult = false)
            }
            _events.send(DetailedOrderEvent.CreateChannelFailed(e))
        }
    }

    @AssistedFactory
    interface DetailedOrderViewModelFactory {
        fun create(orderId: String): DetailedOrderViewModel
    }

    sealed class DetailedOrderEvent {
        data class CreateChannelSuccess(val channelId: String) : DetailedOrderEvent()
        data class CreateChannelFailed(val error: DataError) : DetailedOrderEvent()
    }
}