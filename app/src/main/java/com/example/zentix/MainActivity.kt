package com.example.zentix

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.zentix.UI.FpsAndOtherScreen
import com.example.zentix.UI.GameLoggerScreen
import com.example.zentix.UI.GameModeScreen
import com.example.zentix.UI.HomeScreen
import com.example.zentix.UI.KernelEngineScreen
import com.example.zentix.UI.OnboardingFlow
import com.example.zentix.UI.SettingsScreen
import com.example.zentix.UI.ZentixGlobalSettings
import com.example.zentix.UI.ZentixVividWrapper
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("zentix_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("selected_lang", "ar") ?: "ar"

        val locale = Locale(savedLang)
        Locale.setDefault(locale)

        val configuration = newBase.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val localizedContext = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(localizedContext)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ZentixGlobalSettings.init(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val isDark = ZentixGlobalSettings.isDarkMode
            val selectedLang = ZentixGlobalSettings.selectedLanguageCode

            val isAr = selectedLang == "ar"
            val layoutDirection = if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr

            val rootBackgroundColor by animateColorAsState(
                targetValue = if (isDark) Color(0xFF090A0F) else Color(0xFFF4F6FA),
                animationSpec = tween(300),
                label = "rootBg"
            )

            val prefs = getSharedPreferences("zentix_prefs", Context.MODE_PRIVATE)
            // هل هذه هي المرة الأولى لفتح التطبيق (لعرض الـ Onboarding)؟
            var isFirstRun by remember { mutableStateOf(prefs.getBoolean("is_first_run", true)) }

            // فيديو الإنترو سيعمل دائماً في بداية فتح التطبيق
            var showSplash by rememberSaveable { mutableStateOf(true) }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = rootBackgroundColor
                    ) {
                        ZentixVividWrapper {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(rootBackgroundColor)
                            ) {
                                when {
                                    // 1. شاشة الترحيب واختيار اللغة (تظهر مرة واحدة فقط)
                                    isFirstRun -> {
                                        OnboardingFlow(
                                            context = this@MainActivity,
                                            onFinished = {
                                                isFirstRun = false
                                                showSplash = true
                                            }
                                        )
                                    }
                                    // 2. فيديو الإنترو (يعمل في كل مرة يفتح فيها التطبيق)
                                    showSplash -> {
                                        ZentixSplashScreen(
                                            onAnimationFinished = {
                                                showSplash = false
                                            }
                                        )
                                    }
                                    // 3. الواجهات الرئيسية للتطبيق
                                    else -> {
                                        var currentScreen by rememberSaveable { mutableStateOf("home") }

                                        when (currentScreen) {
                                            "home" -> HomeScreen(
                                                onNavigateToSettings = { currentScreen = "settings" },
                                                onNavigateToMSISuite = { currentScreen = "msi_suite" },
                                                onNavigateToBenchmark = { currentScreen = "logger" },
                                                onNavigateToProfiles = { currentScreen = "profiles" },
                                                onNavigateToOverclock = { currentScreen = "overclock" },
                                                onNavigateToGameMode = { currentScreen = "gamemode" }
                                            )
                                            "settings" -> SettingsScreen(onNavigateBack = { currentScreen = "home" })
                                            "logger" -> GameLoggerScreen(onNavigateBack = { currentScreen = "home" })
                                            "overclock" -> KernelEngineScreen(onNavigateBack = { currentScreen = "home" })
                                            "msi_suite" -> FpsAndOtherScreen(onNavigateBack = { currentScreen = "home" })
                                            "gamemode" -> GameModeScreen(onNavigateBack = { currentScreen = "home" })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// دالة شاشة عرض الفيديو الافتتاحية (Splash Video)
@Composable
fun ZentixSplashScreen(
    onAnimationFinished: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                VideoView(context).apply {
                    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.intro}")
                    setVideoURI(videoUri)

                    setOnCompletionListener {
                        onAnimationFinished()
                    }

                    start()
                }
            }
        )
    }
}