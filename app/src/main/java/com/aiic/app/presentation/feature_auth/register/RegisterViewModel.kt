package com.aiic.app.presentation.feature_auth.register

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

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
)

sealed interface RegisterAction {
    data class UpdateName(val name: String) : RegisterAction
    data class UpdateEmail(val email: String) : RegisterAction
    data class UpdatePassword(val password: String) : RegisterAction
    data class UpdateConfirmPassword(val confirmPassword: String) : RegisterAction
    data object Register : RegisterAction
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<RegisterState, RegisterAction>(RegisterState()) {

    override fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.UpdateName -> updateState { copy(name = action.name, nameError = null) }
            is RegisterAction.UpdateEmail -> updateState { copy(email = action.email, emailError = null) }
            is RegisterAction.UpdatePassword -> updateState { copy(password = action.password, passwordError = null) }
            is RegisterAction.UpdateConfirmPassword -> updateState { copy(confirmPassword = action.confirmPassword, confirmPasswordError = null) }
            RegisterAction.Register -> register()
        }
    }

    private fun register() {
        val name = currentState.name.trim()
        val email = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        var hasError = false
        if (name.length < 2) {
            updateState { copy(nameError = "Name must be at least 2 characters") }
            hasError = true
        }
        if (!email.isValidEmail()) {
            updateState { copy(emailError = "Please enter a valid email") }
            hasError = true
        }
        if (!password.isValidPassword()) {
            updateState { copy(passwordError = "Password must be at least 8 characters") }
            hasError = true
        }
        if (password != confirmPassword) {
            updateState { copy(confirmPasswordError = "Passwords do not match") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            authRepository.register(name, email, password)
                .onSuccess {
                    userPreferencesRepository.setLoggedIn(true)
                    sendEvent(UiEvent.Navigate("home"))
                }
                .onFailure {
                    sendEvent(UiEvent.ShowSnackbar(it.message ?: "Registration failed"))
                }
            updateState { copy(isLoading = false) }
        }
    }
}
