package com.aiic.app.presentation.feature_profile

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.domain.model.UserProfile
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.repository.UserRepository
import com.aiic.app.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountSetupState(
    val targetRole: String = "",
    val targetCompany: String = "",
    val education: String = "",
    val skills: String = "",
    val isLoading: Boolean = false,
)

sealed interface AccountSetupAction {
    data class UpdateRole(val role: String) : AccountSetupAction
    data class UpdateCompany(val company: String) : AccountSetupAction
    data class UpdateEducation(val education: String) : AccountSetupAction
    data class UpdateSkills(val skills: String) : AccountSetupAction
    data object SaveProfile : AccountSetupAction
    data object Skip : AccountSetupAction
}

@HiltViewModel
class AccountSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : BaseViewModel<AccountSetupState, AccountSetupAction>(AccountSetupState()) {

    override fun onAction(action: AccountSetupAction) {
        when (action) {
            is AccountSetupAction.UpdateRole -> updateState { copy(targetRole = action.role) }
            is AccountSetupAction.UpdateCompany -> updateState { copy(targetCompany = action.company) }
            is AccountSetupAction.UpdateEducation -> updateState { copy(education = action.education) }
            is AccountSetupAction.UpdateSkills -> updateState { copy(skills = action.skills) }
            AccountSetupAction.SaveProfile -> saveProfile()
            AccountSetupAction.Skip -> sendEvent(UiEvent.Navigate("home"))
        }
    }

    private fun saveProfile() {
        val uid = authRepository.getCurrentSession()?.uid ?: return
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val updates = buildMap<String, Any> {
                if (currentState.targetRole.isNotBlank()) put("targetRole", currentState.targetRole)
                if (currentState.targetCompany.isNotBlank()) put("targetCompany", currentState.targetCompany)
                if (currentState.education.isNotBlank()) put("education", currentState.education)
                if (currentState.skills.isNotBlank()) {
                    put("skills", currentState.skills.split(",").map { it.trim() }.filter { it.isNotBlank() })
                }
                put("onboardingCompleted", true)
                put("lastActiveAt", System.currentTimeMillis())
            }
            when (val result = updateUserProfileUseCase(uid, updates)) {
                is NetworkResult.Success -> sendEvent(UiEvent.Navigate("home"))
                is NetworkResult.Error -> sendEvent(UiEvent.ShowSnackbar(result.message))
            }
            updateState { copy(isLoading = false) }
        }
    }
}
