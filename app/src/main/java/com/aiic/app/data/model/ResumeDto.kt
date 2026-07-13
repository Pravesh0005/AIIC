package com.aiic.app.data.model

import com.aiic.app.domain.model.AnalysisStatus
import com.aiic.app.domain.model.ProcessingState
import com.aiic.app.domain.model.Resume

data class ResumeDto(
    val resumeId: String = "",
    val userId: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val fileSize: Long = 0L,
    val uploadDate: Long = 0L,
    val lastUpdated: Long = 0L,
    val resumeVersion: Int = 1,
    val activeResume: Boolean = false,
    val analysisStatus: String = AnalysisStatus.PENDING.name,
    val processingState: String = ProcessingState.IDLE.name,
) {
    fun toDomain(): Resume = Resume(
        resumeId = resumeId,
        userId = userId,
        fileName = fileName,
        fileUrl = fileUrl,
        fileSize = fileSize,
        uploadDate = uploadDate,
        lastUpdated = lastUpdated,
        resumeVersion = resumeVersion,
        activeResume = activeResume,
        analysisStatus = try { AnalysisStatus.valueOf(analysisStatus) } catch (e: Exception) { AnalysisStatus.PENDING },
        processingState = try { ProcessingState.valueOf(processingState) } catch (e: Exception) { ProcessingState.IDLE },
    )
}

fun Resume.toDto(): ResumeDto = ResumeDto(
    resumeId = resumeId,
    userId = userId,
    fileName = fileName,
    fileUrl = fileUrl,
    fileSize = fileSize,
    uploadDate = uploadDate,
    lastUpdated = lastUpdated,
    resumeVersion = resumeVersion,
    activeResume = activeResume,
    analysisStatus = analysisStatus.name,
    processingState = processingState.name,
)

fun Resume.toMap(): Map<String, Any> = mapOf(
    "resumeId" to resumeId,
    "userId" to userId,
    "fileName" to fileName,
    "fileUrl" to fileUrl,
    "fileSize" to fileSize,
    "uploadDate" to uploadDate,
    "lastUpdated" to lastUpdated,
    "resumeVersion" to resumeVersion,
    "activeResume" to activeResume,
    "analysisStatus" to analysisStatus.name,
    "processingState" to processingState.name,
)
