package com.bmc.buenacocina.ui.screen.detailed.order.rating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bmc.buenacocina.R
import com.smarttoolfactory.ratingbar.RatingBar

@Composable
fun DetailedOrderRatingItemDialog(
    isDialogOpen: Boolean,
    productImage: String,
    productRating: DetailedOrderRatingUiResultState.DetailedOrderRatingUiStateItemRating,
    onItemRatingChange: (Float) -> Unit,
    onItemCommentChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .height(450.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(productImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(130.dp)
                    )
                    RatingBar(
                        rating = productRating.item.rating,
                        imageVectorEmpty = Icons.Outlined.StarBorder,
                        imageVectorFilled = Icons.Outlined.Star,
                        space = 2.dp,
                        itemSize = 35.dp,
                        tintEmpty = colorResource(id = R.color.rating_bar_empty),
                        tintFilled = colorResource(id = R.color.rating_bar_filled),
                        onRatingChange = { newRating ->
                            onItemRatingChange(newRating)
                        },
                        modifier = Modifier
                            .padding(10.dp)
                    )
                    if (productRating.ratingError != null) {
                        Text(
                            text = productRating.ratingError.asString(),
                            textAlign = TextAlign.End,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp)
                        )
                    }
                    TextField(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .height(140.dp),
                        value = productRating.item.comment,
                        onValueChange = { newComment ->
                            onItemCommentChange(newComment)
                        },
                        placeholder = {
                            Text(text = "Comentario")
                        },
                        isError = productRating.commentError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    if (productRating.commentError != null) {
                        Text(
                            text = productRating.commentError.asString(),
                            textAlign = TextAlign.End,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
