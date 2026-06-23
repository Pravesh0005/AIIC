package com.aiic.app.presentation.feature_feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.repository.FeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedbackUiState {
    object Idle : FeedbackUiState()
    object Loading : FeedbackUiState()
    data class Success(val feedback: AnswerFeedback) : FeedbackUiState()
    data class Error(val message: String) : FeedbackUiState()
}

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedbackUiState>(FeedbackUiState.Idle)
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun loadFeedbackForAnswer(answerId: String) {
        _uiState.value = FeedbackUiState.Loading
        viewModelScope.launch {
            when (val result = feedbackRepository.getFeedbackForAnswer(answerId)) {
                is NetworkResult.Success -> {
                    _uiState.value = FeedbackUiState.Success(result.data!!)
                }
                is NetworkResult.Error -> {
                    _uiState.value = FeedbackUiState.Error(result.message ?: "Failed to load feedback")
                }
            }
        }
    }
}
