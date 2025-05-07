package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.bmc.buenacocina.core.PAGING_PAGE_SIZE
import com.bmc.buenacocina.data.network.dto.CreateOrderDto
import com.bmc.buenacocina.data.network.dto.CreateOrderLineDto
import com.bmc.buenacocina.data.network.dto.UpdateOrderDto
import com.bmc.buenacocina.data.network.service.OrderOrderLineMediator
import com.bmc.buenacocina.data.network.service.OrderService
import com.bmc.buenacocina.data.paging.OrderPagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.OrderDomain
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderService: OrderService,
    private val orderOrderLineMediator: OrderOrderLineMediator,
    private val firestore: FirebaseFirestore
) {
    fun create(
        dto: CreateOrderDto,
        lines: List<CreateOrderLineDto>,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        orderOrderLineMediator.createOrderWithOrderLines(dto, lines, onSuccess, onFailure)
    }

    fun update(
        id: String,
        dto: UpdateOrderDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        orderService.update(id, dto, onSuccess, onFailure)
    }

    fun delete(
        id: String,
        onSuccess: (String) -> Unit,
        onFailure: (String, String) -> Unit
    ) {
        orderService.delete(id, onSuccess, onFailure)
    }

    fun get(id: String): Flow<OrderDomain?> {
        val order = orderService.get(id)
        return order.map { it?.asDomain() }
    }

    fun get(query: (Query) -> Query = { it }): Flow<List<OrderDomain>> {
        val orders = orderService.get(query)
        return orders.map { list -> list.map { it.asDomain() } }
    }

    fun paging(query: (Query) -> Query = { it }): Flow<PagingData<OrderDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE
            ),
            pagingSourceFactory = { OrderPagingSource(query, firestore) }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomain() }
        }
    }
}