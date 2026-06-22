package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import javax.inject.Inject

class CompleteInterviewUseCase @Inject constructor(
    private val sessionRepository: InterviewSessionRepository,
    private val answerRepository: InterviewAnswerRepository,
    private val userRepository: com.aiic.app.domain.repository.UserRepository
) {
    suspend operator fun invoke(sessionId: String): NetworkResult<Float> {
        // 1. Fetch all answers for the session
        val answersResult = answerRepository.getAnswersForSession(sessionId)
        val answers = answersResult.getOrNull()
        if (answers == null) {
            return NetworkResult.Error(message = "Failed to fetch answers to calculate score")
        }

        if (answers.isEmpty()) {
            sessionRepository.completeSession(sessionId, 0f)
            return NetworkResult.Success(0f)
        }

        // 2. Calculate final score (average of all answer scores)
        val totalScore = answers.sumOf { it.aiEvaluationScore.toDouble() }.toFloat()
        val finalScore = totalScore / answers.size

        // 3. Complete session
        val completeResult = sessionRepository.completeSession(sessionId, finalScore)
        if (completeResult.getOrNull() == null) {
            return NetworkResult.Error(message = "Failed to mark session as complete")
        }

        // 4. Update UserProfile with new readiness score and interview count
        val session = sessionRepository.getSessionById(sessionId).getOrNull()
        if (session != null) {
            val userResult = userRepository.getUserProfile(session.userId).getOrNull()
            if (userResult != null) {
                // Moving average or basic weighting for readiness score
                // Example: average of existing score and this score, but normalized to 0.0 - 1.0
                val normalizedScore = finalScore / 100f
                val newCount = userResult.interviewCount + 1
                // Simple moving average
                val currentReadiness = userResult.readinessScore
                val newReadiness = if (currentReadiness == 0f) normalizedScore else ((currentReadiness * userResult.interviewCount) + normalizedScore) / newCount

                userRepository.updateUserProfile(session.userId, mapOf(
                    "readinessScore" to newReadiness,
                    "interviewCount" to newCount
                ))
            }
        }

        return NetworkResult.Success(finalScore)
    }
}
