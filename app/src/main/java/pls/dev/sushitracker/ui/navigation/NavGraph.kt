package pls.dev.sushitracker.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pls.dev.sushitracker.data.AppTheme
import pls.dev.sushitracker.ui.screens.*
import pls.dev.sushitracker.ui.theme.SushiColors

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Counter : Screen("counter")
    data object History : Screen("history")
    data object Stats : Screen("stats")
    data object Achievements : Screen("achievements")
    data object Settings : Screen("settings")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SushiNavGraph(
    navController: NavHostController,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    colors: SushiColors
) {
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
                },
                colors = colors
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                colors = colors,
                onStartCounter = {
                    navController.navigate(Screen.Counter.route)
                },
                onOpenHistory = {
                    navController.navigate(Screen.History.route)
                },
                onOpenStats = {
                    navController.navigate(Screen.Stats.route)
                },
                onOpenAchievements = {
                    navController.navigate(Screen.Achievements.route)
                },
                onOpenSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Counter.route) {
            CounterScreen(
                colors = colors,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                colors = colors,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                colors = colors,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(
                colors = colors,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onThemeChange = onThemeChange,
                currentTheme = currentTheme,
                colors = colors
            )
        }
    }
}
