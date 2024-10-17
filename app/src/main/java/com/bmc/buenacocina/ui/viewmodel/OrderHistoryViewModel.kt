package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.repository.OrderRepository
import com.bmc.buenacocina.domain.repository.UserRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun orders(): Flow<PagingData<OrderDomain>> = flow {
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
                orderRepository.paging(qOrders).cachedIn(viewModelScope)
            }
        }
    }.cachedIn(viewModelScope)
}