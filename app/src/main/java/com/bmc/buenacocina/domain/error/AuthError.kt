package com.bmc.buenacocina.domain.error

enum class AuthError : BaseError {
    NOT_AUTHENTICATED,
    NOT_VALID_USER_ID
}