package com.aiic.app.presentation.feature_resume.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.Resume
import com.aiic.app.presentation.feature_resume.components.ActiveBadge
import com.aiic.app.presentation.feature_resume.components.ResumeStatusChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResumeDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAnalysis: (String) -> Unit,
    viewModel: ResumeDetailViewModel = hiltViewModel()
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
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(
                        color = AIICTheme.colors.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = AIICTheme.colors.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DetailUiState.Success -> {
                    DetailContent(
                        resume = state.resume,
                        onNavigateToAnalysis = { onNavigateToAnalysis(state.resume.resumeId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowHeader(onNavigateBack: () -> Unit) {
    Row(
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
            text = "Resume Details",
            style = AIICTheme.typography.titleLarge,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetailContent(resume: Resume, onNavigateToAnalysis: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AIICTheme.spacing.screenHorizontal)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AIICTheme.colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    tint = AIICTheme.colors.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        PremiumCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = resume.fileName,
                    style = AIICTheme.typography.headlineSmall,
                    color = AIICTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (resume.activeResume) {
                        ActiveBadge()
                        Spacer(Modifier.width(12.dp))
                    }
                    ResumeStatusChip(status = resume.analysisStatus)
                }
                
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = AIICTheme.colors.borderSubtle)
                Spacer(Modifier.height(24.dp))

                DetailRow(label = "Version", value = "Version ${resume.resumeVersion}")
                DetailRow(label = "Upload Date", value = formatDate(resume.uploadDate))
                DetailRow(label = "File Size", value = formatBytes(resume.fileSize))
                DetailRow(label = "Resume ID", value = resume.resumeId)
                DetailRow(label = "Processing State", value = resume.processingState.name)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        com.aiic.app.common.components.PremiumButton(
            text = "Generate AI Intelligence Insights",
            onClick = onNavigateToAnalysis,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(24.dp))
        
        // Future Proofing: Architecture ready for PDF Preview Component.
        PremiumCard(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(AIICTheme.colors.surfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PDF Preview Engine\n(Coming in Future Milestone)",
                    style = AIICTheme.typography.bodyMedium,
                    color = AIICTheme.colors.textTertiary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textPrimary,
            modifier = Modifier.weight(2f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown date"
    val format = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
