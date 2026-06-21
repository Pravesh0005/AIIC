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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.common.components.AIICTextField
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
        repeat(6) { delay(80); visibleItems++ }
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
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            AnimatedVisibility(visibleItems > 0, enter = fadeIn() + slideInVertically { -20 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome Back",
                        style = AIICTheme.typography.displayMedium,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
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

            // Google Button (Top Priority)
            AnimatedVisibility(visibleItems > 1, enter = fadeIn() + slideInVertically { 20 }) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val googleSignInLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                        account?.idToken?.let { idToken ->
                            viewModel.onAction(LoginAction.GoogleSignInSuccess(idToken))
                        }
                    } catch (e: Exception) {
                        viewModel.onAction(LoginAction.GoogleSignInFailure(e.message ?: "Google Sign-In Failed"))
                    }
                }

                com.aiic.app.common.components.GoogleSignInButton(
                    text = "Continue with Google",
                    onClick = { 
                        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(com.aiic.app.R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Divider
            AnimatedVisibility(visibleItems > 2, enter = fadeIn()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = AIICTheme.colors.border,
                    )
                    Text(
                        text = "or",
                        style = AIICTheme.typography.labelMedium,
                        color = AIICTheme.colors.textTertiary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = AIICTheme.colors.border,
                    )
                }
            }

            // Form
            AnimatedVisibility(visibleItems > 3, enter = fadeIn() + slideInVertically { 20 }) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    AIICTextField(
                        value = state.email,
                        onValueChange = { viewModel.onAction(LoginAction.UpdateEmail(it)) },
                        label = "EMAIL",
                        placeholder = "you@example.com",
                        isError = state.emailError != null,
                        errorMessage = state.emailError,
                        keyboardType = KeyboardType.Email,
                    )

                    AIICTextField(
                        value = state.password,
                        onValueChange = { viewModel.onAction(LoginAction.UpdatePassword(it)) },
                        label = "PASSWORD",
                        placeholder = "Enter your password",
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
                                    tint = AIICTheme.colors.textSecondary,
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

            Spacer(Modifier.height(24.dp))

            // Login Button
            AnimatedVisibility(visibleItems > 4, enter = fadeIn() + slideInVertically { 20 }) {
                PremiumButton(
                    text = "Sign In",
                    onClick = { viewModel.onAction(LoginAction.Login) },
                    enabled = !state.isLoading,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(32.dp))

            // Register Link
            AnimatedVisibility(visibleItems > 5, enter = fadeIn()) {
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
            snackbar = { data ->
                androidx.compose.material3.Snackbar(
                    snackbarData = data,
                    containerColor = AIICTheme.colors.surfaceElevated,
                    contentColor = AIICTheme.colors.textPrimary,
                    actionColor = AIICTheme.colors.primary,
                    shape = AIICTheme.shapes.medium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        )
    }
}
