package pls.dev.sushitracker.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pls.dev.sushitracker.data.AppLanguage
import pls.dev.sushitracker.data.AppStrings
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
    currentLanguage: AppLanguage,
    strings: AppStrings.Strings,
    onThemeChange: (AppTheme) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    colors: SushiColors
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                colors = colors,
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                colors = colors,
                strings = strings,
                onStartCounter = { navController.navigate(Screen.Counter.route) },
                onOpenHistory = { navController.navigate(Screen.History.route) },
                onOpenStats = { navController.navigate(Screen.Stats.route) },
                onOpenAchievements = { navController.navigate(Screen.Achievements.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Counter.route) {
            CounterScreen(
                colors = colors,
                strings = strings,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                colors = colors,
                strings = strings,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                colors = colors,
                strings = strings,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(
                colors = colors,
                strings = strings,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                colors = colors,
                strings = strings,
                currentTheme = currentTheme,
                currentLanguage = currentLanguage,
                onThemeChange = onThemeChange,
                onLanguageChange = onLanguageChange,
                onBack = { navController.popBackStack() }
            )
        }
    }
}