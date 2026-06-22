package com.aiic.app.presentation.feature_resume.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiic.app.core.theme.AIICTheme
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.presentation.feature_resume.analysis.components.KeywordGapCard
import com.aiic.app.presentation.feature_resume.analysis.components.SkillChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillBreakdownScreen(
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
                text = "Skill Intelligence",
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
            Text(
                text = "Extracted Technologies & Skills",
                style = AIICTheme.typography.titleMedium,
                color = AIICTheme.colors.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            analysis.skills.forEach { (category, skillsList) ->
                Text(
                    text = category,
                    style = AIICTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = AIICTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    skillsList.forEach { skill ->
                        SkillChip(skill = skill)
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            KeywordGapCard(missingKeywords = analysis.missingKeywords)
            
            Spacer(Modifier.height(32.dp))
        }
    }
}
