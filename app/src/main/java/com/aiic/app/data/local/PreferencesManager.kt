package com.aiic.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aiic_preferences")

@Singleton
class PreferencesManager @Inject constructor(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    fun isOnboardingCompleted(): Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETED] ?: false }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[Keys.IS_LOGGED_IN] = loggedIn }
    }

    fun isLoggedIn(): Flow<Boolean> =
        context.dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }
}
