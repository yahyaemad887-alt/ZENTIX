package com.example.zentix.UI

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Whatshot
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R

private val NeonGreen = Color(0xFF00E676)

object ZentixGameConfigPrefs {
    private const val PREFS_NAME = "zentix_game_config_prefs"

    const val KEY_COLOR_ENHANCE = "color_enhance"
    const val KEY_GAME_ACCEL = "game_accel"
    const val KEY_DND = "dnd_mode"
    const val KEY_MUTE = "mute_mode"
    const val KEY_AIRPLANE = "airplane_mode"
    const val KEY_NET_BOOSTER = "net_booster"
    const val KEY_PERF_MODE = "perf_mode"

    fun getBoolean(context: Context, key: String, defaultVal: Boolean = false): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, defaultVal)
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, value).apply()
    }
}

@Composable
fun ZentixVividWrapper(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isColorEnhanceActive = ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_COLOR_ENHANCE)

    val vividColorMatrix = remember {
        ColorMatrix(
            floatArrayOf(
                1.25f, 0f, 0f, 0f, 0f,
                0f, 1.25f, 0f, 0f, 0f,
                0f, 0f, 1.3f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isColorEnhanceActive) {
                    Modifier.graphicsLayer {
                        colorFilter = ColorFilter.colorMatrix(vividColorMatrix)
                    }
                } else {
                    Modifier
                }
            )
    ) {
        content()
    }
}

