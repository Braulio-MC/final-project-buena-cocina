package com.bmc.buenacocina.ui.screen.category.restaurant

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bmc.buenacocina.R
import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import com.bmc.buenacocina.domain.model.ProductSearchDomain
import com.bmc.buenacocina.domain.model.StoreDomain
import com.bmc.buenacocina.ui.viewmodel.RestaurantCategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: RestaurantCategoryViewModel = hiltViewModel(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onSearchBarButton: () -> Unit,
    onStore: (String) -> Unit,
    onProductHitItemClick: (String, String) -> Unit,
    onBackButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val storesExplore = viewModel.storesExplore.collectAsLazyPagingItems()
    val productSearch = viewModel.productSearch.collectAsLazyPagingItems()

    StoreScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        storesExplore = storesExplore,
        productSearch = productSearch,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onSearchBarButton = onSearchBarButton,
        onStore = onStore,
        onProductHitItemClick = onProductHitItemClick,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: StoreUiState,
    storesExplore: LazyPagingItems<StoreDomain>,
    productSearch: LazyPagingItems<Searchable>,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (StoreIntent) -> Unit,
    onSearchBarButton: () -> Unit,
    onStore: (String) -> Unit,
    onProductHitItemClick: (String, String) -> Unit,
    onBackButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.restaurant_cat_screen_title),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onBackButton() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_navigation_button)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Button(
                onClick = {
                    onSearchBarButton()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.search_button_text),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_bar_icon_content_desc),
                        modifier = Modifier
                            .size(35.dp)
                    )
                }
            }
            if (uiState.isLoadingProductCategories) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(35.dp)
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                ) {
                    items(count = uiState.productCategories.size) { index ->
                        val category = uiState.productCategories[index]
                        ProductCategoryItem(
                            productCategory = category,
                            onClick = { productCategory ->
                                onIntent(StoreIntent.UpdateCurrentProductCategory(productCategory))
                            }
                        )
                    }
                }
            }
            if (uiState.selectedProductCategory != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .width(160.dp)
                            .height(40.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(3.dp)
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.selectedProductCategory.name,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                modifier = Modifier
                                    .size(35.dp),
                                onClick = { onIntent(StoreIntent.UpdateCurrentProductCategory()) }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear category search"
                                )
                            }
                        }
                    }
                }
                when (productSearch.loadState.refresh) {
                    is LoadState.Error -> {

                    }

                    LoadState.Loading -> {
                        LazyColumn(
                            modifier = Modifier
                                .padding(10.dp)
                                .heightIn(max = 1000.dp)
                        ) {
                            items(4) {
                                StoreProductCategorySearchItemShimmer()
                            }
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (productSearch.itemCount == 0) {
                            StoreProductCategorySearchEmpty(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 50.dp)
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
                                items(count = productSearch.itemCount) { index ->
                                    val hit = productSearch[index]
                                    if (hit != null) {
                                        when (hit.type) {
                                            SearchableTypes.PRODUCTS -> {
                                                val product = hit as ProductSearchDomain
                                                StoreProductCategorySearchItem(
                                                    hit = product,
                                                    onClick = { productId, storeOwnerId ->
                                                        onProductHitItemClick(
                                                            productId,
                                                            storeOwnerId
                                                        )
                                                    }
                                                )
                                            }

                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .height(250.dp)
                        .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.restaurant_cat_stores_best_rated),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    LazyRow {

                    }
                }
                Text(
                    text = stringResource(id = R.string.restaurant_cat_stores_explore),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth()
                )
                when (storesExplore.loadState.refresh) {
                    is LoadState.Error -> {

                    }

                    LoadState.Loading -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(count = 2),
                            modifier = Modifier
                                .padding(10.dp)
                                .heightIn(max = 1000.dp)
                        ) {
                            items(4) {
                                StoreItemShimmer()
                            }
                        }
                    }

                    is LoadState.NotLoading -> {
                        if (storesExplore.itemCount == 0) {
                            StoreEmpty(
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(count = 2),
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
                                    count = storesExplore.itemCount,
                                    key = storesExplore.itemKey { item -> item.id }
                                ) { index ->
                                    val store = storesExplore[index]
                                    if (store != null) {
                                        StoreItem(
                                            store = store,
                                            onClick = onStore
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