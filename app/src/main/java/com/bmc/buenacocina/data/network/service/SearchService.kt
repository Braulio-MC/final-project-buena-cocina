package com.bmc.buenacocina.data.network.service

import com.algolia.client.api.SearchClient
import com.algolia.client.model.search.SearchForHits
import com.algolia.client.model.search.SearchMethodParams
import com.algolia.client.model.search.SearchResponse
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_PRODUCTS_INDEX
import com.bmc.buenacocina.core.ALGOLIA_SEARCH_STORES_INDEX
import com.bmc.buenacocina.core.SEARCH_MULTI_INDEX_HITS_PER_PAGE
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.data.network.model.toProductNetwork
import com.bmc.buenacocina.data.network.model.toStoreNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchService @Inject constructor(
    private val algoliaClient: SearchClient
) {
    fun searchMultiIndex(
        query: String,
        indexNames: List<String>,
        hitsPerPage: Int = SEARCH_MULTI_INDEX_HITS_PER_PAGE
    ): Flow<List<Searchable>> = flow {
        val requests = indexNames.map { name ->
            SearchForHits(
                indexName = name,
                query = query,
                hitsPerPage = hitsPerPage
            )
        }
        val response = algoliaClient.search(
            searchMethodParams = SearchMethodParams(
                requests = requests
            )
        )
        val hits = response.results.flatMap { searchResponse ->
            (searchResponse as SearchResponse).hits.map { hit ->
                when (searchResponse.index) {
                    ALGOLIA_SEARCH_PRODUCTS_INDEX -> hit.toProductNetwork()
                    ALGOLIA_SEARCH_STORES_INDEX -> hit.toStoreNetwork()
                    else -> throw IllegalArgumentException("Invalid index name")
                }
            }
        }
        emit(hits)
    }
}