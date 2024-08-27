package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.ShoppingCartItemNetwork
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain

object ShoppingCartItemMapper {
    fun asDomain(network: ShoppingCartItemNetwork): ShoppingCartItemDomain {
        return ShoppingCartItemDomain(
            id = network.documentId,
            product = ShoppingCartItemDomain.ShoppingCartItemProductDomain(
                id = network.product.id,
                name = network.product.name,
                description = network.product.description,
                image = network.product.image,
                price = network.product.price.toBigDecimal(),
                discount = ShoppingCartItemDomain.ShoppingCartItemProductDomain.ShoppingCartItemProductDiscountDomain(
                    id = network.product.discount.id,
                    percentage = network.product.discount.percentage.toBigDecimal(),
                    startDate = DateUtils.firebaseTimestampToLocalDateTime(network.product.discount.startDate),
                    endDate = DateUtils.firebaseTimestampToLocalDateTime(network.product.discount.endDate)
                )
            ),
            quantity = network.quantity.toBigInteger(),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun ShoppingCartItemNetwork.asDomain() = ShoppingCartItemMapper.asDomain(this)