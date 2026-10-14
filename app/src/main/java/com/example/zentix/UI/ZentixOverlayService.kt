package com.example.zentix.UI

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.zentix.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

class ZentixOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceChannel()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = LayoutInflater.from(this)

        // نافذة العوامة الأساسية للـ FPS والحرارة
        floatingView = try {
            inflater.inflate(R.layout.zentix_floating_overlay, null)
        } catch (e: Exception) {
            null
        }

        if (floatingView != null) {
            val layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            // إعدادات العوامة الأساسية (مرور اللمس عشان ما تعطلش اللعب)
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParamsType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 30
                y = 80
            }

            try {
                windowManager.addView(floatingView, params)
                ZentixMonitorEngine.start(this)
                observeStats(floatingView!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startForegroundServiceChannel() {
        val channelId = "zentix_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Zentix OSD Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "خدمة مراقبة الأداء العائمة نشطة الآن"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Zentix OSD نشط فوق الألعاب")
                .setContentText("جاري عرض مؤشرات الأداء الأساسية بدقة")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Zentix OSD نشط")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        }
        startForeground(1001, notification)
    }

    private fun observeStats(view: View) {
        val layoutCpu = view.findViewById<View>(R.id.layoutCpu)
        val layoutGpu = view.findViewById<View>(R.id.layoutGpu)
        val layoutFps = view.findViewById<View>(R.id.layoutFps)
        val layoutFrametime = view.findViewById<View>(R.id.layoutFrametime)

        val cpuTempTv = view.findViewById<TextView>(R.id.txtCpuTemp)
        val gpuTempTv = view.findViewById<TextView>(R.id.txtGpuTemp)
        val fpsTv = view.findViewById<TextView>(R.id.overlayFpsValue)
        val frametimeTv = view.findViewById<TextView>(R.id.txtFrametime)

        serviceScope.launch {
            try {
                // 1. حرارة المعالج
                launch {
                    combine(ZentixMonitorEngine.showCpuTemp, ZentixMonitorEngine.cpuTempFlow) { isShow, temp ->
                        isShow to temp
                    }.collectLatest { (isShow, temp) ->
                        layoutCpu?.visibility = if (isShow) View.VISIBLE else View.GONE
                        cpuTempTv?.text = "$temp °C"
                    }
                }

                // 2. حرارة كارت الشاشة
                launch {
                    combine(ZentixMonitorEngine.showGpuTemp, ZentixMonitorEngine.gpuTempFlow) { isShow, temp ->
                        isShow to temp
                    }.collectLatest { (isShow, temp) ->
                        layoutGpu?.visibility = if (isShow) View.VISIBLE else View.GONE
                        gpuTempTv?.text = "$temp °C"
                    }
                }

                // 3. الـ FPS
                launch {
                    combine(ZentixMonitorEngine.showFps, ZentixMonitorEngine.fpsFlow) { isShow, fps ->
                        isShow to fps
                    }.collectLatest { (isShow, fps) ->
                        layoutFps?.visibility = if (isShow) View.VISIBLE else View.GONE
                        fpsTv?.text = "$fps"
                    }
                }

                // 4. وقت الإطار Frametime
                launch {
                    combine(ZentixMonitorEngine.showFrametime, ZentixMonitorEngine.frametimeFlow) { isShow, ft ->
                        isShow to ft
                    }.collectLatest { (isShow, ft) ->
                        layoutFrametime?.visibility = if (isShow) View.VISIBLE else View.GONE
                        frametimeTv?.text = ft
                    }
                }
            } catch (_: Exception) {}
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        ZentixMonitorEngine.stop()
        try {
            if (floatingView != null) windowManager.removeView(floatingView)
        } catch (_: Exception) {}
    }
}