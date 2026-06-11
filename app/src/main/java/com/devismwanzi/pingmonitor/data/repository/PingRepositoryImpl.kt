package com.devismwanzi.pingmonitor.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.devismwanzi.pingmonitor.data.db.PingDao
import com.devismwanzi.pingmonitor.data.db.PingEntity
import com.devismwanzi.pingmonitor.domain.model.NetworkStats
import com.devismwanzi.pingmonitor.domain.model.PingResult
import com.devismwanzi.pingmonitor.domain.repository.PingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PingRepositoryImpl @Inject constructor(
    private val pingDao: PingDao
) : PingRepository {

    override suspend fun recordPing(pingResult: PingResult) {
        val entity = PingEntity(
            host = pingResult.host,
            latencyMs = pingResult.latencyMs,
            packetLoss = pingResult.packetLoss,
            timestamp = pingResult.timestamp,
            isSuccessful = pingResult.isSuccessful,
            errorMessage = pingResult.errorMessage
        )
        pingDao.insertPing(entity)
    }

    override fun getLatestPings(limit: Int): Flow<List<PingResult>> {
        return pingDao.getLatestPings(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getPingsForHost(host: String, limit: Int): Flow<List<PingResult>> {
        return pingDao.getPingsForHost(host, limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllPings(): LiveData<List<PingResult>> {
        return pingDao.getAllPings().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getNetworkStats(host: String): NetworkStats {
        val latestPings = pingDao.getPingsForHost(host, limit = 1)
        var currentPing = 0f

        latestPings.collect { pings ->
            if (pings.isNotEmpty()) {
                currentPing = pings[0].latencyMs
            }
        }

        val totalPings = pingDao.getPingsForHost(host).collect { pings ->
            // Stats will be calculated below
        }

        return NetworkStats(
            host = host,
            currentPing = currentPing,
            averagePing = pingDao.getAverageLatency(host),
            minPing = pingDao.getMinLatency(host),
            maxPing = pingDao.getMaxLatency(host),
            packetLossPercentage = 0f // Calculate based on failed pings
        )
    }

    override suspend fun clearAllData() {
        pingDao.clearAllData()
    }

    override suspend fun deleteOldData(daysOld: Int) {
        val timeMillis = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000)
        pingDao.deleteOldData(timeMillis)
    }

    private fun PingEntity.toDomainModel(): PingResult {
        return PingResult(
            id = id,
            host = host,
            latencyMs = latencyMs,
            packetLoss = packetLoss,
            timestamp = timestamp,
            isSuccessful = isSuccessful,
            errorMessage = errorMessage
        )
    }
}
