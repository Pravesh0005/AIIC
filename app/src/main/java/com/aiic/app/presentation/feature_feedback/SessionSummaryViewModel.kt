package com.aiic.app.presentation.feature_feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.SessionSummary
import com.aiic.app.domain.usecase.feedback.GetSessionSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SessionSummaryUiState {
    object Idle : SessionSummaryUiState()
    object Loading : SessionSummaryUiState()
    data class Success(val summary: SessionSummary) : SessionSummaryUiState()
    data class Error(val message: String) : SessionSummaryUiState()
}

@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
    private val getSessionSummaryUseCase: GetSessionSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SessionSummaryUiState>(SessionSummaryUiState.Idle)
    val uiState: StateFlow<SessionSummaryUiState> = _uiState.asStateFlow()

    fun loadSummary(sessionId: String) {
        _uiState.value = SessionSummaryUiState.Loading
        viewModelScope.launch {
            when (val result = getSessionSummaryUseCase(sessionId)) {
                is NetworkResult.Success -> {
                    _uiState.value = SessionSummaryUiState.Success(result.data!!)
                }
                is NetworkResult.Error -> {
                    _uiState.value = SessionSummaryUiState.Error(result.message ?: "Failed to generate summary")
                }
            }
        }
    }
}
