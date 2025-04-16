package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_ORDERS_INDEX
import com.bmc.buenacocina.core.AlgoliaGetSecuredSearchApiKeyScopes
import com.bmc.buenacocina.core.OrderHistorySearchChips
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.repository.AlgoliaTokenRepository
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.domain.repository.SearchRepository
import com.bmc.buenacocina.domain.repository.UserRepository
import com.bmc.buenacocina.ui.screen.orderhistory.OrderHistoryIntent
import com.bmc.buenacocina.ui.screen.orderhistory.OrderHistoryUiState
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val searchRepository: SearchRepository,
    private val algoliaTokenRepository: AlgoliaTokenRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState = _uiState
    private val _orderHits = MutableStateFlow<Flow<PagingData<Searchable>>?>(null)
    val orderHits = _orderHits.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val orders: StateFlow<PagingData<OrderDomain>> = flow {
        emit(userRepository.getUserId())
    }.flatMapLatest { result ->
        when (result) {
            is Result.Error -> {
                flowOf(PagingData.empty())
            }

            is Result.Success -> {
                val qOrders: (Query) -> Query = { query ->
                    query.whereEqualTo(FieldPath.of("user", "id"), result.data)
                }
                orderRepository.paging(qOrders)
            }
        }
    }.cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = PagingData.empty(),
        )

    fun onIntent(intent: OrderHistoryIntent) {
        when (intent) {
            OrderHistoryIntent.Search -> {
                search()
            }

            is OrderHistoryIntent.UpdateSearchQuery -> {
                _uiState.update { currentState ->
                    currentState.copy(searchQuery = intent.searchQuery)
                }
            }

            OrderHistoryIntent.ClearSearch -> {
                _uiState.update { currentState ->
                    currentState.copy(searchQuery = "")
                }
                _orderHits.value = null
            }
        }
    }

    private fun search() {
        viewModelScope.launch {
            when (val rAccessToken = userRepository.getAccessToken()) {
                is Result.Error -> {

                }

                is Result.Success -> {
                    val authorization = "Bearer ${rAccessToken.data}"
                    when (val rScopedKey =
                        algoliaTokenRepository.requestScopedToken(
                            authorization,
                            AlgoliaGetSecuredSearchApiKeyScopes.USER_ID_NESTED
                        )
                    ) {
                        is Result.Error -> {

                        }

                        is Result.Success -> {
                            var query = _uiState.value.searchQuery
                            var filters: String? = null
                            when (_uiState.value.searchQuery) {
                                OrderHistorySearchChips.ORDER_RATE_STATUS_FALSE.tag -> {
                                    query = ""
                                    filters = "rated:\"${false}\""
                                }
                                OrderHistorySearchChips.ORDER_RATE_STATUS_TRUE.tag -> {
                                    query = ""
                                    filters = "rated:\"${true}\""
                                }
                            }
                            _orderHits.value = searchRepository.pagingWithScopedApiKey(
                                query = query,
                                indexName = ALGOLIA_SEARCH_ORDERS_INDEX,
                                filters = filters,
                                scopedSecuredApiKey = rScopedKey.data
                            )
                        }
                    }
                }
            }
        }
    }
}