package com.aiic.app.presentation.feature_resume.analysis

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme

@Composable
fun ResumeAnalysisScreen(
    resumeId: String?,
    onNavigateBack: () -> Unit,
    onNavigateToATS: (String) -> Unit,
    onNavigateToSkills: (String) -> Unit,
    onNavigateToRecommendations: (String) -> Unit,
    viewModel: ResumeAnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(resumeId) {
        viewModel.loadAnalysis(resumeId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
        ) {
            AnimatedContent(targetState = uiState, label = "analysis_state") { state ->
                when (state) {
                    is AnalysisUiState.Idle, is AnalysisUiState.Analyzing, is AnalysisUiState.Retrying -> {
                        AnalyzingState()
                    }
                    is AnalysisUiState.NoResume -> {
                        ErrorState("No resume provided for analysis.", onNavigateBack)
                    }
                    is AnalysisUiState.NoAnalysis -> {
                        ErrorState("No analysis generated.", onNavigateBack)
                    }
                    is AnalysisUiState.Failed -> {
                        ErrorState(state.message) {
                            if (resumeId != null) viewModel.forceReanalyze(resumeId) else onNavigateBack()
                        }
                    }
                    is AnalysisUiState.Success -> {
                        AnalysisDashboardContent(
                            analysis = state.analysis,
                            onNavigateToATS = { onNavigateToATS(state.analysis.analysisId) },
                            onNavigateToSkills = { onNavigateToSkills(state.analysis.analysisId) },
                            onNavigateToRecommendations = { onNavigateToRecommendations(state.analysis.analysisId) },
                            onNavigateBack = onNavigateBack
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyzingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AIICTheme.colors.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Psychology,
                contentDescription = null,
                tint = AIICTheme.colors.primary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator(color = AIICTheme.colors.primary)
        Spacer(Modifier.height(24.dp))
        Text(
            text = "AI Intelligence Engine is analyzing your resume...",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Extracting skills, generating ATS score, and building recommendations.",
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Analysis Failed",
            style = AIICTheme.typography.headlineMedium,
            color = AIICTheme.colors.error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        PremiumButton(text = "Retry", onClick = onRetry)
    }
}
