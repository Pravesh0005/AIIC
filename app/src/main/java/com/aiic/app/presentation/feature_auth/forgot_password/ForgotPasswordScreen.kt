package com.aiic.app.presentation.feature_auth.forgot_password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.theme.AIICTheme
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var visibleItems by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(4) { delay(120); visibleItems++ }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
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
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Back button
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = AIICTheme.colors.textPrimary,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            if (state.isResetSent) {
                // Success state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = AIICTheme.colors.primary, // Using primary since it's white now
                        modifier = Modifier.size(72.dp),
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Check Your Email",
                        style = AIICTheme.typography.displayMedium,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "We've sent a password reset link to ${state.email}. Check your inbox and follow the instructions.",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(32.dp))
                    PremiumButton(
                        text = "Back to Sign In",
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                // Form state
                AnimatedVisibility(visibleItems > 0, enter = fadeIn() + slideInVertically { -20 }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Reset Password",
                            style = AIICTheme.typography.displayMedium,
                            color = AIICTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Enter your email and we'll send you a link to reset your password",
                            style = AIICTheme.typography.bodyMedium,
                            color = AIICTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))

                AnimatedVisibility(visibleItems > 1, enter = fadeIn() + slideInVertically { 20 }) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        AIICTextField(
                            value = state.email,
                            onValueChange = { viewModel.onAction(ForgotPasswordAction.UpdateEmail(it)) },
                            label = "EMAIL",
                            placeholder = "you@example.com",
                            isError = state.emailError != null,
                            errorMessage = state.emailError,
                            keyboardType = KeyboardType.Email,
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                AnimatedVisibility(visibleItems > 2, enter = fadeIn() + slideInVertically { 20 }) {
                    PremiumButton(
                        text = "Send Reset Link",
                        onClick = { viewModel.onAction(ForgotPasswordAction.ResetPassword) },
                        enabled = !state.isLoading,
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(32.dp))

                AnimatedVisibility(visibleItems > 3, enter = fadeIn()) {
                    Text(
                        text = "Remember your password? Sign in",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onNavigateBack() },
                    )
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
