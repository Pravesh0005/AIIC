package com.aiic.app.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AIICColors(
    val background: Color = Color(0xFF000000), // True black or 0xFF09090B
    val surface: Color = Color(0xFF09090B),
    val surfaceElevated: Color = Color(0xFF18181B), // Zinc 900
    val surfaceCard: Color = Color(0xFF18181B),
    val surfaceBright: Color = Color(0xFF27272A), // Zinc 800

    val primary: Color = Color(0xFFFFFFFF), // Pristine White for actions
    val primaryVariant: Color = Color(0xFFF4F4F5), // Zinc 100
    val primaryMuted: Color = Color(0xFFD4D4D8), // Zinc 300
    val primaryContainer: Color = Color(0xFF18181B),

    val secondary: Color = Color(0xFF18181B),
    val secondaryVariant: Color = Color(0xFF27272A),
    val secondaryMuted: Color = Color(0xFF3F3F46),

    val accent: Color = Color(0xFFFFFFFF),
    val accentVariant: Color = Color(0xFFF4F4F5),
    val accentMuted: Color = Color(0xFF71717A),

    val tertiary: Color = Color(0xFF27272A),
    val tertiaryMuted: Color = Color(0xFF18181B),

    val warning: Color = Color(0xFFF59E0B), // Amber 500
    val warningMuted: Color = Color(0xFF78350F), // Amber 900

    val error: Color = Color(0xFFEF4444), // Red 500
    val errorMuted: Color = Color(0xFF7F1D1D), // Red 900

    val success: Color = Color(0xFF10B981), // Emerald 500
    val successMuted: Color = Color(0xFF064E3B), // Emerald 900

    val textPrimary: Color = Color(0xFFFFFFFF),
    val textSecondary: Color = Color(0xFFA1A1AA), // Zinc 400
    val textTertiary: Color = Color(0xFF71717A), // Zinc 500
    val textDisabled: Color = Color(0xFF52525B), // Zinc 600
    val textOnPrimary: Color = Color(0xFF000000), // Black text on white buttons

    val border: Color = Color(0xFF27272A), // Zinc 800
    val borderFocused: Color = Color(0xFFFFFFFF),
    val borderSubtle: Color = Color(0xFF18181B),

    val shimmer: Color = Color(0xFF27272A),
    val shimmerHighlight: Color = Color(0xFF3F3F46),

    // Replaced generic purple gradients with sophisticated monochrome gradients
    val gradientPrimaryStart: Color = Color(0xFFFFFFFF),
    val gradientPrimaryEnd: Color = Color(0xFFD4D4D8),
    val gradientSecondaryStart: Color = Color(0xFF27272A),
    val gradientSecondaryEnd: Color = Color(0xFF18181B),
    val gradientDarkStart: Color = Color(0xFF09090B),
    val gradientDarkEnd: Color = Color(0xFF000000),

    val glassBackground: Color = Color(0x1A000000),
    val glassBorder: Color = Color(0x33FFFFFF),
    val glassHighlight: Color = Color(0x0DFFFFFF),

    val overlay: Color = Color(0xCC000000),
    val scrim: Color = Color(0x99000000),

    val navBarBackground: Color = Color(0xF0000000),
    val navBarSelected: Color = Color(0xFFFFFFFF),
    val navBarUnselected: Color = Color(0xFF71717A),
)

val LocalAIICColors = staticCompositionLocalOf { AIICColors() }
