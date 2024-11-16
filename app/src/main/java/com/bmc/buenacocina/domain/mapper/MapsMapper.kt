package com.bmc.buenacocina.domain.mapper

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

object MapsMapper {
    fun asLatLng(geoPoint: GeoPoint): LatLng {
        return LatLng(geoPoint.latitude, geoPoint.longitude)
    }

    fun asLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }
}

fun GeoPoint.asLatLng(): LatLng = MapsMapper.asLatLng(this)
fun Location.asLatLng(): LatLng = MapsMapper.asLatLng(this)