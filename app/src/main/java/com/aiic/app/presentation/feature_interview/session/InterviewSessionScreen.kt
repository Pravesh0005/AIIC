package com.aiic.app.presentation.feature_interview.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSessionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSummary: (String) -> Unit,
    onNavigateToFeedback: (String) -> Unit,
    viewModel: InterviewSessionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is com.aiic.app.core.base.UiEvent.NavigateBack -> onNavigateBack()
                is com.aiic.app.core.base.UiEvent.Navigate -> {
                    if (event.route.startsWith("interview_summary")) {
                        onNavigateToSummary(event.route)
                    } else if (event.route.startsWith("answer_feedback")) {
                        onNavigateToFeedback(event.route)
                    }
                }
                else -> {}
            }
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(AIICTheme.colors.background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AIICTheme.colors.primary)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Question ${state.questionNumber} of ${state.totalQuestions}", style = AIICTheme.typography.titleMedium, color = AIICTheme.colors.textPrimary)
                        // Simple progress bar
                        LinearProgressIndicator(
                            progress = state.questionNumber / state.totalQuestions.toFloat(),
                            color = AIICTheme.colors.primary,
                            trackColor = AIICTheme.colors.borderSubtle,
                            modifier = Modifier.fillMaxWidth(0.5f).padding(top = 4.dp).clip(CircleShape)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(InterviewSessionAction.QuitSession) }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Quit", tint = AIICTheme.colors.textSecondary)
                    }
                },
                actions = {
                    // Timer pill
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AIICTheme.colors.surfaceElevated)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        val minutes = state.timeRemainingSeconds / 60
                        val seconds = state.timeRemainingSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            style = AIICTheme.typography.labelMedium,
                            color = AIICTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AIICTheme.colors.background)
            )
        },
        containerColor = AIICTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
        ) {
            
            Spacer(modifier = Modifier.height(16.dp))

            // Question Card
            PremiumCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (state.currentQuestion?.isFollowUp == true) 2.dp else 1.dp,
                        color = if (state.currentQuestion?.isFollowUp == true) AIICTheme.colors.accent else AIICTheme.colors.borderSubtle,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (state.currentQuestion?.isFollowUp == true) {
                        Text(
                            text = "FOLLOW UP",
                            style = AIICTheme.typography.labelSmall,
                            color = AIICTheme.colors.accent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        text = state.currentQuestion?.content ?: "Preparing question...",
                        style = AIICTheme.typography.headlineMedium,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Answer Input Area
            Text(
                text = "Your Answer",
                style = AIICTheme.typography.titleMedium,
                color = AIICTheme.colors.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.currentAnswerInput,
                onValueChange = { viewModel.onAction(InterviewSessionAction.UpdateAnswerInput(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = AIICTheme.typography.bodyLarge,
                placeholder = { Text("Type your answer here...", color = AIICTheme.colors.textTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AIICTheme.colors.primary,
                    unfocusedBorderColor = AIICTheme.colors.borderSubtle,
                    focusedTextColor = AIICTheme.colors.textPrimary,
                    unfocusedTextColor = AIICTheme.colors.textPrimary,
                    cursorColor = AIICTheme.colors.primary
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !state.isEvaluating
            )

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = AIICTheme.colors.error,
                    style = AIICTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Future: Voice Answer Button
                IconButton(
                    onClick = { /* TODO Day 6 Voice */ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AIICTheme.colors.surfaceElevated)
                ) {
                    Icon(Icons.Rounded.Mic, contentDescription = "Voice", tint = AIICTheme.colors.textSecondary)
                }

                Button(
                    onClick = { viewModel.onAction(InterviewSessionAction.SubmitAnswer) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AIICTheme.colors.primary,
                        disabledContainerColor = AIICTheme.colors.primary.copy(alpha = 0.5f)
                    ),
                    enabled = !state.isEvaluating && state.currentAnswerInput.isNotBlank()
                ) {
                    AnimatedVisibility(visible = state.isEvaluating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp).padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Text(
                        text = if (state.isEvaluating) "Evaluating..." else "Submit Answer",
                        style = AIICTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (!state.isEvaluating) {
                        Icon(Icons.Rounded.Send, contentDescription = null, modifier = Modifier.padding(start = 8.dp).size(20.dp))
                    }
                }
            }
        }
    }
}
