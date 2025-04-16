package com.bmc.buenacocina.data.network.model

import com.algolia.client.model.search.Hit
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import kotlinx.serialization.json.jsonPrimitive

data class OrderSearchNetwork(
    override val id: String,
    val store: OrderSearchStoreNetwork,
    val status: String,
    val paymentMethod: OrderSearchPaymentMethodNetwork,
    val user: OrderSearchUserNetwork,
    val updatedAt: String,
    val rated: Boolean,
    override val type: SearchableTypes = SearchableTypes.ORDERS
) : Searchable {
    data class OrderSearchPaymentMethodNetwork(
        val name: String
    )

    data class OrderSearchStoreNetwork(
        val id: String,
        val name: String,
        val ownerId: String
    )

    data class OrderSearchUserNetwork(
        val id: String,
        val name: String
    )
}

fun Hit.toOrderNetwork(): OrderSearchNetwork {
    return OrderSearchNetwork(
        id = objectID,
        store = OrderSearchNetwork.OrderSearchStoreNetwork(
            id = additionalProperties?.get("store.id")?.jsonPrimitive?.content ?: "",
            name = additionalProperties?.get("store.name")?.jsonPrimitive?.content ?: "",
            ownerId = additionalProperties?.get("store.ownerId")?.jsonPrimitive?.content ?: "",
        ),
        status = additionalProperties?.get("status")?.jsonPrimitive?.content ?: "",
        paymentMethod = OrderSearchNetwork.OrderSearchPaymentMethodNetwork(
            name = additionalProperties?.get("paymentMethod.name")?.jsonPrimitive?.content ?: ""
        ),
        user = OrderSearchNetwork.OrderSearchUserNetwork(
            id = additionalProperties?.get("user.id")?.jsonPrimitive?.content ?: "",
            name = additionalProperties?.get("user.name")?.jsonPrimitive?.content ?: ""
        ),
        updatedAt = additionalProperties?.get("updatedAt")?.jsonPrimitive?.content ?: "",
        rated = additionalProperties?.get("rated")?.jsonPrimitive?.content?.toBoolean() ?: false
    )
}