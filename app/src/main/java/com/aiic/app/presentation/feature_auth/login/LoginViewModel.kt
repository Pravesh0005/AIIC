package com.aiic.app.presentation.feature_auth.login

import androidx.lifecycle.viewModelScope
import com.aiic.app.common.extensions.isValidEmail
import com.aiic.app.common.extensions.isValidPassword
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.UiEvent
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
)

sealed interface LoginAction {
    data class UpdateEmail(val email: String) : LoginAction
    data class UpdatePassword(val password: String) : LoginAction
    data object Login : LoginAction
    data object LoginWithGoogle : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<LoginState, LoginAction>(LoginState()) {

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UpdateEmail -> updateState {
                copy(email = action.email, emailError = null)
            }
            is LoginAction.UpdatePassword -> updateState {
                copy(password = action.password, passwordError = null)
            }
            LoginAction.Login -> login()
            LoginAction.LoginWithGoogle -> loginWithGoogle()
        }
    }

    private fun login() {
        val email = currentState.email.trim()
        val password = currentState.password

        var hasError = false
        if (!email.isValidEmail()) {
            updateState { copy(emailError = "Please enter a valid email") }
            hasError = true
        }
        if (!password.isValidPassword()) {
            updateState { copy(passwordError = "Password must be at least 8 characters") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            authRepository.login(email, password)
                .onSuccess {
                    userPreferencesRepository.setLoggedIn(true)
                    sendEvent(UiEvent.Navigate("home"))
                }
                .onFailure {
                    sendEvent(UiEvent.ShowSnackbar(it.message ?: "Login failed"))
                }
            updateState { copy(isLoading = false) }
        }
    }

    private fun loginWithGoogle() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            authRepository.loginWithGoogle("mock_token")
                .onSuccess {
                    userPreferencesRepository.setLoggedIn(true)
                    sendEvent(UiEvent.Navigate("home"))
                }
                .onFailure {
                    sendEvent(UiEvent.ShowSnackbar(it.message ?: "Google sign-in failed"))
                }
            updateState { copy(isLoading = false) }
        }
    }
}
