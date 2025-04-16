package com.bmc.buenacocina.ui.screen.orderhistory

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import com.bmc.buenacocina.core.OrderHistorySearchChips
import com.bmc.buenacocina.domain.model.OrderDomain
import com.bmc.buenacocina.domain.model.OrderSearchDomain
import com.bmc.buenacocina.ui.viewmodel.OrderHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: OrderHistoryViewModel = hiltViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onOrderItemClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val orders = viewModel.orders.collectAsLazyPagingItems()
    val ordersSearch =
        viewModel.orderHits.collectAsStateWithLifecycle().value?.collectAsLazyPagingItems()
    var ordersStartedLoading by remember { mutableStateOf(false) }

    LaunchedEffect(orders.loadState.refresh) {
        if (orders.loadState.refresh is LoadState.Loading) {
            ordersStartedLoading = true
        }
    }

    OrderHistoryScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        orders = orders,
        ordersSearch = ordersSearch,
        ordersStartedLoading = ordersStartedLoading,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onOrderItemClick = onOrderItemClick,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: OrderHistoryUiState,
    orders: LazyPagingItems<OrderDomain>,
    ordersSearch: LazyPagingItems<Searchable>?,
    ordersStartedLoading: Boolean,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (OrderHistoryIntent) -> Unit,
    onOrderItemClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial",
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackButton() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back navigation"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = uiState.searchQuery,
                        onQueryChange = { newQuery ->
                            onIntent(OrderHistoryIntent.UpdateSearchQuery(newQuery))
                        },
                        onSearch = { onIntent(OrderHistoryIntent.Search) },
                        expanded = false,
                        onExpandedChange = {},
                        enabled = true,
                        placeholder = {
                            Text(
                                text = "Busca entre tus pedidos",
                                maxLines = 1,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W400
                            )
                        },
                        trailingIcon = {
                            Row(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(60.dp, 40.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onIntent(OrderHistoryIntent.ClearSearch) },
                                    enabled = uiState.searchQuery.isNotEmpty(),
                                    modifier = Modifier
                                        .size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search"
                                    )
                                }
                                IconButton(
                                    onClick = { onIntent(OrderHistoryIntent.Search) },
                                    enabled = uiState.searchQuery.isNotEmpty(),
                                    modifier = Modifier
                                        .size(35.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search button"
                                    )
                                }
                            }
                        }
                    )
                },
                expanded = false,
                onExpandedChange = {},
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                ),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {}
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                items(OrderHistorySearchChips.entries) { entry ->
                    AssistChip(
                        onClick = {
                            onIntent(OrderHistoryIntent.UpdateSearchQuery(entry.tag))
                            onIntent(OrderHistoryIntent.Search)
                        },
                        label = {
                            Text(
                                text = entry.tag,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.W400,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            if (ordersSearch != null) {
                when (ordersSearch.loadState.refresh) {
                    is LoadState.Error -> {

                    }

                    LoadState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(45.dp)
                            )
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (ordersSearch.itemCount == 0) {
                            OrderHistorySearchEmpty(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(horizontal = 10.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .heightIn(max = 1000.dp)
                                    .nestedScroll(connection = object : NestedScrollConnection {
                                        override fun onPreScroll(
                                            available: Offset,
                                            source: NestedScrollSource
                                        ): Offset {
                                            if (scrollState.canScrollForward && available.y < 0) {
                                                val consumed =
                                                    scrollState.dispatchRawDelta(-available.y)
                                                return Offset(x = 0f, y = -consumed)
                                            }
                                            return Offset.Zero
                                        }
                                    })
                            ) {
                                items(
                                    count = ordersSearch.itemCount,
                                    key = orders.itemKey { item ->
                                        item.id
                                    }
                                ) { index ->
                                    val hit = ordersSearch[index]
                                    when (hit?.type) {
                                        SearchableTypes.ORDERS -> {
                                            val order = hit as OrderSearchDomain
                                            OrderHistorySearchItem(
                                                order = order,
                                                onClick = onOrderItemClick
                                            )
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                when (orders.loadState.refresh) {
                    is LoadState.Error -> {

                    }

                    LoadState.Loading -> {
                        LazyColumn(
                            modifier = Modifier
                                .padding(10.dp)
                                .heightIn(max = 1000.dp)
                        ) {
                            items(5) {
                                OrderHistoryItemShimmer()
                            }
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (orders.itemCount == 0 && ordersStartedLoading) {
                            OrderHistoryEmpty(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .padding(horizontal = 10.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .heightIn(max = 1000.dp)
                                    .nestedScroll(connection = object : NestedScrollConnection {
                                        override fun onPreScroll(
                                            available: Offset,
                                            source: NestedScrollSource
                                        ): Offset {
                                            if (scrollState.canScrollForward && available.y < 0) {
                                                val consumed =
                                                    scrollState.dispatchRawDelta(-available.y)
                                                return Offset(x = 0f, y = -consumed)
                                            }
                                            return Offset.Zero
                                        }
                                    })
                            ) {
                                items(
                                    count = orders.itemCount,
                                    key = orders.itemKey { item ->
                                        item.id
                                    }
                                ) { index ->
                                    val order = orders[index]
                                    if (order != null) {
                                        OrderHistoryItem(
                                            order = order,
                                            onClick = onOrderItemClick
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
