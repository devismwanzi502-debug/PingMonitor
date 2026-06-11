package com.devismwanzi.pingmonitor.di

import android.content.Context
import com.devismwanzi.pingmonitor.data.db.AppDatabase
import com.devismwanzi.pingmonitor.data.db.PingDao
import com.devismwanzi.pingmonitor.data.repository.PingRepositoryImpl
import com.devismwanzi.pingmonitor.domain.repository.PingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun providePingDao(database: AppDatabase): PingDao {
        return database.pingDao()
    }

    @Singleton
    @Provides
    fun providePingRepository(pingDao: PingDao): PingRepository {
        return PingRepositoryImpl(pingDao)
    }
}
