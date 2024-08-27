package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.ShoppingCartNetwork
import com.bmc.buenacocina.domain.model.ShoppingCartDomain

object ShoppingCartMapper {
    fun asDomain(network: ShoppingCartNetwork): ShoppingCartDomain {
        return ShoppingCartDomain(
            id = network.documentId,
            userId = network.userId,
            store = ShoppingCartDomain.ShoppingCartStoreDomain(
                id = network.store.id,
                name = network.store.name
            ),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun ShoppingCartNetwork.asDomain() = ShoppingCartMapper.asDomain(this)