package com.aiic.app.data.model

import com.aiic.app.domain.model.AnswerFeedback
import com.aiic.app.domain.model.SessionSummary
import com.google.firebase.firestore.PropertyName

data class AnswerFeedbackDto(
    @get:PropertyName("feedback_id") @set:PropertyName("feedback_id") var feedbackId: String = "",
    @get:PropertyName("session_id") @set:PropertyName("session_id") var sessionId: String = "",
    @get:PropertyName("question_id") @set:PropertyName("question_id") var questionId: String = "",
    @get:PropertyName("answer_text") @set:PropertyName("answer_text") var answerText: String = "",
    @get:PropertyName("overall_score") @set:PropertyName("overall_score") var overallScore: Int = 0,
    @get:PropertyName("technical_score") @set:PropertyName("technical_score") var technicalScore: Int = 0,
    @get:PropertyName("communication_score") @set:PropertyName("communication_score") var communicationScore: Int = 0,
    @get:PropertyName("relevance_score") @set:PropertyName("relevance_score") var relevanceScore: Int = 0,
    @get:PropertyName("structure_score") @set:PropertyName("structure_score") var structureScore: Int = 0,
    @get:PropertyName("confidence_score") @set:PropertyName("confidence_score") var confidenceScore: Int = 0,
    @get:PropertyName("strengths") @set:PropertyName("strengths") var strengths: List<String> = emptyList(),
    @get:PropertyName("weaknesses") @set:PropertyName("weaknesses") var weaknesses: List<String> = emptyList(),
    @get:PropertyName("improvement_suggestions") @set:PropertyName("improvement_suggestions") var improvementSuggestions: List<String> = emptyList(),
    @get:PropertyName("interviewer_perspective") @set:PropertyName("interviewer_perspective") var interviewerPerspective: String = "",
    @get:PropertyName("follow_up_questions") @set:PropertyName("follow_up_questions") var followUpQuestions: List<String> = emptyList()
) {
    fun toDomain() = AnswerFeedback(
        feedbackId = feedbackId,
        sessionId = sessionId,
        questionId = questionId,
        answerText = answerText,
        overallScore = overallScore,
        technicalScore = technicalScore,
        communicationScore = communicationScore,
        relevanceScore = relevanceScore,
        structureScore = structureScore,
        confidenceScore = confidenceScore,
        strengths = strengths,
        weaknesses = weaknesses,
        improvementSuggestions = improvementSuggestions,
        interviewerPerspective = interviewerPerspective,
        followUpQuestions = followUpQuestions
    )
}

fun AnswerFeedback.toDto() = AnswerFeedbackDto(
    feedbackId = feedbackId,
    sessionId = sessionId,
    questionId = questionId,
    answerText = answerText,
    overallScore = overallScore,
    technicalScore = technicalScore,
    communicationScore = communicationScore,
    relevanceScore = relevanceScore,
    structureScore = structureScore,
    confidenceScore = confidenceScore,
    strengths = strengths,
    weaknesses = weaknesses,
    improvementSuggestions = improvementSuggestions,
    interviewerPerspective = interviewerPerspective,
    followUpQuestions = followUpQuestions
)

data class SessionSummaryDto(
    @get:PropertyName("session_id") @set:PropertyName("session_id") var sessionId: String = "",
    @get:PropertyName("average_score") @set:PropertyName("average_score") var averageScore: Int = 0,
    @get:PropertyName("strong_areas") @set:PropertyName("strong_areas") var strongAreas: List<String> = emptyList(),
    @get:PropertyName("weak_areas") @set:PropertyName("weak_areas") var weakAreas: List<String> = emptyList(),
    @get:PropertyName("priority_improvements") @set:PropertyName("priority_improvements") var priorityImprovements: List<String> = emptyList(),
    @get:PropertyName("role_readiness") @set:PropertyName("role_readiness") var roleReadiness: String = ""
) {
    fun toDomain() = SessionSummary(
        sessionId = sessionId,
        averageScore = averageScore,
        strongAreas = strongAreas,
        weakAreas = weakAreas,
        priorityImprovements = priorityImprovements,
        roleReadiness = roleReadiness
    )
}

fun SessionSummary.toDto() = SessionSummaryDto(
    sessionId = sessionId,
    averageScore = averageScore,
    strongAreas = strongAreas,
    weakAreas = weakAreas,
    priorityImprovements = priorityImprovements,
    roleReadiness = roleReadiness
)
