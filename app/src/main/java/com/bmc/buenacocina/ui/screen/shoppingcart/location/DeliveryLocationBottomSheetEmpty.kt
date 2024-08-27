package com.bmc.buenacocina.ui.screen.shoppingcart.location

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmc.buenacocina.R

@Composable
fun DeliveryLocationBottomSheetEmpty(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.location_empty),
            contentDescription = null,
            modifier = Modifier
                .size(75.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "No hay direcciones disponibles",
            fontSize = 17.sp,
            fontWeight = FontWeight.W400
        )
    }
}