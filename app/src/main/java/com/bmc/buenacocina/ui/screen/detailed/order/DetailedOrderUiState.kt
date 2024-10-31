package com.bmc.buenacocina.ui.screen.detailed.order

import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.model.OrderLineDomain
import java.math.BigDecimal

data class DetailedOrderUiState(
    val isLoadingOrder: Boolean = false,
    val isLoadingOrderLines: Boolean = false,
    val isWaitingForChannelResult: Boolean = false,
    val orderTotal: BigDecimal = BigDecimal.ZERO,
    val order: OrderDomain? = null,
    val lines: List<OrderLineDomain> = emptyList(),
)
