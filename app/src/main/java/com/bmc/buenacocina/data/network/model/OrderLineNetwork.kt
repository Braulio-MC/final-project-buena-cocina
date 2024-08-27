package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class OrderLineNetwork(
    @DocumentId
    val documentId: String = "",
    val quantity: Int = 0,
    val product: OrderLineProductNetwork = OrderLineProductNetwork(),
    val paginationKey: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null

) {
    data class OrderLineProductNetwork(
        val id: String = "",
        val name: String = "",
        val description: String = "",
        val image: String = "",
        val price: Double = 0.0,
        val discount: OrderLineProductDiscountNetwork = OrderLineProductDiscountNetwork()
    ) {
        data class OrderLineProductDiscountNetwork(
            val id: String = "",
            val percentage: Double = 0.0,
            val startDate: Timestamp? = null,
            val endDate: Timestamp? = null
        )
    }
}