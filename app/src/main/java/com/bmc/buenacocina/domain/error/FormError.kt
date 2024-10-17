package com.bmc.buenacocina.domain.error

sealed interface FormError : BaseError {
    enum class ShoppingCartError : FormError {
        NOT_VALID,
    }

    enum class ShoppingCartItemsError : FormError {
        ITEMS_ARE_EMPTY,
    }

    enum class ShoppingCartLocationError : FormError {
        NOT_SELECTED,
    }

    enum class ShoppingCartPaymentMethodError : FormError {
        NOT_SELECTED,
    }

    enum class OrderError : FormError {
        NOT_VALID,
    }

    enum class StoreReviewCommentError : FormError {
        NOT_VALID,
        COMMENT_IS_BLANK,
        COMMENT_IS_TOO_SHORT,
        COMMENT_IS_TOO_LONG,
    }

    enum class StoreReviewRatingError : FormError {
        NOT_VALID,
    }

    enum class ProductReviewCommentError : FormError {
        NOT_VALID,
        COMMENT_IS_BLANK,
        COMMENT_IS_TOO_SHORT,
        COMMENT_IS_TOO_LONG,
    }

    enum class ProductReviewRatingError : FormError {
        NOT_VALID,
    }
}