package com.aiic.app.data.model

data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val isPremium: Boolean = false,
    val interviewsCompleted: Int = 0,
    val readinessScore: Float = 0f,
    val streakDays: Int = 0,
    val joinedAt: Long = 0L,
)

fun UserDto.toDomain() = com.aiic.app.domain.model.User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    isPremium = isPremium,
    interviewsCompleted = interviewsCompleted,
    readinessScore = readinessScore,
    streakDays = streakDays,
    joinedAt = joinedAt,
)

fun com.aiic.app.domain.model.User.toDto() = UserDto(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    isPremium = isPremium,
    interviewsCompleted = interviewsCompleted,
    readinessScore = readinessScore,
    streakDays = streakDays,
    joinedAt = joinedAt,
)
