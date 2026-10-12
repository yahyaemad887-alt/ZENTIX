package com.example.zentix.UI

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** ألوان الخلفية والكروت والنصوص اللي بتتغير بين الداكن والفاتح (ألوان الـ Neon بتفضل ثابتة). */
data class ZentixColors(
    val background: Color,
    val card: Color,
    val border: Color,
    val text: Color,
    val subText: Color
)

val DarkZentixColors = ZentixColors(
    background = Color(0xFF090A0F),
    card = Color(0xFF12141D),
    border = Color(0xFF1E2230),
    text = Color.White,
    subText = Color.Gray
)

val LightZentixColors = ZentixColors(
    background = Color(0xFFF4F5F9),
    card = Color.White,
    border = Color(0xFFDADDE8),
    text = Color(0xFF12141D),
    subText = Color(0xFF6B7080)
)

val LocalZentixColors = staticCompositionLocalOf { DarkZentixColors }

/** الاستخدام داخل أي Composable: zentixColors.background / .card / .border / .text / .subText */
val zentixColors: ZentixColors
    @Composable
    @ReadOnlyComposable
    get() = LocalZentixColors.current

@Composable
fun ZentixTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalZentixColors provides if (darkTheme) DarkZentixColors else LightZentixColors
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme(),
            content = content
        )
    }
}
