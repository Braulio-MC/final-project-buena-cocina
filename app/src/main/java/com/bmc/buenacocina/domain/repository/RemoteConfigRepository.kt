package com.bmc.buenacocina.domain.repository

import com.bmc.buenacocina.core.CUCEI_AREA_BOUNDS_ON_GMAPS_NAME
import com.bmc.buenacocina.core.CUCEI_CENTER_ON_GMAPS_NAME
import com.bmc.buenacocina.core.DEFAULT_CUCEI_CENTER_ON_GMAPS_NAME
import com.bmc.buenacocina.core.DEFAULT_LATITUDE_CUCEI_CENTER_ON_GMAPS
import com.bmc.buenacocina.core.DEFAULT_LONGITUDE_CUCEI_CENTER_ON_GMAPS
import com.bmc.buenacocina.core.REMOTE_CONFIG_PRODUCT_CATEGORIES_NAME
import com.bmc.buenacocina.domain.model.RemoteConfigProductCategoryDomain
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class RemoteConfigRepository @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) {
    private val _cuceiCenterOnMap = MutableStateFlow<Pair<String, LatLng>?>(null)
    val cuceiCenterOnMap: Flow<Pair<String, LatLng>?> = _cuceiCenterOnMap
    private val _cuceiAreaBoundsOnMap = MutableStateFlow<List<Pair<String, LatLng>>?>(null)
    val cuceiAreaBoundsOnMap: Flow<List<Pair<String, LatLng>>?> = _cuceiAreaBoundsOnMap
    private val _productCategories =
        MutableStateFlow<List<RemoteConfigProductCategoryDomain>>(emptyList())
    val productCategories: Flow<List<RemoteConfigProductCategoryDomain>> = _productCategories

    init {
        _cuceiCenterOnMap.value = fetchCuceiCenter()
        _cuceiAreaBoundsOnMap.value = fetchCuceiAreaBounds()
        _productCategories.value = fetchProductCategories()
        fetchCuceiLocationData()
    }

    private fun fetchCuceiLocationData() {
        firebaseRemoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                firebaseRemoteConfig.activate().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (configUpdate.updatedKeys.contains(CUCEI_CENTER_ON_GMAPS_NAME)) {
                            _cuceiCenterOnMap.value = fetchCuceiCenter()
                        }
                        if (configUpdate.updatedKeys.contains(CUCEI_AREA_BOUNDS_ON_GMAPS_NAME)) {
                            _cuceiAreaBoundsOnMap.value = fetchCuceiAreaBounds()
                        }
                        if (configUpdate.updatedKeys.contains(REMOTE_CONFIG_PRODUCT_CATEGORIES_NAME)) {
                            _productCategories.value = fetchProductCategories()
                        }
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                println("Remote config error: ${error.message}")
            }
        })
    }

    private fun fetchCuceiCenter(): Pair<String, LatLng> {
        val jsonStr = firebaseRemoteConfig.getString(CUCEI_CENTER_ON_GMAPS_NAME)
        val centerObj = Json.parseToJsonElement(jsonStr).jsonObject
        val name = centerObj["name"]?.jsonPrimitive?.content ?: DEFAULT_CUCEI_CENTER_ON_GMAPS_NAME
        val lat = centerObj["lat"]?.jsonPrimitive?.double ?: DEFAULT_LATITUDE_CUCEI_CENTER_ON_GMAPS
        val lng = centerObj["lng"]?.jsonPrimitive?.double ?: DEFAULT_LONGITUDE_CUCEI_CENTER_ON_GMAPS
        return name to LatLng(lat, lng)
    }

    private fun fetchCuceiAreaBounds(): List<Pair<String, LatLng>> {
        val jsonStr = firebaseRemoteConfig.getString(CUCEI_AREA_BOUNDS_ON_GMAPS_NAME)
        val bounds = mutableListOf<Pair<String, LatLng>>()
        val jsonArray = Json.parseToJsonElement(jsonStr).jsonArray
        for (point in jsonArray) {
            val pointObj = point.jsonObject
            val name = pointObj["name"]?.jsonPrimitive?.content ?: "Unknown"
            val lat = pointObj["lat"]?.jsonPrimitive?.double ?: 0.0
            val lng = pointObj["lng"]?.jsonPrimitive?.double ?: 0.0
            bounds.add(name to LatLng(lat, lng))
        }
        return bounds
    }

    private fun fetchProductCategories(): List<RemoteConfigProductCategoryDomain> {
        val jsonStr = firebaseRemoteConfig.getString(REMOTE_CONFIG_PRODUCT_CATEGORIES_NAME)
        val categories = mutableListOf<RemoteConfigProductCategoryDomain>()
        val jsonArray = Json.parseToJsonElement(jsonStr).jsonArray
        for (category in jsonArray) {
            val categoryObj = category.jsonObject
            val name = categoryObj["name"]?.jsonPrimitive?.content ?: "Unknown"
            val icon = categoryObj["icon"]?.jsonPrimitive?.content ?: ""
            categories.add(RemoteConfigProductCategoryDomain(name, icon))
        }
        return categories
    }
}