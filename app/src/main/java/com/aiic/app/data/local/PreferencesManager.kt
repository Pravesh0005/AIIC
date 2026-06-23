package com.aiic.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val LANGUAGE = stringPreferencesKey("language")
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

    suspend fun saveUserSession(userId: String, name: String, email: String, token: String = "") {
        context.dataStore.edit {
            it[Keys.USER_ID] = userId
            it[Keys.USER_NAME] = name
            it[Keys.USER_EMAIL] = email
            it[Keys.AUTH_TOKEN] = token
            it[Keys.IS_LOGGED_IN] = true
        }
    }

    fun getUserId(): Flow<String> = context.dataStore.data.map { it[Keys.USER_ID] ?: "" }
    fun getUserName(): Flow<String> = context.dataStore.data.map { it[Keys.USER_NAME] ?: "" }
    fun getUserEmail(): Flow<String> = context.dataStore.data.map { it[Keys.USER_EMAIL] ?: "" }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(Keys.USER_ID)
            it.remove(Keys.USER_NAME)
            it.remove(Keys.USER_EMAIL)
            it.remove(Keys.AUTH_TOKEN)
            it[Keys.IS_LOGGED_IN] = false
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    fun getLanguage(): Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "English" }
}
