package com.aiic.app.domain.engine

import com.aiic.app.domain.model.BodyLanguageReport
import com.aiic.app.domain.model.FaceAnalysisFrame
import com.aiic.app.domain.model.LightingQuality
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Analyzes face detection frames to produce body language metrics.
 * Accumulates frames over the interview and generates a comprehensive report.
 * Runs entirely on-device.
 */
@Singleton
class BodyLanguageAnalyzer @Inject constructor() {

    companion object {
        private const val EYE_CONTACT_HEAD_Y_THRESHOLD = 15f  // degrees
        private const val EYE_CONTACT_HEAD_X_THRESHOLD = 12f
        private const val LOOKING_DOWN_X_THRESHOLD = 20f
        private const val LOOKING_SIDEWAYS_Y_THRESHOLD = 25f
        private const val SMILE_THRESHOLD = 0.5f
        private const val BLINK_THRESHOLD = 0.3f
        private const val NERVOUS_HEAD_MOVEMENT_THRESHOLD = 8f
    }

    private val frames = mutableListOf<FaceAnalysisFrame>()
    private var warningsList = mutableListOf<String>()

    fun addFrame(frame: FaceAnalysisFrame) {
        frames.add(frame)

        // Real-time warning detection
        if (!frame.faceDetected) {
            addWarning("Face not visible — look at camera")
        }
        if (frame.lightingQuality == LightingQuality.TOO_DARK) {
            addWarning("Too dark — improve lighting")
        }
        if (frame.multipleFaces) {
            addWarning("Multiple faces detected — interview alone")
        }
    }

    private fun addWarning(warning: String) {
        if (warningsList.lastOrNull() != warning) {
            warningsList.add(warning)
        }
    }

    fun getLatestWarning(): String? {
        if (frames.isEmpty()) return null
        // Only show "Face not detected" if the last 5 frames all missed face detection
        // This avoids false positives with glasses, partial face, etc.
        val recentFrames = frames.takeLast(5)
        val allMissed = recentFrames.size >= 5 && recentFrames.all { !it.faceDetected }
        return if (allMissed) {
            "Face not detected"
        } else if (frames.last().lightingQuality == LightingQuality.TOO_DARK) {
            "Too dark"
        } else if (frames.last().multipleFaces) {
            "Multiple faces"
        } else {
            val last = frames.last()
            when {
                abs(last.headEulerAngleX) > LOOKING_DOWN_X_THRESHOLD -> "Looking down"
                abs(last.headEulerAngleY) > LOOKING_SIDEWAYS_Y_THRESHOLD -> "Looking away"
                else -> null
            }
        }
    }

