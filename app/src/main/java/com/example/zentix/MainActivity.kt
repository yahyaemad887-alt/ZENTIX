package com.example.zentix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.zentix.UI.AutoProfilesScreen
import com.example.zentix.UI.FpsAndOtherScreen
import com.example.zentix.UI.GameLoggerScreen
import com.example.zentix.UI.HomeScreen
import com.example.zentix.UI.KernelEngineScreen
import com.example.zentix.UI.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // متغير لتتبع الشاشة الحالية وتغطية كافة واجهات ZENTIX[cite: 12, 19]
                    var currentScreen by remember { mutableStateOf("home") }

                    when (currentScreen) {
                        "home" -> {
                            HomeScreen(
                                onNavigateToSettings = { currentScreen = "settings" },
                                onNavigateToMSISuite = { currentScreen = "msi_suite" }, // ربط شاشة MSI OSD والـ Recorder
                                onNavigateToBenchmark = { currentScreen = "logger" }, // Game Logger[cite: 12, 19]
                                onNavigateToProfiles = { currentScreen = "profiles" }, // Auto Profile[cite: 12, 19]
                                onNavigateToOverclock = { currentScreen = "overclock" } // Kernel Engine OC[cite: 12, 19]
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                onNavigateBack = { currentScreen = "home" }
                            )
                        }
                        "logger" -> {
                            GameLoggerScreen(
                                onNavigateBack = { currentScreen = "home" }
                            )
                        }
                        "profiles" -> {
                            AutoProfilesScreen(
                                onNavigateBack = { currentScreen = "home" }
                            )
                        }
                        "overclock" -> {
                            KernelEngineScreen(
                                onNavigateBack = { currentScreen = "home" }
                            )
                        }
                        "msi_suite" -> {
                            FpsAndOtherScreen(
                                onNavigateBack = { currentScreen = "home" }
                            )
                        }
                    }
                }
            }
        }
    }
}