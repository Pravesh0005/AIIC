package com.aiic.app.presentation.feature_splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.R
import com.aiic.app.core.theme.AIICTheme

/**
 * Splash Screen — Design Reference #2
 * Dark background, centered AIIC logo with scale+fade animation,
 * "AIIC" text, "AI Interview Coach" subtitle,
 * bottom tagline: "Empowering you to crack your dream interview."
 */
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
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
        viewModel.resolveDestination { destination ->
            when (destination) {
                SplashDestination.Onboarding -> onNavigateToOnboarding()
                SplashDestination.Login -> onNavigateToLogin()
                SplashDestination.Home -> onNavigateToHome()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo icon
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "AIIC Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
            )

            Spacer(Modifier.height(16.dp))

            // "AIIC" text
            Text(
                text = "AIIC",
                style = AIICTheme.typography.displayLarge,
                color = AIICTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha.value),
            )

            Spacer(Modifier.height(8.dp))

            // "AI Interview Coach" subtitle
            Text(
                text = "AI Interview Coach",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textSecondary,
                modifier = Modifier.alpha(textAlpha.value),
            )
        }

        // Bottom tagline
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
                    text = buildAnnotatedString {
                        append("Empowering you to crack\n")
                        withStyle(SpanStyle(color = AIICTheme.colors.secondary)) {
                            append("your dream interview.")
                        }
                    },
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textTertiary,
                    fontWeight = FontWeight.Normal,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
