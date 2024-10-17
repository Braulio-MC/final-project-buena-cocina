package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.dto.UpsertProductReviewDto
import com.bmc.buenacocina.data.network.model.ProductReviewNetwork
import com.bmc.buenacocina.domain.model.ProductReviewDomain
import com.bmc.buenacocina.domain.model.UpsertProductReviewDomain
import com.bmc.buenacocina.ui.screen.detailed.order.rating.DetailedOrderRatingUiResultState

object ProductReviewMapper {
    fun asDomain(network: ProductReviewNetwork): ProductReviewDomain {
        return ProductReviewDomain(
            id = network.documentId,
            userId = network.userId,
            productId = network.productId,
            rating = network.rating,
            comment = network.comment,
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt),
        )
    }

    fun asUpsertDto(domain: UpsertProductReviewDomain): UpsertProductReviewDto {
        return UpsertProductReviewDto(
            id = domain.id,
            userId = domain.userId,
            productId = domain.productId,
            rating = domain.rating,
            comment = domain.comment,
        )
    }

    fun asUpsertDomain(
        domain: ProductReviewDomain
    ): UpsertProductReviewDomain {
        return UpsertProductReviewDomain(
            id = domain.id,
            userId = domain.userId,
            productId = domain.productId,
            rating = domain.rating,
            comment = domain.comment
        )
    }
}

fun ProductReviewNetwork.asDomain(): ProductReviewDomain = ProductReviewMapper.asDomain(this)
fun UpsertProductReviewDomain.asUpsertDto(): UpsertProductReviewDto =
    ProductReviewMapper.asUpsertDto(this)

fun ProductReviewDomain.asUpsertDomain(): UpsertProductReviewDomain = ProductReviewMapper.asUpsertDomain(this)