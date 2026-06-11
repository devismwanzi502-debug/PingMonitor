package com.devismwanzi.pingmonitor.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PingMonitorService : Service() {

    @Inject
    lateinit var pingRepository: com.devismwanzi.pingmonitor.domain.repository.PingRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("PingMonitorService started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("PingMonitorService destroyed")
    }
}
