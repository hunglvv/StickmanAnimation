package com.hunglvv.stickmananimation.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.hunglvv.stickmananimation.library.ui.BaseActivity
import com.hunglvv.stickmananimation.navigation.AppNavigation
import com.hunglvv.stickmananimation.ui.theme.ComposeTemplateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
    override fun initViews(savedInstanceState: Bundle?) {
        // Turn off the decor fitting system windows, which means we need to through handling
        // insets
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComposeTemplateTheme {
                // A surface container using the 'background' color from the theme
//                val bottomSheetNavigator = rememberBottomSheetNavigator()
                val navController = rememberNavController(/*bottomSheetNavigator*/)
                CompositionLocalProvider(
                    LocalOverscrollConfiguration provides null
                ) {
                    AppNavigation(
                        navController = navController
                    )
                }
            }
        }
    }
}