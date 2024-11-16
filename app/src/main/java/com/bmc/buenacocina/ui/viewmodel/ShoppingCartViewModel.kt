package com.bmc.buenacocina.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bmc.buenacocina.core.LOCATION_RETRIEVE_INTERVAL_IN_MILLIS
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.data.network.service.LocationService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.isGpsOrNetworkEnabledFlow
import com.bmc.buenacocina.domain.mapper.asFormErrorUiText
import com.bmc.buenacocina.domain.mapper.asLatLng
import com.bmc.buenacocina.domain.model.ShoppingCartDomain
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import com.bmc.buenacocina.domain.repository.ConnectivityRepository
import com.bmc.buenacocina.domain.repository.PaymentMethodRepository
import com.bmc.buenacocina.domain.repository.RemoteConfigRepository
import com.bmc.buenacocina.domain.repository.ShoppingCartItemRepository
import com.bmc.buenacocina.domain.repository.ShoppingCartRepository
import com.bmc.buenacocina.domain.repository.UserRepository
import com.bmc.buenacocina.domain.usecase.CreateOrder
import com.bmc.buenacocina.domain.usecase.SendNewOrderNotificationToSpecificUserDevices
import com.bmc.buenacocina.domain.usecase.UpdateShoppingCartItemCount
import com.bmc.buenacocina.domain.usecase.ValidateShoppingCart
import com.bmc.buenacocina.domain.usecase.ValidateShoppingCartItems
import com.bmc.buenacocina.domain.usecase.ValidateShoppingCartLocation
import com.bmc.buenacocina.domain.usecase.ValidateShoppingCartPaymentMethod
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartIntent
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartUiState
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class ShoppingCartViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val validateCart: ValidateShoppingCart,
    private val validateItems: ValidateShoppingCartItems,
    private val validateLocation: ValidateShoppingCartLocation,
    private val validatePaymentMethod: ValidateShoppingCartPaymentMethod,
    private val createOrder: CreateOrder,
    private val sendNewOrderNotificationToSpecificUserDevices: SendNewOrderNotificationToSpecificUserDevices,
    private val updateShoppingCartItemCount: UpdateShoppingCartItemCount,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingCartItemRepository: ShoppingCartItemRepository,
    private val userRepository: UserRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val locationService: LocationService,
    paymentMethodRepository: PaymentMethodRepository,
    connectivityRepository: ConnectivityRepository
) : ViewModel() {
    private var _locationJob: Job? = null
    private val _visiblePermissionDialogQueue = MutableStateFlow<List<String>>(emptyList())
    val visiblePermissionDialogQueue: StateFlow<List<String>> = _visiblePermissionDialogQueue
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _getShoppingCart: Flow<ShoppingCartDomain?> = flow {
        emit(userRepository.getUserId())
    }.flatMapLatest { result ->
        when (result) {
            is Result.Error -> flowOf(null)

            is Result.Success -> {
                val qCart: (Query) -> Query = { query ->
                    query.whereEqualTo("userId", result.data)
                }
                shoppingCartRepository.get(qCart)
                    .map { carts -> carts.firstOrNull() }
            }
        }
    }
    private val _uiState = MutableStateFlow(ShoppingCartUiState())
    val uiState: StateFlow<ShoppingCartUiState> = _uiState
        .onStart {
            _getShoppingCart
                .onStart {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingShoppingCart = true)
                    }
                }
                .onEach { cart ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoadingShoppingCart = false,
                            shoppingCart = cart
                        )
                    }
                    cart?.let {
                        shoppingCartItemRepository.get(cart.id)
                            .onStart {
                                _uiState.update { currentState ->
                                    currentState.copy(isLoadingShoppingCartItems = true)
                                }
                            }
                            .onEach { cartItems ->
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        isLoadingShoppingCartItems = false,
                                        shoppingCartItems = cartItems
                                    )
                                }
                                calculate(cartItems)
                            }
                            .launchIn(viewModelScope)
                    }
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
            initialValue = ShoppingCartUiState()
        )
    private val _events = Channel<ShoppingCartViewModelEvent>()
    val events = _events.receiveAsFlow()
    val paymentMethods = paymentMethodRepository
        .paging()
        .cachedIn(viewModelScope)
    val netState = connectivityRepository.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = NetworkStatus.Unknown
        )

    fun onIntent(intent: ShoppingCartIntent) {
        when (intent) {
            is ShoppingCartIntent.UpdateCurrentDeliveryLocation -> {
                _uiState.update { currentState ->
                    currentState.copy(currentDeliveryLocation = intent.deliveryLocation)
                }
            }

            is ShoppingCartIntent.UpdateCurrentPaymentMethod -> {
                _uiState.update { currentState ->
                    currentState.copy(currentPaymentMethod = intent.paymentMethod)
                }
            }

            is ShoppingCartIntent.Order -> {
                order()
            }

            is ShoppingCartIntent.DecreaseShoppingCartItemQuantity -> {
                updateItemCount(intent.itemId, intent.count)
            }

            is ShoppingCartIntent.IncreaseShoppingCartItemQuantity -> {
                updateItemCount(intent.itemId, intent.count)
            }
        }
    }

    private fun updateItemCount(itemId: String, count: BigInteger) {
        _uiState.value.shoppingCart?.let { shoppingCart ->
            viewModelScope.launch {
                updateShoppingCartItemCount(
                    shoppingCart.id,
                    itemId,
                    count,
                    onSuccess = { },
                    onFailure = { e -> }
                )
            }
        }
    }

    private fun calculate(items: List<ShoppingCartItemDomain>) {
        if (items.isNotEmpty()) {
            val subtotal =
                items.sumOf { item ->
                    val discount =
                        (item.product.price * (item.product.discount.percentage / BigDecimal.valueOf(
                            100
                        ))) * item.quantity.toBigDecimal()
                    item.product.price.times(item.quantity.toBigDecimal()).minus(discount)
                }.setScale(2, RoundingMode.HALF_DOWN)
            val service = BigDecimal.ZERO
            val total = (subtotal + service).setScale(2, RoundingMode.HALF_DOWN)
            _uiState.update { currentState ->
                currentState.copy(
                    total = ShoppingCartUiState.CartTotalUiState(
                        subTotal = subtotal,
                        service = service,
                        total = total
                    )
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    total = ShoppingCartUiState.CartTotalUiState()
                )
            }
        }
    }

    private fun order() {
        val shoppingCartResult = validateCart(_uiState.value.shoppingCart)
        val itemsResult = validateItems(_uiState.value.shoppingCartItems)
        val locationResult = validateLocation(_uiState.value.currentDeliveryLocation)
        val paymentMethodResult = validatePaymentMethod(_uiState.value.currentPaymentMethod)

        _uiState.update { currentState ->
            currentState.copy(
                shoppingCartError = (shoppingCartResult as? Result.Error)?.asFormErrorUiText(),
                shoppingCartItemsError = (itemsResult as? Result.Error)?.asFormErrorUiText(),
                currentDeliveryLocationError = (locationResult as? Result.Error)?.asFormErrorUiText(),
                currentPaymentMethodError = (paymentMethodResult as? Result.Error)?.asFormErrorUiText()
            )
        }

        val hasErrors = listOf(
            shoppingCartResult,
            itemsResult,
            locationResult,
            paymentMethodResult
        ).any { it is Result.Error }

        if (hasErrors) {
            return
        }

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isWaitingForOrderResult = true)
            }
            when (val result = userRepository.getUserProfile()) {
                is Result.Error -> {

                }

                is Result.Success -> {
                    if (result.data.getId() != null && result.data.name != null) {
                        try {
                            val userId = result.data.getId()!!
                            val userName = result.data.name!!
                            val deliveryLocationLat =
                                _uiState.value.currentDeliveryLocation!!.latitude
                            val deliveryLocationLng =
                                _uiState.value.currentDeliveryLocation!!.longitude
                            val storeId = _uiState.value.shoppingCart!!.store.id
                            val storeOwnerId = _uiState.value.shoppingCart!!.store.ownerId
                            val storeName = _uiState.value.shoppingCart!!.store.name
                            val paymentMethodId = _uiState.value.currentPaymentMethod!!.id
                            val paymentMethodName = _uiState.value.currentPaymentMethod!!.name
                            val cartId = _uiState.value.shoppingCart!!.id
                            val cartItems = _uiState.value.shoppingCartItems
                            val itemCount = _uiState.value.shoppingCartItems.size
                            createOrder(
                                userId = userId,
                                userName = userName,
                                deliveryLocationLatitude = deliveryLocationLat,
                                deliveryLocationLongitude = deliveryLocationLng,
                                storeId = storeId,
                                storeOwnerId = storeOwnerId,
                                storeName = storeName,
                                paymentMethodId = paymentMethodId,
                                paymentMethodName = paymentMethodName,
                                shoppingCartId = cartId,
                                items = cartItems,
                                onSuccess = {
                                    sendNewOrderNotificationToSpecificUserDevices(
                                        storeId = storeId,
                                        storeName = storeName,
                                        userName = userName,
                                        itemCount = itemCount,
                                        onSuccess = {
                                            processOrderSuccess()
                                        },
                                        onFailure = { e ->
                                            processOrderFailed(e)
                                        }
                                    )
                                },
                                onFailure = { e ->
                                    processOrderFailed(e)
                                }
                            )
                        } catch (e: Exception) {
                            processOrderFailed(e)
                        }
                    } else {
                        processOrderFailed(Exception("Failed to get user id or username")) // Custom exception here
                    }
                }
            }
        }
    }

    private fun processOrderSuccess() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isWaitingForOrderResult = false,
                    currentDeliveryLocation = null,
                    currentPaymentMethod = null
                )
            }
            _events.send(ShoppingCartViewModelEvent.OrderSuccess)
        }
    }

    private fun processOrderFailed(e: Exception) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isWaitingForOrderResult = false)
            }
            _events.send(ShoppingCartViewModelEvent.OrderFailed(e))
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
                        .distinctUntilChanged()
                        .filterNotNull()
                        .map { location -> location.asLatLng() }
                        .catch { e -> e.printStackTrace() },
                    context.isGpsOrNetworkEnabledFlow()
                        .distinctUntilChanged()
                ) { location, isLocationEnabled ->
                    if (isLocationEnabled) location else null
                }.onEach { location ->
                    _uiState.update { currentState ->
                        currentState.copy(userLocation = location)
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

    sealed class ShoppingCartViewModelEvent {
        data object OrderSuccess : ShoppingCartViewModelEvent()
        data class OrderFailed(val error: Exception) : ShoppingCartViewModelEvent()
    }
}