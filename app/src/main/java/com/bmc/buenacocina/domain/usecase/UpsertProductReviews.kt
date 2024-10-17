package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.mapper.asUpsertDto
import com.bmc.buenacocina.domain.model.UpsertProductReviewDomain
import com.bmc.buenacocina.domain.repository.ProductReviewRepository
import javax.inject.Inject

class UpsertProductReviews @Inject constructor(
    private val productReviewRepository: ProductReviewRepository
) {
    operator fun invoke(
        reviews: List<UpsertProductReviewDomain>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val dto = reviews.map { it.asUpsertDto() }
        productReviewRepository.upsertAsBatch(
            dto = dto,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}