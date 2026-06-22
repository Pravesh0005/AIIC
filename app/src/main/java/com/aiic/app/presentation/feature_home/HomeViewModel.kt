package com.aiic.app.presentation.feature_home

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.domain.model.InterviewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val userName: String = "User",
    val userEmail: String = "",
    val readinessScore: Float = 0.0f,
    val interviewsCompleted: Int = 0,
    val streakDays: Int = 0,
    val hoursOfPractice: Float = 0.0f,
    val recentCategories: List<InterviewType> = listOf(
        InterviewType.TECHNICAL,
        InterviewType.BEHAVIORAL,
        InterviewType.MIXED,
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
    private val authRepository: com.aiic.app.domain.repository.AuthRepository,
    private val userRepository: com.aiic.app.domain.repository.UserRepository
) : BaseViewModel<HomeState, HomeAction>(HomeState()) {

    init {
        val user = authRepository.getCurrentSession()
        if (user != null) {
            updateState { 
                copy(
                    userName = user.displayName?.ifBlank { "User" } ?: "User",
                    userEmail = user.email
                ) 
            }
            
            viewModelScope.launch {
                userRepository.observeUserProfile(user.uid).collect { profile ->
                    if (profile != null) {
                        updateState {
                            copy(
                                userName = profile.name.ifBlank { userName },
                                readinessScore = profile.readinessScore,
                                interviewsCompleted = profile.interviewCount,
                                streakDays = 1, // Simulated active streak
                                hoursOfPractice = profile.interviewCount * 0.5f // Approx 30 mins per interview
                            )
                        }
                    }
                }
            }
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
