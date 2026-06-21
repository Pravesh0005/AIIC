package com.aiic.app.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AIICColors(
    val background: Color = Color(0xFF0A0C10), // Deep premium dark background
    val surface: Color = Color(0xFF11141A),
    val surfaceElevated: Color = Color(0xFF1A1D24),
    val surfaceCard: Color = Color(0xFF1A1D24),
    val surfaceBright: Color = Color(0xFF232730),

    val primary: Color = Color(0xFF6366F1), // Vibrant Indigo
    val primaryVariant: Color = Color(0xFF4F46E5),
    val primaryMuted: Color = Color(0xFF312E81),
    val primaryContainer: Color = Color(0xFF1E1B4B),

    val secondary: Color = Color(0xFF14B8A6), // Teal for secondary actions
    val secondaryVariant: Color = Color(0xFF0D9488),
    val secondaryMuted: Color = Color(0xFF134E4A),

    val accent: Color = Color(0xFF8B5CF6), // Violet for accents
    val accentVariant: Color = Color(0xFF7C3AED),
    val accentMuted: Color = Color(0xFF4C1D95),

    val tertiary: Color = Color(0xFF334155),
    val tertiaryMuted: Color = Color(0xFF1E293B),

    val warning: Color = Color(0xFFF59E0B),
    val warningMuted: Color = Color(0xFF78350F),

    val error: Color = Color(0xFFEF4444),
    val errorMuted: Color = Color(0xFF7F1D1D),

    val success: Color = Color(0xFF10B981),
    val successMuted: Color = Color(0xFF064E3B),

    val textPrimary: Color = Color(0xFFFFFFFF),
    val textSecondary: Color = Color(0xFF94A3B8), // Slate 400
    val textTertiary: Color = Color(0xFF64748B), // Slate 500
    val textDisabled: Color = Color(0xFF475569), // Slate 600
    val textOnPrimary: Color = Color(0xFFFFFFFF), // White text on vibrant primary buttons

    val border: Color = Color(0xFF2D3340), // Clear, distinguishable borders
    val borderFocused: Color = Color(0xFF6366F1),
    val borderSubtle: Color = Color(0xFF1F2430),

    val shimmer: Color = Color(0xFF1E2430),
    val shimmerHighlight: Color = Color(0xFF2A3240),

    // Sophisticated gradients
    val gradientPrimaryStart: Color = Color(0xFF8B5CF6),
    val gradientPrimaryEnd: Color = Color(0xFF6366F1),
    val gradientSecondaryStart: Color = Color(0xFF14B8A6),
    val gradientSecondaryEnd: Color = Color(0xFF0D9488),
    val gradientDarkStart: Color = Color(0xFF11141A),
    val gradientDarkEnd: Color = Color(0xFF0A0C10),

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
