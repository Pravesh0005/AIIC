package com.aiic.app.domain.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profilePhotoUrl: String = "",
    val targetRole: String = "",
    val targetCompany: String = "",
    val education: String = "",
    val skills: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false,
    val interviewCount: Int = 0,
    val resumeScore: Float = 0f,
    val readinessScore: Float = 0f,
    val isPremium: Boolean = false,
    val createdAt: Long = 0L,
    val lastActiveAt: Long = 0L,
    val preferences: UserPreferences = UserPreferences(),
)

data class UserPreferences(
    val darkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val hapticFeedback: Boolean = true,
    val dailyReminder: Boolean = false,
    val preferredLanguage: String = "en",
)
