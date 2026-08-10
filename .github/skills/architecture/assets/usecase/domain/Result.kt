package com.example.app.domain

sealed class Result<out T : Any>

data class Failure(val error: Error?) : Result<Nothing>()

data class Success<out T : Any>(val data: T) : Result<T>()

data class Loading<out T : Any>(val data: T) : Result<Nothing>()

data class Error(
        val code: Int = 0,
        var httpError: Int = 0,
        val title: String? = null,
        val message: String? = null
) {
    val formattedTitle
        get() = title ?: ""
    val formattedMessage
        get() = message ?: ""
    val formattedMessageCode
        get() = "$code - $title - $message"
}

inline fun <T : Any> Result<T>.onFailure(action: (Error?) -> Unit) {
    if (this is Failure) {
        action(error)
    }
}

inline fun <T : Any> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Success) {
        action(data)
    }
    return this
}
