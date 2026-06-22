package com.aiic.app.core.base

/**
 * Type-safe network result wrapper replacing kotlin.Result.
 * Carries error codes for proper error taxonomy.
 */
sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Error(
        val code: Int = -1,
        val message: String,
        val throwable: Throwable? = null,
    ) : NetworkResult<Nothing>
}

inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onError(action: (NetworkResult.Error) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(this)
    return this
}

fun <T> NetworkResult<T>.getOrNull(): T? =
    (this as? NetworkResult.Success)?.data

fun <T> NetworkResult<T>.getOrThrow(): T =
    (this as? NetworkResult.Success)?.data
        ?: throw (this as NetworkResult.Error).throwable
            ?: IllegalStateException((this as NetworkResult.Error).message)
