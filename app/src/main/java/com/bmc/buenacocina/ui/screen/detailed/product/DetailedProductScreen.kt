package com.bmc.buenacocina.ui.screen.detailed.product

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.domain.model.ProductReviewAnalyzedDomain
import com.bmc.buenacocina.ui.viewmodel.DetailedProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailedProductScreen(
    windowSizeClass: WindowSizeClass,
    productId: String,
    storeOwnerId: String,
    viewModel: DetailedProductViewModel = hiltViewModel(
        creationCallback = { factory: DetailedProductViewModel.DetailedProductViewModelFactory ->
            factory.create(productId, storeOwnerId)
        }
    ),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onProductAddedToCartSuccessful: () -> Unit,
    onTotalReviewsClick: (String) -> Unit,
    onBackButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val netState = viewModel.netState.collectAsStateWithLifecycle()
    val reviews = viewModel.reviews.collectAsLazyPagingItems()
    val currentContext = LocalContext.current
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val bringIntoViewRequesterForProductReviews = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = currentContext) {
        viewModel.events.collect { event ->
            when (event) {
                DetailedProductViewModel.DetailedProductEvent.ProductAddedToCartSuccess -> {
                    val result = snackBarHostState.showSnackbar(
                        message = "Producto actualizado o agregado al carrito",
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                        onProductAddedToCartSuccessful()
                    }
                }

                is DetailedProductViewModel.DetailedProductEvent.ProductAddedToCartFailed -> {
                    Log.e("DetailedProductScreen", "ProductAddedToCartFailed: ${event.error}")
                }

                is DetailedProductViewModel.DetailedProductEvent.CreateProductFavoriteFailed -> {

                }

                DetailedProductViewModel.DetailedProductEvent.CreateProductFavoriteSuccess -> {

                }

                is DetailedProductViewModel.DetailedProductEvent.DeleteProductFavoriteFailed -> {

                }

                DetailedProductViewModel.DetailedProductEvent.DeleteProductFavoriteSuccess -> {

                }
            }
        }
    }

    DetailedProductScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        netState = netState.value,
        reviews = reviews,
        snackbarHostState = snackBarHostState,
        bringIntoViewRequesterForProductReviews = bringIntoViewRequesterForProductReviews,
        coroutineScope = coroutineScope,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onTotalReviewsClick = onTotalReviewsClick,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailedProductScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: DetailedProductUiState,
    netState: NetworkStatus,
    reviews: LazyPagingItems<ProductReviewAnalyzedDomain>,
    snackbarHostState: SnackbarHostState,
    bringIntoViewRequesterForProductReviews: BringIntoViewRequester,
    coroutineScope: CoroutineScope,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (DetailedProductIntent) -> Unit,
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
                        text = "Detalles de producto",
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackButton() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_navigation_button)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        if (uiState.isLoadingProduct) {
            DetailedProductShimmer(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        } else {
            if (uiState.product != null) {
                val icon =
                    if (uiState.favorite != null) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                val color = if (uiState.favorite != null) Color.Red else Color.Black
                val price = uiState.product.price.setScale(2, RoundingMode.HALF_DOWN)
                var discountPercentage: BigDecimal? = null
                if (uiState.product.discount.startDate != null && uiState.product.discount.endDate != null) {
                    if (
                        uiState.product.discount.percentage > BigDecimal.ZERO &&
                        DateUtils.isInRange(
                            LocalDateTime.now(),
                            uiState.product.discount.startDate,
                            uiState.product.discount.endDate
                        )
                    ) {
                        discountPercentage =
                            uiState.product.discount.percentage.setScale(2, RoundingMode.HALF_DOWN)
                    }
                }
                val productRating =
                    uiState.product.rating.setScale(1, RoundingMode.HALF_DOWN).toPlainString()
                val totalRevs =
                    "${uiState.product.totalReviews} ${if (uiState.product.totalReviews == BigInteger.ONE) "reseña" else "reseñas"}"

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(bottom = 20.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uiState.product.image)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                            if (discountPercentage != null) {
                                DetailedProductDiscountItem(
                                    percentage = discountPercentage,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                )
                            }
                        }
                        Text(
                            text = uiState.product.name,
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, top = 5.dp, bottom = 15.dp, end = 5.dp)
                        )
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 10.dp
                            ),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .padding(bottom = 15.dp)
                                .size(320.dp, 70.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .fillMaxSize()
                                        .weight(1f)
                                        .clickable {
                                            coroutineScope.launch {
                                                bringIntoViewRequesterForProductReviews.bringIntoView()
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        text = productRating,
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
                                Text(
                                    text = "$$price",
                                    fontSize = 25.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                )
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
                                        onIntent(DetailedProductIntent.ToggleFavoriteProduct)
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
                            text = uiState.product.description,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(10.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, top = 10.dp, end = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reseñas de ${uiState.product.name}",
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
                                    .clickable { onTotalReviewsClick(uiState.product.id) }
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
                                        .bringIntoViewRequester(bringIntoViewRequesterForProductReviews)
                                ) {
                                    items(3) {
                                        DetailedProductReviewItemShimmer()
                                    }
                                }
                            }

                            is LoadState.NotLoading -> {
                                if (reviews.itemCount == 0) {
                                    DetailedProductEmptyReviews(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .bringIntoViewRequester(
                                                bringIntoViewRequesterForProductReviews
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
                                                bringIntoViewRequesterForProductReviews
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
                                                DetailedProductReviewItem(
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
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        ),
                        shape = RectangleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 5.dp
                                ),
                                modifier = Modifier
                                    .height(50.dp)
                                    .weight(0.5f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    IconButton(
                                        onClick = {
                                            onIntent(DetailedProductIntent.DecreaseProductCount())
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Remove,
                                            tint = Color.Black,
                                            contentDescription = ""
                                        )
                                    }
                                    Text(
                                        text = uiState.addToCartCount.toString(),
                                        fontSize = 19.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            onIntent(DetailedProductIntent.IncreaseProductCount())
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            tint = Color.Black,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Button(
                                onClick = {
                                    onIntent(DetailedProductIntent.AddToShoppingCart)
                                },
                                enabled = !uiState.isWaitingForAddToCartResult,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(50.dp)
                                    .weight(0.7f)
                            ) {
                                if (uiState.isWaitingForAddToCartResult) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Agregar al carrito",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
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