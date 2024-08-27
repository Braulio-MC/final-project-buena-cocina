package com.bmc.buenacocina.data.network.model

data class _PagingResultNetwork<T: Any>(
    val data: T,
    val pagination: PagingResultPaginationNetwork
) {
    data class PagingResultPaginationNetwork(
        val prev: String?,
        val next: String?
    )
}
