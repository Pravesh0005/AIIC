package com.aiic.app.presentation.feature_resume.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.presentation.feature_resume.analysis.components.RecommendationCard

@Composable
fun RecommendationsScreen(
    resumeId: String?,
    onNavigateBack: () -> Unit,
    viewModel: ResumeAnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(resumeId) {
        viewModel.loadAnalysis(resumeId)
    }

    when (val state = uiState) {
        is AnalysisUiState.Success -> {
            RecommendationsContent(analysis = state.analysis, onNavigateBack = onNavigateBack)
        }
        is AnalysisUiState.Failed -> {
            Box(Modifier.fillMaxSize().background(AIICTheme.colors.background), contentAlignment = Alignment.Center) {
                Text("Failed to load analysis", color = AIICTheme.colors.error)
            }
        }
        else -> {
            Box(Modifier.fillMaxSize().background(AIICTheme.colors.background), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AIICTheme.colors.primary)
            }
        }
    }
}

@Composable
private fun RecommendationsContent(
    analysis: ResumeAnalysis,
    onNavigateBack: () -> Unit
) {
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
            }
            Text(
                text = "Action Plan",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = AIICTheme.spacing.screenHorizontal,
                end = AIICTheme.spacing.screenHorizontal,
                top = 0.dp,
                bottom = 24.dp
            )
        ) {
            item {
                Text(
                    text = "AI Coach Recommendations",
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            
            items(analysis.recommendations) { rec ->
                RecommendationCard(recommendation = rec)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
