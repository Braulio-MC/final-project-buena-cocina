package com.bmc.buenacocina.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.repository.StoreRepository
import com.bmc.buenacocina.ui.screen.category.restaurant.StoreIntent
import com.bmc.buenacocina.ui.screen.category.restaurant.StoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RestaurantCategoryViewModel @Inject constructor(
    storeRepository: StoreRepository
) : ViewModel() {
    private val storesBestFlow = storeRepository.get()
    private val storesFavoriteFlow = storeRepository.get()
    private val storesExploreFlow = storeRepository.get()
    private val _uiState = mutableStateOf(StoreUiState())
    val uiState: StateFlow<StoreUiState> = combine(
        storesBestFlow,
        storesFavoriteFlow,
        storesExploreFlow
    ) { storesB, storesF, storesE ->
        StoreUiState(
            favoritesStores = storesF,
            bestRatedStores = storesB,
            exploreStores = storesE
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
        initialValue = _uiState.value
    )

    fun onIntent(intent: StoreIntent) {
        when (intent) {
            StoreIntent.getBestRatedStores -> TODO()
            StoreIntent.getExploreStores -> TODO()
            StoreIntent.getFavoriteStores -> TODO()
        }
    }
}