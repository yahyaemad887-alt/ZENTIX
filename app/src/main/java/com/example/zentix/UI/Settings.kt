package com.example.zentix.UI

import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
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

// ألوان ثيم ZENTIX الداكن
private val BackgroundDark = Color(0xFF090A0F)
private val CardBackground = Color(0xFF12141D)
private val CardBorder = Color(0xFF1E2230)
private val NeonRed = Color(0xFFFF2A4B)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonGreen = Color(0xFF00E676)
private val NeonPurple = Color(0xFFB388FF)
private val NeonAmber = Color(0xFFFFD600)

// نموذج اللغات التسع المدعومة في مشروعك
data class AppLanguage(val code: String, val displayName: String, val nativeName: String)

val SupportedLanguages = listOf(
    AppLanguage("en", "English", "English"),
    AppLanguage("ar", "Arabic", "العربية"),
    AppLanguage("de", "German", "Deutsch"),
    AppLanguage("fr", "French", "Français"),
    AppLanguage("ja", "Japanese", "日本語"),
    AppLanguage("ko", "Korean", "한국어"),
    AppLanguage("ru", "Russian", "Русский"),
    AppLanguage("tr", "Turkish", "Türkçe"),
    AppLanguage("zh", "Chinese", "中文")
)

// خيارات مؤقت جلسات اللعب
val TimerOptions = listOf(
    "Disabled",
    "30 Minutes",
    "1 Hour",
    "1.5 Hours",
    "2 Hours",
    "3 Hours"
)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    // التقاط زر الرجوع الخاص بنظام أندرويد
    BackHandler {
        onNavigateBack()
    }

    // --- اللوجيك والحالات المدمجة (States & Logic) ---
    var isDarkModeEnabled by remember { mutableStateOf(true) }
    var isAiGuardEnabled by remember { mutableStateOf(true) }

    // حالة اللغة الحالية والدايالوج
    var currentLanguageCode by remember { mutableStateOf("en") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // حالة مؤقت اللعب والدايالوج
    var selectedTimerOption by remember { mutableStateOf("Disabled") }
    var showTimerDialog by remember { mutableStateOf(false) }

    // حالة نافذة معلومات التطبيق
    var showInfoDialog by remember { mutableStateOf(false) }

    val currentLangObject = SupportedLanguages.find { it.code == currentLanguageCode } ?: SupportedLanguages.first()

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
            // 1. الهيدر العلوي (زر الرجوع + عنوان الشاشة)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // 2. Dark Mode Switch
            item {
                SettingsSwitchCard(
                    title = stringResource(id = R.string.setting_dark_mode),
                    description = stringResource(id = R.string.setting_dark_mode_desc),
                    icon = Icons.Default.DarkMode,
                    accentColor = NeonCyan,
                    checked = isDarkModeEnabled,
                    onCheckedChange = { isDarkModeEnabled = it }
                )
            }

            // 3. AI Thermal & Lag Guard Switch
            item {
                SettingsSwitchCard(
                    title = stringResource(id = R.string.setting_ai_mode),
                    description = stringResource(id = R.string.setting_ai_mode_desc),
                    icon = Icons.Default.Psychology,
                    accentColor = NeonPurple,
                    checked = isAiGuardEnabled,
                    onCheckedChange = { isAiGuardEnabled = it }
                )
            }

            // 4. Language Selection (كارت فتح قائمة اللغات)
            item {
                SettingsActionCard(
                    title = stringResource(id = R.string.setting_language),
                    description = "${currentLangObject.displayName} (${currentLangObject.nativeName})",
                    icon = Icons.Default.Language,
                    accentColor = NeonGreen,
                    onClick = { showLanguageDialog = true }
                )
            }

            // 5. Game Session Timer (كارت اختيار مؤقت اللعب)
            item {
                SettingsActionCard(
                    title = stringResource(id = R.string.setting_game_timer),
                    description = "Current: $selectedTimerOption",
                    icon = Icons.Default.Timer,
                    accentColor = NeonAmber,
                    onClick = { showTimerDialog = true }
                )
            }

            // 6. About ZENTIX
            item {
                SettingsActionCard(
                    title = stringResource(id = R.string.setting_info),
                    description = stringResource(id = R.string.setting_info_desc),
                    icon = Icons.Default.Info,
                    accentColor = NeonRed,
                    onClick = { showInfoDialog = true }
                )
            }
        }
    }

    // ---------------- 1. قائمة اختيار اللغة (Language Dialog) ---------------- //
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "Select App Language",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(SupportedLanguages) { lang ->
                        val isSelected = lang.code == currentLanguageCode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) NeonGreen.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    currentLanguageCode = lang.code
                                    // لوجيك تغيير لغة التطبيق على مستوى النظام
                                    val appLocales = LocaleListCompat.forLanguageTags(lang.code)
                                    AppCompatDelegate.setApplicationLocales(appLocales)
                                    showLanguageDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = lang.nativeName,
                                    color = if (isSelected) NeonGreen else Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = lang.displayName,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(text = stringResource(id = R.string.dialog_cancel), color = Color.Gray)
                }
            }
        )
    }

    // ---------------- 2. قائمة اختيار مؤقت اللعب (Game Timer Dialog) ---------------- //
    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "Game Session Timer",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TimerOptions.forEach { option ->
                        val isSelected = option == selectedTimerOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) NeonAmber.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedTimerOption = option
                                    showTimerDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                color = if (isSelected) NeonAmber else Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedTimerOption = option
                                    showTimerDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonAmber,
                                    unselectedColor = Color.Gray
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimerDialog = false }) {
                    Text(text = stringResource(id = R.string.dialog_cancel), color = Color.Gray)
                }
            }
        )
    }

    // ---------------- 3. نافذة معلومات التطبيق (Info Dialog) ---------------- //
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = CardBackground,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ZENTIX Command Center", color = Color.White, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "Version: 1.0.0\nHigh-performance Gaming Optimization Tool for Android.\nBuilt with Kotlin & Jetpack Compose.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("OK", color = NeonRed)
                }
            }
        )
    }
}

// ---------------- مكونات الكروت الإضافية ---------------- //

@Composable
private fun SettingsSwitchCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
                        .size(40.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = description, color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = CardBorder
                )
            )
        }
    }
}

@Composable
private fun SettingsActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                        .size(40.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = description, color = Color.Gray, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}