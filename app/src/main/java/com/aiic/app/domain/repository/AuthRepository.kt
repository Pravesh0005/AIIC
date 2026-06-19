package com.aiic.app.domain.repository

import com.aiic.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    fun getCurrentUser(): Flow<User?>
    suspend fun logout()
}
