package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.ProductFavoriteNetwork
import com.bmc.buenacocina.domain.model.ProductFavoriteDomain

object ProductFavoriteMapper {
    fun asDomain(network: ProductFavoriteNetwork): ProductFavoriteDomain {
        return ProductFavoriteDomain(
            id = network.documentId,
            userId = network.userId,
            productId = network.productId,
            productName = network.productName,
            productImage = network.productImage,
            productDescription = network.productDescription,
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun ProductFavoriteNetwork.asDomain() = ProductFavoriteMapper.asDomain(this)