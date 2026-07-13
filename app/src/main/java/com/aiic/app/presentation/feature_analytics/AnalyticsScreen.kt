package com.aiic.app.presentation.feature_analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme

@Composable
fun AnalyticsScreen(
    interviewsCompleted: Int = 0,
    readinessScore: Float = 0f,
    hoursOfPractice: Float = 0f,
    streakDays: Int = 0
) {
    val scrollState = rememberScrollState()

    val avgScore = if (readinessScore > 0) (readinessScore * 100).toInt() else 0
    val totalMinutes = (hoursOfPractice * 60).toInt()
    val avgTimePerInterview = if (interviewsCompleted > 0) totalMinutes / interviewsCompleted else 0
    val practiceDisplay = formatPracticeTime(hoursOfPractice)
    val practiceTrend = when {
        totalMinutes == 0 -> "No practice yet"
        totalMinutes < 60 -> "${totalMinutes} min total"
        totalMinutes % 60 == 0 -> "${totalMinutes / 60}h total"
        else -> "${totalMinutes / 60}h ${totalMinutes % 60}m total"
    }

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

        Text(
            text = "Performance Overview",
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
                value = "$interviewsCompleted",
                trend = if (interviewsCompleted > 0) "+$interviewsCompleted" else "0",
                isPositive = interviewsCompleted > 0,
                icon = Icons.Rounded.WorkOutline
            )
            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "Readiness",
                value = "$avgScore%",
                trend = if (avgScore >= 50) "Good" else "Building",
                isPositive = avgScore >= 50,
                icon = Icons.Rounded.TrendingUp
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "Practice Time",
                value = practiceDisplay,
                trend = practiceTrend,
                isPositive = totalMinutes > 0,
                icon = Icons.Rounded.Schedule
            )
            AnalyticsStatCard(
                modifier = Modifier.weight(1f),
                title = "Streak",
                value = "$streakDays",
                trend = if (streakDays > 0) "Active" else "Start today",
                isPositive = streakDays > 0,
                icon = Icons.Rounded.LocalFireDepartment
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Session Breakdown",
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
                SessionMetricRow(
                    label = "Total Interviews",
                    value = "$interviewsCompleted sessions"
                )
                SessionMetricRow(
                    label = "Total Practice Time",
                    value = if (totalMinutes > 60) "${hoursOfPractice.toInt()}h ${totalMinutes % 60}m" else "${totalMinutes}m"
                )
                SessionMetricRow(
                    label = "Avg. Per Interview",
                    value = if (avgTimePerInterview > 0) "${avgTimePerInterview}m" else "—"
                )
                SessionMetricRow(
                    label = "Current Streak",
                    value = if (streakDays > 0) "$streakDays day${if (streakDays > 1) "s" else ""}" else "No active streak"
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "AI Insights",
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
                        text = "Recommendation",
                        style = AIICTheme.typography.titleSmall,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = when {
                        interviewsCompleted == 0 -> "Start your first mock interview to build your analytics profile. The AI will analyze your performance and provide personalized recommendations."
                        avgScore < 50 -> "Focus on practicing more technical questions. Your readiness score suggests room for improvement in core concepts. Try increasing session frequency."
                        avgScore < 75 -> "Good progress! Consider challenging yourself with Hard difficulty interviews. Practice system design questions to improve your overall score."
                        else -> "Excellent performance! You're interview-ready. Maintain your streak and try different roles to broaden your preparation."
                    },
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textSecondary,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        if (interviewsCompleted > 0) {
            Text(
                text = "Readiness Breakdown",
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
                    SkillProgressRow(skill = "Technical Skills", score = avgScore.coerceIn(0, 100))
                    SkillProgressRow(skill = "Communication", score = (avgScore * 0.9f).toInt().coerceIn(0, 100))
                    SkillProgressRow(skill = "Problem Solving", score = (avgScore * 1.05f).toInt().coerceIn(0, 100))
                    SkillProgressRow(skill = "Confidence", score = (avgScore * 0.85f + streakDays * 2).toInt().coerceIn(0, 100))
                }
            }
        }
    }
}

@Composable
private fun SessionMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
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
                
                Text(
                    text = trend,
                    style = AIICTheme.typography.labelSmall,
                    color = if (isPositive) AIICTheme.colors.success else AIICTheme.colors.textTertiary,
                    fontWeight = FontWeight.Bold
                )
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

private fun formatPracticeTime(hours: Float): String {
    if (hours <= 0f) return "0 min"
    val totalMinutes = (hours * 60).toInt()
    return when {
        totalMinutes < 60 -> "${totalMinutes}m"
        totalMinutes % 60 == 0 -> "${totalMinutes / 60}h"
        else -> "${totalMinutes / 60}h ${totalMinutes % 60}m"
    }
}
