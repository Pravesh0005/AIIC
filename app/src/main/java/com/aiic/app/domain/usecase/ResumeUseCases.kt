package com.aiic.app.domain.usecase

import android.net.Uri
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.model.ResumeConstraints
import com.aiic.app.domain.model.UploadProgress
import com.aiic.app.domain.repository.ResumeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadResumeUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    operator fun invoke(
        userId: String,
        resumeId: String,
        fileName: String,
        fileUri: Uri,
        fileSize: Long,
    ): Flow<NetworkResult<UploadProgress>> {
        return resumeRepository.uploadResume(userId, resumeId, fileName, fileUri, fileSize)
    }

    fun validateFile(fileName: String, fileSize: Long, mimeType: String?): FileValidationResult {
        val extension = fileName.substringAfterLast('.', "").lowercase()

        if (extension !in ResumeConstraints.ALLOWED_EXTENSIONS) {
            return FileValidationResult.InvalidType(extension)
        }
        if (mimeType != null && mimeType !in ResumeConstraints.ALLOWED_MIME_TYPES) {
            return FileValidationResult.InvalidType(mimeType)
        }
        if (fileSize > ResumeConstraints.MAX_FILE_SIZE_BYTES) {
            return FileValidationResult.TooLarge(fileSize, ResumeConstraints.MAX_FILE_SIZE_BYTES)
        }
        if (fileSize <= 0) {
            return FileValidationResult.Empty
        }
        return FileValidationResult.Valid
    }
}

sealed interface FileValidationResult {
    data object Valid : FileValidationResult
    data object Empty : FileValidationResult
    data class InvalidType(val type: String) : FileValidationResult
    data class TooLarge(val actual: Long, val limit: Long) : FileValidationResult
}

class DeleteResumeUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    suspend operator fun invoke(
        resumeId: String,
        userId: String,
        version: Int,
    ): NetworkResult<Unit> =
        resumeRepository.deleteResume(resumeId, userId, version)
}

class GetResumeHistoryUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    suspend operator fun invoke(userId: String): NetworkResult<List<Resume>> =
        resumeRepository.getResumeHistory(userId)
}

class GetLatestResumeUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    suspend operator fun invoke(userId: String): NetworkResult<Resume?> =
        resumeRepository.getLatestResume(userId)
}

class GetResumeDetailsUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    suspend operator fun invoke(resumeId: String): NetworkResult<Resume> =
        resumeRepository.getResumeById(resumeId)
}

class UpdateResumeUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    suspend operator fun invoke(
        resumeId: String,
        updates: Map<String, Any>,
    ): NetworkResult<Unit> =
        resumeRepository.updateResumeMetadata(resumeId, updates)
}

class SetActiveResumeUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    suspend operator fun invoke(userId: String, resumeId: String): NetworkResult<Unit> =
        resumeRepository.setActiveResume(userId, resumeId)
}

class ObserveResumeUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    operator fun invoke(userId: String): Flow<List<Resume>> =
        resumeRepository.observeResumeHistory(userId)
}

class CancelUploadUseCase @Inject constructor(
    private val resumeRepository: ResumeRepository,
) {
    operator fun invoke() = resumeRepository.cancelUpload()
}
