package com.hunglvv.stickmananimation.library.data.repository

import com.hunglvv.stickmananimation.library.data.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    suspend fun <T> safeTask(
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Result.Failure(throwable, null)
            }
        }
    }
}
