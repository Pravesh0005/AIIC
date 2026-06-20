package com.aiic.app.presentation.feature_auth.register

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToAccountSetup: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var visibleItems by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(6) { delay(100); visibleItems++ }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigateToAccountSetup()
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(AIICTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AIICTheme.spacing.screenHorizontal)
                .padding(top = 48.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(visibleItems > 0, enter = fadeIn() + slideInVertically { -40 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GradientText(text = "Create Account", style = AIICTheme.typography.headlineLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Start your interview preparation journey",
                        style = AIICTheme.typography.bodyLarge,
                        color = AIICTheme.colors.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visibleItems > 1, enter = fadeIn() + slideInVertically { 30 }) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AIICTextField(
                            value = state.name,
                            onValueChange = { viewModel.onAction(RegisterAction.UpdateName(it)) },
                            label = "Full Name",
                            placeholder = "John Doe",
                            leadingIcon = Icons.Rounded.Person,
                            isError = state.nameError != null,
                            errorMessage = state.nameError,
                        )

                        AIICTextField(
                            value = state.email,
                            onValueChange = { viewModel.onAction(RegisterAction.UpdateEmail(it)) },
                            label = "Email",
                            placeholder = "you@example.com",
                            leadingIcon = Icons.Rounded.Email,
                            isError = state.emailError != null,
                            errorMessage = state.emailError,
                            keyboardType = KeyboardType.Email,
                        )

                        AIICTextField(
                            value = state.password,
                            onValueChange = { viewModel.onAction(RegisterAction.UpdatePassword(it)) },
                            label = "Password",
                            placeholder = "Min 8 chars, 1 uppercase, 1 number",
                            leadingIcon = Icons.Rounded.Lock,
                            isError = state.passwordError != null,
                            errorMessage = state.passwordError,
                            visualTransformation = if (state.isPasswordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.onAction(RegisterAction.TogglePasswordVisibility) }) {
                                    Icon(
                                        if (state.isPasswordVisible) Icons.Rounded.VisibilityOff
                                        else Icons.Rounded.Visibility,
                                        contentDescription = "Toggle",
                                        tint = AIICTheme.colors.textTertiary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            },
                            keyboardType = KeyboardType.Password,
                        )

                        AIICTextField(
                            value = state.confirmPassword,
                            onValueChange = { viewModel.onAction(RegisterAction.UpdateConfirmPassword(it)) },
                            label = "Confirm Password",
                            placeholder = "Re-enter your password",
                            leadingIcon = Icons.Rounded.Lock,
                            isError = state.confirmPasswordError != null,
                            errorMessage = state.confirmPasswordError,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardType = KeyboardType.Password,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(visibleItems > 2, enter = fadeIn()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Checkbox(
                        checked = state.agreedToTerms,
                        onCheckedChange = { viewModel.onAction(RegisterAction.ToggleTerms(it)) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AIICTheme.colors.primary,
                            uncheckedColor = AIICTheme.colors.textTertiary,
                        ),
                    )
                    Text(
                        text = "I agree to the Terms of Service and Privacy Policy",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(visibleItems > 3, enter = fadeIn() + slideInVertically { 30 }) {
                PremiumButton(
                    text = if (state.isLoading) "" else "Create Account",
                    onClick = { viewModel.onAction(RegisterAction.Register) },
                    enabled = !state.isLoading && state.agreedToTerms,
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

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visibleItems > 4, enter = fadeIn()) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Already have an account? ",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textTertiary,
                    )
                    Text(
                        text = "Sign in",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToLogin() },
                    )
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
