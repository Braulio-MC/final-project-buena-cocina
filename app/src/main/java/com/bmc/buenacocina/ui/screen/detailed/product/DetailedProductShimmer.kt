package com.bmc.buenacocina.ui.screen.detailed.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.core.shimmerEffect

@Composable
fun DetailedProductShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(200.dp, 30.dp)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(15.dp))
        Box(
            modifier = Modifier
                .size(320.dp, 70.dp)
                .shimmerEffect(RoundedCornerShape(15.dp))
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(170.dp, 20.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(250.dp, 20.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(200.dp, 20.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(280.dp, 20.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(150.dp, 20.dp)
                .shimmerEffect()
        )
    }
}