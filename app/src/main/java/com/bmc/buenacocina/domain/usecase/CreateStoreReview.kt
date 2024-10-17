package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.data.network.dto.CreateStoreReviewDto
import com.bmc.buenacocina.domain.repository.StoreReviewRepository
import javax.inject.Inject

class CreateStoreReview @Inject constructor(
    private val storeReviewRepository: StoreReviewRepository
) {
    operator fun invoke(
        userId: String,
        storeId: String,
        rating: Float,
        comment: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dto = CreateStoreReviewDto(
            userId = userId,
            storeId = storeId,
            rating = rating,
            comment = comment
        )
        storeReviewRepository.create(
            dto = dto,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}