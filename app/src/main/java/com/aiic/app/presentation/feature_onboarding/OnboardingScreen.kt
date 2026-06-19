package com.aiic.app.presentation.feature_onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.GradientText
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AIICTheme.spacing.screenHorizontal),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AIICTheme.spacing.base),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(state.totalPages) { index ->
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(if (index == state.currentPage) 24.dp else 12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == state.currentPage) AIICTheme.colors.primary
                                    else AIICTheme.colors.border
                                ),
                        )
                    }
                }
                TextButton(onClick = {
                    viewModel.onAction(OnboardingAction.Skip)
                    onNavigateToLogin()
                }) {
                    Text(
                        text = "Skip",
                        style = AIICTheme.typography.labelLarge,
                        color = AIICTheme.colors.textTertiary,
                    )
                }
            }

            Spacer(Modifier.weight(0.15f))

            AnimatedContent(
                targetState = state.currentPage,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInHorizontally { it / 3 }) togetherWith
                            (fadeOut(tween(200)) + slideOutHorizontally { -it / 3 })
                },
                label = "onboarding_page",
            ) { page ->
                OnboardingPageContent(onboardingPages[page])
            }

            Spacer(Modifier.weight(0.3f))

            val isLastPage = state.currentPage == state.totalPages - 1

            PremiumButton(
                text = if (isLastPage) "Get Started" else "Continue",
                onClick = {
                    if (isLastPage) {
                        viewModel.onAction(OnboardingAction.Complete)
                        onNavigateToLogin()
                    } else {
                        viewModel.onAction(OnboardingAction.NextPage)
                    }
                },
            )

            Spacer(Modifier.height(AIICTheme.spacing.xxl))
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val emojiScale = remember { Animatable(0.5f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(page) {
        emojiScale.snapTo(0.5f)
        contentAlpha.snapTo(0f)
        emojiScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(page) {
        contentAlpha.snapTo(0f)
        kotlinx.coroutines.delay(200)
        contentAlpha.animateTo(1f, tween(400))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = page.accentEmoji,
            fontSize = 64.sp,
            modifier = Modifier.scale(emojiScale.value),
        )

        Spacer(Modifier.height(32.dp))

        GradientText(
            text = page.title,
            style = AIICTheme.typography.displayMedium,
            modifier = Modifier.alpha(contentAlpha.value),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            style = AIICTheme.typography.bodyLarge,
            color = AIICTheme.colors.textSecondary,
            lineHeight = 26.sp,
            modifier = Modifier.alpha(contentAlpha.value),
        )
    }
}
