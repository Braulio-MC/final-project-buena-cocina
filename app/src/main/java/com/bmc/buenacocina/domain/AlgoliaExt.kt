package com.bmc.buenacocina.domain

import com.algolia.client.model.search.Hit
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_ORDERS_INDEX
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_PRODUCTS_INDEX
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_STORES_INDEX
import com.bmc.buenacocina.data.network.model.OrderSearchNetwork
import com.bmc.buenacocina.data.network.model.ProductSearchNetwork
import com.bmc.buenacocina.data.network.model.StoreSearchNetwork
import com.bmc.buenacocina.data.network.model.toProductNetwork
import com.bmc.buenacocina.data.network.model.toStoreNetwork
import com.bmc.buenacocina.data.network.model.toOrderNetwork
import com.bmc.buenacocina.domain.mapper.asDomain

fun Hit.toSearchable(indexName: String?): Searchable {
    return when (indexName) {
        ALGOLIA_SEARCH_PRODUCTS_INDEX -> toProductNetwork()
        ALGOLIA_SEARCH_STORES_INDEX -> toStoreNetwork()
        ALGOLIA_SEARCH_ORDERS_INDEX -> toOrderNetwork()
        else -> throw IllegalArgumentException("Invalid index name: $indexName")
    }
}

fun Searchable.toDomain(): Searchable {
    return when (this.type) {
        SearchableTypes.PRODUCTS -> (this as ProductSearchNetwork).asDomain()
        SearchableTypes.STORES -> (this as StoreSearchNetwork).asDomain()
        SearchableTypes.ORDERS -> (this as OrderSearchNetwork).asDomain()
    }
}