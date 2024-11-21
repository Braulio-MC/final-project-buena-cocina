package com.bmc.buenacocina.ui.screen.detailed.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun DetailedOrderLocationDialog(
    isDialogOpen: Boolean,
    isLoadingUserLocation: Boolean,
    cuceiCenter: Pair<String, LatLng>,
    cuceiBounds: List<Pair<String, LatLng>>,
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cuceiCenter.second, 16.5f)
    },
    orderLocation: LatLng?,
    currentUserLocation: LatLng?,
    onDismiss: () -> Unit
) {
    if (isDialogOpen) {
        val cBounds = cuceiBounds.map { it.second }

        Dialog(
            onDismissRequest = onDismiss
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
            ) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                ) {
                    Polygon(
                        points = cBounds,
                        strokeColor = Color.DarkGray,
                        strokeWidth = 5f,
                        fillColor = Color.DarkGray.copy(alpha = 0.1f)
                    )
                    orderLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Ubicacion del pedido",
                            snippet = "Aqui tienes que recibir tu pedido"
                        )
                    }
                    currentUserLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Ubicacion actual",
                            snippet = "Estas aqui",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                    }
                }
                if (isLoadingUserLocation) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(40.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(25.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}