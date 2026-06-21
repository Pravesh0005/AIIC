package com.aiic.app.presentation.feature_home

import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.domain.model.InterviewCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeState(
    val userName: String = "Praveen",
    val readinessScore: Float = 0.72f,
    val interviewsCompleted: Int = 14,
    val streakDays: Int = 7,
    val hoursOfPractice: Float = 23.5f,
    val recentCategories: List<InterviewCategory> = listOf(
        InterviewCategory.TECHNICAL,
        InterviewCategory.BEHAVIORAL,
        InterviewCategory.DSA,
    ),
)

sealed interface HomeAction {
    data object StartInterview : HomeAction
    data object ViewAnalytics : HomeAction
    data object ViewProfile : HomeAction
    data object Logout : HomeAction
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: com.aiic.app.domain.repository.AuthRepository
) : BaseViewModel<HomeState, HomeAction>(HomeState()) {

    init {
        val user = authRepository.getCurrentSession()
        if (user != null) {
            updateState { copy(userName = user.name.ifBlank { "User" }) }
        }
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ViewProfile -> {}
            HomeAction.ViewAnalytics -> {}
            HomeAction.StartInterview -> {}
            HomeAction.Logout -> {
                viewModelScope.launch {
                    authRepository.logout()
                    sendEvent(com.aiic.app.core.base.UiEvent.Navigate("login"))
                }
            }
        }
    }
}
