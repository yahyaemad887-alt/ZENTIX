package com.example.zentix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import com.example.zentix.UI.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

            androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection) {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = rootBackgroundColor
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(rootBackgroundColor)
                        ) {
                            var currentScreen by remember { mutableStateOf("home") }

                            when (currentScreen) {
                                "home" -> HomeScreen(
                                    onNavigateToSettings = { currentScreen = "settings" },
                                    onNavigateToMSISuite = { currentScreen = "msi_suite" },
                                    onNavigateToBenchmark = { currentScreen = "logger" },
                                    onNavigateToProfiles = { currentScreen = "profiles" },
                                    onNavigateToOverclock = { currentScreen = "overclock" }
                                )
                                "settings" -> SettingsScreen(onNavigateBack = { currentScreen = "home" })
                                "logger" -> GameLoggerScreen(onNavigateBack = { currentScreen = "home" })
                                "profiles" -> AutoProfilesScreen(onNavigateBack = { currentScreen = "home" })
                                "overclock" -> KernelEngineScreen(onNavigateBack = { currentScreen = "home" })
                                "msi_suite" -> FpsAndOtherScreen(onNavigateBack = { currentScreen = "home" })
                            }
                        }
                    }
                }
            }
        }
    }
}