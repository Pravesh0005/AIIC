package com.aiic.app.data.repository

import com.aiic.app.data.local.PreferencesManager
import com.aiic.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : UserPreferencesRepository {

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        preferencesManager.setOnboardingCompleted(completed)

    override fun isOnboardingCompleted(): Flow<Boolean> =
        preferencesManager.isOnboardingCompleted()

    override suspend fun setLoggedIn(loggedIn: Boolean) =
        preferencesManager.setLoggedIn(loggedIn)

    override fun isLoggedIn(): Flow<Boolean> =
        preferencesManager.isLoggedIn()
}
