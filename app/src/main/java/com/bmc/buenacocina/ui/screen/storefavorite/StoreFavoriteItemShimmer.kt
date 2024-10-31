package com.bmc.buenacocina.ui.screen.storefavorite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.core.shimmerEffect

@Composable
fun StoreFavoriteItemShimmer() {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .size(110.dp, 180.dp)
            .clip(shape = RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .height(110.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .fillMaxWidth()
                .height(18.dp)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .height(50.dp)
                .shimmerEffect()
        )
    }
}