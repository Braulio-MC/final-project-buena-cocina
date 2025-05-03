package com.bmc.buenacocina.domain

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isScrolledToEnd(): Boolean {
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}