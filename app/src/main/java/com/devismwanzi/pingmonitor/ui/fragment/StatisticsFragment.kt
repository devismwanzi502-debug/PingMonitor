package com.devismwanzi.pingmonitor.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.devismwanzi.pingmonitor.databinding.FragmentStatisticsBinding
import com.devismwanzi.pingmonitor.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChartStyling()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allPings.collectLatest { pings ->
                if (pings.isEmpty()) {
                    binding.lineChart.visibility = View.GONE
                    binding.textNoData.visibility = View.VISIBLE
                    return@collectLatest
                }

                binding.lineChart.visibility = View.VISIBLE
                binding.textNoData.visibility = View.GONE

                // Graph the last 50 data points chronologically
                val entries = pings.take(50).reversed().mapIndexed { index, pingResult ->
                    Entry(index.toFloat(), pingResult.latencyMs.toFloat())
                }

                val dataSet = LineDataSet(entries, "Latency (ms)").apply {
                    color = Color.parseColor("#00FF66")
                    setCircleColor(Color.parseColor("#00FF66"))
                    lineWidth = 2f
                    circleRadius = 3f
                    setDrawCircleHole(false)
                    valueTextColor = Color.WHITE
                    setDrawFilled(true)
                    fillColor = Color.parseColor("#1B5E20")
                }

                binding.lineChart.data = LineData(dataSet)
                binding.lineChart.invalidate()
            }
        }
    }

    private fun setupChartStyling() {
        binding.lineChart.apply {
            description.isEnabled = false
            legend.textColor = Color.WHITE
            setBackgroundColor(Color.parseColor("#121212"))
            xAxis.apply {
                textColor = Color.WHITE
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
