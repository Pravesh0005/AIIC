package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewAnswer
import com.aiic.app.domain.model.InterviewQuestion
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.aiic.app.domain.repository.InterviewQuestionRepository
import javax.inject.Inject

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
        
        // 1. Evaluate the answer via AI
        val evalResult = answerRepository.evaluateAnswer(currentQuestion.content, answerContent)
        var score = 0f
        var feedback = ""
        
        if (evalResult is NetworkResult.Success) {
            score = evalResult.data.first
            feedback = evalResult.data.second
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
        if (submitResult !is NetworkResult.Success) {
            return NetworkResult.Error("Failed to save answer")
        }

        // 3. Determine if Follow-up is needed
        // For simplicity, we ask the AI to generate a follow-up. If it returns null, no follow-up is needed.
        val followUpResult = questionRepository.generateFollowUpQuestion(currentQuestion.content, answerContent)
        
        if (followUpResult is NetworkResult.Success && followUpResult.data != null) {
            val followUpQuestion = followUpResult.data.copy(
                sessionId = sessionId,
                parentQuestionId = currentQuestion.questionId,
                isFollowUp = true
            )
            // Save the follow-up question
            questionRepository.saveQuestions(listOf(followUpQuestion))
            return NetworkResult.Success(followUpQuestion)
        }

        // Returns null indicating no follow up generated.
        return NetworkResult.Success(null)
    }
}
