package com.devismwanzi.pingmonitor.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.devismwanzi.pingmonitor.R
import com.devismwanzi.pingmonitor.databinding.FragmentHomeBinding
import com.devismwanzi.pingmonitor.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.currentPing.observe(viewLifecycleOwner) { pingResult ->
            if (pingResult != null) {
                binding.latencyValue.text = pingResult.formattedLatency
                binding.latencyTime.text = pingResult.formattedTime
            }
        }

        viewModel.isMonitoring.observe(viewLifecycleOwner) { isMonitoring ->
            binding.statusIndicator.setImageResource(
                if (isMonitoring) R.drawable.ic_monitoring else R.drawable.ic_stopped
            )
            binding.statusText.text = getString(
                if (isMonitoring) R.string.status_monitoring else R.string.status_stopped
            )
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.errorText.text = error
                binding.errorText.visibility = View.VISIBLE
            } else {
                binding.errorText.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            viewModel.startMonitoring()
        }

        binding.btnStop.setOnClickListener {
            viewModel.stopMonitoring()
        }

        binding.btnPause.setOnClickListener {
            viewModel.pauseMonitoring()
        }

        binding.btnResume.setOnClickListener {
            viewModel.resumeMonitoring()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
