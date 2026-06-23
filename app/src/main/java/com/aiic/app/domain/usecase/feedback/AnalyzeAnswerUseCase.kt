package com.aiic.app.domain.usecase.feedback

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.ai.prompt.FeedbackPromptBuilder
import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.repository.FeedbackRepository
import com.aiic.app.domain.repository.GenerativeAiRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnalyzeAnswerUseCase @Inject constructor(
    private val generativeAiRepository: GenerativeAiRepository,
    private val feedbackRepository: FeedbackRepository
) {
    private val gson = Gson()

    suspend operator fun invoke(
        sessionId: String,
        questionId: String,
        question: String,
        answerText: String,
        targetRole: String,
        resumeContext: String
    ): NetworkResult<AnswerFeedback> = withContext(Dispatchers.IO) {
        try {
            // 1. Build Prompt
            val prompt = FeedbackPromptBuilder.buildAnswerEvaluationPrompt(
                question = question,
                answer = answerText,
                targetRole = targetRole,
                resumeContext = resumeContext
            )

            // 2. Call AI
            val aiResponse = generativeAiRepository.generateText(prompt)
            if (aiResponse is NetworkResult.Error) {
                return@withContext NetworkResult.Error(message = aiResponse.message ?: "AI Evaluation failed")
            }

            val jsonString = (aiResponse as NetworkResult.Success).data ?: ""
            
            // Clean JSON string (remove markdown blocks if AI ignored instructions)
            val cleanJson = jsonString.replace("```json", "").replace("```", "").trim()

            // 3. Parse Response
            val parsedMap = gson.fromJson(cleanJson, Map::class.java)

            @Suppress("UNCHECKED_CAST")
            val feedback = AnswerFeedback(
                feedbackId = "fb_${System.currentTimeMillis()}",
                sessionId = sessionId,
                questionId = questionId,
                answerText = answerText,
                overallScore = (parsedMap["overallScore"] as? Number)?.toInt() ?: 0,
                technicalScore = (parsedMap["technicalScore"] as? Number)?.toInt() ?: 0,
                communicationScore = (parsedMap["communicationScore"] as? Number)?.toInt() ?: 0,
                relevanceScore = (parsedMap["relevanceScore"] as? Number)?.toInt() ?: 0,
                structureScore = (parsedMap["structureScore"] as? Number)?.toInt() ?: 0,
                confidenceScore = (parsedMap["confidenceScore"] as? Number)?.toInt() ?: 0,
                strengths = parsedMap["strengths"] as? List<String> ?: emptyList(),
                weaknesses = parsedMap["weaknesses"] as? List<String> ?: emptyList(),
                improvementSuggestions = parsedMap["improvementSuggestions"] as? List<String> ?: emptyList(),
                interviewerPerspective = parsedMap["interviewerPerspective"] as? String ?: "",
                followUpQuestions = parsedMap["followUpQuestions"] as? List<String> ?: emptyList()
            )

            // 4. Save to Repository
            val saveResult = feedbackRepository.saveAnswerFeedback(feedback)
            if (saveResult is NetworkResult.Error) {
                // Return success anyway, since we have the feedback for UI, but maybe log error
                return@withContext NetworkResult.Success(feedback)
            }

            NetworkResult.Success(feedback)
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(message = "Failed to analyze answer: ${e.message}")
        }
    }
}
