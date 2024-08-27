package com.bmc.buenacocina.data.network.handler

import com.bmc.buenacocina.domain.error.DataError
import com.skydoves.sandwich.StatusCode

fun handleApiFailure(statusCode: StatusCode): DataError {
    return when (statusCode) {
        StatusCode.Unknown -> {
            DataError.NetworkError.UNKNOWN
        }

        StatusCode.BadRequest -> {
            DataError.NetworkError.BAD_REQUEST
        }

        StatusCode.Unauthorized -> {
            DataError.NetworkError.UNAUTHORIZED
        }

        StatusCode.NotFound -> {
            DataError.NetworkError.NOT_FOUND
        }

        StatusCode.UnProcessableEntity -> {
            DataError.NetworkError.UNPROCESSABLE_CONTENT
        }

        StatusCode.InternalServerError -> {
            DataError.NetworkError.INTERNAL_SERVER_ERROR
        }

        else -> {
            DataError.NetworkError.UNKNOWN
        }
    }
}