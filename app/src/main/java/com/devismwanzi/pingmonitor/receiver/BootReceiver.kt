package com.devismwanzi.pingmonitor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devismwanzi.pingmonitor.service.PingMonitorService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Device booted, starting PingMonitorService")
            context?.let {
                val serviceIntent = Intent(it, PingMonitorService::class.java)
                it.startService(serviceIntent)
            }
        }
    }
}
