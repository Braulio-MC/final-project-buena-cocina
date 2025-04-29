package com.bmc.buenacocina.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Clase base sellada
@JsonClass(generateAdapter = false) // No generamos el adaptador aquí
sealed class ChatBotApiResponse{
    data class Message(
        @Json(name = "type") val type: String = "message",
        @Json(name = "message") val message: String
    ) : ChatBotApiResponse()

    data class ProductResponse(
        @Json(name = "type") val type: String = "product",
        @Json(name = "data") val data: List<Product>
    ) : ChatBotApiResponse()

    data class StoreResponse(
        @Json(name = "type") val type: String = "store",
        @Json(name = "data") val data: List<Store>
    ) : ChatBotApiResponse()

}


// Modelos de Producto y Tienda
@JsonClass(generateAdapter = true)
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val discount: Discount?,
    val rating: Double,
    val store: StoreRef,
    val image: String,
    val category: List<String>,
    val totalRating: Int,
    val createdAt: String,
    val quantity: Int
)

@JsonClass(generateAdapter = true)
data class Store(
    val id: String,
    val name: String,
    val description: String,
    val rating: Double,
    val totalRating: Int,
    val totalReviews: Int,
    val phoneNumber: String,
    val email: String,
    val startTime: String,
    val endTime: String
)

@JsonClass(generateAdapter = true)
data class Discount(
    val startDate: String,
    val endDate: String,
    val percentage: Int,
    val id: String
)

@JsonClass(generateAdapter = true)
data class StoreRef(
    val id: String,
    val name: String
)
