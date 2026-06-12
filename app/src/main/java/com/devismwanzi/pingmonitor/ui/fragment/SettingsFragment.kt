package com.devismwanzi.pingmonitor.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.devismwanzi.pingmonitor.R
import com.devismwanzi.pingmonitor.databinding.FragmentSettingsBinding
import com.devismwanzi.pingmonitor.service.PingMonitorService

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val sharedPrefs = requireActivity().getSharedPreferences("PingMonitorPrefs", Context.MODE_PRIVATE)
        
        // Pre-fill existing settings
        binding.editTextHost.setText(sharedPrefs.getString("target_host", "8.8.8.8"))
        val currentInterval = sharedPrefs.getLong("ping_interval", 2000L)
        binding.radioGroupInterval.check(
            when (currentInterval) {
                1000L -> R.id.radioFast
                5000L -> R.id.radioSaver
                else -> R.id.radioStandard
            }
        )

        binding.btnSaveSettings.setOnClickListener {
            val host = binding.editTextHost.text.toString().trim()
            if (host.isEmpty()) {
                Toast.makeText(context, "Please enter a valid hostname or IP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val interval = when (binding.radioGroupInterval.checkedRadioButtonId) {
                R.id.radioFast -> 1000L
                R.id.radioSaver -> 5000L
                else -> 2000L
            }

            sharedPrefs.edit().apply {
                putString("target_host", host)
                putLong("ping_interval", interval)
                apply()
            }

            Toast.makeText(context, "Settings saved successfully", Toast.LENGTH_SHORT).show()

            // If the service is running, restart it to apply changes immediately
            if (sharedPrefs.getBoolean("is_monitoring_active", false)) {
                val restartIntent = Intent(context, PingMonitorService::class.java).apply {
                    action = PingMonitorService.ACTION_START
                    putExtra(PingMonitorService.EXTRA_HOST, host)
                    putExtra(PingMonitorService.EXTRA_INTERVAL, interval)
                }
                context?.startService(restartIntent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
