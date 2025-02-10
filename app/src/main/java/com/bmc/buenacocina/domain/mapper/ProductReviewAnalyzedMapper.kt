package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.data.network.model.ProductReviewAnalyzedNetwork
import com.bmc.buenacocina.domain.model.ProductReviewAnalyzedDomain
import java.time.LocalDateTime

object ProductReviewAnalyzedMapper {
    fun asDomain(network: ProductReviewAnalyzedNetwork): ProductReviewAnalyzedDomain {
        return ProductReviewAnalyzedDomain(
            id = network.id,
            productId = network.productId,
            userId = network.userId,
            rating = network.rating,
            comment = network.comment,
            sentiment = network.sentiment,
            // LocalDateTime does not handle zoned, instead use ZonedDateTime
            createdAt = LocalDateTime.parse(network.createdAt.removeSuffix("Z")),
            updatedAt = LocalDateTime.parse(network.updatedAt.removeSuffix("Z"))
        )
    }
}

fun ProductReviewAnalyzedNetwork.asDomain(): ProductReviewAnalyzedDomain =
    ProductReviewAnalyzedMapper.asDomain(this)