package com.aiic.app.domain.model

data class AnswerFeedback(
    val feedbackId: String,
    val sessionId: String,
    val questionId: String,
    val answerText: String,
    val overallScore: Int,
    val technicalScore: Int,
    val communicationScore: Int,
    val relevanceScore: Int,
    val structureScore: Int,
    val confidenceScore: Int,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val improvementSuggestions: List<String>,
    val interviewerPerspective: String,
    val followUpQuestions: List<String>
)

data class SessionSummary(
    val sessionId: String,
    val averageScore: Int,
    val strongAreas: List<String>,
    val weakAreas: List<String>,
    val priorityImprovements: List<String>,
    val roleReadiness: String
)
