package com.aiic.app.data.camera

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.aiic.app.domain.engine.BodyLanguageAnalyzer
import com.aiic.app.domain.model.FaceAnalysisFrame
import com.aiic.app.domain.model.LightingQuality
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executors

/**
 * Manages CameraX with MLKit Face Detection for real-time body language analysis.
 * No recording — only live frame analysis.
 */
class CameraAnalysisManager(
    private val context: Context,
    private val bodyLanguageAnalyzer: BodyLanguageAnalyzer
) {
    companion object {
        private const val TAG = "AIIC_CAMERA"
        private const val ANALYSIS_INTERVAL_MS = 500L // Analyze every 500ms
    }

    private var cameraProvider: ProcessCameraProvider? = null
    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private var lastAnalysisTime = 0L

    private val _currentFrame = MutableStateFlow(FaceAnalysisFrame())
    val currentFrame: StateFlow<FaceAnalysisFrame> = _currentFrame.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _warning = MutableStateFlow<String?>(null)
    val warning: StateFlow<String?> = _warning.asStateFlow()

    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
        .build()

    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(640, 480))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(analysisExecutor, ::analyzeImage) }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                _isActive.value = true
                Log.d(TAG, "Camera started successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Camera start failed: ${e.message}")
                _isActive.value = false
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastAnalysisTime < ANALYSIS_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastAnalysisTime = now

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                processDetectionResult(faces, now)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Face detection failed: ${e.message}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun processDetectionResult(faces: List<Face>, timestamp: Long) {
        val frame = if (faces.isEmpty()) {
            FaceAnalysisFrame(
                timestamp = timestamp,
                faceDetected = false
            )
        } else {
            val face = faces[0]

            FaceAnalysisFrame(
                timestamp = timestamp,
                faceDetected = true,
                smileProbability = face.smilingProbability ?: 0f,
                leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f,
                rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0f,
                headEulerAngleX = face.headEulerAngleX,
                headEulerAngleY = face.headEulerAngleY,
                headEulerAngleZ = face.headEulerAngleZ,
                faceBoundsValid = face.boundingBox.width() > 50,
                lightingQuality = estimateLighting(face),
                multipleFaces = faces.size > 1
            )
        }

        _currentFrame.value = frame
        bodyLanguageAnalyzer.addFrame(frame)
        _warning.value = bodyLanguageAnalyzer.getLatestWarning()
    }

    private fun estimateLighting(face: Face): LightingQuality {
        // Use face bounding box size as proxy for visibility/lighting
        val faceArea = face.boundingBox.width() * face.boundingBox.height()
        return when {
            faceArea < 5000 -> LightingQuality.TOO_DARK
            faceArea < 15000 -> LightingQuality.ADEQUATE
            faceArea < 50000 -> LightingQuality.GOOD
            else -> LightingQuality.GOOD
        }
    }

    fun stopCamera() {
        try {
            cameraProvider?.unbindAll()
            _isActive.value = false
            Log.d(TAG, "Camera stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Stop error: ${e.message}")
        }
    }

    fun destroy() {
        stopCamera()
        faceDetector.close()
        analysisExecutor.shutdown()
    }
}
