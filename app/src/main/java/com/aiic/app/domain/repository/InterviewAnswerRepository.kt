package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewAnswer

interface InterviewAnswerRepository {
    suspend fun submitAnswer(answer: InterviewAnswer): NetworkResult<Unit>
    suspend fun getAnswersForSession(sessionId: String): NetworkResult<List<InterviewAnswer>>
    suspend fun evaluateAnswer(question: String, answer: String): NetworkResult<Pair<Float, String>> // Returns Score & Feedback
}
