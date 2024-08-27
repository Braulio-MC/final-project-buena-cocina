package com.bmc.buenacocina.domain.error

sealed interface DataError : BaseError {
    enum class NetworkError: DataError {
        SERVER_UNAVAILABLE,
        NOT_FOUND,
        BAD_REQUEST,
        UNAUTHORIZED,
        UNPROCESSABLE_CONTENT,
        INTERNAL_SERVER_ERROR,
        UNKNOWN
    }

    enum class LocalError : DataError {
        NOT_FOUND,
        CREATION_FAILED,
        UPDATE_FAILED,
        DELETION_FAILED,
        RESOURCE_ALREADY_CREATED,
        CART_STORE_MISMATCH
    }
}