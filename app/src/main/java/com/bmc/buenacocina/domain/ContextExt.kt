package com.bmc.buenacocina.domain

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.getActivity(): Activity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasLocationPermissionFlow(): Flow<Boolean> = callbackFlow {
    val hasLocationPermission = {
        ContextCompat.checkSelfPermission(
            this@hasLocationPermissionFlow,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this@hasLocationPermissionFlow,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    trySend(hasLocationPermission())
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            trySend(hasLocationPermission())
        }
    }
    try {
        registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        awaitClose { unregisterReceiver(receiver) }
    } catch (e: Exception) {
        close(e)
    }
}

fun Context.isGpsOrNetworkEnabledFlow(): Flow<Boolean> = callbackFlow {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsOrNetEnabled = {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    trySend(isGpsOrNetEnabled())
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION == intent?.action) {
                trySend(isGpsOrNetEnabled())
            }
        }
    }
    try {
        registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        awaitClose { unregisterReceiver(receiver) }
    } catch (e: Exception) {
        close(e)
    }
}