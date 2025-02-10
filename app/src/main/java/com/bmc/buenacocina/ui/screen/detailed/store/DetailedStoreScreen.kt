package com.bmc.buenacocina.ui.screen.detailed.store

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.domain.model.ProductDomain
import com.bmc.buenacocina.domain.model.StoreReviewAnalyzedDomain
import com.bmc.buenacocina.ui.viewmodel.DetailedStoreViewModel
import com.bmc.buenacocina.ui.viewmodel.DetailedStoreViewModel.DetailedStoreViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailedStoreScreen(
    windowSizeClass: WindowSizeClass,
    storeId: String,
    viewModel: DetailedStoreViewModel = hiltViewModel(
        creationCallback = { factory: DetailedStoreViewModelFactory ->
            factory.create(storeId)
        }
    ),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onProductClick: (String, String) -> Unit,
    onTotalReviewsClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val products = viewModel.products.collectAsLazyPagingItems()
    val reviews = viewModel.reviews.collectAsLazyPagingItems()
    val netState = viewModel.netState.collectAsStateWithLifecycle()
    val bringIntoViewRequesterForStoreReviews = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    DetailedStoreScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        netState = netState.value,
        products = products,
        reviews = reviews,
        bringIntoViewRequesterForStoreReviews = bringIntoViewRequesterForStoreReviews,
        coroutineScope = coroutineScope,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onProductClick = onProductClick,
        onTotalReviewsClick = onTotalReviewsClick,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailedStoreScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: DetailedStoreUiState,
    netState: NetworkStatus,
    products: LazyPagingItems<ProductDomain>,
    reviews: LazyPagingItems<StoreReviewAnalyzedDomain>,
    bringIntoViewRequesterForStoreReviews: BringIntoViewRequester,
    coroutineScope: CoroutineScope,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (DetailedStoreIntent) -> Unit,
    onProductClick: (String, String) -> Unit,
    onTotalReviewsClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.store?.name ?: "Detalles de tienda",
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
        if (uiState.isLoadingStore) {
            DetailedStoreShimmer(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        } else {
            if (uiState.store != null) {
                val icon =
                    if (uiState.favorite != null) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                val color = if (uiState.favorite != null) Color.Red else Color.Black
                val storeRating =
                    uiState.store.rating.setScale(1, RoundingMode.HALF_DOWN).toPlainString()
                val totalRevs =
                    "${uiState.store.totalReviews} ${if (uiState.store.totalReviews == BigInteger.ONE) "reseña" else "reseñas"}"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uiState.store.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    Text(
                        text = uiState.store.name,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        ),
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 20.dp)
                            .size(200.dp, 70.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxSize()
                                    .weight(1f)
                                    .clickable {
                                        coroutineScope.launch {
                                            bringIntoViewRequesterForStoreReviews.bringIntoView()
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = storeRating,
                                    fontSize = 25.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "",
                                    tint = colorResource(id = R.color.rating_bar_filled),
                                    modifier = Modifier
                                        .size(35.dp)
                                        .weight(1f)
                                )
                            }
                            VerticalDivider(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(55.dp)
                                    .background(Color.Gray)
                            )
                            IconButton(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                enabled = !uiState.isWaitingForFavoriteResult,
                                onClick = {
                                    onIntent(DetailedStoreIntent.ToggleFavoriteStore)
                                }
                            ) {
                                if (uiState.isWaitingForFavoriteResult || uiState.isLoadingFavorite) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "",
                                        tint = color,
                                        modifier = Modifier
                                            .size(35.dp)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Productos de ${uiState.store.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .fillMaxWidth()
                    )
                    when (products.loadState.refresh) {
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
                                    DetailedStoreItemShimmer()
                                }
                            }
                        }

                        is LoadState.NotLoading -> {
                            if (products.itemCount == 0) {
                                DetailedStoreEmptyProducts(
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
                                        count = products.itemCount,
                                        key = products.itemKey { item ->
                                            item.id
                                        }
                                    ) { index ->
                                        val product = products[index]
                                        if (product != null) {
                                            DetailedStoreItem(
                                                product = product,
                                                uiState.store.userId,
                                                onClick = onProductClick
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reseñas de ${uiState.store.name}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1f)
                        )
                        Text(
                            text = totalRevs,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Light,
                            fontStyle = FontStyle.Italic,
                            color = Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .weight(0.5f)
                                .padding(end = 5.dp)
                                .clickable { onTotalReviewsClick(uiState.store.id) }
                        )
                    }
                    when (reviews.loadState.refresh) {
                        is LoadState.Error -> {

                        }

                        LoadState.Loading -> {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .heightIn(max = 200.dp)
                                    .bringIntoViewRequester(bringIntoViewRequesterForStoreReviews)
                            ) {
                                items(3) {
                                    DetailedStoreReviewItemShimmer()
                                }
                            }
                        }

                        is LoadState.NotLoading -> {
                            if (reviews.itemCount == 0) {
                                DetailedStoreEmptyReviews(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .bringIntoViewRequester(
                                            bringIntoViewRequesterForStoreReviews
                                        )
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .heightIn(max = 200.dp)
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
                                        .bringIntoViewRequester(
                                            bringIntoViewRequesterForStoreReviews
                                        )
                                ) {
                                    items(
                                        count = reviews.itemCount,
                                        key = reviews.itemKey { item ->
                                            item.id
                                        }
                                    ) { index ->
                                        val review = reviews[index]
                                        if (review != null) {
                                            DetailedStoreReviewItem(
                                                rating = review.rating,
                                                comment = review.comment,
                                                sentiment = review.sentiment,
                                                updatedAt = review.updatedAt
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {

            }
        }
    }
}
