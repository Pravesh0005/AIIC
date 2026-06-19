package com.aiic.app.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AIICColors(
    val background: Color = Color(0xFF0A0A0F),
    val surface: Color = Color(0xFF12121A),
    val surfaceElevated: Color = Color(0xFF1A1A26),
    val surfaceCard: Color = Color(0xFF16161F),
    val surfaceBright: Color = Color(0xFF22222E),

    val primary: Color = Color(0xFF7C5CFC),
    val primaryVariant: Color = Color(0xFF6B4EE6),
    val primaryMuted: Color = Color(0xFF3D2E80),
    val primaryContainer: Color = Color(0xFF1E1540),

    val secondary: Color = Color(0xFF00D4AA),
    val secondaryVariant: Color = Color(0xFF00B894),
    val secondaryMuted: Color = Color(0xFF0D3D33),

    val accent: Color = Color(0xFFFF6B9D),
    val accentVariant: Color = Color(0xFFE85588),
    val accentMuted: Color = Color(0xFF3D1A2A),

    val tertiary: Color = Color(0xFF4ECDC4),
    val tertiaryMuted: Color = Color(0xFF1A3D3A),

    val warning: Color = Color(0xFFFFB84D),
    val warningMuted: Color = Color(0xFF3D2E1A),

    val error: Color = Color(0xFFFF5252),
    val errorMuted: Color = Color(0xFF3D1A1A),

    val success: Color = Color(0xFF4CAF50),
    val successMuted: Color = Color(0xFF1A3D1C),

    val textPrimary: Color = Color(0xFFF5F5FA),
    val textSecondary: Color = Color(0xFFB0B0C0),
    val textTertiary: Color = Color(0xFF6E6E82),
    val textDisabled: Color = Color(0xFF4A4A5C),
    val textOnPrimary: Color = Color(0xFFFFFFFF),

    val border: Color = Color(0xFF2A2A3A),
    val borderFocused: Color = Color(0xFF7C5CFC),
    val borderSubtle: Color = Color(0xFF1E1E2E),

    val shimmer: Color = Color(0xFF2A2A3A),
    val shimmerHighlight: Color = Color(0xFF3A3A4A),

    val gradientPrimaryStart: Color = Color(0xFF7C5CFC),
    val gradientPrimaryEnd: Color = Color(0xFFFF6B9D),
    val gradientSecondaryStart: Color = Color(0xFF00D4AA),
    val gradientSecondaryEnd: Color = Color(0xFF4ECDC4),
    val gradientDarkStart: Color = Color(0xFF0A0A0F),
    val gradientDarkEnd: Color = Color(0xFF12121A),

    val glassBackground: Color = Color(0x1AFFFFFF),
    val glassBorder: Color = Color(0x33FFFFFF),
    val glassHighlight: Color = Color(0x0DFFFFFF),

    val overlay: Color = Color(0xCC000000),
    val scrim: Color = Color(0x99000000),

    val navBarBackground: Color = Color(0xF00A0A0F),
    val navBarSelected: Color = Color(0xFF7C5CFC),
    val navBarUnselected: Color = Color(0xFF6E6E82),
)

val LocalAIICColors = staticCompositionLocalOf { AIICColors() }
