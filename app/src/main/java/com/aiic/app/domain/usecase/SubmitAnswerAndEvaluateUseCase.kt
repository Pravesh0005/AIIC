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

class SubmitAnswerAndEvaluateUseCase @Inject constructor(
    private val answerRepository: InterviewAnswerRepository,
    private val questionRepository: InterviewQuestionRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        currentQuestion: InterviewQuestion,
        answerContent: String,
        responseTimeMs: Long
    ): NetworkResult<InterviewQuestion?> {
        
        // Run AI requests concurrently to cut latency in half
        return coroutineScope {
            val evalDeferred = async<NetworkResult<Pair<Float, String>>> {
                answerRepository.evaluateAnswer(currentQuestion.content, answerContent)
            }
            
            val followUpDeferred = async<NetworkResult<InterviewQuestion?>> {
                questionRepository.generateFollowUpQuestion(currentQuestion.content, answerContent)
            }
            
            val evalResult = evalDeferred.await()
            val followUpResult = followUpDeferred.await()

            var score = 0f
            var feedback = ""
            
            val evalResponse = evalResult.getOrNull()
            if (evalResponse != null) {
                score = evalResponse.first
                feedback = evalResponse.second
            }

            // 2. Save the answer
            val answer = InterviewAnswer(
                answerId = "ans_${System.currentTimeMillis()}", // Mock ID generator, typically handled by Firestore
                questionId = currentQuestion.questionId,
                sessionId = sessionId,
                content = answerContent,
                responseTimeMs = responseTimeMs,
                aiEvaluationScore = score,
                aiFeedback = feedback
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
}
