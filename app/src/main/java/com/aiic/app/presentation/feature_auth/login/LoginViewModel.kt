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
    data object LoginWithGoogle : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
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
            LoginAction.LoginWithGoogle -> sendEvent(UiEvent.ShowSnackbar("Google Sign-In coming soon"))
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
