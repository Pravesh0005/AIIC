package com.aiic.app.common.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aiic.app.core.theme.AIICTheme

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  AIIC Design System — Reusable Components
 *  Matching official design reference boards.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */

// ── PremiumButton ──────────────────────────────────
// Blue gradient button with arrow, 56dp height, pill shape
// Reference: Login "Sign In →", Register "Create Account →"
@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    showArrow: Boolean = true,
    containerColor: Color = AIICTheme.colors.primary,
    contentColor: Color = AIICTheme.colors.textOnPrimary,
    content: @Composable (() -> Unit)? = null,
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            AIICTheme.colors.gradientPrimaryStart,
            AIICTheme.colors.gradientPrimaryEnd,
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
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
                else -> Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = text,
                        style = AIICTheme.typography.button,
                        color = contentColor,
                        fontWeight = FontWeight.Medium,
                    )
                    if (showArrow) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

// ── PremiumCard ──────────────────────────────────
// Dark card with subtle blue-tinted border, 16dp radius
// Reference: All cards across screens
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AIICTheme.colors.surface)
            .border(
                width = 1.dp,
                color = AIICTheme.colors.border,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(AIICTheme.spacing.cardPadding),
    ) {
        content()
    }
}

// ── GlowCard ──────────────────────────────────
// Card with neon blue glow border effect
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = AIICTheme.colors.glowBlue,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                // Outer glow
                drawRoundRect(
                    color = glowColor.copy(alpha = 0.15f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                )
            }
            .background(AIICTheme.colors.surface)
            .border(
                width = 1.dp,
                color = glowColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(AIICTheme.spacing.cardPadding),
    ) {
        content()
    }
}

// ── SectionHeader ──────────────────────────────────
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
                fontWeight = FontWeight.SemiBold,
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
                color = AIICTheme.colors.secondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(AIICTheme.shapes.small)
                    .clickable(onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

// ── LoadingShimmer ──────────────────────────────────
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
            .clip(RoundedCornerShape(16.dp))
            .background(shimmerBrush),
    )
}

// ── ShimmerList ──────────────────────────────────
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

// ── EmptyStateView ──────────────────────────────────
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

// ── ErrorStateView ──────────────────────────────────
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
            showArrow = false,
        )
    }
}

// ── AppLogo ──────────────────────────────────
// Blue gradient circle with "AI" text — matches logo system
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

// ── GradientText ──────────────────────────────────
@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = AIICTheme.typography.displayMedium,
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

// ── ScoreCard ──────────────────────────────────
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

// ── FeatureCard ──────────────────────────────────
@Composable
fun FeatureCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumCard(modifier = modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
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

// ── GoogleSignInButton ──────────────────────────────────
// Outlined button with Google icon — matches design reference
@Composable
fun GoogleSignInButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = AIICTheme.colors.textPrimary,
        ),
        border = BorderStroke(1.dp, AIICTheme.colors.border),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AIICTheme.colors.textPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = com.aiic.app.R.drawable.ic_google),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = AIICTheme.typography.button,
                color = AIICTheme.colors.textPrimary,
            )
        }
    }
}

// ── StatItem ──────────────────────────────────
// Individual stat display (Day Streak, Practice Time, etc.)
@Composable
fun StatItem(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon()
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = AIICTheme.typography.labelSmall,
            color = AIICTheme.colors.textTertiary,
        )
    }
}


@androidx.compose.runtime.Composable
fun EarthGlowBackground(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier.androidx.compose.foundation.layout.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .androidx.compose.foundation.layout.fillMaxWidth(1.5f)
                .androidx.compose.foundation.layout.height(300.dp)
                .androidx.compose.foundation.layout.offset(y = 150.dp)
                .androidx.compose.foundation.background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(com.aiic.app.core.theme.AIICTheme.colors.primary.copy(alpha = 0.25f), androidx.compose.ui.graphics.Color.Transparent)
                    )
                )
        )
    }
}
