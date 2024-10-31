package com.bmc.buenacocina.ui.screen.productfavorite

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bmc.buenacocina.domain.model.ProductFavoriteDomain
import com.bmc.buenacocina.ui.viewmodel.ProductFavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFavoriteScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: ProductFavoriteViewModel = hiltViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onProductFavoriteClick: (String, String) -> Unit,
    onBackButton: () -> Unit,
) {
    val productFavorites = viewModel.productFavorites.collectAsLazyPagingItems()

    ProductFavoriteScreenContent(
        windowSizeClass = windowSizeClass,
        productFavorites = productFavorites,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onProductFavoriteClick = onProductFavoriteClick,
        onBackButton = onBackButton,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFavoriteScreenContent(
    windowSizeClass: WindowSizeClass,
    productFavorites: LazyPagingItems<ProductFavoriteDomain>,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onProductFavoriteClick: (String, String) -> Unit,
    onBackButton: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Productos favoritos",
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
                .verticalScroll(scrollState)
        ) {
            when (productFavorites.loadState.refresh) {
                is LoadState.Error -> {

                }

                LoadState.Loading -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(count = 2),
                        modifier = Modifier
                            .padding(10.dp)
                            .heightIn(max = 1000.dp)
                    ) {
                        items(6) {
                            ProductFavoriteItemShimmer()
                        }
                    }
                }

                is LoadState.NotLoading -> {
                    if (productFavorites.itemCount == 0) {
                        ProductFavoriteEmpty(paddingValues)
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
                                count = productFavorites.itemCount,
                                key = productFavorites.itemKey { item ->
                                    item.id
                                }
                            ) { index ->
                                val productFavorite = productFavorites[index]
                                if (productFavorite != null) {
                                    ProductFavoriteItem(
                                        productFavorite = productFavorite,
                                        onClick = onProductFavoriteClick
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