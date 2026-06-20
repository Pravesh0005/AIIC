package com.aiic.app.presentation.feature_auth.forgot_password

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.core.validation.Validator
import com.aiic.app.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val isResetSent: Boolean = false,
)

sealed interface ForgotPasswordAction {
    data class UpdateEmail(val email: String) : ForgotPasswordAction
    data object ResetPassword : ForgotPasswordAction
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : BaseViewModel<ForgotPasswordState, ForgotPasswordAction>(ForgotPasswordState()) {

    override fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.UpdateEmail -> updateState {
                copy(email = action.email, emailError = null)
            }
            ForgotPasswordAction.ResetPassword -> resetPassword()
        }
    }

    private fun resetPassword() {
        val emailResult = Validator.validateEmail(currentState.email.trim())
        if (!emailResult.isValid) {
            updateState { copy(emailError = emailResult.errorMessage) }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            when (val result = resetPasswordUseCase(currentState.email.trim())) {
                is NetworkResult.Success -> {
                    updateState { copy(isResetSent = true) }
                    sendEvent(UiEvent.ShowSnackbar("Password reset email sent"))
                }
                is NetworkResult.Error -> {
                    sendEvent(UiEvent.ShowSnackbar(result.message))
                }
            }
            updateState { copy(isLoading = false) }
        }
    }
}
