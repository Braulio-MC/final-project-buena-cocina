package com.bmc.buenacocina.ui.screen.shoppingcart

import com.bmc.buenacocina.domain.model.PaymentMethodDomain
import com.google.android.gms.maps.model.LatLng
import java.math.BigInteger

sealed class ShoppingCartIntent {
    data class UpdateCurrentDeliveryLocation(val deliveryLocation: LatLng) :
        ShoppingCartIntent()

    data class UpdateCurrentPaymentMethod(val paymentMethod: PaymentMethodDomain) :
        ShoppingCartIntent()

    data class IncreaseShoppingCartItemQuantity(
        val itemId: String,
        val count: BigInteger = BigInteger.ONE
    ) : ShoppingCartIntent()

    data class DecreaseShoppingCartItemQuantity(
        val itemId: String,
        val count: BigInteger = -BigInteger.ONE
    ) : ShoppingCartIntent()

    data object Order : ShoppingCartIntent()
}