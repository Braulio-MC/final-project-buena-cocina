package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.data.network.model.StoreReviewAnalyzedNetwork
import com.bmc.buenacocina.domain.model.StoreReviewAnalyzedDomain
import java.time.LocalDateTime

object StoreReviewAnalyzedMapper {
    fun asDomain(network: StoreReviewAnalyzedNetwork): StoreReviewAnalyzedDomain {
        return StoreReviewAnalyzedDomain(
            id = network.id,
            storeId = network.storeId,
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

fun StoreReviewAnalyzedNetwork.asDomain(): StoreReviewAnalyzedDomain =
    StoreReviewAnalyzedMapper.asDomain(this)