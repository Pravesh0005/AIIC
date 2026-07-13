package com.aiic.app.presentation.feature_interview.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.InterviewDifficulty
import com.aiic.app.domain.model.InterviewMode
import com.aiic.app.domain.model.InterviewType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSetupScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSession: (String) -> Unit,
    viewModel: InterviewSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is com.aiic.app.core.base.UiEvent.Navigate -> {
                    val prefix = "interview_session/"
                    if (event.route.startsWith(prefix)) {
                        val id = event.route.removePrefix(prefix)
                        onNavigateToSession(id)
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interview Setup", color = AIICTheme.colors.textPrimary, fontWeight = FontWeight.Bold) },
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
            Spacer(Modifier.height(8.dp))

            SectionLabel("Interview Mode")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModeCard(
                    icon = Icons.Rounded.Keyboard,
                    label = "Text",
                    description = "Type answers",
                    selected = state.selectedMode == InterviewMode.TEXT,
                    onClick = { viewModel.onAction(InterviewSetupAction.UpdateMode(InterviewMode.TEXT)) },
                    modifier = Modifier.weight(1f)
                )
                ModeCard(
                    icon = Icons.Rounded.Mic,
                    label = "Voice",
                    description = "Speak answers",
                    selected = state.selectedMode == InterviewMode.VOICE,
                    onClick = { viewModel.onAction(InterviewSetupAction.UpdateMode(InterviewMode.VOICE)) },
                    modifier = Modifier.weight(1f)
                )
                ModeCard(
                    icon = Icons.Rounded.CameraAlt,
                    label = "Video",
                    description = "Full analysis",
                    selected = state.selectedMode == InterviewMode.VIDEO,
                    onClick = { viewModel.onAction(InterviewSetupAction.UpdateMode(InterviewMode.VIDEO)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            SectionLabel("Target Role")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.selectedRole,
                onValueChange = { viewModel.onAction(InterviewSetupAction.UpdateRole(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Type or select a role", color = AIICTheme.colors.textTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AIICTheme.colors.primary,
                    unfocusedBorderColor = AIICTheme.colors.borderSubtle,
                    focusedTextColor = AIICTheme.colors.textPrimary,
                    unfocusedTextColor = AIICTheme.colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val roles = listOf(
                    "Android Developer", "Backend Developer", "Frontend Developer",
                    "Full Stack Developer", "AI/ML Engineer", "DevOps Engineer",
                    "iOS Developer", "Data Scientist", "Product Manager",
                    "Cloud Engineer", "System Architect", "QA Engineer"
                )
                roles.forEach { role ->
                    FilterChip(
                        selected = state.selectedRole == role,
                        onClick = { viewModel.onAction(InterviewSetupAction.UpdateRole(role)) },
                        label = { Text(role, style = AIICTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AIICTheme.colors.primary.copy(alpha = 0.15f),
                            selectedLabelColor = AIICTheme.colors.primary,
                            containerColor = AIICTheme.colors.surfaceElevated,
                            labelColor = AIICTheme.colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = AIICTheme.colors.borderSubtle,
                            selectedBorderColor = AIICTheme.colors.primary,
                            enabled = true,
                            selected = state.selectedRole == role
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionLabel("Interview Type")
            Spacer(Modifier.height(8.dp))
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InterviewType.values().forEach { type ->
                    FilterChip(
                        selected = state.selectedType == type,
                        onClick = { viewModel.onAction(InterviewSetupAction.UpdateType(type)) },
                        label = { Text(formatTypeName(type), style = AIICTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AIICTheme.colors.primary.copy(alpha = 0.15f),
                            selectedLabelColor = AIICTheme.colors.primary,
                            containerColor = AIICTheme.colors.surfaceElevated,
                            labelColor = AIICTheme.colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = AIICTheme.colors.borderSubtle,
                            selectedBorderColor = AIICTheme.colors.primary,
                            enabled = true,
                            selected = state.selectedType == type
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionLabel("Difficulty")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InterviewDifficulty.values().forEach { diff ->
                    val isSelected = state.selectedDifficulty == diff
                    val color = when (diff) {
                        InterviewDifficulty.EASY -> Color(0xFF00B894)
                        InterviewDifficulty.MEDIUM -> Color(0xFFFDAA5E)
                        InterviewDifficulty.HARD -> Color(0xFFFF6B6B)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) color.copy(alpha = 0.15f) else AIICTheme.colors.surfaceElevated)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) color else AIICTheme.colors.borderSubtle,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.onAction(InterviewSetupAction.UpdateDifficulty(diff)) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            diff.name,
                            color = if (isSelected) color else AIICTheme.colors.textSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            style = AIICTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionLabel("Questions: ${state.selectedQuestionCount}")
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

            Spacer(Modifier.height(16.dp))
            SectionLabel("Target Company (Optional)")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.targetCompany,
                onValueChange = { viewModel.onAction(InterviewSetupAction.UpdateCompany(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Google, Microsoft, Amazon", color = AIICTheme.colors.textTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AIICTheme.colors.primary,
                    unfocusedBorderColor = AIICTheme.colors.borderSubtle,
                    focusedTextColor = AIICTheme.colors.textPrimary,
                    unfocusedTextColor = AIICTheme.colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(32.dp))

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = AIICTheme.colors.error,
                    style = AIICTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            PremiumButton(
                text = if (state.isLoading) "Preparing AI..." else "Start Interview",
                onClick = { viewModel.onAction(InterviewSetupAction.StartInterview) },
                enabled = !state.isLoading,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = AIICTheme.typography.titleMedium,
        color = AIICTheme.colors.textSecondary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun ModeCard(
    icon: ImageVector,
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) AIICTheme.colors.primary.copy(alpha = 0.1f) else AIICTheme.colors.surfaceElevated)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) AIICTheme.colors.primary else AIICTheme.colors.borderSubtle,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (selected) AIICTheme.colors.primary else AIICTheme.colors.textSecondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                style = AIICTheme.typography.labelMedium,
                color = if (selected) AIICTheme.colors.primary else AIICTheme.colors.textPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                description,
                style = AIICTheme.typography.labelSmall,
                color = AIICTheme.colors.textTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatTypeName(type: InterviewType): String {
    return type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}
