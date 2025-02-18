package com.bmc.buenacocina.data.network.model

import com.algolia.client.model.search.Hit
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import kotlinx.serialization.json.jsonPrimitive

data class ProductSearchNetwork(
    override val id: String,
    val name: String,
    val category: ProductSearchCategoryNetwork,
    val description: String,
    val discount: ProductSearchDiscountNetwork,
    val rating: String,
    val totalReviews: String,
    val store: ProductSearchStoreNetwork,
    override val type: SearchableTypes = SearchableTypes.PRODUCTS,
    val image: String
) : Searchable {
    data class ProductSearchCategoryNetwork(
        val name: String
    )

    data class ProductSearchDiscountNetwork(
        val percentage: String,
        val startDate: String,
        val endDate: String
    )

    data class ProductSearchStoreNetwork(
        val name: String,
        val ownerId: String
    )
}

fun Hit.toProductNetwork(): ProductSearchNetwork {
    return ProductSearchNetwork(
        id = objectID,
        name = additionalProperties?.get("name")?.jsonPrimitive?.content ?: "",
        category = ProductSearchNetwork.ProductSearchCategoryNetwork(
            name = additionalProperties?.get("category.name")?.jsonPrimitive?.content ?: ""
        ),
        description = additionalProperties?.get("description")?.jsonPrimitive?.content ?: "",
        discount = ProductSearchNetwork.ProductSearchDiscountNetwork(
            percentage = additionalProperties?.get("discount.percentage")?.jsonPrimitive?.content
                ?: "",
            startDate = additionalProperties?.get("discount.startDate")?.jsonPrimitive?.content
                ?: "",
            endDate = additionalProperties?.get("discount.endDate")?.jsonPrimitive?.content ?: ""
        ),
        rating = additionalProperties?.get("rating")?.jsonPrimitive?.content ?: "",
        totalReviews = additionalProperties?.get("totalReviews")?.jsonPrimitive?.content ?: "",
        store = ProductSearchNetwork.ProductSearchStoreNetwork(
            name = additionalProperties?.get("store.name")?.jsonPrimitive?.content ?: "",
            ownerId = additionalProperties?.get("store.ownerId")?.jsonPrimitive?.content ?: ""
        ),
        image = additionalProperties?.get("image")?.jsonPrimitive?.content ?: ""
    )
}