package com.aiic.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiic.app.domain.model.AnalysisStatus
import com.aiic.app.domain.model.ProcessingState
import com.aiic.app.domain.model.Resume

/**
 * Room Entity for Resume caching.
 * Prepared for future offline-first architecture.
 */
@Entity(tableName = "resumes")
data class ResumeEntity(
    @PrimaryKey
    val resumeId: String,
    val userId: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
    val uploadDate: Long,
    val lastUpdated: Long,
    val resumeVersion: Int,
    val activeResume: Boolean,
    val analysisStatus: String,
    val processingState: String,
)

fun ResumeEntity.toDomain(): Resume = Resume(
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

fun Resume.toEntity(): ResumeEntity = ResumeEntity(
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
