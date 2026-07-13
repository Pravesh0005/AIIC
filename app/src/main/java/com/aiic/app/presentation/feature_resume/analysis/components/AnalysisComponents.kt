package com.aiic.app.presentation.feature_resume.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.AtsScoreDetails
import com.aiic.app.domain.model.Recommendation

@Composable
fun ATSScoreCard(score: Int, classification: String, modifier: Modifier = Modifier) {
    val color = when (classification) {
        "Excellent" -> AIICTheme.colors.success
        "Strong" -> AIICTheme.colors.primary
        "Intermediate" -> AIICTheme.colors.warning
        else -> AIICTheme.colors.error
    }

    val trackColor = AIICTheme.colors.surfaceElevated
    val sweepAngle = (score / 100f) * 360f

    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.size(160.dp)
                ) {
                    val strokeWidthPx = 12.dp.toPx()
                    val arcSize = size.width - strokeWidthPx
                    val topLeft = androidx.compose.ui.geometry.Offset(strokeWidthPx / 2f, strokeWidthPx / 2f)
                    val arcSizeObj = androidx.compose.ui.geometry.Size(arcSize, arcSize)

                    // Track
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSizeObj,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidthPx,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )

                    // Progress
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSizeObj,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidthPx,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        style = AIICTheme.typography.displayMedium,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/100",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = classification,
                style = AIICTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Overall ATS Compatibility",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillChip(skill: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(AIICTheme.colors.surfaceElevated)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = skill,
            style = AIICTheme.typography.bodySmall,
            color = AIICTheme.colors.textPrimary
        )
    }
@Composable
fun StrengthCard(strengths: List<String>) {
    InsightListCard("Strengths", com.aiic.app.R.drawable.ic_exact_strength, AIICTheme.colors.success, strengths)
}

@Composable
fun WeaknessCard(weaknesses: List<String>) {
    InsightListCard("Weaknesses", com.aiic.app.R.drawable.ic_exact_weakness, AIICTheme.colors.warning, weaknesses)
}

@Composable
fun KeywordGapCard(missingKeywords: List<String>) {
    InsightListCard("Missing Keywords", com.aiic.app.R.drawable.ic_exact_keyword, AIICTheme.colors.error, missingKeywords)
}

@Composable
private fun InsightListCard(title: String, iconRes: Int, tint: Color, items: List<String>) {
    PremiumCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(androidx.compose.ui.res.painterResource(id = iconRes), contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = AIICTheme.typography.titleMedium,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
            if (items.isEmpty()) {
                Text(
                    text = "None detected.",
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textSecondary
                )
            } else {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(tint)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = item,
                            style = AIICTheme.typography.bodyMedium,
                            color = AIICTheme.colors.textSecondary,
                            lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
    val priorityColor = when (recommendation.priority.uppercase()) {
        "HIGH" -> AIICTheme.colors.error
        "MEDIUM" -> AIICTheme.colors.warning
        else -> AIICTheme.colors.primary
    }
    
    PremiumCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier
            .background(priorityColor.copy(alpha = 0.03f))
            .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AIICTheme.colors.surfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            androidx.compose.ui.res.painterResource(id = com.aiic.app.R.drawable.ic_exact_recommendation),
                            contentDescription = null,
                            tint = priorityColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = recommendation.category,
                        style = AIICTheme.typography.titleSmall,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(priorityColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = recommendation.priority.uppercase(),
                        style = AIICTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
                        color = priorityColor
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = recommendation.suggestion,
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary,
                lineHeight = androidx.compose.ui.unit.TextUnit(22f, androidx.compose.ui.unit.TextUnitType.Sp)
            )
        }
    }
}

@Composable
fun AnalysisSummaryCard(summary: String, recruiterImpression: String) {
    PremiumCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AI Profile Summary",
                style = AIICTheme.typography.titleMedium,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = summary,
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Recruiter Impression",
                style = AIICTheme.typography.titleMedium,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = recruiterImpression,
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary
            )
        }
    }
}
