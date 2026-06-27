package com.aiic.app.presentation.feature_interview.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.core.base.NetworkResult
import com.aiic.app.core.base.UiEvent
import com.aiic.app.domain.model.InterviewQuestion
import com.aiic.app.domain.repository.InterviewQuestionRepository
import com.aiic.app.domain.repository.InterviewSessionRepository
import com.aiic.app.domain.usecase.CompleteInterviewUseCase
import com.aiic.app.domain.usecase.SubmitAnswerAndEvaluateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val answeredCount: Int = 0
)

sealed interface InterviewSessionAction {
    data class UpdateAnswerInput(val answer: String) : InterviewSessionAction
    data object SubmitAnswer : InterviewSessionAction
    data object QuitSession : InterviewSessionAction
    data object DismissError : InterviewSessionAction
}

@HiltViewModel
class InterviewSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: InterviewSessionRepository,
    private val questionRepository: InterviewQuestionRepository,
    private val submitAnswerAndEvaluateUseCase: SubmitAnswerAndEvaluateUseCase,
    private val completeInterviewUseCase: CompleteInterviewUseCase
) : BaseViewModel<InterviewSessionState, InterviewSessionAction>(InterviewSessionState()) {

    private val pendingQuestions = mutableListOf<InterviewQuestion>()

    init {
        val sessionId = savedStateHandle.get<String>("sessionId") ?: ""
        updateState { copy(sessionId = sessionId) }
        loadSession(sessionId)
    }

    private fun loadSession(sessionId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            // Fetch session to get target role
            val sessionResult = sessionRepository.getSessionById(sessionId)
            if (sessionResult is NetworkResult.Success) {
                updateState { copy(targetRole = sessionResult.data.role) }
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
                    } else {
                        updateState { copy(isLoading = false, error = "No questions found for this session.") }
                    }
                }
                is NetworkResult.Error -> {
                    updateState { copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    override fun onAction(action: InterviewSessionAction) {
        when (action) {
            is InterviewSessionAction.UpdateAnswerInput -> {
                updateState { copy(currentAnswerInput = action.answer) }
            }
            InterviewSessionAction.SubmitAnswer -> {
                submitCurrentAnswer()
            }
            InterviewSessionAction.QuitSession -> {
                viewModelScope.launch {
                    sessionRepository.updateSessionStatus(currentState.sessionId, com.aiic.app.domain.model.SessionStatus.ABANDONED)
                    sendEvent(UiEvent.NavigateBack)
                }
            }
            InterviewSessionAction.DismissError -> {
                updateState { copy(error = null) }
            }
        }
    }

    private fun submitCurrentAnswer() {
        val question = currentState.currentQuestion ?: return
        val answer = currentState.currentAnswerInput.trim()

        if (answer.isBlank()) {
            updateState { copy(error = "Please provide an answer before submitting.") }
            return
        }

        // Prevent double-submit
        if (currentState.isEvaluating) return

        updateState { copy(isEvaluating = true, error = null) }

        viewModelScope.launch {
            val responseTimeMs = (currentState.timeRemainingSeconds * 1000).toLong()

            // Lightweight save — no heavy AI call per answer
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
                    // Answer saved successfully — move to next question immediately
                    // NO per-answer feedback navigation — batch analysis at session end
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
            // All questions answered — run batch analysis at session end
            updateState { copy(answeredCount = newAnsweredCount) }
            finishSession()
        } else {
            val nextQ = pendingQuestions.removeAt(0)
            updateState {
                copy(
                    currentQuestion = nextQ,
                    questionNumber = currentState.questionNumber + 1,
                    currentAnswerInput = "",
                    isEvaluating = false,
                    answeredCount = newAnsweredCount
                )
            }
        }
    }

    private fun finishSession() {
        viewModelScope.launch {
            updateState { copy(isEvaluating = true) }

            // Batch AI analysis at session end — this is where the heavy evaluation runs
            val completeResult = withTimeoutOrNull(30000L) {
                completeInterviewUseCase(currentState.sessionId)
            }

            // Always navigate to summary — even if analysis fails
            updateState { copy(sessionComplete = true, isEvaluating = false) }
            sendEvent(UiEvent.Navigate("interview_summary/${currentState.sessionId}"))
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            var time = 0
            while (!currentState.sessionComplete) {
                delay(1000)
                time++
                updateState { copy(timeRemainingSeconds = time) }
            }
        }
    }
}
