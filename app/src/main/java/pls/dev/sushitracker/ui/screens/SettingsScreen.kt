package pls.dev.sushitracker.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pls.dev.sushitracker.data.*
import pls.dev.sushitracker.ui.theme.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    colors: SushiColors,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { AppSettingsManager(context) }
    val sessionManager = remember { SessionStorage(context) }
    val scope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }
    var showExportOptions by remember { mutableStateOf(false) }

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
                text = "Ajustes",
                color = colors.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "Apariencia",
                    color = colors.mutedForeground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tema de la app",
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppTheme.entries.forEach { theme ->
                                ThemeOption(
                                    theme = theme,
                                    isSelected = currentTheme == theme,
                                    colors = colors,
                                    onClick = { onThemeChange(theme) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Tus datos",
                    color = colors.mutedForeground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Filled.Share,
                            title = "Exportar estadisticas",
                            subtitle = "Comparte tus datos como texto o JSON",
                            colors = colors,
                            onClick = { showExportOptions = true }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colors.border
                        )

                        SettingsItem(
                            icon = Icons.Filled.Delete,
                            title = "Borrar todos los datos",
                            subtitle = "Elimina todo tu historial",
                            colors = colors,
                            isDestructive = true,
                            onClick = { showResetDialog = true }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Informacion",
                    color = colors.mutedForeground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    val context = LocalContext.current
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val version = packageInfo.versionName
                    Column {
                        SettingsItem(
                            icon = Icons.Filled.Info,
                            title = "Version",
                            subtitle = LocalContext.current.packageManager.getPackageInfo(context.packageName, 0).versionName.toString(),
                            colors = colors
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = colors.border
                        )

                        SettingsItem(
                            icon = Icons.Filled.Edit,
                            title = "Desarrollado por",
                            subtitle = "PLS",
                            colors = colors
                        )
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    "¿Borrar todos los datos?",
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Esta accion no se puede deshacer. Se eliminaran todas tus sesiones, estadisticas y logros.",
                    color = colors.mutedForeground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionManager.deleteAllSessions()
                        showResetDialog = false
                        Toast.makeText(context, "Datos eliminados", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Borrar todo", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar", color = colors.primary)
                }
            }
        )
    }

    if (showExportOptions) {
        AlertDialog(
            onDismissRequest = { showExportOptions = false },
            containerColor = colors.surface,
            title = {
                Text(
                    "Exportar estadisticas",
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona el formato:", color = colors.mutedForeground)
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                val text = generateExportText(sessionManager)
                                shareText(context, text)
                            }
                            showExportOptions = false
                        }
                    ) {
                        Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Texto", color = colors.primary)
                    }

                    TextButton(
                        onClick = {
                            scope.launch {
                                val json = generateExportJson(sessionManager)
                                shareText(context, json, isJson = true)
                            }
                            showExportOptions = false
                        }
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("JSON", color = colors.primary)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportOptions = false }) {
                    Text("Cancelar", color = colors.mutedForeground)
                }
            }
        )
    }
}

@Composable
private fun ThemeOption(
    theme: AppTheme,
    isSelected: Boolean,
    colors: SushiColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = getColorsForTheme(theme)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.primary.copy(alpha = 0.1f) else colors.secondary)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) colors.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(themeColors.background)
                .border(1.dp, themeColors.border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(themeColors.primary)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (theme) {
                AppTheme.DARK -> "Oscuro"
                AppTheme.SALMON -> "Salmón"
                AppTheme.LIGHT -> "Claro"
            },
            color = if (isSelected) colors.primary else colors.onSurface,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: SushiColors,
    isDestructive: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error else colors.mutedForeground,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isDestructive) MaterialTheme.colorScheme.error else colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = colors.mutedForeground,
                fontSize = 13.sp
            )
        }

        if (onClick != null) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.mutedForeground,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
private fun generateExportText(sessionManager: SessionStorage): String {
    val stats = sessionManager.getStats(StatsFilter.ALL)
    val sessions = sessionManager.getSessions()

    return buildString {
        appendLine("🍣 SUSHI TRACKER - Estadisticas")
        appendLine("================================")
        appendLine()
        appendLine("📊 RESUMEN GENERAL")
        appendLine("• Total de piezas: ${stats.total}")
        appendLine("• Sesiones completadas: ${stats.sessionCount}")
        appendLine("• Promedio por sesion: ${String.format("%.1f", stats.avgPerSession)}")
        appendLine("• Record personal: ${stats.maxInSession} piezas")
        appendLine()
        appendLine("🍱 DESGLOSE POR TIPO")
        stats.pieceStats.entries.sortedByDescending { it.value }.forEach { (type, count) ->
            val emoji = when (type) {
                "nigiri" -> "🍣"
                "sashimi" -> "🥢"
                "maki" -> "🍙"
                "temaki" -> "📜"
                "gyoza" -> "🥟"
                "otro" -> "🍽️"
                else -> "•"
            }
            appendLine("$emoji ${type.replaceFirstChar { it.uppercase() }}: $count")
        }
        appendLine()
        appendLine("📅 ULTIMAS SESIONES")
        sessions.take(5).forEach { session ->
            appendLine("• ${session.date}: ${session.totalPieces} piezas en ${session.restaurant}")
        }
        appendLine()
        appendLine("Generado con Sushi Tracker 🍣")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
private fun generateExportJson(sessionManager: SessionStorage): String {
    val stats = sessionManager.getStats(StatsFilter.ALL)
    val sessions = sessionManager.getSessions()

    return buildString {
        appendLine("{")
        appendLine("  \"appName\": \"Sushi Tracker\",")
        appendLine("  \"exportDate\": \"${java.time.LocalDate.now()}\",")
        appendLine("  \"statistics\": {")
        appendLine("    \"totalPieces\": ${stats.total},")
        appendLine("    \"totalSessions\": ${stats.sessionCount},")
        appendLine("    \"averagePerSession\": ${String.format("%.2f", stats.avgPerSession)},")
        appendLine("    \"maxInSession\": ${stats.maxInSession},")
        appendLine("    \"piecesByType\": {")
        stats.pieceStats.entries.forEachIndexed { index, (type, count) ->
            val comma = if (index < stats.pieceStats.size - 1) "," else ""
            appendLine("      \"$type\": $count$comma")
        }
        appendLine("    }")
        appendLine("  },")
        appendLine("  \"sessions\": [")
        sessions.forEachIndexed { index, session ->
            val comma = if (index < sessions.size - 1) "," else ""
            appendLine("    {")
            appendLine("      \"date\": \"${session.date}\",")
            appendLine("      \"restaurant\": \"${session.restaurant}\",")
            appendLine("      \"totalPieces\": ${session.totalPieces},")
            appendLine("      \"pieces\": {")
            session.pieces.entries.filter { it.value > 0 }.forEachIndexed { pi, (type, count) ->
                val pc = if (pi < session.pieces.filter { it.value > 0 }.size - 1) "," else ""
                appendLine("        \"$type\": $count$pc")
            }
            appendLine("      }")
            appendLine("    }$comma")
        }
        appendLine("  ]")
        appendLine("}")
    }
}

private fun shareText(context: android.content.Context, text: String, isJson: Boolean = false) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = if (isJson) "application/json" else "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Sushi Tracker - Estadisticas")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir estadisticas"))
}
