package com.aiic.app.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AIICColors(
    
    val background: Color = Color(0xFF0A0F1C),
    val surface: Color = Color(0xFF121826),
    val surfaceElevated: Color = Color(0xFF1E2433),
    val surfaceCard: Color = Color(0xFF121826),
    val surfaceBright: Color = Color(0xFF2A3347),

    val primary: Color = Color(0xFF006BFF),
    val primaryVariant: Color = Color(0xFF0055CC),
    val primaryMuted: Color = Color(0xFF0A1A3A),
    val primaryContainer: Color = Color(0xFF0D2240),

    val secondary: Color = Color(0xFF00D4FF),
    val secondaryVariant: Color = Color(0xFF00B8D4),
    val secondaryMuted: Color = Color(0xFF0A2A33),

    val accent: Color = Color(0xFF00D4FF),
    val accentVariant: Color = Color(0xFF00B8D4),
    val accentMuted: Color = Color(0xFF0A2A33),

    val tertiary: Color = Color(0xFF1E2433),
    val tertiaryMuted: Color = Color(0xFF151B2B),

    val warning: Color = Color(0xFFF59E0B),
    val warningMuted: Color = Color(0xFF78350F),

    val error: Color = Color(0xFFEF4444),
    val errorMuted: Color = Color(0xFF7F1D1D),

    val success: Color = Color(0xFF10B981),
    val successMuted: Color = Color(0xFF064E3B),

    val purple: Color = Color(0xFF8B5CF6),
    val orange: Color = Color(0xFFF59E0B),
    val green: Color = Color(0xFF10B981),
    val red: Color = Color(0xFFEF4444),
    val cyan: Color = Color(0xFF00D4FF),

    val textPrimary: Color = Color(0xFFFFFFFF),
    val textSecondary: Color = Color(0xFF94A3B8),
    val textTertiary: Color = Color(0xFF64748B),
    val textDisabled: Color = Color(0xFF475569),
    val textOnPrimary: Color = Color(0xFFFFFFFF),

    val border: Color = Color(0xFF1E2433),
    val borderFocused: Color = Color(0xFF006BFF),
    val borderSubtle: Color = Color(0xFF1A2035),

    val shimmer: Color = Color(0xFF1E2433),
    val shimmerHighlight: Color = Color(0xFF2A3347),

    val gradientPrimaryStart: Color = Color(0xFF006BFF),
    val gradientPrimaryEnd: Color = Color(0xFF00D4FF),
    val gradientSecondaryStart: Color = Color(0xFF00D4FF),
    val gradientSecondaryEnd: Color = Color(0xFF006BFF),
    val gradientDarkStart: Color = Color(0xFF121826),
    val gradientDarkEnd: Color = Color(0xFF0A0F1C),

    val glassBackground: Color = Color(0x1A006BFF),
    val glassBorder: Color = Color(0x33006BFF),
    val glassHighlight: Color = Color(0x0D00D4FF),

    val overlay: Color = Color(0xCC0A0F1C),
    val scrim: Color = Color(0x990A0F1C),

    val navBarBackground: Color = Color(0xF00A0F1C),
    val navBarSelected: Color = Color(0xFF006BFF),
    val navBarUnselected: Color = Color(0xFF64748B),

    val glowBlue: Color = Color(0xFF006BFF),
    val glowCyan: Color = Color(0xFF00D4FF),
)

val LocalAIICColors = staticCompositionLocalOf { AIICColors() }
val LocalThemeToggle = staticCompositionLocalOf<(Boolean) -> Unit> { {} }
val LocalIsDarkTheme = staticCompositionLocalOf<Boolean> { true }

fun lightColors() = AIICColors(
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceBright = Color(0xFFF1F5F9),
    primary = Color(0xFF006BFF),
    primaryVariant = Color(0xFF0055CC),
    primaryMuted = Color(0xFFDBEAFE),
    primaryContainer = Color(0xFFE0EDFF),
    secondary = Color(0xFF00B8D4),
    secondaryVariant = Color(0xFF0097A7),
    secondaryMuted = Color(0xFFE0F7FA),
    accent = Color(0xFF00B8D4),
    accentVariant = Color(0xFF0097A7),
    accentMuted = Color(0xFFE0F7FA),
    tertiary = Color(0xFFF1F5F9),
    tertiaryMuted = Color(0xFFE2E8F0),
    warning = Color(0xFFF59E0B),
    warningMuted = Color(0xFFFEF3C7),
    error = Color(0xFFEF4444),
    errorMuted = Color(0xFFFEE2E2),
    success = Color(0xFF10B981),
    successMuted = Color(0xFFD1FAE5),
    purple = Color(0xFF8B5CF6),
    orange = Color(0xFFF59E0B),
    green = Color(0xFF10B981),
    red = Color(0xFFEF4444),
    cyan = Color(0xFF00B8D4),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textTertiary = Color(0xFF94A3B8),
    textDisabled = Color(0xFFCBD5E1),
    textOnPrimary = Color(0xFFFFFFFF),
    border = Color(0xFFE2E8F0),
    borderFocused = Color(0xFF006BFF),
    borderSubtle = Color(0xFFF1F5F9),
    shimmer = Color(0xFFE2E8F0),
    shimmerHighlight = Color(0xFFF1F5F9),
    gradientPrimaryStart = Color(0xFF006BFF),
    gradientPrimaryEnd = Color(0xFF00B8D4),
    gradientSecondaryStart = Color(0xFF00B8D4),
    gradientSecondaryEnd = Color(0xFF006BFF),
    gradientDarkStart = Color(0xFFFFFFFF),
    gradientDarkEnd = Color(0xFFF8FAFC),
    glassBackground = Color(0x0A006BFF),
    glassBorder = Color(0x1A006BFF),
    glassHighlight = Color(0x0500B8D4),
    overlay = Color(0x66000000),
    scrim = Color(0x4D000000),
    navBarBackground = Color(0xFFFFFFFF),
    navBarSelected = Color(0xFF006BFF),
    navBarUnselected = Color(0xFF94A3B8),
    glowBlue = Color(0xFF006BFF),
    glowCyan = Color(0xFF00B8D4),
)
