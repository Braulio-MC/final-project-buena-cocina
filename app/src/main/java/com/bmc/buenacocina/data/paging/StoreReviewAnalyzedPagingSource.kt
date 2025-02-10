package com.bmc.buenacocina.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bmc.buenacocina.data.network.model.StoreReviewAnalyzedNetwork
import com.bmc.buenacocina.data.network.service.StoreReviewAnalyzedService
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StoreReviewAnalyzedPagingSource(
    private val service: StoreReviewAnalyzedService,
    private val storeId: String,
    private val start: LocalDateTime?,
    private val end: LocalDateTime?
) : PagingSource<String, StoreReviewAnalyzedNetwork>() {
    override fun getRefreshKey(state: PagingState<String, StoreReviewAnalyzedNetwork>): String? =
        null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, StoreReviewAnalyzedNetwork> {
        val cursor = params.key
        return try {
            val startStr = start?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val endStr = end?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val response = service.getByStoreIdWithRange(
                storeId = storeId,
                limit = params.loadSize,
                nextCursor = cursor,
                start = startStr,
                end = endStr
            )
            var items = emptyList<StoreReviewAnalyzedNetwork>()
            var nextCursor: String? = null
            response.suspendOnSuccess {
                items = data.reviews
                nextCursor = data.pagination.next
            }.onException {
                throw throwable
            }
            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = nextCursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}