package com.example.zentix.UI

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R

// ألوان ثيم ZENTIX الداكن الاحترافي
private val BackgroundDark = Color(0xFF090A0F)
private val CardBackground = Color(0xFF12141D)
private val CardBorder = Color(0xFF1E2230)
private val NeonRed = Color(0xFFFF2A4B)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)
private val NeonAmber = Color(0xFFFFD600)
private val NeonPurple = Color(0xFFB388FF)

@Composable
fun FpsAndOtherScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler {
        onNavigateBack()
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: OSD, 1: Recorder

    // مفتاح التشغيل الرئيسي للـ OSD
    var isMasterOsdEnabled by remember { mutableStateOf(true) }
    var pollingPeriodMs by remember { mutableFloatStateOf(1000f) }

    // عناصر المراقبة الشاملة المستوحاة من Afterburner
    var showGpuUsage by remember { mutableStateOf(true) }
    var showMemoryUsage by remember { mutableStateOf(true) }
    var showRamProcess by remember { mutableStateOf(true) }
    var showCommitCharge by remember { mutableStateOf(true) }
    var showCoreClock by remember { mutableStateOf(true) }
    var showCpuPower by remember { mutableStateOf(true) }
    var showCpuTemp by remember { mutableStateOf(true) }
    var showBusUsage by remember { mutableStateOf(true) }
    var showNoLoadLimit by remember { mutableStateOf(true) }
    var showFramerate by remember { mutableStateOf(true) }
    var showFrametime by remember { mutableStateOf(true) }
    var showFpsAvg by remember { mutableStateOf(true) }
    var showFpsMax by remember { mutableStateOf(true) }
    var showFps1Low by remember { mutableStateOf(true) }
    var showFps01Low by remember { mutableStateOf(true) }

    // إعدادات الفيديو
    var videoQuality by remember { mutableFloatStateOf(85f) }
    var captureFramerate by remember { mutableFloatStateOf(30f) }
    var isMjpgCompression by remember { mutableStateOf(true) }
    var isMkvContainer by remember { mutableStateOf(true) }

    val uiAlpha = if (isMasterOsdEnabled || selectedTab == 1) 1.0f else 0.4f

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
                                text = stringResource(id = R.string.fps_screen_title),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = stringResource(id = R.string.fps_screen_subtitle),
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 2. شريط التبويبات
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground, RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.tab_osd_monitoring),
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    TabButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.tab_recorder),
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                }
            }

            if (selectedTab == 0) {
                // --- مفتاح التشغيل الرئيسي للـ OSD ---
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
                                    Text(text = stringResource(id = R.string.master_osd_switch), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = stringResource(id = R.string.master_osd_desc), color = Color.Gray, fontSize = 10.sp)
                                }
                            }

                            Switch(
                                checked = isMasterOsdEnabled,
                                onCheckedChange = { isMasterOsdEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = NeonGreen)
                            )
                        }
                    }
                }

                // --- معدل التحديث Polling Period ---
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
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = stringResource(id = R.string.section_polling_rate), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${pollingPeriodMs.toInt()} ms", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            }

                            Slider(
                                value = pollingPeriodMs,
                                onValueChange = { pollingPeriodMs = it },
                                valueRange = 250f..3000f,
                                steps = 10,
                                enabled = isMasterOsdEnabled,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(id = R.string.section_all_graphs),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // قائمة المراقبة الشاملة المستخرجة من كل الصور
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
                            OsdToggleRow("GPU Usage", showGpuUsage, { showGpuUsage = it }, isMasterOsdEnabled, NeonCyan)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Memory / VRAM Usage", showMemoryUsage, { showMemoryUsage = it }, isMasterOsdEnabled, NeonAmber)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("RAM Usage  Process", showRamProcess, { showRamProcess = it }, isMasterOsdEnabled, NeonAmber)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Commit Charge", showCommitCharge, { showCommitCharge = it }, isMasterOsdEnabled, NeonPurple)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Core Clock Frequency", showCoreClock, { showCoreClock = it }, isMasterOsdEnabled, NeonPurple)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("CPU Power Draw", showCpuPower, { showCpuPower = it }, isMasterOsdEnabled, NeonGreen)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("CPU Temperatures", showCpuTemp, { showCpuTemp = it }, isMasterOsdEnabled, NeonRed)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("GPU Temperature", showBusUsage, { showBusUsage = it }, isMasterOsdEnabled, NeonRed)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("BUS Usage", showNoLoadLimit, { showNoLoadLimit = it }, isMasterOsdEnabled, NeonCyan)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("No Load Limit", showNoLoadLimit, { showNoLoadLimit = it }, isMasterOsdEnabled, NeonAmber)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Framerate (FPS Counter)", showFramerate, { showFramerate = it }, isMasterOsdEnabled, NeonGreen)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Frametime Graph", showFrametime, { showFrametime = it }, isMasterOsdEnabled, NeonCyan)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Framerate Avg", showFpsAvg, { showFpsAvg = it }, isMasterOsdEnabled, NeonGreen)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Framerate Max", showFpsMax, { showFpsMax = it }, isMasterOsdEnabled, NeonGreen)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Framerate 1% Low", showFps1Low, { showFps1Low = it }, isMasterOsdEnabled, NeonAmber)
                            HorizontalDivider(color = CardBorder, thickness = 1.dp)
                            OsdToggleRow("Framerate 0.1% Low", showFps01Low, { showFps01Low = it }, isMasterOsdEnabled, NeonRed)
                        }
                    }
                }

            } else {
                // --- تبويب تسجيل الفيديو مع مؤشر الحالة وزر فتح المجلد ---
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonCyan, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(NeonGreen, CircleShape)
                                )
                                Text(
                                    text = stringResource(id = R.string.recorder_status_active),
                                    color = NeonGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(id = R.string.section_video_properties),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

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
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // جودة الفيديو
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Video Quality", color = Color.White, fontSize = 12.sp)
                                Text(text = "${videoQuality.toInt()}%", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = videoQuality,
                                onValueChange = { videoQuality = it },
                                valueRange = 25f..100f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                            )

                            HorizontalDivider(color = CardBorder, thickness = 1.dp)

                            // معدل الإطارات
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Capture Framerate Limit", color = Color.White, fontSize = 12.sp)
                                Text(text = "${captureFramerate.toInt()} FPS", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = captureFramerate,
                                onValueChange = { captureFramerate = it },
                                valueRange = 15f..100f,
                                steps = 16,
                                colors = SliderDefaults.colors(thumbColor = NeonGreen, activeTrackColor = NeonGreen)
                            )

                            HorizontalDivider(color = CardBorder, thickness = 1.dp)

                            // خيار ضغط MJPG
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "MJPG Hardware Compression", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "High speed encoding for real-time capturing.", color = Color.Gray, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = isMjpgCompression,
                                    onCheckedChange = { isMjpgCompression = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                                )
                            }

                            HorizontalDivider(color = CardBorder, thickness = 1.dp)

                            // صيغة الحاوية Matroska MKV
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Matroska MKV Container", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Prevents file corruption upon unexpected crashes.", color = Color.Gray, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = isMkvContainer,
                                    onCheckedChange = { isMkvContainer = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = NeonGreen)
                                )
                            }

                            HorizontalDivider(color = CardBorder, thickness = 1.dp)

                            // زر فتح مجلد حفظ الفيديوهات (Open Folder Button)
                            Button(
                                onClick = { /* Intent لفتح مجلد التخزين في مدير الملفات */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CardBorder)
                            ) {
                                Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(id = R.string.btn_open_folder), color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- مكونات فرعية ---------------- //

@Composable
private fun TabButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(10.dp))
            .border(1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) NeonCyan else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
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
                uncheckedTrackColor = CardBorder
            )
        )
    }
}