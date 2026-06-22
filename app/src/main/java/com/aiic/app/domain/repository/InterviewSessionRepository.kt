package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewConfig
import com.aiic.app.domain.model.InterviewSession
import kotlinx.coroutines.flow.Flow

interface InterviewSessionRepository {
    suspend fun createSession(config: InterviewConfig, userId: String): NetworkResult<InterviewSession>
    suspend fun getActiveSession(userId: String): NetworkResult<InterviewSession?>
    suspend fun completeSession(sessionId: String, finalScore: Float): NetworkResult<Unit>
    suspend fun updateSessionStatus(sessionId: String, status: com.aiic.app.domain.model.SessionStatus): NetworkResult<Unit>
    suspend fun getSessionById(sessionId: String): NetworkResult<InterviewSession>
    fun getSessionHistory(userId: String): Flow<List<InterviewSession>>
}
