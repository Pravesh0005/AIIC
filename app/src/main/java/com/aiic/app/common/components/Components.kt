package com.aiic.app.common.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aiic.app.core.theme.AIICTheme

@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color = AIICTheme.colors.primary,
    contentColor: Color = AIICTheme.colors.textOnPrimary,
    content: @Composable (() -> Unit)? = null,
) {

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = AIICTheme.shapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = AIICTheme.colors.surfaceBright,
            disabledContentColor = AIICTheme.colors.textDisabled
        ),
        contentPadding = PaddingValues(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                content != null -> content()
                isLoading -> Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = contentColor,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Please wait...",
                        style = AIICTheme.typography.button,
                        color = contentColor,
                    )
                }
                else -> Text(
                    text = text,
                    style = AIICTheme.typography.button,
                    color = contentColor,
                )
            }
        }
    }
}

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AIICTheme.shapes.card)
            .background(AIICTheme.colors.surfaceElevated)
            .border(1.dp, AIICTheme.colors.border, AIICTheme.shapes.card)
            .padding(AIICTheme.spacing.cardPadding),
    ) {
        content()
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AIICTheme.spacing.screenHorizontal),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            Text(
                text = title,
                style = AIICTheme.typography.titleMedium,
                color = AIICTheme.colors.textPrimary,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textTertiary,
                )
            }
        }
        if (action != null && onAction != null) {
            Text(
                text = action,
                style = AIICTheme.typography.labelMedium,
                color = AIICTheme.colors.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(AIICTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
fun LoadingShimmer(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate",
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            AIICTheme.colors.shimmer,
            AIICTheme.colors.shimmerHighlight,
            AIICTheme.colors.shimmer,
        ),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(AIICTheme.shapes.card)
            .background(shimmerBrush),
    )
}

@Composable
fun ShimmerList(
    count: Int = 3,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = AIICTheme.spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(AIICTheme.spacing.md),
    ) {
        repeat(count) {
            LoadingShimmer()
        }
    }
}

@Composable
fun EmptyStateView(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AIICTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon?.invoke()
        Spacer(Modifier.height(AIICTheme.spacing.base))
        Text(
            text = title,
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textPrimary,
        )
        Spacer(Modifier.height(AIICTheme.spacing.sm))
        Text(
            text = description,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textTertiary,
        )
        if (action != null) {
            Spacer(Modifier.height(AIICTheme.spacing.lg))
            action()
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AIICTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Something went wrong",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.error,
        )
        Spacer(Modifier.height(AIICTheme.spacing.sm))
        Text(
            text = message,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textTertiary,
        )
        Spacer(Modifier.height(AIICTheme.spacing.lg))
        PremiumButton(
            text = "Try Again",
            onClick = onRetry,
            modifier = Modifier.width(180.dp),
            containerColor = AIICTheme.colors.error,
            contentColor = AIICTheme.colors.textPrimary,
        )
    }
}

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        AIICTheme.colors.gradientPrimaryStart,
                        AIICTheme.colors.gradientPrimaryEnd,
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "AI",
            style = AIICTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = Color.White,
            ),
        )
    }
}

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = AIICTheme.typography.displayMedium,
    colors: List<Color>? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = AIICTheme.colors.textPrimary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun ScoreCard(
    title: String,
    score: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    accentColor: Color = AIICTheme.colors.primary,
) {
    PremiumCard(modifier = modifier) {
        Column {
            Text(
                text = title,
                style = AIICTheme.typography.labelMedium,
                color = AIICTheme.colors.textTertiary,
            )
            Spacer(Modifier.height(AIICTheme.spacing.sm))
            Text(
                text = score,
                style = AIICTheme.typography.displayMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(AIICTheme.spacing.xs))
            Text(
                text = subtitle,
                style = AIICTheme.typography.bodySmall,
                color = AIICTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
fun FeatureCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(AIICTheme.shapes.medium)
                    .background(AIICTheme.colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Spacer(Modifier.width(AIICTheme.spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AIICTheme.typography.titleSmall,
                    color = AIICTheme.colors.textPrimary,
                )
                Text(
                    text = description,
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textTertiary,
                )
            }
        }
    }
}
