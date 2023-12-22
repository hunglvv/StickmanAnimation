package com.hunglvv.stickmananimation.library.utils.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ConfigScreen(
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    statusBarColor: Color = Color.Transparent,
    darkStatusContent: Boolean = false,
    navigationBarColor: Color = Color.Transparent,
    darkIcons: Boolean = navigationBarColor.luminance() > 0.5f,
) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController) {
        systemUiController.isStatusBarVisible = showStatusBar
        systemUiController.isNavigationBarVisible = showNavigationBar
        systemUiController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        systemUiController.setStatusBarColor(statusBarColor, darkStatusContent)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons)
        onDispose {

        }
    }
}

@Composable
fun ConfigSystemUi(
    key: Any? = null,
    showStatusBar: Boolean = true,
    showNavigationBar: Boolean = true,
    statusBarColor: Color = Color.Transparent,
    darkStatusContent: Boolean = false,
    navigationBarColor: Color = Color.Transparent,
    darkIcons: Boolean = navigationBarColor.luminance() > 0.5f,
) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, key) {
        systemUiController.isStatusBarVisible = showStatusBar
        systemUiController.isNavigationBarVisible = showNavigationBar
        systemUiController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        systemUiController.setStatusBarColor(statusBarColor, darkStatusContent)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons)
        onDispose { }
    }
}