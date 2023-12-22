package com.hunglvv.stickmananimation.library.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val DEFAULT_DELAY_INTERVAL = 350L

abstract class BaseViewModel : ViewModel() {

    private var lastTime = 0L
    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }

    protected fun debounceTask(delay: Long = DEFAULT_DELAY_INTERVAL, task: () -> Unit) {
        if (System.currentTimeMillis() - lastTime > delay) {
            task()
            lastTime = System.currentTimeMillis()
        }
    }

    protected fun launchJob(task: () -> Unit) {
        viewModelScope.launch(job) { task() }
    }

    protected fun launchSuspendJob(suspendTask: suspend () -> Unit) {
        cancelJob()
        viewModelScope.launch(job) { suspendTask() }
    }

    fun cancelJob(task: () -> Unit = {}) {
        job.cancel()
        task()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }
}

abstract class BaseAndroidViewModel(application: Application) :
    AndroidViewModel(application) {

    private var lastTime = 0L
    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }

    protected fun debounceTask(delay: Long = DEFAULT_DELAY_INTERVAL, task: () -> Unit) {
        if (System.currentTimeMillis() - lastTime > delay) {
            task()
            lastTime = System.currentTimeMillis()
        }
    }

    protected fun launchJob(task: () -> Unit) {
        viewModelScope.launch(job) { task() }
    }

    protected fun launchSuspendJob(suspendTask: suspend () -> Unit) {
        viewModelScope.launch(job) { suspendTask() }
    }

    fun cancelJob(task: () -> Unit = {}) {
        job.cancel()
        task()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }
}
