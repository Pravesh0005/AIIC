package com.aiic.app.domain.usecase.analysis

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.repository.PdfExtractionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExtractResumeTextUseCase @Inject constructor(
    private val pdfExtractionRepository: PdfExtractionRepository
) {
    suspend operator fun invoke(userId: String, resumeId: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            pdfExtractionRepository.extractTextFromPdf(userId, resumeId)
        }
    }
}
