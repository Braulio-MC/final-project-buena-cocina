package com.bmc.buenacocina.ui.screen.detailed.order

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.OrderStatus
import com.bmc.buenacocina.core.getOrderTotal
import com.bmc.buenacocina.ui.viewmodel.DetailedOrderViewModel
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedOrderScreen(
    windowSizeClass: WindowSizeClass,
    orderId: String,
    viewModel: DetailedOrderViewModel = hiltViewModel(
        creationCallback = { factory: DetailedOrderViewModel.DetailedOrderViewModelFactory ->
            factory.create(orderId)
        }
    ),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    onChannelCreatedSuccessful: (String) -> Unit,
    onOrderRating: (String) -> Unit,
    onBackButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val resultState = viewModel.resultState.collectAsStateWithLifecycle()
    val currentContext = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = currentContext) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailedOrderViewModel.DetailedOrderEvent.CreateChannelFailed -> {
                    snackbarHostState.showSnackbar(
                        message = "No se pudo crear el canal de mensajes",
                        withDismissAction = true
                    )
                }

                is DetailedOrderViewModel.DetailedOrderEvent.CreateChannelSuccess -> {
                    onChannelCreatedSuccessful(event.channelId)
                }
            }
        }
    }

    DetailedOrderScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        resultState = resultState.value,
        scrollState = scrollState,
        snackbarHostState = snackbarHostState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onRateOrderButton = { onOrderRating(orderId) },
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedOrderScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: DetailedOrderUiState,
    resultState: DetailedOrderUiResultState,
    scrollState: ScrollState,
    snackbarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (DetailedOrderIntent) -> Unit,
    onRateOrderButton: () -> Unit,
    onBackButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles del pedido",
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
                actions = {
                    IconButton(
                        onClick = { onIntent(DetailedOrderIntent.CreateChannel) },
                        enabled = uiState.order != null && !resultState.isWaitingForChannelResult
                    ) {
                        if (resultState.isWaitingForChannelResult) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Chat,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = { onRateOrderButton() },
                        enabled = uiState.order != null &&
                                uiState.order.status == OrderStatus.DELIVERED.status &&
                                !uiState.order.rated
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
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

        } else {
            if (uiState.order != null) {
                val gridItems = uiState.lines.take(4)
                val orderDesc = if (uiState.lines.size > 1)
                    "Multiples productos de ${uiState.order.store.name}"
                else
                    "Producto de ${uiState.order.store.name}"
                val createdAt = uiState.order.createdAt?.let {
                    DateUtils.localDateTimeToString(it)
                } ?: "No se pudo obtener la fecha"
                val updatedAt = uiState.order.updatedAt?.let {
                    DateUtils.localDateTimeToString(it)
                } ?: "No se pudo obtener la fecha"
                val orderTotal = getOrderTotal(uiState.lines).setScale(2, RoundingMode.HALF_DOWN)

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image grid here
                    Text(
                        text = orderDesc,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    )
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Creacion",
                                    textAlign = TextAlign.Start,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = createdAt,
                                    textAlign = TextAlign.End,
                                    color = Color.DarkGray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(end = 5.dp)
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Actualizacion",
                                    textAlign = TextAlign.Start,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = updatedAt,
                                    textAlign = TextAlign.End,
                                    color = Color.DarkGray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(end = 5.dp)
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Total",
                                    textAlign = TextAlign.Start,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = "$$orderTotal",
                                    textAlign = TextAlign.End,
                                    color = Color.DarkGray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(end = 5.dp)
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Direccion",
                                    textAlign = TextAlign.Start,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = uiState.order.deliveryLocation.name,
                                    textAlign = TextAlign.End,
                                    color = Color.DarkGray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(end = 5.dp)
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Metodo de pago",
                                    textAlign = TextAlign.Start,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = uiState.order.paymentMethod.name,
                                    textAlign = TextAlign.End,
                                    color = Color.DarkGray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(end = 5.dp)
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Estado",
                                    textAlign = TextAlign.Start,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = uiState.order.status,
                                    textAlign = TextAlign.End,
                                    color = Color.DarkGray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(end = 5.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "Productos en el pedido",
                        textAlign = TextAlign.Start,
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .fillMaxWidth()
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
                        items(uiState.lines) { line ->
                            DetailedOrderItem(line = line)
                        }
                    }
                }
            }
        }
    }
}