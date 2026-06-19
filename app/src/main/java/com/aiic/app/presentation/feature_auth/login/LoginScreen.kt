package com.aiic.app.presentation.feature_auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.AppLogo
import com.aiic.app.common.components.GradientText
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.theme.AIICTheme

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigateToHome()
                else -> {}
            }
        }
    }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AIICTheme.spacing.screenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(60.dp))

            AppLogo(size = 56.dp)
            Spacer(Modifier.height(24.dp))

            GradientText(
                text = "Welcome Back",
                style = AIICTheme.typography.displaySmall,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Sign in to continue your interview prep",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textTertiary,
            )

            Spacer(Modifier.height(40.dp))

            AIICTextField(
                value = state.email,
                onValueChange = { viewModel.onAction(LoginAction.UpdateEmail(it)) },
                label = "Email Address",
                placeholder = "you@example.com",
                isError = state.emailError != null,
                errorMessage = state.emailError,
                imeAction = ImeAction.Next,
            )

            Spacer(Modifier.height(16.dp))

            AIICTextField(
                value = state.password,
                onValueChange = { viewModel.onAction(LoginAction.UpdatePassword(it)) },
                label = "Password",
                placeholder = "Enter your password",
                isPassword = true,
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.onAction(LoginAction.Login) },
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "Forgot Password?",
                    style = AIICTheme.typography.labelMedium,
                    color = AIICTheme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(AIICTheme.shapes.small)
                        .clickable { onNavigateToForgotPassword() }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            PremiumButton(
                text = "Sign In",
                onClick = { viewModel.onAction(LoginAction.Login) },
                isLoading = state.isLoading,
            )

            Spacer(Modifier.height(24.dp))

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
                    style = AIICTheme.typography.labelSmall,
                    color = AIICTheme.colors.textTertiary,
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = AIICTheme.colors.border,
                )
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(AIICTheme.shapes.button)
                    .background(AIICTheme.colors.surfaceElevated)
                    .border(1.dp, AIICTheme.colors.border, AIICTheme.shapes.button)
                    .clickable { viewModel.onAction(LoginAction.LoginWithGoogle) },
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "G", fontWeight = FontWeight.Bold, color = AIICTheme.colors.textPrimary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        style = AIICTheme.typography.button,
                        color = AIICTheme.colors.textPrimary,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textTertiary,
                )
                Text(
                    text = "Sign Up",
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() },
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
