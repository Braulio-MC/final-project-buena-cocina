package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.model.ProductFavoriteDomain
import com.bmc.buenacocina.domain.repository.ProductFavoriteRepository
import com.bmc.buenacocina.domain.repository.UserRepository
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProductFavoriteViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productFavoriteRepository: ProductFavoriteRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val productFavorites: StateFlow<PagingData<ProductFavoriteDomain>> = flow {
        emit(userRepository.getUserId())
    }.flatMapLatest { result ->
        when (result) {
            is Result.Error -> {
                flowOf(PagingData.empty())
            }
            is Result.Success -> {
                val qProductFavorites: (Query) -> Query = { query ->
                    query.whereEqualTo("userId", result.data)
                }
                productFavoriteRepository.paging(qProductFavorites)
            }
        }
    }.cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = PagingData.empty(),
        )
}