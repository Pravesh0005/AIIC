package com.aiic.app.presentation.feature_auth.forgot_password

import androidx.lifecycle.viewModelScope
import com.aiic.app.common.extensions.isValidEmail
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.UiEvent
import com.aiic.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val isSuccess: Boolean = false,
)

sealed interface ForgotPasswordAction {
    data class UpdateEmail(val email: String) : ForgotPasswordAction
    data object SendReset : ForgotPasswordAction
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<ForgotPasswordState, ForgotPasswordAction>(ForgotPasswordState()) {

    override fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.UpdateEmail -> updateState {
                copy(email = action.email, emailError = null)
            }
            ForgotPasswordAction.SendReset -> sendReset()
        }
    }

    private fun sendReset() {
        val email = currentState.email.trim()
        if (!email.isValidEmail()) {
            updateState { copy(emailError = "Please enter a valid email") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            authRepository.sendPasswordReset(email)
                .onSuccess {
                    updateState { copy(isSuccess = true) }
                }
                .onFailure {
                    sendEvent(UiEvent.ShowSnackbar(it.message ?: "Failed to send reset email"))
                }
            updateState { copy(isLoading = false) }
        }
    }
}
