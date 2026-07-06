package com.aiic.app.presentation.feature_interview.setup

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.UiEvent
import com.aiic.app.domain.model.InterviewConfig
import com.aiic.app.domain.model.InterviewDifficulty
import com.aiic.app.domain.model.InterviewMode
import com.aiic.app.domain.model.InterviewType
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.usecase.SetupInterviewSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterviewSetupState(
    val selectedRole: String = "Android Developer",
    val selectedType: InterviewType = InterviewType.MIXED,
    val selectedDifficulty: InterviewDifficulty = InterviewDifficulty.MEDIUM,
    val selectedQuestionCount: Int = 5,
    val selectedMode: InterviewMode = InterviewMode.TEXT,
    val targetCompany: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface InterviewSetupAction {
    data class UpdateRole(val role: String) : InterviewSetupAction
    data class UpdateType(val type: InterviewType) : InterviewSetupAction
    data class UpdateDifficulty(val difficulty: InterviewDifficulty) : InterviewSetupAction
    data class UpdateQuestionCount(val count: Int) : InterviewSetupAction
    data class UpdateMode(val mode: InterviewMode) : InterviewSetupAction
    data class UpdateCompany(val company: String) : InterviewSetupAction
    data object StartInterview : InterviewSetupAction
}

@HiltViewModel
class InterviewSetupViewModel @Inject constructor(
    private val setupInterviewSessionUseCase: SetupInterviewSessionUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel<InterviewSetupState, InterviewSetupAction>(InterviewSetupState()) {

    override fun onAction(action: InterviewSetupAction) {
        when (action) {
            is InterviewSetupAction.UpdateRole -> updateState { copy(selectedRole = action.role) }
            is InterviewSetupAction.UpdateType -> updateState { copy(selectedType = action.type) }
            is InterviewSetupAction.UpdateDifficulty -> updateState { copy(selectedDifficulty = action.difficulty) }
            is InterviewSetupAction.UpdateQuestionCount -> updateState { copy(selectedQuestionCount = action.count) }
            is InterviewSetupAction.UpdateMode -> updateState { copy(selectedMode = action.mode) }
            is InterviewSetupAction.UpdateCompany -> updateState { copy(targetCompany = action.company) }
            InterviewSetupAction.StartInterview -> startInterview()
        }
    }

    private fun startInterview() {
        val user = authRepository.getCurrentSession()
        if (user == null) {
            updateState { copy(error = "User not authenticated") }
            return
        }

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val config = InterviewConfig(
                role = currentState.selectedRole,
                interviewType = currentState.selectedType,
                difficulty = currentState.selectedDifficulty,
                questionCount = currentState.selectedQuestionCount,
                interviewMode = currentState.selectedMode,
                resumeId = null,
                targetCompany = currentState.targetCompany.takeIf { it.isNotBlank() }
            )

            when (val result = setupInterviewSessionUseCase(config, user.uid)) {
                is com.aiic.app.core.base.NetworkResult.Success -> {
                    updateState { copy(isLoading = false) }
                    val session = result.data.first
                    sendEvent(UiEvent.Navigate("interview_session/${session.sessionId}"))
                }
                is com.aiic.app.core.base.NetworkResult.Error -> {
                    updateState { copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }
}
