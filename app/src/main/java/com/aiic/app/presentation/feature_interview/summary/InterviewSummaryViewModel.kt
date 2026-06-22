package com.aiic.app.presentation.feature_interview.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.domain.model.InterviewSession
import com.aiic.app.domain.repository.InterviewSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterviewSummaryState(
    val sessionId: String = "",
    val isLoading: Boolean = true,
    val session: InterviewSession? = null,
    val error: String? = null
)

sealed interface InterviewSummaryAction {
    data object FinishReview : InterviewSummaryAction
}

@HiltViewModel
class InterviewSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: InterviewSessionRepository,
    private val authRepository: com.aiic.app.domain.repository.AuthRepository
) : BaseViewModel<InterviewSummaryState, InterviewSummaryAction>(InterviewSummaryState()) {

    init {
        val sessionId = savedStateHandle.get<String>("sessionId") ?: ""
        updateState { copy(sessionId = sessionId) }
        loadSessionDetails(sessionId)
    }

    private fun loadSessionDetails(sessionId: String) {
        val user = authRepository.getCurrentSession() ?: return
        
        viewModelScope.launch {
            val result = sessionRepository.getSessionById(sessionId)
            when (result) {
                is com.aiic.app.core.base.NetworkResult.Success -> {
                    updateState { copy(isLoading = false, session = result.data) }
                }
                is com.aiic.app.core.base.NetworkResult.Error -> {
                    updateState { copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    override fun onAction(action: InterviewSummaryAction) {
        when (action) {
            InterviewSummaryAction.FinishReview -> {
                viewModelScope.launch {
                    sendEvent(UiEvent.Navigate("home"))
                }
            }
        }
    }
}
