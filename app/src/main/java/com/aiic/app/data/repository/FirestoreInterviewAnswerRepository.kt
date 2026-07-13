package com.aiic.app.data.repository

import android.util.Log
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.model.InterviewAnswer
import com.aiic.app.domain.repository.GenerativeAiRepository
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreInterviewAnswerRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val generativeAiRepository: GenerativeAiRepository
) : InterviewAnswerRepository {

    companion object { private const val TAG = "AIIC_ANS_REPO" }

    private val answersCache = mutableListOf<InterviewAnswer>()

    override suspend fun submitAnswer(answer: InterviewAnswer): NetworkResult<Unit> {
        answersCache.add(answer)
        
        try {
            firestore.collection("sessions")
                .document(answer.sessionId)
                .collection("answers")
                .document(answer.answerId)
                .set(mapOf(
                    "answerId" to answer.answerId,
                    "sessionId" to answer.sessionId,
                    "questionId" to answer.questionId,
                    "content" to answer.content,
                    "responseTimeMs" to answer.responseTimeMs,
                    "aiEvaluationScore" to answer.aiEvaluationScore,
                    "submittedAt" to System.currentTimeMillis()
                ))
            Log.d(TAG, "submitAnswer: persisted to Firestore answerId=${answer.answerId}")
        } catch (e: Exception) {
            Log.e(TAG, "submitAnswer: Firestore persist failed (non-critical): ${e.message}")
        }
        return NetworkResult.Success(Unit)
    }

    override suspend fun getAnswersForSession(sessionId: String): NetworkResult<List<InterviewAnswer>> {
        
        val sessionAnswers = answersCache.filter { it.sessionId == sessionId }
        if (sessionAnswers.isNotEmpty()) {
            Log.d(TAG, "getAnswersForSession: Returning ${sessionAnswers.size} answers from cache")
            return NetworkResult.Success(sessionAnswers)
        }

        return try {
            Log.d(TAG, "getAnswersForSession: Cache miss, fetching from Firestore for session=$sessionId")
            val snapshot = firestore.collection("sessions")
                .document(sessionId)
                .collection("answers")
                .get()
                .await()

            val firestoreAnswers = snapshot.documents.mapNotNull { doc ->
                try {
                    InterviewAnswer(
                        answerId = doc.getString("answerId") ?: doc.id,
                        sessionId = doc.getString("sessionId") ?: sessionId,
                        questionId = doc.getString("questionId") ?: "",
                        content = doc.getString("content") ?: "",
                        responseTimeMs = doc.getLong("responseTimeMs") ?: 0L,
                        aiEvaluationScore = (doc.getDouble("aiEvaluationScore") ?: 0.0).toFloat()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "getAnswersForSession: Failed to parse doc ${doc.id}: ${e.message}")
                    null
                }
            }
            Log.d(TAG, "getAnswersForSession: Loaded ${firestoreAnswers.size} answers from Firestore")
            
            answersCache.addAll(firestoreAnswers.filter { fsAnswer ->
                answersCache.none { it.answerId == fsAnswer.answerId }
            })
            NetworkResult.Success(firestoreAnswers)
        } catch (e: Exception) {
            Log.e(TAG, "getAnswersForSession: Firestore fallback failed: ${e.message}")
            NetworkResult.Success(emptyList())
        }
    }

    override suspend fun evaluateAnswer(
        question: String,
        answer: String
    ): NetworkResult<Pair<Float, String>> {
        val prompt = """
            You are an expert technical interviewer evaluating a candidate.
            Question asked: $question
            Candidate answer: $answer
            
            Evaluate this answer out of 100 based on accuracy, depth, and communication.
            If the answer is completely irrelevant, abusive (e.g. "fuck you"), or nonsense, give a SCORE: 0.
            Format your response exactly as:
            SCORE: [number 0-100]
            FEEDBACK: [1-2 sentences of feedback]
        """.trimIndent()

        val aiResult = generativeAiRepository.generateText(prompt)
        
        val aiResponse = aiResult.getOrNull()
        if (aiResponse != null) {
            val response = aiResponse
            var score = 0f
            var feedback = "Unable to provide specific feedback."
            
            try {
                val scoreRegex = Regex("SCORE:\\s*(\\d+)", RegexOption.IGNORE_CASE)
                val feedbackRegex = Regex("FEEDBACK:\\s*(.+)", RegexOption.DOT_MATCHES_ALL)
                
                scoreRegex.find(response)?.groupValues?.get(1)?.let {
                    score = it.toFloat()
                }
                
                feedbackRegex.find(response)?.groupValues?.get(1)?.let {
                    feedback = it.trim()
                }
                
                return NetworkResult.Success(Pair(score, feedback))
            } catch (e: Exception) {
                
            }
        }
        
        return NetworkResult.Success(Pair(0f, "We couldn't evaluate this answer correctly."))
    }
}
