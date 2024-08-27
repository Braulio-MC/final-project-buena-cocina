package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.StoreFavoriteNetwork
import com.bmc.buenacocina.domain.model.StoreFavoriteDomain

object StoreFavoriteMapper {
    fun asDomain(network: StoreFavoriteNetwork): StoreFavoriteDomain {
        return StoreFavoriteDomain(
            id = network.documentId,
            name = network.name,
            description = network.description,
            image = network.image,
            phoneNumber = network.phoneNumber,
            email = network.email,
            storeId = network.storeId,
            userId = network.userId,
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun StoreFavoriteNetwork.asDomain() = StoreFavoriteMapper.asDomain(this)