package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.OrderNetwork
import com.bmc.buenacocina.domain.model.OrderDomain

object OrderMapper {
    fun asDomain(network: OrderNetwork): OrderDomain {
        return OrderDomain(
            id = network.documentId,
            status = network.status,
            rated = network.rated,
            user = OrderDomain.OrderUserDomain(
                id = network.user.id,
                name = network.user.name
            ),
            deliveryLocation = OrderDomain.OrderDeliveryLocationDomain(
                id = network.deliveryLocation.id,
                name = network.deliveryLocation.name
            ),
            store = OrderDomain.OrderStoreDomain(
                id = network.store.id,
                ownerId = network.store.ownerId,
                name = network.store.name
            ),
            paymentMethod = OrderDomain.OrderPaymentMethodDomain(
                id = network.paymentMethod.id,
                name = network.paymentMethod.name
            ),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun OrderNetwork.asDomain() = OrderMapper.asDomain(this)