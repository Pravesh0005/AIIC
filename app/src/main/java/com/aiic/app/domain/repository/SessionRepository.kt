package com.aiic.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun saveSession(uid: String, email: String, name: String)
    suspend fun clearSession()
    fun isLoggedIn(): Flow<Boolean>
    fun getUid(): Flow<String>
    fun getUserName(): Flow<String>
    fun getUserEmail(): Flow<String>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun updateLastActive()
}
