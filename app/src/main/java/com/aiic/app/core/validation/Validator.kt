package com.aiic.app.core.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
)

object Validator {

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email is required")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult(false, "Enter a valid email address")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "Password is required")
            password.length < 8 -> ValidationResult(false, "At least 8 characters required")
            !password.any { it.isUpperCase() } -> ValidationResult(false, "Include at least one uppercase letter")
            !password.any { it.isDigit() } -> ValidationResult(false, "Include at least one number")
            else -> ValidationResult(true)
        }
    }

    fun validateConfirmPassword(password: String, confirm: String): ValidationResult {
        return when {
            confirm.isBlank() -> ValidationResult(false, "Please confirm your password")
            confirm != password -> ValidationResult(false, "Passwords do not match")
            else -> ValidationResult(true)
        }
    }

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Name is required")
            name.length < 2 -> ValidationResult(false, "Name must be at least 2 characters")
            else -> ValidationResult(true)
        }
    }

    fun validateRequired(value: String, fieldName: String): ValidationResult {
        return if (value.isBlank()) ValidationResult(false, "$fieldName is required")
        else ValidationResult(true)
    }
}
