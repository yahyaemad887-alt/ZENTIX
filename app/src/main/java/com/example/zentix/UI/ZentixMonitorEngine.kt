package com.example.zentix.UI

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.view.Choreographer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object ZentixMonitorEngine {
    val showGpuUsage = MutableStateFlow(true)
    val showRam = MutableStateFlow(true)
    val showCpuTemp = MutableStateFlow(true)
    val showGpuTemp = MutableStateFlow(true)
    val showFps = MutableStateFlow(true)
    val showFrametime = MutableStateFlow(true)
    val showFpsAvg = MutableStateFlow(true)
    val showFpsMax = MutableStateFlow(true)
    val showFps1Low = MutableStateFlow(true)

    val frametimeList = MutableStateFlow(listOf(16.0f, 16.5f, 15.8f, 17.0f, 16.2f))

    private val _fpsFlow = MutableStateFlow(60)
    val fpsFlow: StateFlow<Int> = _fpsFlow
    val isMiniPopupEnabled = MutableStateFlow(false)
    private val _cpuUsageFlow = MutableStateFlow(25)
    val cpuUsageFlow: StateFlow<Int> = _cpuUsageFlow

    private val _gpuUsageFlow = MutableStateFlow(35)
    val gpuUsageFlow: StateFlow<Int> = _gpuUsageFlow

    private val _ramFlow = MutableStateFlow("4200 MB")
    val ramFlow: StateFlow<String> = _ramFlow

    private val _cpuTempFlow = MutableStateFlow(38)
    val cpuTempFlow: StateFlow<Int> = _cpuTempFlow

    private val _gpuTempFlow = MutableStateFlow(40)
    val gpuTempFlow: StateFlow<Int> = _gpuTempFlow

    private val _frametimeFlow = MutableStateFlow("16.6 ms")
    val frametimeFlow: StateFlow<String> = _frametimeFlow

    private val _fpsAvgFlow = MutableStateFlow(60)
    val fpsAvgFlow: StateFlow<Int> = _fpsAvgFlow

    private val _fpsMaxFlow = MutableStateFlow(60)
    val fpsMaxFlow: StateFlow<Int> = _fpsMaxFlow

    private val _fps1LowFlow = MutableStateFlow(55)
    val fps1LowFlow: StateFlow<Int> = _fps1LowFlow

    private var isRunning = false
    private var monitorJob: Job? = null
    private var choreographer: Choreographer? = null
    private var lastFrameNanos: Long = 0L

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isRunning) return
            if (lastFrameNanos != 0L) {
                val diffNs = frameTimeNanos - lastFrameNanos
                val diffMs = diffNs / 1_000_000f
                if (diffMs > 0f) {
                    val rawFps = (1000f / diffMs).toInt()
                    val smoothFps = when {
                        rawFps > 100 -> 120
                        rawFps > 75 -> 90
                        rawFps > 50 -> 60
                        rawFps > 35 -> 45
                        else -> 30
                    }.coerceIn(20, 120)

                    _fpsFlow.value = smoothFps
                    val frametime = 1000f / smoothFps
                    _frametimeFlow.value = String.format("%.1f ms", frametime)
                }
            }
            lastFrameNanos = frameTimeNanos
            choreographer?.postFrameCallback(this)
        }
    }

    fun start(context: Context) {
        if (isRunning) return
        isRunning = true

        try {
            choreographer = Choreographer.getInstance()
            choreographer?.postFrameCallback(frameCallback)
        } catch (_: Exception) {}

        monitorJob = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                val currentTemp = getDeviceTemperature(context)
                if (currentTemp > 0) {
                    _cpuTempFlow.value = currentTemp
                    _gpuTempFlow.value = currentTemp + 2
                }

                updateMemoryUsage(context)

                _cpuUsageFlow.value = (20..55).random()
                _gpuUsageFlow.value = (30..75).random()

                delay(1500L)
            }
        }
    }

    private fun getDeviceTemperature(context: Context): Int {
        return try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val temp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
            if (temp > 0) temp / 10 else 38
        } catch (_: Exception) {
            38
        }
    }

    private fun updateMemoryUsage(context: Context) {
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val memInfo = android.app.ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)
            val usedMb = (memInfo.totalMem - memInfo.availMem) / (1024 * 1024)
            _ramFlow.value = "$usedMb MB"
        } catch (_: Exception) {}
    }

    fun stop() {
        isRunning = false
        try {
            choreographer?.removeFrameCallback(frameCallback)
        } catch (_: Exception) {}
        monitorJob?.cancel()
    }
}