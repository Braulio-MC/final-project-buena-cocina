package com.bmc.buenacocina.ui.screen.detailed.order

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.OrderStatus
import com.bmc.buenacocina.domain.LocationPermissionTextProvider
import com.bmc.buenacocina.domain.getActivity
import com.bmc.buenacocina.domain.hasLocationPermissionFlow
import com.bmc.buenacocina.domain.mapper.asLatLng
import com.bmc.buenacocina.ui.openAppSettings
import com.bmc.buenacocina.ui.screen.common.LocationPermissionDialog
import com.bmc.buenacocina.ui.viewmodel.DetailedOrderViewModel

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
    val locationPermissionQueue by viewModel.visiblePermissionDialogQueue.collectAsStateWithLifecycle()
    val currentContext = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var showOrderLocationDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val locationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                isGranted = isGranted
            )
        }
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    val isForeground = remember { mutableStateOf(false) }

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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isForeground.value = event == Lifecycle.Event.ON_RESUME
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isForeground.value, showOrderLocationDialog) {
        if (isForeground.value && showOrderLocationDialog) {
            currentContext.hasLocationPermissionFlow().collect { hasLocationPermission ->
                if (hasLocationPermission && showOrderLocationDialog) {
                    viewModel.startLocationUpdates()
                } else {
                    viewModel.stopLocationUpdates()
                }
            }
        } else {
            viewModel.stopLocationUpdates()
        }
    }

    locationPermissionQueue
        .reversed()
        .forEach { permission ->
            LocationPermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.ACCESS_FINE_LOCATION -> LocationPermissionTextProvider()
                    else -> return@forEach
                },
                isPermanentlyDeclined = if (currentContext.getActivity() != null) {
                    !shouldShowRequestPermissionRationale(
                        currentContext.getActivity()!!,
                        permission
                    )
                } else false,
                onDismiss = viewModel::dismissPermissionDialog,
                onOkClick = {
                    viewModel.dismissPermissionDialog()
                    locationPermissionResultLauncher.launch(permission)
                },
                onGoToAppSettingsClick = if (currentContext.getActivity() != null) {
                    { currentContext.getActivity()!!.openAppSettings() }
                } else {
                    {}
                },
            )
        }

    if (uiState.value.cuceiCenterOnMap != null && uiState.value.cuceiAreaBoundsOnMap != null) {
        DetailedOrderLocationDialog(
            isDialogOpen = showOrderLocationDialog,
            isLoadingUserLocation = uiState.value.isLoadingUserLocation,
            cuceiCenter = uiState.value.cuceiCenterOnMap!!,
            cuceiBounds = uiState.value.cuceiAreaBoundsOnMap!!,
            orderLocation = uiState.value.order?.deliveryLocation?.asLatLng(),
            currentUserLocation = uiState.value.userLocation,
            onDismiss = { showOrderLocationDialog = false }
        )
    }

    DetailedOrderScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        scrollState = scrollState,
        snackbarHostState = snackbarHostState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onRateOrderButton = { onOrderRating(orderId) },
        onShowLocationOnMapButton = {
            showOrderLocationDialog = true
            locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        },
        onBackButton = onBackButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedOrderScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: DetailedOrderUiState,
    scrollState: ScrollState,
    snackbarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (DetailedOrderIntent) -> Unit,
    onRateOrderButton: () -> Unit,
    onShowLocationOnMapButton: () -> Unit,
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
                        enabled = uiState.order != null && !uiState.isWaitingForChannelResult
                    ) {
                        if (uiState.isWaitingForChannelResult) {
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
        if (uiState.isLoadingOrder) {

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

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(3.dp),
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(200.dp)
                    ) {

                    }
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
                                if (uiState.isCalculatingOrderTotal) {
                                    Text(
                                        text = "Cargando...",
                                        textAlign = TextAlign.End,
                                        color = Color.DarkGray,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Light,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(2f)
                                            .padding(end = 5.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$${uiState.orderTotal}",
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
                                    text = "Ver en el mapa",
                                    textAlign = TextAlign.End,
                                    color = Color.DarkGray,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.W500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(2f)
                                        .padding(end = 5.dp)
                                        .clickable { onShowLocationOnMapButton() }
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