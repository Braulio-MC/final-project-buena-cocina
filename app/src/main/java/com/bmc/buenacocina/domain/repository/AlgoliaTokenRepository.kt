package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.core.AlgoliaGetSecuredSearchApiKeyScopes
import com.bmc.buenacocina.data.network.dto.AlgoliaGetSecuredSearchApiKeyDto
import com.bmc.buenacocina.data.network.service.AlgoliaTokenService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.bmc.buenacocina.domain.error.handleApiException
import com.bmc.buenacocina.domain.error.handleApiFailure
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.statusCode
import javax.inject.Inject

class AlgoliaTokenRepository @Inject constructor(
    private val algoliaTokenService: AlgoliaTokenService
) {
    suspend fun requestScopedToken(
        authorization: String,
        scopeType: AlgoliaGetSecuredSearchApiKeyScopes
    ): Result<String, DataError> {
        val dto = AlgoliaGetSecuredSearchApiKeyDto(scopeType.scope)
        return when (val response = algoliaTokenService.requestScopedToken(authorization, dto)) {
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