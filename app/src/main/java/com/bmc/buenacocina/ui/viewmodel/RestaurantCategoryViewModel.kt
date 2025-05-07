package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_PRODUCTS_INDEX
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.repository.InsightRepository
import com.bmc.buenacocina.domain.repository.RemoteConfigRepository
import com.bmc.buenacocina.domain.repository.SearchRepository
import com.bmc.buenacocina.domain.repository.StoreRepository
import com.bmc.buenacocina.ui.screen.category.restaurant.StoreIntent
import com.bmc.buenacocina.ui.screen.category.restaurant.StoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantCategoryViewModel @Inject constructor(
    storeRepository: StoreRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val searchRepository: SearchRepository,
    private val insightRepository: InsightRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState
        .onStart {
            remoteConfigRepository.productCategories
                .onStart {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingProductCategories = true)
                    }
                }
                .onEach { categories ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            productCategories = categories,
                            isLoadingProductCategories = false
                        )
                    }
                }
                .launchIn(viewModelScope)
            getTopRatedStores()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = StoreUiState()
        )
    val storesExplore = storeRepository
        .paging()
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val productSearch = uiState
        .map { it.selectedProductCategory }
        .distinctUntilChanged()
        .filterNotNull()
        .flatMapLatest { category ->
            searchRepository.paging(
                query = "",
                indexName = ALGOLIA_SEARCH_PRODUCTS_INDEX,
                filters = "categories.name:\"${category.name}\""
            ).cachedIn(viewModelScope)
        }

    fun onIntent(intent: StoreIntent) {
        when (intent) {
            is StoreIntent.UpdateCurrentProductCategory -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedProductCategory = intent.productCategory)
                }
            }
        }
    }

    private fun getTopRatedStores() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoadingTopRatedStores = true)
            }
            when (val response = insightRepository.getTopRatedStores()) {
                is Result.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingTopRatedStores = false)
                    }
                }

                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoadingTopRatedStores = false,
                            topRatedStores = response.data
                        )
                    }
                }
            }
        }
    }
}