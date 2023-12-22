package com.hunglvv.stickmananimation.navigation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.hunglvv.stickmananimation.library.utils.defaultScreenEnterTransition
import com.hunglvv.stickmananimation.library.utils.defaultScreenExitTransition
import com.hunglvv.stickmananimation.library.utils.defaultScreenPopEnterTransition
import com.hunglvv.stickmananimation.library.utils.defaultScreenPopExitTransition
import com.hunglvv.stickmananimation.library.utils.navComposable

internal sealed class NavScreen(val route: String) {
    object Splash : NavScreen("Splash")
    object Intro : NavScreen("Intro")
    object Guide : NavScreen("Guide")
    object Home : NavScreen("Home")
    object Library : NavScreen("Library")
    object Preview : NavScreen("Preview/{urlMedia}") {
        fun createRoute(url: String): String {
            return "Preview/${Uri.encode(url)}"
        }
    }
}


@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavScreen.Splash.route,
        enterTransition = { defaultScreenEnterTransition(initialState, targetState) },
        exitTransition = { defaultScreenExitTransition(initialState, targetState) },
        popEnterTransition = { defaultScreenPopEnterTransition() },
        popExitTransition = { defaultScreenPopExitTransition() }
    ) {
        navComposable(
            route = NavScreen.Splash.route
        ) {
            BackHandler(true) {
                // Nothing executed
            }
//                navController.navigate(NavScreen.Home.route) {
//                    popUpTo(NavScreen.Splash.route) { inclusive = true }
//                    launchSingleTop = true
//                }
        }

        navComposable(
            route = NavScreen.Preview.route,
            arguments = listOf(
                navArgument("urlMedia") { type = NavType.StringType }
            )
        ) {
            BackHandler(true) {
                // Nothing executed
            }
//            PreviewMedia {
//                navController.navigateUp()
//            }
        }
    }
}