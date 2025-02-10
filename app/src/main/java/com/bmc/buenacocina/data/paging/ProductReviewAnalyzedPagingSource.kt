package com.bmc.buenacocina.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bmc.buenacocina.data.network.model.ProductReviewAnalyzedNetwork
import com.bmc.buenacocina.data.network.service.ProductReviewAnalyzedService
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProductReviewAnalyzedPagingSource(
    private val service: ProductReviewAnalyzedService,
    private val productId: String,
    private val start: LocalDateTime?,
    private val end: LocalDateTime?
) : PagingSource<String, ProductReviewAnalyzedNetwork>() {
    override fun getRefreshKey(state: PagingState<String, ProductReviewAnalyzedNetwork>): String? =
        null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ProductReviewAnalyzedNetwork> {
        val cursor = params.key
        return try {
            val startStr = start?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val endStr = end?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val response = service.pagingByProductIdWithRange(
                productId = productId,
                limit = params.loadSize,
                nextCursor = cursor,
                start = startStr,
                end = endStr
            )
            var items = emptyList<ProductReviewAnalyzedNetwork>()
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