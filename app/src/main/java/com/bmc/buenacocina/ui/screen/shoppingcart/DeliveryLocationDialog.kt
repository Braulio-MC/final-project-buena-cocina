package com.bmc.buenacocina.ui.screen.shoppingcart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bmc.buenacocina.core.isPointInPolygon
import com.bmc.buenacocina.domain.model.InsightTopLocationDomain
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.HeatmapTileProvider

@Composable
fun DeliveryLocationDialog(
    isDialogOpen: Boolean,
    isTopLocationsOnMapLoading: Boolean,
    cuceiCenter: Pair<String, LatLng>,
    cuceiBounds: List<Pair<String, LatLng>>,
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cuceiCenter.second, 16.5f)
    },
    currentLocation: LatLng?,
    currentUserLocation: LatLng?,
    topLocationsOnMap: List<InsightTopLocationDomain>,
    onIntent: (ShoppingCartIntent) -> Unit,
    onDismiss: () -> Unit,
) {
    if (isDialogOpen) {
        var isMapLoaded by remember { mutableStateOf(false) }
        val cBounds = cuceiBounds.map { it.second }
        val weightedData = topLocationsOnMap.map { it.geopoint }
        var heatMapTileProvider: HeatmapTileProvider? = null
        if (!isTopLocationsOnMapLoading && weightedData.isNotEmpty()) {
            heatMapTileProvider = HeatmapTileProvider.Builder()
                .weightedData(weightedData)
                .radius(50)
                .build()
        }

        Dialog(
            onDismissRequest = onDismiss
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    if (isPointInPolygon(latLng, cBounds)) {
                        onIntent(ShoppingCartIntent.UpdateCurrentDeliveryLocation(latLng))
                    }
                },
                onMapLoaded = { isMapLoaded = true }
            ) {
                if (isMapLoaded) {
                    Polygon(
                        points = cBounds,
                        strokeColor = Color.DarkGray,
                        strokeWidth = 5f,
                        fillColor = Color.DarkGray.copy(alpha = 0.1f)
                    )
                    heatMapTileProvider?.let {
                        TileOverlay(
                            tileProvider = heatMapTileProvider
                        )
                    }
                    currentLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Ubicacion elegida",
                            snippet = "Aqui se entregara el pedido"
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
            }
        }
    }
}
