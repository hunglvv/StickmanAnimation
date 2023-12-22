package com.hunglvv.stickmananimation.library.data.model

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException

private const val RETRY_TIME_IN_MILLIS = 15_000L
private const val RETRY_ATTEMPT_COUNT = 3

sealed class Result<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Result<T>(data)
    class Failure<T>(
        throwable: Throwable,
        data: T? = null
    ) : Result<T>(data, throwable)

    class Loading<T>(data: T? = null) : Result<T>(data)
}


fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> {
            Result.Success(it)
        }
        .onStart { emit(Result.Loading()) }
        .retryWhen { cause, attempt ->
            if (cause is IOException && attempt < RETRY_ATTEMPT_COUNT) {
                delay(RETRY_TIME_IN_MILLIS)
                true
            } else {
                false
            }
        }
        .catch { emit(Result.Failure(it)) }
}