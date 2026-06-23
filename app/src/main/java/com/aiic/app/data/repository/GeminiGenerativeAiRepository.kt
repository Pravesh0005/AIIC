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
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.client.generativeai.type.RequestOptions

class GeminiGenerativeAiRepository @Inject constructor() : GenerativeAiRepository {

    private val apiKey = com.aiic.app.BuildConfig.GEMINI_API_KEY
    private val gson = Gson()

    private suspend fun generateContentWithFallback(prompt: String): String {
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 0.85f
                },
                requestOptions = RequestOptions(timeout = 15000L)
            )
            val response = generativeModel.generateContent(prompt)
            val text = response.text
            if (!text.isNullOrBlank()) {
                return text
            }
        } catch (e: Exception) {
            // Log or ignore to try fallback
        }

        // Fallback to Groq
        val groqResponse = tryGroqFallback(prompt)
        if (!groqResponse.isNullOrBlank()) {
            return groqResponse
        }

        throw Exception("Both Gemini and Groq fallback failed to generate content.")
    }

    private suspend fun tryGroqFallback(prompt: String): String? {
        val groqKey = com.aiic.app.BuildConfig.GROQ_API_KEY
        if (groqKey.isBlank()) return null
        
        return withContext(Dispatchers.IO) {
            try {
                val url = java.net.URL("https://api.groq.com/openai/v1/chat/completions")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 15000 // 15 seconds
                connection.readTimeout = 20000 // 20 seconds
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $groqKey")
                connection.doOutput = true
                
                val body = mapOf(
                    "model" to "llama3-70b-8192",
                    "messages" to listOf(
                        mapOf("role" to "user", "content" to prompt)
                    )
                )
                val bodyStr = gson.toJson(body)
                
                connection.outputStream.use { os ->
                    val input = bodyStr.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }
                
                if (connection.responseCode in 200..299) {
                    val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val root = gson.fromJson(responseStr, Map::class.java)
                    val choices = root["choices"] as? List<*>
                    val firstChoice = choices?.firstOrNull() as? Map<*, *>
                    val message = firstChoice?.get("message") as? Map<*, *>
                    message?.get("content") as? String
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun generateResumeAnalysis(
        userId: String,
        resumeId: String,
        rawText: String
    ): NetworkResult<ResumeAnalysis> {
        return withContext(Dispatchers.IO) {
            try {
                val promptBuilder = ResumeAnalysisPromptBuilder()
                    .setRawResumeText(rawText)
                    .includeJsonSchema(true)

                val systemPrompt = promptBuilder.buildSystemPrompt()
                val userPrompt = promptBuilder.buildUserPrompt()
                val combinedPrompt = "$systemPrompt\n\n$userPrompt"

                val responseText = generateContentWithFallback(combinedPrompt)

                val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
                val analysisData = gson.fromJson(cleanJson, AiAnalysisResponse::class.java)

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

    override suspend fun generateText(prompt: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val responseText = generateContentWithFallback(prompt)
                NetworkResult.Success(responseText)
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
