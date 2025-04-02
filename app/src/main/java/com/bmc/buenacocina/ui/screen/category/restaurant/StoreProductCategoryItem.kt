package com.bmc.buenacocina.ui.screen.category.restaurant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.bmc.buenacocina.domain.model.RemoteConfigProductCategoryDomain

@Composable
fun ProductCategoryItem(
    productCategory: RemoteConfigProductCategoryDomain,
    onClick: (RemoteConfigProductCategoryDomain) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(3.dp)
            .size(100.dp)
            .clickable { onClick(productCategory) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(productCategory.iconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(id = R.string.restaurant_cat_specific_img_content_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(5.dp)
                    .size(50.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = productCategory.name,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
