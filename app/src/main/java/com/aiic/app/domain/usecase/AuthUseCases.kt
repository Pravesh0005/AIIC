package com.aiic.app.domain.usecase

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.onSuccess
import com.aiic.app.domain.model.AuthSession
import com.aiic.app.domain.model.UserProfile
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.repository.SessionRepository
import com.aiic.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<AuthSession> {
        val result = authRepository.login(email, password)
        result.onSuccess { session ->
            sessionRepository.saveSession(session.uid, session.email, session.displayName ?: "")
        }
        return result
    }
}

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
    ): NetworkResult<AuthSession> {
        val result = authRepository.register(name, email, password)
        result.onSuccess { session ->
            sessionRepository.saveSession(session.uid, session.email, name)
            userRepository.createUserProfile(
                UserProfile(
                    uid = session.uid,
                    name = name,
                    email = session.email,
                    createdAt = System.currentTimeMillis(),
                    lastActiveAt = System.currentTimeMillis(),
                )
            )
        }
        return result
    }
}

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): NetworkResult<Unit> =
        authRepository.sendPasswordReset(email)
}

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke() {
        authRepository.logout()
        sessionRepository.clearSession()
    }
}

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.observeAuthState()
}

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(uid: String): NetworkResult<UserProfile> =
        userRepository.getUserProfile(uid)
}

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String, updates: Map<String, Any>): NetworkResult<Unit> =
        userRepository.updateUserProfile(uid, updates)
}

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(idToken: String): NetworkResult<AuthSession> {
        val result = authRepository.loginWithGoogle(idToken)
        result.onSuccess { session ->
            sessionRepository.saveSession(session.uid, session.email, session.displayName ?: "")
            val existing = userRepository.getUserProfile(session.uid)
            if (existing is NetworkResult.Error) {
                userRepository.createUserProfile(
                    UserProfile(
                        uid = session.uid,
                        name = session.displayName ?: "",
                        email = session.email,
                        profilePhotoUrl = session.photoUrl ?: "",
                        createdAt = System.currentTimeMillis(),
                        lastActiveAt = System.currentTimeMillis(),
                    )
                )
            }
        }
        return result
    }
}

class UploadProfilePhotoUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String, uri: android.net.Uri): NetworkResult<String> =
        userRepository.uploadProfilePhoto(uid, uri)
}
