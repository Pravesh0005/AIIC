package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewConfig
import com.aiic.app.domain.model.InterviewQuestion

interface InterviewQuestionRepository {
    suspend fun generateInitialQuestions(config: InterviewConfig, resumeContext: String): NetworkResult<List<InterviewQuestion>>
    suspend fun generateFollowUpQuestion(previousQuestion: String, answer: String): NetworkResult<InterviewQuestion?>
    suspend fun saveQuestions(questions: List<InterviewQuestion>): NetworkResult<Unit>
    suspend fun getQuestionsForSession(sessionId: String): NetworkResult<List<InterviewQuestion>>
}
