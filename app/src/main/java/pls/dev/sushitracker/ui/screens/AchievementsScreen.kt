package pls.dev.sushitracker.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pls.dev.sushitracker.data.*
import pls.dev.sushitracker.ui.theme.*

@Composable
fun AchievementsScreen(
    colors: SushiColors,
    onBack: () -> Unit,
    strings: AppStrings.Strings
) {
    val context = LocalContext.current
    val achievementManager = remember { AchievementManager(context) }

    var achievements by remember {
        mutableStateOf(achievementManager.getAllAchievementsWithStatus())
    }

    LaunchedEffect(Unit) {
        achievementManager.checkAndUnlockAll()
        achievements = achievementManager.getAllAchievementsWithStatus()
    }

    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.secondary)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = colors.onSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = strings.achievements,
                color = colors.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.primary.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$unlockedCount / $totalCount",
                    color = if (unlockedCount == totalCount) colors.primary else colors.primary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = strings.achievementsUnlocked,
                    color = colors.mutedForeground,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(colors.secondary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (unlockedCount == totalCount) colors.primary else colors.primary)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(achievements) { item ->
                AchievementCard(item, colors)
            }
        }
    }
}

@Composable
fun AchievementCard(item: AchievementWithStatus, colors: SushiColors) {
    val bgColor by animateColorAsState(
        targetValue = if (item.isUnlocked) colors.primary.copy(alpha = 0.15f) else colors.surface,
        label = "bgColorAnimation"
    )

    val contentColor = if (item.isUnlocked) colors.primary else colors.onSurface
    val iconBgColor = if (item.isUnlocked) colors.primary else colors.secondary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (item.isUnlocked) 1f else 0.8f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getAchievementIcon(item.achievement.category),
                    contentDescription = null,
                    tint = if (item.isUnlocked) colors.background else colors.mutedForeground,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getAchievementTitle(item.achievement.id),
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = getAchievementDescription(item.achievement.id),
                    color = colors.mutedForeground,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(colors.secondary)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(item.progress.percentage)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(if (item.isUnlocked) colors.primary else colors.primary)
                        )
                    }

                    Text(
                        text = "${item.progress.displayCurrent}/${item.progress.target}",
                        color = if (item.isUnlocked) colors.primary else colors.mutedForeground,
                        fontSize = 11.sp,
                        fontWeight = if (item.isUnlocked) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            if (item.isUnlocked) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Desbloqueado",
                    tint = colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

private fun getAchievementIcon(category: AchievementCategory): ImageVector {
    return when (category) {
        AchievementCategory.TOTAL_PIECES -> Icons.Filled.AddCircle
        AchievementCategory.SESSION_PIECES -> Icons.Filled.AddCircle
        AchievementCategory.SPECIFIC_PIECE -> Icons.Filled.AddCircle
        AchievementCategory.SESSIONS_COUNT -> Icons.Filled.AddCircle
        AchievementCategory.VARIETY -> Icons.Filled.AddCircle
        AchievementCategory.SPECIAL -> Icons.Filled.AddCircle
    }
}

private fun getAchievementTitle(id: String): String = when (id) {
    "total_100" -> "Principiante"
    "total_500" -> "Aficionado"
    "total_1000" -> "Experto"
    "total_5000" -> "Maestro Sushi"
    "session_30" -> "Buen apetito"
    "session_50" -> "Hambre voraz"
    "session_100" -> "Máquina de comer"
    "nigiri_100" -> "Fan del Nigiri"
    "sashimi_100" -> "Amante del Sashimi"
    "maki_100" -> "Loco por el Maki"
    "gyoza_50" -> "Gyoza Lover"
    "nigiri_session_30" -> "Rey del Nigiri"
    "sashimi_session_20" -> "Sashimi Master"
    "sessions_5" -> "Hábito"
    "sessions_25" -> "Cliente VIP"
    "sessions_50" -> "Leyenda del buffet"
    "variety_6" -> "Explorador"
    "variety_all" -> "Catador completo"
    "all_in_one" -> "El completista"
    "first_session" -> "Primera vez"
    else -> "Logro"
}

private fun getAchievementDescription(id: String): String = when (id) {
    "total_100" -> "Come 100 piezas en total"
    "total_500" -> "Come 500 piezas en total"
    "total_1000" -> "Come 1000 piezas en total"
    "total_5000" -> "Come 5000 piezas en total"
    "session_30" -> "Come 30 piezas en una sesión"
    "session_50" -> "Come 50 piezas en una sesión"
    "session_100" -> "Come 100 piezas en una sesión"
    "nigiri_100" -> "Come 100 nigiris en total"
    "sashimi_100" -> "Come 100 sashimis en total"
    "maki_100" -> "Come 100 makis en total"
    "gyoza_50" -> "Come 50 gyozas en total"
    "nigiri_session_30" -> "Come 30 nigiris en una sesión"
    "sashimi_session_20" -> "Come 20 sashimis en una sesión"
    "sessions_5" -> "Completa 5 sesiones"
    "sessions_25" -> "Completa 25 sesiones"
    "sessions_50" -> "Completa 50 sesiones"
    "variety_6" -> "Prueba 6 tipos diferentes de piezas"
    "variety_all" -> "Prueba todos los tipos de piezas"
    "all_in_one" -> "Come al menos 1 de cada tipo en una sesión"
    "first_session" -> "Completa tu primera sesión"
    else -> ""
}