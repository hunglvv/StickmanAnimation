package com.hunglvv.stickmananimation.library.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val DEFAULT_DELAY_INTERVAL = 350L
abstract class BaseMediaStoreViewModel(application: Application) :
    AndroidViewModel(application) {
    //region Const and Fields
    private var contentObserver: ContentObserver? = null

    private var lastTime = 0L
    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }
    //endregion

    //region abstract methods
    abstract suspend fun actionFetchData()
    //endregion

    //region open methods
    fun fetchData() {
        viewModelScope.launch {
            actionFetchData()
            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    getUriStore()
                ) {
                    fetchData()
                }
            }
        }
    }

    protected fun debounceTask(delay: Long = DEFAULT_DELAY_INTERVAL, task: () -> Unit) {
        if (System.currentTimeMillis() - lastTime > 500) {
            task()
            lastTime = System.currentTimeMillis()
        }
    }

    protected fun launchJob(delay: Long = DEFAULT_DELAY_INTERVAL, task: () -> Unit) {
        viewModelScope.launch(job) { task() }
    }

    protected fun launchSuspendJob(suspendTask: suspend () -> Unit) {
        viewModelScope.launch(job) { suspendTask() }
    }

    fun cancelJob(task: () -> Unit = {}) {
        job.cancel()
        task()
    }

    protected fun getUriStore(): Uri = MediaStore.Files.getContentUri("external")
    //endregion

    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
    }
}

/**
 * Convenience extension method to register a [ContentObserver] given a lambda.
 */
private fun ContentResolver.registerObserver(
    uri: Uri,
    observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}

