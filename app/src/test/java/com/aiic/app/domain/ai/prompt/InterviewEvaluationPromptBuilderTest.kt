package com.aiic.app.domain.ai.prompt

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for InterviewEvaluationPromptBuilder to verify prompt construction
 * covers all dimensions and produces valid, parseable AI prompts.
 */
class InterviewEvaluationPromptBuilderTest {

    @Test
    fun `build - includes interview context`() {
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Senior Android Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("HARD")
            .setQuestionAnswerPairs(listOf("Q1" to "A1"))
            .build()

        assertTrue(prompt.contains("Senior Android Engineer"))
        assertTrue(prompt.contains("TECHNICAL"))
        assertTrue(prompt.contains("HARD"))
    }

    @Test
    fun `build - includes target company when set`() {
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
            .setTargetCompany("Google")
            .setQuestionAnswerPairs(listOf("Q1" to "A1"))
            .build()

        assertTrue(prompt.contains("Google"))
    }

    @Test
    fun `build - excludes company when null`() {
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
            .setTargetCompany(null)
            .setQuestionAnswerPairs(listOf("Q1" to "A1"))
            .build()

        assertFalse(prompt.contains("Target Company"))
    }

    @Test
    fun `build - includes all question-answer pairs`() {
        val pairs = listOf(
            "What is dependency injection?" to "DI is a design pattern...",
            "Explain MVVM" to "MVVM separates concerns...",
            "What is a coroutine?" to "A lightweight thread..."
        )
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Android Dev")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
            .setQuestionAnswerPairs(pairs)
            .build()

        assertTrue(prompt.contains("Question 1"))
        assertTrue(prompt.contains("Question 2"))
        assertTrue(prompt.contains("Question 3"))
        assertTrue(prompt.contains("dependency injection"))
        assertTrue(prompt.contains("coroutine"))
    }

    @Test
    fun `build - includes voice metrics when provided`() {
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
            .setQuestionAnswerPairs(listOf("Q" to "A"))
            .setVoiceMetrics("- Words Per Minute: 140\n- Filler Words: 3")
            .build()

        assertTrue(prompt.contains("Voice Analysis Data"))
        assertTrue(prompt.contains("Words Per Minute: 140"))
    }

    @Test
    fun `build - includes body language metrics when provided`() {
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
            .setQuestionAnswerPairs(listOf("Q" to "A"))
            .setBodyLanguageMetrics("- Eye Contact Score: 85")
            .build()

        assertTrue(prompt.contains("Body Language Data"))
        assertTrue(prompt.contains("Eye Contact Score: 85"))
    }

    @Test
    fun `build - includes all required JSON fields in output schema`() {
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
            .setQuestionAnswerPairs(listOf("Q" to "A"))
            .build()

        val requiredFields = listOf(
            "overallScore", "technicalAccuracyScore", "communicationScore",
            "confidenceScore", "problemSolvingScore", "depthScore",
            "structureScore", "leadershipScore", "examplesScore",
            "vocabularyScore", "professionalismScore", "roleReadiness",
            "hiringRecommendation", "companyFit", "salaryReadiness",
            "strengths", "weaknesses", "improvementPlan", "nextLearningPath",
            "weakestTopics", "strongestTopics", "questionResults"
        )

        requiredFields.forEach { field ->
            assertTrue("Prompt should contain field: $field", prompt.contains(field))
        }
    }

    @Test
    fun `build - instructs AI to return only JSON`() {
        val prompt = InterviewEvaluationPromptBuilder()
            .setTargetRole("Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
            .setQuestionAnswerPairs(listOf("Q" to "A"))
            .build()

        assertTrue(prompt.contains("ONLY a valid JSON"))
    }

    @Test
    fun `build - builder pattern returns same instance`() {
        val builder = InterviewEvaluationPromptBuilder()
        val result = builder
            .setTargetRole("Engineer")
            .setInterviewType("TECHNICAL")
            .setDifficulty("MEDIUM")
        assertSame(builder, result)
    }
}
