package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<AuthSession>
    suspend fun register(name: String, email: String, password: String): NetworkResult<AuthSession>
    suspend fun sendPasswordReset(email: String): NetworkResult<Unit>
    suspend fun loginWithGoogle(idToken: String): NetworkResult<AuthSession>
    suspend fun logout()
    fun observeAuthState(): Flow<AuthSession?>
    fun getCurrentSession(): AuthSession?
}
