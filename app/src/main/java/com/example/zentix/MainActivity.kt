package com.example.zentix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.zentix.UI.HomeScreen
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
                    // متغير لتتبع الشاشة الحالية ("home" أو "settings")
                    var currentScreen by remember { mutableStateOf("home") }

                    if (currentScreen == "home") {
                        HomeScreen(
                            onNavigateToSettings = { currentScreen = "settings" },
                            onNavigateToMSISuite = { /* هنربطها بعدين */ },
                            onNavigateToBenchmark = { /* هنربطها بعدين */ },
                            onNavigateToProfiles = { /* هنربطها بعدين */ },
                            onNavigateToOverclock = { /* هنربطها بعدين */ }
                        )
                    } else {
                        SettingsScreen(
                            onNavigateBack = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}