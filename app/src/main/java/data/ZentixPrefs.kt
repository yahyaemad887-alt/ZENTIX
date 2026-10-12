package com.example.zentix.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * مخزن الإعدادات الدائم لتطبيق ZENTIX.
 * بيحفظ القيم في SharedPreferences وبيعرضها كـ StateFlow عشان أي شاشة تقدر تتابعها لحظيًا.
 * استخدامه: val prefs = ZentixPrefs.get(context)
 */
class ZentixPrefs private constructor(context: Context) {

    private val sp = context.applicationContext
        .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    private val _darkMode = MutableStateFlow(sp.getBoolean(KEY_DARK_MODE, true))
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _aiGuard = MutableStateFlow(sp.getBoolean(KEY_AI_GUARD, true))
    val aiGuard: StateFlow<Boolean> = _aiGuard.asStateFlow()

    private val _languageCode = MutableStateFlow(
        sp.getString(KEY_LANGUAGE, null)
            ?: Locale.getDefault().language.takeIf { it in SUPPORTED_LANGUAGE_CODES }
            ?: "en"
    )
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    /** مدة تنبيه جلسة اللعب بالدقايق (0 = متوقف) */
    private val _sessionTimerMinutes = MutableStateFlow(sp.getInt(KEY_TIMER_MINUTES, 0))
    val sessionTimerMinutes: StateFlow<Int> = _sessionTimerMinutes.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        sp.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _darkMode.value = enabled
    }

    fun setAiGuard(enabled: Boolean) {
        sp.edit().putBoolean(KEY_AI_GUARD, enabled).apply()
        _aiGuard.value = enabled
    }

    fun setLanguage(code: String) {
        if (code !in SUPPORTED_LANGUAGE_CODES) return
        sp.edit().putString(KEY_LANGUAGE, code).apply()
        _languageCode.value = code
    }

    fun setSessionTimerMinutes(minutes: Int) {
        val safe = minutes.coerceAtLeast(0)
        sp.edit().putInt(KEY_TIMER_MINUTES, safe).apply()
        _sessionTimerMinutes.value = safe
    }

    companion object {
        private const val FILE_NAME = "zentix_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_AI_GUARD = "ai_guard"
        private const val KEY_LANGUAGE = "language_code"
        private const val KEY_TIMER_MINUTES = "session_timer_minutes"

        val SUPPORTED_LANGUAGE_CODES = setOf("en", "ar", "de", "fr", "ja", "ko", "ru", "tr", "zh")

        @Volatile
        private var instance: ZentixPrefs? = null

        fun get(context: Context): ZentixPrefs =
            instance ?: synchronized(this) {
                instance ?: ZentixPrefs(context).also { instance = it }
            }
    }
}
