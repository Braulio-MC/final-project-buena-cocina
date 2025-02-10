package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.core.SEARCH_MULTI_INDEX_HITS_PER_PAGE
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import com.bmc.buenacocina.data.network.model.ProductSearchNetwork
import com.bmc.buenacocina.data.network.model.StoreSearchNetwork
import com.bmc.buenacocina.data.network.service.SearchService
import com.bmc.buenacocina.domain.mapper.asDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val service: SearchService,
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
}