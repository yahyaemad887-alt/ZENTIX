package com.example.zentix.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R

// ألوان الثيم الداكن المخصص للجيمنج والأداء
private val BackgroundDark = Color(0xFF090A0F)
private val CardBackground = Color(0xFF12141D)
private val CardBorder = Color(0xFF1E2230)
private val NeonRed = Color(0xFFFF2A4B)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)
private val NeonPurple = Color(0xFFB388FF)
private val NeonAmber = Color(0xFFFFD600)

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAI: () -> Unit = {},
    onNavigateToBenchmark: () -> Unit = {},
    onNavigateToProfiles: () -> Unit = {},
    onNavigateToSentinel: () -> Unit = {},
    onNavigateToOSD: () -> Unit = {},
    onNavigateToOverclock: () -> Unit = {}
) {
    var boostState by remember { mutableStateOf(0) } // 0: Idle, 1: Boosting, 2: Boosted
    var isOsdEnabled by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. الشريط العلوي زر الإعدادات والترجمة
            item {
                TopAppBarSection(onSettingsClick = onNavigateToSettings)
            }

            // 2. شريط الإحصائيات الفورية لشاشة MSI Afterburner Style
            item {
                QuickStatsBar()
            }

            // 3. زر التسريع الفوري المتفاعل
            item {
                BoostActionButton(
                    boostState = boostState,
                    onBoostClick = {
                        if (boostState == 0) {
                            boostState = 1
                            // محاكاة عملية التسريع
                        } else if (boostState == 2) {
                            boostState = 0
                        }
                    }
                )
            }

            // 4. مفاتيح التحكم السريع (Quick Controls Row)
            item {
                QuickTogglesRow(
                    isOsdEnabled = isOsdEnabled,
                    onOsdToggle = { isOsdEnabled = !isOsdEnabled }
                )
            }

            // 5. قسم الذكاء الاصطناعي لتوقع الحرارة والتهنيج
            item {
                ZentixFeatureCard(
                    title = stringResource(id = R.string.sec_ai_title),
                    description = stringResource(id = R.string.sec_ai_desc),
                    badgeText = stringResource(id = R.string.status_ai_active),
                    accentColor = NeonPurple,
                    icon = Icons.Default.Psychology,
                    onClick = onNavigateToAI
                )
            }

            // 6. قسم مسجل ومحلل الاختناق الحراري والأداء
            item {
                ZentixFeatureCard(
                    title = stringResource(id = R.string.sec_benchmark_title),
                    description = stringResource(id = R.string.sec_benchmark_desc),
                    badgeText = stringResource(id = R.string.status_benchmark_ready),
                    accentColor = NeonCyan,
                    icon = Icons.Default.Assessment,
                    onClick = onNavigateToBenchmark
                )
            }

            // 7. قسم البروفايلات الذكية والتخصيص التلقائي
            item {
                ZentixFeatureCard(
                    title = stringResource(id = R.string.sec_profiles_title),
                    description = stringResource(id = R.string.sec_profiles_desc),
                    badgeText = stringResource(id = R.string.status_profiles_auto),
                    accentColor = NeonGreen,
                    icon = Icons.Default.Speed,
                    onClick = onNavigateToProfiles
                )
            }

            // 8. قسم حماية الخلفية وتطبيقات السحب الشاذ
            item {
                ZentixFeatureCard(
                    title = stringResource(id = R.string.sec_sentinel_title),
                    description = stringResource(id = R.string.sec_sentinel_desc),
                    badgeText = stringResource(id = R.string.status_sentinel_protect),
                    accentColor = NeonAmber,
                    icon = Icons.Default.Security,
                    onClick = onNavigateToSentinel
                )
            }

            // 9. قسم الشريط العائم ومسجل الفيديو في الألعاب OSD/HUD
            item {
                ZentixFeatureCard(
                    title = stringResource(id = R.string.sec_osd_title),
                    description = stringResource(id = R.string.sec_osd_desc),
                    badgeText = stringResource(id = R.string.status_osd_overlay),
                    accentColor = NeonRed,
                    icon = Icons.Default.Videocam,
                    onClick = onNavigateToOSD
                )
            }

            // 10. قسم كسر السرعة والتحكم المباشر بالنواة (Advanced Engine)
            item {
                ZentixFeatureCard(
                    title = stringResource(id = R.string.sec_overclock_title),
                    description = stringResource(id = R.string.sec_overclock_desc),
                    badgeText = stringResource(id = R.string.status_root_disabled),
                    accentColor = Color.Gray,
                    icon = Icons.Default.DeveloperBoard,
                    onClick = onNavigateToOverclock
                )
            }
        }
    }
}

// ---------------- مكونات واجهة المستخدم الفرعية ---------------- //

@Composable
private fun TopAppBarSection(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.app_name),
                color = NeonRed,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = stringResource(id = R.string.app_subtitle),
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .background(CardBackground, RoundedCornerShape(12.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.settings_cd),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun QuickStatsBar() {
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatBox(label = stringResource(id = R.string.stat_cpu_temp), value = "41°C", color = NeonCyan)
            StatBox(label = stringResource(id = R.string.stat_ram_usage), value = "62%", color = NeonAmber)
            StatBox(label = stringResource(id = R.string.stat_fps), value = "60 FPS", color = NeonGreen)
            StatBox(label = stringResource(id = R.string.stat_battery_temp), value = "34°C", color = NeonPurple)
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BoostActionButton(boostState: Int, onBoostClick: () -> Unit) {
    val buttonColor by animateColorAsState(
        targetValue = when (boostState) {
            1 -> NeonAmber
            2 -> NeonGreen
            else -> NeonRed
        },
        animationSpec = tween(500),
        label = "BoostColorAnimation"
    )

    Button(
        onClick = onBoostClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (boostState == 2) Icons.Default.CheckCircle else Icons.Default.Bolt,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (boostState) {
                    1 -> stringResource(id = R.string.btn_boosting)
                    2 -> stringResource(id = R.string.btn_boosted)
                    else -> stringResource(id = R.string.btn_boost_now)
                },
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun QuickTogglesRow(isOsdEnabled: Boolean, onOsdToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // FPS Lock Shortcut Button
        OutlinedButton(
            onClick = { /* تغيير معدل الفريمات */ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(id = R.string.quick_fps_lock), color = Color.White, fontSize = 12.sp)
            }
        }

        // OSD Overlay Quick Toggle
        OutlinedButton(
            onClick = onOsdToggle,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isOsdEnabled) NeonRed.copy(alpha = 0.2f) else CardBackground
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isOsdEnabled) NeonRed else CardBorder)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = if (isOsdEnabled) NeonRed else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.quick_osd_toggle),
                    color = if (isOsdEnabled) NeonRed else Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ZentixFeatureCard(
    title: String,
    description: String,
    badgeText: String,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(accentColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = accentColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // شارة الحالة (Status Badge)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = badgeText,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                color = Color.Gray,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}