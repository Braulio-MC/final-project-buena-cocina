package com.bmc.buenacocina.ui.screen.detailed.order

import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.model.OrderLineDomain

data class DetailedOrderUiState(
    val isLoading: Boolean = false,
    val order: OrderDomain? = null,
    val lines: List<OrderLineDomain> = emptyList(),
)
