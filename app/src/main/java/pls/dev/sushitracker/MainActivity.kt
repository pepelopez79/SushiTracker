package pls.dev.sushitracker

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import pls.dev.sushitracker.data.AppSettingsManager
import pls.dev.sushitracker.data.AppTheme
import pls.dev.sushitracker.ui.navigation.SushiNavGraph
import pls.dev.sushitracker.ui.theme.SushiTrackerTheme
import pls.dev.sushitracker.ui.theme.getColorsForTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val settingsManager = AppSettingsManager(this)

        setContent {
            var currentTheme by remember { mutableStateOf(settingsManager.getTheme()) }
            val colors = getColorsForTheme(currentTheme)

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window

                    window.statusBarColor = colors.background.toArgb()
                    window.navigationBarColor = colors.background.toArgb()

                    val isDarkTheme = currentTheme == AppTheme.DARK
                    WindowCompat.getInsetsController(window, view).apply {
                        isAppearanceLightStatusBars = !isDarkTheme
                        isAppearanceLightNavigationBars = !isDarkTheme
                    }
                }
            }

            SushiTrackerTheme(colors = colors) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colors.background
                ) {
                    val navController = rememberNavController()

                    Box(modifier = Modifier.safeDrawingPadding()) {
                        SushiNavGraph(
                            navController = navController,
                            currentTheme = currentTheme,
                            onThemeChange = { newTheme ->
                                currentTheme = newTheme
                                settingsManager.setTheme(newTheme)
                            },
                            colors = colors
                        )
                    }
                }
            }
        }
    }
}