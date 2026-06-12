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

        when (intent?.action) {
            ACTION_START -> {
                val host = intent.getStringExtra(EXTRA_HOST) ?: "8.8.8.8"
                val interval = intent.getLongExtra(EXTRA_INTERVAL, 2000L)
                Timber.d("Starting monitoring - Host: $host, Interval: ${interval}ms")
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("PingMonitorService destroyed")
    }

    companion object {
        const val ACTION_START = "com.devismwanzi.pingmonitor.action.START"
        const val EXTRA_HOST = "extra_host"
        const val EXTRA_INTERVAL = "extra_interval"
    }
}
