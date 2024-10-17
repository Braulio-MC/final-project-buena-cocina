package com.bmc.buenacocina.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bmc.buenacocina.core.STORE_REVIEW_COLLECTION_NAME
import com.bmc.buenacocina.data.network.model.StoreReviewNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class StoreReviewPagingSource(
    private val query: (Query) -> Query = { it },
    private val firestore: FirebaseFirestore
) : PagingSource<QuerySnapshot, StoreReviewNetwork>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, StoreReviewNetwork>): QuerySnapshot? =
        null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, StoreReviewNetwork> =
        try {
            val ref = firestore.collection(STORE_REVIEW_COLLECTION_NAME)
            val q = query(ref)
            val currentPage = params.key ?: q.get().await()
            val lastVisibleDoc = currentPage.documents.lastOrNull()
            val nextPage =
                if (lastVisibleDoc == null) null else q.startAfter(lastVisibleDoc).get().await()
            LoadResult.Page(
                data = currentPage.toObjects(StoreReviewNetwork::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}