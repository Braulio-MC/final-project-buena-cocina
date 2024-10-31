package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.model.StoreFavoriteDomain
import com.bmc.buenacocina.domain.repository.StoreFavoriteRepository
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
class StoreFavoriteViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storeFavoriteRepository: StoreFavoriteRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val storeFavorites: StateFlow<PagingData<StoreFavoriteDomain>> = flow {
        emit(userRepository.getUserId())
    }.flatMapLatest { result ->
        when (result) {
            is Result.Error -> {
                flowOf(PagingData.empty())
            }

            is Result.Success -> {
                val qStoreFavorites: (Query) -> Query = { query ->
                    query.whereEqualTo("userId", result.data)
                }
                storeFavoriteRepository.paging(qStoreFavorites)
            }
        }
    }.cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = PagingData.empty(),
        )
}