package com.example.zentix.UI

import android.media.MediaPlayer
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R

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
    onNavigateToOverclock: () -> Unit = {},
    onNavigateToGameMode: () -> Unit = {}
) {
    val context = LocalContext.current

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
                TopAppBarSection(
                    onSettingsClick = onNavigateToSettings,
                    cardBackground = cardBackground,
                    cardBorder = cardBorder,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor
                )
            }

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
                        onClick = onNavigateToMSISuite,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )
                    ZabetStyleCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.sec_benchmark_title),
                        description = stringResource(id = R.string.sec_benchmark_desc),
                        accentColor = NeonCyan,
                        icon = Icons.Default.Assessment,
                        onClick = onNavigateToBenchmark,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor
                    )
                }
            }

            item {
                ZabetStyleCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.sec_overclock_title),
                    description = stringResource(id = R.string.sec_overclock_desc),
                    accentColor = NeonAmber,
                    icon = Icons.Default.DeveloperBoard,
                    onClick = onNavigateToOverclock,
                    cardBackground = cardBackground,
                    cardBorder = cardBorder,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor
                )
            }

            item {
                GameModeButton(
                    onClick = {
                        try {
                            val powerClickPlayer = MediaPlayer.create(context, R.raw.ps3_power_click)
                            powerClickPlayer?.start()
                            powerClickPlayer?.setOnCompletionListener { it.release() }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        onNavigateToGameMode()
                    }
                )
            }
        }
    }
}

@Composable
private fun TopAppBarSection(
    onSettingsClick: () -> Unit,
    cardBackground: Color,
    cardBorder: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
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
                    color = primaryTextColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = stringResource(id = R.string.app_subtitle),
                    color = secondaryTextColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(38.dp)
                .background(cardBackground, RoundedCornerShape(12.dp))
                .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.settings_cd),
                tint = primaryTextColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ZabetStyleCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    cardBackground: Color,
    cardBorder: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color
) {
    Card(
        modifier = modifier
            .height(175.dp)
            .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
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
                        color = primaryTextColor,
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
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GameModeButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NeonRed)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.game_mode_btn),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }
    }
}