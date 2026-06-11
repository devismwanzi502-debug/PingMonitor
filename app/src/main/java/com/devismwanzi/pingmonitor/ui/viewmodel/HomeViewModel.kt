package com.devismwanzi.pingmonitor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devismwanzi.pingmonitor.domain.model.PingResult
import com.devismwanzi.pingmonitor.domain.repository.PingRepository
import com.devismwanzi.pingmonitor.domain.usecase.PerformPingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pingRepository: PingRepository,
    private val performPingUseCase: PerformPingUseCase
) : ViewModel() {

    private val _currentPing = MutableLiveData<PingResult?>(null)
    val currentPing: LiveData<PingResult?> = _currentPing

    private val _isMonitoring = MutableLiveData(false)
    val isMonitoring: LiveData<Boolean> = _isMonitoring

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _targetHost = MutableLiveData("8.8.8.8")
    val targetHost: LiveData<String> = _targetHost

    private val _pingInterval = MutableLiveData(5000L) // 5 seconds
    val pingInterval: LiveData<Long> = _pingInterval

    private var monitoringJob: Job? = null

    val allPings: LiveData<List<PingResult>> = pingRepository.getAllPings()

    fun startMonitoring(host: String = _targetHost.value ?: "8.8.8.8") {
        if (_isMonitoring.value == true) return

        _targetHost.value = host
        _isMonitoring.value = true
        _errorMessage.value = null

        monitoringJob = viewModelScope.launch {
            while (_isMonitoring.value == true) {
                try {
                    val result = performPingUseCase.execute(host)
                    _currentPing.value = result
                    Timber.d("Ping result: ${result.latencyMs}ms")
                    delay(_pingInterval.value ?: 5000L)
                } catch (e: Exception) {
                    Timber.e(e, "Error during monitoring")
                    _errorMessage.value = e.message
                    delay(1000)
                }
            }
        }
    }

    fun stopMonitoring() {
        _isMonitoring.value = false
        monitoringJob?.cancel()
    }

    fun pauseMonitoring() {
        _isMonitoring.value = false
    }

    fun resumeMonitoring() {
        startMonitoring(_targetHost.value ?: "8.8.8.8")
    }

    fun setTargetHost(host: String) {
        _targetHost.value = host
    }

    fun setPingInterval(intervalMs: Long) {
        _pingInterval.value = intervalMs
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}
