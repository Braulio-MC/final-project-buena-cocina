package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.dto.GetGetStreamTokenDto
import com.bmc.buenacocina.domain.error.handleApiException
import com.bmc.buenacocina.domain.error.handleApiFailure
import com.bmc.buenacocina.data.network.service.GetStreamTokenService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.statusCode
import javax.inject.Inject

class GetStreamTokenRepository @Inject constructor(
    private val getStreamTokenService: GetStreamTokenService
) {
    suspend fun request(userId: String): Result<String, DataError> {
        val dto = GetGetStreamTokenDto(userId)
        return when (val response = getStreamTokenService.request(dto)) {
            is ApiResponse.Failure.Error -> {
                Result.Error(handleApiFailure(response.statusCode))
            }

            is ApiResponse.Failure.Exception -> {
                Result.Error(handleApiException(response.throwable))
            }

            is ApiResponse.Success -> {
                Result.Success(response.data)
            }
        }
    }
}