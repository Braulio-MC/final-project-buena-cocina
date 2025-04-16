package com.bmc.buenacocina.ui.screen.category.restaurant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.core.shimmerEffect

@Composable
fun StoreItemShimmer() {
    Column(
        modifier = Modifier
            .width(175.dp)
            .height(190.dp)
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(5.dp))
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(19.dp)
                .padding(horizontal = 5.dp, vertical = 2.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .size(70.dp, 25.dp)
                .padding(horizontal = 5.dp, vertical = 3.dp)
                .align(Alignment.Start)
                .shimmerEffect()
        )
    }
}