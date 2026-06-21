package com.aiic.app.presentation.feature_resume.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.presentation.feature_resume.analysis.components.ATSScoreCard
import com.aiic.app.presentation.feature_resume.analysis.components.AnalysisSummaryCard

@Composable
fun AnalysisDashboardContent(
    analysis: ResumeAnalysis,
    onNavigateToATS: () -> Unit,
    onNavigateToSkills: () -> Unit,
    onNavigateToRecommendations: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
            }
            Text(
                text = "Resume Intelligence",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        ATSScoreCard(score = analysis.overallScore, classification = analysis.classification)
        
        Spacer(Modifier.height(24.dp))
        
        AnalysisSummaryCard(summary = analysis.profileSummary, recruiterImpression = analysis.recruiterImpression)
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = "Deep Dives",
            style = AIICTheme.typography.titleMedium,
            color = AIICTheme.colors.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        PremiumButton(
            text = "View ATS Breakdown",
            onClick = onNavigateToATS,
            isSecondary = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        PremiumButton(
            text = "View Skill Matrix & Gaps",
            onClick = onNavigateToSkills,
            isSecondary = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        PremiumButton(
            text = "View Actionable Recommendations",
            onClick = onNavigateToRecommendations,
            isSecondary = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}
