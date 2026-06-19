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
}

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeState, HomeAction>(HomeState()) {
    override fun onAction(action: HomeAction) {}
}
