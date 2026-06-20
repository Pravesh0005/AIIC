package com.aiic.app.data.repository

import com.aiic.app.data.local.PreferencesManager
import com.aiic.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val prefs: PreferencesManager,
) : SessionRepository {

    override suspend fun saveSession(uid: String, email: String, name: String) =
        prefs.saveUserSession(uid, name, email)

    override suspend fun clearSession() = prefs.clearSession()

    override fun isLoggedIn(): Flow<Boolean> = prefs.isLoggedIn()

    override fun getUid(): Flow<String> = prefs.getUserId()

    override fun getUserName(): Flow<String> = prefs.getUserName()

    override fun getUserEmail(): Flow<String> = prefs.getUserEmail()

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        prefs.setOnboardingCompleted(completed)

    override fun isOnboardingCompleted(): Flow<Boolean> = prefs.isOnboardingCompleted()

    override suspend fun updateLastActive() {
        // Will be enhanced with Firestore write in future
    }
}
