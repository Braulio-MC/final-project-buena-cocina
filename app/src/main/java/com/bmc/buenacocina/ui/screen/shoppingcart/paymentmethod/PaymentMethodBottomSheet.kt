package com.bmc.buenacocina.ui.screen.shoppingcart.paymentmethod

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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.bmc.buenacocina.domain.model.PaymentMethodDomain
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodBottomSheet(
    paymentMethods: LazyPagingItems<PaymentMethodDomain>,
    sheetState: SheetState,
    onIntent: (ShoppingCartIntent) -> Unit,
    onAfterItemClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        when (paymentMethods.loadState.refresh) {
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
                if (paymentMethods.itemCount == 0) {
                    PaymentMethodBottomSheetEmpty(
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
                            count = paymentMethods.itemCount,
                            key = paymentMethods.itemKey { item ->
                                item.id
                            }
                        ) { index ->
                            val item = paymentMethods[index]
                            if (item != null) {
                                PaymentMethodBottomSheetItem(
                                    paymentMethod = item,
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
