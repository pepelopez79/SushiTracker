package pls.dev.sushitracker.ui.theme

import androidx.compose.ui.graphics.Color
import pls.dev.sushitracker.data.AppTheme

data class SushiColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val primaryDark: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val mutedForeground: Color,
    val border: Color,
    val itemBackground: Color,
    val itemForeground: Color
)

val DarkThemeColors = SushiColors(
    background = Color(0xFF1B2838),
    surface = Color(0xFF2A3A4A),
    primary = Color(0xFF4ECDC4),
    primaryDark = Color(0xFF2A9D8F),
    onPrimary = Color(0xFF1B2838),
    secondary = Color(0xFF3D4D5C),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    mutedForeground = Color(0xFF94A3B3),
    border = Color(0xFF394959),
    itemBackground = Color(0xFFFFFFFF),
    itemForeground = Color(0xFF1A1A1A)
)

val LightThemeColors = SushiColors(
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF4ECDC4),
    primaryDark = Color(0xFF2A9D8F),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFE0E0E0),
    onSecondary = Color(0xFF1A1A1A),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    mutedForeground = Color(0xFF757575),
    border = Color(0xFFE0E0E0),
    itemBackground = Color(0xFFFFFFFF),
    itemForeground = Color(0xFF1A1A1A)
)

val SalmonThemeColors = SushiColors(
    background = Color(0xFFFFF0F0),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFFFA8072),
    primaryDark = Color(0xFFE76F6F),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFE4E1),
    onSecondary = Color(0xFF8B4513),
    onBackground = Color(0xFF5D4037),
    onSurface = Color(0xFF5D4037),
    mutedForeground = Color(0xFFBC8F8F),
    border = Color(0xFFFFCCBC),
    itemBackground = Color(0xFFFFFFFF),
    itemForeground = Color(0xFF5D4037)
)

fun getColorsForTheme(theme: AppTheme): SushiColors {
    return when (theme) {
        AppTheme.DARK -> DarkThemeColors
        AppTheme.LIGHT -> LightThemeColors
        AppTheme.SALMON -> SalmonThemeColors
    }
}
