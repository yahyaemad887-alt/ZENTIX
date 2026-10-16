package com.example.zentix.UI

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.util.Locale

private val NeonRed = Color(0xFFFF2A4B)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)
private val NeonAmber = Color(0xFFFFD600)

object KernelRootUtils {
    fun checkRoot(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output.contains("uid=0(root)")
        } catch (_: Exception) {
            false
        }
    }

    fun applyKernelCommand(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("$command\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            outputStream.close()
            process.waitFor()
            process.exitValue() == 0
        } catch (_: Exception) {
            false
        }
    }
}

@Composable
fun KernelEngineScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val appContext = context.applicationContext
    val coroutineScope = rememberCoroutineScope()

    var hasCheckedRoot by remember { mutableStateOf(false) }
    var isRootGranted by remember { mutableStateOf(false) }
    var isCheckingRoot by remember { mutableStateOf(false) }

    var cpuFreqGhz by remember { mutableFloatStateOf(2.4f) }
    var gpuFreqMhz by remember { mutableFloatStateOf(650f) }

    var isUndervoltEnabled by remember { mutableStateOf(false) }
    var isHighPriorityEnabled by remember { mutableStateOf(true) }

    var showExtremeWarningDialog by remember { mutableStateOf(false) }

    val isEngineUnlocked = hasCheckedRoot && isRootGranted
    val uiAlpha = if (isEngineUnlocked) 1.0f else 0.35f

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
                                contentDescription = stringResource(id = R.string.back),
                                tint = primaryTextColor
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(id = R.string.kernel_title),
                                color = primaryTextColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = stringResource(id = R.string.kernel_subtitle),
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
                        .border(1.dp, if (isEngineUnlocked) NeonGreen else NeonAmber, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when {
                                            !hasCheckedRoot -> NeonAmber.copy(alpha = 0.15f)
                                            isRootGranted -> NeonGreen.copy(alpha = 0.15f)
                                            else -> NeonRed.copy(alpha = 0.15f)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        !hasCheckedRoot -> Icons.Default.Lock
                                        isRootGranted -> Icons.Default.Security
                                        else -> Icons.Default.Warning
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        !hasCheckedRoot -> NeonAmber
                                        isRootGranted -> NeonGreen
                                        else -> NeonRed
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (!hasCheckedRoot) stringResource(id = R.string.root_status_locked)
                                    else if (isRootGranted) stringResource(id = R.string.status_root_detected)
                                    else stringResource(id = R.string.root_failed_msg),
                                    color = primaryTextColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (!hasCheckedRoot) stringResource(id = R.string.root_required_msg)
                                    else if (isRootGranted) stringResource(id = R.string.superuser_active)
                                    else stringResource(id = R.string.non_root_space),
                                    color = secondaryTextColor,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        val rootGrantedMsg = stringResource(id = R.string.toast_root_granted)
                        val rootFailedMsg = stringResource(id = R.string.toast_root_failed)

                        Button(
                            onClick = {
                                isCheckingRoot = true
                                coroutineScope.launch {
                                    val granted = withContext(Dispatchers.IO) {
                                        KernelRootUtils.checkRoot()
                                    }
                                    hasCheckedRoot = true
                                    isRootGranted = granted
                                    isCheckingRoot = false
                                    Toast.makeText(
                                        appContext,
                                        if (granted) rootGrantedMsg else rootFailedMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(23.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!hasCheckedRoot) NeonCyan else cardBorder
                            )
                        ) {
                            if (isCheckingRoot) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = backgroundColor, strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = if (!hasCheckedRoot) Icons.Default.VerifiedUser else Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = if (!hasCheckedRoot) backgroundColor else NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (!hasCheckedRoot) stringResource(id = R.string.btn_check_root) else stringResource(id = R.string.reverify_root),
                                    color = if (!hasCheckedRoot) backgroundColor else NeonCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
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
                                Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                Text(
                                    text = stringResource(id = R.string.section_cpu_oc),
                                    color = primaryTextColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = String.format(Locale.US, "%.1f GHz", cpuFreqGhz),
                                color = if (cpuFreqGhz > 3.2f) NeonRed else NeonGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Slider(
                            value = cpuFreqGhz,
                            onValueChange = { cpuFreqGhz = it },
                            valueRange = 1.8f..3.8f,
                            steps = 19,
                            enabled = isEngineUnlocked,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = cardBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = stringResource(id = R.string.base_freq), color = secondaryTextColor, fontSize = 10.sp)
                            Text(text = stringResource(id = R.string.extreme_freq), color = secondaryTextColor, fontSize = 10.sp)
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
                                Icon(imageVector = Icons.Default.DeveloperBoard, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                                Text(
                                    text = stringResource(id = R.string.section_gpu_oc),
                                    color = primaryTextColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = String.format(Locale.US, "%.0f MHz", gpuFreqMhz),
                                color = NeonAmber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Slider(
                            value = gpuFreqMhz,
                            onValueChange = { gpuFreqMhz = it },
                            valueRange = 400f..950f,
                            steps = 11,
                            enabled = isEngineUnlocked,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonAmber,
                                activeTrackColor = NeonAmber,
                                inactiveTrackColor = cardBorder
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.section_extra_oc),
                    color = secondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(id = R.string.feature_undervolt), color = primaryTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = stringResource(id = R.string.feature_undervolt_desc), color = secondaryTextColor, fontSize = 10.sp)
                            }
                            Switch(
                                checked = isUndervoltEnabled,
                                onCheckedChange = { isUndervoltEnabled = it },
                                enabled = isEngineUnlocked,
                                colors = SwitchDefaults.colors(checkedTrackColor = NeonGreen)
                            )
                        }

                        HorizontalDivider(color = cardBorder, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(id = R.string.feature_high_priority), color = primaryTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = stringResource(id = R.string.feature_high_priority_desc), color = secondaryTextColor, fontSize = 10.sp)
                            }
                            Switch(
                                checked = isHighPriorityEnabled,
                                onCheckedChange = { isHighPriorityEnabled = it },
                                enabled = isEngineUnlocked,
                                colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                val cpuAppliedMsg = stringResource(id = R.string.toast_cpu_applied)
                val cpuFailedMsg = stringResource(id = R.string.toast_cpu_failed)

                Button(
                    onClick = {
                        if (isEngineUnlocked) {
                            if (cpuFreqGhz > 3.2f || gpuFreqMhz > 800f) {
                                showExtremeWarningDialog = true
                            } else {
                                coroutineScope.launch {
                                    val cpuKhz = (cpuFreqGhz * 1000000).toInt()
                                    val success = withContext(Dispatchers.IO) {
                                        KernelRootUtils.applyKernelCommand("echo $cpuKhz > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq")
                                    }
                                    Toast.makeText(
                                        appContext,
                                        if (success) cpuAppliedMsg else cpuFailedMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    enabled = isEngineUnlocked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(
                        text = stringResource(id = R.string.btn_apply_kernel),
                        color = backgroundColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }

    if (showExtremeWarningDialog) {
        val extremeAppliedMsg = stringResource(id = R.string.toast_extreme_applied)
        AlertDialog(
            onDismissRequest = { showExtremeWarningDialog = false },
            containerColor = cardBackground,
            title = { Text(text = stringResource(id = R.string.thermal_warning_title), color = primaryTextColor, fontWeight = FontWeight.Bold) },
            text = { Text(text = stringResource(id = R.string.thermal_warning_desc), color = secondaryTextColor, fontSize = 12.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showExtremeWarningDialog = false
                    coroutineScope.launch {
                        val cpuKhz = (cpuFreqGhz * 1000000).toInt()
                        withContext(Dispatchers.IO) {
                            KernelRootUtils.applyKernelCommand("echo $cpuKhz > /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq")
                        }
                        Toast.makeText(appContext, extremeAppliedMsg, Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = stringResource(id = R.string.apply), color = NeonRed)
                }
            }
        )
    }
}