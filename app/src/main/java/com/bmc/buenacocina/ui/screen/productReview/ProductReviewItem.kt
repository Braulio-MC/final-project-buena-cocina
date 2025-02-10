package com.bmc.buenacocina.ui.screen.productReview

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.DateUtils
import com.bmc.buenacocina.core.getReviewSentimentStatusColor
import com.smarttoolfactory.ratingbar.RatingBar
import com.smarttoolfactory.ratingbar.model.GestureStrategy
import java.time.LocalDateTime

@Composable
fun ProductReviewItem(
    rating: Float,
    comment: String,
    sentiment: String,
    updatedAt: LocalDateTime,
    scrollState: ScrollState = rememberScrollState()
) {
    val updatedAtStr = DateUtils.localDateTimeToString(updatedAt)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .height(35.dp)
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = getReviewSentimentStatusColor(sentiment))
                ),
                modifier = Modifier
                    .width(115.dp)
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
                        text = sentiment,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            RatingBar(
                rating = rating,
                imageVectorEmpty = Icons.Outlined.StarBorder,
                imageVectorFilled = Icons.Outlined.Star,
                space = 2.dp,
                itemSize = 20.dp,
                tintEmpty = colorResource(id = R.color.rating_bar_empty),
                tintFilled = colorResource(id = R.color.rating_bar_filled),
                gestureStrategy = GestureStrategy.None,
                onRatingChange = {},
                modifier = Modifier
                    .fillMaxHeight()
            )
        }
        Box(
            modifier = Modifier
                .padding(5.dp)
                .height(85.dp)
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Gray)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = comment,
                textAlign = TextAlign.Start,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ultima actualizacion",
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
                text = updatedAtStr,
                textAlign = TextAlign.End,
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 5.dp)
            )
        }
    }
}