package com.aiic.app.domain.model

data class InterviewSession(
    val id: String = "",
    val title: String = "",
    val category: InterviewCategory = InterviewCategory.GENERAL,
    val score: Float = 0f,
    val duration: Long = 0L,
    val questionsAsked: Int = 0,
    val completedAt: Long = 0L,
)

enum class InterviewCategory(val displayName: String) {
    GENERAL("General"),
    TECHNICAL("Technical"),
    BEHAVIORAL("Behavioral"),
    HR("HR Round"),
    SYSTEM_DESIGN("System Design"),
    DSA("DSA"),
    CASE_STUDY("Case Study"),
}
