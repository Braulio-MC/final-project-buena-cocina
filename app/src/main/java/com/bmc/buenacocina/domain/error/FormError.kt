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
}