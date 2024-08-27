package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.LocationNetwork
import com.bmc.buenacocina.domain.model.LocationDomain

object LocationMapper {
    fun asDomain(network: LocationNetwork): LocationDomain {
        return LocationDomain(
            id = network.documentId,
            name = network.name,
            description = network.description,
            storeId = network.storeId,
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun LocationNetwork.asDomain() = LocationMapper.asDomain(this)