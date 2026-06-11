package com.devismwanzi.pingmonitor.data.repository

import com.devismwanzi.pingmonitor.data.local.dao.PingDao
import com.devismwanzi.pingmonitor.data.local.entity.PingEntity
import com.devismwanzi.pingmonitor.domain.model.NetworkStats
import com.devismwanzi.pingmonitor.domain.model.PingResult
import com.devismwanzi.pingmonitor.domain.repository.PingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PingRepositoryImpl @Inject constructor(
    private val dao: PingDao
) : PingRepository {

    override suspend fun recordPing(ping: PingResult) {
        dao.insertPing(
            PingEntity(
                host = ping.host,
                latencyMs = ping.latencyMs,
                packetLoss = ping.packetLoss,
                timestamp = ping.timestamp,
                errorMessage = ping.errorMessage
            )
        )
    }

    override fun getAllPings(): Flow<List<PingResult>> {
        return dao.getAllPings().map { entities ->
            entities.map {
                PingResult(
                    id = it.id,
                    host = it.host,
                    latencyMs = it.latencyMs,
                    packetLoss = it.packetLoss,
                    timestamp = it.timestamp,
                    errorMessage = it.errorMessage
                )
            }
        }
    }

    override fun getNetworkStats(): Flow<NetworkStats> {
        return dao.getAllPings().map { entities ->
            if (entities.isEmpty()) {
                return@map NetworkStats(
                    currentPing = 0,
                    minPing = 0,
                    maxPing = 0,
                    avgPing = 0,
                    packetLoss = 0
                )
            }
            
            // Filter out drops and timeouts for accurate calculations
            val validLatencies = entities.map { it.latencyMs }.filter { it > 0 }
            
            val current = validLatencies.firstOrNull() ?: 0
            val min = validLatencies.minOrNull() ?: 0
            val max = validLatencies.maxOrNull() ?: 0
            val avg = if (validLatencies.isNotEmpty()) validLatencies.average().toInt() else 0
            
            // Calculate total packet loss percentile over the logged scope
            val totalPackets = entities.size
            val lostPackets = entities.count { it.latencyMs <= 0 || it.errorMessage != null }
            val lossPercent = if (totalPackets > 0) ((lostPackets.toFloat() / totalPackets) * 100).toInt() else 0

            NetworkStats(
                currentPing = current,
                minPing = min,
                maxPing = max,
                avgPing = avg,
                packetLoss = lossPercent
            )
        }
    }
}
