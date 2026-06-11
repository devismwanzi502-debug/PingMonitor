package com.devismwanzi.pingmonitor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devismwanzi.pingmonitor.data.local.entity.PingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPing(ping: PingEntity)

    @Query("SELECT * FROM ping_records ORDER BY timestamp DESC")
    fun getAllPings(): Flow<List<PingEntity>>

    @Query("DELETE FROM ping_records")
    suspend fun clearAllPings()
}
