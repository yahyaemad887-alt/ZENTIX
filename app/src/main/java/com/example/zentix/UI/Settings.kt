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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentix.R
import com.example.zentix.data.ZentixPrefs

// ألوان الـ Neon ثابتة في الوضعين (الخلفية والكروت والنصوص جاية من ZentixTheme)
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

// خيارات مؤقت جلسات اللعب (minutes = 0 معناها متوقف)
data class TimerOption(val label: String, val minutes: Int)

val TimerOptions = listOf(
    TimerOption("Disabled", 0),
    TimerOption("30 Minutes", 30),
    TimerOption("1 Hour", 60),
    TimerOption("1.5 Hours", 90),
    TimerOption("2 Hours", 120),
    TimerOption("3 Hours", 180)
)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { ZentixPrefs.get(context) }

    // الحالات كلها جاية من التخزين الدائم، فبتفضل محفوظة بعد قفل التطبيق
    val isDarkModeEnabled by prefs.darkMode.collectAsState()
    val isAiGuardEnabled by prefs.aiGuard.collectAsState()
    val currentLanguageCode by prefs.languageCode.collectAsState()
    val timerMinutes by prefs.sessionTimerMinutes.collectAsState()

    // الثيم هنا بيتغير فورًا مع زرار Dark Mode
    ZentixTheme(darkTheme = isDarkModeEnabled) {
        SettingsContent(
            isDarkModeEnabled = isDarkModeEnabled,
            isAiGuardEnabled = isAiGuardEnabled,
            currentLanguageCode = currentLanguageCode,
            timerMinutes = timerMinutes,
            onDarkModeChange = prefs::setDarkMode,
            onAiGuardChange = prefs::setAiGuard,
            onLanguageSelected = { code ->
                prefs.setLanguage(code)
                // تغيير لغة التطبيق على مستوى النظام (بيعمل recreate للـ Activity)
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
            },
            onTimerSelected = prefs::setSessionTimerMinutes,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
private fun SettingsContent(
    isDarkModeEnabled: Boolean,
    isAiGuardEnabled: Boolean,
    currentLanguageCode: String,
    timerMinutes: Int,
    onDarkModeChange: (Boolean) -> Unit,
    onAiGuardChange: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onTimerSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val colors = zentixColors
    val context = LocalContext.current

    // التقاط زر الرجوع الخاص بنظام أندرويد
    BackHandler { onNavigateBack() }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    val currentLangObject = SupportedLanguages.find { it.code == currentLanguageCode }
        ?: SupportedLanguages.first()
    val selectedTimer = TimerOptions.find { it.minutes == timerMinutes } ?: TimerOptions.first()

    // رقم النسخة الحقيقي من الـ APK بدل نص ثابت
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0.0"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
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
                            .background(colors.card, RoundedCornerShape(12.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.text
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.settings_title),
                        color = colors.text,
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
                    onCheckedChange = onDarkModeChange
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
                    onCheckedChange = onAiGuardChange
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
                    description = "Current: ${selectedTimer.label}",
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
            containerColor = colors.card,
            title = {
                Text(
                    text = "Select App Language",
                    color = colors.text,
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
                                    showLanguageDialog = false
                                    // لو نفس اللغة مفيش داعي نعمل recreate
                                    if (!isSelected) onLanguageSelected(lang.code)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = lang.nativeName,
                                    color = if (isSelected) NeonGreen else colors.text,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = lang.displayName,
                                    color = colors.subText,
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
                    Text(text = stringResource(id = R.string.dialog_cancel), color = colors.subText)
                }
            }
        )
    }

    // ---------------- 2. قائمة اختيار مؤقت اللعب (Game Timer Dialog) ---------------- //
    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            containerColor = colors.card,
            title = {
                Text(
                    text = "Game Session Timer",
                    color = colors.text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TimerOptions.forEach { option ->
                        val isSelected = option.minutes == timerMinutes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) NeonAmber.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onTimerSelected(option.minutes)
                                    showTimerDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option.label,
                                color = if (isSelected) NeonAmber else colors.text,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onTimerSelected(option.minutes)
                                    showTimerDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonAmber,
                                    unselectedColor = colors.subText
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimerDialog = false }) {
                    Text(text = stringResource(id = R.string.dialog_cancel), color = colors.subText)
                }
            }
        )
    }

    // ---------------- 3. نافذة معلومات التطبيق (Info Dialog) ---------------- //
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = colors.card,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ZENTIX Command Center", color = colors.text, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "Version: $versionName\nHigh-performance Gaming Optimization Tool for Android.\nBuilt with Kotlin & Jetpack Compose.",
                    color = colors.subText,
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
    val colors = zentixColors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.border, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card)
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
                    Text(text = title, color = colors.text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = description, color = colors.subText, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = colors.border
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
    val colors = zentixColors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card)
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
                    Text(text = title, color = colors.text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = description, color = colors.subText, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colors.subText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
