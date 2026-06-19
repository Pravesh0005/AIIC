package com.aiic.app.domain.usecase

import com.aiic.app.domain.model.User
import com.aiic.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.login(email, password)
}

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> =
        authRepository.register(name, email, password)
}

class SendPasswordResetUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> =
        authRepository.sendPasswordReset(email)
}

class GetCurrentUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.getCurrentUser()
}

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.logout()
}
