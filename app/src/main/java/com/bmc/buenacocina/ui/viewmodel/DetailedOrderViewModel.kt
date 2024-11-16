package com.bmc.buenacocina.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.LOCATION_RETRIEVE_INTERVAL_IN_MILLIS
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.data.network.service.LocationService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.bmc.buenacocina.domain.isGpsOrNetworkEnabledFlow
import com.bmc.buenacocina.domain.mapper.asLatLng
import com.bmc.buenacocina.domain.model.OrderLineDomain
import com.bmc.buenacocina.domain.repository.OrderLineRepository
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.domain.repository.RemoteConfigRepository
import com.bmc.buenacocina.domain.usecase.CreateGetStreamChannel
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderIntent
import com.bmc.buenacocina.ui.screen.detailed.order.DetailedOrderUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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
    @ApplicationContext private val context: Context,
    private val createGetStreamChannel: CreateGetStreamChannel,
    private val orderRepository: OrderRepository,
    private val orderLineRepository: OrderLineRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val locationService: LocationService,
    @Assisted private val orderId: String
) : ViewModel() {
    private var _locationJob: Job? = null
    private val _visiblePermissionDialogQueue = MutableStateFlow<List<String>>(emptyList())
    val visiblePermissionDialogQueue: StateFlow<List<String>> = _visiblePermissionDialogQueue
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
            remoteConfigRepository.cuceiCenterOnMap
                .onEach { pair ->
                    _uiState.update { currentState ->
                        currentState.copy(cuceiCenterOnMap = pair)
                    }
                }
                .launchIn(viewModelScope)
            remoteConfigRepository.cuceiAreaBoundsOnMap
                .onEach { pairList ->
                    _uiState.update { currentState ->
                        currentState.copy(cuceiAreaBoundsOnMap = pairList)
                    }
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
        _uiState.update { currentState ->
            currentState.copy(isCalculatingOrderTotal = true)
        }
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
                currentState.copy(
                    isCalculatingOrderTotal = false,
                    orderTotal = total
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isCalculatingOrderTotal = false,
                    orderTotal = BigDecimal.ZERO
                )
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

    fun dismissPermissionDialog() {
        if (_visiblePermissionDialogQueue.value.isNotEmpty()) {
            _visiblePermissionDialogQueue.update { currentState ->
                currentState.drop(1)
            }
        }
    }

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted && !_visiblePermissionDialogQueue.value.contains(permission)) {
            _visiblePermissionDialogQueue.update { currentState ->
                currentState + permission
            }
        }
    }

    fun startLocationUpdates() {
        if (_locationJob == null || _locationJob?.isActive == false) {
            _locationJob = viewModelScope.launch {
                combine(
                    locationService.getLocationUpdates(LOCATION_RETRIEVE_INTERVAL_IN_MILLIS)
                        .filterNotNull()
                        .map { location -> location.asLatLng() }
                        .catch { e -> e.printStackTrace() },
                    context.isGpsOrNetworkEnabledFlow()
                        .distinctUntilChanged()
                ) { location, isLocationEnabled ->
                    if (isLocationEnabled) location else null
                }.onStart {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingUserLocation = true)
                    }
                }.onEach { location ->
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingUserLocation = false, userLocation = location)
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    fun stopLocationUpdates() {
        _locationJob?.cancel()
        _locationJob = null
        _uiState.update { currentState ->
            currentState.copy(userLocation = null)
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