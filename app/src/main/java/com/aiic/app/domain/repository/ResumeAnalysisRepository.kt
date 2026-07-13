package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.ResumeAnalysis
import kotlinx.coroutines.flow.Flow

interface ResumeAnalysisRepository {
    
    suspend fun getAnalysis(userId: String, resumeId: String): NetworkResult<ResumeAnalysis>
    
    suspend fun saveAnalysis(analysis: ResumeAnalysis): NetworkResult<Unit>
    
    fun observeUserAnalyses(userId: String): Flow<List<ResumeAnalysis>>
}
