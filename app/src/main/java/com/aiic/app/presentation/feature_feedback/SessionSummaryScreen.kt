package com.aiic.app.presentation.feature_feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.presentation.feature_feedback.components.FeedbackCard
import com.aiic.app.presentation.feature_feedback.components.ScoreRing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSummaryScreen(
    sessionId: String,
    onNavigateHome: () -> Unit,
    onNavigateToReport: (String) -> Unit = {},
    viewModel: SessionSummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadSummary(sessionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Interview Summary",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onNavigateHome) {
                Icon(Icons.Rounded.Home, contentDescription = "Home", tint = AIICTheme.colors.textPrimary)
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val state = uiState) {
                is SessionSummaryUiState.Idle, is SessionSummaryUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = AIICTheme.colors.primary)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Generating your performance report...",
                            style = AIICTheme.typography.bodyMedium,
                            color = AIICTheme.colors.textSecondary
                        )
                    }
                }
                is SessionSummaryUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AIICTheme.colors.warning.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Refresh,
                                contentDescription = null,
                                tint = AIICTheme.colors.warning,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Summary Generation Failed",
                            style = AIICTheme.typography.titleMedium,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Your answers are saved. The AI summary couldn't be generated right now.",
                            style = AIICTheme.typography.bodyMedium,
                            color = AIICTheme.colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        PremiumButton(
                            text = "Try Again",
                            onClick = { viewModel.loadSummary(sessionId) },
                            modifier = Modifier.width(200.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = onNavigateHome) {
                            Text("Return to Dashboard", color = AIICTheme.colors.textSecondary)
                        }
                    }
                }
                is SessionSummaryUiState.Success -> {
                    val summary = state.summary
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = AIICTheme.spacing.screenHorizontal),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AIICTheme.colors.success.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = AIICTheme.colors.success,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Interview Complete!",
                            style = AIICTheme.typography.headlineMedium,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(24.dp))

                        ScoreRing(
                            score = summary.averageScore,
                            label = "Performance Score",
                            modifier = Modifier.size(150.dp),
                            color = when {
                                summary.averageScore >= 80 -> AIICTheme.colors.success
                                summary.averageScore >= 50 -> AIICTheme.colors.warning
                                else -> AIICTheme.colors.error
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        PremiumCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Role Readiness",
                                    style = AIICTheme.typography.labelMedium,
                                    color = AIICTheme.colors.textSecondary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = summary.roleReadiness,
                                    style = AIICTheme.typography.titleLarge,
                                    color = AIICTheme.colors.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        if (summary.strongAreas.isNotEmpty()) {
                            FeedbackCard(
                                title = "Strong Areas",
                                items = summary.strongAreas,
                                cardColor = AIICTheme.colors.success
                            )
                            Spacer(Modifier.height(12.dp))
                        }

                        if (summary.weakAreas.isNotEmpty()) {
                            FeedbackCard(
                                title = "Areas to Improve",
                                items = summary.weakAreas,
                                cardColor = AIICTheme.colors.error
                            )
                            Spacer(Modifier.height(12.dp))
                        }

                        if (summary.priorityImprovements.isNotEmpty()) {
                            FeedbackCard(
                                title = "Priority Actions",
                                items = summary.priorityImprovements,
                                cardColor = AIICTheme.colors.primary
                            )
                        }

                        Spacer(Modifier.height(32.dp))

                        PremiumButton(
                            text = "View Full Report",
                            onClick = { onNavigateToReport(sessionId) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavigateHome,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AIICTheme.colors.textSecondary
                            )
                        ) {
                            Icon(Icons.Rounded.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Return to Dashboard")
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
