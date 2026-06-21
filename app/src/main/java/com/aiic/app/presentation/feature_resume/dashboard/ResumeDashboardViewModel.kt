package com.aiic.app.presentation.feature_resume.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.usecase.GetLatestResumeUseCase
import com.aiic.app.domain.usecase.ObserveResumeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val activeResume: Resume?, val latestResume: Resume?) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class ResumeDashboardViewModel @Inject constructor(
    private val observeResumeUseCase: ObserveResumeUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeResumes()
    }

    private fun observeResumes() {
        val uid = authRepository.getCurrentSession()?.uid ?: return
        
        viewModelScope.launch {
            observeResumeUseCase(uid).collectLatest { resumes ->
                val active = resumes.firstOrNull { it.activeResume }
                val latest = resumes.maxByOrNull { it.resumeVersion }
                _uiState.update { DashboardUiState.Success(activeResume = active, latestResume = latest) }
            }
        }
    }
}
