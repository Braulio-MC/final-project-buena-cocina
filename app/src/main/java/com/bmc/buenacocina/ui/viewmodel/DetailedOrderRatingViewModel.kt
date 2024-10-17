package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.mapper.asFormErrorUiText
import com.bmc.buenacocina.domain.mapper.asUpsertDomain
import com.bmc.buenacocina.domain.model.OrderLineDomain
import com.bmc.buenacocina.domain.model.UpsertProductReviewDomain
import com.bmc.buenacocina.domain.repository.OrderLineRepository
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.domain.repository.ProductReviewRepository
import com.bmc.buenacocina.domain.repository.StoreReviewRepository
import com.bmc.buenacocina.domain.usecase.UpdateOrder
import com.bmc.buenacocina.domain.usecase.UpsertProductReviews
import com.bmc.buenacocina.domain.usecase.UpsertStoreReview
import com.bmc.buenacocina.domain.usecase.ValidateOrder
import com.bmc.buenacocina.domain.usecase.ValidateProductReviewComment
import com.bmc.buenacocina.domain.usecase.ValidateProductReviewRating
import com.bmc.buenacocina.domain.usecase.ValidateStoreReviewComment
import com.bmc.buenacocina.domain.usecase.ValidateStoreReviewRating
import com.bmc.buenacocina.ui.screen.detailed.order.rating.DetailedOrderRatingIntent
import com.bmc.buenacocina.ui.screen.detailed.order.rating.DetailedOrderRatingUiResultState
import com.bmc.buenacocina.ui.screen.detailed.order.rating.DetailedOrderRatingUiState
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailedOrderRatingViewModel.DetailedOrderRatingViewModelFactory::class)
class DetailedOrderRatingViewModel @AssistedInject constructor(
    private val validateOrder: ValidateOrder,
    private val validateStoreReviewRating: ValidateStoreReviewRating,
    private val validateStoreReviewComment: ValidateStoreReviewComment,
    private val validateProductReviewRating: ValidateProductReviewRating,
    private val validateProductReviewComment: ValidateProductReviewComment,
    private val upsertStoreReview: UpsertStoreReview,
    private val upsertProductReviews: UpsertProductReviews,
    private val updateOrder: UpdateOrder,
    private val storeReviewRepository: StoreReviewRepository,
    private val productReviewRepository: ProductReviewRepository,
    orderRepository: OrderRepository,
    orderLineRepository: OrderLineRepository,
    @Assisted private val orderId: String
) : ViewModel() {
    private val _order = orderRepository.get(orderId)
    private val _orderLines = orderLineRepository.get(orderId)
    private val _resultState = MutableStateFlow(DetailedOrderRatingUiResultState())
    private val _events = Channel<DetailedOrderRatingViewModelEvent>()
    val events = _events.receiveAsFlow()
    val resultState = _resultState.asStateFlow()
    val uiState: StateFlow<DetailedOrderRatingUiState> = combine(
        _order,
        _orderLines
    ) { order, orderLines ->
        if (order != null) {
            val qStoreReview: (Query) -> Query = { query ->
                query.where(
                    Filter.and(
                        Filter.equalTo("userId", order.user.id),
                        Filter.equalTo("storeId", order.store.id)
                    )
                )
            }
            val ids = orderLines.map { line ->
                line.product.id
            }
            val qProductReview: (Query) -> Query = { query ->
                query.where(
                    Filter.and(
                        Filter.equalTo("userId", order.user.id),
                        Filter.inArray("productId", ids)
                    )
                )
            }
            collectStoreReview(qStoreReview, order.user.id, order.store.id)
            collectItemReviews(qProductReview, order.user.id, orderLines)
        }
        DetailedOrderRatingUiState(
            order = order,
            lines = orderLines,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
        initialValue = DetailedOrderRatingUiState(isLoading = true)
    )

    private fun collectStoreReview(q: (Query) -> Query, userId: String, storeId: String) {
        storeReviewRepository.get(q)
            .onEach { storeReview ->
                val storeFirst = storeReview.firstOrNull()
                if (storeFirst != null) {
                    _resultState.update { currentState ->
                        currentState.copy(storeRating = storeFirst.asUpsertDomain())
                    }
                } else {
                    // Initialize store review
                    _resultState.update { currentState ->
                        currentState.copy(
                            storeRating = currentState.storeRating.copy(
                                userId = userId,
                                storeId = storeId
                            )
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun collectItemReviews(
        q: (Query) -> Query,
        userId: String,
        lines: List<OrderLineDomain>
    ) {
        productReviewRepository.get(q)
            .onEach { productReviews ->
                _resultState.update { currentState ->
                    // Initialize product reviews
                    val initReviews = lines.map {
                        DetailedOrderRatingUiResultState.DetailedOrderRatingUiStateItemRating(
                            item = UpsertProductReviewDomain(
                                userId = userId,
                                productId = it.product.id
                            )
                        )
                    }
                    val collectedReviews = productReviews.map {
                        DetailedOrderRatingUiResultState.DetailedOrderRatingUiStateItemRating(
                            item = it.asUpsertDomain()
                        )
                    }
                    currentState.copy(itemRatings = initReviews.map {
                        val review = collectedReviews.find { review ->
                            review.item.productId == it.item.productId
                        }
                        it.copy(item = review?.item ?: it.item)
                    })
                }
            }.launchIn(viewModelScope)
    }

    fun onIntent(intent: DetailedOrderRatingIntent) {
        when (intent) {
            is DetailedOrderRatingIntent.StoreCommentChanged -> {
                _resultState.update { currentState ->
                    currentState.copy(storeRating = currentState.storeRating.copy(comment = intent.comment))
                }
            }

            is DetailedOrderRatingIntent.StoreRatingChanged -> {
                _resultState.update { currentState ->
                    currentState.copy(storeRating = currentState.storeRating.copy(rating = intent.rating))
                }
            }

            is DetailedOrderRatingIntent.ItemCommentChanged -> {
                _resultState.update { currentState ->
                    val updatedProductRatings = currentState.itemRatings.map { item ->
                        if (item.item.productId == intent.id) {
                            item.copy(item = item.item.copy(comment = intent.comment))
                        } else {
                            item
                        }
                    }
                    currentState.copy(itemRatings = updatedProductRatings)
                }
            }

            is DetailedOrderRatingIntent.ItemRatingChanged -> {
                _resultState.update { currentState ->
                    val updatedProductRatings = currentState.itemRatings.map { item ->
                        if (item.item.productId == intent.id) {
                            item.copy(item = item.item.copy(rating = intent.rating))
                        } else {
                            item
                        }
                    }
                    currentState.copy(itemRatings = updatedProductRatings)
                }
            }

            DetailedOrderRatingIntent.Submit -> {
                submitOrderRating()
            }
        }
    }

    private fun submitOrderRating() {
        val orderResult = validateOrder(uiState.value.order)
        val storeRatingResult = validateStoreReviewRating(_resultState.value.storeRating.rating)
        val storeCommentResult = validateStoreReviewComment(_resultState.value.storeRating.comment)
        val productRatingResults = _resultState.value.itemRatings.map { product ->
            validateProductReviewRating(product.item.rating)
        }
        val productCommentResults = _resultState.value.itemRatings.map { product ->
            validateProductReviewComment(product.item.comment)
        }

        _resultState.update { currentState ->
            currentState.copy(
                storeRatingError = (storeRatingResult as? Result.Error)?.asFormErrorUiText(),
                storeCommentError = (storeCommentResult as? Result.Error)?.asFormErrorUiText(),
                itemRatings = currentState.itemRatings.mapIndexed { index, item ->
                    item.copy(
                        ratingError = (productRatingResults[index] as? Result.Error)?.asFormErrorUiText(),
                        commentError = (productCommentResults[index] as? Result.Error)?.asFormErrorUiText()
                    )
                }
            )
        }

        val hasErrors = listOf(
            orderResult,
            storeRatingResult,
            storeCommentResult,
            *productRatingResults.toTypedArray(),
            *productCommentResults.toTypedArray()
        ).any { it is Result.Error }

        if (hasErrors) {
            return
        }

        _resultState.update { currentState ->
            currentState.copy(isWaitingForStoreRatingResult = true)
        }
        upsertStoreReview(
            review = _resultState.value.storeRating,
            onSuccess = {
                processOrderStoreRatingSuccess()
            },
            onFailure = { e ->
                processOrderStoreRatingFailure(e)
            }
        )
        _resultState.update { currentState ->
            currentState.copy(isWaitingForItemRatingsResult = true)
        }
        upsertProductReviews(
            reviews = _resultState.value.itemRatings.map { it.item },
            onSuccess = { processOrderItemRatingsSuccess() },
            onFailure = { e ->
                processOrderItemRatingsFailure(e)
            }
        )
        // TODO: Update only if the store and item ratings successfully changed, check how to do it
        _resultState.update { currentState ->
            currentState.copy(isWaitingForOverallRatingResult = true)
        }
        updateOrder(
            id = orderId,
            rated = true,
            onSuccess = {
                processOrderRatingUpdatedSuccess()
            },
            onFailure = { e ->
                processOrderRatingUpdatedFailure(e)
            }
        )
    }

    private fun processOrderStoreRatingSuccess() {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForStoreRatingResult = false)
            }
            _events.send(DetailedOrderRatingViewModelEvent.OrderStoreRatingSuccess)
        }
    }

    private fun processOrderStoreRatingFailure(e: Exception) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForStoreRatingResult = false)
            }
            _events.send(DetailedOrderRatingViewModelEvent.OrderStoreRatingFailed(e))
        }
    }

    private fun processOrderItemRatingsSuccess() {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForItemRatingsResult = false)
            }
            _events.send(DetailedOrderRatingViewModelEvent.OrderItemRatingsSuccess)
        }
    }

    private fun processOrderItemRatingsFailure(e: Exception) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForItemRatingsResult = false)
            }
            _events.send(DetailedOrderRatingViewModelEvent.OrderItemRatingsFailed(e))
        }
    }

    private fun processOrderRatingUpdatedSuccess() {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForOverallRatingResult = false)
            }
            _events.send(DetailedOrderRatingViewModelEvent.OrderRatingUpdatedSuccess)
        }
    }

    private fun processOrderRatingUpdatedFailure(e: Exception) {
        viewModelScope.launch {
            _resultState.update { currentState ->
                currentState.copy(isWaitingForOverallRatingResult = false)
            }
            _events.send(DetailedOrderRatingViewModelEvent.OrderRatingUpdatedFailed(e))
        }
    }

    @AssistedFactory
    interface DetailedOrderRatingViewModelFactory {
        fun create(orderId: String): DetailedOrderRatingViewModel
    }

    sealed class DetailedOrderRatingViewModelEvent {
        data object OrderStoreRatingSuccess : DetailedOrderRatingViewModelEvent()
        data class OrderStoreRatingFailed(val error: Exception) :
            DetailedOrderRatingViewModelEvent()

        data object OrderItemRatingsSuccess : DetailedOrderRatingViewModelEvent()
        data class OrderItemRatingsFailed(val error: Exception) :
            DetailedOrderRatingViewModelEvent()

        data object OrderRatingUpdatedSuccess : DetailedOrderRatingViewModelEvent()
        data class OrderRatingUpdatedFailed(val error: Exception) :
            DetailedOrderRatingViewModelEvent()
    }
}