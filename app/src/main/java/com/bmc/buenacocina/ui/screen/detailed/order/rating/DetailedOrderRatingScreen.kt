package com.bmc.buenacocina.ui.screen.detailed.order.rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bmc.buenacocina.R
import com.bmc.buenacocina.ui.viewmodel.DetailedOrderRatingViewModel
import com.smarttoolfactory.ratingbar.RatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedOrderRatingScreen(
    windowSizeClass: WindowSizeClass,
    orderId: String,
    viewModel: DetailedOrderRatingViewModel = hiltViewModel(
        creationCallback = { factory: DetailedOrderRatingViewModel.DetailedOrderRatingViewModelFactory ->
            factory.create(orderId)
        }
    ),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onOrderRatingUpdatedSuccessful: () -> Unit,
    onBackButton: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val resultState = viewModel.resultState.collectAsStateWithLifecycle()
    val currentContext = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = currentContext) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailedOrderRatingViewModel.DetailedOrderRatingViewModelEvent.OrderItemRatingsFailed -> {

                }

                DetailedOrderRatingViewModel.DetailedOrderRatingViewModelEvent.OrderItemRatingsSuccess -> {

                }

                is DetailedOrderRatingViewModel.DetailedOrderRatingViewModelEvent.OrderRatingUpdatedFailed -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "La calificacion no se envio correctamente",
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {

                    }
                }

                DetailedOrderRatingViewModel.DetailedOrderRatingViewModelEvent.OrderRatingUpdatedSuccess -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "La calificacion se envio correctamente, gracias",
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                        onOrderRatingUpdatedSuccessful()
                    }
                }

                is DetailedOrderRatingViewModel.DetailedOrderRatingViewModelEvent.OrderStoreRatingFailed -> {

                }

                DetailedOrderRatingViewModel.DetailedOrderRatingViewModelEvent.OrderStoreRatingSuccess -> {

                }
            }
        }
    }

    DetailedOrderRatingScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        resultState = resultState.value,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedOrderRatingScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: DetailedOrderRatingUiState,
    resultState: DetailedOrderRatingUiResultState,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState,
    onIntent: (DetailedOrderRatingIntent) -> Unit,
    onBackButton: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calificar orden",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackButton() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (uiState.order != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.order_rating_store),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Calificacion para ${uiState.order.store.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                RatingBar(
                    rating = resultState.storeRating.rating,
                    imageVectorEmpty = Icons.Outlined.StarBorder,
                    imageVectorFilled = Icons.Outlined.Star,
                    space = 2.dp,
                    itemSize = 35.dp,
                    tintEmpty = colorResource(id = R.color.rating_bar_empty),
                    tintFilled = colorResource(id = R.color.rating_bar_filled),
                    onRatingChange = { newRating ->
                        onIntent(DetailedOrderRatingIntent.StoreRatingChanged(newRating))
                    },
                    modifier = Modifier
                        .padding(7.dp)
                )
                if (resultState.storeRatingError != null) {
                    Text(
                        text = resultState.storeRatingError.asString(),
                        textAlign = TextAlign.End,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    )
                }
                TextField(
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 5.dp)
                        .fillMaxWidth()
                        .height(140.dp),
                    value = resultState.storeRating.comment,
                    onValueChange = { newComment ->
                        onIntent(DetailedOrderRatingIntent.StoreCommentChanged(newComment))
                    },
                    placeholder = {
                        Text(text = "Comentario")
                    },
                    isError = resultState.storeCommentError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                if (resultState.storeCommentError != null) {
                    Text(
                        text = resultState.storeCommentError.asString(),
                        textAlign = TextAlign.End,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Califica los productos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                    items(uiState.lines.size) { index ->
                        val line = uiState.lines[index]
                        val productRating = resultState.itemRatings.find { rating ->
                            rating.item.productId == line.product.id
                        }
                        if (productRating != null) {
                            DetailedOrderRatingItem(
                                line = line,
                                productRating = productRating,
                                onItemRatingChange = { productId, newRating ->
                                    onIntent(
                                        DetailedOrderRatingIntent.ItemRatingChanged(
                                            productId,
                                            newRating
                                        )
                                    )
                                },
                                onItemCommentChange = { productId, newComment ->
                                    onIntent(
                                        DetailedOrderRatingIntent.ItemCommentChanged(
                                            productId,
                                            newComment
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        onIntent(DetailedOrderRatingIntent.Submit)
                    },
                    enabled = !uiState.order.rated,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                        .size(width = 170.dp, height = 45.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (resultState.isWaitingForStoreRatingResult
                        && resultState.isWaitingForItemRatingsResult
                        && resultState.isWaitingForOverallRatingResult
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(20.dp),
                        )
                    } else {
                        Text(
                            text = "Calificar",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}