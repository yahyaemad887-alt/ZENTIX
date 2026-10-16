package com.example.zentix.UI

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AppLanguage(val code: String, val nativeName: String, val englishName: String)

@Composable
fun OnboardingFlow(
    context: Context,
    onFinished: () -> Unit
) {
    var currentStep by rectMemoryStateOpt(0)

    // استخدام نفس اسم SharedPreferences ومفاتيح الإعدادات عندك تماماً
    val prefs = context.getSharedPreferences("zentix_prefs", Context.MODE_PRIVATE)
    var selectedLang by remember { mutableStateOf(prefs.getString("selected_lang", "ar") ?: "ar") }

    val languages = listOf(
        AppLanguage("ar", "العربية", "Arabic"),
        AppLanguage("en", "English", "English"),
        AppLanguage("de", "Deutsch", "German"),
        AppLanguage("fr", "Français", "French"),
        AppLanguage("ja", "日本語", "Japanese"),
        AppLanguage("ko", "한국어", "Korean"),
        AppLanguage("ru", "Русский", "Russian"),
        AppLanguage("tr", "Türkçe", "Turkish"),
        AppLanguage("zh", "中文", "Chinese")
    )

    // قاموس النصوص لكل اللغات المدعومة عشان يتغير فوراً مع اختيار المستخدم
    val descriptions = mapOf(
        "ar" to "ZENTIX هو رفيقك الأقوى لتحسين أداء الألعاب، رفع معدل الإطارات (FPS)، إدارة الهاردوير، والارتقاء بتجربة هاتفك لمستويات احترافية.",
        "en" to "ZENTIX is your ultimate high-performance gaming companion. Designed to optimize frame rates, manage hardware acceleration, and elevate your mobile gaming experience.",
        "de" to "ZENTIX ist dein ultimativer Gaming-Begleiter mit hoher Leistung. Entwickelt, um Bildraten zu optimieren und das mobile Spielerlebnis zu verbessern.",
        "fr" to "ZENTIX est votre compagnon de jeu ultime à hautes performances. Conçu pour optimiser les fréquences d'images et améliorer votre expérience de jeu.",
        "ja" to "ZENTIXは、ゲームパフォーマンスを向上させ、フレームレートを最適化し、究極のモバイルゲーム体験を提供するための強力なツールです。",
        "ko" to "ZENTIX는 최고의 고성능 게이밍 파트너입니다. 프레임 레이트를 최적화하고 하드웨어를 관리하여 모바일 게이밍 경험을 극대화합니다.",
        "ru" to "ZENTIX — твой лучший игровой компаньон для повышения производительности, оптимизации кадров (FPS) и управления железом.",
        "tr" to "ZENTIX, oyun performansını artırmak, kare hızlarını optimize etmek ve mobil oyun deneyiminizi en üst düzeye çıkarmak için nihai yardımcınızdır.",
        "zh" to "ZENTIX 是您极致的高性能游戏伴侣。旨在优化帧率、管理硬件加速并将您的手机游戏体验提升到专业水平。"
    )

    val helloTexts = mapOf(
        "ar" to "أهلاً بك", "en" to "HELLO", "de" to "HALLO",
        "fr" to "BONJOUR", "ja" to "こんにちは", "ko" to "안녕하세요",
        "ru" to "ПРИВЕТ", "tr" to "MERHABA", "zh" to "你好"
    )

    val startTexts = mapOf(
        "ar" to "ابدأ", "en" to "START", "de" to "STARTEN",
        "fr" to "COMMENCER", "ja" to "スタート", "ko" to "시작",
        "ru" to "СТАРТ", "tr" to "BAŞLA", "zh" to "开始"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050608)),
        contentAlignment = Alignment.Center
    ) {
        when (currentStep) {
            // الشاشة الأولى: اختيار اللغة
            0 -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "SELECT LANGUAGE / اختر اللغة",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(languages) { lang ->
                            LanguageItemCard(
                                lang = lang,
                                isSelected = selectedLang == lang.code,
                                onClick = {
                                    selectedLang = lang.code
                                    // حفظ اللغة بالطريقة اللي الإعدادات بتعتمد عليها
                                    prefs.edit().putString("selected_lang", lang.code).apply()
                                    ZentixGlobalSettings.selectedLanguageCode = lang.code

                                    // إعادة تشغيل النشاط فوراً لتطبيق تغيير اللغة على مستوى التطبيق بالكامل (بنفس لغة الإعدادات)
                                    (context as? Activity)?.recreate()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = { currentStep = 1 },
                        containerColor = Color(0xFFFF1744)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // الشاشة الثانية: تعريف التطبيق (تتغير تلقائياً حسب اللغة المختارة)
            1 -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ZENTIX ENGINE",
                        color = Color(0xFFFF1744),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = descriptions[selectedLang] ?: descriptions["en"]!!,
                        color = Color(0xFFD0D0D0),
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                    FloatingActionButton(
                        onClick = { currentStep = 2 },
                        containerColor = Color(0xFFFF1744)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                    }
                }
            }

            // الشاشة الثالثة: كلمة الترحيب وزر Start المتوافقان مع اللغة
            2 -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = helloTexts[selectedLang] ?: "HELLO",
                        color = Color.White,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ZENTIX GAMING EXPERIENCE",
                        color = Color(0xFFFF1744),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(60.dp))

                    Button(
                        onClick = {
                            prefs.edit().putBoolean("is_first_run", false).apply()
                            onFinished()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(0.75f).height(56.dp)
                    ) {
                        Text(
                            text = startTexts[selectedLang] ?: "START",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                    }
                }
            }
        }
    }
}

// دالة مساعدة لإنشاء الحالة لتعمل بسلاسة داخل Compose
@Composable
fun rememberStateOf(initialVal: Int) = remember { mutableStateOf(initialVal) }
// دالة توافقية بسيطة
@Composable
fun rectMemoryStateOpt(initialVal: Int) = remember { mutableStateOf(initialVal) }

@Composable
fun LanguageItemCard(lang: AppLanguage, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(0.85f).height(52.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFF1744).copy(alpha = 0.25f) else Color(0xFF121622)
        ),
        border = BorderStroke(1.5.dp, if (isSelected) Color(0xFFFF1744) else Color(0xFF1F2636))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = lang.nativeName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = lang.englishName, color = Color.Gray, fontSize = 12.sp)
        }
    }
}