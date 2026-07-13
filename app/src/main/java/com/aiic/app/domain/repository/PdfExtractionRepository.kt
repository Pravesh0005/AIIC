package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult

interface PdfExtractionRepository {
    
    suspend fun extractTextFromPdf(userId: String, resumeId: String): NetworkResult<String>
}
