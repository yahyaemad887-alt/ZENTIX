package com.example.zentix.UI

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

// نموذج بيانات نمط الهاردوير
data class HardwarePreset(
    val id: Int,
    val name: String,
    var governor: String,
    var touchBoost: Boolean,
    var bypassCharging: Boolean,
    val accentColor: Color
)

// نموذج بيانات لعبة مع حالة ربطها بالبروفايل
data class GameTarget(
    val id: Int,
    val name: String,
    val engine: String,
    var isAssigned: Boolean
)

@Composable
fun AutoProfilesScreen(
    onNavigateBack: () -> Unit = {}
) {
    // تتبع البروفايل المحدد للذهاب لشاشة الاختيار التفصيلية (null تعني أننا في الشاشة الرئيسية)
    var selectedPresetForEdit by remember { mutableStateOf<HardwarePreset?>(null) }

    BackHandler {
        if (selectedPresetForEdit != null) {
            selectedPresetForEdit = null
        } else {
            onNavigateBack()
        }
    }

    // إذا تم تحديد بروفايل، ننتقل فوراً لشاشة ربط الألعاب
    if (selectedPresetForEdit != null) {
        ProfileDetailScreen(
            preset = selectedPresetForEdit!!,
            onBack = { selectedPresetForEdit = null }
        )
    } else {
        // الشاشة الرئيسية للأوتو بروفايل
        AutoProfilesMainContent(
            onNavigateBack = onNavigateBack,
            onSelectPreset = { preset -> selectedPresetForEdit = preset }
        )
    }
}

