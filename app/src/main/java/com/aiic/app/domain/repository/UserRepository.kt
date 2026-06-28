package com.aiic.app.domain.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUserProfile(profile: UserProfile): NetworkResult<Unit>
    suspend fun getUserProfile(uid: String): NetworkResult<UserProfile>
    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): NetworkResult<Unit>
    fun observeUserProfile(uid: String): Flow<UserProfile?>
    suspend fun uploadProfilePhoto(uid: String, uri: android.net.Uri): NetworkResult<String>
}
