package com.bmc.buenacocina.ui.screen.storefavorite

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
import com.bmc.buenacocina.domain.model.StoreFavoriteDomain
import com.bmc.buenacocina.ui.viewmodel.StoreFavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFavoriteScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: StoreFavoriteViewModel = hiltViewModel(),
    scrollState: ScrollState = rememberScrollState(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onStoreFavoriteClick: (String) -> Unit,
    onBackButton: () -> Unit,
) {
    val storeFavorites = viewModel.storeFavorites.collectAsLazyPagingItems()

    StoreFavoriteScreenContent(
        windowSizeClass = windowSizeClass,
        storeFavorites = storeFavorites,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onStoreFavoriteClick = onStoreFavoriteClick,
        onBackButton = onBackButton,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFavoriteScreenContent(
    windowSizeClass: WindowSizeClass,
    storeFavorites: LazyPagingItems<StoreFavoriteDomain>,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onStoreFavoriteClick: (String) -> Unit,
    onBackButton: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tiendas favoritas",
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
            when (storeFavorites.loadState.refresh) {
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
                            StoreFavoriteItemShimmer()
                        }
                    }
                }

                is LoadState.NotLoading -> {
                    if (storeFavorites.itemCount == 0) {
                        StoreFavoriteEmpty(paddingValues)
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
                                count = storeFavorites.itemCount,
                                key = storeFavorites.itemKey { item ->
                                    item.id
                                }
                            ) { index ->
                                val storeFavorite = storeFavorites[index]
                                if (storeFavorite != null) {
                                    StoreFavoriteItem(
                                        storeFavorite = storeFavorite,
                                        onClick = onStoreFavoriteClick
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