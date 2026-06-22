package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import javax.inject.Inject

class CompleteInterviewUseCase @Inject constructor(
    private val sessionRepository: InterviewSessionRepository,
    private val answerRepository: InterviewAnswerRepository
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

        return NetworkResult.Success(finalScore)
    }
}
