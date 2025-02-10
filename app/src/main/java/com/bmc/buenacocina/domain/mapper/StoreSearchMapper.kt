package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.data.network.model.StoreSearchNetwork
import com.bmc.buenacocina.domain.model.StoreSearchDomain

object StoreSearchMapper {
    fun asDomain(network: StoreSearchNetwork): StoreSearchDomain {
        return StoreSearchDomain(
            id = network.id,
            name = network.name,
            description = network.description,
            rating = network.rating.toFloat(),
            totalReviews = network.totalReviews.toBigInteger(),
            type = network.type,
            image = network.image
        )
    }
}

fun StoreSearchNetwork.asDomain(): StoreSearchDomain = StoreSearchMapper.asDomain(this)