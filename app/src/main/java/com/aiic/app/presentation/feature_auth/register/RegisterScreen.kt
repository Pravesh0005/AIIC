package com.aiic.app.presentation.feature_auth.register

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.GradientText
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.theme.AIICTheme

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
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
        ) {
            Spacer(Modifier.height(12.dp))

            IconButton(onClick = onNavigateToLogin) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = AIICTheme.colors.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            GradientText(
                text = "Create Account",
                style = AIICTheme.typography.displaySmall,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Start your journey to interview mastery",
                style = AIICTheme.typography.bodyMedium,
                color = AIICTheme.colors.textTertiary,
            )

            Spacer(Modifier.height(36.dp))

            AIICTextField(
                value = state.name,
                onValueChange = { viewModel.onAction(RegisterAction.UpdateName(it)) },
                label = "Full Name",
                placeholder = "John Doe",
                isError = state.nameError != null,
                errorMessage = state.nameError,
            )

            Spacer(Modifier.height(16.dp))

            AIICTextField(
                value = state.email,
                onValueChange = { viewModel.onAction(RegisterAction.UpdateEmail(it)) },
                label = "Email Address",
                placeholder = "you@example.com",
                keyboardType = KeyboardType.Email,
                isError = state.emailError != null,
                errorMessage = state.emailError,
            )

            Spacer(Modifier.height(16.dp))

            AIICTextField(
                value = state.password,
                onValueChange = { viewModel.onAction(RegisterAction.UpdatePassword(it)) },
                label = "Password",
                placeholder = "Min. 8 characters",
                isPassword = true,
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
            )

            Spacer(Modifier.height(16.dp))

            AIICTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onAction(RegisterAction.UpdateConfirmPassword(it)) },
                label = "Confirm Password",
                placeholder = "Re-enter your password",
                isPassword = true,
                isError = state.confirmPasswordError != null,
                errorMessage = state.confirmPasswordError,
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.onAction(RegisterAction.Register) },
            )

            Spacer(Modifier.height(28.dp))

            PremiumButton(
                text = "Create Account",
                onClick = { viewModel.onAction(RegisterAction.Register) },
                isLoading = state.isLoading,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "By creating an account, you agree to our Terms of Service and Privacy Policy.",
                style = AIICTheme.typography.caption,
                color = AIICTheme.colors.textDisabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Already have an account? ",
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.textTertiary,
                )
                Text(
                    text = "Sign In",
                    style = AIICTheme.typography.bodySmall,
                    color = AIICTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() },
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
