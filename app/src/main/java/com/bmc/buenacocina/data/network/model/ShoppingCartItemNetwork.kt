package com.bmc.buenacocina.data.network.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class ShoppingCartItemNetwork(
    @DocumentId
    val documentId: String = "",
    val cartId: String = "",
    val product: ShoppingCartItemProductNetwork = ShoppingCartItemProductNetwork(),
    val quantity: Int = 0,
    val paginationKey: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    data class ShoppingCartItemProductNetwork(
        val id: String = "",
        val name: String = "",
        val description: String = "",
        val image: String = "",
        val price: Double = 0.0,
        val discount: ShoppingCartItemProductDiscountNetwork = ShoppingCartItemProductDiscountNetwork()
    ) {
        data class ShoppingCartItemProductDiscountNetwork(
            val id: String = "",
            val percentage: Double = 0.0,
            val startDate: Timestamp? = null,
            val endDate: Timestamp? = null
        )
    }
}
