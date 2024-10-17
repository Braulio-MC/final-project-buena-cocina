package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import javax.inject.Inject

class ValidateStoreReviewRating @Inject constructor() {
    operator fun invoke(rating: Float?): Result<Unit, FormError.StoreReviewRatingError> {
        if (rating == null) {
            return Result.Error(FormError.StoreReviewRatingError.NOT_VALID)
        }
        return if (rating !in 0f..5f) {
            Result.Error(FormError.StoreReviewRatingError.NOT_VALID)
        } else {
            Result.Success(Unit)
        }
    }
}