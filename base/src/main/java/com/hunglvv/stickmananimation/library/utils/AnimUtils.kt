package com.hunglvv.stickmananimation.library.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

fun brightnessEnterTransition(): EnterTransition {
    return fadeIn() + slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth }
    )
}

fun brightnessExitTransition(): ExitTransition {
    return fadeOut() + slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth }
    )
}