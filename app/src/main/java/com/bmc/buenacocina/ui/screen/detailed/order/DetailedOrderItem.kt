package com.bmc.buenacocina.ui.screen.detailed.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.domain.model.OrderLineDomain
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun DetailedOrderItem(
    line: OrderLineDomain
) {
    val pPriceUnit = line.product.price.setScale(2, RoundingMode.HALF_DOWN)
    val pQuantity = line.quantity.toBigDecimal()
    val pDiscount = line.product.discount.percentage.setScale(2, RoundingMode.HALF_DOWN)
    val discount = (pPriceUnit * (pDiscount / BigDecimal.valueOf(100)) * pQuantity).setScale(
        2,
        RoundingMode.HALF_DOWN
    )
    val total = (pPriceUnit * pQuantity - discount).setScale(2, RoundingMode.HALF_DOWN)
    val discountStr = if (pDiscount > BigDecimal.ZERO) {
        "$$discount ($pDiscount%)"
    } else {
        "No aplica"
    }

    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(line.product.image)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f)
        ) {
            Text(
                text = line.product.name,
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
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
                    text = "Por unidad",
                    textAlign = TextAlign.End,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "$$pPriceUnit",
                    textAlign = TextAlign.End,
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.W400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 5.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Descuento",
                    textAlign = TextAlign.End,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = discountStr,
                    textAlign = TextAlign.End,
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.W400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 5.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cantidad",
                    textAlign = TextAlign.End,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = line.quantity.toString(),
                    textAlign = TextAlign.End,
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.W400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 5.dp)
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Black,
                modifier = Modifier
                    .padding(vertical = 3.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    textAlign = TextAlign.End,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                )
                Text(
                    text = "$$total",
                    textAlign = TextAlign.End,
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 5.dp)

                )
            }
        }
    }
}