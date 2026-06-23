package com.aiic.app.presentation.feature_dummy

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Rocket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiic.app.core.theme.AIICTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DummyScreen(
    title: String,
    onNavigateBack: () -> Unit
) {
    val fadeIn = remember { Animatable(0f) }
    LaunchedEffect(Unit) { fadeIn.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }

    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = AIICTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AIICTheme.colors.background,
                    titleContentColor = AIICTheme.colors.textPrimary,
                    navigationIconContentColor = AIICTheme.colors.textPrimary,
                )
            )
        },
        containerColor = AIICTheme.colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .alpha(fadeIn.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            // Animated icon container
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                AIICTheme.colors.primary.copy(alpha = 0.15f),
                                AIICTheme.colors.accent.copy(alpha = 0.1f),
                            )
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.Rocket,
                    contentDescription = null,
                    tint = AIICTheme.colors.primary,
                    modifier = Modifier.size(56.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Under Development",
                style = AIICTheme.typography.headlineMedium,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "We're building something amazing for \"$title\".\nThis feature will be available in the next update.",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 48.dp),
            )

            Spacer(Modifier.height(32.dp))

            // Progress chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AIICTheme.colors.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = AIICTheme.colors.primary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Coming Soon",
                    style = AIICTheme.typography.labelMedium,
                    color = AIICTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(48.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(horizontal = 48.dp).fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AIICTheme.colors.primary),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
            ) {
                Text(
                    text = "Go Back",
                    style = AIICTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}
