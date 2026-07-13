package com.aiic.app.presentation.feature_interview.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun VoiceWaveform(
    rmsLevel: Float,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF6C5CE7),
    barColorSecondary: Color = Color(0xFFA29BFE),
    barCount: Int = 40
) {
    val barHeights = remember { mutableStateListOf<Float>().apply { repeat(barCount) { add(0.1f) } } }

    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val animationPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    LaunchedEffect(rmsLevel, isActive) {
        if (isActive) {
            for (i in 0 until barCount) {
                val phaseOffset = (animationPhase + i * 15f) * Math.PI.toFloat() / 180f
                val wave = sin(phaseOffset) * 0.3f + 0.3f
                val rmsContribution = rmsLevel * 0.7f
                val targetHeight = (wave + rmsContribution).coerceIn(0.05f, 1f)
                barHeights[i] = barHeights[i] + (targetHeight - barHeights[i]) * 0.3f
            }
        } else {
            for (i in 0 until barCount) {
                barHeights[i] = barHeights[i] * 0.95f + 0.05f * 0.05f
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / (barCount * 2f)
        val spacing = barWidth
        val cornerRadius = barWidth / 2f

        for (i in 0 until barCount) {
            val height = barHeights.getOrElse(i) { 0.1f }
            val barHeight = canvasHeight * height
            val x = i * (barWidth + spacing) + spacing / 2f
            val y = (canvasHeight - barHeight) / 2f

            val distFromCenter = kotlin.math.abs(i - barCount / 2f) / (barCount / 2f)
            val color = lerp(barColor, barColorSecondary, distFromCenter)

            drawRoundRect(
                color = color.copy(alpha = if (isActive) 0.9f else 0.3f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}

private fun lerp(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = start.red + (stop.red - start.red) * fraction,
        green = start.green + (stop.green - start.green) * fraction,
        blue = start.blue + (stop.blue - start.blue) * fraction,
        alpha = start.alpha + (stop.alpha - start.alpha) * fraction
    )
}

@Composable
fun PulsingMicIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF6C5CE7)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (isActive) 0.2f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f

        if (isActive) {
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius * scale
            )
        }

        drawCircle(
            color = color.copy(alpha = if (isActive) 0.9f else 0.4f),
            radius = radius * 0.7f
        )
    }
}
