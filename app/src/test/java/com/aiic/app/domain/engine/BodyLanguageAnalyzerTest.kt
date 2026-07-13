package com.aiic.app.domain.engine

import com.aiic.app.domain.model.FaceAnalysisFrame
import com.aiic.app.domain.model.LightingQuality
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BodyLanguageAnalyzerTest {

    private lateinit var analyzer: BodyLanguageAnalyzer

    @Before
    fun setup() {
        analyzer = BodyLanguageAnalyzer()
    }

    @Test
    fun `generateReport - returns default report when no frames`() {
        val report = analyzer.generateReport()
        assertTrue(report.warnings.contains("No camera data collected"))
        assertEquals(0, analyzer.getFrameCount())
    }

    @Test
    fun `generateReport - returns warning when face never detected`() {
        repeat(10) {
            analyzer.addFrame(FaceAnalysisFrame(timestamp = it * 500L, faceDetected = false))
        }
        val report = analyzer.generateReport()
        assertTrue(report.warnings.any { it.contains("never detected") })
    }

    @Test
    fun `generateReport - high eye contact when looking at camera`() {
        
        repeat(20) {
            analyzer.addFrame(
                FaceAnalysisFrame(
                    timestamp = it * 500L,
                    faceDetected = true,
                    headEulerAngleX = 2f,
                    headEulerAngleY = 3f,
                    smileProbability = 0.5f,
                    leftEyeOpenProbability = 0.9f,
                    rightEyeOpenProbability = 0.9f
                )
            )
        }
        val report = analyzer.generateReport()
        assertTrue("Eye contact score should be high", report.eyeContactScore > 70f)
    }

    @Test
    fun `generateReport - low eye contact when looking away`() {
        repeat(20) {
            analyzer.addFrame(
                FaceAnalysisFrame(
                    timestamp = it * 500L,
                    faceDetected = true,
                    headEulerAngleX = 25f,  
                    headEulerAngleY = 30f,  
                    smileProbability = 0.2f
                )
            )
        }
        val report = analyzer.generateReport()
        assertTrue("Eye contact score should be low", report.eyeContactScore < 30f)
    }

    @Test
    fun `generateReport - high confidence for steady head and good eye contact`() {
        repeat(30) {
            analyzer.addFrame(
                FaceAnalysisFrame(
                    timestamp = it * 500L,
                    faceDetected = true,
                    headEulerAngleX = 1f,
                    headEulerAngleY = -1f,
                    headEulerAngleZ = 0f,
                    smileProbability = 0.6f,
                    leftEyeOpenProbability = 0.95f,
                    rightEyeOpenProbability = 0.95f
                )
            )
        }
        val report = analyzer.generateReport()
        assertTrue("Confidence should be high", report.confidenceScore > 60f)
    }

    @Test
    fun `generateReport - high nervousness for erratic head movement`() {
        
        repeat(20) { i ->
            analyzer.addFrame(
                FaceAnalysisFrame(
                    timestamp = i * 500L,
                    faceDetected = true,
                    headEulerAngleX = if (i % 2 == 0) 20f else -20f,
                    headEulerAngleY = if (i % 2 == 0) 25f else -25f,
                    smileProbability = 0.1f
                )
            )
        }
        val report = analyzer.generateReport()
        assertTrue("Nervousness should be elevated", report.nervousnessScore > 30f)
    }

    @Test
    fun `generateReport - high facial expression score when smiling`() {
        repeat(20) {
            analyzer.addFrame(
                FaceAnalysisFrame(
                    timestamp = it * 500L,
                    faceDetected = true,
                    smileProbability = 0.8f,
                    headEulerAngleX = 0f,
                    headEulerAngleY = 0f
                )
            )
        }
        val report = analyzer.generateReport()
        assertTrue("Facial expression score should be high", report.facialExpressionScore > 70f)
        assertTrue("Smile frequency should be high", report.smileFrequency > 50f)
    }

    @Test
    fun `getLatestWarning - returns face not detected warning`() {
        analyzer.addFrame(FaceAnalysisFrame(timestamp = 0L, faceDetected = false))
        assertEquals("Face not detected", analyzer.getLatestWarning())
    }

    @Test
    fun `getLatestWarning - returns looking down warning`() {
        analyzer.addFrame(
            FaceAnalysisFrame(
                timestamp = 0L,
                faceDetected = true,
                headEulerAngleX = 25f, 
                headEulerAngleY = 0f
            )
        )
        assertEquals("Looking down", analyzer.getLatestWarning())
    }

    @Test
    fun `getLatestWarning - returns looking away warning`() {
        analyzer.addFrame(
            FaceAnalysisFrame(
                timestamp = 0L,
                faceDetected = true,
                headEulerAngleX = 0f,
                headEulerAngleY = 30f 
            )
        )
        assertEquals("Looking away", analyzer.getLatestWarning())
    }

    @Test
    fun `getLatestWarning - returns null for good posture`() {
        analyzer.addFrame(
            FaceAnalysisFrame(
                timestamp = 0L,
                faceDetected = true,
                headEulerAngleX = 2f,
                headEulerAngleY = 3f
            )
        )
        assertNull(analyzer.getLatestWarning())
    }

    @Test
    fun `generateReport - suggests eye contact improvement when score low`() {
        repeat(20) {
            analyzer.addFrame(
                FaceAnalysisFrame(
                    timestamp = it * 500L,
                    faceDetected = true,
                    headEulerAngleX = 18f,
                    headEulerAngleY = 20f,
                    smileProbability = 0.1f
                )
            )
        }
        val report = analyzer.generateReport()
        assertTrue(report.suggestions.any { it.contains("eye contact", ignoreCase = true) })
    }

    @Test
    fun `reset - clears all accumulated data`() {
        repeat(10) {
            analyzer.addFrame(FaceAnalysisFrame(timestamp = it * 500L, faceDetected = true))
        }
        assertEquals(10, analyzer.getFrameCount())
        analyzer.reset()
        assertEquals(0, analyzer.getFrameCount())
    }

    @Test
    fun `generateReport - all scores within 0-100 bounds`() {
        
        repeat(50) { i ->
            analyzer.addFrame(
                FaceAnalysisFrame(
                    timestamp = i * 500L,
                    faceDetected = i % 3 != 0,
                    headEulerAngleX = (i * 7f % 40f) - 20f,
                    headEulerAngleY = (i * 5f % 50f) - 25f,
                    smileProbability = (i * 3f % 100f) / 100f,
                    leftEyeOpenProbability = 0.8f,
                    rightEyeOpenProbability = 0.8f,
                    lightingQuality = if (i % 10 == 0) LightingQuality.TOO_DARK else LightingQuality.GOOD,
                    multipleFaces = i % 15 == 0
                )
            )
        }
        val report = analyzer.generateReport()

        listOf(
            report.confidenceScore,
            report.professionalismScore,
            report.eyeContactScore,
            report.facialExpressionScore,
            report.energyScore,
            report.engagementScore,
            report.nervousnessScore,
            report.headMovementScore,
            report.overallBodyLanguageScore
        ).forEach { score ->
            assertTrue("Score $score should be >= 0", score >= 0f)
            assertTrue("Score $score should be <= 100", score <= 100f)
        }
    }

    @Test
    fun `addFrame - generates warning for dark lighting`() {
        analyzer.addFrame(
            FaceAnalysisFrame(
                timestamp = 0L,
                faceDetected = true,
                lightingQuality = LightingQuality.TOO_DARK
            )
        )
        assertEquals("Too dark", analyzer.getLatestWarning())
    }

    @Test
    fun `addFrame - generates warning for multiple faces`() {
        analyzer.addFrame(
            FaceAnalysisFrame(
                timestamp = 0L,
                faceDetected = true,
                multipleFaces = true
            )
        )
        assertEquals("Multiple faces", analyzer.getLatestWarning())
    }
}
