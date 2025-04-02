package com.bmc.buenacocina.domain

import com.algolia.client.model.search.Hit
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_PRODUCTS_INDEX
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_STORES_INDEX
import com.bmc.buenacocina.data.network.model.toProductNetwork
import com.bmc.buenacocina.data.network.model.toStoreNetwork

fun Hit.toSearchable(indexName: String?): Searchable {
    return when (indexName) {
        ALGOLIA_SEARCH_PRODUCTS_INDEX -> toProductNetwork()
        ALGOLIA_SEARCH_STORES_INDEX -> toStoreNetwork()
        else -> throw IllegalArgumentException("Invalid index name: $indexName")
    }
}