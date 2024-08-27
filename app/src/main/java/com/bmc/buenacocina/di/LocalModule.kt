package com.bmc.buenacocina.di

import android.content.Context
import androidx.room.Room
import com.bmc.buenacocina.core.ROOM_DATABASE_NAME
import com.bmc.buenacocina.data.local.LocalDatabase
import com.bmc.buenacocina.data.local.dao.SearchDao
import com.bmc.buenacocina.data.local.dao.SearchRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext appContext: Context): LocalDatabase {
        return Room.databaseBuilder(
            appContext, LocalDatabase::class.java, ROOM_DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideSearchRemoteKeyDao(db: LocalDatabase): SearchRemoteKeyDao {
        return db.getSearchRemoteKeyDao()
    }

    @Provides
    @Singleton
    fun provideSearchDao(db: LocalDatabase): SearchDao {
        return db.getSearchDao()
    }
}