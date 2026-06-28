package com.aiic.app.data.repository

import android.net.Uri
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.data.model.ResumeDto
import com.aiic.app.data.model.toMap
import com.aiic.app.domain.model.Resume
import com.aiic.app.domain.model.UploadProgress
import com.aiic.app.domain.repository.ResumeRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreResumeRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : ResumeRepository {

    private val resumesCollection get() = firestore.collection("resumes")
    private var currentUploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    override fun uploadResume(
        userId: String,
        resumeId: String,
        fileName: String,
        fileUri: Uri,
        fileSize: Long
    ): Flow<NetworkResult<UploadProgress>> = callbackFlow {
        val storageRef = storage.reference.child("resumes/$userId/$resumeId.pdf")
        val metadata = com.google.firebase.storage.StorageMetadata.Builder()
            .setContentType("application/pdf")
            .build()

        currentUploadTask = storageRef.putFile(fileUri, metadata)

        // Listen for progress updates
        currentUploadTask?.addOnProgressListener { snapshot ->
            val transferred = snapshot.bytesTransferred
            val total = snapshot.totalByteCount
            val safeTransferred = if (total > 0 && transferred >= total) total - 1 else transferred
            trySend(NetworkResult.Success(UploadProgress(safeTransferred, total)))
        }

        // Await completion in a coroutine
        kotlinx.coroutines.launch {
            try {
                currentUploadTask?.await()
                trySend(NetworkResult.Success(UploadProgress(fileSize, fileSize)))
                close()
            } catch (e: Exception) {
                trySend(NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Upload failed", throwable = e))
                close()
            }
        }

        awaitClose { 
            currentUploadTask = null 
        }
    }

    override suspend fun createResumeMetadata(resume: Resume): NetworkResult<Resume> {
        return try {
            val storageRef = storage.reference.child("resumes/${resume.userId}/${resume.resumeId}.pdf")
            
            // Add retry logic for downloadUrl to handle Google Cloud Storage eventual consistency latency
            var downloadUrl: String? = null
            var retryCount = 0
            while (downloadUrl == null && retryCount < 5) {
                try {
                    downloadUrl = storageRef.downloadUrl.await().toString()
                } catch (e: Exception) {
                    retryCount++
                    if (retryCount >= 5) throw e
                    kotlinx.coroutines.delay(1000)
                }
            }
            
            val updatedResume = resume.copy(
                fileUrl = downloadUrl ?: "",
                uploadDate = System.currentTimeMillis(),
                lastUpdated = System.currentTimeMillis()
            )

            resumesCollection.document(updatedResume.resumeId).set(updatedResume.toMap()).await()
            NetworkResult.Success(updatedResume)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to save resume metadata", throwable = e)
        }
    }

    override suspend fun getResumeHistory(userId: String): NetworkResult<List<Resume>> {
        return try {
            val snapshot = resumesCollection
                .whereEqualTo("userId", userId)
                .orderBy("resumeVersion", Query.Direction.DESCENDING)
                .get()
                .await()
            val resumes = snapshot.toObjects(ResumeDto::class.java).map { it.toDomain() }
            NetworkResult.Success(resumes)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to fetch resume history", throwable = e)
        }
    }

    override fun observeResumeHistory(userId: String): Flow<List<Resume>> = callbackFlow {
        var registration: ListenerRegistration? = null
        registration = resumesCollection
            .whereEqualTo("userId", userId)
            .orderBy("resumeVersion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val resumes = snapshot?.toObjects(ResumeDto::class.java)?.map { it.toDomain() } ?: emptyList()
                trySend(resumes)
            }
        awaitClose { registration?.remove() }
    }

    override suspend fun getLatestResume(userId: String): NetworkResult<Resume?> {
        return try {
            val snapshot = resumesCollection
                .whereEqualTo("userId", userId)
                .orderBy("resumeVersion", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            
            val resume = snapshot.documents.firstOrNull()?.toObject(ResumeDto::class.java)?.toDomain()
            NetworkResult.Success(resume)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to fetch latest resume", throwable = e)
        }
    }

    override suspend fun getResumeById(resumeId: String): NetworkResult<Resume> {
        return try {
            val snapshot = resumesCollection.document(resumeId).get().await()
            if (snapshot.exists()) {
                NetworkResult.Success(snapshot.toObject(ResumeDto::class.java)!!.toDomain())
            } else {
                NetworkResult.Error(code = 404, message = "Resume not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to fetch resume", throwable = e)
        }
    }

    override suspend fun setActiveResume(userId: String, resumeId: String): NetworkResult<Unit> {
        return try {
            val snapshot = resumesCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("activeResume", true)
                .get()
                .await()
                
            firestore.runTransaction { transaction ->
                for (doc in snapshot.documents) {
                    transaction.update(doc.reference, "activeResume", false)
                }

                transaction.update(resumesCollection.document(resumeId), "activeResume", true)
            }.await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to set active resume", throwable = e)
        }
    }

    override suspend fun getActiveResume(userId: String): NetworkResult<Resume?> {
        return try {
            val snapshot = resumesCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("activeResume", true)
                .limit(1)
                .get()
                .await()
            
            val resume = snapshot.documents.firstOrNull()?.toObject(ResumeDto::class.java)?.toDomain()
            NetworkResult.Success(resume)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to fetch active resume", throwable = e)
        }
    }

    override suspend fun deleteResume(resumeId: String, userId: String, version: Int): NetworkResult<Unit> {
        return try {
            val storageRef = storage.reference.child("resumes/$userId/$resumeId.pdf")
            try {
                storageRef.delete().await()
            } catch (e: Exception) {
                // Proceed even if storage delete fails (file might not exist)
            }
            resumesCollection.document(resumeId).delete().await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to delete resume", throwable = e)
        }
    }

    override suspend fun updateResumeMetadata(resumeId: String, updates: Map<String, Any>): NetworkResult<Unit> {
        return try {
            val finalUpdates = updates.toMutableMap()
            finalUpdates["lastUpdated"] = System.currentTimeMillis()
            resumesCollection.document(resumeId).update(finalUpdates).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(code = 500, message = e.localizedMessage ?: "Failed to update resume metadata", throwable = e)
        }
    }

    override suspend fun getNextVersionNumber(userId: String): Int {
        return try {
            val snapshot = resumesCollection
                .whereEqualTo("userId", userId)
                .orderBy("resumeVersion", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
            
            val latestVersion = snapshot.documents.firstOrNull()?.getLong("resumeVersion")?.toInt() ?: 0
            latestVersion + 1
        } catch (e: Exception) {
            1
        }
    }

    override fun cancelUpload() {
        currentUploadTask?.cancel()
        currentUploadTask = null
    }
}
