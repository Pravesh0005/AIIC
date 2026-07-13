package com.aiic.app.domain.model

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

enum class AnalysisStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    SKIPPED,
}

enum class ProcessingState {
    IDLE,
    UPLOADING,
    UPLOADED,
    SYNCING,
    PROCESSING,
    READY,
    ERROR,
}

data class UploadProgress(
    val bytesTransferred: Long = 0L,
    val totalBytes: Long = 0L,
) {
    val percentage: Int
        get() = if (totalBytes > 0) ((bytesTransferred * 100) / totalBytes).toInt() else 0

    val isComplete: Boolean
        get() = totalBytes > 0 && bytesTransferred >= totalBytes
}

object ResumeConstraints {
    const val MAX_FILE_SIZE_BYTES: Long = 10 * 1024 * 1024 
    const val MAX_FILE_SIZE_MB: Int = 10
    val ALLOWED_MIME_TYPES = setOf("application/pdf")
    val ALLOWED_EXTENSIONS = setOf("pdf")
}
