package com.aiic.app.domain.model

import org.junit.Assert.*
import org.junit.Test

class InterviewModelsTest {

    @Test
    fun `InterviewType - all 15 types exist`() {
        val types = InterviewType.entries
        assertEquals(15, types.size)
        assertNotNull(InterviewType.valueOf("HR"))
        assertNotNull(InterviewType.valueOf("TECHNICAL"))
        assertNotNull(InterviewType.valueOf("BEHAVIORAL"))
        assertNotNull(InterviewType.valueOf("SYSTEM_DESIGN"))
        assertNotNull(InterviewType.valueOf("CODING"))
        assertNotNull(InterviewType.valueOf("ANDROID"))
        assertNotNull(InterviewType.valueOf("AI"))
        assertNotNull(InterviewType.valueOf("MACHINE_LEARNING"))
        assertNotNull(InterviewType.valueOf("CLOUD"))
        assertNotNull(InterviewType.valueOf("DEVOPS"))
        assertNotNull(InterviewType.valueOf("LEADERSHIP"))
        assertNotNull(InterviewType.valueOf("DATABASE"))
        assertNotNull(InterviewType.valueOf("BACKEND"))
        assertNotNull(InterviewType.valueOf("FRONTEND"))
        assertNotNull(InterviewType.valueOf("MIXED"))
    }

    @Test
    fun `InterviewDifficulty - all levels exist`() {
        assertEquals(3, InterviewDifficulty.entries.size)
        assertNotNull(InterviewDifficulty.valueOf("EASY"))
        assertNotNull(InterviewDifficulty.valueOf("MEDIUM"))
        assertNotNull(InterviewDifficulty.valueOf("HARD"))
    }

    @Test
    fun `InterviewMode - all modes exist`() {
        assertEquals(3, InterviewMode.entries.size)
        assertNotNull(InterviewMode.valueOf("TEXT"))
        assertNotNull(InterviewMode.valueOf("VOICE"))
        assertNotNull(InterviewMode.valueOf("VIDEO"))
    }

    @Test
    fun `SessionStatus - all statuses exist`() {
        assertEquals(3, SessionStatus.entries.size)
        assertNotNull(SessionStatus.valueOf("IN_PROGRESS"))
        assertNotNull(SessionStatus.valueOf("COMPLETED"))
        assertNotNull(SessionStatus.valueOf("ABANDONED"))
    }

    @Test
    fun `SpeechSpeed - all speeds exist`() {
        assertEquals(5, SpeechSpeed.entries.size)
    }

    @Test
    fun `LightingQuality - all qualities exist`() {
        assertEquals(4, LightingQuality.entries.size)
    }

    @Test
    fun `InterviewConfig - defaults are correct`() {
        val config = InterviewConfig(
            role = "SDE",
            interviewType = InterviewType.TECHNICAL,
            difficulty = InterviewDifficulty.MEDIUM,
            questionCount = 5
        )
        assertEquals(InterviewMode.TEXT, config.interviewMode)
        assertNull(config.resumeId)
        assertNull(config.targetCompany)
    }

    @Test
    fun `InterviewSession - defaults are correct`() {
        val session = InterviewSession()
        assertEquals("", session.sessionId)
        assertEquals(InterviewType.MIXED, session.interviewType)
        assertEquals(InterviewDifficulty.MEDIUM, session.difficulty)
        assertEquals(InterviewMode.TEXT, session.interviewMode)
        assertEquals(SessionStatus.IN_PROGRESS, session.status)
        assertEquals(0f, session.score, 0.01f)
        assertNull(session.endedAt)
        assertNull(session.targetCompany)
    }

    @Test
    fun `InterviewQuestion - defaults are correct`() {
        val question = InterviewQuestion()
        assertEquals("", question.questionId)
        assertEquals(QuestionCategory.TECHNICAL, question.category)
        assertFalse(question.isFollowUp)
        assertNull(question.parentQuestionId)
    }

    @Test
    fun `InterviewAnswer - defaults are correct`() {
        val answer = InterviewAnswer()
        assertEquals("", answer.answerId)
        assertEquals(0f, answer.aiEvaluationScore, 0.01f)
        assertNull(answer.voiceMetrics)
    }

    @Test
    fun `VoiceMetrics - defaults are correct`() {
        val vm = VoiceMetrics()
        assertEquals(0f, vm.wordsPerMinute, 0.01f)
        assertEquals(0, vm.fillerWordCount)
        assertTrue(vm.fillerWords.isEmpty())
    }

    @Test
    fun `BodyLanguageReport - defaults are correct`() {
        val bl = BodyLanguageReport()
        assertEquals(0f, bl.confidenceScore, 0.01f)
        assertEquals(0f, bl.eyeContactScore, 0.01f)
        assertTrue(bl.warnings.isEmpty())
        assertTrue(bl.suggestions.isEmpty())
    }

    @Test
    fun `InterviewReport - defaults are correct`() {
        val report = InterviewReport()
        assertEquals("", report.sessionId)
        assertEquals(0f, report.overallScore, 0.01f)
        assertEquals(InterviewType.MIXED, report.interviewType)
        assertEquals(InterviewMode.TEXT, report.interviewMode)
        assertNull(report.voiceAnalysis)
        assertNull(report.bodyLanguageReport)
        assertTrue(report.strengths.isEmpty())
        assertTrue(report.questionResults.isEmpty())
    }

    @Test
    fun `QuestionResult - defaults are correct`() {
        val qr = QuestionResult()
        assertEquals("", qr.questionId)
        assertEquals(QuestionCategory.TECHNICAL, qr.category)
        assertEquals(0L, qr.responseTimeMs)
    }

    @Test
    fun `InterviewSession - copy updates only specified fields`() {
        val original = InterviewSession(sessionId = "test-1", role = "SDE", score = 75f)
        val updated = original.copy(score = 90f)
        assertEquals("test-1", updated.sessionId)
        assertEquals("SDE", updated.role)
        assertEquals(90f, updated.score, 0.01f)
    }

    @Test
    fun `InterviewReport - copy preserves nested data`() {
        val metrics = VoiceMetrics(wordsPerMinute = 140f, totalWords = 200)
        val original = InterviewReport(
            sessionId = "s1",
            overallScore = 85f,
            voiceAnalysis = metrics,
            strengths = listOf("Communication", "Technical")
        )
        val updated = original.copy(overallScore = 92f)
        assertEquals(85f, original.overallScore, 0.01f)
        assertEquals(92f, updated.overallScore, 0.01f)
        assertEquals(metrics, updated.voiceAnalysis)
        assertEquals(2, updated.strengths.size)
    }
}
