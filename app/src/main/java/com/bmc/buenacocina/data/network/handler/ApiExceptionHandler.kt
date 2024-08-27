package com.bmc.buenacocina.data.network.handler

import com.bmc.buenacocina.domain.error.DataError
import java.io.IOException

// Investigate how to handle exceptions correctly
fun handleApiException(e: Throwable): DataError {
    return when (e) {
        else -> DataError.NetworkError.UNKNOWN
    }
}