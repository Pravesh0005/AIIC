package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.ResumeAnalysis

interface GenerativeAiRepository {
    
    suspend fun generateResumeAnalysis(
        userId: String,
        resumeId: String,
        rawText: String
    ): NetworkResult<ResumeAnalysis>

    suspend fun generateText(prompt: String): NetworkResult<String>
    
    suspend fun generateJson(prompt: String): NetworkResult<String>
}
