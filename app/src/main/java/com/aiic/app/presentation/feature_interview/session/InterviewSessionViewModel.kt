package com.aiic.app.presentation.feature_interview.session

import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.data.speech.SpeechRecognizerManager
import com.aiic.app.domain.engine.BodyLanguageAnalyzer
import com.aiic.app.domain.engine.VoiceAnalysisEngine
import com.aiic.app.domain.model.*
import com.aiic.app.domain.repository.InterviewQuestionRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import com.aiic.app.domain.usecase.CompleteInterviewUseCase
import com.aiic.app.domain.usecase.GenerateInterviewReportUseCase
import com.aiic.app.domain.usecase.SubmitAnswerAndEvaluateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

data class InterviewSessionState(
    val sessionId: String = "",
    val targetRole: String = "Software Engineer",
    val isLoading: Boolean = true,
    val currentQuestion: InterviewQuestion? = null,
    val questionNumber: Int = 1,
    val totalQuestions: Int = 5,
    val currentAnswerInput: String = "",
    val timeRemainingSeconds: Int = 0,
    val isEvaluating: Boolean = false,
    val sessionComplete: Boolean = false,
    val error: String? = null,
    val answeredCount: Int = 0,

    // Interview mode
    val interviewMode: InterviewMode = InterviewMode.TEXT,

    // Voice state
    val isVoiceRecording: Boolean = false,
    val voiceTranscript: String = "",
    val voiceRmsLevel: Float = 0f,

    // Camera state
    val cameraWarning: String? = null,

    // Pause state
    val isPaused: Boolean = false
)

sealed interface InterviewSessionAction {
    data class UpdateAnswerInput(val answer: String) : InterviewSessionAction
    data object SubmitAnswer : InterviewSessionAction
    data object QuitSession : InterviewSessionAction
    data object DismissError : InterviewSessionAction
    data object ToggleVoiceRecording : InterviewSessionAction
    data object TogglePause : InterviewSessionAction
}

