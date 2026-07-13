package com.aiic.app.domain.usecase.feedback

import android.util.Log
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
        Log.d("AIIC_DEBUG", "Entering AnalyzeAnswerUseCase for questionId: $questionId, sessionId: $sessionId")
        try {
            
            val prompt = FeedbackPromptBuilder.buildAnswerEvaluationPrompt(
                question = question,
                answer = answerText,
                targetRole = targetRole,
                resumeContext = resumeContext
            )

            Log.d("AIIC_DEBUG", "AnalyzeAnswerUseCase: Sending prompt to GenerativeAiRepository (generateJson)")
            val aiResponse = generativeAiRepository.generateJson(prompt)
            if (aiResponse is NetworkResult.Error) {
                Log.e("AIIC_DEBUG", "AnalyzeAnswerUseCase: Groq returned error: ${aiResponse.message}")
                val fallbackFeedback = AnswerFeedback(
                    feedbackId = "fb_${System.currentTimeMillis()}",
                    sessionId = sessionId,
                    questionId = questionId,
                    answerText = answerText,
                    overallScore = 0,
                    technicalScore = 0,
                    communicationScore = 0,
                    relevanceScore = 0,
                    structureScore = 0,
                    confidenceScore = 0,
                    strengths = emptyList(),
                    weaknesses = emptyList(),
                    improvementSuggestions = listOf("We encountered an error analyzing this answer. Please continue with the next question."),
                    interviewerPerspective = "Error during AI evaluation: ${aiResponse.message}",
                    followUpQuestions = emptyList()
                )
                feedbackRepository.saveAnswerFeedback(fallbackFeedback)
                return@withContext NetworkResult.Success(fallbackFeedback)
            }

            val jsonString = (aiResponse as NetworkResult.Success).data ?: ""
            
            val cleanJson = jsonString.replace("```json", "").replace("```", "").trim()
            Log.d("AIIC_DEBUG", "AnalyzeAnswerUseCase: Clean JSON String to parse: $cleanJson")

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

            Log.d("AIIC_DEBUG", "AnalyzeAnswerUseCase: Parsed AnswerFeedback overallScore = ${feedback.overallScore}")

            val saveResult = feedbackRepository.saveAnswerFeedback(feedback)
            if (saveResult is NetworkResult.Error) {
                Log.e("AIIC_DEBUG", "AnalyzeAnswerUseCase: Failed to save AnswerFeedback to repository")
                
                return@withContext NetworkResult.Success(feedback)
            }

            Log.d("AIIC_DEBUG", "Leaving AnalyzeAnswerUseCase successfully")
            NetworkResult.Success(feedback)
        } catch (e: Exception) {
            Log.e("AIIC_DEBUG", "AnalyzeAnswerUseCase: Exception during evaluation", e)
            val fallbackFeedback = AnswerFeedback(
                feedbackId = "fb_${System.currentTimeMillis()}",
                sessionId = sessionId,
                questionId = questionId,
                answerText = answerText,
                overallScore = 0,
                technicalScore = 0,
                communicationScore = 0,
                relevanceScore = 0,
                structureScore = 0,
                confidenceScore = 0,
                strengths = emptyList(),
                weaknesses = emptyList(),
                improvementSuggestions = listOf("We encountered an error analyzing this answer. Please continue with the next question."),
                interviewerPerspective = "Error during AI evaluation: ${e.message}",
                followUpQuestions = emptyList()
            )
            feedbackRepository.saveAnswerFeedback(fallbackFeedback)
            NetworkResult.Success(fallbackFeedback)
        }
    }
}
