package com.aiic.app.presentation.feature_resume.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.model.UploadProgress
import com.aiic.app.domain.repository.ResumeRepository
import com.aiic.app.domain.repository.SessionRepository
import com.aiic.app.domain.usecase.CancelUploadUseCase
import com.aiic.app.domain.usecase.FileValidationResult
import com.aiic.app.domain.usecase.UploadResumeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface UploadUiState {
    data object Idle : UploadUiState
    data object Validating : UploadUiState
    data class Uploading(val progress: UploadProgress) : UploadUiState
    data object Syncing : UploadUiState
    data class Success(val resume: Resume) : UploadUiState
    data class Error(val message: String) : UploadUiState
}

@HiltViewModel
class ResumeUploadViewModel @Inject constructor(
    private val uploadResumeUseCase: UploadResumeUseCase,
    private val cancelUploadUseCase: CancelUploadUseCase,
    private val resumeRepository: ResumeRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun uploadFile(uri: Uri, fileName: String, fileSize: Long, mimeType: String?) {
        val userId = sessionRepository.getCurrentUserId()
        if (userId == null) {
            _uiState.update { UploadUiState.Error("User not logged in.") }
            return
        }

        _uiState.update { UploadUiState.Validating }
        val validation = uploadResumeUseCase.validateFile(fileName, fileSize, mimeType)
        
        when (validation) {
            is FileValidationResult.InvalidType -> {
                _uiState.update { UploadUiState.Error("Invalid file type. Only PDF is allowed.") }
                return
            }
            is FileValidationResult.TooLarge -> {
                _uiState.update { UploadUiState.Error("File too large. Maximum size is 10 MB.") }
                return
            }
            is FileValidationResult.Empty -> {
                _uiState.update { UploadUiState.Error("File is empty.") }
                return
            }
            is FileValidationResult.Valid -> {}
        }

        viewModelScope.launch {
            _uiState.update { UploadUiState.Uploading(UploadProgress(0, fileSize)) }
            
            uploadResumeUseCase(userId, fileName, uri, fileSize).collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val progress = result.data
                        if (progress.isComplete) {
                            finalizeUpload(userId, fileName, fileSize)
                        } else {
                            _uiState.update { UploadUiState.Uploading(progress) }
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update { UploadUiState.Error(result.message) }
                    }
                }
            }
        }
    }

    private fun finalizeUpload(userId: String, fileName: String, fileSize: Long) {
        _uiState.update { UploadUiState.Syncing }
        viewModelScope.launch {
            val nextVersion = resumeRepository.getNextVersionNumber(userId)
            val newResume = Resume(
                resumeId = UUID.randomUUID().toString(),
                userId = userId,
                fileName = fileName,
                fileSize = fileSize,
                resumeVersion = nextVersion,
                activeResume = nextVersion == 1 // Automatically set to active if it's the first resume
            )
            
            when (val result = resumeRepository.createResumeMetadata(newResume)) {
                is NetworkResult.Success -> {
                    if (newResume.activeResume) {
                        resumeRepository.setActiveResume(userId, newResume.resumeId)
                    }
                    _uiState.update { UploadUiState.Success(result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { UploadUiState.Error(result.message) }
                }
            }
        }
    }

    fun cancelUpload() {
        cancelUploadUseCase()
        _uiState.update { UploadUiState.Idle }
    }
    
    fun resetState() {
        _uiState.update { UploadUiState.Idle }
    }
}
