package com.aiic.app.domain.ai.prompt

object FeedbackPromptBuilder {

    fun buildAnswerEvaluationPrompt(
        question: String,
        answer: String,
        targetRole: String,
        resumeContext: String
    ): String {
        return """
            You are an expert technical interviewer and career coach evaluating a candidate's answer.
            
            Target Role: $targetRole
            Question Asked: $question
            Candidate's Answer: $answer
            Resume Context: $resumeContext
            
            Evaluate the answer based on:
            1. Technical correctness and depth.
            2. Communication clarity and structure.
            3. Relevance to the target role.
            4. Confidence and conciseness.
            
            You MUST return a JSON object ONLY. No markdown, no markdown blocks, no extra text.
            Use this exact schema:
            {
              "overallScore": <int 0-100>,
              "technicalScore": <int 0-100>,
              "communicationScore": <int 0-100>,
              "relevanceScore": <int 0-100>,
              "structureScore": <int 0-100>,
              "confidenceScore": <int 0-100>,
              "strengths": ["<string>", ...],
              "weaknesses": ["<string>", ...],
              "improvementSuggestions": ["<string>", ...],
              "interviewerPerspective": "<string>",
              "followUpQuestions": ["<string>", ...]
            }
        """.trimIndent()
    }
}
