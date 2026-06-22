package com.aiic.app.domain.model

enum class InterviewType {
    HR, TECHNICAL, BEHAVIORAL, MIXED
}

enum class InterviewDifficulty {
    EASY, MEDIUM, HARD
}

enum class SessionStatus {
    IN_PROGRESS, COMPLETED, ABANDONED
}

data class InterviewConfig(
    val role: String,
    val interviewType: InterviewType,
    val difficulty: InterviewDifficulty,
    val questionCount: Int,
    val resumeId: String? = null
)

data class InterviewSession(
    val sessionId: String = "",
    val userId: String = "",
    val resumeId: String? = null,
    val role: String = "",
    val interviewType: InterviewType = InterviewType.MIXED,
    val difficulty: InterviewDifficulty = InterviewDifficulty.MEDIUM,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val status: SessionStatus = SessionStatus.IN_PROGRESS,
    val questionCount: Int = 0,
    val score: Float = 0f
)

data class InterviewQuestion(
    val questionId: String = "",
    val sessionId: String = "",
    val order: Int = 0,
    val content: String = "",
    val isFollowUp: Boolean = false,
    val parentQuestionId: String? = null
)

data class InterviewAnswer(
    val answerId: String = "",
    val questionId: String = "",
    val sessionId: String = "",
    val content: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val responseTimeMs: Long = 0L,
    val aiEvaluationScore: Float = 0f,
    val aiFeedback: String = ""
)
