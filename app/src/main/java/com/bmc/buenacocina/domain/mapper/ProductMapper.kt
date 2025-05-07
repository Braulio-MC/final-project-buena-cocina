package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.ProductNetwork
import com.bmc.buenacocina.domain.model.ProductDomain

object ProductMapper {
    fun asDomain(network: ProductNetwork): ProductDomain {
        return ProductDomain(
            id = network.documentId,
            name = network.name,
            description = network.description,
            image = network.image,
            price = network.price.toBigDecimal(),
            quantity = network.quantity.toBigInteger(),
            store = ProductDomain.ProductStoreDomain(
                id = network.store.id,
                name = network.store.name,
                ownerId = network.store.ownerId
            ),
            categories = network.categories.map { category ->
                ProductDomain.ProductCategoryDomain(
                    id = category.id,
                    name = category.name
                )
            },
            discount = ProductDomain.ProductDiscountDomain(
                id = network.discount.id,
                percentage = network.discount.percentage.toBigDecimal(),
                startDate = DateUtils.firebaseTimestampToLocalDateTime(network.discount.startDate),
                endDate = DateUtils.firebaseTimestampToLocalDateTime(network.discount.endDate)
            ),
            rating = network.rating.toBigDecimal(),
            totalRating = network.totalReviews.toBigDecimal(),
            totalReviews = network.totalReviews.toBigInteger(),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun ProductNetwork.asDomain() = ProductMapper.asDomain(this)
