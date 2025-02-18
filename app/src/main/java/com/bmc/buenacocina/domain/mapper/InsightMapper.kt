package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.data.network.model.InsightTopLocationNetwork
import com.bmc.buenacocina.domain.model.InsightTopLocationDomain
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng

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
}

fun InsightTopLocationNetwork.asDomain() = InsightMapper.asDomain(this)