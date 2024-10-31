package com.bmc.buenacocina.ui.screen.shoppingcart.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bmc.buenacocina.domain.model.LocationDomain
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartIntent
import com.bmc.buenacocina.ui.viewmodel.DeliveryLocationBottomSheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryLocationBottomSheet(
    storeId: String,
    viewModel: DeliveryLocationBottomSheetViewModel = hiltViewModel(
        creationCallback = { factory: DeliveryLocationBottomSheetViewModel.DeliveryLocationBottomSheetViewModelFactory ->
            factory.create(storeId)
        }
    ),
    sheetState: SheetState,
    onIntent: (ShoppingCartIntent) -> Unit,
    onAfterItemClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val locations = viewModel.locations.collectAsLazyPagingItems()

    DeliveryLocationBottomSheetContent(
        locations = locations,
        sheetState = sheetState,
        onIntent = onIntent,
        onAfterItemClick = onAfterItemClick,
        onDismissRequest = onDismissRequest
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryLocationBottomSheetContent(
    locations: LazyPagingItems<LocationDomain>,
    sheetState: SheetState,
    onIntent: (ShoppingCartIntent) -> Unit,
    onAfterItemClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    )
    {
        when (locations.loadState.refresh) {
            is LoadState.Error -> {

            }

            LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.NotLoading -> {
                if (locations.itemCount == 0) {
                    DeliveryLocationBottomSheetEmpty(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(250.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            count = locations.itemCount,
                            key = locations.itemKey { item ->
                                item.id
                            }
                        ) { index ->
                            val item = locations[index]
                            if (item != null) {
                                DeliveryLocationBottomSheetItem(
                                    deliveryLocation = item,
                                    onIntent = onIntent,
                                    onAfterItemClick = onAfterItemClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
