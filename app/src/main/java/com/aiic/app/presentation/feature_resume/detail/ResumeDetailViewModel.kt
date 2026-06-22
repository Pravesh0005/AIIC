package com.aiic.app.presentation.feature_resume.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.usecase.GetResumeDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val resume: Resume) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class ResumeDetailViewModel @Inject constructor(
    private val getResumeDetailsUseCase: GetResumeDetailsUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val resumeId: String = checkNotNull(savedStateHandle["resumeId"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadResumeDetails()
    }

    fun loadResumeDetails() {
        viewModelScope.launch {
            _uiState.update { DetailUiState.Loading }
            when (val result = getResumeDetailsUseCase(resumeId)) {
                is NetworkResult.Success -> {
                    _uiState.update { DetailUiState.Success(result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { DetailUiState.Error(result.message) }
                }
            }
        }
    }
}
