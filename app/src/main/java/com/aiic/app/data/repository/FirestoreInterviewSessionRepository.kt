package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.InterviewConfig
import com.aiic.app.domain.model.InterviewSession
import com.aiic.app.domain.model.SessionStatus
import com.aiic.app.domain.repository.InterviewSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class FirestoreInterviewSessionRepository @Inject constructor() : InterviewSessionRepository {
    
    // In-memory cache for demo purposes
    private val sessions = mutableMapOf<String, InterviewSession>()

    override suspend fun createSession(
        config: InterviewConfig,
        userId: String
    ): NetworkResult<InterviewSession> {
        val sessionId = UUID.randomUUID().toString()
        val session = InterviewSession(
            sessionId = sessionId,
            userId = userId,
            resumeId = config.resumeId,
            role = config.role,
            interviewType = config.interviewType,
            difficulty = config.difficulty,
            questionCount = config.questionCount,
            status = SessionStatus.IN_PROGRESS
        )
        sessions[sessionId] = session
        return NetworkResult.Success(session)
    }

    override suspend fun getActiveSession(userId: String): NetworkResult<InterviewSession?> {
        val active = sessions.values.find { it.userId == userId && it.status == SessionStatus.IN_PROGRESS }
        return NetworkResult.Success(active)
    }

    override suspend fun completeSession(sessionId: String, finalScore: Float): NetworkResult<Unit> {
        val session = sessions[sessionId]
        if (session != null) {
            sessions[sessionId] = session.copy(
                status = SessionStatus.COMPLETED,
                score = finalScore,
                endedAt = System.currentTimeMillis()
            )
            return NetworkResult.Success(Unit)
        }
        return NetworkResult.Error(message = "Session not found")
    }

    override suspend fun updateSessionStatus(
        sessionId: String,
        status: SessionStatus
    ): NetworkResult<Unit> {
        val session = sessions[sessionId]
        if (session != null) {
            sessions[sessionId] = session.copy(status = status)
            return NetworkResult.Success(Unit)
        }
        return NetworkResult.Error(message = "Session not found")
    }

    override suspend fun getSessionById(sessionId: String): NetworkResult<InterviewSession> {
        val session = sessions[sessionId]
        return if (session != null) {
            NetworkResult.Success(session)
        } else {
            NetworkResult.Error(message = "Session not found")
        }
    }

    override fun getSessionHistory(userId: String): Flow<List<InterviewSession>> = flow {
        emit(sessions.values.filter { it.userId == userId && it.status == SessionStatus.COMPLETED })
    }
}
