package com.aiic.app.presentation.feature_feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.presentation.feature_feedback.components.FeedbackCard
import com.aiic.app.presentation.feature_feedback.components.InterviewerNoteCard
import com.aiic.app.presentation.feature_feedback.components.ScoreRing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerFeedbackScreen(
    answerId: String,
    onNavigateBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(answerId) {
        viewModel.loadFeedbackForAnswer(answerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Answer Feedback") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is FeedbackUiState.Idle, is FeedbackUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FeedbackUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is FeedbackUiState.Success -> {
                    val feedback = state.feedback
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text("Question", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Question ID: ${feedback.questionId}", style = MaterialTheme.typography.bodyMedium) // Should fetch question text in real app
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Your Answer", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text(text = feedback.answerText, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScoreRing(score = feedback.overallScore, label = "Overall", color = Color(0xFF4CAF50))
                            ScoreRing(score = feedback.technicalScore, label = "Technical", color = Color(0xFF2196F3))
                            ScoreRing(score = feedback.communicationScore, label = "Comms", color = Color(0xFFFF9800))
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        InterviewerNoteCard(note = feedback.interviewerPerspective)

                        Spacer(modifier = Modifier.height(16.dp))

                        if (feedback.strengths.isNotEmpty()) {
                            FeedbackCard(title = "Strengths", items = feedback.strengths, cardColor = Color(0xFF4CAF50))
                        }
                        
                        if (feedback.weaknesses.isNotEmpty()) {
                            FeedbackCard(title = "Weaknesses", items = feedback.weaknesses, cardColor = Color(0xFFF44336))
                        }
                        
                        if (feedback.improvementSuggestions.isNotEmpty()) {
                            FeedbackCard(title = "How to Improve", items = feedback.improvementSuggestions, cardColor = Color(0xFF2196F3))
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Next Question")
                        }
                    }
                }
            }
        }
    }
}
