package com.aiic.app.presentation.feature_resume.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.presentation.feature_resume.analysis.components.StrengthCard
import com.aiic.app.presentation.feature_resume.analysis.components.WeaknessCard

@Composable
fun ATSScoreScreen(
    analysis: ResumeAnalysis,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
            }
            Text(
                text = "ATS Breakdown",
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
        ) {
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Scoring Matrix",
                        style = AIICTheme.typography.titleMedium,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    val details = analysis.atsScoreDetails
                    ScoreBar("Technical Skills", details.skillsScore)
                    ScoreBar("Project Impact", details.projectScore)
                    ScoreBar("Experience Depth", details.experienceScore)
                    ScoreBar("Keyword Relevance", details.keywordScore)
                    ScoreBar("Formatting & Structure", details.structureScore)
                    ScoreBar("Data Completeness", details.completenessScore)
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            StrengthCard(strengths = analysis.strengths)
            
            Spacer(Modifier.height(16.dp))
            
            WeaknessCard(weaknesses = analysis.weaknesses)
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ScoreBar(label: String, score: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary
            )
            Text(
                text = "$score / 100",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = if (score >= 80) AIICTheme.colors.success else if (score >= 50) AIICTheme.colors.primary else AIICTheme.colors.warning,
            trackColor = AIICTheme.colors.surfaceBright
        )
    }
}
