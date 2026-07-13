package com.aiic.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.core.theme.LocalIsDarkTheme
import com.aiic.app.core.theme.LocalThemeToggle
import com.aiic.app.navigation.AIICNavHost
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        
        val prefs = newBase.getSharedPreferences("aiic_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("app_language", "English") ?: "English"
        val locale = mapLanguageToLocale(savedLang)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("aiic_prefs", Context.MODE_PRIVATE)

        setContent {
            val isDarkThemePref = remember { mutableStateOf(sharedPrefs.getBoolean("dark_mode", true)) }

            CompositionLocalProvider(
                LocalThemeToggle provides { isDark ->
                    sharedPrefs.edit().putBoolean("dark_mode", isDark).apply()
                    isDarkThemePref.value = isDark
                },
                LocalIsDarkTheme provides isDarkThemePref.value
            ) {
                AIICTheme(darkTheme = isDarkThemePref.value) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = AIICTheme.colors.background
                    ) {
                        AIICNavHost()
                    }
                }
            }
        }
    }

    companion object {
        fun mapLanguageToLocale(language: String): Locale {
            return when (language.lowercase()) {
                "hindi" -> Locale("hi", "IN")
                "spanish" -> Locale("es")
                "french" -> Locale("fr")
                "german" -> Locale("de")
                "japanese" -> Locale("ja")
                "korean" -> Locale("ko")
                "chinese" -> Locale("zh")
                else -> Locale("en")
            }
        }
    }
}
