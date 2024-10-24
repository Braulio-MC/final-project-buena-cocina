package com.bmc.buenacocina.ui.screen.shoppingcart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Composable
fun ShoppingCartItem(
    item: ShoppingCartItemDomain,
    onIncreaseButton: (String) -> Unit,
    onDecreaseButton: (String) -> Unit
) {
    val pPriceUnit = item.product.price
    val pQuantity = item.quantity.toBigDecimal()
    val pPriceTotal = (pPriceUnit * pQuantity).setScale(2, RoundingMode.HALF_DOWN)
    var finalPriceStr = "$$pPriceTotal"
    if (item.product.discount.startDate != null && item.product.discount.endDate != null) {
        if (
            item.product.discount.percentage > BigDecimal.ZERO &&
            DateUtils.isInRange(
                LocalDateTime.now(),
                item.product.discount.startDate,
                item.product.discount.endDate
            )
        ) {
            val discount =
                pPriceUnit * (item.product.discount.percentage / BigDecimal.valueOf(100)) * pQuantity
            val priceMinusDiscount = (pPriceTotal - discount).setScale(2, RoundingMode.HALF_DOWN)
            finalPriceStr =
                "$$pPriceTotal - $${discount.setScale(2, RoundingMode.HALF_DOWN)} = $$priceMinusDiscount"
        }
    }
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(7.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(10.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.product.image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(id = R.string.shopping_cart_item_image_content_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.product.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = finalPriceStr,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    IconButton(
                        onClick = { onDecreaseButton(item.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = stringResource(id = R.string.shopping_cart_item_decrease_button_content_desc),
                            tint = Color.Black
                        )
                    }
                    Text(
                        text = item.quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { onIncreaseButton(item.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.shopping_cart_item_increase_button_content_desc),
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}
