package pls.dev.sushitracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import pls.dev.sushitracker.ui.navigation.SushiNavGraph
import pls.dev.sushitracker.ui.theme.Background
import pls.dev.sushitracker.ui.theme.SushiTrackerTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Background.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Background.toArgb())
        )
        setContent {
            SushiTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    Surface(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        color = Background
                    ) {
                        val navController = rememberNavController()
                        SushiNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
