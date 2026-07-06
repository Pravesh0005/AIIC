package com.aiic.app.data.repository

import android.util.Log
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.domain.model.*
import com.aiic.app.domain.repository.InterviewReportRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreInterviewReportRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : InterviewReportRepository {

    companion object {
        private const val COLLECTION = "interview_reports"
        private const val TAG = "AIIC_REPORT_REPO"
    }

    override suspend fun saveReport(report: InterviewReport): NetworkResult<Unit> {
        return try {
            val docId = report.sessionId
            val data = mapOf(
                "reportId" to report.reportId,
                "sessionId" to report.sessionId,
                "userId" to report.userId,
                "generatedAt" to report.generatedAt,
                "overallScore" to report.overallScore,
                "technicalAccuracyScore" to report.technicalAccuracyScore,
                "communicationScore" to report.communicationScore,
                "confidenceScore" to report.confidenceScore,
                "problemSolvingScore" to report.problemSolvingScore,
                "depthScore" to report.depthScore,
                "structureScore" to report.structureScore,
                "leadershipScore" to report.leadershipScore,
                "examplesScore" to report.examplesScore,
                "vocabularyScore" to report.vocabularyScore,
                "professionalismScore" to report.professionalismScore,
                "roleReadiness" to report.roleReadiness,
                "hiringRecommendation" to report.hiringRecommendation,
                "companyFit" to report.companyFit,
                "salaryReadiness" to report.salaryReadiness,
                "strengths" to report.strengths,
                "weaknesses" to report.weaknesses,
                "improvementPlan" to report.improvementPlan,
                "nextLearningPath" to report.nextLearningPath,
                "weakestTopics" to report.weakestTopics,
                "strongestTopics" to report.strongestTopics,
                "totalDurationMs" to report.totalDurationMs,
                "targetRole" to report.targetRole,
                "interviewType" to report.interviewType.name,
                "difficulty" to report.difficulty.name,
                "interviewMode" to report.interviewMode.name,
                "questionResults" to report.questionResults.map { qr ->
                    mapOf(
                        "questionId" to qr.questionId,
                        "question" to qr.question,
                        "answer" to qr.answer,
                        "score" to qr.score,
                        "feedback" to qr.feedback,
                        "category" to qr.category.name,
                        "responseTimeMs" to qr.responseTimeMs
                    )
                },
                "voiceMetrics" to report.voiceAnalysis?.let { vm ->
                    mapOf(
                        "wordsPerMinute" to vm.wordsPerMinute,
                        "speechDurationMs" to vm.speechDurationMs,
                        "fillerWordCount" to vm.fillerWordCount,
                        "communicationScore" to vm.communicationScore,
                        "totalWords" to vm.totalWords,
                        "fillerWords" to vm.fillerWords,
                        "speechConfidence" to vm.speechConfidence
                    )
                },
                "bodyLanguageReport" to report.bodyLanguageReport?.let { bl ->
                    mapOf(
                        "confidenceScore" to bl.confidenceScore,
                        "professionalismScore" to bl.professionalismScore,
                        "eyeContactScore" to bl.eyeContactScore,
                        "facialExpressionScore" to bl.facialExpressionScore,
                        "energyScore" to bl.energyScore,
                        "engagementScore" to bl.engagementScore,
                        "nervousnessScore" to bl.nervousnessScore,
                        "overallBodyLanguageScore" to bl.overallBodyLanguageScore,
                        "suggestions" to bl.suggestions
                    )
                }
            )

            firestore.collection(COLLECTION).document(docId).set(data).await()
            Log.d(TAG, "Saved report for session $docId")
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Save failed: ${e.message}", e)
            NetworkResult.Error(message = e.message ?: "Failed to save report")
        }
    }

    override suspend fun getReport(sessionId: String): NetworkResult<InterviewReport> {
        return try {
            val doc = firestore.collection(COLLECTION).document(sessionId).get().await()
            if (!doc.exists()) {
                return NetworkResult.Error(message = "No report found for this session")
            }

            val data = doc.data ?: return NetworkResult.Error(message = "Empty report data")

            @Suppress("UNCHECKED_CAST")
            val questionResultsRaw = data["questionResults"] as? List<Map<String, Any>> ?: emptyList()
            val questionResults = questionResultsRaw.map { qr ->
                QuestionResult(
                    questionId = qr["questionId"] as? String ?: "",
                    question = qr["question"] as? String ?: "",
                    answer = qr["answer"] as? String ?: "",
                    score = (qr["score"] as? Number)?.toFloat() ?: 0f,
                    feedback = qr["feedback"] as? String ?: "",
                    category = try { QuestionCategory.valueOf(qr["category"] as? String ?: "TECHNICAL") } catch (_: Exception) { QuestionCategory.TECHNICAL },
                    responseTimeMs = (qr["responseTimeMs"] as? Number)?.toLong() ?: 0L
                )
            }

            @Suppress("UNCHECKED_CAST")
            val voiceRaw = data["voiceMetrics"] as? Map<String, Any>
            val voiceMetrics = voiceRaw?.let { vm ->
                @Suppress("UNCHECKED_CAST")
                VoiceMetrics(
                    wordsPerMinute = (vm["wordsPerMinute"] as? Number)?.toFloat() ?: 0f,
                    speechDurationMs = (vm["speechDurationMs"] as? Number)?.toLong() ?: 0L,
                    fillerWordCount = (vm["fillerWordCount"] as? Number)?.toInt() ?: 0,
                    communicationScore = (vm["communicationScore"] as? Number)?.toFloat() ?: 0f,
                    totalWords = (vm["totalWords"] as? Number)?.toInt() ?: 0,
                    fillerWords = (vm["fillerWords"] as? Map<String, Number>)?.mapValues { it.value.toInt() } ?: emptyMap(),
                    speechConfidence = (vm["speechConfidence"] as? Number)?.toFloat() ?: 0f
                )
            }

            @Suppress("UNCHECKED_CAST")
            val bodyRaw = data["bodyLanguageReport"] as? Map<String, Any>
            val bodyLanguageReport = bodyRaw?.let { bl ->
                @Suppress("UNCHECKED_CAST")
                BodyLanguageReport(
                    confidenceScore = (bl["confidenceScore"] as? Number)?.toFloat() ?: 0f,
                    professionalismScore = (bl["professionalismScore"] as? Number)?.toFloat() ?: 0f,
                    eyeContactScore = (bl["eyeContactScore"] as? Number)?.toFloat() ?: 0f,
                    facialExpressionScore = (bl["facialExpressionScore"] as? Number)?.toFloat() ?: 0f,
                    energyScore = (bl["energyScore"] as? Number)?.toFloat() ?: 0f,
                    engagementScore = (bl["engagementScore"] as? Number)?.toFloat() ?: 0f,
                    nervousnessScore = (bl["nervousnessScore"] as? Number)?.toFloat() ?: 0f,
                    overallBodyLanguageScore = (bl["overallBodyLanguageScore"] as? Number)?.toFloat() ?: 0f,
                    suggestions = (bl["suggestions"] as? List<String>) ?: emptyList()
                )
            }

            val report = InterviewReport(
                reportId = data["reportId"] as? String ?: "",
                sessionId = data["sessionId"] as? String ?: "",
                userId = data["userId"] as? String ?: "",
                generatedAt = (data["generatedAt"] as? Number)?.toLong() ?: 0L,
                overallScore = (data["overallScore"] as? Number)?.toFloat() ?: 0f,
                technicalAccuracyScore = (data["technicalAccuracyScore"] as? Number)?.toFloat() ?: 0f,
                communicationScore = (data["communicationScore"] as? Number)?.toFloat() ?: 0f,
                confidenceScore = (data["confidenceScore"] as? Number)?.toFloat() ?: 0f,
                problemSolvingScore = (data["problemSolvingScore"] as? Number)?.toFloat() ?: 0f,
                depthScore = (data["depthScore"] as? Number)?.toFloat() ?: 0f,
                structureScore = (data["structureScore"] as? Number)?.toFloat() ?: 0f,
                leadershipScore = (data["leadershipScore"] as? Number)?.toFloat() ?: 0f,
                examplesScore = (data["examplesScore"] as? Number)?.toFloat() ?: 0f,
                vocabularyScore = (data["vocabularyScore"] as? Number)?.toFloat() ?: 0f,
                professionalismScore = (data["professionalismScore"] as? Number)?.toFloat() ?: 0f,
                roleReadiness = data["roleReadiness"] as? String ?: "",
                hiringRecommendation = data["hiringRecommendation"] as? String ?: "",
                companyFit = data["companyFit"] as? String ?: "",
                salaryReadiness = data["salaryReadiness"] as? String ?: "",
                strengths = (data["strengths"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                weaknesses = (data["weaknesses"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                improvementPlan = (data["improvementPlan"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                nextLearningPath = (data["nextLearningPath"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                weakestTopics = (data["weakestTopics"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                strongestTopics = (data["strongestTopics"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                questionResults = questionResults,
                totalDurationMs = (data["totalDurationMs"] as? Number)?.toLong() ?: 0L,
                targetRole = data["targetRole"] as? String ?: "",
                interviewType = try { InterviewType.valueOf(data["interviewType"] as? String ?: "MIXED") } catch (_: Exception) { InterviewType.MIXED },
                difficulty = try { InterviewDifficulty.valueOf(data["difficulty"] as? String ?: "MEDIUM") } catch (_: Exception) { InterviewDifficulty.MEDIUM },
                interviewMode = try { InterviewMode.valueOf(data["interviewMode"] as? String ?: "TEXT") } catch (_: Exception) { InterviewMode.TEXT },
                voiceAnalysis = voiceMetrics,
                bodyLanguageReport = bodyLanguageReport
            )

            Log.d(TAG, "Loaded report for session $sessionId, score=${report.overallScore}")
            NetworkResult.Success(report)
        } catch (e: Exception) {
            Log.e(TAG, "Load failed: ${e.message}", e)
            NetworkResult.Error(message = e.message ?: "Failed to load report")
        }
    }

    override suspend fun getReportsForUser(userId: String): NetworkResult<List<InterviewReport>> {
        return try {
            val docs = firestore.collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val reports = docs.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null
                    InterviewReport(
                        reportId = data["reportId"] as? String ?: "",
                        sessionId = data["sessionId"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        generatedAt = (data["generatedAt"] as? Number)?.toLong() ?: 0L,
                        overallScore = (data["overallScore"] as? Number)?.toFloat() ?: 0f,
                        targetRole = data["targetRole"] as? String ?: "",
                        interviewType = try { InterviewType.valueOf(data["interviewType"] as? String ?: "MIXED") } catch (_: Exception) { InterviewType.MIXED },
                        totalDurationMs = (data["totalDurationMs"] as? Number)?.toLong() ?: 0L,
                        strengths = (data["strengths"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        weaknesses = (data["weaknesses"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        roleReadiness = data["roleReadiness"] as? String ?: "",
                        hiringRecommendation = data["hiringRecommendation"] as? String ?: ""
                    )
                } catch (_: Exception) { null }
            }.sortedByDescending { it.generatedAt }

            NetworkResult.Success(reports)
        } catch (e: Exception) {
            Log.e(TAG, "List failed: ${e.message}", e)
            NetworkResult.Error(message = e.message ?: "Failed to list reports")
        }
    }
}
