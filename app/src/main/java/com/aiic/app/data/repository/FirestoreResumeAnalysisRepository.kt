package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.data.model.ResumeAnalysisDto
import com.aiic.app.data.model.toDomain
import com.aiic.app.data.model.toDto
import com.aiic.app.domain.model.ResumeAnalysis
import com.aiic.app.domain.repository.ResumeAnalysisRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreResumeAnalysisRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ResumeAnalysisRepository {

    private val collectionPath = "resume_analysis"

    override suspend fun getAnalysis(userId: String, resumeId: String): NetworkResult<ResumeAnalysis> {
        return try {
            val snapshot = firestore.collection(collectionPath)
                .whereEqualTo("userId", userId)
                .whereEqualTo("resumeId", resumeId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                NetworkResult.Error(message = "No analysis found for this resume.")
            } else {
                val dto = snapshot.documents.first().toObject(ResumeAnalysisDto::class.java)
                if (dto != null) {
                    NetworkResult.Success(dto.toDomain())
                } else {
                    NetworkResult.Error(message = "Failed to parse resume analysis data.")
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "An unknown error occurred while fetching analysis.")
        }
    }

    override suspend fun saveAnalysis(analysis: ResumeAnalysis): NetworkResult<Unit> {
        return try {
            firestore.collection(collectionPath)
                .document(analysis.analysisId)
                .set(analysis.toDto())
                .await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "An unknown error occurred while saving analysis.")
        }
    }

    override fun observeUserAnalyses(userId: String): Flow<List<ResumeAnalysis>> = callbackFlow {
        val subscription = firestore.collection(collectionPath)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val analyses = snapshot?.documents?.mapNotNull { 
                    it.toObject(ResumeAnalysisDto::class.java)?.toDomain() 
                } ?: emptyList()
                
                // Sort by timestamp descending
                trySend(analyses.sortedByDescending { it.timestamp })
            }
            
        awaitClose { subscription.remove() }
    }
}
