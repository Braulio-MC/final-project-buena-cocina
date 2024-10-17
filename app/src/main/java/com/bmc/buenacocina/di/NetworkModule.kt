package com.bmc.buenacocina.di

import android.content.Context
import com.bmc.buenacocina.R
import com.bmc.buenacocina.core.OK_HTTP_CLIENT_CONNECTION_TIMEOUT_IN_SEC
import com.bmc.buenacocina.core.OK_HTTP_CLIENT_READ_TIMEOUT_IN_SEC
import com.bmc.buenacocina.core.OK_HTTP_CLIENT_WRITE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.data.network.service.GetStreamChannelService
import com.bmc.buenacocina.data.network.service.GetStreamTokenService
import com.bmc.buenacocina.data.network.service.SearchService
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
    }

    @HeaderInterceptorOkHttpClient
    @Provides
    fun provideOkHttpClient(headerInterceptorOkHttpClient: Interceptor): OkHttpClient {
        val connectionTimeout = Duration.ofSeconds(OK_HTTP_CLIENT_CONNECTION_TIMEOUT_IN_SEC)
        val writeTimeout = Duration.ofSeconds(OK_HTTP_CLIENT_WRITE_TIMEOUT_IN_SEC)
        val readTimeout = Duration.ofSeconds(OK_HTTP_CLIENT_READ_TIMEOUT_IN_SEC)
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptorOkHttpClient)
            .connectTimeout(connectionTimeout)
            .writeTimeout(writeTimeout)
            .readTimeout(readTimeout)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        @HeaderInterceptorOkHttpClient okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit {
        val baseUrl = context.getString(R.string.base_api_server_url)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())  // Sandwich integration
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideSearchService(retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetStreamTokenService(retrofit: Retrofit): GetStreamTokenService {
        return retrofit.create(GetStreamTokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetStreamChannelService(retrofit: Retrofit): GetStreamChannelService {
        return retrofit.create(GetStreamChannelService::class.java)
    }
}