@HiltViewModel
class InterviewSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val sessionRepository: InterviewSessionRepository,
    private val questionRepository: InterviewQuestionRepository,
    private val submitAnswerAndEvaluateUseCase: SubmitAnswerAndEvaluateUseCase,
    private val completeInterviewUseCase: CompleteInterviewUseCase,
    private val generateInterviewReportUseCase: GenerateInterviewReportUseCase,
    private val voiceAnalysisEngine: VoiceAnalysisEngine,
    private val bodyLanguageAnalyzer: BodyLanguageAnalyzer
) : BaseViewModel<InterviewSessionState, InterviewSessionAction>(InterviewSessionState()) {

    companion object {
        private const val TAG = "AIIC_SESSION_VM"
    }

    private val pendingQuestions = mutableListOf<InterviewQuestion>()
    private var speechManager: SpeechRecognizerManager? = null
    private var timerJob: Job? = null
    private var voiceCollectorJob: Job? = null
    private var accumulatedVoiceMetrics: VoiceMetrics? = null

    init {
        val sessionId = savedStateHandle.get<String>("sessionId") ?: ""
        updateState { copy(sessionId = sessionId) }
        loadSession(sessionId)
    }

    private fun loadSession(sessionId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            val sessionResult = sessionRepository.getSessionById(sessionId)
            if (sessionResult is NetworkResult.Success) {
                updateState {
                    copy(
                        targetRole = sessionResult.data.role,
                        interviewMode = sessionResult.data.interviewMode
                    )
                }
            }

            when (val result = questionRepository.getQuestionsForSession(sessionId)) {
                is NetworkResult.Success -> {
                    val questions = result.data.sortedBy { it.order }
                    pendingQuestions.addAll(questions)

                    if (pendingQuestions.isNotEmpty()) {
                        val firstQ = pendingQuestions.removeAt(0)
                        updateState {
                            copy(
                                isLoading = false,
                                currentQuestion = firstQ,
                                totalQuestions = questions.size,
                                questionNumber = 1
                            )
                        }
                        startTimer()

                        // Initialize voice if needed
                        if (currentState.interviewMode != InterviewMode.TEXT) {
                            initializeSpeechRecognizer()
                        }
                        // Initialize camera if needed
                        if (currentState.interviewMode == InterviewMode.VIDEO) {
                            initializeCamera()
                        }
                    } else {
                        updateState { copy(isLoading = false, error = "No questions found.") }
                    }
                }
                is NetworkResult.Error -> {
                    updateState { copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    override fun onAction(action: InterviewSessionAction) {
        when (action) {
            is InterviewSessionAction.UpdateAnswerInput -> {
                updateState { copy(currentAnswerInput = action.answer) }
            }
            InterviewSessionAction.SubmitAnswer -> submitCurrentAnswer()
            InterviewSessionAction.QuitSession -> quitSession()
            InterviewSessionAction.DismissError -> updateState { copy(error = null) }
            InterviewSessionAction.ToggleVoiceRecording -> toggleVoiceRecording()
            InterviewSessionAction.TogglePause -> togglePause()
        }
    }

    private fun initializeSpeechRecognizer() {
        speechManager = SpeechRecognizerManager(application.applicationContext)
        if (!speechManager!!.isAvailable()) {
            updateState { copy(error = "Speech recognition not available on this device") }
            return
        }

        voiceCollectorJob = viewModelScope.launch {
            launch {
                speechManager!!.rmsLevel.collect { rms ->
                    updateState { copy(voiceRmsLevel = rms) }
                }
            }
            launch {
                speechManager!!.accumulatedText.collect { text ->
                    updateState { copy(voiceTranscript = text) }
                }
            }
            launch {
                speechManager!!.state.collect { speechState ->
                    when (speechState) {
                        is SpeechRecognizerManager.SpeechState.Error -> {
                            Log.e(TAG, "Speech error: ${speechState.message}")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private var cameraManager: com.aiic.app.data.camera.CameraAnalysisManager? = null

    private fun initializeCamera() {
        cameraManager = com.aiic.app.data.camera.CameraAnalysisManager(application, bodyLanguageAnalyzer)
        viewModelScope.launch {
            cameraManager?.warning?.collect { warn ->
                updateState { copy(cameraWarning = warn) }
            }
        }
    }

    fun startCamera(lifecycleOwner: androidx.lifecycle.LifecycleOwner, previewView: androidx.camera.view.PreviewView) {
        cameraManager?.startCamera(lifecycleOwner, previewView)
    }

    private fun toggleVoiceRecording() {
        val manager = speechManager ?: return

        if (currentState.isVoiceRecording) {
            manager.stopListening()
            updateState { copy(isVoiceRecording = false) }
        } else {
            manager.startListening()
            updateState { copy(isVoiceRecording = true) }
        }
    }

    private fun togglePause() {
        updateState { copy(isPaused = !currentState.isPaused) }
        if (currentState.isPaused && currentState.isVoiceRecording) {
            speechManager?.stopListening()
            updateState { copy(isVoiceRecording = false) }
        }
    }

    private fun submitCurrentAnswer() {
        val question = currentState.currentQuestion ?: return

        // Get the answer from either voice transcript or text input
        val answer = if (currentState.interviewMode != InterviewMode.TEXT && currentState.voiceTranscript.isNotBlank()) {
            currentState.voiceTranscript
        } else {
            currentState.currentAnswerInput.trim()
        }

        if (answer.isBlank()) {
            updateState { copy(error = "Please provide an answer before submitting.") }
            return
        }

        if (currentState.isEvaluating) return

        // Stop voice recording if active
        if (currentState.isVoiceRecording) {
            speechManager?.stopListening()
            updateState { copy(isVoiceRecording = false) }
        }

        // Analyze voice metrics if applicable
        if (currentState.interviewMode != InterviewMode.TEXT && speechManager != null) {
            val voiceResult = voiceAnalysisEngine.analyzeTranscript(
                transcript = currentState.voiceTranscript,
                speechDurationMs = speechManager!!.getSpeechDurationMs(),
                silenceDurationMs = speechManager!!.getSilenceDurationMs(),
                speechConfidence = 0.8f
            )
            accumulatedVoiceMetrics = voiceResult.metrics
        }

        updateState { copy(isEvaluating = true, error = null, currentAnswerInput = answer) }

        viewModelScope.launch {
            val responseTimeMs = (currentState.timeRemainingSeconds * 1000).toLong()

            val saveResult = withTimeoutOrNull(10000L) {
                submitAnswerAndEvaluateUseCase(
                    currentState.sessionId, question, answer, responseTimeMs, currentState.targetRole
                )
            }

            when {
                saveResult == null -> {
                    updateState { copy(error = "Save timed out. Please try again.", isEvaluating = false) }
                }
                saveResult is NetworkResult.Success -> {
                    moveToNextQuestion()
                }
                saveResult is NetworkResult.Error -> {
                    updateState { copy(error = "Save failed: ${saveResult.message}", isEvaluating = false) }
                }
            }
        }
    }

    private fun moveToNextQuestion() {
        val newAnsweredCount = currentState.answeredCount + 1

        if (pendingQuestions.isEmpty()) {
            updateState { copy(answeredCount = newAnsweredCount) }
            finishSession()
        } else {
            // Reset voice state for next question
            speechManager?.resetTranscript()

            val nextQ = pendingQuestions.removeAt(0)
            updateState {
                copy(
                    currentQuestion = nextQ,
                    questionNumber = currentState.questionNumber + 1,
                    currentAnswerInput = "",
                    voiceTranscript = "",
                    isEvaluating = false,
                    answeredCount = newAnsweredCount
                )
            }
        }
    }

    private fun finishSession() {
        viewModelScope.launch {
            updateState { copy(isEvaluating = true) }

            // Complete the interview (batch AI evaluation of answers)
            withTimeoutOrNull(30000L) {
                completeInterviewUseCase(currentState.sessionId)
            }

            // Generate comprehensive report
            withTimeoutOrNull(45000L) {
                generateInterviewReportUseCase(
                    sessionId = currentState.sessionId,
                    voiceMetrics = accumulatedVoiceMetrics,
                    bodyLanguageReport = bodyLanguageAnalyzer.generateReport().takeIf { bodyLanguageAnalyzer.getFrameCount() > 0 }
                )
            }

            // Navigate to summary
            updateState { copy(sessionComplete = true, isEvaluating = false) }
            sendEvent(UiEvent.Navigate("interview_summary/${currentState.sessionId}"))
        }
    }

    private fun quitSession() {
        viewModelScope.launch {
            sessionRepository.updateSessionStatus(currentState.sessionId, SessionStatus.ABANDONED)
            cleanup()
            sendEvent(UiEvent.NavigateBack)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var time = 0
            while (!currentState.sessionComplete) {
                delay(1000)
                if (!currentState.isPaused) {
                    time++
                    updateState { copy(timeRemainingSeconds = time) }
                }
            }
        }
    }

    private fun cleanup() {
        speechManager?.destroy()
        speechManager = null
        timerJob?.cancel()
        voiceCollectorJob?.cancel()
        bodyLanguageAnalyzer.reset()
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }
}
