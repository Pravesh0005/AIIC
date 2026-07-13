package com.aiic.app.presentation.feature_auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiic.app.R
import com.aiic.app.common.components.AIICTextField
import com.aiic.app.common.components.GoogleSignInButton
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
        repeat(8) { delay(80); visibleItems++ }
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
        com.aiic.app.common.components.EarthGlowBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            
            AnimatedVisibility(visibleItems > 0, enter = fadeIn() + slideInVertically { -30 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "AIIC Logo",
                        modifier = Modifier.size(80.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "AIIC",
                        style = AIICTheme.typography.headlineLarge,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "AI Interview Coach",
                        style = AIICTheme.typography.bodySmall,
                        color = AIICTheme.colors.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visibleItems > 1, enter = fadeIn() + slideInVertically { -20 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome Back!",
                        style = AIICTheme.typography.headlineLarge,
                        color = AIICTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Sign in to continue your interview journey",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visibleItems > 2, enter = fadeIn() + slideInVertically { 20 }) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    
                    AIICTextField(
                        value = state.email,
                        onValueChange = { viewModel.onAction(LoginAction.UpdateEmail(it)) },
                        label = "Email",
                        placeholder = "Enter your email",
                        isError = state.emailError != null,
                        errorMessage = state.emailError,
                        keyboardType = KeyboardType.Email,
                    )

                    AIICTextField(
                        value = state.password,
                        onValueChange = { viewModel.onAction(LoginAction.UpdatePassword(it)) },
                        label = "Password",
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
                                text = "Forgot Password?",
                                style = AIICTheme.typography.bodySmall,
                                color = AIICTheme.colors.secondary,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(visibleItems > 3, enter = fadeIn() + slideInVertically { 20 }) {
                PremiumButton(
                    text = "Sign In",
                    onClick = { viewModel.onAction(LoginAction.Login) },
                    enabled = !state.isLoading,
                    isLoading = state.isLoading,
                    showArrow = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(visibleItems > 4, enter = fadeIn()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = AIICTheme.colors.border,
                    )
                    Text(
                        text = "or continue with",
                        style = AIICTheme.typography.labelSmall,
                        color = AIICTheme.colors.textTertiary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = AIICTheme.colors.border,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(visibleItems > 5, enter = fadeIn() + slideInVertically { 20 }) {
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

                GoogleSignInButton(
                    text = "Continue with Google",
                    onClick = {
                        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
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

            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visibleItems > 6, enter = fadeIn()) {
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
                        text = "Sign Up",
                        style = AIICTheme.typography.bodyMedium,
                        color = AIICTheme.colors.secondary,
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
