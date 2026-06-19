package com.aiic.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val aiicColors = AIICColors()
private val aiicTypography = AIICTypography()
private val aiicShapes = AIICShapes()
private val aiicSpacing = AIICSpacing()

private val DarkColorScheme = darkColorScheme(
    primary = aiicColors.primary,
    onPrimary = aiicColors.textOnPrimary,
    primaryContainer = aiicColors.primaryContainer,
    secondary = aiicColors.secondary,
    tertiary = aiicColors.tertiary,
    background = aiicColors.background,
    surface = aiicColors.surface,
    surfaceVariant = aiicColors.surfaceElevated,
    error = aiicColors.error,
    onBackground = aiicColors.textPrimary,
    onSurface = aiicColors.textPrimary,
    onSurfaceVariant = aiicColors.textSecondary,
    outline = aiicColors.border,
    outlineVariant = aiicColors.borderSubtle,
)

@Composable
fun AIICTheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = aiicColors.background,
            darkIcons = false
        )
        systemUiController.setNavigationBarColor(
            color = aiicColors.background,
            darkIcons = false
        )
    }

    CompositionLocalProvider(
        LocalAIICColors provides aiicColors,
        LocalAIICTypography provides aiicTypography,
        LocalAIICShapes provides aiicShapes,
        LocalAIICSpacing provides aiicSpacing,
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = aiicTypography.toMaterial3Typography(),
            shapes = aiicShapes.toMaterial3Shapes(),
            content = content
        )
    }
}

object AIICTheme {
    val colors: AIICColors
        @Composable @ReadOnlyComposable
        get() = LocalAIICColors.current

    val typography: AIICTypography
        @Composable @ReadOnlyComposable
        get() = LocalAIICTypography.current

    val shapes: AIICShapes
        @Composable @ReadOnlyComposable
        get() = LocalAIICShapes.current

    val spacing: AIICSpacing
        @Composable @ReadOnlyComposable
        get() = LocalAIICSpacing.current
}
