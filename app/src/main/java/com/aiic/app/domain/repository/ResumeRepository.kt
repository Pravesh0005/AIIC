package com.aiic.app.domain.repository

import android.net.Uri
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.model.UploadProgress
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for the Resume Platform.
 * All implementations must follow the Remote → Cache → UI data flow.
 * Firebase is never called directly from the presentation layer.
 */
interface ResumeRepository {

    /**
     * Uploads a PDF to Firebase Storage and creates a Firestore metadata document.
     * Automatically increments the version number based on existing uploads.
     * Returns a Flow of [UploadProgress] during the upload, then emits the final [Resume].
     */
    fun uploadResume(
        userId: String,
        resumeId: String,
        fileName: String,
        fileUri: Uri,
        fileSize: Long,
    ): Flow<NetworkResult<UploadProgress>>

    /**
     * Completes the upload by creating the Firestore metadata document.
     * Called internally after the storage upload succeeds.
     */
    suspend fun createResumeMetadata(resume: Resume): NetworkResult<Resume>

    /**
     * Retrieves all resume versions for a given user, ordered by version descending.
     */
    suspend fun getResumeHistory(userId: String): NetworkResult<List<Resume>>

    /**
     * Observes resume history in real-time via Firestore snapshots.
     */
    fun observeResumeHistory(userId: String): Flow<List<Resume>>

    /**
     * Retrieves the latest (highest version) resume for a user.
     */
    suspend fun getLatestResume(userId: String): NetworkResult<Resume?>

    /**
     * Retrieves a specific resume by its ID.
     */
    suspend fun getResumeById(resumeId: String): NetworkResult<Resume>

    /**
     * Sets a specific resume as the active resume.
     * Deactivates all other resumes for that user.
     */
    suspend fun setActiveResume(userId: String, resumeId: String): NetworkResult<Unit>

    /**
     * Retrieves the currently active resume for a user.
     */
    suspend fun getActiveResume(userId: String): NetworkResult<Resume?>

    /**
     * Soft-deletes a resume version. Removes from Storage and Firestore.
     */
    suspend fun deleteResume(resumeId: String, userId: String, version: Int): NetworkResult<Unit>

    /**
     * Updates mutable resume metadata fields (e.g. analysisStatus, processingState).
     */
    suspend fun updateResumeMetadata(
        resumeId: String,
        updates: Map<String, Any>,
    ): NetworkResult<Unit>

    /**
     * Returns the next version number for a user's resume uploads.
     */
    suspend fun getNextVersionNumber(userId: String): Int

    /**
     * Cancels an ongoing upload, if supported by the storage backend.
     */
    fun cancelUpload()
}
