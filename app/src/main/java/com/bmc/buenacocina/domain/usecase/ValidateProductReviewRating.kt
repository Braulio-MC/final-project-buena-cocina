package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import javax.inject.Inject

class ValidateProductReviewRating @Inject constructor() {
    operator fun invoke(rating: Float?): Result<Unit, FormError.ProductReviewRatingError> {
        if (rating == null) {
            return Result.Error(FormError.ProductReviewRatingError.NOT_VALID)
        }
        return if (rating !in 0f..5f) {
            Result.Error(FormError.ProductReviewRatingError.NOT_VALID)
        } else {
            Result.Success(Unit)
        }
    }
}