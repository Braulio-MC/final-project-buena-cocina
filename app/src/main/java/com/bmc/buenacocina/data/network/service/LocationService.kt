package com.bmc.buenacocina.data.network.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.bmc.buenacocina.domain.exception.LocationPermissionException
import com.bmc.buenacocina.domain.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(interval: Long): Flow<Location?> = callbackFlow {
        if (!context.hasLocationPermission()) {
            close(LocationPermissionException("Missing location permission"))
        }
        val request = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(interval)
            .setMaxUpdateDelayMillis(interval * 2)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                trySend(result.lastLocation)
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                super.onLocationAvailability(availability)
                if (!availability.isLocationAvailable) {
                    trySend(null)
                }
            }
        }
        client.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener { e -> close(e) }
        awaitClose { client.removeLocationUpdates(locationCallback) }
    }
}