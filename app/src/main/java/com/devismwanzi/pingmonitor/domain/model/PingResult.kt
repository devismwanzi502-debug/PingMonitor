package com.devismwanzi.pingmonitor.domain.model

import java.util.Date

data class PingResult(
    val id: Long = 0,
    val host: String,
    val latencyMs: Float,
    val packetLoss: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccessful: Boolean = true,
    val errorMessage: String? = null
) {
    val formattedLatency: String
        get() = "${latencyMs.toInt()} ms"

    val formattedTime: String
        get() = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US)
            .format(Date(timestamp))
}

data class NetworkStats(
    val host: String,
    val currentPing: Float,
    val averagePing: Float?,
    val minPing: Float?,
    val maxPing: Float?,
    val packetLossPercentage: Float
)
