package com.testarossa.template.library.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SavedStateHandleDelegate<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T,
    initializer: (valueLoadedFromState: T?, setter: (T) -> Unit) -> Unit
) : ReadWriteProperty<Any, T> {
    private val state: MutableState<T>

    init {
        val savedValue = savedStateHandle.get<T>(key)
        state = mutableStateOf(
            savedValue ?: defaultValue
        )
        initializer(savedValue, ::updateValue)
    }

    override fun getValue(thisRef: Any, property: KProperty<*>) = state.value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        updateValue(value)
    }

    private fun updateValue(value: T) {
        state.value = value
        savedStateHandle.set(key, value)
    }
}

fun <T> SavedStateHandle.mutableStateOf(
    defaultValue: T,
    initializer: (valueLoadedFromState: T?, setter: (T) -> Unit) -> Unit = { _, _ -> },
) = PropertyDelegateProvider<Any, SavedStateHandleDelegate<T>> { _, property ->
    SavedStateHandleDelegate(
        savedStateHandle = this,
        key = property.name,
        defaultValue = defaultValue,
        initializer = initializer
    )
}

/** Used:
var v1 by state.mutableStateOf("0")
var v2 by state.mutableStateOf("0")

val result by state.mutableStateOf("0") { valueLoadedFromState, setter ->
snapshotFlow { v1 to v2 }
.drop(if (valueLoadedFromState != null) 1 else 0)
.mapLatest {
sum(it.first, it.second)
}
.onEach {
setter(it)
}
.launchIn(viewModelScope)
}
 * */