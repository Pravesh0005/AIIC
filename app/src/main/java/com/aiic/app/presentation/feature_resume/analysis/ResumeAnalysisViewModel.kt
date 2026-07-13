package com.aiic.app.presentation.feature_resume.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.usecase.analysis.AnalyzeResumeUseCase
import com.aiic.app.domain.usecase.analysis.GetResumeAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AnalysisUiState {
    data object Idle : AnalysisUiState
    data object Analyzing : AnalysisUiState
    data class Success(val analysis: ResumeAnalysis) : AnalysisUiState
    data class Failed(val message: String) : AnalysisUiState
    data object NoResume : AnalysisUiState
    data object NoAnalysis : AnalysisUiState
    data object Retrying : AnalysisUiState
}

@HiltViewModel
class ResumeAnalysisViewModel @Inject constructor(
    private val analyzeResumeUseCase: AnalyzeResumeUseCase,
    private val getResumeAnalysisUseCase: GetResumeAnalysisUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    fun loadAnalysis(resumeId: String?) {
        val userId = authRepository.getCurrentSession()?.uid
        if (userId == null) {
            _uiState.update { AnalysisUiState.Failed("User not logged in.") }
            return
        }

        if (resumeId == null) {
            _uiState.update { AnalysisUiState.NoResume }
            return
        }

        val current = _uiState.value
        if (current is AnalysisUiState.Success && current.analysis.resumeId == resumeId) {
            android.util.Log.d("AIIC_ANALYSIS", "loadAnalysis: Already loaded for resumeId=$resumeId, skipping")
            return
        }

        _uiState.update { AnalysisUiState.Retrying }

        viewModelScope.launch {
            android.util.Log.d("AIIC_ANALYSIS", "loadAnalysis: Checking Firestore for userId=$userId, resumeId=$resumeId")
            
            when (val existingResult = getResumeAnalysisUseCase(userId, resumeId)) {
                is NetworkResult.Success -> {
                    android.util.Log.d("AIIC_ANALYSIS", "loadAnalysis: Found existing analysis, score=${existingResult.data.overallScore}")
                    _uiState.update { AnalysisUiState.Success(existingResult.data) }
                }
                is NetworkResult.Error -> {
                    android.util.Log.d("AIIC_ANALYSIS", "loadAnalysis: No existing analysis found (${existingResult.message}), running pipeline")
                    
                    _uiState.update { AnalysisUiState.Analyzing }
                    runAnalysisPipeline(userId, resumeId)
                }
            }
        }
    }

    private suspend fun runAnalysisPipeline(userId: String, resumeId: String) {
        _uiState.update { AnalysisUiState.Analyzing }
        android.util.Log.d("AIIC_ANALYSIS", "runAnalysisPipeline: Starting for userId=$userId, resumeId=$resumeId")
        when (val result = analyzeResumeUseCase(userId, resumeId)) {
            is NetworkResult.Success -> {
                android.util.Log.d("AIIC_ANALYSIS", "runAnalysisPipeline: Success, score=${result.data.overallScore}")
                _uiState.update { AnalysisUiState.Success(result.data) }
            }
            is NetworkResult.Error -> {
                android.util.Log.e("AIIC_ANALYSIS", "runAnalysisPipeline: Failed: ${result.message}")
                _uiState.update { AnalysisUiState.Failed(result.message) }
            }
        }
    }
    
    fun forceReanalyze(resumeId: String) {
        val userId = authRepository.getCurrentSession()?.uid ?: return
        _uiState.update { AnalysisUiState.Retrying }
        viewModelScope.launch {
            runAnalysisPipeline(userId, resumeId)
        }
    }
}
