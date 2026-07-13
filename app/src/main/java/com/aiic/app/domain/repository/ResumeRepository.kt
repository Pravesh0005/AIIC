package com.aiic.app.domain.repository

import android.net.Uri
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.model.UploadProgress
import kotlinx.coroutines.flow.Flow

interface ResumeRepository {

    fun uploadResume(
        userId: String,
        resumeId: String,
        fileName: String,
        fileUri: Uri,
        fileSize: Long,
    ): Flow<NetworkResult<UploadProgress>>

    suspend fun createResumeMetadata(resume: Resume): NetworkResult<Resume>

    suspend fun getResumeHistory(userId: String): NetworkResult<List<Resume>>

    fun observeResumeHistory(userId: String): Flow<List<Resume>>

    suspend fun getLatestResume(userId: String): NetworkResult<Resume?>

    suspend fun getResumeById(resumeId: String): NetworkResult<Resume>

    suspend fun setActiveResume(userId: String, resumeId: String): NetworkResult<Unit>

    suspend fun getActiveResume(userId: String): NetworkResult<Resume?>

    suspend fun deleteResume(resumeId: String, userId: String, version: Int): NetworkResult<Unit>

    suspend fun updateResumeMetadata(
        resumeId: String,
        updates: Map<String, Any>,
    ): NetworkResult<Unit>

    suspend fun getNextVersionNumber(userId: String): Int

    fun cancelUpload()
}
