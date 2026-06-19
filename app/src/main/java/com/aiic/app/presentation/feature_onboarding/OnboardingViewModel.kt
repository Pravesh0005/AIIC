package com.aiic.app.presentation.feature_onboarding

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
)

sealed interface OnboardingAction {
    data object NextPage : OnboardingAction
    data object PreviousPage : OnboardingAction
    data object Skip : OnboardingAction
    data object Complete : OnboardingAction
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val accentEmoji: String,
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Master Every\nInterview",
        description = "Practice with AI-powered mock interviews tailored to your target role, company, and experience level.",
        accentEmoji = "🎯",
    ),
    OnboardingPage(
        title = "Real-Time\nAI Feedback",
        description = "Get instant, actionable insights on your answers, body language cues, and communication patterns.",
        accentEmoji = "⚡",
    ),
    OnboardingPage(
        title = "Land Your\nDream Job",
        description = "Track your progress, build confidence, and walk into every interview fully prepared.",
        accentEmoji = "🚀",
    ),
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<OnboardingState, OnboardingAction>(OnboardingState()) {

    override fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.NextPage -> {
                if (currentState.currentPage < currentState.totalPages - 1) {
                    updateState { copy(currentPage = currentPage + 1) }
                }
            }
            OnboardingAction.PreviousPage -> {
                if (currentState.currentPage > 0) {
                    updateState { copy(currentPage = currentPage - 1) }
                }
            }
            OnboardingAction.Skip, OnboardingAction.Complete -> {
                viewModelScope.launch {
                    userPreferencesRepository.setOnboardingCompleted(true)
                }
            }
        }
    }
}
