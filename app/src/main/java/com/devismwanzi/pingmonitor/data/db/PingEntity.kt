package com.devismwanzi.pingmonitor.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ping_data")
data class PingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val host: String,
    val latencyMs: Float,
    val packetLoss: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccessful: Boolean = true,
    val errorMessage: String? = null
)
