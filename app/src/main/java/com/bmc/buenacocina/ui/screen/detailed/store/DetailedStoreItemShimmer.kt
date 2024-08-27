package com.bmc.buenacocina.ui.screen.detailed.store

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bmc.buenacocina.core.shimmerEffect

@Preview(showBackground = true)
@Composable
fun DetailedStoreItemShimmer() {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .size(100.dp, 190.dp)
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
                .padding(bottom = 3.dp)
                .fillMaxWidth()
                .height(20.dp)
                .shimmerEffect()
        )
        Box(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .height(10.dp)
                .shimmerEffect()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp, 20.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(40.dp, 20.dp)
                    .shimmerEffect()
            )
        }
    }
}