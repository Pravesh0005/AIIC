package com.aiic.app.presentation.feature_splash

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.domain.model.AuthSession
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(
    val isAnimating: Boolean = true,
)

sealed interface SplashAction

sealed interface SplashDestination {
    data object Onboarding : SplashDestination
    data object Login : SplashDestination
    data object Home : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
) : BaseViewModel<SplashState, SplashAction>(SplashState()) {

    fun resolveDestination(onResolved: (SplashDestination) -> Unit) {
        viewModelScope.launch {
            delay(2000)

            val onboardingDone = sessionRepository.isOnboardingCompleted().first()
            if (!onboardingDone) {
                onResolved(SplashDestination.Onboarding)
                return@launch
            }

            val currentSession: AuthSession? = authRepository.getCurrentSession()
            if (currentSession != null) {
                sessionRepository.updateLastActive()
                onResolved(SplashDestination.Home)
            } else {
                onResolved(SplashDestination.Login)
            }
        }
    }

    override fun onAction(action: SplashAction) {}
}
