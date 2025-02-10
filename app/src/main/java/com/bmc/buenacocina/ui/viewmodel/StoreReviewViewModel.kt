package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bmc.buenacocina.domain.repository.StoreReviewRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = StoreReviewViewModel.StoreReviewViewModelFactory::class)
class StoreReviewViewModel @AssistedInject constructor(
    storeReviewRepository: StoreReviewRepository,
    @Assisted private val storeId: String
) : ViewModel() {
    val reviews = storeReviewRepository
        .pagingAnalyzedByStoreIdWithRange(storeId = storeId)
        .cachedIn(viewModelScope)

    @AssistedFactory
    interface StoreReviewViewModelFactory {
        fun create(storeId: String): StoreReviewViewModel
    }
}