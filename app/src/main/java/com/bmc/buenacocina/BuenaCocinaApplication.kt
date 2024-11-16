package com.bmc.buenacocina

import android.app.Application
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BuenaCocinaApplication : Application() {
    @Inject
    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()
        initFirebaseRemoteConfig()
    }

    private fun initFirebaseRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                } else {

                }
            }
    }
}