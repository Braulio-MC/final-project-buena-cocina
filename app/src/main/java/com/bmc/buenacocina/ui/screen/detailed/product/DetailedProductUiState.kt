package com.bmc.buenacocina.ui.screen.detailed.product

import com.bmc.buenacocina.domain.model.ProductDomain
import com.bmc.buenacocina.domain.model.ProductFavoriteDomain
import java.math.BigInteger

data class DetailedProductUiState(
    val isWaitingForFavoriteResult: Boolean = false,
    val isWaitingForAddToCartResult: Boolean = false,
    val isLoadingProduct: Boolean = false,
    val isLoadingFavorite: Boolean = false,
    val addToCartCount: BigInteger = BigInteger.ONE,
    val product: ProductDomain? = null,
    val favorite: ProductFavoriteDomain? = null
)
