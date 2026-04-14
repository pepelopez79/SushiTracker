package pls.dev.sushitracker.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pls.dev.sushitracker.data.SessionStorage
import pls.dev.sushitracker.data.StatsFilter
import pls.dev.sushitracker.ui.theme.*

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
@Composable
fun StatsScreen(
    colors: SushiColors,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionStorage(context) }
    val stats = remember { sessionManager.getStats(StatsFilter.ALL) }

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
                text = "Estadisticas",
                color = colors.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { shareStats(context, sessionManager) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.2f))
            ) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Compartir",
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (stats.sessionCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = colors.mutedForeground,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Sin datos",
                        color = colors.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Completa sesiones para\nver tus estadisticas",
                        color = colors.mutedForeground,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.primary.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🍣", fontSize = 48.sp)
                            Text(
                                text = stats.total.toString(),
                                color = colors.primary,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "piezas totales",
                                color = colors.mutedForeground,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Filled.DateRange,
                            value = stats.sessionCount.toString(),
                            label = "Sesiones",
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Filled.Add,
                            value = String.format("%.1f", stats.avgPerSession),
                            label = "Promedio",
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Filled.Add,
                            value = stats.maxInSession.toString(),
                            label = "Record",
                            iconTint = colors.primary,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Desglose por tipo",
                        color = colors.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val maxPieces = stats.pieceStats.values.maxOrNull() ?: 1
                            val sortedTypes = stats.pieceStats.entries.sortedByDescending { it.value }

                            sortedTypes.forEach { (type, count) ->
                                PieceTypeRow(
                                    type = type,
                                    count = count,
                                    maxCount = maxPieces,
                                    colors = colors
                                )
                            }
                        }
                    }
                }

                if (stats.sessionCount > 0) {
                    item {
                        Text(
                            text = "Curiosidades",
                            color = colors.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val riceGrams = (stats.total * 20)
                                CuriosityItem(
                                    emoji = "🍚",
                                    text = "Has comido aprox. ${riceGrams}g de arroz",
                                    colors = colors
                                )

                                val favoriteType = stats.pieceStats.maxByOrNull { it.value }
                                favoriteType?.let {
                                    CuriosityItem(
                                        emoji = getPieceEmoji(it.key),
                                        text = "Lo que más has comido: ${it.key}",
                                        colors = colors
                                    )
                                }

                                val salmonEstimate = (stats.pieceStats["nigiri"] ?: 0) / 2
                                if (salmonEstimate > 0) {
                                    CuriosityItem(
                                        emoji = "🐟",
                                        text = "Aproximadamente $salmonEstimate piezas de salmón",
                                        colors = colors
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    colors: SushiColors,
    modifier: Modifier = Modifier,
    iconTint: Color = colors.primary
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = colors.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                color = colors.mutedForeground,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun PieceTypeRow(
    type: String,
    count: Int,
    maxCount: Int,
    colors: SushiColors
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = getPieceEmoji(type), fontSize = 20.sp)
                Text(
                    text = type.replaceFirstChar { it.uppercase() },
                    color = colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = count.toString(),
                color = colors.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colors.secondary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(count.toFloat() / maxCount)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.primary)
            )
        }
    }
}

@Composable
private fun CuriosityItem(
    emoji: String,
    text: String,
    colors: SushiColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }
        Text(text = text, color = colors.onSurface, fontSize = 14.sp)
    }
}

private fun getPieceEmoji(type: String): String {
    return when (type.lowercase()) {
        "nigiri" -> "🍣"
        "sashimi" -> "🥢"
        "maki" -> "🍙"
        "temaki" -> "📜"
        "gyoza" -> "🥟"
        "otro" -> "🍽️"
        else -> "🍱"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
private fun shareStats(context: android.content.Context, sessionManager: SessionStorage) {
    val stats = sessionManager.getStats(StatsFilter.ALL)

    val text = buildString {
        appendLine("🍣 Mis estadisticas de Sushi Tracker")
        appendLine("=====================================")
        appendLine()
        appendLine("📊 RESUMEN")
        appendLine("• Total de piezas: ${stats.total}")
        appendLine("• Sesiones completadas: ${stats.sessionCount}")
        appendLine("• Promedio por sesion: ${String.format("%.1f", stats.avgPerSession)}")
        appendLine("• Record personal: ${stats.maxInSession} piezas 🏆")
        appendLine()
        appendLine("🍱 POR TIPO")
        stats.pieceStats.entries.sortedByDescending { it.value }.forEach { (type, count) ->
            appendLine("${getPieceEmoji(type)} ${type.replaceFirstChar { it.uppercase() }}: $count")
        }
        appendLine()
        appendLine("Registrado con Sushi Tracker 🍣")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Mis estadisticas de Sushi Tracker")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir estadisticas"))
}
