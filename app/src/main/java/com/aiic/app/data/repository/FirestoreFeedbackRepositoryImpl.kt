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

/**
 * Local-first feedback repository.
 * 
 * Interview data lives in-memory (session, questions, answers repos are all in-memory caches).
 * This impl generates AI-powered summaries using Groq via the answers already held in memory,
 * then optionally syncs to Firestore in the background.
 * 
 * NEVER blocks on Firestore reads for critical user flows.
 */
class FirestoreFeedbackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val answerRepository: InterviewAnswerRepository,
    private val questionRepository: InterviewQuestionRepository,
    private val sessionRepository: InterviewSessionRepository,
    private val generativeAiRepository: GenerativeAiRepository
) : FeedbackRepository {

    // Local cache for feedbacks to avoid re-generation
    private val feedbackCache = mutableMapOf<String, AnswerFeedback>()
    private val summaryCache = mutableMapOf<String, SessionSummary>()
    private val gson = Gson()

    override suspend fun saveAnswerFeedback(feedback: AnswerFeedback): NetworkResult<Unit> {
        // Save locally first (always succeeds)
        feedbackCache[feedback.feedbackId] = feedback

        // Try to sync to Firestore in background — don't block on failure
        try {
            val dto = feedback.toDto()
            firestore.collection("sessions")
                .document(feedback.sessionId)
                .collection("feedbacks")
                .document(feedback.feedbackId)
                .set(dto)
        } catch (_: Exception) {
            // Firestore sync failed — that's OK, we have local data
        }
        return NetworkResult.Success(Unit)
    }

    override suspend fun getFeedbackForAnswer(answerId: String): NetworkResult<AnswerFeedback> {
        // Check local cache first
        val cached = feedbackCache.values.find { it.questionId == answerId }
        if (cached != null) {
            return NetworkResult.Success(cached)
        }

        // Fallback to Firestore only if local cache misses
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
        // Check cache first
        summaryCache[sessionId]?.let { return NetworkResult.Success(it) }

        return try {
            // 1. Get session data from in-memory repo
            val sessionResult = sessionRepository.getSessionById(sessionId)
            val session = sessionResult.getOrNull()

            // 2. Get answers from in-memory repo
            val answersResult = answerRepository.getAnswersForSession(sessionId)
            val answers = answersResult.getOrNull() ?: emptyList()

            // 3. Get questions from in-memory repo
            val questionsResult = questionRepository.getQuestionsForSession(sessionId)
            val questions = questionsResult.getOrNull() ?: emptyList()

            if (answers.isEmpty()) {
                // No answers saved — generate a basic summary from session data
                val basicSummary = SessionSummary(
                    sessionId = sessionId,
                    averageScore = session?.score?.toInt() ?: 0,
                    strongAreas = listOf("Session completed"),
                    weakAreas = listOf("No detailed analysis available"),
                    priorityImprovements = listOf("Practice more with different question types"),
                    roleReadiness = if ((session?.score ?: 0f) >= 60f) "Almost Ready" else "Needs Practice"
                )
                summaryCache[sessionId] = basicSummary
                return NetworkResult.Success(basicSummary)
            }

            // 4. Try AI-powered summary via Groq
            val aiSummary = tryAiSummary(sessionId, session?.role ?: "Software Engineer", questions, answers)
            if (aiSummary != null) {
                summaryCache[sessionId] = aiSummary
                trySyncSummaryToFirestore(aiSummary)
                return NetworkResult.Success(aiSummary)
            }

            // 5. Fallback: Build summary locally without AI
            val localSummary = buildLocalSummary(sessionId, session?.role ?: "Unknown", session?.score ?: 0f, answers, questions)
            summaryCache[sessionId] = localSummary
            trySyncSummaryToFirestore(localSummary)
            NetworkResult.Success(localSummary)

        } catch (e: Exception) {
            // Ultimate fallback — never show raw exception to user
            val fallbackSummary = SessionSummary(
                sessionId = sessionId,
                averageScore = 0,
                strongAreas = listOf("Interview completed successfully"),
                weakAreas = listOf("Detailed analysis unavailable"),
                priorityImprovements = listOf("Try again when online for full AI analysis"),
                roleReadiness = "Needs Practice"
            )
            summaryCache[sessionId] = fallbackSummary
            NetworkResult.Success(fallbackSummary)
        }
    }

    override suspend fun getSessionSummary(sessionId: String): NetworkResult<SessionSummary> {
        // Local cache first
        summaryCache[sessionId]?.let { return NetworkResult.Success(it) }

        // Try Firestore
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

    // ── AI-Powered Summary via Groq ──

    private suspend fun tryAiSummary(
        sessionId: String,
        role: String,
        questions: List<com.aiic.app.domain.model.InterviewQuestion>,
        answers: List<InterviewAnswer>
    ): SessionSummary? {
        try {
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

            val aiResult = generativeAiRepository.generateText(prompt)
            val responseText = aiResult.getOrNull() ?: return null

            val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
            val parsed = gson.fromJson(cleanJson, Map::class.java)

            @Suppress("UNCHECKED_CAST")
            return SessionSummary(
                sessionId = sessionId,
                averageScore = (parsed["averageScore"] as? Number)?.toInt() ?: 50,
                strongAreas = (parsed["strongAreas"] as? List<String>) ?: listOf("Completed interview"),
                weakAreas = (parsed["weakAreas"] as? List<String>) ?: listOf("Needs more detail"),
                priorityImprovements = (parsed["priorityImprovements"] as? List<String>) ?: listOf("Practice more"),
                roleReadiness = (parsed["roleReadiness"] as? String) ?: "Needs Practice"
            )
        } catch (_: Exception) {
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
        val answeredCount = answers.size
        val totalQuestions = questions.size
        val avgResponseTime = if (answers.isNotEmpty()) answers.map { it.responseTimeMs }.average() else 0.0

        val strongAreas = mutableListOf<String>()
        val weakAreas = mutableListOf<String>()
        val improvements = mutableListOf<String>()

        if (answeredCount == totalQuestions) strongAreas.add("Completed all $totalQuestions questions")
        else weakAreas.add("Only answered $answeredCount of $totalQuestions questions")

        val avgLen = answers.map { it.content.length }.average()
        if (avgLen > 100) strongAreas.add("Detailed and thorough answers")
        else weakAreas.add("Answers could be more detailed")

        if (avgResponseTime > 0 && avgResponseTime < 120000) strongAreas.add("Good response speed")
        
        strongAreas.add("Practice session for $role completed")
        improvements.add("Review answers and identify areas for deeper technical knowledge")
        improvements.add("Practice explaining your thought process step by step")

        val readiness = when {
            score >= 80f -> "Ready"
            score >= 50f -> "Almost Ready"
            else -> "Needs Practice"
        }

        return SessionSummary(
            sessionId = sessionId,
            averageScore = score.toInt(),
            strongAreas = strongAreas.take(4),
            weakAreas = weakAreas.ifEmpty { listOf("No major issues detected") },
            priorityImprovements = improvements.take(4),
            roleReadiness = readiness
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
            // Background sync — don't block UI
        }
    }
}
