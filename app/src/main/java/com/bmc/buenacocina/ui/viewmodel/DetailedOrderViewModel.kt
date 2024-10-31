package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.bmc.buenacocina.domain.model.OrderLineDomain
import com.bmc.buenacocina.domain.repository.OrderLineRepository
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.domain.usecase.CreateGetStreamChannel
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderIntent
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@HiltViewModel(assistedFactory = DetailedOrderViewModel.DetailedOrderViewModelFactory::class)
class DetailedOrderViewModel @AssistedInject constructor(
    private val createGetStreamChannel: CreateGetStreamChannel,
    private val orderRepository: OrderRepository,
    private val orderLineRepository: OrderLineRepository,
    @Assisted private val orderId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailedOrderUiState())
    val uiState: StateFlow<DetailedOrderUiState> = _uiState
        .onStart {
            orderRepository
                .get(orderId)
                .onStart {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingOrder = true)
                    }
                }
                .onEach { order ->
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingOrder = false, order = order)
                    }
                }
                .launchIn(viewModelScope)
            orderLineRepository
                .get(orderId)
                .onStart {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingOrderLines = true)
                    }
                }
                .onEach { lines ->
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingOrderLines = false, lines = lines)
                    }
                    calculate(lines)
                }
                .launchIn(viewModelScope)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = DetailedOrderUiState()
        )
    private val _events = Channel<DetailedOrderEvent>()
    val events = _events.receiveAsFlow()

    fun onIntent(intent: DetailedOrderIntent) {
        when (intent) {
            DetailedOrderIntent.CreateChannel -> {
                createChannel()
            }
        }
    }

    private fun calculate(items: List<OrderLineDomain>) {
        if (items.isNotEmpty()) {
            val total =
                items.sumOf { item ->
                    val discount =
                        (item.product.price * (item.product.discount.percentage / BigDecimal.valueOf(
                            100
                        ))) * item.quantity.toBigDecimal()
                    item.product.price.times(item.quantity.toBigDecimal()).minus(discount)
                }.setScale(2, RoundingMode.HALF_DOWN)
            _uiState.update { currentState ->
                currentState.copy(orderTotal = total)
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(orderTotal = BigDecimal.ZERO)
            }
        }
    }

    private fun createChannel() {
        _uiState.value.order?.let { order ->
            _uiState.update { currentState ->
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
            _uiState.update { currentState ->
                currentState.copy(isWaitingForChannelResult = false)
            }
            _events.send(DetailedOrderEvent.CreateChannelSuccess(channelId))
        }
    }

    private fun processCreateChannelFailed(e: DataError) {
        viewModelScope.launch {
            _uiState.update { currentState ->
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