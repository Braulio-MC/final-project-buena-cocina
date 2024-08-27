package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.service.PaymentMethodService
import com.bmc.buenacocina.data.paging.PaymentMethodPagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.PaymentMethodDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PaymentMethodRepository @Inject constructor(
    private val paymentMethodService: PaymentMethodService,
    private val firestore: FirebaseFirestore
) {
    fun get(id: String): Flow<PaymentMethodDomain?> {
        val paymentMethod = paymentMethodService.get(id)
        return paymentMethod.map { it?.asDomain() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<PaymentMethodDomain>> {
        val paymentMethods = paymentMethodService.get(query)
        return paymentMethods.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<PaymentMethodDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { PaymentMethodPagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}