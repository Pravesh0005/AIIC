package com.aiic.app.data.repository

import android.util.Log
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.ai.prompt.ResumeAnalysisPromptBuilder
import com.aiic.app.domain.model.AtsScoreDetails
import com.aiic.app.domain.model.Recommendation
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.GenerativeAiRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import javax.inject.Inject

/**
 * Groq-only AI repository.
 * All AI generation uses Groq API exclusively.
 * No Gemini dependency, no Gemini fallback.
 */
class GeminiGenerativeAiRepository @Inject constructor() : GenerativeAiRepository {

    private val groqKey = com.aiic.app.BuildConfig.GROQ_API_KEY
    private val gson = Gson()

    init {
        val masked = if (groqKey.length > 8) "${groqKey.take(4)}...${groqKey.takeLast(4)}" else "(empty)"
        Log.d("AIIC_DEBUG", "GeminiGenerativeAiRepository INIT — key length=${groqKey.length}, masked=$masked, model=llama-3.3-70b-versatile")
    }

    private suspend fun generateContentGroq(prompt: String): String {
        val result = callGroq(prompt)
        if (!result.isNullOrBlank()) return result
        throw Exception("Groq AI provider failed. Check API key and network connection.")
    }
    private suspend fun callGroq(prompt: String): String? {
        if (groqKey.isBlank()) {
            Log.e("AIIC_DEBUG", "callGroq: API Key is blank")
            return null
        }
        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(15000L) {
                try {
                    Log.d("AIIC_DEBUG", "Entering callGroq - Groq Model: llama-3.3-70b-versatile")
                    val url = java.net.URL("https://api.groq.com/openai/v1/chat/completions")
                    Log.d("AIIC_DEBUG", "callGroq: HTTP URL: $url")
                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.connectTimeout = 5000
                    connection.readTimeout = 12000
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Authorization", "Bearer $groqKey")
                    connection.doOutput = true

                    val body = mapOf(
                        "model" to "llama-3.3-70b-versatile",
                        "messages" to listOf(
                            mapOf("role" to "user", "content" to prompt)
                        ),
                        "temperature" to 0.7,
                        "max_tokens" to 2048
                    )
                    val bodyStr = gson.toJson(body)

                    val startTime = System.currentTimeMillis()
                    connection.outputStream.use { os ->
                        val input = bodyStr.toByteArray(Charsets.UTF_8)
                        os.write(input, 0, input.size)
                    }

                    val status = connection.responseCode
                    Log.d("AIIC_DEBUG", "callGroq: HTTP Status: $status, Execution Time: ${System.currentTimeMillis() - startTime}ms")
                    
                    if (status in 200..299) {
                        val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("AIIC_DEBUG", "callGroq: HTTP Response: $responseStr")
                        val root = gson.fromJson(responseStr, Map::class.java)
                        val choices = root["choices"] as? List<*>
                        val firstChoice = choices?.firstOrNull() as? Map<*, *>
                        val message = firstChoice?.get("message") as? Map<*, *>
                        val content = message?.get("content") as? String
                        Log.d("AIIC_DEBUG", "Leaving callGroq - Parsed content success")
                        content
                    } else {
                        val errorStr = try {
                            connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                        } catch (_: Exception) { "" }
                        Log.e("AIIC_DEBUG", "callGroq: Failed HTTP Status $status, Error: $errorStr")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("AIIC_DEBUG", "callGroq: Exception caught", e)
                    null
                }
            } ?: run {
                Log.e("AIIC_DEBUG", "callGroq: Timeout after 15000ms")
                null
            }
        }
    }

    /**
     * Groq call with JSON response format enforced.
     */
    private suspend fun callGroqJson(prompt: String): String? {
        if (groqKey.isBlank()) {
            Log.e("AIIC_DEBUG", "callGroqJson: API Key is blank")
            return null
        }
        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(15000L) {
                try {
                    Log.d("AIIC_DEBUG", "Entering callGroqJson - Groq Model: llama-3.3-70b-versatile")
                    val url = java.net.URL("https://api.groq.com/openai/v1/chat/completions")
                    Log.d("AIIC_DEBUG", "callGroqJson: HTTP URL: $url")
                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.connectTimeout = 5000
                    connection.readTimeout = 12000
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Authorization", "Bearer $groqKey")
                    connection.doOutput = true

                    val body = mapOf(
                        "model" to "llama-3.3-70b-versatile",
                        "messages" to listOf(
                            mapOf("role" to "user", "content" to prompt)
                        ),
                        "temperature" to 0.7,
                        "max_tokens" to 2048,
                        "response_format" to mapOf("type" to "json_object")
                    )
                    val bodyStr = gson.toJson(body)

                    val startTime = System.currentTimeMillis()
                    connection.outputStream.use { os ->
                        val input = bodyStr.toByteArray(Charsets.UTF_8)
                        os.write(input, 0, input.size)
                    }

                    val status = connection.responseCode
                    Log.d("AIIC_DEBUG", "callGroqJson: HTTP Status: $status, Execution Time: ${System.currentTimeMillis() - startTime}ms")

                    if (status in 200..299) {
                        val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.d("AIIC_DEBUG", "callGroqJson: HTTP Response: $responseStr")
                        val root = gson.fromJson(responseStr, Map::class.java)
                        val choices = root["choices"] as? List<*>
                        val firstChoice = choices?.firstOrNull() as? Map<*, *>
                        val message = firstChoice?.get("message") as? Map<*, *>
                        val content = message?.get("content") as? String
                        Log.d("AIIC_DEBUG", "Leaving callGroqJson - Parsed JSON string success")
                        content
                    } else {
                        val errorStr = try {
                            connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                        } catch (_: Exception) { "" }
                        Log.e("AIIC_DEBUG", "callGroqJson: Failed HTTP Status $status, Error: $errorStr")
                        null
                    }
                } catch (e: Exception) {
                    Log.e("AIIC_DEBUG", "callGroqJson: Exception caught", e)
                    null
                }
            } ?: run {
                Log.e("AIIC_DEBUG", "callGroqJson: Timeout after 15000ms")
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

                val responseText = callGroqJson(combinedPrompt)
                    ?: return@withContext NetworkResult.Error(message = "AI analysis failed. Please check your network connection and try again.")

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
                val responseText = generateContentGroq(prompt)
                NetworkResult.Success(responseText)
            } catch (e: Exception) {
                NetworkResult.Error(message = "AI Processing Error: ${e.message}")
            }
        }
    }

    override suspend fun generateJson(prompt: String): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val responseText = callGroqJson(prompt)
                if (responseText.isNullOrBlank()) {
                    return@withContext NetworkResult.Error(message = "AI JSON Processing Error: Empty response from Groq.")
                }
                NetworkResult.Success(responseText)
            } catch (e: Exception) {
                NetworkResult.Error(message = "AI JSON Processing Error: ${e.message}")
            }
        }
    }
}

// Internal data class for Gson parsing mapping strictly to the JSON schema
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
