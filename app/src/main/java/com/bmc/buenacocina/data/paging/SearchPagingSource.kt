package com.bmc.buenacocina.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.algolia.client.api.SearchClient
import com.algolia.client.model.search.SearchParamsObject
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.domain.toSearchable

class SearchPagingSource(
    private val searchParamsObject: SearchParamsObject,
    private val indexName: String,
    private val client: SearchClient
) : PagingSource<Int, Searchable>() {
    override fun getRefreshKey(state: PagingState<Int, Searchable>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Searchable> {
        val page = params.key ?: 0
        val hitsPerPage = params.loadSize

        return try {
            val searchParams = searchParamsObject.copy(
                page = page,
                hitsPerPage = hitsPerPage
            )
            val response = client.searchSingleIndex(
                indexName = indexName,
                searchParams = searchParams
            )
            val hits = response.hits.map { it.toSearchable(indexName) }
            LoadResult.Page(
                data = hits,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (hits.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}