package com.aiic.app.common.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aiic.app.core.theme.AIICTheme

/**
 * AIIC Custom Loading & Animation Icons
 * As per Design Reference:
 * - Dot Pulse
 * - Ring Loader
 * - AI Thinking (Pulse / Wave)
 * - Gradient Spin
 */

@Composable
fun AIICDotPulse(
    modifier: Modifier = Modifier,
    color: Color = AIICTheme.colors.primary,
    dotSize: Dp = 8.dp,
    delayUnit: Int = 200
) {
    val infiniteTransition = rememberInfiniteTransition(label = "DotPulse")

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = delay, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleAnim"
    ).value

    val scale1 = animateScaleWithDelay(0)
    val scale2 = animateScaleWithDelay(delayUnit)
    val scale3 = animateScaleWithDelay(delayUnit * 2)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(scale1, color, dotSize)
        Dot(scale2, color, dotSize)
        Dot(scale3, color, dotSize)
    }
}

@Composable
private fun Dot(scale: Float, color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun AIICRingLoader(
    modifier: Modifier = Modifier,
    color: Color = AIICTheme.colors.primary,
    strokeWidth: Dp = 4.dp
) {
    // Custom styled CircularProgressIndicator for Ring Loader
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun AIICPulseAnimation(
    modifier: Modifier = Modifier,
    color: Color = AIICTheme.colors.primary,
    baseSize: Dp = 60.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Outer pulsing ring
        Box(
            modifier = Modifier
                .size(baseSize)
                .scale(scale)
                .background(color.copy(alpha = alpha), shape = CircleShape)
        )
        // Inner solid core
        Box(
            modifier = Modifier
                .size(baseSize / 2)
                .background(color, shape = CircleShape)
        )
    }
}

@Composable
fun AIICDataLoadingBar(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = AIICTheme.colors.primary,
    trackColor: Color = AIICTheme.colors.surfaceBright
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ProgressBar"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(8.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}
