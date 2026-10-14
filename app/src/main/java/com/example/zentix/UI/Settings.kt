package com.example.zentix.UI

import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.app.Activity
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ==================================================================================
// ZENTIX GLOBAL SETTINGS ENGINE (المحرك المركزي الدائم لعدم تصفير الحالة)
// ==================================================================================
object ZentixGlobalSettings {
    var isDarkMode by mutableStateOf(true)
    var selectedLanguageCode by mutableStateOf("ar")
    var isAiGuardActive by mutableStateOf(true)
}

// جسر مشاركة المؤقت مع شاشة الـ FPS والـ OSD
object ZentixFpsOsdBridge {
    var activeTimerLabel by mutableStateOf("Disabled")
    var isTimerRunning by mutableStateOf(false)
    var remainingTimeMs by mutableStateOf(0L)
    var showTimeUpAlert by mutableStateOf(false)
}

// الثوابت والألوان
private val CyanSwitch = Color(0xFF00E5FF)
private val PurpleSwitch = Color(0xFFAB47BC)
private val GreenIcon = Color(0xFF00E676)
private val YellowIcon = Color(0xFFFFD600)
private val RedIcon = Color(0xFFFF1744)

// نموذج اللغات المدعومة المتوافقة مع مجلدات values-[lang][cite: 9]
data class AppLanguageModel(
    val code: String,
    val nativeName: String,
    val englishName: String
)

val ZentixLanguages = listOf(
    AppLanguageModel("en", "English", "English"),
    AppLanguageModel("ar", "العربية", "Arabic"),
    AppLanguageModel("de", "Deutsch", "German"),
    AppLanguageModel("fr", "Français", "French"),
    AppLanguageModel("ja", "日本語", "Japanese"),
    AppLanguageModel("ko", "한국어", "Korean"),
    AppLanguageModel("ru", "Русский", "Russian"),
    AppLanguageModel("tr", "Türkçe", "Turkish"),
    AppLanguageModel("zh", "中文", "Chinese")
)

