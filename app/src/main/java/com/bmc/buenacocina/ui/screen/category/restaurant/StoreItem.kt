package com.bmc.buenacocina.ui.screen.category.restaurant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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


    Column(
        modifier = Modifier
            .width(175.dp)
            .height(190.dp)
            .padding(5.dp)
            .clickable { onClick(store.id) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(store.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }
        Text(
            text = store.name,
            fontSize = 19.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 2.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "",
                tint = colorResource(id = R.color.rating_bar_filled),
                modifier = Modifier
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = storeRating,
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}