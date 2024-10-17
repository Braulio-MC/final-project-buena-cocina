package com.bmc.buenacocina.ui.screen.detailed.product

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.ui.viewmodel.DetailedProductViewModel
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
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
    onBackButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val resultState = viewModel.resultState.collectAsStateWithLifecycle()
    val netState = viewModel.netState.collectAsStateWithLifecycle()
    val currentContext = LocalContext.current
    val snackBarHostState = remember {
        SnackbarHostState()
    }

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
        resultState = resultState.value,
        netState = netState.value,
        snackbarHostState = snackBarHostState,
        scrollState = scrollState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedProductScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: DetailedProductUiState,
    resultState: DetailedProductUiResultState,
    netState: NetworkStatus,
    snackbarHostState: SnackbarHostState,
    scrollState: ScrollState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (DetailedProductIntent) -> Unit,
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
        if (uiState.isLoading) {
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

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                .padding(bottom = 20.dp)
                                .fillMaxWidth()
                                .height(250.dp)
                        )
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
                                        .weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        text = "N/A",
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
                                        tint = Color.Yellow,
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
                                    enabled = !resultState.isWaitingForFavoriteResult,
                                    onClick = {
                                        onIntent(DetailedProductIntent.ToggleFavoriteProduct)
                                    }
                                ) {
                                    if (resultState.isWaitingForFavoriteResult) {
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
                                        text = resultState.addToCartCount.toString(),
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
                                enabled = !resultState.isWaitingForAddToCartResult,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(50.dp)
                                    .weight(0.7f)
                            ) {
                                if (resultState.isWaitingForAddToCartResult) {
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