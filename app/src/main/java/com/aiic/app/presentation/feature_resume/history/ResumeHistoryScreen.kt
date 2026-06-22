package com.aiic.app.presentation.feature_resume.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.presentation.feature_resume.components.EmptyResumeState
import com.aiic.app.presentation.feature_resume.components.ResumeCard

@Composable
fun ResumeHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: ResumeHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        RowHeader(onNavigateBack)
        
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    CircularProgressIndicator(
                        color = AIICTheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is HistoryUiState.Error -> {
                    Text(
                        text = state.message,
                        color = AIICTheme.colors.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is HistoryUiState.Success -> {
                    if (state.resumes.isEmpty()) {
                        EmptyResumeState(
                            title = "No History Found",
                            message = "You haven't uploaded any resumes yet.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = AIICTheme.spacing.screenHorizontal,
                                end = AIICTheme.spacing.screenHorizontal,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.resumes) { resume ->
                                var showMenu by remember { mutableStateOf(false) }
                                
                                Box {
                                    ResumeCard(
                                        resume = resume,
                                        onClick = { onNavigateToDetail(resume.resumeId) },
                                        onMenuClick = { showMenu = true },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false },
                                        modifier = Modifier.background(AIICTheme.colors.surfaceElevated)
                                    ) {
                                        if (!resume.activeResume) {
                                            DropdownMenuItem(
                                                text = { Text("Set as Active", color = AIICTheme.colors.textPrimary) },
                                                onClick = {
                                                    viewModel.setActiveResume(resume.resumeId)
                                                    showMenu = false
                                                }
                                            )
                                        }
                                        DropdownMenuItem(
                                            text = { Text("Delete Version", color = AIICTheme.colors.error) },
                                            onClick = {
                                                viewModel.deleteResume(resume.resumeId, resume.resumeVersion)
                                                showMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowHeader(onNavigateBack: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = "Version History",
            style = AIICTheme.typography.titleLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
