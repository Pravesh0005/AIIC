package com.aiic.app.domain.ai.prompt

class InterviewEvaluationPromptBuilder {

    private var questions: List<Pair<String, String>> = emptyList() 
    private var targetRole: String = ""
    private var interviewType: String = ""
    private var difficulty: String = ""
    private var targetCompany: String? = null
    private var resumeContext: String? = null
    private var voiceMetrics: String? = null
    private var bodyLanguageMetrics: String? = null

    fun setQuestionAnswerPairs(pairs: List<Pair<String, String>>): InterviewEvaluationPromptBuilder {
        this.questions = pairs
        return this
    }

    fun setTargetRole(role: String): InterviewEvaluationPromptBuilder {
        this.targetRole = role
        return this
    }

    fun setInterviewType(type: String): InterviewEvaluationPromptBuilder {
        this.interviewType = type
        return this
    }

    fun setDifficulty(difficulty: String): InterviewEvaluationPromptBuilder {
        this.difficulty = difficulty
        return this
    }

    fun setTargetCompany(company: String?): InterviewEvaluationPromptBuilder {
        this.targetCompany = company
        return this
    }

    fun setResumeContext(context: String?): InterviewEvaluationPromptBuilder {
        this.resumeContext = context
        return this
    }

    fun setVoiceMetrics(metrics: String?): InterviewEvaluationPromptBuilder {
        this.voiceMetrics = metrics
        return this
    }

    fun setBodyLanguageMetrics(metrics: String?): InterviewEvaluationPromptBuilder {
        this.bodyLanguageMetrics = metrics
        return this
    }

    fun build(): String {
        val sb = StringBuilder()

        sb.appendLine("You are a senior technical interviewer and hiring manager.")
        sb.appendLine("Evaluate the following mock interview session with extreme precision.")
        sb.appendLine("Every score MUST come from your actual analysis of the answers — NEVER use random or default scores.")
        sb.appendLine()

        sb.appendLine("## Interview Context")
        sb.appendLine("- Target Role: $targetRole")
        sb.appendLine("- Interview Type: $interviewType")
        sb.appendLine("- Difficulty: $difficulty")
        if (!targetCompany.isNullOrBlank()) {
            sb.appendLine("- Target Company: $targetCompany")
        }
        if (!resumeContext.isNullOrBlank()) {
            sb.appendLine("- Resume Summary: $resumeContext")
        }
        sb.appendLine()

        if (!voiceMetrics.isNullOrBlank()) {
            sb.appendLine("## Voice Analysis Data")
            sb.appendLine(voiceMetrics)
            sb.appendLine()
        }

        if (!bodyLanguageMetrics.isNullOrBlank()) {
            sb.appendLine("## Body Language Data")
            sb.appendLine(bodyLanguageMetrics)
            sb.appendLine()
        }

        sb.appendLine("## Interview Transcript")
        questions.forEachIndexed { index, (question, answer) ->
            sb.appendLine("### Question ${index + 1}")
            sb.appendLine("Q: $question")
            sb.appendLine("A: $answer")
            sb.appendLine()
        }

        sb.appendLine("## Required JSON Output")
        sb.appendLine("Respond with ONLY a valid JSON object, no markdown, no explanation:")
        sb.appendLine("""
{
    "overallScore": <0-100 integer>,
    "technicalAccuracyScore": <0-100>,
    "communicationScore": <0-100>,
    "confidenceScore": <0-100>,
    "problemSolvingScore": <0-100>,
    "depthScore": <0-100>,
    "structureScore": <0-100>,
    "leadershipScore": <0-100>,
    "examplesScore": <0-100>,
    "vocabularyScore": <0-100>,
    "professionalismScore": <0-100>,
    "roleReadiness": "<Not Ready / Needs Work / Almost Ready / Ready / Exceptional>",
    "hiringRecommendation": "<Strong No / No / Borderline / Yes / Strong Yes>",
    "companyFit": "<explanation of fit for the role/company>",
    "salaryReadiness": "<Junior / Mid-Level / Senior / Lead level assessment>",
    "strengths": ["strength1", "strength2", "strength3"],
    "weaknesses": ["weakness1", "weakness2", "weakness3"],
    "improvementPlan": ["step1", "step2", "step3"],
    "nextLearningPath": ["topic1", "topic2", "topic3"],
    "weakestTopics": ["topic1", "topic2"],
    "strongestTopics": ["topic1", "topic2"],
    "questionResults": [
        {
            "questionId": "<index starting from 0>",
            "score": <0-100>,
            "feedback": "<specific feedback for this answer>"
        }
    ]
}
""".trim())

        return sb.toString()
    }
}
