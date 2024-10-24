package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.mapper.asFormErrorUiText
import com.bmc.buenacocina.domain.model.LocationDomain
import com.bmc.buenacocina.domain.model.ShoppingCartDomain
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import com.bmc.buenacocina.domain.repository.ConnectivityRepository
import com.bmc.buenacocina.domain.repository.LocationRepository
import com.bmc.buenacocina.domain.repository.PaymentMethodRepository
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
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartCartUiState
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartIntent
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartUiState
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
    private val locationRepository: LocationRepository,
    paymentMethodRepository: PaymentMethodRepository,
    connectivityRepository: ConnectivityRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingCartUiState())
    val uiState: StateFlow<ShoppingCartUiState> = _uiState.asStateFlow()
    private val _events = Channel<ShoppingCartViewModelEvent>()
    val events = _events.receiveAsFlow()
    val netState = connectivityRepository.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = NetworkStatus.Unknown
        )
    private val _qPaymentMethods: (Query) -> Query = { query ->
        query.whereNotEqualTo("name", "")
    }
    val paymentMethods = paymentMethodRepository
        .paging(_qPaymentMethods)
        .cachedIn(viewModelScope)
    val shoppingCartState: StateFlow<ShoppingCartCartUiState> = combine(
        getShoppingCart(),
        getShoppingCartItems()
    ) { shoppingCart, shoppingCartItems ->
        calculate(shoppingCartItems)
        ShoppingCartCartUiState(
            shoppingCart = shoppingCart.firstOrNull(),
            shoppingCartItems = shoppingCartItems,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
        initialValue = ShoppingCartCartUiState(isLoading = true)
    )

    fun locations(): Flow<PagingData<LocationDomain>> = flow {
        shoppingCartState.value.shoppingCart?.let { shoppingCart ->
            val qLocation: (Query) -> Query = { query ->
                query.whereEqualTo("storeId", shoppingCart.store.id)
            }
            emitAll(locationRepository.paging(qLocation))
        }
    }.cachedIn(viewModelScope)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getShoppingCart(): Flow<List<ShoppingCartDomain>> = flow {
        emit(userRepository.getUserId())
    }.flatMapLatest { result ->
        when (result) {
            is Result.Error -> {
                flowOf(emptyList())
            }

            is Result.Success -> {
                val qCart: (Query) -> Query = { query ->
                    query.whereEqualTo("userId", result.data)
                }
                shoppingCartRepository.get(qCart)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getShoppingCartItems(): Flow<List<ShoppingCartItemDomain>> = flow {
        emit(getShoppingCart())
    }.flatMapLatest { result ->
        val cart = result.firstOrNull()?.firstOrNull()
        if (cart != null) {
            shoppingCartItemRepository.get(cart.id)
        } else {
            flowOf(emptyList())
        }
    }

    private fun updateItemCount(itemId: String, count: BigInteger) {
        shoppingCartState.value.shoppingCart?.let { shoppingCart ->
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
        val shoppingCartResult = validateCart(shoppingCartState.value.shoppingCart)
        val itemsResult = validateItems(shoppingCartState.value.shoppingCartItems)
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
                currentState.copy(isWaitingForResult = true)
            }
            when (val result = userRepository.getUserProfile()) {
                is Result.Error -> {

                }

                is Result.Success -> {
                    if (result.data.getId() != null && result.data.name != null) {
                        try {
                            val userId = result.data.getId()!!
                            val userName = result.data.name!!
                            val deliveryLocationId = _uiState.value.currentDeliveryLocation!!.id
                            val deliveryLocationName = _uiState.value.currentDeliveryLocation!!.name
                            val storeId = shoppingCartState.value.shoppingCart!!.store.id
                            val storeOwnerId = shoppingCartState.value.shoppingCart!!.store.ownerId
                            val storeName = shoppingCartState.value.shoppingCart!!.store.name
                            val paymentMethodId = _uiState.value.currentPaymentMethod!!.id
                            val paymentMethodName = _uiState.value.currentPaymentMethod!!.name
                            val cartId = shoppingCartState.value.shoppingCart!!.id
                            val cartItems = shoppingCartState.value.shoppingCartItems
                            val itemCount = shoppingCartState.value.shoppingCartItems.size
                            createOrder(
                                userId = userId,
                                userName = userName,
                                deliveryLocationId = deliveryLocationId,
                                deliveryLocationName = deliveryLocationName,
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
                                        locationName = deliveryLocationName,
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
            _uiState.value = ShoppingCartUiState()
            _uiState.update { currentState ->
                currentState.copy(isWaitingForResult = false)
            }
            _events.send(ShoppingCartViewModelEvent.OrderSuccess)
        }
    }

    private fun processOrderFailed(e: Exception) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isWaitingForResult = false)
            }
            _events.send(ShoppingCartViewModelEvent.OrderFailed(e))
        }
    }

    sealed class ShoppingCartViewModelEvent {
        data object OrderSuccess : ShoppingCartViewModelEvent()
        data class OrderFailed(val error: Exception) : ShoppingCartViewModelEvent()
    }
}