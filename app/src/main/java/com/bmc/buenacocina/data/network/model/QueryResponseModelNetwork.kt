package com.bmc.buenacocina.data.network.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

sealed class ChatBotApiResponse {
    data class Message(
        val type: String = "message",
        val message: String
    ) : ChatBotApiResponse()

    data class ProductResponse(
        val type: String = "product",
        val data: List<Product>
    ) : ChatBotApiResponse()

    data class StoreResponse(
        val type: String = "store",
        val data: List<Store>
    ) : ChatBotApiResponse()

    class Adapter : JsonDeserializer<ChatBotApiResponse> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): ChatBotApiResponse {
            val jsonObject = json.asJsonObject
            val type = jsonObject.get("type").asString

            return when (type) {
                "message" -> context.deserialize(json, Message::class.java)
                "product" -> context.deserialize(json, ProductResponse::class.java)
                "store" -> context.deserialize(json, StoreResponse::class.java)
                else -> throw JsonParseException("Unknown type: $type")
            }
        }
    }
}

data class Product(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("discount") val discount: Discount?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("store") val store: StoreRef,
    @SerializedName("image") val image: String,
    @SerializedName("category") val category: List<String>,
    @SerializedName("totalRating") val totalRating: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("quantity") val quantity: Int
)

data class Store(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("totalRating") val totalRating: Int,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String
)

data class Discount(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("percentage") val percentage: Int,
    @SerializedName("id") val id: String
)

data class StoreRef(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)