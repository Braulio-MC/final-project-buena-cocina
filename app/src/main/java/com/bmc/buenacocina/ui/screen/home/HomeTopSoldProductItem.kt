package com.bmc.buenacocina.ui.screen.home

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
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
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
import com.bmc.buenacocina.domain.model.InsightTopSoldProductDomain
import com.smarttoolfactory.ratingbar.RatingBar
import com.smarttoolfactory.ratingbar.model.GestureStrategy
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

@Composable
fun HomeTopSoldProductItem(
    product: InsightTopSoldProductDomain,
    onClick: (String, String) -> Unit
) {
    var isStoreNameOverFlowing by remember { mutableStateOf(false) }
    var isHitOnOrdersOverFlowing by remember { mutableStateOf(false) }
    var isQuantitySoldOverFlowing by remember { mutableStateOf(false) }
    val storeNameTextAlign = if (isStoreNameOverFlowing) TextAlign.Start else TextAlign.End
    val hitOnOrdersTextAlign = if (isHitOnOrdersOverFlowing) TextAlign.Start else TextAlign.End
    val quantitySoldTextAlign = if (isQuantitySoldOverFlowing) TextAlign.Start else TextAlign.End
    var discountPercentage: BigDecimal? = null
    if (product.discountPercentage > BigDecimal.ZERO && DateUtils.isInRange(
            LocalDateTime.now(),
            product.discountStartDate,
            product.discountEndDate
        )
    ) {
        discountPercentage = FormatUtils.formatDiscountPercentage(product.discountPercentage)
    }
    val hitsOnOrdersStr = if (product.hitsOnOrders > BigInteger.ONE) {
        "Aparece en ${product.hitsOnOrders} ordenes"
    } else {
        "Aparece en ${product.hitsOnOrders} orden"
    }
    val quantitySoldStr = if (product.totalQuantitySold > BigInteger.ONE) {
        "${product.totalQuantitySold} productos vendidos"
    } else {
        "${product.totalQuantitySold} producto vendido"
    }

    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clickable { onClick(product.id, product.storeOwnerId) }
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
                        .data(product.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Product image",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                )
                if (discountPercentage != null) {
                    HomeTopSoldProductItemDiscount(
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
                    text = product.name,
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
                        text = product.categoryName,
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
                            text = product.storeName,
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
                                .size(24.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hitsOnOrdersStr,
                        textAlign = hitOnOrdersTextAlign,
                        fontSize = 15.5.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { textLayoutResult ->
                            isHitOnOrdersOverFlowing = textLayoutResult.hasVisualOverflow
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.ShoppingBasket,
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quantitySoldStr,
                        textAlign = quantitySoldTextAlign,
                        fontSize = 15.5.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { textLayoutResult ->
                            isQuantitySoldOverFlowing = textLayoutResult.hasVisualOverflow
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.Fastfood,
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${product.totalReviews} reseñas",
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    RatingBar(
                        rating = product.rating.toFloat(),
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