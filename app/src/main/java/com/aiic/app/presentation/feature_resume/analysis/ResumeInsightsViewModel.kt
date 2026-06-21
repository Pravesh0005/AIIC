package com.aiic.app.presentation.feature_resume.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.ResumeAnalysisRepository
import com.aiic.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface InsightsUiState {
    data object Loading : InsightsUiState
    data class Success(val history: List<ResumeAnalysis>) : InsightsUiState
    data class Error(val message: String) : InsightsUiState
}

@HiltViewModel
class ResumeInsightsViewModel @Inject constructor(
    private val analysisRepository: ResumeAnalysisRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InsightsUiState>(InsightsUiState.Loading)
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        val userId = sessionRepository.getCurrentUserId()
        if (userId == null) {
            _uiState.update { InsightsUiState.Error("User not logged in.") }
            return
        }

        viewModelScope.launch {
            analysisRepository.observeUserAnalyses(userId).collectLatest { analyses ->
                _uiState.update { InsightsUiState.Success(analyses) }
            }
        }
    }
}
