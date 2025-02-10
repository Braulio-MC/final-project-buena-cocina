package com.bmc.buenacocina.domain.error

// Investigate how to handle exceptions correctly
fun handleApiException(e: Throwable): DataError {
    return when (e) {
        else -> DataError.NetworkError.UNKNOWN
    }
}