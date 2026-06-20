package com.aiic.app.domain.model

data class AuthCredentials(
    val email: String,
    val password: String,
    val name: String = "",
)

data class AuthSession(
    val uid: String,
    val email: String,
    val displayName: String?,
    val isEmailVerified: Boolean,
    val photoUrl: String?,
)
