package com.aiic.app.presentation.feature_settings

import androidx.lifecycle.viewModelScope
import com.aiic.app.core.base.BaseViewModel
import com.aiic.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val language: String = "English",
    val notificationsEnabled: Boolean = true,
    val hapticEnabled: Boolean = true
)

sealed interface SettingsAction {
    data class UpdateLanguage(val language: String) : SettingsAction
    data class UpdateNotifications(val enabled: Boolean) : SettingsAction
    data class UpdateHaptics(val enabled: Boolean) : SettingsAction
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : BaseViewModel<SettingsState, SettingsAction>(SettingsState()) {

    init {
        preferencesRepository.getLanguage()
            .onEach { lang ->
                updateState { copy(language = lang) }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.UpdateLanguage -> {
                viewModelScope.launch {
                    preferencesRepository.setLanguage(action.language)
                }
            }
            is SettingsAction.UpdateNotifications -> {
                updateState { copy(notificationsEnabled = action.enabled) }
            }
            is SettingsAction.UpdateHaptics -> {
                updateState { copy(hapticEnabled = action.enabled) }
            }
        }
    }
}
