package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
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
        responseTimeMs: Long,
        targetRole: String = "General Candidate",
        resumeContext: String = ""
    ): NetworkResult<InterviewQuestion?> {
        
        val answer = InterviewAnswer(
            answerId = "ans_${System.currentTimeMillis()}",
            questionId = currentQuestion.questionId,
            sessionId = sessionId,
            content = answerContent,
            responseTimeMs = responseTimeMs,
            aiEvaluationScore = 0f, 
            aiFeedback = "" 
        )
        
        val submitResult = answerRepository.submitAnswer(answer)
        if (submitResult.getOrNull() == null) {
            return NetworkResult.Error(message = "Failed to save answer")
        }

        return NetworkResult.Success(null)
    }
}
