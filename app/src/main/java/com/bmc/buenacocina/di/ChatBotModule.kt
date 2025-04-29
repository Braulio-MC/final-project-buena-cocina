package com.bmc.buenacocina.di

import android.util.Log
import com.bmc.buenacocina.data.network.service.BotApiService
import com.bmc.buenacocina.data.network.model.ChatBotApiResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ChatBotNetwork {

    private const val BASE_URL = "http://192.168.100.16:8000/" // Esta es la direccion de pyapi

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d("Request", "URL: ${request.url}")
            val response = chain.proceed(request)
            Log.d("Response", "Code: ${response.code}")
            response
        }
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(ChatBotApiResponse::class.java, "type")
                .withSubtype(ChatBotApiResponse.ProductResponse::class.java, "product")
                .withSubtype(ChatBotApiResponse.StoreResponse::class.java, "store")
                .withSubtype(ChatBotApiResponse.Message::class.java, "message")
        )
        .add(KotlinJsonAdapterFactory()) // Para soportar Kotlin
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val botApiService: BotApiService = retrofit.create(BotApiService::class.java)
}
