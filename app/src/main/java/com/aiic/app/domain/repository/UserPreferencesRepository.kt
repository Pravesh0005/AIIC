package com.aiic.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setLoggedIn(loggedIn: Boolean)
    fun isLoggedIn(): Flow<Boolean>
}
