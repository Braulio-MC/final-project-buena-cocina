package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.mapper.asUpsertDto
import com.bmc.buenacocina.domain.model.UpsertStoreReviewDomain
import com.bmc.buenacocina.domain.repository.StoreReviewRepository
import javax.inject.Inject

class UpsertStoreReview @Inject constructor(
    private val storeReviewRepository: StoreReviewRepository
) {
    operator fun invoke(
        review: UpsertStoreReviewDomain,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dto = review.asUpsertDto()
        storeReviewRepository.upsert(dto, onSuccess, onFailure)
    }
}