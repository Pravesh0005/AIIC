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
            // Use deterministic document ID to avoid compound query (no composite index needed)
            val docId = "${userId}_${resumeId}"
            android.util.Log.d("AIIC_ANALYSIS", "getAnalysis: Looking up document ID = $docId")
            
            val document = firestore.collection(collectionPath)
                .document(docId)
                .get()
                .await()

            if (!document.exists()) {
                android.util.Log.d("AIIC_ANALYSIS", "getAnalysis: No analysis found for docId=$docId")
                NetworkResult.Error(message = "No analysis found for this resume.")
            } else {
                val dto = document.toObject(ResumeAnalysisDto::class.java)
                if (dto != null) {
                    android.util.Log.d("AIIC_ANALYSIS", "getAnalysis: Successfully loaded analysis, score=${dto.overallScore}")
                    NetworkResult.Success(dto.toDomain())
                } else {
                    android.util.Log.e("AIIC_ANALYSIS", "getAnalysis: Failed to parse DTO from document")
                    NetworkResult.Error(message = "Failed to parse resume analysis data.")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AIIC_ANALYSIS", "getAnalysis: Exception: ${e.message}", e)
            NetworkResult.Error(message = e.message ?: "An unknown error occurred while fetching analysis.")
        }
    }

    override suspend fun saveAnalysis(analysis: ResumeAnalysis): NetworkResult<Unit> {
        return try {
            // Use deterministic document ID: userId_resumeId
            val docId = "${analysis.userId}_${analysis.resumeId}"
            android.util.Log.d("AIIC_ANALYSIS", "saveAnalysis: Saving to document ID = $docId")
            
            firestore.collection(collectionPath)
                .document(docId)
                .set(analysis.toDto())
                .await()
            
            android.util.Log.d("AIIC_ANALYSIS", "saveAnalysis: Successfully saved analysis, score=${analysis.overallScore}")
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AIIC_ANALYSIS", "saveAnalysis: Exception: ${e.message}", e)
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
