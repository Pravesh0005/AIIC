package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.aiic.app.domain.repository.InterviewQuestionRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import javax.inject.Inject

/**
 * Completes an interview session.
 * 
 * Calculates the final score based on answers:
 * - If AI per-answer scores exist, averages them.
 * - Otherwise, calculates a heuristic score based on answer quality.
 * 
 * Updates session status to COMPLETED and updates user profile stats.
 */
class CompleteInterviewUseCase @Inject constructor(
    private val sessionRepository: InterviewSessionRepository,
    private val answerRepository: InterviewAnswerRepository,
    private val questionRepository: InterviewQuestionRepository,
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

        // 2. Fetch questions to know total expected
        val questionsResult = questionRepository.getQuestionsForSession(sessionId)
        val questions = questionsResult.getOrNull() ?: emptyList()

        // 3. Calculate a heuristic score based on answer quality
        // (Per-answer AI evaluation is deferred — scores are set to 0 during submission)
        val finalScore = calculateHeuristicScore(answers, questions.size)

        // 4. Complete session
        val completeResult = sessionRepository.completeSession(sessionId, finalScore)
        if (completeResult.getOrNull() == null) {
            return NetworkResult.Error(message = "Failed to mark session as complete")
        }

        // 5. Update UserProfile with new readiness score and interview count
        val session = sessionRepository.getSessionById(sessionId).getOrNull()
        if (session != null) {
            val userResult = userRepository.getUserProfile(session.userId).getOrNull()
            
            val currentCount = userResult?.interviewCount ?: 0
            val currentReadiness = userResult?.readinessScore ?: 0f
            
            val normalizedScore = finalScore / 100f
            val newCount = currentCount + 1
            
            // Simple moving average
            val newReadiness = if (currentReadiness == 0f) {
                normalizedScore 
            } else {
                ((currentReadiness * currentCount) + normalizedScore) / newCount
            }

            userRepository.updateUserProfile(session.userId, mapOf(
                "readinessScore" to newReadiness,
                "interviewCount" to newCount
            ))
        }

        return NetworkResult.Success(finalScore)
    }

    /**
     * Heuristic scoring when per-answer AI evaluation hasn't run:
     * - Base: 40 points for completing the interview
     * - Completion bonus: up to 20 points based on % questions answered
     * - Detail bonus: up to 25 points based on average answer length
     * - Consistency bonus: up to 15 points for answering all questions
     */
    private fun calculateHeuristicScore(
        answers: List<com.aiic.app.domain.model.InterviewAnswer>,
        totalQuestions: Int
    ): Float {
        // Check if we have real AI scores (non-zero)
        val hasRealScores = answers.any { it.aiEvaluationScore > 0f }
        if (hasRealScores) {
            val totalScore = answers.sumOf { it.aiEvaluationScore.toDouble() }.toFloat()
            return totalScore / answers.size
        }

        // Heuristic scoring
        var score = 40f // base score for completing

        // Completion ratio (up to 20 points)
        val completionRatio = if (totalQuestions > 0) answers.size.toFloat() / totalQuestions else 1f
        score += completionRatio * 20f

        // Answer detail (up to 25 points)
        val avgLength = answers.map { it.content.length }.average()
        val detailScore = when {
            avgLength > 300 -> 25f
            avgLength > 200 -> 20f
            avgLength > 100 -> 15f
            avgLength > 50 -> 10f
            else -> 5f
        }
        score += detailScore

        // Consistency bonus (up to 15 points)
        if (answers.size == totalQuestions && totalQuestions > 0) {
            score += 15f
        } else if (answers.size > totalQuestions / 2) {
            score += 8f
        }

        return score.coerceIn(0f, 100f)
    }
}
