package com.aiic.app.data.remote

import com.google.firebase.auth.FirebaseAuthException

object FirebaseErrorMapper {
    fun map(e: Exception): String = when (e) {
        is FirebaseAuthException -> when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "The email address is invalid."
            "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists."
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 8 characters."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later."
            "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is not enabled."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Check your connection."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already associated with another account."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid credentials. Please try again."
            else -> e.localizedMessage ?: "Authentication failed."
        }
        else -> e.localizedMessage ?: "An unexpected error occurred."
    }
}
