package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.UserPreferences
import com.aiic.app.domain.model.UserProfile
import com.aiic.app.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: com.google.firebase.storage.FirebaseStorage,
) : UserRepository {

    private val usersCollection get() = firestore.collection("users")

    override suspend fun createUserProfile(profile: UserProfile): NetworkResult<Unit> {
        return try {
            usersCollection.document(profile.uid).set(profile.toMap(), SetOptions.merge()).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to create profile", throwable = e)
        }
    }

    override suspend fun getUserProfile(uid: String): NetworkResult<UserProfile> {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            if (snapshot.exists()) {
                NetworkResult.Success(snapshot.toUserProfile())
            } else {
                NetworkResult.Error(code = 404, message = "Profile not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to fetch profile", throwable = e)
        }
    }

    override suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): NetworkResult<Unit> {
        return try {
            usersCollection.document(uid).set(updates, SetOptions.merge()).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to update profile", throwable = e)
        }
    }

    override fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        var registration: ListenerRegistration? = null
        registration = usersCollection.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            trySend(snapshot?.toUserProfile())
        }
        awaitClose { registration?.remove() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun com.google.firebase.firestore.DocumentSnapshot.toUserProfile(): UserProfile {
        return UserProfile(
            uid = getString("uid") ?: id,
            name = getString("name") ?: "",
            email = getString("email") ?: "",
            profilePhotoUrl = getString("profilePhotoUrl") ?: "",
            gender = getString("gender") ?: "",
            targetRole = getString("targetRole") ?: "",
            targetCompany = getString("targetCompany") ?: "",
            education = getString("education") ?: "",
            skills = get("skills") as? List<String> ?: emptyList(),
            onboardingCompleted = getBoolean("onboardingCompleted") ?: false,
            interviewCount = getLong("interviewCount")?.toInt() ?: 0,
            totalPracticeHours = getDouble("totalPracticeHours")?.toFloat() ?: 0f,
            resumeScore = getDouble("resumeScore")?.toFloat() ?: 0f,
            readinessScore = getDouble("readinessScore")?.toFloat() ?: 0f,
            isPremium = getBoolean("isPremium") ?: false,
            createdAt = getLong("createdAt") ?: 0L,
            lastActiveAt = getLong("lastActiveAt") ?: 0L,
        )
    }

    private fun UserProfile.toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "profilePhotoUrl" to profilePhotoUrl,
        "gender" to gender,
        "targetRole" to targetRole,
        "targetCompany" to targetCompany,
        "education" to education,
        "skills" to skills,
        "onboardingCompleted" to onboardingCompleted,
        "interviewCount" to interviewCount,
        "totalPracticeHours" to totalPracticeHours,
        "resumeScore" to resumeScore,
        "readinessScore" to readinessScore,
        "isPremium" to isPremium,
        "createdAt" to createdAt,
        "lastActiveAt" to lastActiveAt,
    )

    override suspend fun uploadProfilePhoto(uid: String, uri: android.net.Uri): NetworkResult<String> {
        return try {
            val path = "profiles/$uid/avatar.jpg"
            val storageRef = storage.reference.child(path)
            
            // 1. Copy content URI to local cache to verify read access and size
            val context = com.google.firebase.FirebaseApp.getInstance().applicationContext
            val localFile = java.io.File(context.cacheDir, "temp_avatar_$uid.jpg")
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    java.io.FileOutputStream(localFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                return NetworkResult.Error(code = 500, message = "Local file read failed: ${e.localizedMessage}", throwable = e)
            }
            
            val fileSize = localFile.length()
            android.util.Log.d("AIIC_STORAGE", "Local file size: $fileSize bytes")
            if (fileSize == 0L) {
                return NetworkResult.Error(code = 400, message = "Selected image is empty (0 bytes)")
            }

            val metadata = com.google.firebase.storage.StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()
                
            try {
                android.util.Log.d("AIIC_STORAGE", "Starting putFile to: $path using local file")
                val snapshot = storageRef.putFile(android.net.Uri.fromFile(localFile), metadata).await()
                android.util.Log.d("AIIC_STORAGE", "putFile succeeded. Bytes transferred: ${snapshot.bytesTransferred}")
            } catch (e: Exception) {
                android.util.Log.e("AIIC_STORAGE", "putFile failed: ${e.message}", e)
                return NetworkResult.Error(code = 500, message = "Upload failed (putFile): ${e.localizedMessage}", throwable = e)
            } finally {
                localFile.delete() // Cleanup
            }
            
            val downloadUrl = try {
                android.util.Log.d("AIIC_STORAGE", "Fetching downloadUrl for: $path")
                var url: String? = null
                var retries = 0
                while (url == null && retries < 5) {
                    try {
                        url = storageRef.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        retries++
                        if (retries >= 5) throw e
                        kotlinx.coroutines.delay(1000)
                    }
                }
                url!!
            } catch (e: Exception) {
                android.util.Log.e("AIIC_STORAGE", "downloadUrl failed: ${e.message}", e)
                return NetworkResult.Error(code = 500, message = "URL fetch failed (downloadUrl): ${e.localizedMessage}", throwable = e)
            }
            
            android.util.Log.d("AIIC_STORAGE", "Upload completely successful. URL: $downloadUrl")
            NetworkResult.Success(downloadUrl)
        } catch (e: Exception) {
            android.util.Log.e("AIIC_STORAGE", "Overall error: ${e.message}", e)
            NetworkResult.Error(code = 500, message = "Overall upload error: ${e.localizedMessage}", throwable = e)
        }
    }
}
