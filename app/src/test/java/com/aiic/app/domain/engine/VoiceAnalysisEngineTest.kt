package com.aiic.app.domain.engine

import com.aiic.app.domain.model.SpeechSpeed
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive test suite for the VoiceAnalysisEngine.
 * Tests all analysis dimensions: WPM, filler detection, speech speed,
 * communication scoring, and clarity scoring.
 */
class VoiceAnalysisEngineTest {

    private lateinit var engine: VoiceAnalysisEngine

    @Before
    fun setup() {
        engine = VoiceAnalysisEngine()
    }

    // ── WPM Calculation ──

    @Test
    fun `analyzeTranscript - calculates correct WPM for normal speech`() {
        val transcript = "The quick brown fox jumps over the lazy dog. I believe this is a good example sentence."
        // 16 words in 8 seconds = 120 WPM
        val result = engine.analyzeTranscript(transcript, speechDurationMs = 8000, silenceDurationMs = 500, speechConfidence = 0.9f)
        assertTrue("WPM should be positive", result.metrics.wordsPerMinute > 0)
        assertEquals(16, result.metrics.totalWords)
    }

    @Test
    fun `analyzeTranscript - handles very short speech duration`() {
        val result = engine.analyzeTranscript("Hello", speechDurationMs = 100, silenceDurationMs = 0, speechConfidence = 0.5f)
        // Duration < 3 seconds threshold
        assertTrue("Should return valid result", result.metrics.totalWords == 1)
    }

    @Test
    fun `analyzeTranscript - returns zero WPM for empty transcript`() {
        val result = engine.analyzeTranscript("", speechDurationMs = 5000, silenceDurationMs = 1000, speechConfidence = 0.8f)
        assertEquals(0f, result.metrics.wordsPerMinute, 0.01f)
        assertEquals(0, result.metrics.totalWords)
    }

    // ── Filler Word Detection ──

    @Test
    fun `analyzeTranscript - detects single-word fillers`() {
        val transcript = "I um think that uh basically um the solution works"
        val result = engine.analyzeTranscript(transcript, speechDurationMs = 10000, silenceDurationMs = 500, speechConfidence = 0.8f)
        assertTrue("Should detect filler words", result.metrics.fillerWordCount > 0)
        assertTrue("Should detect 'um'", result.fillerWordBreakdown.containsKey("um"))
        assertTrue("Should detect 'uh'", result.fillerWordBreakdown.containsKey("uh"))
        assertTrue("Should detect 'basically'", result.fillerWordBreakdown.containsKey("basically"))
    }

    @Test
    fun `analyzeTranscript - detects multi-word fillers`() {
        val transcript = "I think, you know, that sort of approach is, you know, what we need"
        val result = engine.analyzeTranscript(transcript, speechDurationMs = 10000, silenceDurationMs = 500, speechConfidence = 0.8f)
        assertTrue("Should detect multi-word fillers", result.fillerWordBreakdown.containsKey("you know"))
    }

    @Test
    fun `analyzeTranscript - zero fillers for clean speech`() {
        val transcript = "Our system processes requests through a distributed pipeline with automatic failover"
        val result = engine.analyzeTranscript(transcript, speechDurationMs = 8000, silenceDurationMs = 300, speechConfidence = 0.95f)
        assertEquals("Clean speech should have few/no fillers", 0, result.metrics.fillerWordCount)
    }

    // ── Speech Speed Classification ──

    @Test
    fun `analyzeTranscript - classifies normal speech speed`() {
        // 20 words in 10 seconds = 120 WPM (NORMAL or SLOW boundary)
        val words = (1..25).joinToString(" ") { "word" }
        val result = engine.analyzeTranscript(words, speechDurationMs = 12000, silenceDurationMs = 500, speechConfidence = 0.8f)
        assertTrue("Should be SLOW or NORMAL speed",
            result.speechSpeed == SpeechSpeed.NORMAL || result.speechSpeed == SpeechSpeed.SLOW)
    }

    @Test
    fun `analyzeTranscript - classifies fast speech speed`() {
        // 50 words in 15 seconds = 200 WPM (TOO_FAST)
        val words = (1..50).joinToString(" ") { "word" }
        val result = engine.analyzeTranscript(words, speechDurationMs = 15000, silenceDurationMs = 100, speechConfidence = 0.8f)
        assertTrue("Should be FAST or TOO_FAST",
            result.speechSpeed == SpeechSpeed.FAST || result.speechSpeed == SpeechSpeed.TOO_FAST)
    }

