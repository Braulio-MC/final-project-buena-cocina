plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.bmc.buenacocina"
    compileSdk = 35

    defaultConfig {
        manifestPlaceholders += mapOf(
            "auth0Domain" to "@string/com_auth0_domain",
            "auth0Scheme" to "@string/com_auth0_scheme"
        )
        applicationId = "com.bmc.buenacocina"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3.adaptive.nav)
    implementation(libs.auth0.sdk)
    implementation(libs.getstream.compose.chat)
    implementation(libs.getstream.chat.offline)
    implementation(libs.getstream.push.firebase)
    implementation(libs.coil.compose.coil)
    implementation(libs.androidx.security.crypto)
    implementation(libs.compose.ratingbar)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material3.wsc)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.jetbrains.kotlinx.serialization)
    implementation(libs.androidx.compose.dagger.hilt)
    implementation(libs.squareup.retrofit2.retrofit)
    implementation(libs.squareup.retrofit2.coverter)
    implementation(libs.github.skydoves.sandwich)
    implementation(libs.github.skydoves.sandwich.retrofit)
    implementation(libs.androidx.paging3.runtime.ktx)
    implementation(libs.androidx.compose.paging3)
    implementation(libs.androidx.room.paging3)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.functions.ktx)
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.google.firebase.config.ktx)
    implementation(libs.google.playservices.maps)
    implementation(libs.google.playservices.location)
    implementation(libs.google.compose.maps)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.google.dagger.hilt)
    ksp(libs.google.dagger.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}