package com.devismwanzi.pingmonitor.domain.usecase

import com.devismwanzi.pingmonitor.domain.model.PingResult
import com.devismwanzi.pingmonitor.domain.repository.PingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class PerformPingUseCase @Inject constructor(
    private val pingRepository: PingRepository
) {

    suspend fun execute(host: String): PingResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val latency = performSystemPing(host)
            val result = PingResult(
                host = host,
                latencyMs = latency,
                packetLoss = 0f,
                isSuccessful = true
            )
            pingRepository.recordPing(result)
            result
        } catch (e: Exception) {
            Timber.e(e, "Ping failed for host: $host")
            val result = PingResult(
                host = host,
                latencyMs = 0f,
                isSuccessful = false,
                errorMessage = e.message
            )
            pingRepository.recordPing(result)
            result
        }
    }

    private suspend fun performSystemPing(host: String): Float = withContext(Dispatchers.IO) {
        val process = Runtime.getRuntime().exec(arrayOf("/system/bin/ping", "-c", "1", host))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = reader.use { it.readText() }
        
        val timeMatch = Regex("""time=(\d+\.?\d*)\s*ms""").find(output)
        return@withContext timeMatch?.groupValues?.get(1)?.toFloat() ?: 0f
    }
}
