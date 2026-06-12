package com.devismwanzi.pingmonitor.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devismwanzi.pingmonitor.domain.model.NetworkStats
import com.devismwanzi.pingmonitor.domain.model.PingResult
import com.devismwanzi.pingmonitor.domain.repository.PingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PingRepositoryImpl @Inject constructor() : PingRepository {

    private val _pings = mutableListOf<PingResult>()
    private val _allPingsLiveData = MutableLiveData<List<PingResult>>(emptyList())

    override suspend fun recordPing(pingResult: PingResult) {
        _pings.add(0, pingResult)
        _allPingsLiveData.postValue(_pings.toList())
    }

    override fun getLatestPings(limit: Int): Flow<List<PingResult>> {
        return MutableStateFlow(_pings.take(limit))
    }

    override fun getPingsForHost(host: String, limit: Int): Flow<List<PingResult>> {
        return MutableStateFlow(_pings.filter { it.host == host }.take(limit))
    }

    override fun getAllPings(): LiveData<List<PingResult>> {
        return _allPingsLiveData
    }

    override suspend fun getNetworkStats(host: String): NetworkStats {
        val hostPings = _pings
            .filter { it.host == host }
            .filter { it.isSuccessful }

        if (hostPings.isEmpty()) {
            return NetworkStats(
                host = host,
                currentPing = 0f,
                averagePing = null,
                minPing = null,
                maxPing = null,
                packetLossPercentage = if (_pings.isEmpty()) 0f else 100f
            )
        }

        val latencies = hostPings.map { it.latencyMs }
        val avg = latencies.average().toFloat()
        val min = latencies.minOrNull() ?: 0f
        val max = latencies.maxOrNull() ?: 0f
        val totalAttempts = _pings.count { it.host == host }
        val failures = totalAttempts - hostPings.size
        val loss = if (totalAttempts == 0) 0f else (failures.toFloat() / totalAttempts) * 100f

        return NetworkStats(
            host = host,
            currentPing = hostPings.first().latencyMs,
            averagePing = avg,
            minPing = min,
            maxPing = max,
            packetLossPercentage = loss
        )
    }

    override suspend fun clearAllData() {
        _pings.clear()
        _allPingsLiveData.postValue(emptyList())
    }

    override suspend fun deleteOldData(daysOld: Int) {
        val cutoff = System.currentTimeMillis() - (daysOld * 86_400_000L)
        _pings.removeAll { it.timestamp < cutoff }
        _allPingsLiveData.postValue(_pings.toList())
    }
}
