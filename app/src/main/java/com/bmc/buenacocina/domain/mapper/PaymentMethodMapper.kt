package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.data.network.model.PaymentMethodNetwork
import com.bmc.buenacocina.domain.model.PaymentMethodDomain

object PaymentMethodMapper {
    fun asDomain(network: PaymentMethodNetwork): PaymentMethodDomain {
        return PaymentMethodDomain(
            id = network.documentId,
            name = network.name,
            description = network.description,
            updatedAt = DateUtils.firebaseTimestampToLocalDateTime(network.createdAt),
            createdAt = DateUtils.firebaseTimestampToLocalDateTime(network.updatedAt)
        )
    }
}

fun PaymentMethodNetwork.asDomain() = PaymentMethodMapper.asDomain(this)