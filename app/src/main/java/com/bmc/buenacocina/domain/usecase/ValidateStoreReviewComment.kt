package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.core.STORE_REVIEW_MAXIMUM_COMMENT_LENGTH
import com.bmc.buenacocina.core.STORE_REVIEW_MINIMUM_COMMENT_LENGTH
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import javax.inject.Inject

class ValidateStoreReviewComment @Inject constructor() {
    operator fun invoke(comment: String?): Result<Unit, FormError.StoreReviewCommentError> {
        if (comment == null) {
            return Result.Error(FormError.StoreReviewCommentError.NOT_VALID)
        }
        if (comment.isBlank()) {
            return Result.Error(FormError.StoreReviewCommentError.COMMENT_IS_BLANK)
        }
        if (comment.length < STORE_REVIEW_MINIMUM_COMMENT_LENGTH) {
            return Result.Error(FormError.StoreReviewCommentError.COMMENT_IS_TOO_SHORT)
        }
        if (comment.length > STORE_REVIEW_MAXIMUM_COMMENT_LENGTH) {
            return Result.Error(FormError.StoreReviewCommentError.COMMENT_IS_TOO_LONG)
        }
        return Result.Success(Unit)
    }
}