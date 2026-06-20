package com.aiic.app.presentation.feature_auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.GlassCard
import com.aiic.app.common.components.GradientText
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.theme.AIICTheme
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var visibleItems by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(5) { delay(100); visibleItems++ }
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
                .padding(top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            AnimatedVisibility(visibleItems > 0, enter = fadeIn() + slideInVertically { -40 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GradientText(
                        text = "AIIC",
                        style = AIICTheme.typography.displayLarge,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Sign in to your account",
                        style = AIICTheme.typography.bodyLarge,
                        color = AIICTheme.colors.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Form Card
            AnimatedVisibility(visibleItems > 1, enter = fadeIn() + slideInVertically { 30 }) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AIICTextField(
                            value = state.email,
                            onValueChange = { viewModel.onAction(LoginAction.UpdateEmail(it)) },
                            label = "Email",
                            placeholder = "you@example.com",
                            leadingIcon = Icons.Rounded.Email,
                            isError = state.emailError != null,
                            errorMessage = state.emailError,
                            keyboardType = KeyboardType.Email,
                        )

                        AIICTextField(
                            value = state.password,
                            onValueChange = { viewModel.onAction(LoginAction.UpdatePassword(it)) },
                            label = "Password",
                            placeholder = "Enter your password",
                            leadingIcon = Icons.Rounded.Lock,
                            isError = state.passwordError != null,
                            errorMessage = state.passwordError,
                            visualTransformation = if (state.isPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.onAction(LoginAction.TogglePasswordVisibility) }) {
                                    Icon(
                                        if (state.isPasswordVisible) Icons.Rounded.VisibilityOff
                                        else Icons.Rounded.Visibility,
                                        contentDescription = "Toggle password",
                                        tint = AIICTheme.colors.textTertiary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            },
                            keyboardType = KeyboardType.Password,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = onNavigateToForgotPassword) {
                                Text(
                                    text = "Forgot password?",
                                    style = AIICTheme.typography.bodySmall,
                                    color = AIICTheme.colors.primary,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Login Button
            AnimatedVisibility(visibleItems > 2, enter = fadeIn() + slideInVertically { 30 }) {
                PremiumButton(
                    text = if (state.isLoading) "" else "Sign In",
                    onClick = { viewModel.onAction(LoginAction.Login) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    content = if (state.isLoading) {
                        {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = AIICTheme.colors.textOnPrimary,
                                strokeWidth = 2.dp,
                            )
                        }
                    } else null,
                )
            }

            Spacer(Modifier.height(24.dp))

            // Divider
            AnimatedVisibility(visibleItems > 3, enter = fadeIn()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = AIICTheme.colors.border,
                    )
                    Text(
                        text = "  or continue with  ",
                        style = AIICTheme.typography.caption,
                        color = AIICTheme.colors.textTertiary,
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = AIICTheme.colors.border,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Google Button
            AnimatedVisibility(visibleItems > 3, enter = fadeIn() + slideInVertically { 20 }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(AIICTheme.shapes.button)
                        .background(AIICTheme.colors.surfaceElevated)
                        .clickable { viewModel.onAction(LoginAction.LoginWithGoogle) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Continue with Google",
                        style = AIICTheme.typography.titleSmall,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Register Link
            AnimatedVisibility(visibleItems > 4, enter = fadeIn()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textTertiary,
                    )
                    Text(
                        text = "Create one",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToRegister() },
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
