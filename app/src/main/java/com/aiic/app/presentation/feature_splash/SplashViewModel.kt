package com.aiic.app.presentation.feature_splash

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(
    val isAnimating: Boolean = true,
)

sealed interface SplashAction

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<SplashState, SplashAction>(SplashState()) {

    fun checkDestination(
        onNavigateToOnboarding: () -> Unit,
        onNavigateToHome: () -> Unit,
    ) {
        viewModelScope.launch {
            delay(2500)
            val onboardingDone = userPreferencesRepository.isOnboardingCompleted().first()
            val loggedIn = userPreferencesRepository.isLoggedIn().first()
            if (onboardingDone && loggedIn) onNavigateToHome()
            else onNavigateToOnboarding()
        }
    }

    override fun onAction(action: SplashAction) {}
}
