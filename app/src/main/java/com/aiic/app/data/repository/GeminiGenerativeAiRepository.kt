package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.ai.prompt.ResumeAnalysisPromptBuilder
import com.aiic.app.domain.model.AtsScoreDetails
import com.aiic.app.domain.model.Recommendation
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.GenerativeAiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class GeminiGenerativeAiRepository @Inject constructor() : GenerativeAiRepository {

    private val apiKey = com.aiic.app.BuildConfig.GEMINI_API_KEY
    private val gson = Gson()

    override suspend fun generateResumeAnalysis(
        userId: String,
        resumeId: String,
        rawText: String
    ): NetworkResult<ResumeAnalysis> {
        return withContext(Dispatchers.IO) {
            try {
                // Configure the Builder to enforce strict JSON
                val promptBuilder = ResumeAnalysisPromptBuilder()
                    .setRawResumeText(rawText)
                    .includeJsonSchema(true)

                val systemPrompt = promptBuilder.buildSystemPrompt()
                val userPrompt = promptBuilder.buildUserPrompt()
                val combinedPrompt = "$systemPrompt\n\n$userPrompt"

                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKey
                )

                val response = generativeModel.generateContent(combinedPrompt)
                val responseText = response.text

                if (responseText.isNullOrBlank()) {
                    return@withContext NetworkResult.Error(message = "Failed to generate analysis: Empty response from AI.")
                }

                // Clean the response text from potential markdown code blocks
                val cleanJson = responseText.replace("```json", "").replace("```", "").trim()

                // Parse the JSON into our internal mapping class
                val analysisData = gson.fromJson(cleanJson, AiAnalysisResponse::class.java)

                // Map to domain model and apply unique identifiers
                val finalAnalysis = ResumeAnalysis(
                    analysisId = UUID.randomUUID().toString(),
                    resumeId = resumeId,
                    userId = userId,
                    timestamp = System.currentTimeMillis(),
                    overallScore = analysisData.overallScore,
                    atsScoreDetails = analysisData.atsScoreDetails,
                    profileSummary = analysisData.profileSummary,
                    strengths = analysisData.strengths ?: emptyList(),
                    weaknesses = analysisData.weaknesses ?: emptyList(),
                    riskAreas = analysisData.riskAreas ?: emptyList(),
                    recruiterImpression = analysisData.recruiterImpression ?: "",
                    hirePotential = analysisData.hirePotential ?: "",
                    skills = analysisData.skills ?: emptyMap(),
                    missingKeywords = analysisData.missingKeywords ?: emptyList(),
                    recommendations = analysisData.recommendations ?: emptyList()
                )

                NetworkResult.Success(finalAnalysis)
            } catch (e: Exception) {
                NetworkResult.Error(message = "AI Processing Error: ${e.message}")
            }
        }
    }
}

// Temporary internal data class for Gson parsing mapping strictly to the JSON schema
data class AiAnalysisResponse(
    val overallScore: Int,
    val atsScoreDetails: AtsScoreDetails,
    val profileSummary: String,
    val strengths: List<String>?,
    val weaknesses: List<String>?,
    val riskAreas: List<String>?,
    val recruiterImpression: String?,
    val hirePotential: String?,
    val skills: Map<String, List<String>>?,
    val missingKeywords: List<String>?,
    val recommendations: List<Recommendation>?
)
