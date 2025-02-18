package com.bmc.buenacocina.ui.screen.shoppingcart

import com.bmc.buenacocina.domain.UiText
import com.bmc.buenacocina.domain.model.InsightTopLocationDomain
import com.bmc.buenacocina.domain.model.PaymentMethodDomain
import com.bmc.buenacocina.domain.model.ShoppingCartDomain
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import com.google.android.gms.maps.model.LatLng
import java.math.BigDecimal

data class ShoppingCartUiState(
    val isLoadingShoppingCart: Boolean = false,
    val isLoadingShoppingCartItems: Boolean = false,
    val isLoadingTopLocationsOnMap: Boolean = false,
    val isWaitingForOrderResult: Boolean = false,
    val shoppingCart: ShoppingCartDomain? = null,
    val shoppingCartError: UiText? = null,
    val shoppingCartItems: List<ShoppingCartItemDomain> = emptyList(),
    val shoppingCartItemsError: UiText? = null,
    val total: CartTotalUiState = CartTotalUiState(),
    val currentDeliveryLocation: LatLng? = null,
    val currentDeliveryLocationError: UiText? = null,
    val currentPaymentMethod: PaymentMethodDomain? = null,
    val currentPaymentMethodError: UiText? = null,
    val cuceiCenterOnMap: Pair<String, LatLng>? = null,
    val cuceiAreaBoundsOnMap: List<Pair<String, LatLng>>? = null,
    val userLocation: LatLng? = null,
    val topLocationsOnMap: List<InsightTopLocationDomain> = emptyList()
) {
    data class CartTotalUiState(
        val subTotal: BigDecimal = BigDecimal.ZERO,
        val service: BigDecimal = BigDecimal.ZERO,
        val total: BigDecimal = BigDecimal.ZERO
    )
}
