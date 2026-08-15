package com.example.app.repository

import com.example.app.domain.Error
import com.example.app.domain.Failure
import com.example.app.domain.Result
import com.example.app.domain.Success
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response

val generalFailure = Failure(null)

inline fun <T : Any> Response<T>.onSuccess(action: (T) -> Unit): Response<T> {
    if (isSuccessful) {
        body()?.run(action)
    }
    return this
}

inline fun <T : Any> Response<T>.onFailure(action: (Error) -> Unit) {
    if (!isSuccessful) {
        errorBody()?.use { action(extractError(code(), it)) }
    }
}

fun <T : DomainMapperResponse<R>, R : Any> Response<T>.extractData(): Result<R> {
    onSuccess {
        return Success(it.mapToDomain())
    }

    onFailure {
        return Failure(it)
    }

    return generalFailure
}

fun Response<Unit>.extractNoData(): Result<Unit> {
    onSuccess {
        return Success(Unit)
    }

    onFailure {
        return Failure(it)
    }

    return generalFailure
}

fun <T : List<DomainMapperResponse<R>>, R : Any> Response<T>.extractList(): Result<List<R>> {
    onSuccess {
        return Success(it.map { item -> item.mapToDomain() })
    }

    onFailure {
        return Failure(it)
    }

    return generalFailure
}

fun Response<Void>.processData(): Result<Unit> {
    return if (isSuccessful) {
        Success(Unit)
    } else {
        errorBody()?.use { Failure(extractError(code(), it)) } ?: generalFailure
    }
}

fun extractError(httpError: Int, body: ResponseBody): Error {
    val msg = body.string()

    if (msg.isNullOrEmpty().not()) {
        Gson().fromJson(msg, Error::class.java)?.let {
            return it.apply { this.httpError = httpError }
        }
    }

    return Error(httpError = httpError)
}
