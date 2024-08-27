package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateOrderLineDto
import com.bmc.buenacocina.data.network.service.OrderLineService
import com.bmc.buenacocina.data.paging.OrderLinePagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.OrderLineDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderLineRepository @Inject constructor(
    private val orderLineService: OrderLineService,
    private val firestore: FirebaseFirestore
) {
    fun create(
        orderId: String,
        dto: CreateOrderLineDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        orderLineService.create(orderId, dto, onSuccess, onFailure)
    }

    fun createAsBatch(
        orderId: String,
        list: List<CreateOrderLineDto>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        orderLineService.createAsBatch(orderId, list, onSuccess, onFailure)
    }

    fun get(orderId: String, lineId: String): Flow<OrderLineDomain?> {
        val line = orderLineService.get(orderId, lineId)
        return line.map { it?.asDomain() }
    }

    fun get(orderId: String): Flow<List<OrderLineDomain>> {
        val lines = orderLineService.get(orderId)
        return lines.map { list -> list.map { it.asDomain() } }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<OrderLineDomain>> {
        val lines = orderLineService.get(query)
        return lines.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<OrderLineDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { OrderLinePagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}