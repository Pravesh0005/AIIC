package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult

interface PdfExtractionRepository {
    
    /**
     * Extracts raw text from a given PDF document stored at the specified URI or Path.
     */
    suspend fun extractTextFromPdf(userId: String, resumeId: String): NetworkResult<String>
}
