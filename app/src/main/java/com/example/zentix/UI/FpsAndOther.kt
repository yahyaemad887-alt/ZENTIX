package com.example.zentix.UI

import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R

private val BackgroundDark = Color(0xFF090A0F)
private val CardBackground = Color(0xFF12141D)
private val CardBorder = Color(0xFF1E2230)
private val NeonRed = Color(0xFFFF2A4B)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)

@Composable
fun FpsAndOtherScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current

    // السويتش يبدأ مطفأ افتراضياً عند الدخول للصفحة
    var isMasterOsdEnabled by remember { mutableStateOf(false) }

    val showCpuTemp by ZentixMonitorEngine.showCpuTemp.collectAsState()
    val showGpuTemp by ZentixMonitorEngine.showGpuTemp.collectAsState()
    val showFramerate by ZentixMonitorEngine.showFps.collectAsState()
    val showFrametime by ZentixMonitorEngine.showFrametime.collectAsState()

    val frametimeValues by ZentixMonitorEngine.frametimeList.collectAsState()
    val uiAlpha = if (isMasterOsdEnabled) 1.0f else 0.4f

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
                                .background(CardBackground, RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Column {
                            Text(
                                text = "FPS and Other",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "OSD Performance Monitor",
                                color = Color.Gray,
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
                        .border(1.dp, if (isMasterOsdEnabled) NeonGreen else CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                                    .background(if (isMasterOsdEnabled) NeonGreen.copy(alpha = 0.15f) else CardBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = if (isMasterOsdEnabled) NeonGreen else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(text = "Master OSD Control", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "إظهار أو إخفاء مؤشرات الأداء العائمة", color = Color.Gray, fontSize = 10.sp)
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
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(text = "Frametime Live Graph", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OsdToggleRow("CPU Temperature", showCpuTemp, { ZentixMonitorEngine.showCpuTemp.value = it }, isMasterOsdEnabled, NeonRed)
                        HorizontalDivider(color = CardBorder, thickness = 1.dp)
                        OsdToggleRow("GPU Temperature", showGpuTemp, { ZentixMonitorEngine.showGpuTemp.value = it }, isMasterOsdEnabled, NeonRed)
                        HorizontalDivider(color = CardBorder, thickness = 1.dp)
                        OsdToggleRow("Framerate (FPS Counter)", showFramerate, { ZentixMonitorEngine.showFps.value = it }, isMasterOsdEnabled, NeonGreen)
                        HorizontalDivider(color = CardBorder, thickness = 1.dp)
                        OsdToggleRow("Frametime Graph", showFrametime, { ZentixMonitorEngine.showFrametime.value = it }, isMasterOsdEnabled, NeonCyan)
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
    color: Color
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
                    .background(if (checked && isEnabled) color else Color.Gray, CircleShape)
            )
            Text(
                text = label,
                color = if (isEnabled) (if (checked) Color.White else Color.Gray) else Color.DarkGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = isEnabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BackgroundDark,
                checkedTrackColor = color,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = CardBorder,
                disabledCheckedThumbColor = Color.DarkGray,
                disabledCheckedTrackColor = Color.Gray.copy(alpha = 0.3f),
                disabledUncheckedThumbColor = Color.DarkGray,
                disabledUncheckedTrackColor = Color.Gray.copy(alpha = 0.1f)
            )
        )
    }
}