package com.bmc.buenacocina.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.algolia.client.api.SearchClient
import com.algolia.client.model.search.SearchParamsObject
import com.bmc.buenacocina.core.SEARCH_MULTI_INDEX_HITS_PER_PAGE
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import com.bmc.buenacocina.core.SEARCH_PAGING_HITS_PER_PAGE
import com.bmc.buenacocina.data.network.model.ProductSearchNetwork
import com.bmc.buenacocina.data.network.model.StoreSearchNetwork
import com.bmc.buenacocina.data.network.service.SearchService
import com.bmc.buenacocina.data.paging.SearchPagingSource
import com.bmc.buenacocina.domain.mapper.asDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: SearchService,
    private val algoliaClient: SearchClient
) {
    fun searchMultiIndex(
        query: String,
        indexNames: List<String>,
        hitsPerPage: Int = SEARCH_MULTI_INDEX_HITS_PER_PAGE
    ): Flow<List<Searchable>> {
        val response = service.searchMultiIndex(query, indexNames, hitsPerPage)
        return response.map { list ->
            list.map { searchable ->
                when (searchable.type) {
                    SearchableTypes.PRODUCTS -> (searchable as ProductSearchNetwork).asDomain()
                    SearchableTypes.STORES -> (searchable as StoreSearchNetwork).asDomain()
                }
            }
        }
    }

    fun paging(
        query: String,
        indexName: String,
        filters: String? = null
    ): Flow<PagingData<Searchable>> {
        return Pager(
            config = PagingConfig(
                pageSize = SEARCH_PAGING_HITS_PER_PAGE
            ),
            pagingSourceFactory = {
                val paramsObject = SearchParamsObject(
                    query = query,
                    filters = filters
                )
                SearchPagingSource(paramsObject, indexName, algoliaClient)
            }
        ).flow.map { pagingData ->
            pagingData.map { searchable ->
                when (searchable.type) {
                    SearchableTypes.PRODUCTS -> (searchable as ProductSearchNetwork).asDomain()
                    SearchableTypes.STORES -> (searchable as StoreSearchNetwork).asDomain()
                }
            }
        }
    }
}