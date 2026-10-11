package com.example.zentix.UI

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R
import java.util.Locale

// ألوان ثيم ZENTIX الداكن
private val BackgroundDark = Color(0xFF090A0F)
private val CardBackground = Color(0xFF12141D)
private val CardBorder = Color(0xFF1E2230)
private val NeonRed = Color(0xFFFF2A4B)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)
private val NeonAmber = Color(0xFFFFD600)

@Composable
fun KernelEngineScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler {
        onNavigateBack()
    }

    // حالات فحص الروت
    var hasCheckedRoot by remember { mutableStateOf(false) }
    var isRootGranted by remember { mutableStateOf(false) }
    var isCheckingRoot by remember { mutableStateOf(false) }

    // القيم للترددات
    var cpuFreqGhz by remember { mutableFloatStateOf(2.4f) }
    var gpuFreqMhz by remember { mutableFloatStateOf(650f) }

    // خيارات متقدمة
    var isUndervoltEnabled by remember { mutableStateOf(true) }
    var isHighPriorityEnabled by remember { mutableStateOf(true) }

    var showExtremeWarningDialog by remember { mutableStateOf(false) }

    // تحديد الشفافية والعطل (لو لسه متمش الفحص أو مفيش روت، العناصر تكون باهتة وغير مفعلة)
    val isEngineUnlocked = hasCheckedRoot && isRootGranted
    val uiAlpha = if (isEngineUnlocked) 1.0f else 0.35f

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
                                text = stringResource(id = R.string.kernel_title),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = stringResource(id = R.string.kernel_subtitle),
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 2. كارت التحقق من الروت الرئيسي (Check Root Card)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isEngineUnlocked) NeonGreen else NeonAmber, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (!hasCheckedRoot) stringResource(id = R.string.root_required_msg)
                                    else if (isRootGranted) "Superuser binary responsive at /system/bin/su"
                                    else "Device is running standard non-root user space.",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // زر كبير مدور لفحص الروت (Check Root Button)
                        Button(
                            onClick = {
                                isCheckingRoot = true
                                // محاكاة عملية فحص الروت (يمكن استبدالها بـ su check حقيقي لاحقاً)
                                hasCheckedRoot = true
                                isRootGranted = true // اجعلها true للتجربة أو افتراض وجود روت عند الفحص
                                isCheckingRoot = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(23.dp), // شكل مدور مميز
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!hasCheckedRoot) NeonCyan else CardBorder
                            )
                        ) {
                            if (isCheckingRoot) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BackgroundDark, strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = if (!hasCheckedRoot) Icons.Default.VerifiedUser else Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = if (!hasCheckedRoot) BackgroundDark else NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (!hasCheckedRoot) stringResource(id = R.string.btn_check_root) else "Re-verify Root Access",
                                    color = if (!hasCheckedRoot) BackgroundDark else NeonCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // 3. قسم كسر سرعة المعالج (باهت وغير متاح لو مفيش روت)
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                Text(
                                    text = stringResource(id = R.string.section_cpu_oc),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = String.format(Locale.US, "%.1f GHz", cpuFreqGhz),
                                color = if (cpuFreqGhz > 3.0f) NeonRed else NeonGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Slider(
                            value = cpuFreqGhz,
                            onValueChange = { cpuFreqGhz = it },
                            valueRange = 1.8f..3.8f,
                            steps = 19,
                            enabled = isEngineUnlocked, // معطل لو مفيش روت
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = CardBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Base: 1.8 GHz", color = Color.Gray, fontSize = 10.sp)
                            Text(text = "Extreme OC: 3.8 GHz", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }

            // 4. قسم كسر سرعة الجرافيك GPU (باهت وغير متاح لو مفيش روت)
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.DeveloperBoard, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                                Text(
                                    text = stringResource(id = R.string.section_gpu_oc),
                                    color = Color.White,
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
                            enabled = isEngineUnlocked, // معطل لو مفيش روت
                            colors = SliderDefaults.colors(
                                thumbColor = NeonAmber,
                                activeTrackColor = NeonAmber,
                                inactiveTrackColor = CardBorder
                            )
                        )
                    }
                }
            }

            // 5. ميزات إضافية (Undervolt & High Priority)
            item {
                Text(
                    text = stringResource(id = R.string.section_extra_oc),
                    color = Color.Gray,
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
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                                Text(text = stringResource(id = R.string.feature_undervolt), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = stringResource(id = R.string.feature_undervolt_desc), color = Color.Gray, fontSize = 10.sp)
                            }
                            Switch(
                                checked = isUndervoltEnabled,
                                onCheckedChange = { isUndervoltEnabled = it },
                                enabled = isEngineUnlocked,
                                colors = SwitchDefaults.colors(checkedTrackColor = NeonGreen)
                            )
                        }

                        HorizontalDivider(color = CardBorder, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stringResource(id = R.string.feature_high_priority), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = stringResource(id = R.string.feature_high_priority_desc), color = Color.Gray, fontSize = 10.sp)
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

            // 6. زر التطبيق
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (isEngineUnlocked) {
                            if (cpuFreqGhz > 3.2f || gpuFreqMhz > 800f) {
                                showExtremeWarningDialog = true
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
                        color = BackgroundDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }

    if (showExtremeWarningDialog) {
        AlertDialog(
            onDismissRequest = { showExtremeWarningDialog = false },
            containerColor = CardBackground,
            title = { Text(text = "High Thermal Risk Warning", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(text = "Extreme frequency curve applied via kernel superuser access.", color = Color.Gray, fontSize = 12.sp) },
            confirmButton = {
                TextButton(onClick = { showExtremeWarningDialog = false }) {
                    Text(text = "Apply", color = NeonRed)
                }
            }
        )
    }
}