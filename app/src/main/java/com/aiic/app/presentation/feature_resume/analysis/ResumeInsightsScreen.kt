package com.aiic.app.presentation.feature_resume.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.ResumeAnalysis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResumeInsightsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAnalysis: (String) -> Unit, // pass resumeId
    viewModel: ResumeInsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Analysis History",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is InsightsUiState.Loading -> {
                    CircularProgressIndicator(
                        color = AIICTheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is InsightsUiState.Error -> {
                    Text(
                        text = state.message,
                        color = AIICTheme.colors.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is InsightsUiState.Success -> {
                    if (state.history.isEmpty()) {
                        Text(
                            text = "No analysis history found.",
                            color = AIICTheme.colors.textSecondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                start = AIICTheme.spacing.screenHorizontal,
                                end = AIICTheme.spacing.screenHorizontal,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.history) { analysis ->
                                AnalysisHistoryCard(
                                    analysis = analysis,
                                    onClick = { onNavigateToAnalysis(analysis.resumeId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisHistoryCard(analysis: ResumeAnalysis, onClick: () -> Unit) {
    PremiumCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(AIICTheme.shapes.small)
                    .background(AIICTheme.colors.surfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${analysis.overallScore}",
                    style = AIICTheme.typography.titleMedium,
                    color = AIICTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = analysis.classification,
                    style = AIICTheme.typography.titleMedium,
                    color = AIICTheme.colors.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Analyzed on ${formatDate(analysis.timestamp)}",
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textSecondary
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown date"
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(Date(timestamp))
}
