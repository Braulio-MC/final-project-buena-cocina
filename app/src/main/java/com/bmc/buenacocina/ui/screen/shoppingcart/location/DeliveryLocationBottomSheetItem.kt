package com.bmc.buenacocina.ui.screen.shoppingcart.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmc.buenacocina.domain.model.LocationDomain
import com.bmc.buenacocina.ui.screen.shoppingcart.ShoppingCartIntent

@Composable
fun DeliveryLocationBottomSheetItem(
    deliveryLocation: LocationDomain,
    onIntent: (ShoppingCartIntent) -> Unit,
    onAfterItemClick: () -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(5.dp)
            .clickable {
                onIntent(ShoppingCartIntent.UpdateCurrentDeliveryLocation(deliveryLocation))
                onAfterItemClick()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = deliveryLocation.name,
                fontSize = 19.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = deliveryLocation.description,
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
