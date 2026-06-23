package com.aiic.app.presentation.feature_profile

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.usecase.GetCurrentUserUseCase
import com.aiic.app.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileState(
    val name: String = "",
    val gender: String = "",
    val targetRole: String = "",
    val targetCompany: String = "",
    val education: String = "",
    val skills: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
)

sealed interface EditProfileAction {
    data class UpdateName(val name: String) : EditProfileAction
    data class UpdateGender(val gender: String) : EditProfileAction
    data class UpdateRole(val role: String) : EditProfileAction
    data class UpdateCompany(val company: String) : EditProfileAction
    data class UpdateEducation(val education: String) : EditProfileAction
    data class UpdateSkills(val skills: String) : EditProfileAction
    data object SaveProfile : EditProfileAction
    data object Cancel : EditProfileAction
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : BaseViewModel<EditProfileState, EditProfileAction>(EditProfileState()) {

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val uid = authRepository.getCurrentSession()?.uid ?: return
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            when (val result = getCurrentUserUseCase(uid)) {
                is NetworkResult.Success -> {
                    val profile = result.data
                    updateState {
                        copy(
                            name = profile.name,
                            gender = profile.gender,
                            targetRole = profile.targetRole,
                            targetCompany = profile.targetCompany,
                            education = profile.education,
                            skills = profile.skills.joinToString(", "),
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    sendEvent(UiEvent.ShowSnackbar(result.message))
                    updateState { copy(isLoading = false) }
                }
            }
        }
    }

    override fun onAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.UpdateName -> updateState { copy(name = action.name) }
            is EditProfileAction.UpdateGender -> updateState { copy(gender = action.gender) }
            is EditProfileAction.UpdateRole -> updateState { copy(targetRole = action.role) }
            is EditProfileAction.UpdateCompany -> updateState { copy(targetCompany = action.company) }
            is EditProfileAction.UpdateEducation -> updateState { copy(education = action.education) }
            is EditProfileAction.UpdateSkills -> updateState { copy(skills = action.skills) }
            EditProfileAction.SaveProfile -> saveProfile()
            EditProfileAction.Cancel -> sendEvent(UiEvent.Navigate("back"))
        }
    }

    private fun saveProfile() {
        val uid = authRepository.getCurrentSession()?.uid ?: return
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            val updates = buildMap<String, Any> {
                put("name", currentState.name)
                put("gender", currentState.gender)
                put("targetRole", currentState.targetRole)
                put("targetCompany", currentState.targetCompany)
                put("education", currentState.education)
                put("skills", currentState.skills.split(",").map { it.trim() }.filter { it.isNotBlank() })
                put("lastActiveAt", System.currentTimeMillis())
            }
            when (val result = updateUserProfileUseCase(uid, updates)) {
                is NetworkResult.Success -> {
                    sendEvent(UiEvent.ShowSnackbar("Profile updated successfully"))
                    sendEvent(UiEvent.Navigate("back"))
                }
                is NetworkResult.Error -> sendEvent(UiEvent.ShowSnackbar(result.message))
            }
            updateState { copy(isSaving = false) }
        }
    }
}
