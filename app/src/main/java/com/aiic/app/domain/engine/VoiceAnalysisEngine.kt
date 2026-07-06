package com.aiic.app.domain.engine

import com.aiic.app.domain.model.SpeechSpeed
import com.aiic.app.domain.model.VoiceAnalysisResult
import com.aiic.app.domain.model.VoiceMetrics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analyzes voice transcripts for filler words, WPM, speech speed, and communication quality.
 * Runs entirely on-device — no network calls required.
 */
@Singleton
class VoiceAnalysisEngine @Inject constructor() {

    companion object {
        val FILLER_WORDS = setOf(
            "um", "uh", "hmm", "like", "actually", "basically",
            "sort of", "kind of", "you know", "i mean", "so yeah",
            "right", "well", "okay so", "literally", "honestly",
            "essentially", "obviously", "clearly"
        )

        private const val IDEAL_WPM_MIN = 120f
        private const val IDEAL_WPM_MAX = 160f
        private const val SLOW_WPM = 90f
        private const val FAST_WPM = 180f
    }

    fun analyzeTranscript(
        transcript: String,
        speechDurationMs: Long,
        silenceDurationMs: Long,
        speechConfidence: Float
    ): VoiceAnalysisResult {
        if (transcript.isBlank()) {
            return VoiceAnalysisResult(transcript = transcript)
        }

        val words = transcript.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }
        val totalWords = words.size
        val speechDurationMinutes = speechDurationMs / 60000f

        // Calculate WPM
        val wpm = if (speechDurationMinutes > 0.05f) {
            totalWords / speechDurationMinutes
        } else {
            0f
        }

        // Detect filler words
        val fillerBreakdown = mutableMapOf<String, Int>()
        val lowerTranscript = transcript.lowercase()

        // Check multi-word fillers first
        FILLER_WORDS.filter { it.contains(" ") }.forEach { filler ->
            val regex = "\\b${Regex.escape(filler)}\\b".toRegex()
            val count = regex.findAll(lowerTranscript).count()
            if (count > 0) {
                fillerBreakdown[filler] = count
            }
        }

        // Check single-word fillers
        FILLER_WORDS.filter { !it.contains(" ") }.forEach { filler ->
            val count = words.count { it.replace("[^a-z]".toRegex(), "") == filler }
            if (count > 0) {
                fillerBreakdown[filler] = count
            }
        }

        val totalFillers = fillerBreakdown.values.sum()

        // Calculate speech speed
        val speechSpeed = when {
            wpm < SLOW_WPM -> SpeechSpeed.TOO_SLOW
            wpm < IDEAL_WPM_MIN -> SpeechSpeed.SLOW
            wpm <= IDEAL_WPM_MAX -> SpeechSpeed.NORMAL
            wpm <= FAST_WPM -> SpeechSpeed.FAST
            else -> SpeechSpeed.TOO_FAST
        }

        // Calculate average pause duration
        val estimatedPauses = maxOf(1, (speechDurationMs / 5000).toInt()) // Rough estimate
        val averagePauseMs = if (silenceDurationMs > 0) {
            silenceDurationMs / estimatedPauses
        } else {
            0L
        }

        // Calculate communication score (0-100)
        val fillerPenalty = (totalFillers.toFloat() / maxOf(1, totalWords)) * 100f
        val speedBonus = when (speechSpeed) {
            SpeechSpeed.NORMAL -> 25f
            SpeechSpeed.SLOW, SpeechSpeed.FAST -> 15f
            else -> 5f
        }
        val confidenceBonus = speechConfidence * 25f
        val contentBonus = minOf(25f, totalWords / 4f) // More words = more content
        val communicationScore = (100f - fillerPenalty + speedBonus + confidenceBonus + contentBonus)
            .coerceIn(0f, 100f)

        // Clarity score based on filler ratio
        val fillerRatio = totalFillers.toFloat() / maxOf(1, totalWords)
        val clarityScore = ((1f - fillerRatio) * 100f).coerceIn(0f, 100f)

        val metrics = VoiceMetrics(
            wordsPerMinute = wpm,
            speechDurationMs = speechDurationMs,
            silenceDurationMs = silenceDurationMs,
            fillerWordCount = totalFillers,
            fillerWords = fillerBreakdown,
            speechConfidence = speechConfidence,
            communicationScore = communicationScore,
            totalWords = totalWords,
            averagePauseMs = averagePauseMs
        )

        return VoiceAnalysisResult(
            transcript = transcript,
            metrics = metrics,
            fillerWordBreakdown = fillerBreakdown,
            speechSpeed = speechSpeed,
            clarityScore = clarityScore
        )
    }

    fun getSpeechSpeedLabel(speed: SpeechSpeed): String = when (speed) {
        SpeechSpeed.TOO_SLOW -> "Too Slow — Speed up slightly"
        SpeechSpeed.SLOW -> "Slow — Slightly below ideal"
        SpeechSpeed.NORMAL -> "Perfect — Natural pace"
        SpeechSpeed.FAST -> "Fast — Consider slowing down"
        SpeechSpeed.TOO_FAST -> "Too Fast — Much too rapid"
    }

    fun getFillerAdvice(fillerCount: Int, totalWords: Int): String {
        val ratio = fillerCount.toFloat() / maxOf(1, totalWords)
        return when {
            ratio < 0.02f -> "Excellent — Almost no filler words detected"
            ratio < 0.05f -> "Good — Minimal filler usage"
            ratio < 0.10f -> "Fair — Try to reduce filler words"
            ratio < 0.15f -> "Needs Work — Noticeable filler word usage"
            else -> "Critical — Excessive filler words affecting clarity"
        }
    }
}
