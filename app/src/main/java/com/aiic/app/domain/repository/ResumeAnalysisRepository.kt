package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.ResumeAnalysis
import kotlinx.coroutines.flow.Flow

interface ResumeAnalysisRepository {
    
    /**
     * Retrieves an existing resume analysis from Firestore.
     */
    suspend fun getAnalysis(userId: String, resumeId: String): NetworkResult<ResumeAnalysis>
    
    /**
     * Saves a newly generated resume analysis to Firestore.
     */
    suspend fun saveAnalysis(analysis: ResumeAnalysis): NetworkResult<Unit>
    
    /**
     * Observes all analyses for a given user. Useful for history/dashboard views.
     */
    fun observeUserAnalyses(userId: String): Flow<List<ResumeAnalysis>>
}
