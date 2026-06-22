package com.aiic.app.presentation.feature_auth.login

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.validation.Validator
import com.aiic.app.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
)

sealed interface LoginAction {
    data class UpdateEmail(val email: String) : LoginAction
    data class UpdatePassword(val password: String) : LoginAction
    data object TogglePasswordVisibility : LoginAction
    data object Login : LoginAction
    data class GoogleSignInSuccess(val idToken: String) : LoginAction
    data class GoogleSignInFailure(val error: String) : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: com.aiic.app.domain.repository.AuthRepository
) : BaseViewModel<LoginState, LoginAction>(LoginState()) {

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UpdateEmail -> updateState {
                copy(email = action.email, emailError = null)
            }
            is LoginAction.UpdatePassword -> updateState {
                copy(password = action.password, passwordError = null)
            }
            LoginAction.TogglePasswordVisibility -> updateState {
                copy(isPasswordVisible = !isPasswordVisible)
            }
            LoginAction.Login -> login()
            is LoginAction.GoogleSignInSuccess -> loginWithGoogle(action.idToken)
            is LoginAction.GoogleSignInFailure -> sendEvent(UiEvent.ShowSnackbar(action.error))
        }
    }

    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            when (val result = authRepository.loginWithGoogle(idToken)) {
                is NetworkResult.Success -> {
                    sendEvent(UiEvent.Navigate("home"))
                }
                is NetworkResult.Error -> {
                    sendEvent(UiEvent.ShowSnackbar(result.message))
                }
            }
            updateState { copy(isLoading = false) }
        }
    }

    private fun login() {
        val emailResult = Validator.validateEmail(currentState.email.trim())
        val passwordResult = Validator.validatePassword(currentState.password)

        if (!emailResult.isValid || !passwordResult.isValid) {
            updateState {
                copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                )
            }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            when (val result = loginUseCase(currentState.email.trim(), currentState.password)) {
                is NetworkResult.Success -> {
                    sendEvent(UiEvent.Navigate("home"))
                }
                is NetworkResult.Error -> {
                    sendEvent(UiEvent.ShowSnackbar(result.message))
                }
            }
            updateState { copy(isLoading = false) }
        }
    }
}
