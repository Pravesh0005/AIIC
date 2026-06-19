package com.aiic.app.presentation.feature_auth.forgot_password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.GradientText
import com.aiic.app.common.components.PremiumButton
import com.aiic.app.core.theme.AIICTheme

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
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
            Spacer(Modifier.height(12.dp))

            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = AIICTheme.colors.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(
                visible = !state.isSuccess,
                enter = fadeIn(),
            ) {
                Column {
                    Icon(
                        imageVector = Icons.Rounded.MailOutline,
                        contentDescription = null,
                        tint = AIICTheme.colors.primary,
                        modifier = Modifier.size(48.dp),
                    )

                    Spacer(Modifier.height(24.dp))

                    GradientText(
                        text = "Reset Password",
                        style = AIICTheme.typography.displaySmall,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Enter the email associated with your account and we'll send a reset link.",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textTertiary,
                    )

                    Spacer(Modifier.height(36.dp))

                    AIICTextField(
                        value = state.email,
                        onValueChange = { viewModel.onAction(ForgotPasswordAction.UpdateEmail(it)) },
                        label = "Email Address",
                        placeholder = "you@example.com",
                        keyboardType = KeyboardType.Email,
                        isError = state.emailError != null,
                        errorMessage = state.emailError,
                        imeAction = ImeAction.Done,
                        onImeAction = { viewModel.onAction(ForgotPasswordAction.SendReset) },
                    )

                    Spacer(Modifier.height(28.dp))

                    PremiumButton(
                        text = "Send Reset Link",
                        onClick = { viewModel.onAction(ForgotPasswordAction.SendReset) },
                        isLoading = state.isLoading,
                    )
                }
            }

            AnimatedVisibility(
                visible = state.isSuccess,
                enter = fadeIn() + slideInVertically { it / 2 },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(60.dp))

                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = AIICTheme.colors.success,
                        modifier = Modifier.size(72.dp),
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Check Your Email",
                        style = AIICTheme.typography.headlineLarge,
                        color = AIICTheme.colors.textPrimary,
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "We've sent a password reset link to\n${state.email}",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textTertiary,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(36.dp))

                    PremiumButton(
                        text = "Back to Sign In",
                        onClick = onNavigateBack,
                        gradientColors = listOf(
                            AIICTheme.colors.gradientSecondaryStart,
                            AIICTheme.colors.gradientSecondaryEnd,
                        ),
                    )
                }
            }
        }
    }
}
