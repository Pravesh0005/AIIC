package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.model.SessionSummary

interface FeedbackRepository {
    suspend fun saveAnswerFeedback(feedback: AnswerFeedback): NetworkResult<Unit>
    suspend fun getFeedbackForAnswer(answerId: String): NetworkResult<AnswerFeedback>
    suspend fun generateAndSaveSessionSummary(sessionId: String): NetworkResult<SessionSummary>
    suspend fun getSessionSummary(sessionId: String): NetworkResult<SessionSummary>
}
