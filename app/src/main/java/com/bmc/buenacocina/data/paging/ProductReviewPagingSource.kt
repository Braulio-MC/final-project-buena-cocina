package com.bmc.buenacocina.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bmc.buenacocina.core.PRODUCT_REVIEW_COLLECTION_NAME
import com.bmc.buenacocina.data.network.model.ProductReviewNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class ProductReviewPagingSource(
    private val query: (Query) -> Query = { it },
    private val firestore: FirebaseFirestore
) : PagingSource<QuerySnapshot, ProductReviewNetwork>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, ProductReviewNetwork>): QuerySnapshot? =
        null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ProductReviewNetwork> =
        try {
            val ref = firestore.collection(PRODUCT_REVIEW_COLLECTION_NAME)
            val q = query(ref)
            val currentPage = params.key ?: q.get().await()
            val lastVisibleDoc = currentPage.documents.lastOrNull()
            val nextPage =
                if (lastVisibleDoc == null) null else q.startAfter(lastVisibleDoc).get().await()
            LoadResult.Page(
                data = currentPage.toObjects(ProductReviewNetwork::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}