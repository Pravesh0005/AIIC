package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.data.model.AnswerFeedbackDto
import com.aiic.app.data.model.SessionSummaryDto
import com.aiic.app.data.model.toDomain
import com.aiic.app.data.model.toDto
import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.model.SessionSummary
import com.aiic.app.domain.repository.FeedbackRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreFeedbackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FeedbackRepository {

    override suspend fun saveAnswerFeedback(feedback: AnswerFeedback): NetworkResult<Unit> {
        return try {
            val dto = feedback.toDto()
            firestore.collection("sessions")
                .document(feedback.sessionId)
                .collection("feedbacks")
                .document(feedback.feedbackId)
                .set(dto)
                .await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "Failed to save answer feedback")
        }
    }

    override suspend fun getFeedbackForAnswer(answerId: String): NetworkResult<AnswerFeedback> {
        // Since we don't know sessionId here directly without querying, we could structure our queries better
        // Let's assume answerId is unique globally across a collection group or we just query by it
        return try {
            val querySnapshot = firestore.collectionGroup("feedbacks")
                .whereEqualTo("question_id", answerId)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            if (document != null) {
                val dto = document.toObject(AnswerFeedbackDto::class.java)
                if (dto != null) {
                    return NetworkResult.Success(dto.toDomain())
                }
            }
            NetworkResult.Error(message = "Feedback not found")
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "Failed to get feedback")
        }
    }

    override suspend fun generateAndSaveSessionSummary(sessionId: String): NetworkResult<SessionSummary> {
        return try {
            val snapshot = firestore.collection("sessions")
                .document(sessionId)
                .collection("feedbacks")
                .get()
                .await()

            val feedbacks = snapshot.documents.mapNotNull { it.toObject(AnswerFeedbackDto::class.java)?.toDomain() }

            if (feedbacks.isEmpty()) {
                return NetworkResult.Error(message = "No feedbacks found to summarize")
            }

            val avgScore = feedbacks.map { it.overallScore }.average().toInt()
            
            // Basic aggregation for demonstration
            val allStrengths = feedbacks.flatMap { it.strengths }.distinct().take(5)
            val allWeaknesses = feedbacks.flatMap { it.weaknesses }.distinct().take(5)
            val allSuggestions = feedbacks.flatMap { it.improvementSuggestions }.distinct().take(5)

            val summary = SessionSummary(
                sessionId = sessionId,
                averageScore = avgScore,
                strongAreas = allStrengths,
                weakAreas = allWeaknesses,
                priorityImprovements = allSuggestions,
                roleReadiness = if (avgScore >= 80) "Ready" else if (avgScore >= 60) "Almost Ready" else "Needs Practice"
            )

            firestore.collection("sessions")
                .document(sessionId)
                .collection("summary")
                .document("final")
                .set(summary.toDto())
                .await()

            NetworkResult.Success(summary)
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "Failed to generate summary")
        }
    }

    override suspend fun getSessionSummary(sessionId: String): NetworkResult<SessionSummary> {
        return try {
            val document = firestore.collection("sessions")
                .document(sessionId)
                .collection("summary")
                .document("final")
                .get()
                .await()

            val dto = document.toObject(SessionSummaryDto::class.java)
            if (dto != null) {
                NetworkResult.Success(dto.toDomain())
            } else {
                NetworkResult.Error(message = "Session summary not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "Failed to get session summary")
        }
    }
}
