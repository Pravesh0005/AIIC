package com.aiic.app.presentation.feature_interview.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PrimaryButton
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.InterviewDifficulty
import com.aiic.app.domain.model.InterviewType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSetupScreen(
    onNavigateBack: () -> Unit,
    viewModel: InterviewSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mock Interview Setup", color = AIICTheme.colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
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
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Configure Your Session",
                style = AIICTheme.typography.headlineMedium,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(24.dp))
            
            // Role Selection
            Text("Target Role", style = AIICTheme.typography.titleMedium, color = AIICTheme.colors.textSecondary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.selectedRole,
                onValueChange = { viewModel.onAction(InterviewSetupAction.UpdateRole(it)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AIICTheme.colors.primary,
                    unfocusedBorderColor = AIICTheme.colors.borderSubtle,
                    focusedTextColor = AIICTheme.colors.textPrimary,
                    unfocusedTextColor = AIICTheme.colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(Modifier.height(24.dp))
            
            // Interview Type
            Text("Interview Type", style = AIICTheme.typography.titleMedium, color = AIICTheme.colors.textSecondary)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InterviewType.values().forEach { type ->
                    ChoiceChip(
                        text = type.name,
                        selected = state.selectedType == type,
                        onClick = { viewModel.onAction(InterviewSetupAction.UpdateType(type)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Difficulty
            Text("Difficulty", style = AIICTheme.typography.titleMedium, color = AIICTheme.colors.textSecondary)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InterviewDifficulty.values().forEach { diff ->
                    ChoiceChip(
                        text = diff.name,
                        selected = state.selectedDifficulty == diff,
                        onClick = { viewModel.onAction(InterviewSetupAction.UpdateDifficulty(diff)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Question Count
            Text("Questions Count: ${state.selectedQuestionCount}", style = AIICTheme.typography.titleMedium, color = AIICTheme.colors.textSecondary)
            Slider(
                value = state.selectedQuestionCount.toFloat(),
                onValueChange = { viewModel.onAction(InterviewSetupAction.UpdateQuestionCount(it.toInt())) },
                valueRange = 3f..15f,
                steps = 11,
                colors = SliderDefaults.colors(
                    thumbColor = AIICTheme.colors.primary,
                    activeTrackColor = AIICTheme.colors.primary
                )
            )
            
            Spacer(Modifier.height(40.dp))
            
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = AIICTheme.colors.error,
                    style = AIICTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            PrimaryButton(
                text = if (state.isLoading) "Preparing AI..." else "Start Interview",
                onClick = { viewModel.onAction(InterviewSetupAction.StartInterview) },
                icon = if (!state.isLoading) Icons.Rounded.PlayArrow else null,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ChoiceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) AIICTheme.colors.primary.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (selected) AIICTheme.colors.primary else AIICTheme.colors.textSecondary
    val borderColor = if (selected) AIICTheme.colors.primary else AIICTheme.colors.borderSubtle

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            style = AIICTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
