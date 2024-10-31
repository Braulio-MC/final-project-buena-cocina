package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.data.network.dto.CreateProductFavoriteDto
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartDto
import com.bmc.buenacocina.data.network.dto.CreateShoppingCartItemDto
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.repository.ConnectivityRepository
import com.bmc.buenacocina.domain.repository.ProductFavoriteRepository
import com.bmc.buenacocina.domain.repository.ProductRepository
import com.bmc.buenacocina.domain.repository.ShoppingCartRepository
import com.bmc.buenacocina.domain.repository.UserRepository
import com.bmc.buenacocina.domain.model.ProductDomain
import com.bmc.buenacocina.domain.model.ProductFavoriteDomain
import com.bmc.buenacocina.ui.screen.detailed.product.DetailedProductIntent
import com.bmc.buenacocina.ui.screen.detailed.product.DetailedProductUiResultState
import com.bmc.buenacocina.ui.screen.detailed.product.DetailedProductUiState
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigInteger

@HiltViewModel(assistedFactory = DetailedProductViewModel.DetailedProductViewModelFactory::class)
class DetailedProductViewModel @AssistedInject constructor(
    productRepository: ProductRepository,
    private val productFavoriteRepository: ProductFavoriteRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val userRepository: UserRepository,
    connectivityRepository: ConnectivityRepository,
    @Assisted("productId") private val productId: String,
    @Assisted("storeOwnerId") private val storeOwnerId: String
) : ViewModel() {
    private val mutex = Mutex()
    private val _resultState = MutableStateFlow(DetailedProductUiResultState())
    private val _product = productRepository.get(productId)
    private val _favorite = getProductFavorite()
    val uiState: StateFlow<DetailedProductUiState> = combine(
        _product,
        _favorite
    ) { product, favorite ->
        DetailedProductUiState(
            product = product,
            favorite = favorite.firstOrNull()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
        initialValue = DetailedProductUiState(isLoading = true)
    )
    val resultState = _resultState.asStateFlow()
    val netState = connectivityRepository.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = NetworkStatus.Unavailable
        )
    private val _events = Channel<DetailedProductEvent>()
    val events = _events.receiveAsFlow()

    fun onIntent(intent: DetailedProductIntent) {
        when (intent) {
            DetailedProductIntent.ToggleFavoriteProduct -> {
                toggleFavoriteProduct()
            }

            is DetailedProductIntent.DecreaseProductCount -> {
                viewModelScope.launch {
                    updateAddToCartCount(intent.count)
                }
            }

            is DetailedProductIntent.IncreaseProductCount -> {
                viewModelScope.launch {
                    updateAddToCartCount(intent.count)
                }
            }

            DetailedProductIntent.AddToShoppingCart -> {
                addToCart()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getProductFavorite(): Flow<List<ProductFavoriteDomain>> = flow {
        emit(userRepository.getUserId())
    }.flatMapLatest { result ->
        when (result) {
            is Result.Error -> {
                flowOf(emptyList()) // TODO: handle error
            }

            is Result.Success -> {
                val q: (Query) -> Query = { query ->
                    query.where(
                        Filter.and(
                            Filter.equalTo("userId", result.data),
                            Filter.equalTo("productId", productId)
                        )
                    )
                }
                productFavoriteRepository.get(q)
            }
        }
    }

    private fun toggleFavoriteProduct() {
        val favorite = uiState.value.favorite
        if (favorite != null) {
            deleteFavoriteProduct()
        } else {
            createFavoriteProduct()
        }
    }

    private fun createFavoriteProduct() {
        _resultState.update { currentState ->
            currentState.copy(isWaitingForFavoriteResult = true)
        }
        viewModelScope.launch {
            uiState.value.product?.let { product ->
                when (val result = userRepository.getUserId()) {
                    is Result.Error -> {

                    }

                    is Result.Success -> {
                        val dto = CreateProductFavoriteDto(
                            userId = result.data,
                            productId = product.id,
                            productStoreOwnerId = storeOwnerId,
                            productName = product.name,
                            productImage = product.image,
                            productDescription = product.description
                        )
                        productFavoriteRepository.create(
                            dto,
                            onSuccess = {
                                processCreateFavoriteSuccess()
                            },
                            onFailure = { e ->
                                processCreateFavoriteFailed(e)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun processCreateFavoriteSuccess() {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedProductEvent.CreateProductFavoriteSuccess)
        }
    }

    private fun processCreateFavoriteFailed(e: Exception) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedProductEvent.CreateProductFavoriteFailed(e))
        }
    }

    private fun deleteFavoriteProduct() {
        _resultState.update { currentState ->
            currentState.copy(isWaitingForFavoriteResult = true)
        }
        uiState.value.favorite?.let { favorite ->
            productFavoriteRepository.delete(
                favorite.id,
                onSuccess = {
                    processDeleteFavoriteSuccess()
                },
                onFailure = { e ->
                    processDeleteFavoriteFailed(e)
                }
            )
        }
    }

    private fun processDeleteFavoriteSuccess() {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedProductEvent.DeleteProductFavoriteSuccess)
        }
    }

    private fun processDeleteFavoriteFailed(e: Exception) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedProductEvent.DeleteProductFavoriteFailed(e))
        }
    }

    private suspend fun updateAddToCartCount(count: BigInteger) = mutex.withLock {
        _resultState.update { currentState ->
            val currentCount = currentState.addToCartCount
            val finalCount = currentCount + (count)
            if (finalCount <= BigInteger.ZERO) {
                return@withLock
            }
            currentState.copy(addToCartCount = finalCount)
        }
    }

    private fun addToCart() {
        _resultState.update { currentState ->
            currentState.copy(isWaitingForAddToCartResult = true)
        }
        uiState.value.product?.let { product ->
            viewModelScope.launch {
                when (val resultUser = userRepository.getUserId()) {
                    is Result.Error -> {

                    }

                    is Result.Success -> {
                        val dtoCart = makeCreateShoppingCartDto(resultUser.data, product)
                        try {
                            val dtoItem = makeCreateShoppingCartItemDto(product)
                            shoppingCartRepository.upsert(
                                dtoCart,
                                dtoItem,
                                product.store.id,
                                onSuccess = {
                                    processAddToCartSuccess()
                                },
                                onFailure = { e ->
                                    processAddToCartFailed(e)
                                }
                            )
                        } catch (e: Exception) {
                            processAddToCartFailed(e)
                        }
                    }
                }
            }
        }
    }

    private fun makeCreateShoppingCartDto(
        userId: String,
        product: ProductDomain
    ): CreateShoppingCartDto {
        return CreateShoppingCartDto(
            userId = userId,
            storeId = product.store.id,
            storeOwnerId = storeOwnerId,
            storeName = product.store.name
        )
    }

    private fun makeCreateShoppingCartItemDto(product: ProductDomain): CreateShoppingCartItemDto {
        if (product.discount.startDate == null || product.discount.endDate == null) {
            throw Exception("Failed to add product to cart") // Custom exception here
        }
        return CreateShoppingCartItemDto(
            quantity = _resultState.value.addToCartCount.toInt(),
            product = CreateShoppingCartItemDto.CreateShoppingCartItemProductDto(
                id = product.id,
                name = product.name,
                description = product.description,
                image = product.image,
                price = product.price.toDouble(),
                discount = CreateShoppingCartItemDto.CreateShoppingCartItemProductDto.CreateShoppingCartItemProductDiscountDto(
                    id = product.discount.id,
                    percentage = product.discount.percentage.toDouble(),
                    startDate = DateUtils.localDateTimeToFirebaseTimestamp(product.discount.startDate),
                    endDate = DateUtils.localDateTimeToFirebaseTimestamp(product.discount.endDate)
                )
            )
        )
    }

    private fun processAddToCartSuccess() {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForAddToCartResult = false)
            }
            _events.send(DetailedProductEvent.ProductAddedToCartSuccess)
        }
    }

    private fun processAddToCartFailed(e: Exception) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForAddToCartResult = false)
            }
            _events.send(DetailedProductEvent.ProductAddedToCartFailed(e))
        }
    }

    @AssistedFactory
    interface DetailedProductViewModelFactory {
        fun create(
            @Assisted("productId") productId: String,
            @Assisted("storeOwnerId") storeOwnerId: String
        ): DetailedProductViewModel
    }

    sealed class DetailedProductEvent {
        data object ProductAddedToCartSuccess : DetailedProductEvent()
        data class ProductAddedToCartFailed(val error: Exception) : DetailedProductEvent()
        data object CreateProductFavoriteSuccess : DetailedProductEvent()
        data class CreateProductFavoriteFailed(val error: Exception) : DetailedProductEvent()
        data object DeleteProductFavoriteSuccess : DetailedProductEvent()
        data class DeleteProductFavoriteFailed(val error: Exception) : DetailedProductEvent()
    }
}