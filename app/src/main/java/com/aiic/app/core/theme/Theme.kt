package com.aiic.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.lightColorScheme

private val aiicTypography = AIICTypography()
private val aiicShapes = AIICShapes()
private val aiicSpacing = AIICSpacing()

private fun darkColorScheme(colors: AIICColors) = darkColorScheme(
    primary = colors.primary,
    onPrimary = colors.textOnPrimary,
    primaryContainer = colors.primaryContainer,
    secondary = colors.secondary,
    tertiary = colors.tertiary,
    background = colors.background,
    surface = colors.surface,
    surfaceVariant = colors.surfaceElevated,
    error = colors.error,
    onBackground = colors.textPrimary,
    onSurface = colors.textPrimary,
    onSurfaceVariant = colors.textSecondary,
    outline = colors.border,
    outlineVariant = colors.borderSubtle,
)

private fun lightColorScheme(colors: AIICColors) = lightColorScheme(
    primary = colors.primary,
    onPrimary = colors.textOnPrimary,
    primaryContainer = colors.primaryContainer,
    secondary = colors.secondary,
    tertiary = colors.tertiary,
    background = colors.background,
    surface = colors.surface,
    surfaceVariant = colors.surfaceElevated,
    error = colors.error,
    onBackground = colors.textPrimary,
    onSurface = colors.textPrimary,
    onSurfaceVariant = colors.textSecondary,
    outline = colors.border,
    outlineVariant = colors.borderSubtle,
)

@Composable
fun AIICTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val aiicColors = if (darkTheme) AIICColors() else lightColors()
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = aiicColors.background,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = aiicColors.background,
            darkIcons = !darkTheme
        )
    }

    CompositionLocalProvider(
        LocalAIICColors provides aiicColors,
        LocalAIICTypography provides aiicTypography,
        LocalAIICShapes provides aiicShapes,
        LocalAIICSpacing provides aiicSpacing,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme(aiicColors) else lightColorScheme(aiicColors),
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
