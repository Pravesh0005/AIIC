package com.aiic.app.common.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy"): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))

fun Float.toPercentageString(): String = "${(this * 100).toInt()}%"

fun Long.toReadableDuration(): String {
    val minutes = this / 60000
    val seconds = (this % 60000) / 1000
    return "${minutes}m ${seconds}s"
}

fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean = length >= 8