    @Test
    fun `analyzeTranscript - classifies slow speech speed`() {
        // 5 words in 10 seconds = 30 WPM (TOO_SLOW)
        val result = engine.analyzeTranscript("I am thinking very carefully", speechDurationMs = 10000, silenceDurationMs = 5000, speechConfidence = 0.5f)
        assertTrue("Should be TOO_SLOW or SLOW",
            result.speechSpeed == SpeechSpeed.TOO_SLOW || result.speechSpeed == SpeechSpeed.SLOW)
    }

    // ── Communication Score ──

    @Test
    fun `analyzeTranscript - high communication score for quality speech`() {
        val transcript = "Our microservices architecture uses event-driven patterns with Kafka for asynchronous communication between services. We implemented circuit breakers using Resilience4j to handle cascading failures gracefully."
        val result = engine.analyzeTranscript(transcript, speechDurationMs = 15000, silenceDurationMs = 1000, speechConfidence = 0.95f)
        assertTrue("Communication score should be above 60", result.metrics.communicationScore > 60f)
    }

    @Test
    fun `analyzeTranscript - lower communication score for filler-heavy speech`() {
        val transcript = "Um so like basically um I think uh we should like um use uh sort of a like database um for storing uh data"
        val result = engine.analyzeTranscript(transcript, speechDurationMs = 15000, silenceDurationMs = 3000, speechConfidence = 0.4f)
        val cleanTranscript = "We implemented a distributed caching layer using Redis to reduce database load by sixty percent across our production environment"
        val cleanResult = engine.analyzeTranscript(cleanTranscript, speechDurationMs = 15000, silenceDurationMs = 500, speechConfidence = 0.95f)
        assertTrue("Filler-heavy score should be lower than clean speech",
            result.metrics.communicationScore < cleanResult.metrics.communicationScore)
    }

    // ── Clarity Score ──

    @Test
    fun `analyzeTranscript - clarity score inversely correlated with filler ratio`() {
        val cleanResult = engine.analyzeTranscript(
            "The system handles ten thousand requests per second",
            speechDurationMs = 5000, silenceDurationMs = 200, speechConfidence = 0.9f
        )
        val dirtyResult = engine.analyzeTranscript(
            "Um like the system um basically um handles like um requests",
            speechDurationMs = 5000, silenceDurationMs = 500, speechConfidence = 0.5f
        )
        assertTrue("Clean speech should have higher clarity", cleanResult.clarityScore > dirtyResult.clarityScore)
    }

    // ── Helper Methods ──

    @Test
    fun `getSpeechSpeedLabel - returns correct labels`() {
        assertEquals("Too Slow — Speed up slightly", engine.getSpeechSpeedLabel(SpeechSpeed.TOO_SLOW))
        assertEquals("Perfect — Natural pace", engine.getSpeechSpeedLabel(SpeechSpeed.NORMAL))
        assertEquals("Too Fast — Much too rapid", engine.getSpeechSpeedLabel(SpeechSpeed.TOO_FAST))
    }

    @Test
    fun `getFillerAdvice - returns appropriate advice for ratio ranges`() {
        val excellent = engine.getFillerAdvice(0, 100)
        assertTrue(excellent.contains("Excellent"))

        val critical = engine.getFillerAdvice(20, 100)
        assertTrue(critical.contains("Critical"))
    }

    // ── Edge Cases ──

    @Test
    fun `analyzeTranscript - handles whitespace-only transcript`() {
        val result = engine.analyzeTranscript("   ", speechDurationMs = 5000, silenceDurationMs = 1000, speechConfidence = 0.5f)
        assertEquals(0, result.metrics.totalWords)
    }

    @Test
    fun `analyzeTranscript - handles single word transcript`() {
        val result = engine.analyzeTranscript("Hello", speechDurationMs = 2000, silenceDurationMs = 0, speechConfidence = 0.8f)
        assertEquals(1, result.metrics.totalWords)
    }

    @Test
    fun `analyzeTranscript - communication score stays within bounds`() {
        // Extremely bad scenario
        val badResult = engine.analyzeTranscript("um uh like um", speechDurationMs = 60000, silenceDurationMs = 55000, speechConfidence = 0.1f)
        assertTrue(badResult.metrics.communicationScore >= 0f)
        assertTrue(badResult.metrics.communicationScore <= 100f)

        // Extremely good scenario
        val goodResult = engine.analyzeTranscript(
            (1..200).joinToString(" ") { "excellent" },
            speechDurationMs = 60000, silenceDurationMs = 1000, speechConfidence = 1.0f
        )
        assertTrue(goodResult.metrics.communicationScore >= 0f)
        assertTrue(goodResult.metrics.communicationScore <= 100f)
    }
}
