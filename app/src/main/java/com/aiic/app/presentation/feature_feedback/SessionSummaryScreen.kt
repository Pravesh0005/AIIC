package com.aiic.app.presentation.feature_feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.presentation.feature_feedback.components.FeedbackCard
import com.aiic.app.presentation.feature_feedback.components.ScoreRing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSummaryScreen(
    sessionId: String,
    onNavigateHome: () -> Unit,
    viewModel: SessionSummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadSummary(sessionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interview Summary") },
                actions = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is SessionSummaryUiState.Idle, is SessionSummaryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SessionSummaryUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is SessionSummaryUiState.Success -> {
                    val summary = state.summary
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Overall Performance",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        ScoreRing(
                            score = summary.averageScore,
                            label = "Average Score",
                            modifier = Modifier.size(150.dp),
                            color = if (summary.averageScore >= 70) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Role Readiness: ${summary.roleReadiness}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (summary.strongAreas.isNotEmpty()) {
                            FeedbackCard(title = "Strong Areas", items = summary.strongAreas, cardColor = Color(0xFF4CAF50))
                        }
                        
                        if (summary.weakAreas.isNotEmpty()) {
                            FeedbackCard(title = "Areas to Improve", items = summary.weakAreas, cardColor = Color(0xFFF44336))
                        }
                        
                        if (summary.priorityImprovements.isNotEmpty()) {
                            FeedbackCard(title = "Priority Actions", items = summary.priorityImprovements, cardColor = Color(0xFF2196F3))
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = onNavigateHome,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Back to Dashboard")
                        }
                    }
                }
            }
        }
    }
}
