package com.devismwanzi.pingmonitor.domain.repository

import androidx.lifecycle.LiveData
import com.devismwanzi.pingmonitor.domain.model.NetworkStats
import com.devismwanzi.pingmonitor.domain.model.PingResult
import kotlinx.coroutines.flow.Flow

interface PingRepository {
    suspend fun recordPing(pingResult: PingResult)

    fun getLatestPings(limit: Int = 100): Flow<List<PingResult>>

    fun getPingsForHost(host: String, limit: Int = 100): Flow<List<PingResult>>

    fun getAllPings(): LiveData<List<PingResult>>

    suspend fun getNetworkStats(host: String): NetworkStats

    suspend fun clearAllData()

    suspend fun deleteOldData(daysOld: Int)
}
