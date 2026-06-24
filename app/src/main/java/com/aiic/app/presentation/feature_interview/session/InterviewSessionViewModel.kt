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
    val error: String? = null
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

            // HARD 25-second timeout
            val evalResult = withTimeoutOrNull(25000L) {
                submitAnswerAndEvaluateUseCase(currentState.sessionId, question, answer, responseTimeMs, currentState.targetRole)
            }

            when {
                evalResult == null -> {
                    // Timeout hit — allow user to retry
                    updateState { copy(error = "Evaluation timed out. Please try submitting again.", isEvaluating = false) }
                }
                evalResult is NetworkResult.Success -> {
                    val followUp = evalResult.data
                    if (followUp != null) {
                        pendingQuestions.add(0, followUp)
                        updateState { copy(totalQuestions = currentState.totalQuestions + 1) }
                    }
                    val currentQId = question.questionId
                    moveToNextQuestion()
                    sendEvent(UiEvent.Navigate("answer_feedback/${currentQId}"))
                }
                evalResult is NetworkResult.Error -> {
                    // Error from AI — allow user to retry
                    updateState { copy(error = "Evaluation issue: ${evalResult.message}. Please try submitting again.", isEvaluating = false) }
                }
            }
        }
    }

    private fun moveToNextQuestion() {
        if (pendingQuestions.isEmpty()) {
            finishSession()
        } else {
            val nextQ = pendingQuestions.removeAt(0)
            updateState {
                copy(
                    currentQuestion = nextQ,
                    questionNumber = currentState.questionNumber + 1,
                    currentAnswerInput = "",
                    isEvaluating = false
                )
            }
        }
    }

    private fun finishSession() {
        viewModelScope.launch {
            updateState { copy(isEvaluating = true) }

            // Timeout the completion too — never block
            val completeResult = withTimeoutOrNull(15000L) {
                completeInterviewUseCase(currentState.sessionId)
            }

            when {
                completeResult == null -> {
                    // Even if scoring fails, let user proceed
                    updateState { copy(sessionComplete = true, isEvaluating = false) }
                    sendEvent(UiEvent.Navigate("interview_summary/${currentState.sessionId}"))
                }
                completeResult is NetworkResult.Success -> {
                    updateState { copy(sessionComplete = true, isEvaluating = false) }
                    sendEvent(UiEvent.Navigate("interview_summary/${currentState.sessionId}"))
                }
                completeResult is NetworkResult.Error -> {
                    // Still navigate to summary even on error
                    updateState { copy(sessionComplete = true, isEvaluating = false, error = completeResult.message) }
                    sendEvent(UiEvent.Navigate("interview_summary/${currentState.sessionId}"))
                }
            }
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
