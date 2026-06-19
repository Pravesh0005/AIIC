package com.aiic.app.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val isPremium: Boolean = false,
    val interviewsCompleted: Int = 0,
    val readinessScore: Float = 0f,
    val streakDays: Int = 0,
    val joinedAt: Long = System.currentTimeMillis(),
)