@Composable
private fun AutoProfilesMainContent(
    onNavigateBack: () -> Unit,
    onSelectPreset: (HardwarePreset) -> Unit
) {
    var isAutoDetectEnabled by remember { mutableStateOf(true) }
    var isRamCleanEnabled by remember { mutableStateOf(true) }
    var isThermalGuardEnabled by remember { mutableStateOf(true) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }
    var selectedGovernor by remember { mutableStateOf("performance") }
    var newTouchBoost by remember { mutableStateOf(true) }
    var newBypassCharging by remember { mutableStateOf(true) }

    var presetsList by remember {
        mutableStateOf(
            listOf(
                HardwarePreset(1, "Extreme Gaming Turbo", "performance", touchBoost = true, bypassCharging = true, NeonRed),
                HardwarePreset(2, "Balanced Emulation", "schedutil", touchBoost = true, bypassCharging = false, NeonCyan),
                HardwarePreset(3, "Thermal Saver", "powersave", touchBoost = false, bypassCharging = true, NeonGreen),
                HardwarePreset(4, "winter", "performance", touchBoost = true, bypassCharging = true, NeonPurple)
            )
        )
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
                                text = stringResource(id = R.string.auto_profiles_title),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = stringResource(id = R.string.auto_profiles_subtitle),
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(CardBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Profile",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. قسم أتمتة النظام
            item {
                Text(
                    text = stringResource(id = R.string.section_auto_engine),
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
                        AutomationRuleSwitch(
                            title = stringResource(id = R.string.rule_auto_detect),
                            description = stringResource(id = R.string.rule_auto_detect_desc),
                            icon = Icons.Default.AutoMode,
                            iconColor = NeonGreen,
                            checked = isAutoDetectEnabled,
                            onCheckedChange = { isAutoDetectEnabled = it }
                        )

                        HorizontalDivider(color = CardBorder, thickness = 1.dp)

                        AutomationRuleSwitch(
                            title = stringResource(id = R.string.rule_ram_clean),
                            description = stringResource(id = R.string.rule_ram_clean_desc),
                            icon = Icons.Default.CleaningServices,
                            iconColor = NeonCyan,
                            checked = isRamCleanEnabled,
                            onCheckedChange = { isRamCleanEnabled = it }
                        )

                        HorizontalDivider(color = CardBorder, thickness = 1.dp)

                        AutomationRuleSwitch(
                            title = stringResource(id = R.string.rule_thermal_guard),
                            description = stringResource(id = R.string.rule_thermal_guard_desc),
                            icon = Icons.Default.Security,
                            iconColor = NeonAmber,
                            checked = isThermalGuardEnabled,
                            onCheckedChange = { isThermalGuardEnabled = it }
                        )
                    }
                }
            }

            // 3. البروفايلات المفعلة (اضغط على أي واحدة لربط الألعاب بها)
            item {
                Text(
                    text = stringResource(id = R.string.section_hardware_presets) + " (Tap to assign games)",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            items(presetsList) { preset ->
                HardwarePresetCard(
                    preset = preset,
                    onClick = { onSelectPreset(preset) }
                )
            }
        }
    }

    // حوار الإنشاء
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = CardBackground,
            title = { Text(text = stringResource(id = R.string.dialog_create_profile), color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newProfileName,
                        onValueChange = { newProfileName = it },
                        label = { Text(stringResource(id = R.string.dialog_profile_name), color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Text(text = stringResource(id = R.string.dialog_gov_select), color = Color.Gray, fontSize = 12.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("performance", "schedutil", "powersave").forEach { gov ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (selectedGovernor == gov) NeonCyan.copy(alpha = 0.2f) else BackgroundDark, RoundedCornerShape(8.dp))
                                    .border(1.dp, if (selectedGovernor == gov) NeonCyan else CardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedGovernor = gov }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = gov, color = if (selectedGovernor == gov) NeonCyan else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newProfileName.isNotBlank()) {
                            presetsList = presetsList + HardwarePreset(
                                id = presetsList.size + 1,
                                name = newProfileName,
                                governor = selectedGovernor,
                                touchBoost = newTouchBoost,
                                bypassCharging = newBypassCharging,
                                accentColor = NeonPurple
                            )
                            newProfileName = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.btn_save_profile), color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(text = stringResource(id = R.string.btn_cancel), color = Color.Gray)
                }
            }
        )
    }
}

// ---------------- الشاشة الثانية: اختيار الألعاب المربوطة بالبروفايل ---------------- //

@Composable
private fun ProfileDetailScreen(
    preset: HardwarePreset,
    onBack: () -> Unit
) {
    var gamesList by remember {
        mutableStateOf(
            listOf(
                GameTarget(1, "Mafia II Classic", "Winlator 11 Ajay", isAssigned = preset.name.contains("Turbo") || preset.name == "winter"),
                GameTarget(2, "GTA IV", "Winlator Ludashi", isAssigned = preset.name.contains("Turbo") || preset.name == "winter"),
                GameTarget(3, "Blur", "Native Port", isAssigned = preset.name.contains("Balanced")),
                GameTarget(4, "God of War PSP", "PPSSPP Gold", isAssigned = preset.name.contains("Balanced")),
                GameTarget(5, "Supermarket Simulator", "Winlator 11", isAssigned = false),
                GameTarget(6, "Manhunt", "PPSSPP Gold", isAssigned = false)
            )
        )
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
            // الهيدر علوي مخصص لاسم البروفايل
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
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
                            text = preset.name.uppercase(),
                            color = preset.accentColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = stringResource(id = R.string.profile_detail_title),
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // قسم اختيار الألعاب المربوطة
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
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.section_assigned_games),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.desc_assigned_games),
                            color = Color.Gray,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // قائمة الألعاب مع Checkboxes للاختيار والتخصيص
            items(gamesList) { game ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            gamesList = gamesList.map {
                                if (it.id == game.id) it.copy(isAssigned = !it.isAssigned) else it
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
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
                                    .size(34.dp)
                                    .background(
                                        if (game.isAssigned) preset.accentColor.copy(alpha = 0.2f) else CardBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = if (game.isAssigned) preset.accentColor else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = game.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = game.engine,
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Checkbox(
                            checked = game.isAssigned,
                            onCheckedChange = { checked ->
                                gamesList = gamesList.map {
                                    if (it.id == game.id) it.copy(isAssigned = checked) else it
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = preset.accentColor,
                                uncheckedColor = CardBorder
                            )
                        )
                    }
                }
            }

            // زر الحفظ والعودة
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = preset.accentColor)
                ) {
                    Text(
                        text = stringResource(id = R.string.btn_save_changes),
                        color = BackgroundDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ---------------- مكونات فرعية ---------------- //

@Composable
private fun AutomationRuleSwitch(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                    .size(34.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, color = Color.Gray, fontSize = 10.sp, lineHeight = 13.sp)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BackgroundDark,
                checkedTrackColor = iconColor,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = CardBorder
            )
        )
    }
}

@Composable
private fun HardwarePresetCard(
    preset: HardwarePreset,
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
                            .size(34.dp)
                            .background(preset.accentColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = preset.accentColor, modifier = Modifier.size(18.dp))
                    }
                    Text(text = preset.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Badge(containerColor = preset.accentColor.copy(alpha = 0.2f)) {
                        Text(
                            text = "Gov: ${preset.governor}",
                            color = preset.accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }

            HorizontalDivider(color = CardBorder, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Touch Boost: ${if (preset.touchBoost) "ON" else "OFF"}",
                    color = if (preset.touchBoost) NeonGreen else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bypass Power: ${if (preset.bypassCharging) "ON" else "OFF"}",
                    color = if (preset.bypassCharging) NeonCyan else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}