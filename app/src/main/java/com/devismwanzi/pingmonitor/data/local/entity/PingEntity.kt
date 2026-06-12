package com.devismwanzi.pingmonitor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ping_records")
data class PingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val host: String,
    val latencyMs: Int,
    val packetLoss: Int,
    val timestamp: Long,
    val errorMessage: String?
)
