package com.bmc.buenacocina.data.network.service

import com.bmc.buenacocina.data.network.dto.CreateOrderDto
import com.bmc.buenacocina.data.network.dto.CreateOrderLineDto
import javax.inject.Inject

class OrderOrderLineMediator @Inject constructor(
    private val orderService: OrderService,
    private val orderLineService: OrderLineService
) {
    fun createOrderWithOrderLines(
        dto: CreateOrderDto,
        lines: List<CreateOrderLineDto>,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        orderService.create(
            dto,
            onSuccess = { orderId ->
                orderLineService.createAsBatch(
                    orderId,
                    lines,
                    onSuccess = {
                        onSuccess(orderId)
                    },
                    onFailure = {
                        // Rolling back the order
                        orderService.delete(
                            orderId,
                            onSuccess = {},
                            onFailure
                        )
                    }
                )
            },
            onFailure
        )
    }
}