    fun generateReport(): BodyLanguageReport {
        if (frames.isEmpty()) {
            return BodyLanguageReport(
                warnings = listOf("No camera data collected"),
                suggestions = listOf("Enable camera for body language analysis")
            )
        }

        val detectedFrames = frames.filter { it.faceDetected }
        val totalFrames = frames.size
        val detectedCount = detectedFrames.size

        if (detectedCount == 0) {
            return BodyLanguageReport(
                warnings = listOf("Face was never detected during the interview"),
                suggestions = listOf("Position yourself in front of the camera with good lighting")
            )
        }

        // ── Eye Contact Score ──
        val eyeContactFrames = detectedFrames.count { frame ->
            abs(frame.headEulerAngleY) < EYE_CONTACT_HEAD_Y_THRESHOLD &&
            abs(frame.headEulerAngleX) < EYE_CONTACT_HEAD_X_THRESHOLD
        }
        val eyeContactScore = (eyeContactFrames.toFloat() / detectedCount * 100f).coerceIn(0f, 100f)

        // ── Looking Down Percentage ──
        val lookingDownFrames = detectedFrames.count { frame ->
            frame.headEulerAngleX > LOOKING_DOWN_X_THRESHOLD
        }
        val lookingDownPercentage = (lookingDownFrames.toFloat() / detectedCount * 100f).coerceIn(0f, 100f)

        // ── Looking Sideways Percentage ──
        val lookingSidewaysFrames = detectedFrames.count { frame ->
            abs(frame.headEulerAngleY) > LOOKING_SIDEWAYS_Y_THRESHOLD
        }
        val lookingSidewaysPercentage = (lookingSidewaysFrames.toFloat() / detectedCount * 100f).coerceIn(0f, 100f)

        // ── Smile Frequency ──
        val smilingFrames = detectedFrames.count { it.smileProbability > SMILE_THRESHOLD }
        val smileFrequency = (smilingFrames.toFloat() / detectedCount * 100f).coerceIn(0f, 100f)

        // ── Facial Expression Score ──
        val avgSmile = detectedFrames.map { it.smileProbability }.average().toFloat()
        val facialExpressionScore = when {
            avgSmile > 0.7f -> 90f  // Very expressive
            avgSmile > 0.4f -> 80f  // Good expression
            avgSmile > 0.2f -> 65f  // Neutral
            else -> 45f  // Too serious
        }

        // ── Head Movement Score ──
        val headMovements = if (detectedFrames.size > 1) {
            detectedFrames.zipWithNext().count { (a, b) ->
                abs(a.headEulerAngleY - b.headEulerAngleY) > NERVOUS_HEAD_MOVEMENT_THRESHOLD ||
                abs(a.headEulerAngleX - b.headEulerAngleX) > NERVOUS_HEAD_MOVEMENT_THRESHOLD
            }
        } else 0
        val headMovementRatio = headMovements.toFloat() / maxOf(1, detectedCount)
        val headMovementScore = ((1f - headMovementRatio) * 100f).coerceIn(0f, 100f)

        // ── Nervousness Score (lower is better for the candidate) ──
        val nervousnessScore = (headMovementRatio * 50f + lookingSidewaysPercentage * 0.3f + (1f - avgSmile) * 20f)
            .coerceIn(0f, 100f)

        // ── Energy Score ──
        val energyScore = (facialExpressionScore * 0.4f + smileFrequency * 0.3f + (100f - nervousnessScore) * 0.3f)
            .coerceIn(0f, 100f)

        // ── Engagement Score ──
        val faceVisibilityRatio = detectedCount.toFloat() / totalFrames
        val engagementScore = (eyeContactScore * 0.4f + faceVisibilityRatio * 100f * 0.3f + (100f - lookingDownPercentage) * 0.3f)
            .coerceIn(0f, 100f)

        // ── Confidence Score ──
        val confidenceScore = (eyeContactScore * 0.3f + (100f - nervousnessScore) * 0.3f + facialExpressionScore * 0.2f + headMovementScore * 0.2f)
            .coerceIn(0f, 100f)

        // ── Professionalism Score ──
        val professionalismScore = (eyeContactScore * 0.25f + headMovementScore * 0.25f + (100f - lookingDownPercentage) * 0.25f + engagementScore * 0.25f)
            .coerceIn(0f, 100f)

        // ── Overall Score ──
        val overallScore = (confidenceScore * 0.2f + professionalismScore * 0.2f + eyeContactScore * 0.2f +
                engagementScore * 0.15f + energyScore * 0.1f + facialExpressionScore * 0.1f + headMovementScore * 0.05f)
            .coerceIn(0f, 100f)

        // ── Suggestions ──
        val suggestions = mutableListOf<String>()
        if (eyeContactScore < 60f) suggestions.add("Maintain more eye contact with the camera")
        if (lookingDownPercentage > 30f) suggestions.add("Reduce looking down — keep your head up")
        if (smileFrequency < 20f) suggestions.add("Smile more to appear approachable")
        if (nervousnessScore > 60f) suggestions.add("Keep your head steady — excessive movement shows nervousness")
        if (faceVisibilityRatio < 0.7f) suggestions.add("Keep your face visible to the camera at all times")
        if (suggestions.isEmpty()) suggestions.add("Excellent body language throughout the interview!")

        return BodyLanguageReport(
            confidenceScore = confidenceScore,
            professionalismScore = professionalismScore,
            eyeContactScore = eyeContactScore,
            facialExpressionScore = facialExpressionScore,
            energyScore = energyScore,
            engagementScore = engagementScore,
            nervousnessScore = nervousnessScore,
            smileFrequency = smileFrequency,
            headMovementScore = headMovementScore,
            lookingDownPercentage = lookingDownPercentage,
            lookingSidewaysPercentage = lookingSidewaysPercentage,
            overallBodyLanguageScore = overallScore,
            warnings = warningsList.distinct().takeLast(10),
            suggestions = suggestions
        )
    }

    fun reset() {
        frames.clear()
        warningsList.clear()
    }

    fun getFrameCount(): Int = frames.size
}
