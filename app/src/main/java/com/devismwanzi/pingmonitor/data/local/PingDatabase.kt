package com.devismwanzi.pingmonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devismwanzi.pingmonitor.data.local.dao.PingDao
import com.devismwanzi.pingmonitor.data.local.entity.PingEntity

@Database(entities = [PingEntity::class], version = 1, exportSchema = false)
abstract class PingDatabase : RoomDatabase() {
    abstract val pingDao: PingDao
}
