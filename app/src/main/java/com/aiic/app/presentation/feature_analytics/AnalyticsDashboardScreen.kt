package com.aiic.app.presentation.feature_analytics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.presentation.feature_interview.components.AnimatedScoreRing
import com.aiic.app.presentation.feature_interview.components.ScoreBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReport: (String) -> Unit = {},
    viewModel: AnalyticsDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Analytics",
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = AIICTheme.colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AIICTheme.colors.background
                )
            )
        },
        containerColor = AIICTheme.colors.background
    ) { padding ->

        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AIICTheme.colors.primary)
            }
            return@Scaffold
        }

        if (state.sessions.isEmpty()) {
            EmptyAnalyticsState(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(8.dp))

            PerformanceHeaderCard(state)

            Spacer(Modifier.height(20.dp))

            Text(
                "Score Progression",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            PremiumCard {
                ScoreTrendChart(
                    scores = state.scoreHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(16.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Performance Breakdown",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricTile(
                    label = "Avg Score",
                    value = "${state.averageScore.toInt()}",
                    unit = "/100",
                    icon = Icons.Rounded.Star,
                    color = Color(0xFF6C5CE7),
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Sessions",
                    value = "${state.totalSessions}",
                    unit = "total",
                    icon = Icons.Rounded.VideoChat,
                    color = Color(0xFF00B894),
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Best Score",
                    value = "${state.bestScore.toInt()}",
                    unit = "/100",
                    icon = Icons.Rounded.EmojiEvents,
                    color = Color(0xFFFDAA5E),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricTile(
                    label = "Improvement",
                    value = if (state.improvementRate >= 0) "+${state.improvementRate.toInt()}" else "${state.improvementRate.toInt()}",
                    unit = "pts",
                    icon = Icons.Rounded.TrendingUp,
                    color = if (state.improvementRate >= 0) Color(0xFF00B894) else Color(0xFFFF6B6B),
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Streak",
                    value = "${state.practiceStreak}",
                    unit = "days",
                    icon = Icons.Rounded.Whatshot,
                    color = Color(0xFFE17055),
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Hrs Practiced",
                    value = "${state.totalHoursPracticed}",
                    unit = "hrs",
                    icon = Icons.Rounded.Timer,
                    color = Color(0xFF0984E3),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Skill Dimensions",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            PremiumCard {
                Column(Modifier.padding(16.dp)) {
                    ScoreBar("Technical Accuracy", state.avgTechnical, color = Color(0xFF6C5CE7))
                    ScoreBar("Communication", state.avgCommunication, color = Color(0xFF00B894))
                    ScoreBar("Confidence", state.avgConfidence, color = Color(0xFFFDAA5E))
                    ScoreBar("Problem Solving", state.avgProblemSolving, color = Color(0xFF0984E3))
                    ScoreBar("Structure", state.avgStructure, color = Color(0xFFE17055))
                    ScoreBar("Professionalism", state.avgProfessionalism, color = Color(0xFFFF6B6B))
                }
            }

            Spacer(Modifier.height(20.dp))

            if (state.typeBreakdown.isNotEmpty()) {
                Text(
                    "By Interview Type",
                    style = AIICTheme.typography.titleLarge,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                PremiumCard {
                    Column(Modifier.padding(16.dp)) {
                        state.typeBreakdown.forEach { (type, avg) ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    type,
                                    style = AIICTheme.typography.bodyMedium,
                                    color = AIICTheme.colors.textSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val color = when {
                                        avg >= 80 -> Color(0xFF00B894)
                                        avg >= 60 -> Color(0xFFFDAA5E)
                                        else -> Color(0xFFFF6B6B)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Text(
                                        "${avg.toInt()}/100",
                                        style = AIICTheme.typography.labelMedium,
                                        color = color,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = AIICTheme.colors.borderSubtle.copy(alpha = 0.5f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (state.sessions.isNotEmpty()) {
                Text(
                    "Recent Sessions",
                    style = AIICTheme.typography.titleLarge,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                state.sessions.take(5).forEach { session ->
                    RecentSessionRow(
                        role = session.role,
                        score = session.score ?: 0f,
                        type = session.interviewType.name,
                        onViewReport = { onNavigateToReport(session.sessionId) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PerformanceHeaderCard(state: AnalyticsDashboardState) {
    PremiumCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            AIICTheme.colors.primary.copy(alpha = 0.08f),
                            AIICTheme.colors.accent.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Overall Performance",
                        style = AIICTheme.typography.labelMedium,
                        color = AIICTheme.colors.textTertiary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        state.performanceLabel,
                        style = AIICTheme.typography.headlineMedium,
                        color = AIICTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Based on ${state.totalSessions} interviews",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textSecondary
                    )
                }
                AnimatedScoreRing(
                    score = state.averageScore,
                    label = "Avg",
                    size = 100,
                    strokeWidth = 10f
                )
            }
        }
    }
}

@Composable
private fun ScoreTrendChart(
    scores: List<Float>,
    modifier: Modifier = Modifier
) {
    if (scores.size < 2) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(
                "Complete more interviews to see trend",
                color = AIICTheme.colors.textTertiary,
                style = AIICTheme.typography.bodySmall
            )
        }
        return
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(scores) {
        animProgress.animateTo(1f, animationSpec = tween(1500, easing = FastOutSlowInEasing))
    }

    val primaryColor = AIICTheme.colors.primary
    val accentColor = AIICTheme.colors.accent

    Canvas(modifier = modifier) {
        val maxScore = scores.max().coerceAtLeast(1f)
        val minScore = scores.min()
        val range = (maxScore - minScore).coerceAtLeast(1f)

        val stepX = size.width / (scores.size - 1).toFloat()
        val points = scores.mapIndexed { i, score ->
            val x = i * stepX
            val y = size.height - ((score - minScore) / range) * size.height * 0.85f - size.height * 0.075f
            Offset(x, y)
        }

        val visibleCount = (points.size * animProgress.value).toInt().coerceAtLeast(1)
        val visiblePoints = points.take(visibleCount)

        val fillPath = Path().apply {
            moveTo(visiblePoints.first().x, size.height)
            visiblePoints.forEach { lineTo(it.x, it.y) }
            lineTo(visiblePoints.last().x, size.height)
            close()
        }
        drawPath(
            fillPath,
            brush = Brush.verticalGradient(
                listOf(accentColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        val linePath = Path().apply {
            visiblePoints.forEachIndexed { i, pt ->
                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
            }
        }
        drawPath(linePath, color = primaryColor, style = Stroke(width = 3f, cap = StrokeCap.Round))

        visiblePoints.forEachIndexed { i, pt ->
            drawCircle(primaryColor, radius = 5f, center = pt)
            drawCircle(Color.White, radius = 2.5f, center = pt)
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    PremiumCard(modifier = modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = AIICTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
                Text(unit, style = AIICTheme.typography.bodySmall, color = AIICTheme.colors.textTertiary, modifier = Modifier.padding(bottom = 2.dp))
            }
            Text(label, style = AIICTheme.typography.labelSmall, color = AIICTheme.colors.textSecondary, maxLines = 1)
        }
    }
}

@Composable
private fun RecentSessionRow(
    role: String,
    score: Float,
    type: String,
    onViewReport: () -> Unit
) {
    val scoreColor = when {
        score >= 80 -> Color(0xFF00B894)
        score >= 60 -> Color(0xFFFDAA5E)
        else -> Color(0xFFFF6B6B)
    }

    PremiumCard {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(role, style = AIICTheme.typography.titleSmall, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textTertiary
                )
            }
            Text(
                "${score.toInt()}",
                style = AIICTheme.typography.titleLarge,
                color = scoreColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(12.dp))
            IconButton(onClick = onViewReport, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "View Report",
                    tint = AIICTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun EmptyAnalyticsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AIICTheme.colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.BarChart,
                    contentDescription = null,
                    tint = AIICTheme.colors.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "No Interview Data Yet",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Complete your first interview to unlock\npersonalized analytics and insights.",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
