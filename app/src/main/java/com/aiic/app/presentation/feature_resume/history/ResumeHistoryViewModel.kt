package com.aiic.app.presentation.feature_resume.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.repository.SessionRepository
import com.aiic.app.domain.usecase.DeleteResumeUseCase
import com.aiic.app.domain.usecase.ObserveResumeUseCase
import com.aiic.app.domain.usecase.SetActiveResumeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(val resumes: List<Resume>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

@HiltViewModel
class ResumeHistoryViewModel @Inject constructor(
    private val observeResumeUseCase: ObserveResumeUseCase,
    private val deleteResumeUseCase: DeleteResumeUseCase,
    private val setActiveResumeUseCase: SetActiveResumeUseCase,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        observeResumes()
    }

    private fun observeResumes() {
        val uid = sessionRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            observeResumeUseCase(uid).collectLatest { resumes ->
                _uiState.update { HistoryUiState.Success(resumes) }
            }
        }
    }

    fun setActiveResume(resumeId: String) {
        val uid = sessionRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            setActiveResumeUseCase(uid, resumeId)
        }
    }

    fun deleteResume(resumeId: String, version: Int) {
        val uid = sessionRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            deleteResumeUseCase(resumeId, uid, version)
        }
    }
}
