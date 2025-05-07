package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.data.network.model.InsightTopLocationNetwork
import com.bmc.buenacocina.data.network.model.InsightTopRatedStoreNetwork
import com.bmc.buenacocina.data.network.model.InsightTopSoldProductNetwork
import com.bmc.buenacocina.domain.model.InsightTopLocationDomain
import com.bmc.buenacocina.domain.model.InsightTopRatedStoreDomain
import com.bmc.buenacocina.domain.model.InsightTopSoldProductDomain
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng
import java.time.LocalDateTime

object InsightMapper {
    fun asDomain(network: InsightTopLocationNetwork): InsightTopLocationDomain {
        return InsightTopLocationDomain(
            geohash = network.geohash,
            geopoint = WeightedLatLng(
                LatLng(network.geopoint.lat, network.geopoint.lng),
                network.intensity.toDouble()
            )
        )
    }

    fun asDomain(network: InsightTopSoldProductNetwork): InsightTopSoldProductDomain {
        return InsightTopSoldProductDomain(
            id = network.id,
            name = network.name,
            description = network.description,
            image = network.image,
            categories = network.categories.map {
                InsightTopSoldProductDomain.InsightTopSoldProductCategoryDomain(
                    id = it.id,
                    name = it.name
                )
            },
            storeName = network.storeName,
            storeOwnerId = network.storeOwnerId,
            discountPercentage = network.discountPercentage.toBigDecimal(),
            discountStartDate = LocalDateTime.parse(network.discountStartDate),
            discountEndDate = LocalDateTime.parse(network.discountEndDate),
            rating = network.rating.toBigDecimal(),
            totalReviews = network.totalReviews.toBigInteger(),
            totalQuantitySold = network.totalQuantitySold.toBigInteger(),
            hitsOnOrders = network.hitsOnOrders.toBigInteger()
        )
    }

    fun asDomain(network: InsightTopRatedStoreNetwork): InsightTopRatedStoreDomain {
        return InsightTopRatedStoreDomain(
            id = network.id,
            name = network.name,
            image = network.image,
            rating = network.rating.toBigDecimal(),
            totalReviews = network.totalReviews
        )
    }
}

fun InsightTopLocationNetwork.asDomain() = InsightMapper.asDomain(this)
fun InsightTopSoldProductNetwork.asDomain() = InsightMapper.asDomain(this)
fun InsightTopRatedStoreNetwork.asDomain() = InsightMapper.asDomain(this)