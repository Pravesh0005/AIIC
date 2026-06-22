package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewAnswer
import com.aiic.app.domain.repository.GenerativeAiRepository
import com.aiic.app.domain.repository.InterviewAnswerRepository
import javax.inject.Inject

class FirestoreInterviewAnswerRepository @Inject constructor(
    private val generativeAiRepository: GenerativeAiRepository
) : InterviewAnswerRepository {

    private val answersCache = mutableListOf<InterviewAnswer>()

    override suspend fun submitAnswer(answer: InterviewAnswer): NetworkResult<Unit> {
        answersCache.add(answer)
        return NetworkResult.Success(Unit)
    }

    override suspend fun getAnswersForSession(sessionId: String): NetworkResult<List<InterviewAnswer>> {
        val sessionAnswers = answersCache.filter { it.sessionId == sessionId }
        return NetworkResult.Success(sessionAnswers)
    }

    override suspend fun evaluateAnswer(
        question: String,
        answer: String
    ): NetworkResult<Pair<Float, String>> {
        val prompt = """
            You are an expert technical interviewer evaluating a candidate.
            Question asked: $question
            Candidate answer: $answer
            
            Evaluate this answer out of 100 based on accuracy, depth, and communication.
            Format your response exactly as:
            SCORE: [number 0-100]
            FEEDBACK: [1-2 sentences of feedback]
        """.trimIndent()

        val aiResult = generativeAiRepository.generateText(prompt)
        
        val aiResponse = aiResult.getOrNull()
        if (aiResponse != null) {
            val response = aiResponse
            var score = 50f
            var feedback = "Fair attempt."
            
            try {
                val scoreRegex = Regex("SCORE:\\s*(\\d+)")
                val feedbackRegex = Regex("FEEDBACK:\\s*(.+)", RegexOption.DOT_MATCHES_ALL)
                
                scoreRegex.find(response)?.groupValues?.get(1)?.let {
                    score = it.toFloat()
                }
                
                feedbackRegex.find(response)?.groupValues?.get(1)?.let {
                    feedback = it.trim()
                }
                
                return NetworkResult.Success(Pair(score, feedback))
            } catch (e: Exception) {
                // Fallback parsing
            }
        }
        
        // Default fallback if parsing fails
        return NetworkResult.Success(Pair(60f, "Your answer was acceptable but could use more detail."))
    }
}