// خيارات المؤقت بالمللي ثانية
val TimerDurationMap = mapOf(
    "Disabled" to 0L,
    "Minutes 30" to 30L * 60L * 1000L,
    "Hour 1" to 60L * 60L * 1000L,
    "Hours 1.5" to 90L * 60L * 1000L,
    "Hours 2" to 120L * 60L * 1000L,
    "Hours 3" to 180L * 60L * 1000L
)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    BackHandler {
        onNavigateBack()
    }

    var showLanguageModal by remember { mutableStateOf(false) }
    var showTimerModal by remember { mutableStateOf(false) }
    var showAboutModal by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // حاسبة الألوان الديناميكية (Dark / Light)
    val isDark = ZentixGlobalSettings.isDarkMode
    val backgroundColor by animateColorAsState(if (isDark) Color(0xFF090A0F) else Color(0xFFF4F6FA), animationSpec = tween(300), label = "bg")
    val cardBgColor by animateColorAsState(if (isDark) Color(0xFF11141E) else Color(0xFFFFFFFF), animationSpec = tween(300), label = "card")
    val cardBorderColor by animateColorAsState(if (isDark) Color(0xFF1A1F2C) else Color(0xFFE0E5F0), animationSpec = tween(300), label = "border")
    val primaryTextColor by animateColorAsState(if (isDark) Color.White else Color(0xFF1A1D26), animationSpec = tween(300), label = "text1")
    val secondaryTextColor by animateColorAsState(if (isDark) Color.Gray else Color(0xFF6B7280), animationSpec = tween(300), label = "text2")
    val dialogBgColor by animateColorAsState(if (isDark) Color(0xFF121520) else Color(0xFFFFFFFF), animationSpec = tween(300), label = "dialog")

    // لوجيك العد التنازلي وإظهار دايالوج التنبيه فور انتهاء الوقت[cite: 9]
    LaunchedEffect(ZentixFpsOsdBridge.isTimerRunning, ZentixFpsOsdBridge.remainingTimeMs) {
        if (ZentixFpsOsdBridge.isTimerRunning && ZentixFpsOsdBridge.remainingTimeMs > 0L) {
            delay(1000L)
            ZentixFpsOsdBridge.remainingTimeMs -= 1000L
            if (ZentixFpsOsdBridge.remainingTimeMs <= 0L) {
                ZentixFpsOsdBridge.isTimerRunning = false
                ZentixFpsOsdBridge.activeTimerLabel = "Disabled"
                ZentixFpsOsdBridge.showTimeUpAlert = true
            }
        }
    }

    val currentLangObject = ZentixLanguages.find { it.code == ZentixGlobalSettings.selectedLanguageCode } ?: ZentixLanguages.first()
    val isAr = ZentixGlobalSettings.selectedLanguageCode == "ar"
    val layoutDirection = if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
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
                // 1. الهيدر العلوي
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(42.dp)
                                .background(cardBgColor, RoundedCornerShape(12.dp))
                                .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryTextColor
                            )
                        }

                        Text(
                            text = if (isAr) "الإعدادات" else "Settings",
                            color = primaryTextColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(42.dp))
                    }
                }

                // 2. كارت الوضع الداكن
                item {
                    SettingsToggleCard(
                        title = if (isAr) "الوضع الداكن" else "Dark Mode",
                        description = if (isAr) "تفعيل الثيم الداكن المخصص للجيمنج" else "Enable dark theme tailored for gaming",
                        icon = Icons.Default.DarkMode,
                        iconColor = Color(0xFF00B0FF),
                        switchColor = CyanSwitch,
                        isChecked = ZentixGlobalSettings.isDarkMode,
                        cardBgColor = cardBgColor,
                        cardBorderColor = cardBorderColor,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        onCheckedChange = { newValue ->
                            ZentixGlobalSettings.isDarkMode = newValue

                            // تطبيق الثيم رسمياً على مستوى النظام والتطبيق[cite: 9]
                            AppCompatDelegate.setDefaultNightMode(
                                if (newValue) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                            )

                            // إعادة تحميل الأكتيفيتي لتغيير الثيم في التطبيق كله فوراً[cite: 9]
                            (context as? Activity)?.recreate()
                        }
                    )
                }

                // 3. كارت حارس الحرارة واللاج
                item {
                    SettingsToggleCard(
                        title = if (isAr) "حارس الحرارة واللاجو بالذكاء الاصطناعي" else "AI Thermal & Lag Guard",
                        description = if (isAr) "مراقبة تنبؤية محلية بالتعلم الآلي لمنع اختناق المعالج" else "Local ML predictive monitoring to prevent throttling",
                        icon = Icons.Default.Psychology,
                        iconColor = PurpleSwitch,
                        switchColor = PurpleSwitch,
                        isChecked = ZentixGlobalSettings.isAiGuardActive,
                        cardBgColor = cardBgColor,
                        cardBorderColor = cardBorderColor,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        onCheckedChange = { ZentixGlobalSettings.isAiGuardActive = it }
                    )
                }

                // 4. كارت اختيار اللغة (تم حذف الدايالوج المتكرر بالخطأ من هنا)
                item {
                    SettingsInteractiveCard(
                        title = "اللغة / Language",
                        description = "${currentLangObject.nativeName} (${currentLangObject.englishName})",
                        icon = Icons.Default.Language,
                        iconBgColor = if (isDark) Color(0xFF003820) else Color(0xFFE8F5E9),
                        iconTintColor = GreenIcon,
                        cardBgColor = cardBgColor,
                        cardBorderColor = cardBorderColor,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        onClick = { showLanguageModal = true }
                    )
                }

                // 5. كارت مؤقت جلسة اللعب
                item {
                    val timerSubtitleText = if (ZentixFpsOsdBridge.isTimerRunning) {
                        val minutes = (ZentixFpsOsdBridge.remainingTimeMs / 1000L) / 60L
                        val seconds = (ZentixFpsOsdBridge.remainingTimeMs / 1000L) % 60L
                        "Active: %02d:%02d".format(minutes, seconds)
                    } else {
                        "Current: ${ZentixFpsOsdBridge.activeTimerLabel}"
                    }

                    SettingsInteractiveCard(
                        title = if (isAr) "مؤقت جلسة اللعب" else "Game Session Timer",
                        description = timerSubtitleText,
                        icon = Icons.Default.Timer,
                        iconBgColor = if (isDark) Color(0xFF383000) else Color(0xFFFFFDE7),
                        iconTintColor = YellowIcon,
                        cardBgColor = cardBgColor,
                        cardBorderColor = cardBorderColor,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        onClick = { showTimerModal = true }
                    )
                }

                // 6. كارت عن ZENTIX
                item {
                    SettingsInteractiveCard(
                        title = "عن ZENTIX",
                        description = if (isAr) "الإصدار 1.0.0 - مركز التحكم في الأداء" else "Version 1.0.0 - Performance Control Center",
                        icon = Icons.Default.Info,
                        iconBgColor = if (isDark) Color(0xFF38000B) else Color(0xFFFFEBEE),
                        iconTintColor = RedIcon,
                        cardBgColor = cardBgColor,
                        cardBorderColor = cardBorderColor,
                        primaryTextColor = primaryTextColor,
                        secondaryTextColor = secondaryTextColor,
                        onClick = { showAboutModal = true }
                    )
                }
            }
        }

        // ==================================================================================
        // DIALOGS & POPUPS (المكان الصحيح والآمن للـ Modals)
        // ==================================================================================

        // دايالوج اللغة (مع تفعيل AppCompatDelegate لقراءة مجلدات values وإعادة التحميل الفوري)[cite: 9]
        if (showLanguageModal) {
            AlertDialog(
                onDismissRequest = { showLanguageModal = false },
                containerColor = dialogBgColor,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "Select App Language",
                        color = primaryTextColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(ZentixLanguages) { langItem ->
                            val isSelected = langItem.code == ZentixGlobalSettings.selectedLanguageCode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) (if (isDark) Color(0xFF0B2E1D) else Color(0xFFC8E6C9)) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        ZentixGlobalSettings.selectedLanguageCode = langItem.code
                                        showLanguageModal = false

                                        // تفعيل اللغة على مستوى التطبيق وقراءة ملفات الـ values المخصصة[cite: 9]
                                        val appLocales = androidx.core.os.LocaleListCompat.forLanguageTags(langItem.code)
                                        AppCompatDelegate.setApplicationLocales(appLocales)

                                        // إعادة تحميل الأكتيفيتي لتطبيق اللغة واتجاه الواجهة في التطبيق كله[cite: 9]
                                        (context as? Activity)?.recreate()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = langItem.nativeName,
                                        color = if (isSelected) GreenIcon else primaryTextColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = langItem.englishName,
                                        color = secondaryTextColor,
                                        fontSize = 12.sp
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GreenIcon,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showLanguageModal = false }) {
                        Text(text = if (isAr) "إلغاء" else "Cancel", color = secondaryTextColor, fontSize = 14.sp)
                    }
                }
            )
        }

        // دايالوج المؤقت
        if (showTimerModal) {
            AlertDialog(
                onDismissRequest = { showTimerModal = false },
                containerColor = dialogBgColor,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "Game Session Timer",
                        color = primaryTextColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TimerDurationMap.forEach { (optionLabel, durationMs) ->
                            val isSelected = optionLabel == ZentixFpsOsdBridge.activeTimerLabel
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isSelected) (if (isDark) Color(0xFF2C2600) else Color(0xFFFFF9C4)) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        ZentixFpsOsdBridge.activeTimerLabel = optionLabel
                                        if (durationMs > 0L) {
                                            ZentixFpsOsdBridge.remainingTimeMs = durationMs
                                            ZentixFpsOsdBridge.isTimerRunning = true
                                        } else {
                                            ZentixFpsOsdBridge.remainingTimeMs = 0L
                                            ZentixFpsOsdBridge.isTimerRunning = false
                                        }
                                        showTimerModal = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = optionLabel,
                                    color = if (isSelected) YellowIcon else primaryTextColor,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )

                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        ZentixFpsOsdBridge.activeTimerLabel = optionLabel
                                        if (durationMs > 0L) {
                                            ZentixFpsOsdBridge.remainingTimeMs = durationMs
                                            ZentixFpsOsdBridge.isTimerRunning = true
                                        } else {
                                            ZentixFpsOsdBridge.remainingTimeMs = 0L
                                            ZentixFpsOsdBridge.isTimerRunning = false
                                        }
                                        showTimerModal = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = YellowIcon,
                                        unselectedColor = secondaryTextColor
                                    )
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showTimerModal = false }) {
                        Text(text = if (isAr) "إلغاء" else "Cancel", color = secondaryTextColor, fontSize = 14.sp)
                    }
                }
            )
        }

        // دايالوج تنبيه انتهاء الوقت
        if (ZentixFpsOsdBridge.showTimeUpAlert) {
            AlertDialog(
                onDismissRequest = { ZentixFpsOsdBridge.showTimeUpAlert = false },
                containerColor = dialogBgColor,
                shape = RoundedCornerShape(24.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = RedIcon,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = if (isAr) "انتهت جلسة اللعب!" else "Game Session Ended!",
                        color = RedIcon,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (isAr)
                            "لقد انتهى وقت الجلسة المحدد للحفاظ على درجة حرارة الموبايل والبطارية. يرجى أخذ قسط من الراحة!"
                        else "Your allocated game session time has elapsed to protect battery health and thermal limits.",
                        color = primaryTextColor,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { ZentixFpsOsdBridge.showTimeUpAlert = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RedIcon)
                    ) {
                        Text(if (isAr) "حسناً" else "OK", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // دايالوج عن التطبيق
        if (showAboutModal) {
            AlertDialog(
                onDismissRequest = { showAboutModal = false },
                containerColor = dialogBgColor,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = RedIcon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ZENTIX Command Center",
                            color = primaryTextColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Version: 1.0",
                            color = secondaryTextColor,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "High-performance Gaming Optimization Tool\nfor Android",
                            color = secondaryTextColor,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Built with Kotlin & Jetpack Compose",
                            color = secondaryTextColor,
                            fontSize = 13.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutModal = false }) {
                        Text(text = "OK", color = RedIcon, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
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