package com.aiic.app.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AIICSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val base: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 40.dp,
    val huge: Dp = 48.dp,
    val massive: Dp = 64.dp,
    val screenHorizontal: Dp = 20.dp,
    val screenVertical: Dp = 24.dp,
    val cardPadding: Dp = 16.dp,
    val sectionGap: Dp = 28.dp,
)

val LocalAIICSpacing = staticCompositionLocalOf { AIICSpacing() }
