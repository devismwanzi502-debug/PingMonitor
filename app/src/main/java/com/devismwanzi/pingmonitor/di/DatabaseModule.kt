package com.devismwanzi.pingmonitor.di

import android.content.Context
import androidx.room.Room
import com.devismwanzi.pingmonitor.data.local.PingDatabase
import com.devismwanzi.pingmonitor.data.local.dao.PingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePingDatabase(@ApplicationContext context: Context): PingDatabase {
        return Room.databaseBuilder(
            context,
            PingDatabase::class.java,
            "ping_monitor_db"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    @Singleton
    fun providePingDao(database: PingDatabase): PingDao {
        return database.pingDao
    }
}
