package com.aiic.app.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class AIICShapes(
    val small: RoundedCornerShape = RoundedCornerShape(8.dp),
    val medium: RoundedCornerShape = RoundedCornerShape(12.dp),
    val large: RoundedCornerShape = RoundedCornerShape(16.dp),
    val extraLarge: RoundedCornerShape = RoundedCornerShape(24.dp),
    val pill: RoundedCornerShape = RoundedCornerShape(50),
    val card: RoundedCornerShape = RoundedCornerShape(20.dp),
    val button: RoundedCornerShape = RoundedCornerShape(14.dp),
    val input: RoundedCornerShape = RoundedCornerShape(12.dp),
    val bottomSheet: RoundedCornerShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val dialog: RoundedCornerShape = RoundedCornerShape(28.dp),
)

val LocalAIICShapes = staticCompositionLocalOf { AIICShapes() }

fun AIICShapes.toMaterial3Shapes() = Shapes(
    small = small,
    medium = medium,
    large = large,
    extraLarge = extraLarge,
)
