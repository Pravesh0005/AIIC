package com.aiic.app.data.repository

import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.data.model.AnswerFeedbackDto
import com.aiic.app.data.model.SessionSummaryDto
import com.aiic.app.data.model.toDomain
import com.aiic.app.data.model.toDto
import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.model.InterviewAnswer
import com.aiic.app.domain.model.SessionSummary
import com.aiic.app.domain.repository.FeedbackRepository
import com.aiic.app.domain.repository.GenerativeAiRepository
import com.aiic.app.domain.repository.InterviewAnswerRepository
import com.aiic.app.domain.repository.InterviewQuestionRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreFeedbackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val answerRepository: InterviewAnswerRepository,
    private val questionRepository: InterviewQuestionRepository,
    private val sessionRepository: InterviewSessionRepository,
    private val generativeAiRepository: GenerativeAiRepository
) : FeedbackRepository {

    private val feedbackCache = mutableMapOf<String, AnswerFeedback>()
    private val summaryCache = mutableMapOf<String, SessionSummary>()
    private val gson = Gson()

    override suspend fun saveAnswerFeedback(feedback: AnswerFeedback): NetworkResult<Unit> {
        
        feedbackCache[feedback.feedbackId] = feedback

        try {
            val dto = feedback.toDto()
            firestore.collection("sessions")
                .document(feedback.sessionId)
                .collection("feedbacks")
                .document(feedback.feedbackId)
                .set(dto)
        } catch (_: Exception) {
            
        }
        return NetworkResult.Success(Unit)
    }

    override suspend fun getFeedbackForAnswer(answerId: String): NetworkResult<AnswerFeedback> {
        
        val cached = feedbackCache.values.find { it.questionId == answerId }
        if (cached != null) {
            return NetworkResult.Success(cached)
        }

        return try {
            val querySnapshot = firestore.collectionGroup("feedbacks")
                .whereEqualTo("question_id", answerId)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            if (document != null) {
                val dto = document.toObject(AnswerFeedbackDto::class.java)
                if (dto != null) {
                    val domain = dto.toDomain()
                    feedbackCache[domain.feedbackId] = domain
                    return NetworkResult.Success(domain)
                }
            }
            NetworkResult.Error(message = "Feedback not found")
        } catch (e: Exception) {
            NetworkResult.Error(message = "Feedback not available offline")
        }
    }

    override suspend fun generateAndSaveSessionSummary(sessionId: String): NetworkResult<SessionSummary> {
        android.util.Log.d("AIIC_DEBUG", "Entering generateAndSaveSessionSummary for sessionId: $sessionId")
        
        summaryCache[sessionId]?.let { return NetworkResult.Success(it) }

        return try {
            
            val sessionResult = sessionRepository.getSessionById(sessionId)
            val session = sessionResult.getOrNull()

            val answersResult = answerRepository.getAnswersForSession(sessionId)
            val answers = answersResult.getOrNull() ?: emptyList()

            val questionsResult = questionRepository.getQuestionsForSession(sessionId)
            val questions = questionsResult.getOrNull() ?: emptyList()

            android.util.Log.d("AIIC_DEBUG", "generateAndSaveSessionSummary: Fetched ${answers.size} answers, ${questions.size} questions")

            if (answers.isEmpty()) {
                android.util.Log.d("AIIC_DEBUG", "generateAndSaveSessionSummary: Answers empty, returning basic summary")
                
                val basicSummary = SessionSummary(
                    sessionId = sessionId,
                    averageScore = 0,
                    strongAreas = listOf("Session completed"),
                    weakAreas = listOf("No detailed analysis available"),
                    priorityImprovements = listOf("You did not provide any answers."),
                    roleReadiness = "Evaluation Unavailable"
                )
                summaryCache[sessionId] = basicSummary
                return NetworkResult.Success(basicSummary)
            }

            android.util.Log.d("AIIC_DEBUG", "generateAndSaveSessionSummary: Calling tryAiSummary")
            val aiSummary = tryAiSummary(sessionId, session?.role ?: "Software Engineer", questions, answers)
            if (aiSummary != null) {
                android.util.Log.d("AIIC_DEBUG", "generateAndSaveSessionSummary: tryAiSummary succeeded, returning AI summary")
                summaryCache[sessionId] = aiSummary
                trySyncSummaryToFirestore(aiSummary)
                return NetworkResult.Success(aiSummary)
            }

            android.util.Log.e("AIIC_DEBUG", "generateAndSaveSessionSummary: tryAiSummary failed, calling buildLocalSummary fallback")
            val localSummary = buildLocalSummary(sessionId, session?.role ?: "Unknown", session?.score ?: 0f, answers, questions)
            summaryCache[sessionId] = localSummary
            trySyncSummaryToFirestore(localSummary)
            NetworkResult.Success(localSummary)

        } catch (e: Exception) {
            android.util.Log.e("AIIC_DEBUG", "generateAndSaveSessionSummary: Exception caught", e)
            
            val fallbackSummary = SessionSummary(
                sessionId = sessionId,
                averageScore = 0,
                strongAreas = listOf("Session completed"),
                weakAreas = listOf("Detailed AI feedback processing"),
                priorityImprovements = listOf("View full report for detailed insights."),
                roleReadiness = "Review Full Report"
            )
            summaryCache[sessionId] = fallbackSummary
            NetworkResult.Success(fallbackSummary)
        }
    }

    override suspend fun getSessionSummary(sessionId: String): NetworkResult<SessionSummary> {
        
        summaryCache[sessionId]?.let { return NetworkResult.Success(it) }

        return try {
            val document = firestore.collection("sessions")
                .document(sessionId)
                .collection("summary")
                .document("final")
                .get()
                .await()

            val dto = document.toObject(SessionSummaryDto::class.java)
            if (dto != null) {
                val summary = dto.toDomain()
                summaryCache[sessionId] = summary
                NetworkResult.Success(summary)
            } else {
                NetworkResult.Error(message = "Session summary not found")
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = "Summary not available offline")
        }
    }

    private suspend fun tryAiSummary(
        sessionId: String,
        role: String,
        questions: List<com.aiic.app.domain.model.InterviewQuestion>,
        answers: List<InterviewAnswer>
    ): SessionSummary? {
        try {
            android.util.Log.d("AIIC_DEBUG", "Entering tryAiSummary for sessionId: $sessionId")
            val qaPairs = questions.mapIndexed { index, q ->
                val answer = answers.find { it.questionId == q.questionId }
                "Q${index + 1}: ${q.content}\nA${index + 1}: ${answer?.content ?: "(no answer)"}"
            }.joinToString("\n\n")

            val prompt = """
You are an expert interview coach analyzing a mock interview session.

ROLE: $role
INTERVIEW Q&A:
$qaPairs

Analyze the candidate's performance and return a JSON object with these exact fields:
{
  "averageScore": <number 0-100>,
  "strongAreas": [<list of 2-4 specific strengths shown in answers>],
  "weakAreas": [<list of 2-4 specific areas needing improvement>],
  "priorityImprovements": [<list of 2-4 actionable improvement suggestions>],
  "roleReadiness": "<one of: Ready, Almost Ready, Needs Practice>"
}

Be specific to the actual answers given. Do not be generic.
Return ONLY valid JSON, no markdown.
""".trimIndent()

            android.util.Log.d("AIIC_DEBUG", "tryAiSummary: Prompt built, calling generateJson")
            val aiResult = generativeAiRepository.generateJson(prompt)
            val responseText = aiResult.getOrNull()
            
            if (responseText == null) {
                android.util.Log.e("AIIC_DEBUG", "tryAiSummary: Groq generateJson returned null/Error")
                return null
            }

            val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
            android.util.Log.d("AIIC_DEBUG", "tryAiSummary: Parsing JSON: $cleanJson")
            
            val parsed = gson.fromJson(cleanJson, Map::class.java)

            @Suppress("UNCHECKED_CAST")
            val summary = SessionSummary(
                sessionId = sessionId,
                averageScore = (parsed["averageScore"] as? Number)?.toInt() ?: 50,
                strongAreas = (parsed["strongAreas"] as? List<String>) ?: listOf("Completed interview"),
                weakAreas = (parsed["weakAreas"] as? List<String>) ?: listOf("Needs more detail"),
                priorityImprovements = (parsed["priorityImprovements"] as? List<String>) ?: listOf("Practice more"),
                roleReadiness = (parsed["roleReadiness"] as? String) ?: "Needs Practice"
            )
            
            android.util.Log.d("AIIC_DEBUG", "tryAiSummary: Successfully parsed SessionSummary with score: ${summary.averageScore}")
            return summary
        } catch (e: Exception) {
            android.util.Log.e("AIIC_DEBUG", "tryAiSummary: Exception caught during JSON parsing or execution", e)
            return null
        }
    }

    private fun buildLocalSummary(
        sessionId: String,
        role: String,
        score: Float,
        answers: List<InterviewAnswer>,
        questions: List<com.aiic.app.domain.model.InterviewQuestion>
    ): SessionSummary {
        return SessionSummary(
            sessionId = sessionId,
            averageScore = score.toInt(),
            strongAreas = listOf("Session completed"),
            weakAreas = listOf("Detailed AI feedback processing"),
            priorityImprovements = listOf("View full report for detailed insights."),
            roleReadiness = "Review Full Report"
        )
    }

    private fun trySyncSummaryToFirestore(summary: SessionSummary) {
        try {
            firestore.collection("sessions")
                .document(summary.sessionId)
                .collection("summary")
                .document("final")
                .set(summary.toDto())
        } catch (_: Exception) {
            
        }
    }
}
