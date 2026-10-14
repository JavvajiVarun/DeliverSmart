package app.deliversmart.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.deliversmart.ui.home.HomeScreen
import app.deliversmart.ui.history.HistoryScreen
import app.deliversmart.ui.settings.SettingsScreen
import app.deliversmart.ui.onboarding.HowItWorksScreen
import app.deliversmart.ui.onboarding.PrivacyScreen
import app.deliversmart.ui.onboarding.PermissionsScreen
import app.deliversmart.ui.splash.SplashScreen

object Routes {
    const val SPLASH       = "splash"
    const val HOW_IT_WORKS = "how_it_works"
    const val PRIVACY      = "privacy"
    const val PERMISSIONS  = "permissions"
    const val HOME         = "home"
    const val HISTORY      = "history"
    const val SETTINGS     = "settings"
}

@Composable
fun DeliverSmartApp(onStartService: (Double, Double) -> Unit = { _, _ -> }) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                nav.navigate(Routes.HOW_IT_WORKS) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(Routes.HOW_IT_WORKS) {
            HowItWorksScreen(
                onNext = { nav.navigate(Routes.PRIVACY) },
                onSkip = { nav.navigate(Routes.PERMISSIONS) }
            )
        }
        composable(Routes.PRIVACY) {
            PrivacyScreen(
                onNext = { nav.navigate(Routes.PERMISSIONS) },
                onSkip = { nav.navigate(Routes.PERMISSIONS) }
            )
        }
        composable(Routes.PERMISSIONS) {
            PermissionsScreen(
                onDone = {
                    onStartService(2.5, 12.0)
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onHistory  = { nav.navigate(Routes.HISTORY) },
                onSettings = { nav.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                onHome     = { nav.popBackStack(Routes.HOME, false) },
                onSettings = { nav.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onHome    = { nav.popBackStack(Routes.HOME, false) },
                onHistory = { nav.navigate(Routes.HISTORY) }
            )
        }
    }
}