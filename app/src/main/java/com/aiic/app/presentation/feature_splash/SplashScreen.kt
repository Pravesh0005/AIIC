package com.aiic.app.presentation.feature_splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AppLogo
import com.aiic.app.common.components.GradientText
import com.aiic.app.core.theme.AIICTheme

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val logoScale = remember { Animatable(0.3f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        textAlpha.animateTo(1f, tween(500))
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(700)
        taglineAlpha.animateTo(1f, tween(500))
    }
    LaunchedEffect(Unit) {
        viewModel.checkDestination(onNavigateToOnboarding, onNavigateToHome)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AIICTheme.colors.gradientDarkStart,
                        AIICTheme.colors.surface,
                        AIICTheme.colors.gradientDarkEnd,
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppLogo(
                size = 80.dp,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
            )

            Spacer(Modifier.height(24.dp))

            GradientText(
                text = "AIIC",
                style = AIICTheme.typography.displayLarge,
                modifier = Modifier.alpha(textAlpha.value),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your Personal AI Career Coach",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textTertiary,
                modifier = Modifier.alpha(taglineAlpha.value),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(taglineAlpha.value),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(80.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "AI Interview Coach",
                    style = AIICTheme.typography.labelSmall,
                    color = AIICTheme.colors.textDisabled,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
