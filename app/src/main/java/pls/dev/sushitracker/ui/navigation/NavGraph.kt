package pls.dev.sushitracker.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pls.dev.sushitracker.ui.screens.*

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Counter : Screen("counter")
    data object History : Screen("history")
    data object Stats : Screen("stats")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SushiNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onStartCounter = {
                    navController.navigate(Screen.Counter.route)
                },
                onOpenHistory = {
                    navController.navigate(Screen.History.route)
                },
                onOpenStats = {
                    navController.navigate(Screen.Stats.route)
                }
            )
        }

        composable(Screen.Counter.route) {
            CounterScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
