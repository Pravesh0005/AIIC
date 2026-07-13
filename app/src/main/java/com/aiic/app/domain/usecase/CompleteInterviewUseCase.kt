package com.aiic.app.domain.usecase

import android.util.Log
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.model.InterviewAnswer
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.aiic.app.domain.repository.InterviewQuestionRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import com.aiic.app.domain.usecase.feedback.AnalyzeAnswerUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class CompleteInterviewUseCase @Inject constructor(
    private val sessionRepository: InterviewSessionRepository,
    private val answerRepository: InterviewAnswerRepository,
    private val questionRepository: InterviewQuestionRepository,
    private val userRepository: com.aiic.app.domain.repository.UserRepository,
    private val analyzeAnswerUseCase: AnalyzeAnswerUseCase
) {
    suspend operator fun invoke(sessionId: String): NetworkResult<Float> {
        Log.d("AIIC_DEBUG", "Entering CompleteInterviewUseCase for sessionId: $sessionId")
        
        val answersResult = answerRepository.getAnswersForSession(sessionId)
        val answers = answersResult.getOrNull()
        if (answers == null) {
            Log.e("AIIC_DEBUG", "CompleteInterviewUseCase: Failed to fetch answers")
            return NetworkResult.Error(message = "Failed to fetch answers to calculate score")
        }

        if (answers.isEmpty()) {
            Log.d("AIIC_DEBUG", "CompleteInterviewUseCase: No answers found, completing with 0 score")
            sessionRepository.completeSession(sessionId, 0f)
            return NetworkResult.Success(0f)
        }

        val questionsResult = questionRepository.getQuestionsForSession(sessionId)
        val questions = questionsResult.getOrNull() ?: emptyList()
        val session = sessionRepository.getSessionById(sessionId).getOrNull()
        val targetRole = session?.role ?: "General Candidate"

        Log.d("AIIC_DEBUG", "CompleteInterviewUseCase: Found ${answers.size} answers to evaluate")

        val evaluatedAnswers = coroutineScope {
            answers.map { answer ->
                async {
                    val question = questions.find { it.questionId == answer.questionId }?.content ?: ""
                    Log.d("AIIC_DEBUG", "CompleteInterviewUseCase: Sending answer ${answer.answerId} to AnalyzeAnswerUseCase")
                    
                    val feedbackResult = analyzeAnswerUseCase(
                        sessionId = sessionId,
                        questionId = answer.questionId,
                        question = question,
                        answerText = answer.content,
                        targetRole = targetRole,
                        resumeContext = "" 
                    )

                    val aiScore = feedbackResult.getOrNull()?.overallScore?.toFloat() ?: 0f
                    val aiFeedbackText = feedbackResult.getOrNull()?.interviewerPerspective ?: ""
                    
                    Log.d("AIIC_DEBUG", "CompleteInterviewUseCase: Evaluated answer ${answer.answerId}, Score: $aiScore")

                    val updatedAnswer = answer.copy(
                        aiEvaluationScore = aiScore,
                        aiFeedback = aiFeedbackText
                    )
                    
                    answerRepository.submitAnswer(updatedAnswer)
                    
                    updatedAnswer
                }
            }.awaitAll()
        }

        val finalScore = evaluatedAnswers.map { it.aiEvaluationScore }.average().toFloat().coerceIn(0f, 100f)
        Log.d("AIIC_DEBUG", "CompleteInterviewUseCase: Calculated final average score: $finalScore")

        val completeResult = sessionRepository.completeSession(sessionId, finalScore)
        if (completeResult.getOrNull() == null) {
            Log.e("AIIC_DEBUG", "CompleteInterviewUseCase: Failed to complete session in repository")
            return NetworkResult.Error(message = "Failed to mark session as complete")
        }

        if (session != null) {
            val userResult = userRepository.getUserProfile(session.userId).getOrNull()
            
            val currentCount = userResult?.interviewCount ?: 0
            val currentReadiness = userResult?.readinessScore ?: 0f
            val currentHours = userResult?.totalPracticeHours ?: 0f
            
            val durationMs = if (session.startedAt > 0) System.currentTimeMillis() - session.startedAt else 0L
            val durationHours = durationMs / 3600000f
            val newHours = currentHours + durationHours
            
            val normalizedScore = finalScore / 100f
            val newCount = currentCount + 1
            
            val newReadiness = if (currentReadiness == 0f) {
                normalizedScore 
            } else {
                ((currentReadiness * currentCount) + normalizedScore) / newCount
            }

            userRepository.updateUserProfile(session.userId, mapOf(
                "readinessScore" to newReadiness,
                "interviewCount" to newCount,
                "totalPracticeHours" to newHours
            ))
            Log.d("AIIC_DEBUG", "CompleteInterviewUseCase: Updated UserProfile readiness to $newReadiness")
        }

        Log.d("AIIC_DEBUG", "Leaving CompleteInterviewUseCase successfully")
        return NetworkResult.Success(finalScore)
    }
}