@Composable
fun ZentixGameConfigScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val notificationManager = remember { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    var colorEnhance by remember { mutableStateOf(ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_COLOR_ENHANCE)) }
    var gameAccel by remember { mutableStateOf(ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_GAME_ACCEL, true)) }
    var dndMode by remember { mutableStateOf(ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_DND)) }
    var muteMode by remember { mutableStateOf(ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_MUTE)) }
    var airplaneMode by remember { mutableStateOf(ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_AIRPLANE)) }
    var netBooster by remember { mutableStateOf(ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_NET_BOOSTER, true)) }
    var perfMode by remember { mutableStateOf(ZentixGameConfigPrefs.getBoolean(context, ZentixGameConfigPrefs.KEY_PERF_MODE, true)) }

    // ألوان تفاعلية بناءً على الوضع الداكن العام للتطبيق
    val isDark = ZentixGlobalSettings.isDarkMode
    val cinematicBlack = if (isDark) Color(0xFF010204) else Color(0xFFF4F6FA)
    val ps3GlassCard = if (isDark) Color(0x26FFFFFF) else Color(0x26000000)
    val ps3GlassBorder = if (isDark) Color(0x4DFFFFFF) else Color(0x4D000000)
    val pureWhiteText = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1D26)
    val mutedTextGray = if (isDark) Color(0xFFA0A6B8) else Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cinematicBlack)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(ps3GlassCard, RoundedCornerShape(10.dp))
                            .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = pureWhiteText, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = stringResource(R.string.config_title),
                        color = pureWhiteText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ConfigSwitchItem(
                        icon = Icons.Default.Palette,
                        title = stringResource(R.string.color_enhance_title),
                        subtitle = stringResource(R.string.color_enhance_desc),
                        checked = colorEnhance,
                        onCheckedChange = {
                            colorEnhance = it
                            ZentixGameConfigPrefs.setBoolean(context, ZentixGameConfigPrefs.KEY_COLOR_ENHANCE, it)
                        },
                        ps3GlassCard = ps3GlassCard,
                        ps3GlassBorder = ps3GlassBorder,
                        pureWhiteText = pureWhiteText,
                        mutedTextGray = mutedTextGray,
                        cinematicBlack = cinematicBlack
                    )
                }
                item {
                    ConfigSwitchItem(
                        icon = Icons.Default.Speed,
                        title = stringResource(R.string.game_accel_title),
                        subtitle = stringResource(R.string.game_accel_desc),
                        checked = gameAccel,
                        onCheckedChange = {
                            gameAccel = it
                            ZentixGameConfigPrefs.setBoolean(context, ZentixGameConfigPrefs.KEY_GAME_ACCEL, it)
                            if (it) {
                                try {
                                    System.gc()
                                    Runtime.getRuntime().gc()
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        },
                        ps3GlassCard = ps3GlassCard,
                        ps3GlassBorder = ps3GlassBorder,
                        pureWhiteText = pureWhiteText,
                        mutedTextGray = mutedTextGray,
                        cinematicBlack = cinematicBlack
                    )
                }
                item {
                    ConfigSwitchItem(
                        icon = Icons.Default.DoNotDisturb,
                        title = stringResource(R.string.dnd_mode_title),
                        subtitle = stringResource(R.string.dnd_mode_desc),
                        checked = dndMode,
                        onCheckedChange = { checked ->
                            dndMode = checked
                            ZentixGameConfigPrefs.setBoolean(context, ZentixGameConfigPrefs.KEY_DND, checked)
                            try {
                                if (checked) {
                                    if (notificationManager.isNotificationPolicyAccessGranted()) {
                                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                                    } else {
                                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                                    }
                                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                                } else {
                                    if (notificationManager.isNotificationPolicyAccessGranted()) {
                                        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                                    }
                                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        },
                        ps3GlassCard = ps3GlassCard,
                        ps3GlassBorder = ps3GlassBorder,
                        pureWhiteText = pureWhiteText,
                        mutedTextGray = mutedTextGray,
                        cinematicBlack = cinematicBlack
                    )
                }
                item {
                    ConfigSwitchItem(
                        icon = Icons.Default.VolumeOff,
                        title = stringResource(R.string.mute_audio_title),
                        subtitle = stringResource(R.string.mute_audio_desc),
                        checked = muteMode,
                        onCheckedChange = { checked ->
                            muteMode = checked
                            ZentixGameConfigPrefs.setBoolean(context, ZentixGameConfigPrefs.KEY_MUTE, checked)
                            try {
                                if (checked) {
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                                } else {
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0)
                                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 5, 0)
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        },
                        ps3GlassCard = ps3GlassCard,
                        ps3GlassBorder = ps3GlassBorder,
                        pureWhiteText = pureWhiteText,
                        mutedTextGray = mutedTextGray,
                        cinematicBlack = cinematicBlack
                    )
                }
                item {
                    ConfigSwitchItem(
                        icon = Icons.Default.AirplanemodeActive,
                        title = stringResource(R.string.airplane_mode_title),
                        subtitle = stringResource(R.string.airplane_mode_desc),
                        checked = airplaneMode,
                        onCheckedChange = {
                            airplaneMode = it
                            ZentixGameConfigPrefs.setBoolean(context, ZentixGameConfigPrefs.KEY_AIRPLANE, it)
                            try {
                                context.startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
                            } catch (e: Exception) { e.printStackTrace() }
                        },
                        ps3GlassCard = ps3GlassCard,
                        ps3GlassBorder = ps3GlassBorder,
                        pureWhiteText = pureWhiteText,
                        mutedTextGray = mutedTextGray,
                        cinematicBlack = cinematicBlack
                    )
                }
                item {
                    ConfigSwitchItem(
                        icon = Icons.Default.NetworkCheck,
                        title = stringResource(R.string.net_booster_title),
                        subtitle = stringResource(R.string.net_booster_desc),
                        checked = netBooster,
                        onCheckedChange = {
                            netBooster = it
                            ZentixGameConfigPrefs.setBoolean(context, ZentixGameConfigPrefs.KEY_NET_BOOSTER, it)
                        },
                        ps3GlassCard = ps3GlassCard,
                        ps3GlassBorder = ps3GlassBorder,
                        pureWhiteText = pureWhiteText,
                        mutedTextGray = mutedTextGray,
                        cinematicBlack = cinematicBlack
                    )
                }
                item {
                    ConfigSwitchItem(
                        icon = Icons.Default.Whatshot,
                        title = stringResource(R.string.perf_mode_title),
                        subtitle = stringResource(R.string.perf_mode_desc),
                        checked = perfMode,
                        onCheckedChange = { it ->
                            perfMode = it
                            ZentixGameConfigPrefs.setBoolean(context, ZentixGameConfigPrefs.KEY_PERF_MODE, it)
                            if (it) {
                                try {
                                    Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY)
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        },
                        ps3GlassCard = ps3GlassCard,
                        ps3GlassBorder = ps3GlassBorder,
                        pureWhiteText = pureWhiteText,
                        mutedTextGray = mutedTextGray,
                        cinematicBlack = cinematicBlack
                    )
                }
            }
        }
    }
}

@Composable
fun ConfigSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    ps3GlassCard: Color,
    ps3GlassBorder: Color,
    pureWhiteText: Color,
    mutedTextGray: Color,
    cinematicBlack: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ps3GlassCard, RoundedCornerShape(14.dp))
            .border(1.dp, ps3GlassBorder, RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(14.dp),
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
                    .size(36.dp)
                    .background(pureWhiteText.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = pureWhiteText, modifier = Modifier.size(20.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = title, color = pureWhiteText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = mutedTextGray, fontSize = 11.sp)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = pureWhiteText,
                checkedTrackColor = NeonGreen,
                uncheckedThumbColor = mutedTextGray,
                uncheckedTrackColor = cinematicBlack
            )
        )
    }
}