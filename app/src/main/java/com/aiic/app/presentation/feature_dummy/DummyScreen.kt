package com.aiic.app.presentation.feature_dummy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiic.app.core.theme.AIICTheme

@Composable
fun DummyScreen(
    title: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AIICTheme.colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = AIICTheme.typography.titleLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            Icons.Rounded.Construction,
            contentDescription = null,
            tint = AIICTheme.colors.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Coming Soon",
            style = AIICTheme.typography.headlineMedium,
            color = AIICTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We are working hard to bring you this feature.",
            style = AIICTheme.typography.bodyMedium,
            color = AIICTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.weight(1.5f))
    }
}
