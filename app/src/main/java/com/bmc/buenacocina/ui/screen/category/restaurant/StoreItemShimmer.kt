package com.bmc.buenacocina.ui.screen.category.restaurant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.core.shimmerEffect

@Composable
fun StoreItemShimmer() {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .height(140.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .height(20.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .size(80.dp, 30.dp)
                .shimmerEffect()
        )
    }
}