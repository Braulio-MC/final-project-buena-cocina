package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.repository.StoreRepository
import com.bmc.buenacocina.ui.screen.category.restaurant.StoreIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RestaurantCategoryViewModel @Inject constructor(
    storeRepository: StoreRepository
) : ViewModel() {
    private val storesBest = "storeRepository.get()"
    private val storesFavorite = "storeRepository.get()"
    val storesExplore = storeRepository
        .paging()
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = PagingData.empty()
        )

    fun onIntent(intent: StoreIntent) {
        when (intent) {

            else -> {}
        }
    }
}