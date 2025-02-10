package com.bmc.buenacocina.data.network.model

import com.algolia.client.model.search.Hit
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import kotlinx.serialization.json.jsonPrimitive

data class StoreSearchNetwork(
    override val id: String,
    val name: String,
    val description: String,
    val rating: String,
    val totalReviews: String,
    override val type: SearchableTypes = SearchableTypes.STORES,
    val image: String
) : Searchable

fun Hit.toStoreNetwork(): StoreSearchNetwork {
    return StoreSearchNetwork(
        id = objectID,
        name = additionalProperties?.get("name")?.jsonPrimitive?.content ?: "",
        description = additionalProperties?.get("description")?.jsonPrimitive?.content ?: "",
        rating = additionalProperties?.get("rating")?.jsonPrimitive?.content ?: "",
        totalReviews = additionalProperties?.get("totalReviews")?.jsonPrimitive?.content ?: "",
        image = additionalProperties?.get("image")?.jsonPrimitive?.content ?: ""
    )
}