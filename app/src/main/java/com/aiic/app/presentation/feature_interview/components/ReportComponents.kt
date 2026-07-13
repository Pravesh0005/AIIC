package com.aiic.app.presentation.feature_interview.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiic.app.core.theme.AIICTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedScoreRing(
    score: Float,
    maxScore: Float = 100f,
    label: String,
    modifier: Modifier = Modifier,
    size: Int = 100,
    strokeWidth: Float = 10f,
    primaryColor: Color = AIICTheme.colors.primary,
    trackColor: Color = AIICTheme.colors.borderSubtle
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(score) {
        progress.animateTo(
            targetValue = score / maxScore,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size.dp)
        ) {
            Canvas(modifier = Modifier.size(size.dp)) {
                val sweepAngle = 270f * progress.value
                val arcSize = this.size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                drawArc(
                    color = trackColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                drawArc(
                    color = primaryColor,
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Text(
                text = "${(score).toInt()}",
                style = AIICTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = AIICTheme.typography.labelSmall,
            color = AIICTheme.colors.textSecondary,
            maxLines = 1
        )
    }
}

@Composable
fun RadarChart(
    scores: List<Float>,
    labels: List<String>,
    maxScore: Float = 100f,
    modifier: Modifier = Modifier,
    fillColor: Color = AIICTheme.colors.primary.copy(alpha = 0.25f),
    strokeColor: Color = AIICTheme.colors.primary,
    gridColor: Color = AIICTheme.colors.borderSubtle,
    labelColor: Color = AIICTheme.colors.textSecondary
) {
    require(scores.size == labels.size) { "Scores and labels must have same size" }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(scores) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(32.dp)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = minOf(centerX, centerY) * 0.75f
        val sides = scores.size
        val angleStep = 360f / sides

        for (level in 1..3) {
            val levelRadius = radius * level / 3f
            val gridPath = Path()
            for (i in 0 until sides) {
                val angle = Math.toRadians((angleStep * i - 90).toDouble())
                val x = centerX + levelRadius * cos(angle).toFloat()
                val y = centerY + levelRadius * sin(angle).toFloat()
                if (i == 0) gridPath.moveTo(x, y)
                else gridPath.lineTo(x, y)
            }
            gridPath.close()
            drawPath(gridPath, color = gridColor, style = Stroke(width = 1f))
        }

        for (i in 0 until sides) {
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val endX = centerX + radius * cos(angle).toFloat()
            val endY = centerY + radius * sin(angle).toFloat()
            drawLine(gridColor, Offset(centerX, centerY), Offset(endX, endY), strokeWidth = 1f)
        }

        val dataPath = Path()
        for (i in 0 until sides) {
            val value = (scores[i] / maxScore).coerceIn(0f, 1f) * animProgress.value
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val x = centerX + radius * value * cos(angle).toFloat()
            val y = centerY + radius * value * sin(angle).toFloat()
            if (i == 0) dataPath.moveTo(x, y)
            else dataPath.lineTo(x, y)
        }
        dataPath.close()

        drawPath(dataPath, color = fillColor)
        drawPath(dataPath, color = strokeColor, style = Stroke(width = 2.5f))

        for (i in 0 until sides) {
            val value = (scores[i] / maxScore).coerceIn(0f, 1f) * animProgress.value
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val x = centerX + radius * value * cos(angle).toFloat()
            val y = centerY + radius * value * sin(angle).toFloat()
            drawCircle(strokeColor, radius = 4f, center = Offset(x, y))
            drawCircle(Color.White, radius = 2f, center = Offset(x, y))
        }

        for (i in 0 until sides) {
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val labelRadius = radius + 24f
            val x = centerX + labelRadius * cos(angle).toFloat()
            val y = centerY + labelRadius * sin(angle).toFloat()

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = labelColor.hashCode()
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 28f
                    isAntiAlias = true
                }
                drawText(labels[i], x, y + 10f, paint)
            }
        }
    }
}

@Composable
fun ScoreBar(
    label: String,
    score: Float,
    maxScore: Float = 100f,
    color: Color = AIICTheme.colors.primary
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(score) {
        progress.animateTo(
            targetValue = (score / maxScore).coerceIn(0f, 1f),
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textSecondary)
            Text("${score.toInt()}%", style = AIICTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(AIICTheme.colors.borderSubtle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun TagPill(
    text: String,
    color: Color = AIICTheme.colors.primary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = AIICTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MetricRow(
    label: String,
    value: String,
    color: Color = AIICTheme.colors.textPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textSecondary)
        Text(value, style = AIICTheme.typography.bodyMedium, color = color, fontWeight = FontWeight.SemiBold)
    }
}
