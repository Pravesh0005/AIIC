package com.aiic.app.presentation.feature_analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme

@Composable
fun AnalyticsScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = AIICTheme.spacing.screenHorizontal)
            .padding(bottom = 80.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Analytics & Insights",
            style = AIICTheme.typography.headlineLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Track your interview readiness and performance.",
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary,
        )

        Spacer(Modifier.height(24.dp))

        // Analyze with AI Golden Button
        GoldenAIButton(
            text = "Analyze Weaknesses with AI",
            icon = Icons.Rounded.AutoAwesome,
            onClick = { /* TODO */ }
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Weekly Overview",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "Interviews",
                value = "12",
                trend = "+3",
                isPositive = true,
                icon = Icons.Rounded.WorkOutline
            )
            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "Avg. Score",
                value = "85%",
                trend = "+5%",
                isPositive = true,
                icon = Icons.Rounded.TrendingUp
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Skill Analysis",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        PremiumCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SkillProgressRow(skill = "Data Structures", score = 92)
                SkillProgressRow(skill = "System Design", score = 78)
                SkillProgressRow(skill = "Behavioral", score = 88)
                SkillProgressRow(skill = "Problem Solving", score = 85)
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "AI Feedback Summary",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        PremiumCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = null,
                        tint = AIICTheme.colors.warning,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Key Insight",
                        style = AIICTheme.typography.titleSmall,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "You are performing exceptionally well in technical algorithms, but your System Design explanations lack real-world examples. AI suggests practicing scalability scenarios.",
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textSecondary,
                    lineHeight = 22.dp
                )
            }
        }
    }
}

@Composable
fun GoldenAIButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFBBF24), // Golden Start
                        Color(0xFFF59E0B), // Golden End
                        Color(0xFFD4AF37)
                    )
                )
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                style = AIICTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AnalyticsStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    trend: String,
    isPositive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    PremiumCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AIICTheme.colors.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AIICTheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPositive) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                        contentDescription = null,
                        tint = if (isPositive) AIICTheme.colors.success else AIICTheme.colors.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = trend,
                        style = AIICTheme.typography.labelSmall,
                        color = if (isPositive) AIICTheme.colors.success else AIICTheme.colors.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = value,
                style = AIICTheme.typography.displayMedium,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = AIICTheme.typography.bodySmall,
                color = AIICTheme.colors.textSecondary
            )
        }
    }
}

@Composable
fun SkillProgressRow(skill: String, score: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = skill,
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$score%",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(AIICTheme.colors.borderSubtle)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(AIICTheme.colors.primary)
            )
        }
    }
}
