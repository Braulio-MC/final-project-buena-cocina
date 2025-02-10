package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bmc.buenacocina.domain.repository.ProductReviewRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ProductReviewViewModel.ProductReviewViewModelFactory::class)
class ProductReviewViewModel @AssistedInject constructor(
    productReviewRepository: ProductReviewRepository,
    @Assisted private val productId: String
) : ViewModel() {
    val reviews = productReviewRepository
        .pagingAnalyzedByProductIdWithRange(productId = productId)
        .cachedIn(viewModelScope)

    @AssistedFactory
    interface ProductReviewViewModelFactory {
        fun create(productId: String): ProductReviewViewModel
    }
}