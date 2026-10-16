package com.example.zentix.UI

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private val CyanSwitch = Color(0xFF00E5FF)
private val GreenIcon = Color(0xFF00E676)
private val YellowIcon = Color(0xFFFFD600)
private val RedIcon = Color(0xFFFF1744)

fun triggerTimeUpNotification(context: Context) {
    val channelId = "zentix_timer_channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Zentix Game Timer"
        val descriptionText = "Notifications for game session timer"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("انتهت جلسة اللعب!")
        .setContentText("لقد انتهى وقت الجلسة المحدد للحفاظ على صحة البطارية والحرارة.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            notify(1001, builder.build())
        }
    }
}



@Composable
private fun SettingsToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    switchColor: Color,
    isChecked: Boolean,
    cardBgColor: Color,
    cardBorderColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(iconColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = primaryTextColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = description,
                        color = secondaryTextColor,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = switchColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = cardBorderColor
                )
            )
        }
    }
}

@Composable
private fun SettingsInteractiveCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    cardBgColor: Color,
    cardBorderColor: Color,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(iconBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = primaryTextColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = description,
                        color = secondaryTextColor,
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = secondaryTextColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}