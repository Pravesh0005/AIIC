package com.aiic.app.presentation.feature_auth.register

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.validation.Validator
import com.aiic.app.domain.usecase.RegisterUseCase
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
    val isPasswordVisible: Boolean = false,
    val agreedToTerms: Boolean = false,
)

sealed interface RegisterAction {
    data class UpdateName(val name: String) : RegisterAction
    data class UpdateEmail(val email: String) : RegisterAction
    data class UpdatePassword(val password: String) : RegisterAction
    data class UpdateConfirmPassword(val confirmPassword: String) : RegisterAction
    data object TogglePasswordVisibility : RegisterAction
    data class ToggleTerms(val agreed: Boolean) : RegisterAction
    data object Register : RegisterAction
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : BaseViewModel<RegisterState, RegisterAction>(RegisterState()) {

    override fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.UpdateName -> updateState { copy(name = action.name, nameError = null) }
            is RegisterAction.UpdateEmail -> updateState { copy(email = action.email, emailError = null) }
            is RegisterAction.UpdatePassword -> updateState { copy(password = action.password, passwordError = null) }
            is RegisterAction.UpdateConfirmPassword -> updateState { copy(confirmPassword = action.confirmPassword, confirmPasswordError = null) }
            RegisterAction.TogglePasswordVisibility -> updateState { copy(isPasswordVisible = !isPasswordVisible) }
            is RegisterAction.ToggleTerms -> updateState { copy(agreedToTerms = action.agreed) }
            RegisterAction.Register -> register()
        }
    }

    private fun register() {
        val nameResult = Validator.validateName(currentState.name.trim())
        val emailResult = Validator.validateEmail(currentState.email.trim())
        val passwordResult = Validator.validatePassword(currentState.password)
        val confirmResult = Validator.validateConfirmPassword(currentState.password, currentState.confirmPassword)

        if (!nameResult.isValid || !emailResult.isValid || !passwordResult.isValid || !confirmResult.isValid) {
            updateState {
                copy(
                    nameError = nameResult.errorMessage,
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    confirmPasswordError = confirmResult.errorMessage,
                )
            }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            when (val result = registerUseCase(
                currentState.name.trim(),
                currentState.email.trim(),
                currentState.password,
            )) {
                is NetworkResult.Success -> {
                    sendEvent(UiEvent.Navigate("account_setup"))
                }
                is NetworkResult.Error -> {
                    sendEvent(UiEvent.ShowSnackbar(result.message))
                }
            }
            updateState { copy(isLoading = false) }
        }
    }
}
