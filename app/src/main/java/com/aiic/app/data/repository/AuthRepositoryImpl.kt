package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.data.remote.FirebaseErrorMapper
import com.aiic.app.domain.model.AuthSession
import com.aiic.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<AuthSession> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return NetworkResult.Error(
                code = 401, message = "Authentication failed"
            )
            NetworkResult.Success(user.toAuthSession())
        } catch (e: Exception) {
            NetworkResult.Error(
                code = 401,
                message = FirebaseErrorMapper.map(e),
                throwable = e,
            )
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): NetworkResult<AuthSession> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return NetworkResult.Error(
                code = 500, message = "Account creation failed"
            )
            user.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            ).await()
            NetworkResult.Success(user.toAuthSession().copy(displayName = name))
        } catch (e: Exception) {
            NetworkResult.Error(
                code = 400,
                message = FirebaseErrorMapper.map(e),
                throwable = e,
            )
        }
    }

    override suspend fun sendPasswordReset(email: String): NetworkResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(
                code = 400,
                message = FirebaseErrorMapper.map(e),
                throwable = e,
            )
        }
    }

    override suspend fun loginWithGoogle(idToken: String): NetworkResult<AuthSession> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return NetworkResult.Error(
                code = 401, message = "Google sign-in failed"
            )
            NetworkResult.Success(user.toAuthSession())
        } catch (e: Exception) {
            NetworkResult.Error(
                code = 401,
                message = FirebaseErrorMapper.map(e),
                throwable = e,
            )
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override fun observeAuthState(): Flow<AuthSession?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toAuthSession())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun getCurrentSession(): AuthSession? = auth.currentUser?.toAuthSession()

    private fun com.google.firebase.auth.FirebaseUser.toAuthSession() = AuthSession(
        uid = uid,
        email = email ?: "",
        displayName = displayName,
        isEmailVerified = isEmailVerified,
        photoUrl = photoUrl?.toString(),
    )
}
