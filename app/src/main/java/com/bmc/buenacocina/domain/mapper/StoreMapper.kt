package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.StoreNetwork
import com.bmc.buenacocina.domain.model.StoreDomain

object StoreMapper {
    fun asDomain(network: StoreNetwork): StoreDomain {
        return StoreDomain(
            id = network.documentId,
            name = network.name,
            description = network.description,
            email = network.email,
            phoneNumber = network.phoneNumber,
            startTime = StoreDomain.StoreWorkingHoursDomain(
                hour = network.startTime.hour,
                minute = network.startTime.minute
            ),
            endTime = StoreDomain.StoreWorkingHoursDomain(
                hour = network.endTime.hour,
                minute = network.endTime.minute
            ),
            rating = network.rating.toBigDecimal(),
            totalRating = network.totalRating.toBigDecimal(),
            totalReviews = network.totalReviews.toBigInteger(),
            image = network.image,
            userId = network.userId,
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt),
        )
    }
}

fun StoreNetwork.asDomain() = StoreMapper.asDomain(this)