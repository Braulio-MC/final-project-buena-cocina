package com.bmc.buenacocina.ui.screen.orderhistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.OrderStatus
import com.bmc.buenacocina.core.getOrderStatusColor
import com.bmc.buenacocina.domain.model.OrderSearchDomain

@Composable
fun OrderHistorySearchItem(
    order: OrderSearchDomain,
    onClick: (String) -> Unit
) {
    var orderTitle = "Informacion: "
    orderTitle += if (order.status == OrderStatus.DELIVERED.status && !order.rated) {
        "se puede calificar"
    } else if (order.status == OrderStatus.DELIVERED.status && order.rated) {
        "se califico"
    } else {
        "pendiente"
    }

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.order_item),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f)
            ) {
                Text(
                    text = orderTitle,
                    textAlign = TextAlign.Start,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Actualizacion",
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
                        text = DateUtils.localDateTimeToString(order.updatedAt),
                        textAlign = TextAlign.End,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
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
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Tienda",
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
                        text = order.store.name,
                        textAlign = TextAlign.End,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
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
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Metodo de pago",
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
                        text = order.paymentMethod.name,
                        textAlign = TextAlign.End,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(2f)
                            .padding(end = 5.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = getOrderStatusColor(order.status))
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(IntrinsicSize.Max)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = order.status,
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Button(
                        onClick = { onClick(order.id) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize(),
                        shape = RoundedCornerShape(6.dp),
                    ) {
                        Text(
                            text = "Detalles",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
