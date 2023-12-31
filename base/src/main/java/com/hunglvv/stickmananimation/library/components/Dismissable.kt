package com.hunglvv.stickmananimation.library.components

import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Dismissable(
    onDismiss: () -> Unit,
    directions: Set<DismissDirection> = setOf(
        DismissDirection.StartToEnd,
        DismissDirection.EndToStart
    ),
    content: @Composable () -> Unit,
    background: @Composable () -> Unit = {}
) {
    val dismissState = rememberDismissState {
        if (it != DismissValue.Default) {
            onDismiss.invoke()
        }
        true
    }
    SwipeToDismiss(
        state = dismissState,
        directions = directions,
        background = { background() },
        dismissContent = { content() }
    )
}