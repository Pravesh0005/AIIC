package com.aiic.app.presentation.feature_interview.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme

@Composable
fun InterviewSummaryScreen(
    onNavigateHome: () -> Unit,
    viewModel: InterviewSummaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is com.aiic.app.core.base.UiEvent.Navigate -> {
                    if (event.route == "home") onNavigateHome()
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = AIICTheme.spacing.screenHorizontal)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AIICTheme.colors.success.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = AIICTheme.colors.success,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Interview Completed!",
            style = AIICTheme.typography.headlineLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your answers have been saved and analyzed. Keep practicing to improve your readiness score.",
            style = AIICTheme.typography.bodyLarge,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        PremiumCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Session Overview",
                    style = AIICTheme.typography.titleLarge,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                Divider(color = AIICTheme.colors.borderSubtle)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Role", style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textSecondary)
                    Text(state.session?.role ?: "Unknown", style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Questions Answered", style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textSecondary)
                    Text("${state.session?.questionCount ?: 0}", style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.SemiBold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Session Score", style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.textSecondary)
                    val scoreStr = state.session?.score?.let { "${it.toInt()}%" } ?: "0%"
                    Text(scoreStr, style = AIICTheme.typography.bodyMedium, color = AIICTheme.colors.success, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Return to Dashboard",
            onClick = { viewModel.onAction(InterviewSummaryAction.FinishReview) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}
