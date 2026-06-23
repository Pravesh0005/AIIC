package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.model.InterviewAnswer
import com.aiic.app.domain.model.InterviewQuestion
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.aiic.app.domain.repository.InterviewQuestionRepository
import javax.inject.Inject

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull

class SubmitAnswerAndEvaluateUseCase @Inject constructor(
    private val answerRepository: InterviewAnswerRepository,
    private val questionRepository: InterviewQuestionRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        currentQuestion: InterviewQuestion,
        answerContent: String,
        responseTimeMs: Long,
        targetRole: String = "General Candidate",
        resumeContext: String = ""
    ): NetworkResult<InterviewQuestion?> {
        // Run AI requests concurrently
        val timeoutResult = withTimeoutOrNull(10000L) {
            coroutineScope {
                val evalDeferred = async<NetworkResult<com.aiic.app.domain.model.AnswerFeedback>> {
                    com.aiic.app.domain.usecase.feedback.AnalyzeAnswerUseCase(
                        com.aiic.app.data.repository.GeminiGenerativeAiRepository(), // In a real scenario, inject this properly
                        com.aiic.app.data.repository.FirestoreFeedbackRepositoryImpl(com.google.firebase.firestore.FirebaseFirestore.getInstance())
                    ).invoke(sessionId, currentQuestion.questionId, currentQuestion.content, answerContent, targetRole, resumeContext)
                }
                
                val followUpDeferred = async<NetworkResult<InterviewQuestion?>> {
                    questionRepository.generateFollowUpQuestion(currentQuestion.content, answerContent)
                }
                
                val evalResult = evalDeferred.await()
                val followUpResult = followUpDeferred.await()
    
                var score = 0f
                var feedbackStr = ""
                
                val evalResponse = evalResult.getOrNull()
                if (evalResponse != null) {
                    score = evalResponse.overallScore.toFloat()
                    feedbackStr = evalResponse.interviewerPerspective
                }
    
                // 2. Save the answer
                val answer = InterviewAnswer(
                    answerId = "ans_${System.currentTimeMillis()}",
                    questionId = currentQuestion.questionId,
                    sessionId = sessionId,
                    content = answerContent,
                    responseTimeMs = responseTimeMs,
                    aiEvaluationScore = score,
                    aiFeedback = feedbackStr
                )
                
                val submitResult = answerRepository.submitAnswer(answer)
                if (submitResult.getOrNull() == null) {
                    return@coroutineScope NetworkResult.Error(message = "Failed to save answer")
                }
    
                // 3. Determine if Follow-up is needed
                val followUpData = followUpResult.getOrNull()
                if (followUpData != null) {
                    val followUpQuestion = followUpData.copy(
                        sessionId = sessionId,
                        parentQuestionId = currentQuestion.questionId,
                        isFollowUp = true
                    )
                    // Save the follow-up question
                    questionRepository.saveQuestions(listOf(followUpQuestion))
                    return@coroutineScope NetworkResult.Success(followUpQuestion)
                }
    
                // Returns null indicating no follow up generated.
                return@coroutineScope NetworkResult.Success(null)
            }
        }
        
        return timeoutResult ?: NetworkResult.Error(message = "AI Evaluation timed out.")
    }
}
