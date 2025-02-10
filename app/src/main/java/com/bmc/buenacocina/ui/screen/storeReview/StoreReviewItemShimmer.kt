package com.bmc.buenacocina.ui.screen.storeReview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.core.shimmerEffect

@Composable
fun StoreReviewItemShimmer() {
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
            Box(
                modifier = Modifier
                    .width(115.dp)
                    .height(25.dp)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(20.dp)
                    .shimmerEffect()
            )
        }
        Box(
            modifier = Modifier
                .padding(5.dp)
                .height(85.dp)
                .fillMaxWidth()
                .shimmerEffect()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .height(12.dp)
                    .weight(1f)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .height(12.dp)
                    .weight(0.8f)
                    .shimmerEffect()
            )
        }
    }
}