package com.bmc.buenacocina.ui.screen.detailed.order

import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.model.OrderLineDomain
import com.google.android.gms.maps.model.LatLng
import java.math.BigDecimal

data class DetailedOrderUiState(
    val isLoadingOrder: Boolean = false,
    val isLoadingOrderLines: Boolean = false,
    val isLoadingUserLocation: Boolean = false,
    val isCalculatingOrderTotal: Boolean = false,
    val isWaitingForChannelResult: Boolean = false,
    val orderTotal: BigDecimal = BigDecimal.ZERO,
    val order: OrderDomain? = null,
    val lines: List<OrderLineDomain> = emptyList(),
    val cuceiCenterOnMap: Pair<String, LatLng>? = null,
    val cuceiAreaBoundsOnMap: List<Pair<String, LatLng>>? = null,
    val userLocation: LatLng? = null,
)
