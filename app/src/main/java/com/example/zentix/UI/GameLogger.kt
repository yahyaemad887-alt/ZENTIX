package com.example.zentix.UI

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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

// نموذج بيانات جلسة اللعب
data class AdvancedGameSession(
    val id: Int,
    val gameName: String,
    val engineType: String,
    val isHeavy: Boolean, // لتحديد الفلترة
    val duration: String,
    val avgFps: String,
    val lowFps: String,
    val peakTemp: String,
    val powerDraw: String,
    val date: String,
    val accentColor: Color
)

@Composable
fun GameLoggerScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler {
        onNavigateBack()
    }

    // 0: Analytics & Hardware, 1: Session History
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("All") }
    var isBypassChargingEnabled by remember { mutableStateOf(true) } // ميزة هاردوير حصرية

    val allSessions = remember {
        listOf(
            AdvancedGameSession(1, "Mafia II Classic", "Winlator 11 Ajay • DXVK", true, "2h 15m", "58 FPS", "48 FPS", "45°C", "38W", "Today", NeonRed),
            AdvancedGameSession(2, "GTA IV", "Winlator Ludashi • Turnip", true, "1h 40m", "42 FPS", "31 FPS", "55°C", "45W", "Yesterday", NeonCyan),
            AdvancedGameSession(3, "Blur", "Native Port • Adreno", false, "45m", "60 FPS", "58 FPS", "39°C", "28W", "Oct 10", NeonGreen),
            AdvancedGameSession(4, "God of War PSP", "PPSSPP Gold • Vulkan", false, "1h 10m", "50 FPS", "46 FPS", "41°C", "22W", "Oct 08", NeonAmber)
        )
    }

    var sessionList by remember { mutableStateOf(allSessions) }

    // تطبيق الفلترة الحقيقية عند التبديل
    val filteredSessions = remember(selectedFilter, sessionList) {
        if (selectedFilter == "Heavy") {
            sessionList.filter { it.isHeavy }
        } else {
            sessionList
        }
    }

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
            // 1. الهيدر العلوي بدون مربعات سوداء فاضية
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
                            text = "Hardware Telemetry & Performance Suite",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // زر الرجوع المباشر
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

                        // زر مسح السجلات
                        IconButton(
                            onClick = { sessionList = emptyList() },
                            modifier = Modifier
                                .size(40.dp)
                                .background(CardBackground, RoundedCornerShape(12.dp))
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Logs",
                                tint = NeonRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 2. شريط التبويبات (Tabs Switcher)
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
                        text = stringResource(id = R.string.tab_analytics),
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    TabButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.tab_history),
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                }
            }

            if (selectedTab == 0) {
                // --- تبويب التحليلات وهندسة الهاردوير (Analytics Dashboard) ---
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // ميزة حصرية جديدة: Bypass Charging & Power Direct
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
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
                                            text = stringResource(id = R.string.section_bypass_charging),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isBypassChargingEnabled)
                                                stringResource(id = R.string.status_bypass_active)
                                            else
                                                stringResource(id = R.string.status_bypass_disabled),
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
                        }

                        // الصف الأول من المؤشرات
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TelemetryMetricCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.metric_stability),
                                value = "96.4%",
                                subtitle = "Stable Frametime",
                                accentColor = NeonGreen,
                                icon = Icons.Default.Verified
                            )
                            TelemetryMetricCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.metric_power),
                                value = if (isBypassChargingEnabled) "22.1W (Direct)" else "34.5W",
                                subtitle = if (isBypassChargingEnabled) "Bypass Active" else "Battery Charge",
                                accentColor = NeonCyan,
                                icon = Icons.Default.Bolt
                            )
                        }

                        // الصف الثاني من المؤشرات
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TelemetryMetricCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.metric_driver),
                                value = "Turnip v24.3",
                                subtitle = "Vulkan Driver",
                                accentColor = NeonPurple,
                                icon = Icons.Default.Memory
                            )
                            TelemetryMetricCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.metric_vram),
                                value = "5.2 GB",
                                subtitle = "Dedicated Usage",
                                accentColor = NeonAmber,
                                icon = Icons.Default.Storage
                            )
                        }

                        // قسم الحالة الحرارية
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
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Thermostat,
                                            contentDescription = null,
                                            tint = NeonAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = stringResource(id = R.string.section_thermal_health),
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "0 Throttling Events",
                                        color = NeonGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { if (isBypassChargingEnabled) 0.35f else 0.55f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = if (isBypassChargingEnabled) NeonGreen else NeonAmber,
                                    trackColor = CardBorder,
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isBypassChargingEnabled) "CPU Peak: 38°C (Cool)" else "CPU Peak: 48°C",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                    Text(text = "GPU Safe Limit: 75°C", color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }

                        // قسم توزيع الفريمات
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
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.section_fps_breakdown),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                FpsDistributionBar(label = "60 FPS (Fluid)", percentage = 0.75f, color = NeonGreen)
                                FpsDistributionBar(label = "30-59 FPS (Playable)", percentage = 0.20f, color = NeonAmber)
                                FpsDistributionBar(label = "<30 FPS (Drops)", percentage = 0.05f, color = NeonRed)
                            }
                        }
                    }
                }
            } else {
                // --- تبويب سجلات الجلسات (Session History) ---
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChipItem(
                                label = stringResource(id = R.string.filter_all),
                                isSelected = selectedFilter == "All",
                                onClick = { selectedFilter = "All" }
                            )
                            FilterChipItem(
                                label = stringResource(id = R.string.filter_heavy),
                                isSelected = selectedFilter == "Heavy",
                                onClick = { selectedFilter = "Heavy" }
                            )
                        }

                        Text(
                            text = stringResource(id = R.string.section_all_sessions),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (filteredSessions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No telemetry records found for this filter.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(filteredSessions) { session ->
                        AdvancedGameSessionCard(session = session)
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
            .background(
                if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isSelected) NeonCyan else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
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
                Text(text = value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FpsDistributionBar(label: String, percentage: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.Gray, fontSize = 11.sp)
            Text(text = "${(percentage * 100).toInt()}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = CardBorder
        )
    }
}

@Composable
private fun FilterChipItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) NeonCyan.copy(alpha = 0.2f) else CardBackground,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                if (isSelected) NeonCyan else CardBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) NeonCyan else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdvancedGameSessionCard(session: AdvancedGameSession) {
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
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(session.accentColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = session.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = session.gameName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = session.engineType,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = session.duration,
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = CardBorder, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBadge(label = "Avg FPS", value = session.avgFps, color = NeonGreen)
                StatBadge(label = "1% Low", value = session.lowFps, color = NeonAmber)
                StatBadge(label = "Peak Temp", value = session.peakTemp, color = NeonRed)
                StatBadge(label = "Power", value = session.powerDraw, color = NeonCyan)
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}