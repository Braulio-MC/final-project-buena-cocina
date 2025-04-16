package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.data.network.model.OrderSearchNetwork
import com.bmc.buenacocina.domain.model.OrderSearchDomain
import java.time.Instant
import java.time.ZoneId

object OrderSearchMapper {
    fun asDomain(network: OrderSearchNetwork): OrderSearchDomain {
        return OrderSearchDomain(
            id = network.id,
            store = OrderSearchDomain.OrderSearchStoreDomain(
                id = network.store.id,
                name = network.store.name,
                ownerId = network.store.ownerId
            ),
            status = network.status,
            paymentMethod = OrderSearchDomain.OrderSearchPaymentMethodDomain(
                name = network.paymentMethod.name
            ),
            user = OrderSearchDomain.OrderSearchUserDomain(
                id = network.user.id,
                name = network.user.name
            ),
            updatedAt = Instant.ofEpochMilli(network.updatedAt.toLong()).atZone(ZoneId.of("UTC"))
                .toLocalDateTime(),
            rated = network.rated,
            type = network.type
        )
    }
}

fun OrderSearchNetwork.asDomain(): OrderSearchDomain = OrderSearchMapper.asDomain(this)