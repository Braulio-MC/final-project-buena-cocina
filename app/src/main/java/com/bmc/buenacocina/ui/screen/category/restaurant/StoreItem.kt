package com.bmc.buenacocina.ui.screen.category.restaurant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.bmc.buenacocina.domain.model.StoreDomain
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun StoreItem(
    store: StoreDomain,
    onClick: (String) -> Unit
) {
    val storeRating = if (store.rating > BigDecimal.ZERO) store.rating.setScale(
        1, RoundingMode.HALF_DOWN
    ).toPlainString() else "N/A"

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clickable { onClick(store.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .fillMaxWidth()
                    .weight(1f),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(store.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            Text(
                text = store.name,
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "",
                    tint = colorResource(id = R.color.rating_bar_filled)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = storeRating,
                    fontSize = 17.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}