package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.ResumeAnalysis

interface GenerativeAiRepository {
    
    /**
     * Analyzes raw text using the Intelligence Engine prompt system to return a structured ResumeAnalysis.
     * This abstracts the underlying generative AI implementation (e.g. Gemini).
     */
    suspend fun generateResumeAnalysis(
        userId: String,
        resumeId: String,
        rawText: String
    ): NetworkResult<ResumeAnalysis>
}
