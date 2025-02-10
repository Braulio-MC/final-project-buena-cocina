package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.dto.CreateGetStreamChannelDto
import com.bmc.buenacocina.domain.error.handleApiException
import com.bmc.buenacocina.domain.error.handleApiFailure
import com.bmc.buenacocina.data.network.service.GetStreamChannelService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.statusCode
import javax.inject.Inject

class GetStreamChannelRepository @Inject constructor(
    private val getStreamChannelService: GetStreamChannelService
) {
    suspend fun create(dto: CreateGetStreamChannelDto): Result<String, DataError> {
        return when (val response = getStreamChannelService.create(dto)) {
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