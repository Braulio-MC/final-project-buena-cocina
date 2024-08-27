package com.bmc.buenacocina.ui.screen.detailed.product

import java.math.BigInteger

sealed class DetailedProductIntent {
    data object ToggleFavoriteProduct : DetailedProductIntent()
    data class IncreaseProductCount(val count: BigInteger = BigInteger.ONE) : DetailedProductIntent()
    data class DecreaseProductCount(val count: BigInteger = -BigInteger.ONE) : DetailedProductIntent()
    data object AddToShoppingCart : DetailedProductIntent()
}
