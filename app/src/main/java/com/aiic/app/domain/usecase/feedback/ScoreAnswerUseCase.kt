package com.aiic.app.domain.usecase.feedback

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.repository.GenerativeAiRepository
import javax.inject.Inject

class ScoreAnswerUseCase @Inject constructor(
    private val analyzeAnswerUseCase: AnalyzeAnswerUseCase
) {
    
    suspend operator fun invoke(
        sessionId: String,
        questionId: String,
        question: String,
        answerText: String,
        targetRole: String,
        resumeContext: String
    ): NetworkResult<AnswerFeedback> {
        
        if (answerText.length < 10) {
            return NetworkResult.Error(message = "Answer too short to evaluate.")
        }
        
        return analyzeAnswerUseCase(sessionId, questionId, question, answerText, targetRole, resumeContext)
    }
}
