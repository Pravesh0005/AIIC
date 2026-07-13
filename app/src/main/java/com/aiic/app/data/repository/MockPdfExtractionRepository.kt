package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.repository.PdfExtractionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockPdfExtractionRepository @Inject constructor() : PdfExtractionRepository {

    override suspend fun extractTextFromPdf(userId: String, resumeId: String): NetworkResult<String> {
        
        delay(1500)
        
        val mockExtractedText = """
            John Doe
            Software Engineer
            Skills: Kotlin, Android, Jetpack Compose, Firebase, Coroutines, MVVM
            Experience:
            - Senior Android Developer at TechCorp (2020-2023)
              Led the migration of the core app to Jetpack Compose.
              Reduced app load time by 30%.
            - Android Developer at StartupInc (2018-2020)
              Maintained legacy Java codebase and introduced Kotlin.
            Education:
            B.S. in Computer Science, University of Technology (2014-2018)
        """.trimIndent()

        return NetworkResult.Success(mockExtractedText)
    }
}
