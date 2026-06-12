package com.devismwanzi.pingmonitor.data.di

import com.devismwanzi.pingmonitor.data.repository.PingRepositoryImpl
import com.devismwanzi.pingmonitor.domain.repository.PingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindPingRepository(
        pingRepositoryImpl: PingRepositoryImpl
    ): PingRepository
}
