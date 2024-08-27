package com.bmc.buenacocina.ui.screen.detailed.product

import com.bmc.buenacocina.domain.model.ProductDomain
import com.bmc.buenacocina.domain.model.ProductFavoriteDomain
import java.math.BigInteger

data class DetailedProductUiResultState(
    val isWaitingForFavoriteResult: Boolean = false,
    val isWaitingForAddToCartResult: Boolean = false,
    val addToCartCount: BigInteger = BigInteger.ONE
)

data class DetailedProductUiState(
    val isLoading: Boolean = false,
    val product: ProductDomain? = null,
    val favorite: ProductFavoriteDomain? = null
)
