package com.aiic.app.domain.usecase

import android.util.Log
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.getOrNull
import com.aiic.app.domain.ai.prompt.InterviewEvaluationPromptBuilder
import com.aiic.app.domain.model.*
import com.aiic.app.domain.repository.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.inject.Inject

class GenerateInterviewReportUseCase @Inject constructor(
    private val sessionRepository: InterviewSessionRepository,
    private val questionRepository: InterviewQuestionRepository,
    private val answerRepository: InterviewAnswerRepository,
    private val generativeAiRepository: GenerativeAiRepository,
    private val reportRepository: InterviewReportRepository
) {
    companion object {
        private const val TAG = "AIIC_REPORT_GEN"
    }

    suspend operator fun invoke(
        sessionId: String,
        voiceMetrics: VoiceMetrics? = null,
        bodyLanguageReport: BodyLanguageReport? = null
    ): NetworkResult<InterviewReport> {
        Log.d(TAG, "Generating report for session: $sessionId")

        val session = sessionRepository.getSessionById(sessionId).getOrNull()
            ?: return NetworkResult.Error(message = "Session not found")

        val questions = questionRepository.getQuestionsForSession(sessionId).getOrNull() ?: emptyList()
        val answers = answerRepository.getAnswersForSession(sessionId).getOrNull() ?: emptyList()

        if (questions.isEmpty() || answers.isEmpty()) {
            return NetworkResult.Error(message = "No interview data found")
        }

        val qaPairs = questions.sortedBy { it.order }.map { question ->
            val answer = answers.find { it.questionId == question.questionId }
            Pair(question.content, answer?.content ?: "No answer provided")
        }

        val voiceMetricsStr = voiceMetrics?.let {
            """
            - Words Per Minute: ${it.wordsPerMinute}
            - Total Words: ${it.totalWords}
            - Filler Word Count: ${it.fillerWordCount}
            - Filler Words: ${it.fillerWords}
            - Speech Confidence: ${it.speechConfidence}
            - Speech Duration: ${it.speechDurationMs}ms
            - Communication Score: ${it.communicationScore}
            """.trimIndent()
        }

        val bodyMetricsStr = bodyLanguageReport?.let {
            """
            - Eye Contact Score: ${it.eyeContactScore}
            - Confidence Score: ${it.confidenceScore}
            - Professionalism: ${it.professionalismScore}
            - Engagement: ${it.engagementScore}
            - Nervousness: ${it.nervousnessScore}
            - Overall Body Language: ${it.overallBodyLanguageScore}
            """.trimIndent()
        }

        val prompt = InterviewEvaluationPromptBuilder()
            .setQuestionAnswerPairs(qaPairs)
            .setTargetRole(session.role)
            .setInterviewType(session.interviewType.name)
            .setDifficulty(session.difficulty.name)
            .setTargetCompany(session.targetCompany)
            .setVoiceMetrics(voiceMetricsStr)
            .setBodyLanguageMetrics(bodyMetricsStr)
            .build()

        val aiResult = generativeAiRepository.generateJson(prompt)
        val jsonStr = aiResult.getOrNull()
            ?: return NetworkResult.Error(message = "AI evaluation failed")

        return try {
            val cleanJson = jsonStr.replace("```json", "").replace("```", "").trim()
            val gson = Gson()
            val json = gson.fromJson(cleanJson, JsonObject::class.java)

            val questionResults = questions.sortedBy { it.order }.mapIndexed { index, question ->
                val answer = answers.find { it.questionId == question.questionId }
                val qrArray = json.getAsJsonArray("questionResults")
                val qrJson = qrArray?.get(index.coerceAtMost((qrArray?.size() ?: 1) - 1))?.asJsonObject

                QuestionResult(
                    questionId = question.questionId,
                    question = question.content,
                    answer = answer?.content ?: "",
                    score = qrJson?.get("score")?.asFloat ?: (answer?.aiEvaluationScore ?: 0f),
                    feedback = qrJson?.get("feedback")?.asString ?: "",
                    category = question.category,
                    responseTimeMs = answer?.responseTimeMs ?: 0L
                )
            }

            val durationMs = if (session.startedAt > 0) {
                (session.endedAt ?: System.currentTimeMillis()) - session.startedAt
            } else 0L

            val report = InterviewReport(
                reportId = "${session.userId}_${sessionId}",
                sessionId = sessionId,
                userId = session.userId,
                generatedAt = System.currentTimeMillis(),
                overallScore = json.get("overallScore")?.asFloat ?: 0f,
                technicalAccuracyScore = json.get("technicalAccuracyScore")?.asFloat ?: 0f,
                communicationScore = json.get("communicationScore")?.asFloat ?: 0f,
                confidenceScore = json.get("confidenceScore")?.asFloat ?: 0f,
                problemSolvingScore = json.get("problemSolvingScore")?.asFloat ?: 0f,
                depthScore = json.get("depthScore")?.asFloat ?: 0f,
                structureScore = json.get("structureScore")?.asFloat ?: 0f,
                leadershipScore = json.get("leadershipScore")?.asFloat ?: 0f,
                examplesScore = json.get("examplesScore")?.asFloat ?: 0f,
                vocabularyScore = json.get("vocabularyScore")?.asFloat ?: 0f,
                professionalismScore = json.get("professionalismScore")?.asFloat ?: 0f,
                roleReadiness = json.get("roleReadiness")?.asString ?: "",
                hiringRecommendation = json.get("hiringRecommendation")?.asString ?: "",
                companyFit = json.get("companyFit")?.asString ?: "",
                salaryReadiness = json.get("salaryReadiness")?.asString ?: "",
                strengths = json.getAsJsonArray("strengths")?.map { it.asString } ?: emptyList(),
                weaknesses = json.getAsJsonArray("weaknesses")?.map { it.asString } ?: emptyList(),
                improvementPlan = json.getAsJsonArray("improvementPlan")?.map { it.asString } ?: emptyList(),
                nextLearningPath = json.getAsJsonArray("nextLearningPath")?.map { it.asString } ?: emptyList(),
                weakestTopics = json.getAsJsonArray("weakestTopics")?.map { it.asString } ?: emptyList(),
                strongestTopics = json.getAsJsonArray("strongestTopics")?.map { it.asString } ?: emptyList(),
                questionResults = questionResults,
                voiceAnalysis = voiceMetrics,
                bodyLanguageReport = bodyLanguageReport,
                totalDurationMs = durationMs,
                targetRole = session.role,
                interviewType = session.interviewType,
                difficulty = session.difficulty,
                interviewMode = session.interviewMode
            )

            reportRepository.saveReport(report)
            Log.d(TAG, "Report generated successfully. Overall score: ${report.overallScore}")

            NetworkResult.Success(report)
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message}", e)
            NetworkResult.Error(message = "Failed to parse AI evaluation: ${e.message}")
        }
    }
}
