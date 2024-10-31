package com.bmc.buenacocina.ui.screen.shoppingcart

import com.bmc.buenacocina.domain.UiText
import com.bmc.buenacocina.domain.model.LocationDomain
import com.bmc.buenacocina.domain.model.PaymentMethodDomain
import com.bmc.buenacocina.domain.model.ShoppingCartDomain
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import java.math.BigDecimal

data class ShoppingCartUiState(
    val isLoadingShoppingCart: Boolean = false,
    val isLoadingShoppingCartItems: Boolean = false,
    val isWaitingForOrderResult: Boolean = false,
    val shoppingCart: ShoppingCartDomain? = null,
    val shoppingCartError: UiText? = null,
    val shoppingCartItems: List<ShoppingCartItemDomain> = emptyList(),
    val shoppingCartItemsError: UiText? = null,
    val total: CartTotalUiState = CartTotalUiState(),
    val currentDeliveryLocation: LocationDomain? = null,
    val currentDeliveryLocationError: UiText? = null,
    val currentPaymentMethod: PaymentMethodDomain? = null,
    val currentPaymentMethodError: UiText? = null,
) {
    data class CartTotalUiState(
        val subTotal: BigDecimal = BigDecimal.ZERO,
        val service: BigDecimal = BigDecimal.ZERO,
        val total: BigDecimal = BigDecimal.ZERO
    )
}
