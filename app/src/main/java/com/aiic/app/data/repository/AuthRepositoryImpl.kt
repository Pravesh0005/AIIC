package com.aiic.app.data.repository

import com.aiic.app.domain.model.User
import com.aiic.app.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override suspend fun login(email: String, password: String): Result<User> {
        delay(1200)
        val user = User(
            id = "usr_${System.currentTimeMillis()}",
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = email,
            readinessScore = 0.72f,
            interviewsCompleted = 14,
            streakDays = 7,
        )
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        delay(1500)
        val user = User(id = "usr_${System.currentTimeMillis()}", name = name, email = email)
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        delay(1000)
        val user = User(
            id = "usr_g_${System.currentTimeMillis()}",
            name = "Google User",
            email = "user@gmail.com",
        )
        _currentUser.value = user
        return Result.success(user)
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser

    override suspend fun logout() {
        _currentUser.value = null
    }
}
