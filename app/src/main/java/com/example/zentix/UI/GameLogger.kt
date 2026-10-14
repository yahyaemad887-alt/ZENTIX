package com.example.zentix.UI

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R
import java.io.File
import java.util.Locale

// ألوان ثيم ZENTIX الداكن الاحترافي
private val BackgroundDark = Color(0xFF090A0F)
private val CardBackground = Color(0xFF12141D)
private val CardBorder = Color(0xFF1E2230)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)
private val NeonAmber = Color(0xFFFFD600)
private val NeonPurple = Color(0xFFB388FF)

// دالة ديناميكية لقراءة اسم المعالج الحقيقي من نظام الهاتف
private fun getCpuHardwareInfo(): String {
    return try {
        val cpuInfo = File("/proc/cpuinfo").readText()
        val line = cpuInfo.lines().find { it.startsWith("Hardware") || it.startsWith("model name") }
        if (line != null) {
            line.substringAfter(":").trim()
        } else {
            Build.HARDWARE
        }
    } catch (e: Exception) {
        Build.HARDWARE
    }
}

// دالة لحساب عدد الأنوية الحقيقية المتاحة في معالج الهاتف الحالي
private fun getCpuCoresCount(): Int {
    return Runtime.getRuntime().availableProcessors()
}

@Composable
fun GameLoggerScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    var isBypassChargingEnabled by remember { mutableStateOf(true) }

    // --- قراءة بيانات الهاردوير الحقيقية للجهاز الحالي ديناميكياً ---
    val deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
    val androidVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    val cpuProcessor = getCpuHardwareInfo()
    val coresCount = getCpuCoresCount()

    // قراءة الرامات الحقيقية
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memInfo)
    val totalRamGb = String.format(Locale.US, "%.1f GB", memInfo.totalMem / (1024.0 * 1024.0 * 1024.0))
    val availRamGb = String.format(Locale.US, "%.1f GB", memInfo.availMem / (1024.0 * 1024.0 * 1024.0))

    // قراءة التخزين الداخلي الحقيقي بطريقة سليمة
    val stat = StatFs(context.filesDir.path)
    val totalStorageGb = String.format(Locale.US, "%.1f GB", stat.totalBytes / (1024.0 * 1024.0 * 1024.0))
    val freeStorageGb = String.format(Locale.US, "%.1f GB", stat.availableBytes / (1024.0 * 1024.0 * 1024.0))

    // قراءة حالة البطارية الحقيقية
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    val batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
    val batteryPlugged = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: 0
    val isCharging = batteryPlugged > 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. الهيدر العلوي
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.game_logger_title),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Real-Time Hardware & Performance Suite",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }

                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(CardBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }

            // 2. كارت معلومات الهاتف الأساسية
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Text(text = "Device Identification", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = CardBorder)
                        HardwareInfoRow(label = "Device Model", value = deviceModel)
                        HardwareInfoRow(label = "CPU Processor", value = cpuProcessor)
                        HardwareInfoRow(label = "System Version", value = androidVersion)
                    }
                }
            }

            // 3. كارت الذاكرة العشوائية (RAM) والتخزين
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                            Text(text = "Memory & Storage Status", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = CardBorder)
                        HardwareInfoRow(label = "Total RAM / Available", value = "$totalRamGb / $availRamGb")
                        HardwareInfoRow(label = "Internal Storage (App Dir)", value = "Total: $totalStorageGb | Free: $freeStorageGb")
                    }
                }
            }

            // 4. ميزة Bypass Charging والبطارية
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NeonCyan.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Power,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Bypass Charging",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isBypassChargingEnabled) "Direct Board Power Active" else "Standard Charging Mode",
                                        color = if (isBypassChargingEnabled) NeonGreen else Color.Gray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Switch(
                                checked = isBypassChargingEnabled,
                                onCheckedChange = { isBypassChargingEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BackgroundDark,
                                    checkedTrackColor = NeonCyan,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = CardBorder
                                )
                            )
                        }

                        HorizontalDivider(color = CardBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Battery Level: $batteryLevel%", color = Color.Gray, fontSize = 11.sp)
                            Text(text = if (isCharging) "Status: Charging" else "Status: Discharging", color = NeonAmber, fontSize = 11.sp)
                        }
                    }
                }
            }

            // 5. مؤشرات الأداء الديناميكية
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TelemetryMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "CPU Cores",
                        value = "$coresCount Active",
                        subtitle = Build.SUPPORTED_ABIS.firstOrNull() ?: "ARM64",
                        accentColor = NeonPurple,
                        icon = Icons.Default.Memory
                    )
                    TelemetryMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Power State",
                        value = if (isCharging) "Connected" else "Battery Mode",
                        subtitle = "Power Source",
                        accentColor = NeonCyan,
                        icon = Icons.Default.Bolt
                    )
                }
            }
        }
    }
}

// ---------------- مكونات فرعية ---------------- //

@Composable
private fun HardwareInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TelemetryMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color.Gray, fontSize = 11.sp)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
                }
            }

            Column {
                Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}