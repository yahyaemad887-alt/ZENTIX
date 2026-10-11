package com.example.zentix.UI

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMSISuite: () -> Unit = {},
    onNavigateToBenchmark: () -> Unit = {},
    onNavigateToProfiles: () -> Unit = {},
    onNavigateToOverclock: () -> Unit = {}
) {
    var boostState by remember { mutableStateOf(0) } // 0: Normal, 1: Boosting

    // قائمة الألعاب الافتراضية للاختصارات السريعة
    var savedGames by remember { mutableStateOf(listOf("Mafia II", "GTA IV", "Blur")) }
    var showAddGameDialog by remember { mutableStateOf(false) }

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
            // 1. الهيدر العلوي: الشعار + اسم التطبيق + زر البوست الصغير + زر الإعدادات
            item {
                TopAppBarSection(
                    boostState = boostState,
                    onBoostClick = { boostState = if (boostState == 0) 1 else 0 },
                    onSettingsClick = onNavigateToSettings
                )
            }

            // 2. شريط الإحصائيات الفورية الرفيع
            item {
                SlimQuickStatsBar()
            }

            // 3. كروت المميزات الرئيسية (2 Columns Grid) - الصف الأول
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ZabetStyleCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.sec_msi_title),
                        description = stringResource(id = R.string.sec_msi_desc),
                        accentColor = NeonRed,
                        icon = Icons.Default.Monitor,
                        onClick = onNavigateToMSISuite
                    )
                    ZabetStyleCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.sec_benchmark_title),
                        description = stringResource(id = R.string.sec_benchmark_desc),
                        accentColor = NeonCyan,
                        icon = Icons.Default.Assessment,
                        onClick = onNavigateToBenchmark
                    )
                }
            }

            // كروت المميزات الرئيسية - الصف الثاني
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ZabetStyleCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.sec_profiles_title),
                        description = stringResource(id = R.string.sec_profiles_desc),
                        accentColor = NeonGreen,
                        icon = Icons.Default.Speed,
                        onClick = onNavigateToProfiles
                    )
                    ZabetStyleCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.sec_overclock_title),
                        description = stringResource(id = R.string.sec_overclock_desc),
                        accentColor = NeonAmber,
                        icon = Icons.Default.DeveloperBoard,
                        onClick = onNavigateToOverclock
                    )
                }
            }

            // 4. قسم مشغل واختصارات الألعاب السفلي (Game Launcher & Boost)
            item {
                GameLauncherSection(
                    games = savedGames,
                    onAddClick = { showAddGameDialog = true },
                    onGameClick = { gameName ->
                        // تفعيل وضع التسريع تلقائياً عند تشغيل اللعبة
                        boostState = 1
                    }
                )
            }
        }
    }

    // نافذة منبثقة لإضافة لعبة جديدة للاختصارات
    if (showAddGameDialog) {
        AlertDialog(
            onDismissRequest = { showAddGameDialog = false },
            containerColor = CardBackground,
            title = {
                Text(
                    text = stringResource(id = R.string.dialog_add_game_title),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.dialog_add_game_desc),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    savedGames = savedGames + "New Game"
                    showAddGameDialog = false
                }) {
                    Text(text = stringResource(id = R.string.btn_add_sample), color = NeonCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGameDialog = false }) {
                    Text(text = stringResource(id = R.string.dialog_cancel), color = Color.Gray)
                }
            }
        )
    }
}

// ---------------- 1. الهيدر العلوي ---------------- //

@Composable
private fun TopAppBarSection(
    boostState: Int,
    onBoostClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NeonRed.copy(alpha = 0.18f), CircleShape)
                    .border(1.5.dp, NeonRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = NeonRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = stringResource(id = R.string.app_subtitle),
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val buttonColor by animateColorAsState(
                targetValue = if (boostState == 1) NeonAmber else NeonRed,
                animationSpec = tween(300),
                label = "BoostBtnColor"
            )

            Button(
                onClick = onBoostClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(38.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (boostState == 1) stringResource(id = R.string.btn_boosting) else stringResource(id = R.string.btn_boost_now),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .background(CardBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings_cd),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ---------------- 2. شريط الأداء الحقيقي الرفيع ---------------- //

@Composable
private fun SlimQuickStatsBar() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SlimStatBox(label = stringResource(id = R.string.stat_battery_temp), value = "34°C", color = NeonCyan)
            VerticalDivider(modifier = Modifier.height(20.dp), color = CardBorder)
            SlimStatBox(label = stringResource(id = R.string.stat_ram_usage), value = "62%", color = NeonAmber)
            VerticalDivider(modifier = Modifier.height(20.dp), color = CardBorder)
            SlimStatBox(label = stringResource(id = R.string.stat_cpu_temp), value = "41°C", color = NeonGreen)
        }
    }
}

@Composable
private fun SlimStatBox(label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ---------------- 3. كارت الميزات الشبكي الأساسي ---------------- //

@Composable
private fun ZabetStyleCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(175.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ---------------- 4. قسم مشغل واختصارات الألعاب السفلي ---------------- //

@Composable
private fun GameLauncherSection(
    games: List<String>,
    onAddClick: () -> Unit,
    onGameClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
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
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(NeonCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.launcher_title),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(NeonCyan, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_game_cd),
                        tint = BackgroundDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                games.forEach { game ->
                    GameShortcutCard(gameName = game, onClick = { onGameClick(game) })
                }
            }
        }
    }
}

@Composable
private fun GameShortcutCard(gameName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(75.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF191D28))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = gameName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}