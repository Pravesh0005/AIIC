package com.aiic.app.domain.model

/**
 * Core domain model for a Resume.
 * Each upload creates a new version — resumes are never overwritten.
 * Future-proofed for ATS analysis, skill extraction, and AI feedback.
 */
data class Resume(
    val resumeId: String = "",
    val userId: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val fileSize: Long = 0L,
    val uploadDate: Long = 0L,
    val lastUpdated: Long = 0L,
    val resumeVersion: Int = 1,
    val activeResume: Boolean = false,
    val analysisStatus: AnalysisStatus = AnalysisStatus.PENDING,
    val processingState: ProcessingState = ProcessingState.IDLE,
)

/**
 * Tracks the lifecycle of AI analysis on a resume.
 * Kept as a separate enum so future milestones (ATS, Skill Extraction)
 * can extend without modifying the Resume model.
 */
enum class AnalysisStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    SKIPPED,
}

/**
 * Tracks the real-time processing pipeline state.
 * Covers upload, sync, and future analysis operations.
 */
enum class ProcessingState {
    IDLE,
    UPLOADING,
    UPLOADED,
    SYNCING,
    PROCESSING,
    READY,
    ERROR,
}

/**
 * Represents upload progress as a first-class domain concept.
 * Decoupled from Firebase so any storage backend can report progress.
 */
data class UploadProgress(
    val bytesTransferred: Long = 0L,
    val totalBytes: Long = 0L,
) {
    val percentage: Int
        get() = if (totalBytes > 0) ((bytesTransferred * 100) / totalBytes).toInt() else 0

    val isComplete: Boolean
        get() = totalBytes > 0 && bytesTransferred >= totalBytes
}

/**
 * File validation constraints.
 * Centralised so both UI and data layer reference the same limits.
 */
object ResumeConstraints {
    const val MAX_FILE_SIZE_BYTES: Long = 10 * 1024 * 1024 // 10 MB
    const val MAX_FILE_SIZE_MB: Int = 10
    val ALLOWED_MIME_TYPES = setOf("application/pdf")
    val ALLOWED_EXTENSIONS = setOf("pdf")
}
