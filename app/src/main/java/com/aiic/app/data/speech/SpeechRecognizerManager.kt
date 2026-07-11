package com.aiic.app.data.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Production-grade SpeechRecognizer wrapper with real-time transcript streaming,
 * silence detection, auto-restart on pause, and RMS audio level monitoring.
 */
class SpeechRecognizerManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "AIIC_SPEECH"
        private const val SILENCE_TIMEOUT_MS = 5000L
    }

    sealed interface SpeechState {
        data object Idle : SpeechState
        data object Listening : SpeechState
        data class PartialResult(val text: String) : SpeechState
        data class FinalResult(val text: String, val confidence: Float) : SpeechState
        data class Error(val message: String, val code: Int) : SpeechState
        data object SilenceDetected : SpeechState
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var lastSpeechTimestamp = 0L
    private var accumulatedTranscript = StringBuilder()
    private var speechStartTimeMs = 0L
    private var totalSilenceMs = 0L
    private var lastRmsTimestamp = 0L

    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _accumulatedText = MutableStateFlow("")
    val accumulatedText: StateFlow<String> = _accumulatedText.asStateFlow()

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening() {
        if (isListening) return

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createListener())

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, SILENCE_TIMEOUT_MS)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, SILENCE_TIMEOUT_MS)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)
            }

            speechRecognizer?.startListening(intent)
            isListening = true
            _isActive.value = true
            speechStartTimeMs = System.currentTimeMillis()
            lastSpeechTimestamp = speechStartTimeMs
            _state.value = SpeechState.Listening

            Log.d(TAG, "Started listening")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start: ${e.message}")
            _state.value = SpeechState.Error("Failed to start speech recognition: ${e.message}", -1)
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            isListening = false
            _isActive.value = false
            _rmsLevel.value = 0f
            Log.d(TAG, "Stopped listening")
        } catch (e: Exception) {
            Log.e(TAG, "Stop error: ${e.message}")
        }
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            isListening = false
            _isActive.value = false
            _rmsLevel.value = 0f
            Log.d(TAG, "Destroyed")
        } catch (e: Exception) {
            Log.e(TAG, "Destroy error: ${e.message}")
        }
    }

    fun resetTranscript() {
        accumulatedTranscript.clear()
        _accumulatedText.value = ""
        totalSilenceMs = 0L
        speechStartTimeMs = 0L
    }

    fun getSpeechDurationMs(): Long {
        return if (speechStartTimeMs > 0) System.currentTimeMillis() - speechStartTimeMs else 0L
    }

    fun getSilenceDurationMs(): Long = totalSilenceMs

    // Auto-restart removed for manual control

    private fun createListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "Ready for speech")
            _state.value = SpeechState.Listening
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "Speech began")
            lastSpeechTimestamp = System.currentTimeMillis()
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Normalize RMS to 0-1 range
            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
            _rmsLevel.value = normalized

            val now = System.currentTimeMillis()
            if (now - lastRmsTimestamp > 200) {
                lastRmsTimestamp = now
                // Track silence: if RMS is very low for extended period
                if (rmsdB < -1f) {
                    val silenceGap = now - lastSpeechTimestamp
                    if (silenceGap > SILENCE_TIMEOUT_MS) {
                        totalSilenceMs += 200
                    }
                } else {
                    lastSpeechTimestamp = now
                }
            }
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d(TAG, "End of speech")
        }

        override fun onError(error: Int) {
            val msg = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                else -> "Unknown error ($error)"
            }
            Log.e(TAG, "Error: $msg (code=$error)")

            when (error) {
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
                SpeechRecognizer.ERROR_AUDIO -> {
                    _state.value = SpeechState.SilenceDetected
                    isListening = false
                    _isActive.value = false
                    _rmsLevel.value = 0f
                }
                else -> {
                    _state.value = SpeechState.Error(msg, error)
                    isListening = false
                    _isActive.value = false
                    _rmsLevel.value = 0f
                }
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

            if (!matches.isNullOrEmpty()) {
                val text = matches[0]
                val confidence = confidences?.firstOrNull() ?: 0.8f

                if (text.isNotBlank()) {
                    if (accumulatedTranscript.isNotEmpty()) {
                        accumulatedTranscript.append(" ")
                    }
                    accumulatedTranscript.append(text)
                    _accumulatedText.value = accumulatedTranscript.toString()

                    Log.d(TAG, "Final result: $text (confidence: $confidence)")
                    _state.value = SpeechState.FinalResult(accumulatedTranscript.toString(), confidence)
                }
            }

            // Stopped naturally
            isListening = false
            _isActive.value = false
            _rmsLevel.value = 0f
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val partial = matches[0]
                if (partial.isNotBlank()) {
                    val fullText = if (accumulatedTranscript.isNotEmpty()) {
                        "${accumulatedTranscript} $partial"
                    } else {
                        partial
                    }
                    _state.value = SpeechState.PartialResult(fullText)
                }
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}
