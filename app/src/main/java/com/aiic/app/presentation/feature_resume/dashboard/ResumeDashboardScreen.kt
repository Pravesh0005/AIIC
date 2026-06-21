package com.aiic.app.presentation.feature_resume.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.Resume
import com.aiic.app.presentation.feature_resume.components.EmptyResumeState
import com.aiic.app.presentation.feature_resume.components.ResumeCard

@Composable
fun ResumeDashboardScreen(
    onNavigateToUpload: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: ResumeDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        DashboardHeader()
        
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(
                        color = AIICTheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DashboardUiState.Error -> {
                    Text(
                        text = state.message,
                        color = AIICTheme.colors.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DashboardUiState.Success -> {
                    DashboardContent(
                        activeResume = state.activeResume,
                        latestResume = state.latestResume,
                        onNavigateToUpload = onNavigateToUpload,
                        onNavigateToHistory = onNavigateToHistory,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AIICTheme.spacing.screenHorizontal, vertical = 24.dp)
    ) {
        Text(
            text = "Resume Platform",
            style = AIICTheme.typography.headlineLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Manage your professional profile and resume versions.",
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary
        )
    }
}

@Composable
private fun DashboardContent(
    activeResume: Resume?,
    latestResume: Resume?,
    onNavigateToUpload: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AIICTheme.spacing.screenHorizontal)
    ) {
        if (activeResume == null && latestResume == null) {
            Spacer(Modifier.height(48.dp))
            EmptyResumeState(
                title = "No Resume Uploaded",
                message = "Upload your resume in PDF format to enable AI analysis and ATS scoring.",
                modifier = Modifier.padding(vertical = 32.dp)
            )
            Spacer(Modifier.height(32.dp))
            PremiumButton(
                text = "Upload Resume",
                onClick = onNavigateToUpload,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            if (activeResume != null) {
                Text(
                    text = "Active Resume",
                    style = AIICTheme.typography.titleMedium,
                    color = AIICTheme.colors.textPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                ResumeCard(
                    resume = activeResume,
                    onClick = { onNavigateToDetail(activeResume.resumeId) }
                )
                Spacer(Modifier.height(24.dp))
            }
            
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = AIICTheme.typography.titleSmall,
                        color = AIICTheme.colors.textSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    PremiumButton(
                        text = "Upload New Version",
                        onClick = onNavigateToUpload,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    
                    PremiumButton(
                        text = "View Version History",
                        onClick = onNavigateToHistory,
                        isSecondary = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
