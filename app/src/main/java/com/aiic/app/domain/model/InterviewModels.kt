package com.aiic.app.domain.model

enum class InterviewType {
    HR, TECHNICAL, BEHAVIORAL, MIXED, LEADERSHIP, SYSTEM_DESIGN,
    CODING, DATABASE, ANDROID, BACKEND, FRONTEND, AI, MACHINE_LEARNING,
    CLOUD, DEVOPS
}

enum class InterviewDifficulty {
    EASY, MEDIUM, HARD
}

enum class InterviewMode {
    TEXT, VOICE, VIDEO
}

enum class SessionStatus {
    IN_PROGRESS, COMPLETED, ABANDONED
}

enum class QuestionCategory {
    TECHNICAL, BEHAVIORAL, PROJECT_BASED, HR_GENERAL,
    SYSTEM_DESIGN, CODING, LEADERSHIP, SITUATIONAL
}

data class InterviewConfig(
    val role: String,
    val interviewType: InterviewType,
    val difficulty: InterviewDifficulty,
    val questionCount: Int,
    val interviewMode: InterviewMode = InterviewMode.TEXT,
    val resumeId: String? = null,
    val targetCompany: String? = null
)

data class InterviewSession(
    val sessionId: String = "",
    val userId: String = "",
    val resumeId: String? = null,
    val role: String = "",
    val interviewType: InterviewType = InterviewType.MIXED,
    val difficulty: InterviewDifficulty = InterviewDifficulty.MEDIUM,
    val interviewMode: InterviewMode = InterviewMode.TEXT,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val status: SessionStatus = SessionStatus.IN_PROGRESS,
    val questionCount: Int = 0,
    val score: Float = 0f,
    val targetCompany: String? = null
)

data class InterviewQuestion(
    val questionId: String = "",
    val sessionId: String = "",
    val order: Int = 0,
    val content: String = "",
    val category: QuestionCategory = QuestionCategory.TECHNICAL,
    val isFollowUp: Boolean = false,
    val parentQuestionId: String? = null,
    val difficulty: InterviewDifficulty = InterviewDifficulty.MEDIUM,
    val expectedTopics: List<String> = emptyList()
)

data class InterviewAnswer(
    val answerId: String = "",
    val questionId: String = "",
    val sessionId: String = "",
    val content: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val responseTimeMs: Long = 0L,
    val aiEvaluationScore: Float = 0f,
    val aiFeedback: String = "",
    val voiceMetrics: VoiceMetrics? = null
)

data class VoiceMetrics(
    val wordsPerMinute: Float = 0f,
    val speechDurationMs: Long = 0L,
    val silenceDurationMs: Long = 0L,
    val fillerWordCount: Int = 0,
    val fillerWords: Map<String, Int> = emptyMap(),
    val speechConfidence: Float = 0f,
    val communicationScore: Float = 0f,
    val totalWords: Int = 0,
    val averagePauseMs: Long = 0L
)

data class VoiceAnalysisResult(
    val transcript: String = "",
    val metrics: VoiceMetrics = VoiceMetrics(),
    val fillerWordBreakdown: Map<String, Int> = emptyMap(),
    val speechSpeed: SpeechSpeed = SpeechSpeed.NORMAL,
    val clarityScore: Float = 0f
)

enum class SpeechSpeed {
    TOO_SLOW, SLOW, NORMAL, FAST, TOO_FAST
}

data class FaceAnalysisFrame(
    val timestamp: Long = 0L,
    val faceDetected: Boolean = false,
    val smileProbability: Float = 0f,
    val leftEyeOpenProbability: Float = 0f,
    val rightEyeOpenProbability: Float = 0f,
    val headEulerAngleX: Float = 0f,
    val headEulerAngleY: Float = 0f,
    val headEulerAngleZ: Float = 0f,
    val faceBoundsValid: Boolean = false,
    val lightingQuality: LightingQuality = LightingQuality.ADEQUATE,
    val multipleFaces: Boolean = false
)

enum class LightingQuality {
    TOO_DARK, ADEQUATE, GOOD, TOO_BRIGHT
}

data class BodyLanguageReport(
    val confidenceScore: Float = 0f,
    val professionalismScore: Float = 0f,
    val eyeContactScore: Float = 0f,
    val facialExpressionScore: Float = 0f,
    val energyScore: Float = 0f,
    val engagementScore: Float = 0f,
    val nervousnessScore: Float = 0f,
    val smileFrequency: Float = 0f,
    val headMovementScore: Float = 0f,
    val lookingDownPercentage: Float = 0f,
    val lookingSidewaysPercentage: Float = 0f,
    val overallBodyLanguageScore: Float = 0f,
    val warnings: List<String> = emptyList(),
    val suggestions: List<String> = emptyList()
)

data class InterviewReport(
    val reportId: String = "",
    val sessionId: String = "",
    val userId: String = "",
    val generatedAt: Long = System.currentTimeMillis(),

    val overallScore: Float = 0f,
    val technicalAccuracyScore: Float = 0f,
    val communicationScore: Float = 0f,
    val confidenceScore: Float = 0f,
    val problemSolvingScore: Float = 0f,
    val depthScore: Float = 0f,
    val structureScore: Float = 0f,
    val leadershipScore: Float = 0f,
    val examplesScore: Float = 0f,
    val vocabularyScore: Float = 0f,
    val professionalismScore: Float = 0f,

    val roleReadiness: String = "",
    val hiringRecommendation: String = "",
    val companyFit: String = "",
    val salaryReadiness: String = "",

    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val improvementPlan: List<String> = emptyList(),
    val nextLearningPath: List<String> = emptyList(),

    val questionResults: List<QuestionResult> = emptyList(),
    val weakestTopics: List<String> = emptyList(),
    val strongestTopics: List<String> = emptyList(),

    val voiceAnalysis: VoiceMetrics? = null,

    val bodyLanguageReport: BodyLanguageReport? = null,

    val totalDurationMs: Long = 0L,
    val targetRole: String = "",
    val interviewType: InterviewType = InterviewType.MIXED,
    val difficulty: InterviewDifficulty = InterviewDifficulty.MEDIUM,
    val interviewMode: InterviewMode = InterviewMode.TEXT
)

data class QuestionResult(
    val questionId: String = "",
    val question: String = "",
    val answer: String = "",
    val score: Float = 0f,
    val feedback: String = "",
    val category: QuestionCategory = QuestionCategory.TECHNICAL,
    val responseTimeMs: Long = 0L
)
