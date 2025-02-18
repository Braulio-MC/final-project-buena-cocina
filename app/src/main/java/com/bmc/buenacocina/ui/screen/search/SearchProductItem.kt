package com.bmc.buenacocina.ui.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.FormatUtils
import com.bmc.buenacocina.domain.model.ProductSearchDomain
import com.smarttoolfactory.ratingbar.RatingBar
import com.smarttoolfactory.ratingbar.model.GestureStrategy
import java.math.BigDecimal
import java.time.LocalDateTime

@Composable
fun SearchProductItem(
    hit: ProductSearchDomain,
    onClick: (String, String) -> Unit
) {
    var isStoreNameOverFlowing by remember { mutableStateOf(false) }
    val storeNameTextAlign = if (isStoreNameOverFlowing) TextAlign.Start else TextAlign.End
    var discountPercentage: BigDecimal? = null
    if (hit.discount.percentage > BigDecimal.ZERO && DateUtils.isInRange(
            LocalDateTime.now(),
            hit.discount.startDate,
            hit.discount.endDate
        )
    ) {
        discountPercentage = FormatUtils.formatDiscountPercentage(hit.discount.percentage)
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clickable { onClick(hit.id, hit.store.ownerId) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(hit.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Search result product image",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                )
                if (discountPercentage != null) {
                    SearchProductItemDiscount(
                        percentage = discountPercentage,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(1.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = hit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hit.type.type,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(0.5f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = hit.store.name,
                            textAlign = storeNameTextAlign,
                            fontSize = 15.5.sp,
                            fontStyle = FontStyle.Italic,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { textLayoutResult ->
                                isStoreNameOverFlowing = textLayoutResult.hasVisualOverflow
                            },
                            modifier = Modifier
                                .weight(1f)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = "",
                            modifier = Modifier
                                .size(25.dp)
                        )
                    }
                }
                Text(
                    text = hit.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${hit.totalReviews} reseñas",
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    RatingBar(
                        rating = hit.rating,
                        imageVectorEmpty = Icons.Outlined.StarBorder,
                        imageVectorFilled = Icons.Outlined.Star,
                        space = 2.dp,
                        itemSize = 20.dp,
                        tintEmpty = colorResource(id = R.color.rating_bar_empty),
                        tintFilled = colorResource(id = R.color.rating_bar_filled),
                        gestureStrategy = GestureStrategy.None,
                        onRatingChange = {}
                    )
                }
            }
        }
    }
}
