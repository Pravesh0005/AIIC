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
import kotlinx.coroutines.withTimeoutOrNull
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
        if (sessionId.isBlank()) {
            _uiState.value = SessionSummaryUiState.Error("Invalid session")
            return
        }

        _uiState.value = SessionSummaryUiState.Loading
        viewModelScope.launch {
            // 20 second timeout — never hang forever
            val result = withTimeoutOrNull(20000L) {
                getSessionSummaryUseCase(sessionId)
            }

            when {
                result == null -> {
                    _uiState.value = SessionSummaryUiState.Error(
                        "Analysis timed out. Your answers are saved — tap Retry to try again."
                    )
                }
                result is NetworkResult.Success -> {
                    _uiState.value = SessionSummaryUiState.Success(result.data!!)
                }
                result is NetworkResult.Error -> {
                    _uiState.value = SessionSummaryUiState.Error(
                        "Your answers are saved but the summary couldn't be generated. Tap Retry."
                    )
                }
            }
        }
    }
}
