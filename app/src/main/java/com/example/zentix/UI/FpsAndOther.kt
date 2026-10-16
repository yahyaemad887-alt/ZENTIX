package com.example.zentix.UI

import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R

private val NeonRed = Color(0xFFFF2A4B)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)

@Composable
fun FpsAndOtherScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current

    var isMasterOsdEnabled by remember { mutableStateOf(false) }

    val showCpuTemp by ZentixMonitorEngine.showCpuTemp.collectAsState()
    val showGpuTemp by ZentixMonitorEngine.showGpuTemp.collectAsState()
    val showFramerate by ZentixMonitorEngine.showFps.collectAsState()
    val showFrametime by ZentixMonitorEngine.showFrametime.collectAsState()

    val frametimeValues by ZentixMonitorEngine.frametimeList.collectAsState()
    val uiAlpha = if (isMasterOsdEnabled) 1.0f else 0.4f

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .background(cardBackground, RoundedCornerShape(12.dp))
                                .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = primaryTextColor
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.fps_monitor_title),
                                color = primaryTextColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = stringResource(R.string.osd_monitor_subtitle),
                                color = secondaryTextColor,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isMasterOsdEnabled) NeonGreen else cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (isMasterOsdEnabled) NeonGreen.copy(alpha = 0.15f) else cardBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = if (isMasterOsdEnabled) NeonGreen else secondaryTextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(text = stringResource(R.string.master_osd_control), color = primaryTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.master_osd_desc), color = secondaryTextColor, fontSize = 10.sp)
                            }
                        }

                        Switch(
                            checked = isMasterOsdEnabled,
                            onCheckedChange = { isChecked ->
                                if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.canDrawOverlays(context)) {
                                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, android.net.Uri.parse("package:${context.packageName}"))
                                    context.startActivity(intent)
                                    isMasterOsdEnabled = false
                                } else {
                                    isMasterOsdEnabled = isChecked
                                    val serviceIntent = Intent(context, ZentixOverlayService::class.java)
                                    if (isChecked) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            context.startForegroundService(serviceIntent)
                                        } else {
                                            context.startService(serviceIntent)
                                        }
                                    } else {
                                        context.stopService(serviceIntent)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = NeonGreen)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(text = stringResource(R.string.frametime_live_graph), color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            val points = frametimeValues
                            if (points.size > 1) {
                                val stepX = width / (points.size - 1)
                                for (i in 0 until points.size - 1) {
                                    val startX = i * stepX
                                    val startY = height - ((points[i] / 50.0f) * height).coerceIn(0f, height)
                                    val endX = (i + 1) * stepX
                                    val endY = height - ((points[i + 1] / 50.0f) * height).coerceIn(0f, height)
                                    drawLine(
                                        color = NeonGreen,
                                        start = Offset(startX, startY),
                                        end = Offset(endX, endY),
                                        strokeWidth = 3f
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(uiAlpha)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OsdToggleRow(stringResource(R.string.cpu_temperature), showCpuTemp, { ZentixMonitorEngine.showCpuTemp.value = it }, isMasterOsdEnabled, NeonRed, primaryTextColor, secondaryTextColor, cardBorder, backgroundColor)
                        HorizontalDivider(color = cardBorder, thickness = 1.dp)
                        OsdToggleRow(stringResource(R.string.gpu_temperature), showGpuTemp, { ZentixMonitorEngine.showGpuTemp.value = it }, isMasterOsdEnabled, NeonRed, primaryTextColor, secondaryTextColor, cardBorder, backgroundColor)
                        HorizontalDivider(color = cardBorder, thickness = 1.dp)
                        OsdToggleRow(stringResource(R.string.framerate_counter), showFramerate, { ZentixMonitorEngine.showFps.value = it }, isMasterOsdEnabled, NeonGreen, primaryTextColor, secondaryTextColor, cardBorder, backgroundColor)
                        HorizontalDivider(color = cardBorder, thickness = 1.dp)
                        OsdToggleRow(stringResource(R.string.frametime_graph), showFrametime, { ZentixMonitorEngine.showFrametime.value = it }, isMasterOsdEnabled, NeonCyan, primaryTextColor, secondaryTextColor, cardBorder, backgroundColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun OsdToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isEnabled: Boolean,
    color: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    cardBorder: Color,
    backgroundDark: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(if (checked && isEnabled) color else secondaryTextColor, CircleShape)
            )
            Text(
                text = label,
                color = if (isEnabled) (if (checked) primaryTextColor else secondaryTextColor) else secondaryTextColor.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = isEnabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = backgroundDark,
                checkedTrackColor = color,
                uncheckedThumbColor = secondaryTextColor,
                uncheckedTrackColor = cardBorder,
                disabledCheckedThumbColor = secondaryTextColor,
                disabledCheckedTrackColor = color.copy(alpha = 0.3f),
                disabledUncheckedThumbColor = secondaryTextColor,
                disabledUncheckedTrackColor = secondaryTextColor.copy(alpha = 0.1f)
            )
        )
    }
}