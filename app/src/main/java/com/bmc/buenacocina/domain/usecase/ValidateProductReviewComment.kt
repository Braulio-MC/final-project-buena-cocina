package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.core.PRODUCT_REVIEW_MAXIMUM_COMMENT_LENGTH
import com.bmc.buenacocina.core.PRODUCT_REVIEW_MINIMUM_COMMENT_LENGTH
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import javax.inject.Inject

class ValidateProductReviewComment @Inject constructor() {
    operator fun invoke(comment: String?): Result<Unit, FormError.ProductReviewCommentError> {
        if (comment == null) {
            return Result.Error(FormError.ProductReviewCommentError.NOT_VALID)
        }
        if (comment.isBlank()) {
            return Result.Error(FormError.ProductReviewCommentError.COMMENT_IS_BLANK)
        }
        if (comment.length < PRODUCT_REVIEW_MINIMUM_COMMENT_LENGTH) {
            return Result.Error(FormError.ProductReviewCommentError.COMMENT_IS_TOO_SHORT)
        }
        if (comment.length > PRODUCT_REVIEW_MAXIMUM_COMMENT_LENGTH) {
            return Result.Error(FormError.ProductReviewCommentError.COMMENT_IS_TOO_LONG)
        }
        return Result.Success(Unit)
    }
}