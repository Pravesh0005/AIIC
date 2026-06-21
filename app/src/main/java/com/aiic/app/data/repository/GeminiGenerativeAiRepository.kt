package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.ai.prompt.ResumeAnalysisPromptBuilder
import com.aiic.app.domain.model.AtsScoreDetails
import com.aiic.app.domain.model.Recommendation
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.GenerativeAiRepository
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

class GeminiGenerativeAiRepository @Inject constructor() : GenerativeAiRepository {

    override suspend fun generateResumeAnalysis(
        userId: String,
        resumeId: String,
        rawText: String
    ): NetworkResult<ResumeAnalysis> {
        // Here we would configure the Google AI SDK / Vertex AI
        val promptBuilder = ResumeAnalysisPromptBuilder()
            .setRawResumeText(rawText)
            .includeJsonSchema(true)

        val systemPrompt = promptBuilder.buildSystemPrompt()
        val userPrompt = promptBuilder.buildUserPrompt()

        // Simulate AI processing latency
        delay(3000)

        // Stubbed structured JSON response mimicking the exact output expected from the LLM
        val mockAnalysis = ResumeAnalysis(
            analysisId = UUID.randomUUID().toString(),
            resumeId = resumeId,
            userId = userId,
            timestamp = System.currentTimeMillis(),
            overallScore = 82,
            atsScoreDetails = AtsScoreDetails(
                skillsScore = 85,
                projectScore = 70,
                experienceScore = 90,
                keywordScore = 80,
                structureScore = 88,
                completenessScore = 80
            ),
            profileSummary = "Strong mid-to-senior level Android developer with significant experience in modern frameworks like Compose and Kotlin. Lacks explicit cloud backend skills.",
            strengths = listOf("Deep Android knowledge", "Modern stack adoption (Compose, Coroutines)", "Proven track record of performance optimization"),
            weaknesses = listOf("No mention of testing frameworks (JUnit, Espresso)", "Lacks backend/cloud integration experience", "Missing measurable metrics in earlier roles"),
            riskAreas = listOf("Might need ramp-up time for full-stack tasks", "Testing practices unclear"),
            recruiterImpression = "Solid candidate for native Android roles. Needs technical screening on architecture patterns.",
            hirePotential = "High",
            skills = mapOf(
                "Languages" to listOf("Kotlin", "Java"),
                "Android" to listOf("Jetpack Compose", "Coroutines", "MVVM"),
                "Backend/Cloud" to listOf("Firebase")
            ),
            missingKeywords = listOf("JUnit", "Espresso", "CI/CD", "Dagger/Hilt", "REST APIs"),
            recommendations = listOf(
                Recommendation("Resume Improvements", "Add specific metrics to your StartupInc role (e.g. 'reduced crash rate by X%').", "High"),
                Recommendation("Technology Suggestions", "Include testing frameworks like JUnit or Espresso to strengthen the profile.", "High"),
                Recommendation("Project Suggestions", "Add a personal project demonstrating CI/CD pipelines.", "Medium")
            )
        )

        return NetworkResult.Success(mockAnalysis)
    }
}
