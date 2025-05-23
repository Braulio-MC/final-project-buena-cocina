package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.data.network.service.InsightService
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.DataError
import com.bmc.buenacocina.domain.error.handleApiException
import com.bmc.buenacocina.domain.error.handleApiFailure
import com.bmc.buenacocina.domain.mapper.asDomain
import com.bmc.buenacocina.domain.model.InsightTopLocationDomain
import com.bmc.buenacocina.domain.model.InsightTopRatedStoreDomain
import com.bmc.buenacocina.domain.model.InsightTopSoldProductDomain
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.statusCode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class InsightRepository @Inject constructor(
    private val insightService: InsightService
) {
    suspend fun getTopLocationsOnMap(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Result<List<InsightTopLocationDomain>, DataError> {
        return when (val response = insightService.getTopLocationsOnMap(
            startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        )) {
            is ApiResponse.Failure.Error -> {
                Result.Error(handleApiFailure(response.statusCode))
            }

            is ApiResponse.Failure.Exception -> {
                Result.Error(handleApiException(response.throwable))
            }

            is ApiResponse.Success -> {
                val domain = response.data.points.map { point ->
                    point.asDomain()
                }
                Result.Success(domain)
            }
        }
    }

    suspend fun getTopSoldProducts(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Result<List<InsightTopSoldProductDomain>, DataError> {
        return when (val response = insightService.getTopSoldProducts(
            startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        )) {
            is ApiResponse.Failure.Error -> {
                Result.Error(handleApiFailure(response.statusCode))
            }

            is ApiResponse.Failure.Exception -> {
                Result.Error(handleApiException(response.throwable))
            }

            is ApiResponse.Success -> {
                val domain = response.data.products.map { product ->
                    product.asDomain()
                }
                Result.Success(domain)
            }
        }
    }

    suspend fun getTopRatedStores(
        start: Float? = null,
        end: Float? = null
    ): Result<List<InsightTopRatedStoreDomain>, DataError> {
        return when (val response = insightService.getTopRatedStores(start, end)) {
            is ApiResponse.Failure.Error -> {
                Result.Error(handleApiFailure(response.statusCode))
            }

            is ApiResponse.Failure.Exception -> {
                Result.Error(handleApiException(response.throwable))
            }

            is ApiResponse.Success -> {
                val domain = response.data.stores.map { store ->
                    store.asDomain()
                }
                Result.Success(domain)
            }
        }
    }
}