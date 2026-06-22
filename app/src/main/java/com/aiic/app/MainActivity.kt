package com.aiic.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.navigation.AIICNavHost
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.aiic.app.core.theme.LocalThemeToggle
import com.aiic.app.core.theme.LocalIsDarkTheme
import android.content.Context

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
}
