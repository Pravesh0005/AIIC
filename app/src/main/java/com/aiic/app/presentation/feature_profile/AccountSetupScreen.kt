package com.aiic.app.presentation.feature_profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.PremiumCard
import com.aiic.app.common.components.GradientText
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.theme.AIICTheme
import kotlinx.coroutines.delay

@Composable
fun AccountSetupScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AccountSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var visibleItems by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(5) {
            delay(120)
            visibleItems++
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigateToHome()
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AIICTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
                .padding(top = 32.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(visibleItems > 0, enter = fadeIn() + slideInVertically { -40 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GradientText(
                        text = "Complete Your Profile",
                        style = AIICTheme.typography.headlineLarge,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Help us personalize your interview coaching experience",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visibleItems > 1, enter = fadeIn() + slideInVertically { 30 }) {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Career Goals",
                            style = AIICTheme.typography.titleMedium,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )

                        AIICTextField(
                            value = state.targetRole,
                            onValueChange = { viewModel.onAction(AccountSetupAction.UpdateRole(it)) },
                            label = "Target Role",
                            placeholder = "e.g. Software Engineer, Product Manager",
                            leadingIcon = Icons.Rounded.Work,
                        )

                        AIICTextField(
                            value = state.targetCompany,
                            onValueChange = { viewModel.onAction(AccountSetupAction.UpdateCompany(it)) },
                            label = "Dream Company",
                            placeholder = "e.g. Google, Amazon, Meta",
                            leadingIcon = Icons.Rounded.Business,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(visibleItems > 2, enter = fadeIn() + slideInVertically { 30 }) {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Background",
                            style = AIICTheme.typography.titleMedium,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )

                        AIICTextField(
                            value = state.education,
                            onValueChange = { viewModel.onAction(AccountSetupAction.UpdateEducation(it)) },
                            label = "Education",
                            placeholder = "e.g. B.Tech CS, MBA, Self-taught",
                            leadingIcon = Icons.Rounded.School,
                        )

                        AIICTextField(
                            value = state.skills,
                            onValueChange = { viewModel.onAction(AccountSetupAction.UpdateSkills(it)) },
                            label = "Key Skills",
                            placeholder = "Kotlin, System Design, DSA",
                            leadingIcon = Icons.Rounded.Stars,
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visibleItems > 3, enter = fadeIn() + slideInVertically { 30 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PremiumButton(
                        text = if (state.isLoading) "Saving..." else "Continue",
                        onClick = { viewModel.onAction(AccountSetupAction.SaveProfile) },
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(12.dp))

                    TextButton(onClick = { viewModel.onAction(AccountSetupAction.Skip) }) {
                        Text(
                            text = "Skip for now",
                            style = AIICTheme.typography.bodyMedium,
                            color = AIICTheme.colors.textTertiary,
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
