package com.bmc.buenacocina.ui.screen.shoppingcart

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.domain.LocationPermissionTextProvider
import com.bmc.buenacocina.domain.getActivity
import com.bmc.buenacocina.domain.hasLocationPermissionFlow
import com.bmc.buenacocina.ui.openAppSettings
import com.bmc.buenacocina.ui.screen.common.LocationPermissionDialog
import com.bmc.buenacocina.ui.screen.shoppingcart.paymentmethod.PaymentMethodBottomSheet
import com.bmc.buenacocina.ui.viewmodel.ShoppingCartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: ShoppingCartViewModel = hiltViewModel(),
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState),
    paymentMethodSheetState: SheetState = rememberModalBottomSheetState(),
    emptyShoppingCartSheetState: SheetState = rememberModalBottomSheetState(),
    onBackButton: () -> Unit,
    onExploreStoresButton: () -> Unit,
    onSuccessfulOrderCreated: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val locationPermissionQueue by viewModel.visiblePermissionDialogQueue.collectAsStateWithLifecycle()
    val currentContext = LocalContext.current
    val paymentMethods = viewModel.paymentMethods.collectAsLazyPagingItems()
    val netState = viewModel.netState.collectAsStateWithLifecycle()
    var showDeliveryLocationDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showPaymentMethodBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    var showEmptyShoppingCartBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
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
                is ShoppingCartViewModel.ShoppingCartViewModelEvent.OrderFailed -> {
                    Log.e("ShoppingCartScreen", "Order failed ${event.error}")
                }

                ShoppingCartViewModel.ShoppingCartViewModelEvent.OrderSuccess -> {
                    val result =
                        snackbarHostState.showSnackbar(
                            message = "Orden creada",
                            withDismissAction = true
                        )
                    if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                        onSuccessfulOrderCreated()
                    }
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

    LaunchedEffect(isForeground.value, showDeliveryLocationDialog) {
        if (isForeground.value && showDeliveryLocationDialog) {
            currentContext.hasLocationPermissionFlow().collect { hasLocationPermission ->
                if (hasLocationPermission && showDeliveryLocationDialog) {
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
                }
            )
        }

    if (uiState.value.cuceiCenterOnMap != null && uiState.value.cuceiAreaBoundsOnMap != null) {
        DeliveryLocationDialog(
            isDialogOpen = showDeliveryLocationDialog,
            isTopLocationsOnMapLoading = uiState.value.isLoadingTopLocationsOnMap,
            cuceiCenter = uiState.value.cuceiCenterOnMap!!,
            cuceiBounds = uiState.value.cuceiAreaBoundsOnMap!!,
            currentLocation = uiState.value.currentDeliveryLocation,
            currentUserLocation = uiState.value.userLocation,
            topLocationsOnMap = uiState.value.topLocationsOnMap,
            onIntent = viewModel::onIntent,
            onDismiss = { showDeliveryLocationDialog = false }
        )
    }

    if (showEmptyShoppingCartBottomSheet) {
        EmptyShoppingCartBottomSheet(
            sheetState = emptyShoppingCartSheetState,
            onDismissRequest = { showEmptyShoppingCartBottomSheet = false },
            onExploreStoresButton = {
                showEmptyShoppingCartBottomSheet = false
                onExploreStoresButton()
            }
        )
    }

    if (showPaymentMethodBottomSheet) {
        PaymentMethodBottomSheet(
            paymentMethods = paymentMethods,
            sheetState = paymentMethodSheetState,
            onIntent = viewModel::onIntent,
            onAfterItemClick = { showPaymentMethodBottomSheet = false },
            onDismissRequest = { showPaymentMethodBottomSheet = false }
        )
    }

    ShoppingCartScreenContent(
        windowSizeClass = windowSizeClass,
        uiState = uiState.value,
        netState = netState.value,
        scrollState = scrollState,
        snackbarHostState = snackbarHostState,
        scrollBehavior = scrollBehavior,
        onIntent = viewModel::onIntent,
        onBackButton = onBackButton,
        onDeliveryLocationClick = {
            if (uiState.value.shoppingCart != null && uiState.value.shoppingCartItems.isNotEmpty()) {
                showDeliveryLocationDialog = true
                locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                showEmptyShoppingCartBottomSheet = true
            }
        },
        onPaymentMethodClick = { showPaymentMethodBottomSheet = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreenContent(
    windowSizeClass: WindowSizeClass,
    uiState: ShoppingCartUiState,
    netState: NetworkStatus,
    scrollState: ScrollState,
    snackbarHostState: SnackbarHostState,
    scrollBehavior: TopAppBarScrollBehavior,
    onIntent: (ShoppingCartIntent) -> Unit,
    onBackButton: () -> Unit,
    onDeliveryLocationClick: () -> Unit,
    onPaymentMethodClick: () -> Unit
) {
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
        TODO()
    } else {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.shopping_cart_screen_title),
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
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center
            ) {
                val deliveryLocationName =
                    if (uiState.currentDeliveryLocation != null)
                        "Se selecciono una ubicacion"
                    else
                        stringResource(id = R.string.shopping_cart_empty_delivery_location)
                val paymentMethodName =
                    if (uiState.currentPaymentMethod != null)
                        uiState.currentPaymentMethod.name
                    else
                        stringResource(id = R.string.shopping_cart_empty_payment_method)
                if (uiState.isLoadingShoppingCartItems) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(15.dp)
                            .height(300.dp)
                    ) {
                        items(2) {
                            ShoppingCartItemShimmer()
                        }
                    }
                } else {
                    if (uiState.shoppingCartItems.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .padding(15.dp)
                                .heightIn(max = 1000.dp)
                        ) {
                            items(
                                items = uiState.shoppingCartItems,
                                key = { item -> item.id }
                            ) { item ->
                                ShoppingCartItem(
                                    item = item,
                                    onIncreaseButton = { itemId ->
                                        onIntent(
                                            ShoppingCartIntent.IncreaseShoppingCartItemQuantity(
                                                itemId
                                            )
                                        )
                                    },
                                    onDecreaseButton = { itemId ->
                                        onIntent(
                                            ShoppingCartIntent.DecreaseShoppingCartItemQuantity(
                                                itemId
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        ShoppingCartEmpty(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(300.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
                if (uiState.shoppingCartItemsError != null) {
                    Text(
                        text = uiState.shoppingCartItemsError.asString(),
                        textAlign = TextAlign.End,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Resumen de orden",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 15.dp)
                )
                Card(
                    shape = RoundedCornerShape(
                        5.dp
                    ),
                    modifier = Modifier
                        .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 25.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row {
                            Text(
                                text = "Subtotal",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 10.dp)
                                    .weight(1f)
                            )
                            Text(
                                text = "$ ${uiState.total.subTotal}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(end = 10.dp, top = 10.dp)
                                    .weight(2f)
                            )
                        }
                        Row {
                            Text(
                                text = "Servicio",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 10.dp)
                                    .weight(1f)
                            )
                            Text(
                                text = "$ ${uiState.total.service}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(end = 10.dp, top = 10.dp)
                                    .weight(2f)
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                        )
                        Row {
                            Text(
                                text = "Total",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .padding(start = 10.dp, bottom = 10.dp)
                                    .weight(1f)
                            )
                            Text(
                                text = "$ ${uiState.total.total}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(end = 10.dp, bottom = 10.dp)
                                    .weight(2f)
                            )
                        }
                    }
                }
                Text(
                    text = "Informacion",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 15.dp)
                )
                Card(
                    shape = RoundedCornerShape(
                        5.dp
                    ),
                    modifier = Modifier
                        .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 25.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 5.dp, end = 5.dp, bottom = 10.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = stringResource(id = R.string.shopping_cart_delivery_location_msg),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 7.dp, top = 7.dp, bottom = 7.dp)
                        )
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onDeliveryLocationClick()
                                }
                                .padding(start = 10.dp, end = 10.dp)
                                .minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = null
                            )
                            Text(
                                text = deliveryLocationName,
                                textAlign = TextAlign.Start,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = stringResource(
                                    id = R.string.shopping_cart_delivery_location_arrow_icon
                                )
                            )
                        }
                        if (uiState.currentDeliveryLocationError != null && uiState.shoppingCart != null) {
                            Text(
                                text = uiState.currentDeliveryLocationError.asString(),
                                textAlign = TextAlign.End,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp)
                            )
                        }
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(start = 5.dp, top = 15.dp, end = 5.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.shopping_cart_payment_method_msg),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 7.dp, top = 7.dp, bottom = 7.dp)
                        )
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onPaymentMethodClick()
                                }
                                .padding(start = 10.dp, end = 10.dp)
                                .minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Payment,
                                contentDescription = null
                            )
                            Text(
                                text = paymentMethodName,
                                textAlign = TextAlign.Start,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = stringResource(
                                    id = R.string.shopping_cart_payment_method_arrow_icon
                                )
                            )
                        }
                        if (uiState.currentPaymentMethodError != null && uiState.shoppingCart != null) {
                            Text(
                                text = uiState.currentPaymentMethodError.asString(),
                                textAlign = TextAlign.End,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp)
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        onIntent(ShoppingCartIntent.Order)
                    },
                    enabled = !uiState.isWaitingForOrderResult,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .align(Alignment.CenterHorizontally)
                        .size(200.dp, 50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (uiState.isWaitingForOrderResult) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Ordenar",
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