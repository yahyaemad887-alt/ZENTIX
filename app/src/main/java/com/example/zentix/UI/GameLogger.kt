package com.example.zentix.UI

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.StatFs
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)
private val NeonAmber = Color(0xFFFFD600)
private val NeonPurple = Color(0xFFB388FF)

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

    val deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
    val androidVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    val cpuProcessor = getCpuHardwareInfo()
    val coresCount = getCpuCoresCount()

    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memInfo)
    val totalRamGb = String.format(Locale.US, "%.1f GB", memInfo.totalMem / (1024.0 * 1024.0 * 1024.0))
    val availRamGb = String.format(Locale.US, "%.1f GB", memInfo.availMem / (1024.0 * 1024.0 * 1024.0))

    val stat = StatFs(context.filesDir.path)
    val totalStorageGb = String.format(Locale.US, "%.1f GB", stat.totalBytes / (1024.0 * 1024.0 * 1024.0))
    val freeStorageGb = String.format(Locale.US, "%.1f GB", stat.availableBytes / (1024.0 * 1024.0 * 1024.0))

    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    val batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
    val batteryPlugged = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: 0
    val isCharging = batteryPlugged > 0

    // ألوان تفاعلية بناءً على الوضع الداكن العام للتطبيق
    val isDark = ZentixGlobalSettings.isDarkMode
    val backgroundColor by animateColorAsState(if (isDark) Color(0xFF090A0F) else Color(0xFFF4F6FA), animationSpec = tween(300), label = "bg")
    val cardBackground by animateColorAsState(if (isDark) Color(0xFF12141D) else Color(0xFFFFFFFF), animationSpec = tween(300), label = "card")
    val cardBorder by animateColorAsState(if (isDark) Color(0xFF1E2230) else Color(0xFFE0E5F0), animationSpec = tween(300), label = "border")
    val primaryTextColor by animateColorAsState(if (isDark) Color.White else Color(0xFF1A1D26), animationSpec = tween(300), label = "text1")
    val secondaryTextColor by animateColorAsState(if (isDark) Color.Gray else Color(0xFF6B7280), animationSpec = tween(300), label = "text2")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                            color = primaryTextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = stringResource(id = R.string.game_logger_subtitle),
                            color = secondaryTextColor,
                            fontSize = 10.sp
                        )
                    }

                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(cardBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = primaryTextColor
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
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
                            Text(text = stringResource(id = R.string.device_identification), color = primaryTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = cardBorder)
                        HardwareInfoRow(label = stringResource(id = R.string.device_model), value = deviceModel, primaryTextColor, secondaryTextColor)
                        HardwareInfoRow(label = stringResource(id = R.string.cpu_processor), value = cpuProcessor, primaryTextColor, secondaryTextColor)
                        HardwareInfoRow(label = stringResource(id = R.string.system_version), value = androidVersion, primaryTextColor, secondaryTextColor)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
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
                            Text(text = stringResource(id = R.string.memory_storage_status), color = primaryTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = cardBorder)
                        HardwareInfoRow(label = stringResource(id = R.string.total_ram_available), value = "$totalRamGb / $availRamGb", primaryTextColor, secondaryTextColor)
                        HardwareInfoRow(label = stringResource(id = R.string.internal_storage), value = stringResource(id = R.string.storage_format, totalStorageGb, freeStorageGb), primaryTextColor, secondaryTextColor)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
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
                                        text = stringResource(id = R.string.bypass_charging),
                                        color = primaryTextColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isBypassChargingEnabled) stringResource(id = R.string.direct_board_active) else stringResource(id = R.string.standard_charging_mode),
                                        color = if (isBypassChargingEnabled) NeonGreen else secondaryTextColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Switch(
                                checked = isBypassChargingEnabled,
                                onCheckedChange = { isBypassChargingEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = backgroundColor,
                                    checkedTrackColor = NeonCyan,
                                    uncheckedThumbColor = secondaryTextColor,
                                    uncheckedTrackColor = cardBorder
                                )
                            )
                        }

                        HorizontalDivider(color = cardBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = stringResource(id = R.string.battery_level_format, batteryLevel), color = secondaryTextColor, fontSize = 11.sp)
                            Text(text = if (isCharging) stringResource(id = R.string.status_charging) else stringResource(id = R.string.status_discharging), color = NeonAmber, fontSize = 11.sp)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TelemetryMetricCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.cpu_cores),
                        value = stringResource(id = R.string.active_cores_format, coresCount),
                        subtitle = Build.SUPPORTED_ABIS.firstOrNull() ?: "ARM64",
                        accentColor = NeonPurple,
                        icon = Icons.Default.Memory,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )
                    TelemetryMetricCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.power_state),
                        value = if (isCharging) stringResource(id = R.string.connected) else stringResource(id = R.string.battery_mode),
                        subtitle = stringResource(id = R.string.power_source),
                        accentColor = NeonCyan,
                        icon = Icons.Default.Bolt,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun HardwareInfoRow(label: String, value: String, primaryTextColor: Color, secondaryTextColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = secondaryTextColor, fontSize = 11.sp)
        Text(text = value, color = primaryTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TelemetryMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    cardBackground: Color,
    cardBorder: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
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
                Text(text = title, color = secondaryTextColor, fontSize = 11.sp)
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
                Text(text = value, color = primaryTextColor, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}