package com.devismwanzi.pingmonitor.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PingDao {
    @Insert
    suspend fun insertPing(ping: PingEntity): Long

    @Update
    suspend fun updatePing(ping: PingEntity)

    @Delete
    suspend fun deletePing(ping: PingEntity)

    @Query("SELECT * FROM ping_data WHERE id = :id")
    suspend fun getPingById(id: Long): PingEntity?

    @Query("SELECT * FROM ping_data ORDER BY timestamp DESC LIMIT :limit")
    fun getLatestPings(limit: Int = 100): Flow<List<PingEntity>>

    @Query("SELECT * FROM ping_data WHERE host = :host ORDER BY timestamp DESC LIMIT :limit")
    fun getPingsForHost(host: String, limit: Int = 100): Flow<List<PingEntity>>

    @Query("SELECT * FROM ping_data ORDER BY timestamp DESC")
    fun getAllPings(): LiveData<List<PingEntity>>

    @Query("SELECT AVG(latencyMs) FROM ping_data WHERE host = :host AND isSuccessful = 1")
    suspend fun getAverageLatency(host: String): Float?

    @Query("SELECT MIN(latencyMs) FROM ping_data WHERE host = :host AND isSuccessful = 1")
    suspend fun getMinLatency(host: String): Float?

    @Query("SELECT MAX(latencyMs) FROM ping_data WHERE host = :host AND isSuccessful = 1")
    suspend fun getMaxLatency(host: String): Float?

    @Query("SELECT COUNT(*) FROM ping_data WHERE host = :host AND isSuccessful = 0")
    suspend fun getPacketLossCount(host: String): Int

    @Query("DELETE FROM ping_data")
    suspend fun clearAllData()

    @Query("DELETE FROM ping_data WHERE timestamp < :timeMillis")
    suspend fun deleteOldData(timeMillis: Long)
}
