package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.dto.UpsertStoreReviewDto
import com.bmc.buenacocina.data.network.model.StoreReviewNetwork
import com.bmc.buenacocina.domain.model.StoreReviewDomain
import com.bmc.buenacocina.domain.model.UpsertStoreReviewDomain

object StoreReviewMapper {
    fun asDomain(network: StoreReviewNetwork): StoreReviewDomain {
        return StoreReviewDomain(
            id = network.documentId,
            userId = network.userId,
            storeId = network.storeId,
            rating = network.rating,
            comment = network.comment,
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt),
        )
    }

    fun asUpsertDto(domain: UpsertStoreReviewDomain): UpsertStoreReviewDto {
        return UpsertStoreReviewDto(
            id = domain.id,
            userId = domain.userId,
            storeId = domain.storeId,
            rating = domain.rating,
            comment = domain.comment,
        )
    }

    fun asUpsertDomain(domain: StoreReviewDomain): UpsertStoreReviewDomain {
        return UpsertStoreReviewDomain(
            id = domain.id,
            userId = domain.userId,
            storeId = domain.storeId,
            rating = domain.rating,
            comment = domain.comment,
        )
    }
}

fun StoreReviewNetwork.asDomain(): StoreReviewDomain = StoreReviewMapper.asDomain(this)
fun UpsertStoreReviewDomain.asUpsertDto(): UpsertStoreReviewDto = StoreReviewMapper.asUpsertDto(this)
fun StoreReviewDomain.asUpsertDomain(): UpsertStoreReviewDomain = StoreReviewMapper.asUpsertDomain(this)