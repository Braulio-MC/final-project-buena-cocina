package com.bmc.buenacocina.ui.screen.detailed.order

import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.model.OrderLineDomain

data class DetailedOrderUiResultState(
    val isWaitingForChannelResult: Boolean = false
)

data class DetailedOrderUiState(
    val isLoading: Boolean = false,
    val order: OrderDomain? = null,
    val lines: List<OrderLineDomain> = emptyList(),
)
